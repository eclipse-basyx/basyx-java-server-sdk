/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.aas.active;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;

public class ActiveModel {
	private Set<VABModelTaskGroup> groups = new HashSet<>();
	private IModelProvider modelProvider;

	public ActiveModel(IModelProvider modelProvider) {
		this.modelProvider = modelProvider;
	}

	/**
	 * Runs a task with a specific update rate in a new task group.
	 * 
	 * @param updateInterval
	 *            The interval in which the task is updated
	 * @param task
	 *            The task that is scheduled
	 * @return The resulting task group in which the task is contained
	 */
	public VABModelTaskGroup runTask(int updateInterval, VABModelTask task) {
		VABModelTaskGroup group = new VABModelTaskGroup(modelProvider);
		groups.add(group);
		group.setUpdateInterval(updateInterval).addTask(task).start();
		return group;
	}

	/**
	 * Creates a new task group and associated it with this active model.
	 * 
	 * @return The created task group
	 */
	public VABModelTaskGroup createTaskGroup() {
		VABModelTaskGroup group = new VABModelTaskGroup(modelProvider);
		groups.add(group);
		return group;
	}

	/**
	 * Shuts all running threads down and removes all groups.
	 */
	public void clear() {
		for (VABModelTaskGroup group : groups) {
			group.clear();
		}
		groups.clear();
	}

	/**
	 * Getter for all containing task groups.
	 * 
	 * @return A set of all task groups that have been created in this model
	 */
	public Set<VABModelTaskGroup> getTaskGroups() {
		return groups;
	}

	/**
	 * Stops all running tasks in the contained groups.
	 */
	public void stopAll() {
		for (VABModelTaskGroup group : groups) {
			group.stop();
		}
	}

	/**
	 * Starts all running tasks in the contained groups.
	 */
	public void startAll() {
		for (VABModelTaskGroup group : groups) {
			group.start();
		}
	}
}
