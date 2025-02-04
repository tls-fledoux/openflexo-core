/**
 * 
 * Copyright (c) 2014, Openflexo
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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rm.CompilationUnitResourceFactory;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.task.Progress;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.toolbox.StringUtils;

/**
 * This action allows to create a {@link VirtualModel} in a container {@link VirtualModel}<br>
 * (this {@link VirtualModel} is declared as top-level)
 * 
 * @author sylvain
 * 
 */
public class CreateContainedVirtualModel extends AbstractCreateVirtualModel<CreateContainedVirtualModel, FMLCompilationUnit, FMLObject>
		implements TechnologySpecificFlexoAction<FMLTechnologyAdapter> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateContainedVirtualModel.class.getPackage().getName());

	public static FlexoActionFactory<CreateContainedVirtualModel, FMLCompilationUnit, FMLObject> actionType = new FlexoActionFactory<CreateContainedVirtualModel, FMLCompilationUnit, FMLObject>(
			"create_basic_virtual_model", FlexoActionFactory.newVirtualModelMenu, FlexoActionFactory.defaultGroup,
			FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateContainedVirtualModel makeNewAction(FMLCompilationUnit focusedObject, Vector<FMLObject> globalSelection,
				FlexoEditor editor) {
			return new CreateContainedVirtualModel(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FMLCompilationUnit object, Vector<FMLObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FMLCompilationUnit object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateContainedVirtualModel.actionType, FMLCompilationUnit.class);
	}

	private String newVirtualModelName;
	private String newVirtualModelDescription;
	private VirtualModel newVirtualModel;

	// public Vector<IFlexoOntology> importedOntologies = new Vector<IFlexoOntology>();

	// private boolean createsOntology = false;

	private CreateContainedVirtualModel(FMLCompilationUnit focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public Class<? extends FMLTechnologyAdapter> getTechnologyAdapterClass() {
		return FMLTechnologyAdapter.class;
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException, SaveResourceException, FlexoException {

		Progress.progress(getLocales().localizedForKey("create_virtual_model"));

		FMLTechnologyAdapter fmlTechnologyAdapter = getServiceManager().getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		CompilationUnitResourceFactory factory = fmlTechnologyAdapter.getCompilationUnitResourceFactory();

		try {
			CompilationUnitResource vmResource = factory.makeContainedCompilationUnitResource(getNewVirtualModelName(),
					(CompilationUnitResource) getFocusedObject().getResource(), null, true);
			newVirtualModel = vmResource.getLoadedResourceData().getVirtualModel();
			newVirtualModel.setDescription(newVirtualModelDescription);
			newVirtualModel.setVisibility(getVisibility());
			newVirtualModel.setAbstract(getIsAbstract());
		} catch (SaveResourceException e) {
			throw new SaveResourceException(null);
		} catch (ModelDefinitionException e) {
			throw new FlexoException(e);
		}

		Progress.progress(getLocales().localizedForKey("create_model_slots"));
		performCreateModelSlots();

		Progress.progress(getLocales().localizedForKey("set_parent_concepts"));
		performSetParentConcepts();

		Progress.progress(getLocales().localizedForKey("create_properties"));
		performCreateProperties();

		Progress.progress(getLocales().localizedForKey("create_behaviours"));
		performCreateBehaviours();

		Progress.progress(getLocales().localizedForKey("create_inspector"));
		performCreateInspectors();

		Progress.progress(getLocales().localizedForKey("perform_post_processings"));
		performPostProcessings();

		newVirtualModel.getPropertyChangeSupport().firePropertyChange("name", null, newVirtualModel.getName());
		newVirtualModel.getResource().getPropertyChangeSupport().firePropertyChange("name", null, newVirtualModel.getName());
	}

	public boolean isNewVirtualModelNameValid() {
		if (StringUtils.isEmpty(newVirtualModelName)) {
			return false;
		}
		if (getFocusedObject().getVirtualModelNamed(newVirtualModelName) != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValid() {
		if (!isNewVirtualModelNameValid()) {
			return false;
		}
		return true;
	}

	@Override
	public VirtualModel getNewVirtualModel() {
		return newVirtualModel;
	}

	public String getNewVirtualModelName() {
		return newVirtualModelName;
	}

	public void setNewVirtualModelName(String newVirtualModelName) {
		this.newVirtualModelName = newVirtualModelName;
		getPropertyChangeSupport().firePropertyChange("newVirtualModelName", null, newVirtualModelName);

	}

	public String getNewVirtualModelURI() {
		String baseURI = getFocusedObject().getVirtualModel().getURI();
		if (!baseURI.endsWith("/")) {
			baseURI = baseURI + "/";
		}
		return baseURI + getNewVirtualModelName() + CompilationUnitResourceFactory.FML_SUFFIX;
	}

	public String getNewVirtualModelDescription() {
		return newVirtualModelDescription;
	}

	public void setNewVirtualModelDescription(String newVirtualModelDescription) {
		this.newVirtualModelDescription = newVirtualModelDescription;
		getPropertyChangeSupport().firePropertyChange("newVirtualModelDescription", null, newVirtualModelDescription);
	}

	@Override
	public int getExpectedProgressSteps() {
		return 15;
	}

}
