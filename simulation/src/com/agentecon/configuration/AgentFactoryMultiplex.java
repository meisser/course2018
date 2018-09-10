/**
 * Created by Luzius Meisser on Jun 18, 2017
 * Copyright: Meisser Economics AG, Zurich
 * Contact: luzius@meissereconomics.com
 *
 * Feel free to reuse this code under the MIT License
 * https://opensource.org/licenses/MIT
 */
package com.agentecon.configuration;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.agentecon.IAgentFactory;
import com.agentecon.agent.Endowment;
import com.agentecon.agent.IAgentIdGenerator;
import com.agentecon.consumer.Consumer;
import com.agentecon.consumer.IConsumer;
import com.agentecon.consumer.IUtility;

public class AgentFactoryMultiplex implements IAgentFactory {

	private int current;
	private IAgentFactory[] factories;

	public AgentFactoryMultiplex(IAgentFactory... factories) throws IOException {
		if (factories.length == 0) {
			this.factories = new IAgentFactory[] { new IAgentFactory() {

				@Override
				public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
					return null;
				}
			} };
		} else {
			this.factories = factories;
		}
		this.current = 0;
	}

	public AgentFactoryMultiplex(Class<? extends IConsumer>[] agents) {
		this.factories = new IAgentFactory[agents.length];
		for (int i = 0; i < agents.length; i++) {
			final Class<? extends IConsumer> current = agents[i];
			this.factories[i] = new IAgentFactory() {
				
				@Override
				public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
					try {
						Constructor<? extends IConsumer> constructor = current.getConstructor(IAgentIdGenerator.class, int.class, Endowment.class, IUtility.class);
						return constructor.newInstance(id, maxAge, endowment, utilityFunction);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						System.err.println("Could not instantiate agent " + current + " due to " + e);
						return null;
					}
				}

				@Override
				public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
					try {
						return current.getConstructor(IAgentIdGenerator.class, Endowment.class, IUtility.class).newInstance(id, endowment, utilityFunction);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						System.err.println("Could not instantiate agent " + current + " due to " + e);
						return null;
					}
				}
			};
		}
	}

	private IAgentFactory getCurrent() {
		return factories[current++ % factories.length];
	}
	
	protected IAgentFactory getDefaultFactory() {
		return new IAgentFactory() {
			
			@Override
			public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
				return new Consumer(id, endowment, utilityFunction);
			}
		};
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, int maxAge, Endowment endowment, IUtility utilityFunction) {
		IAgentFactory current = getCurrent();
		IConsumer consumer = current.createConsumer(id, maxAge, endowment, utilityFunction);
		return consumer == null ? getDefaultFactory().createConsumer(id, maxAge, endowment, utilityFunction) : consumer;
	}

	@Override
	public IConsumer createConsumer(IAgentIdGenerator id, Endowment endowment, IUtility utilityFunction) {
		IAgentFactory current = getCurrent();
		IConsumer consumer = current.createConsumer(id, endowment, utilityFunction);
		return consumer == null ? getDefaultFactory().createConsumer(id, endowment, utilityFunction) : consumer;
	}
	
}
