/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-ui, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.view.controller;

import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.openflexo.fib.AskResourceCenterDirectory;
import org.openflexo.fib.AskResourceCenterGit;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.GitResourceCenter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rm.AddResourceCenterTask;
import org.openflexo.rm.RefreshResourceCenterTask;
import org.openflexo.rm.RemoveResourceCenterTask;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.FlexoFrame;

public class ResourceCenterEditor implements HasPropertyChangeSupport {

	static final Logger logger = Logger.getLogger(ResourceCenterEditor.class.getPackage().getName());

	public static final String RESOURCE_CENTER_EDITOR_FIB_NAME = "Fib/ResourceCenterEditor.fib";

	private final PropertyChangeSupport _pcSupport;

	private final FlexoResourceCenterService rcService;

	private Window owner;

	public Window getOwner() {
		return owner;
	}

	public void setOwner(Window owner) {
		this.owner = owner;
	}

	public ResourceCenterEditor(FlexoResourceCenterService rcService) {
		_pcSupport = new PropertyChangeSupport(this);
		this.rcService = rcService;
	}

	public FlexoResourceCenterService getRcService() {
		return rcService;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return _pcSupport;
	}

	public void addResourceCenter() {
		FIBComponent askRCDirectoryComponent = FIBLibrary.instance().retrieveFIBComponent(AskResourceCenterDirectory.FIB_FILE);
		AskResourceCenterDirectory askDir = new AskResourceCenterDirectory();
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(askRCDirectoryComponent, askDir, FlexoFrame.getActiveFrame(), true,
				FlexoLocalization.getMainLocalizer());
		if (dialog.getStatus() == Status.VALIDATED) {
			DirectoryResourceCenter newRC = new DirectoryResourceCenter(askDir.getLocalResourceDirectory());
			AddResourceCenterTask task = new AddResourceCenterTask(getRcService(), newRC);
			rcService.getServiceManager().getTaskManager().scheduleExecution(task);
		}
	}

	public void addGitResourceCenter(){
		FIBComponent askRCGitComponent = FIBLibrary.instance().retrieveFIBComponent(AskResourceCenterGit.FIB_FILE);
		AskResourceCenterGit askGit = new AskResourceCenterGit();
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(askRCGitComponent, askGit, FlexoFrame.getActiveFrame(), true,
				FlexoLocalization.getMainLocalizer());
		if (dialog.getStatus() == Status.VALIDATED) {
			GitResourceCenter newRC = null;
			try {
				newRC = new GitResourceCenter(askGit.getLocalResourceDirectory());
			} catch (IllegalStateException | IOException | GitAPIException e) {
				e.printStackTrace();
			}
			AddResourceCenterTask task = new AddResourceCenterTask(getRcService(), newRC);
			rcService.getServiceManager().getTaskManager().scheduleExecution(task);
		}
	}
	
	public void removeResourceCenter(FlexoResourceCenter<?> rc) {
		logger.info("removeResourceCenter " + rc);
		// if (rc instanceof DirectoryResourceCenter) {
		RemoveResourceCenterTask task = new RemoveResourceCenterTask(getRcService(), rc);
		rcService.getServiceManager().getTaskManager().scheduleExecution(task);
		/*showProgress("removing_resources");
		rcService.removeFromResourceCenters(rc);
		hideProgress();*/
		// }
	}

	public void refreshResourceCenter(FlexoResourceCenter<?> rc) {
		if (rc != null) {
			logger.info("refreshResourceCenter " + rc);
			RefreshResourceCenterTask task = new RefreshResourceCenterTask(getRcService(), rc);
			rcService.getServiceManager().getTaskManager().scheduleExecution(task);
			/*showProgress("scanning_resources");
			rc.update();
			hideProgress();*/
		}
	}

	/*private void showProgress(String stepname) {
		ProgressWindow.showProgressWindow(owner, FlexoLocalization.localizedForKey(stepname), 1);
		ProgressWindow.instance().setProgress(FlexoLocalization.localizedForKey(stepname));
	}

	private void hideProgress() {
		ProgressWindow.hideProgressWindow();
	}*/

	public void saveResourceCenters() {
		logger.info("Save resource centers");
		rcService.storeDirectoryResourceCenterLocations();
	}

}
