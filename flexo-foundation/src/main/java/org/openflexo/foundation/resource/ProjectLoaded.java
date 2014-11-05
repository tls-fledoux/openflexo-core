package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.FlexoService.ServiceNotification;

/**
 * This notification is broadcasted on ServiceManager bus when a project is loaded
 * 
 * @author sylvain
 *
 */
public final class ProjectLoaded implements ServiceNotification {

	private final FlexoProject project;

	public ProjectLoaded(FlexoProject project) {
		this.project = project;
	}

	public FlexoProject getProject() {
		return project;
	}
}