package org.openflexo.view.controller;

import java.awt.Window;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.components.ResourceCenterEditorDialog;
import org.openflexo.fib.AskResourceCenterDirectory;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.view.FlexoFrame;

public class ResourceCenterEditor implements HasPropertyChangeSupport {

	static final Logger logger = Logger.getLogger(ResourceCenterEditor.class.getPackage().getName());

	public static final String RESOURCE_CENTER_EDITOR_FIB_NAME = "Fib/ResourceCenterEditor.fib";

	private PropertyChangeSupport _pcSupport;

	private FlexoResourceCenterService rcService;
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
		System.out.println("Add resource center ");
		FIBComponent askRCDirectoryComponent = FIBLibrary.instance().retrieveFIBComponent(AskResourceCenterDirectory.FIB_FILE);
		AskResourceCenterDirectory askDir = new AskResourceCenterDirectory();
		FIBDialog dialog = FIBDialog.instanciateAndShowDialog(askRCDirectoryComponent, askDir, FlexoFrame.getActiveFrame(), true,
				FlexoLocalization.getMainLocalizer());
		if (dialog.getStatus() == Status.VALIDATED) {
			DirectoryResourceCenter newRC = new DirectoryResourceCenter(askDir.getLocalResourceDirectory());
			rcService.addToResourceCenters(newRC);
		}
	}

	public void removeResourceCenter(FlexoResourceCenter rc) {
		System.out.println("Remove resource center " + rc);
		if (rc instanceof DirectoryResourceCenter) {
			rcService.removeFromResourceCenters(rc);
		}
	}

	public void refreshResourceCenter(FlexoResourceCenter rc) {
		if (rc != null) {
			System.out.println("Refresh resource center " + rc);
			try {
				rc.update();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveResourceCenters() {
		System.out.println("Save resource centers");
		rcService.storeDirectoryResourceCenterLocations();
	}

}
