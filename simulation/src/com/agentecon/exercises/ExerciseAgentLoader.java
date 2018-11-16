/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.exercises;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.agentecon.IAgentFactory;
import com.agentecon.classloader.GitSimulationHandle;
import com.agentecon.configuration.AgentFactoryMultiplex;
import com.agentecon.sim.SimulationConfig;

public class ExerciseAgentLoader extends AgentFactoryMultiplex {

	public static final String DEFAULT_REPO = "course2018";

	public static final Collection<String> TEAMS = createRepos(0, 1, 2, 3, 4, 5);

	public ExerciseAgentLoader(String classname) throws SocketTimeoutException, IOException {
		this(classname, SimulationConfig.shouldLoadRemoteTeams());
	}

	public ExerciseAgentLoader(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		super(remoteTeams ? new ExerciseAgentFactory(classname, "meisser", DEFAULT_REPO) : new ExerciseAgentFactory(classname), false);
		if (remoteTeams) {
			addFactories(classname, remoteTeams);
		}
	}

	private static Collection<String> createRepos(int... numbers) {
		ArrayList<String> repos = new ArrayList<>();
		for (int i : numbers) {
			String number = Integer.toString(i);
			repos.add("team10" + number);
		}
		return repos;
	}

	private void addFactories(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		Stream<ExerciseAgentFactory> stream = TEAMS.parallelStream().map(team -> {
			return createFactory(classname, team);
		}).filter(factory -> factory != null);
		for (IAgentFactory factory : stream.collect(Collectors.toList())) {
			super.addFactory(factory);
		}
	}

	protected static ExerciseAgentFactory createFactory(String classname, String team) {
		try {
			ExerciseAgentFactory factory = new ExerciseAgentFactory(classname, new GitSimulationHandle("meisser", team, false));
			try {
				factory.preload();
				return factory;
			} catch (ClassNotFoundException e) {
				System.err.println("Could not load agent from " + factory + " due to " + e);
				return null;
			}
		} catch (IOException e) {
			return null;
		}
	}

}
