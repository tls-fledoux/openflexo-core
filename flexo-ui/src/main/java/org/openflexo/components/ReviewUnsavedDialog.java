/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.components;

import java.util.logging.Logger;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.resource.ResourceManager;
import org.openflexo.foundation.resource.SaveResourceExceptionList;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.rm.Resource;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ResourceSavingInfo;

/**
 * Dialog allowing to select resources to save
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class ReviewUnsavedDialog extends FIBDialog<ResourceSavingInfo> {

	static final Logger logger = Logger.getLogger(ReviewUnsavedDialog.class.getPackage().getName());
	

	public static final Resource FIB_FILE_NAME = ResourceLocator.locateResource("Fib/Dialog/ReviewUnsavedDialog.fib");

	private final ResourceManager resourceManager;

	/**
	 * Constructor
	 * 
	 */
	public ReviewUnsavedDialog(ResourceManager resourceManager) {

		super(FIBLibrary.instance().retrieveFIBComponent(FIB_FILE_NAME), new ResourceSavingInfo(resourceManager), FlexoFrame.getActiveFrame(),
				true, FlexoLocalization.getMainLocalizer());
		this.resourceManager = resourceManager;
		setTitle("Save modified resources");

	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void saveSelection(FlexoProgressFactory progressFactory) throws SaveResourceExceptionList, SaveResourcePermissionDeniedException {
		getData().saveSelectedResources(progressFactory);
		// _reviewUnsavedModel.saveSelected();
		getResourceManager().deleteFilesToBeDeleted();
	}

	/*public String savedFilesList() {
		return _reviewUnsavedModel.savedFilesList();
	}*/

	/*public static class ReviewUnsavedModel implements HasPropertyChangeSupport {

		private final FlexoEditor editor;
		private final Hashtable<FlexoResource<?>, Boolean> resourcesToSave;

		private final PropertyChangeSupport pcSupport;

		public ReviewUnsavedModel(FlexoEditor editor, Collection<FlexoFileResource<?>> resources) {
			super();
			this.editor = editor;
			pcSupport = new PropertyChangeSupport(this);
			resourcesToSave = new Hashtable<FlexoResource<?>, Boolean>();
			for (FlexoResource<?> r : resources) {
				if (r.isLoaded()) {
					System.out.println("loaded resource " + r + " with " + r.getLoadedResourceData());
				}
				if (r.isLoaded() && r.getLoadedResourceData().isModified()) {
					resourcesToSave.put(r, Boolean.TRUE);
				} else {
					resourcesToSave.put(r, Boolean.FALSE);
				}
			}
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public Hashtable<FlexoResource<?>, Boolean> getResourcesToSave() {
			return resourcesToSave;
		}

		public boolean isSelected(FlexoResource<?> resource) {
			return resourcesToSave.get(resource);
		}

		public void setSelected(boolean selected, FlexoResource<?> resource) {
			System.out.println("setSelected " + selected + " resource=" + resource);
			resourcesToSave.put(resource, selected);
			pcSupport.firePropertyChange("getNbOfFilesToSave()", -1, getNbOfFilesToSave());
		}

		public Icon getIcon(FlexoResource<?> resource) {
			return IconLibrary.getIconForResource(resource);
		}

		public int getNbOfFilesToSave() {
			int nbOfFilesToSave = 0;
			for (FlexoResource<?> r : resourcesToSave.keySet()) {
				if (resourcesToSave.get(r)) {
					nbOfFilesToSave++;
				}
			}
			return nbOfFilesToSave;
		}

		public void saveSelected() throws SaveResourceExceptionList, SaveResourcePermissionDeniedException {

			SaveResourceExceptionList listOfRaisedExceptions = null;

			int nbOfFilesToSave = getNbOfFilesToSave();

			if (nbOfFilesToSave > 0) {

				FlexoProgress progress = editor.getFlexoProgressFactory().makeFlexoProgress(
						FlexoLocalization.localizedForKey("saving_selected_resources"), nbOfFilesToSave);

				for (FlexoResource<?> r : resourcesToSave.keySet()) {
					if (resourcesToSave.get(r)) {
						try {
							r.save(progress);
						} catch (SaveResourceException e) {
							if (listOfRaisedExceptions == null) {
								listOfRaisedExceptions = new SaveResourceExceptionList(e);
							} else {
								listOfRaisedExceptions.registerNewException(e);
							}
							e.printStackTrace();
						}
					}
				}

				progress.hideWindow();

				if (listOfRaisedExceptions != null) {
					throw listOfRaisedExceptions;
				}

			}
		}

		private String savedFilesList;

		public String savedFilesList() {
			return savedFilesList;
		}

		public void selectAll() {
			for (FlexoResource<?> r : resourcesToSave.keySet()) {
				if (!resourcesToSave.get(r)) {
					resourcesToSave.put(r, Boolean.TRUE);
				}
			}
		}

		public void deselectAll() {
			for (FlexoResource<?> r : resourcesToSave.keySet()) {
				if (resourcesToSave.get(r)) {
					resourcesToSave.put(r, Boolean.FALSE);
				}
			}
		}
	}*/

}
