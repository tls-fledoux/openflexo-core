/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.InvalidNameException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelLibrary;
import org.openflexo.foundation.fml.rm.CompilationUnitResourceFactory;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class RenameVirtualModel extends FlexoAction<RenameVirtualModel, VirtualModel, FMLObject>
		implements TechnologySpecificFlexoAction<FMLTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(RenameVirtualModel.class.getPackage().getName());

	public static FlexoActionFactory<RenameVirtualModel, VirtualModel, FMLObject> actionType = new FlexoActionFactory<RenameVirtualModel, VirtualModel, FMLObject>(
			"rename", FlexoActionFactory.refactorMenu, FlexoActionFactory.defaultGroup, FlexoActionFactory.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RenameVirtualModel makeNewAction(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new RenameVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModel object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(RenameVirtualModel.actionType, VirtualModel.class);
	}

	private String newVirtualModelName;
	private String newVirtualModelURI;
	private String newVirtualModelDescription;

	RenameVirtualModel(VirtualModel focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		newVirtualModelName = focusedObject.getName();
		newVirtualModelDescription = focusedObject.getDescription();
		if (!focusedObject.getResource().computeDefaultURI().equals(focusedObject.getURI())) {
			newVirtualModelURI = focusedObject.getURI();
		}
	}

	@Override
	protected void doAction(Object context) throws InvalidNameException {

		System.out.println("Rename VM to " + getNewVirtualModelName());

		getFocusedObject().setName(getNewVirtualModelName());
		getFocusedObject().setURI(getNewVirtualModelURI());
		getFocusedObject().setDescription(getNewVirtualModelDescription());

		try {
			getFocusedObject().getResource().save();
		} catch (SaveResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Class<? extends FMLTechnologyAdapter> getTechnologyAdapterClass() {
		return FMLTechnologyAdapter.class;
	}

	public FMLTechnologyAdapter getFMLTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class);
	}

	public String getNewVirtualModelName() {
		return newVirtualModelName;
	}

	public void setNewVirtualModelName(String newViewPointName) {
		this.newVirtualModelName = newViewPointName;
		getPropertyChangeSupport().firePropertyChange("newVirtualModelName", null, newViewPointName);
		getPropertyChangeSupport().firePropertyChange("newVirtualModelURI", null, getNewVirtualModelURI());
	}

	public String getNewVirtualModelDescription() {
		return newVirtualModelDescription;
	}

	public void setNewVirtualModelDescription(String newVirtualModelDescription) {
		this.newVirtualModelDescription = newVirtualModelDescription;
		getPropertyChangeSupport().firePropertyChange("newVirtualModelDescription", null, newVirtualModelDescription);
	}

	private String getBaseName() {
		return JavaUtils.getClassName(getNewVirtualModelName());
	}

	public String getNewVirtualModelURI() {
		if (newVirtualModelURI == null) {
			String baseURI;
			if (getFocusedObject().getOwningVirtualModel() != null) {
				baseURI = getFocusedObject().getOwningVirtualModel().getURI();
			}
			else {
				RepositoryFolder currentFolder = getFocusedObject().getResource().getResourceCenter()
						.getRepositoryFolder(getFocusedObject().getResource());
				baseURI = currentFolder.getDefaultBaseURI();
			}
			if (!baseURI.endsWith("/")) {
				baseURI = baseURI + "/";
			}
			return baseURI + getBaseName() + CompilationUnitResourceFactory.FML_SUFFIX;
		}

		return newVirtualModelURI;
	}

	public void setNewVirtualModelURI(String newVirtualModelURI) {
		this.newVirtualModelURI = newVirtualModelURI;
		getPropertyChangeSupport().firePropertyChange("newVirtualModelURI", null, newVirtualModelURI);

	}

	public VirtualModelLibrary getVirtualModelLibrary() {
		return getServiceManager().getVirtualModelLibrary();
	}

	public boolean isNewVirtualModelNameValid() {
		if (StringUtils.isEmpty(getNewVirtualModelName())) {
			return false;
		}
		return true;
	}

	public boolean isNewVirtualModelURIValid() {
		if (StringUtils.isEmpty(getNewVirtualModelURI())) {
			return false;
		}
		try {
			new URL(getNewVirtualModelURI());
		} catch (MalformedURLException e) {
			return false;
		}
		if (getVirtualModelLibrary() == null) {
			return false;
		}
		if (getVirtualModelLibrary().getCompilationUnitResource(getNewVirtualModelURI()) != null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isValid() {
		if (!isNewVirtualModelNameValid()) {
			return false;
		}
		if (!isNewVirtualModelURIValid()) {
			return false;
		}
		return true;
	}

}
