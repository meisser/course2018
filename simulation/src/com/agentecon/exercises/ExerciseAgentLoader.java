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

	private static final Collection<String> TEAMS = createRepos(0, 1, 2, 3, 4, 5);

	private IAgentFactory defaultFactory;

	public ExerciseAgentLoader(String classname) throws SocketTimeoutException, IOException {
		this(classname, SimulationConfig.shouldLoadRemoteTeams());
	}

	public ExerciseAgentLoader(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		super(createFactories(classname, remoteTeams));
		if (remoteTeams) {
			defaultFactory = new ExerciseAgentFactory(classname, "meisser", "course");
		} else {
			defaultFactory = new ExerciseAgentFactory(classname);
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

	private static IAgentFactory[] createFactories(String classname, boolean remoteTeams) throws SocketTimeoutException, IOException {
		ArrayList<ExerciseAgentFactory> factories = new ArrayList<>();
		if (remoteTeams) {
			Stream<ExerciseAgentFactory> stream = TEAMS.parallelStream().map(team -> {
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
			}).filter(factory -> factory != null);
			factories.addAll(stream.collect(Collectors.toList()));
		}
		return factories.toArray(new IAgentFactory[factories.size()]);
	}

	@Override
	protected IAgentFactory getDefaultFactory() {
		return defaultFactory;
	}

}
