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

import java.lang.reflect.Type;

// org.openflexo.foundation.fml.action.AbstractCreateVirtualModel$ModelSlotEntry

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.task.Progress;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;
import org.openflexo.toolbox.StringUtils;

/**
 * Abstract action creating a {@link VirtualModel} or any of its subclass
 * 
 * @author sylvain
 * 
 */
public abstract class AbstractCreateVirtualModel<A extends AbstractCreateVirtualModel<A, T1, T2>, T1 extends FlexoObject, T2 extends FMLObject>
		extends AbstractCreateFlexoConcept<A, T1, T2> {

	private static final Logger logger = Logger.getLogger(AbstractCreateVirtualModel.class.getPackage().getName());

	private final List<ModelSlotEntry> modelSlotEntries;

	protected AbstractCreateVirtualModel(FlexoActionFactory<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		modelSlotEntries = new ArrayList<>();
	}

	public List<ModelSlotEntry> getModelSlotEntries() {
		return modelSlotEntries;
	}

	public ModelSlotEntry newModelSlotEntry() {
		ModelSlotEntry returned = new ModelSlotEntry("modelSlot" + (getModelSlotEntries().size() + 1), getLocales());
		modelSlotEntries.add(returned);
		getPropertyChangeSupport().firePropertyChange("modelSlotEntries", null, returned);
		return returned;
	}

	public void deleteModelSlotEntry(ModelSlotEntry modelSlotEntryToDelete) {
		modelSlotEntries.remove(modelSlotEntryToDelete);
		modelSlotEntryToDelete.delete();
		getPropertyChangeSupport().firePropertyChange("modelSlotEntries", modelSlotEntryToDelete, null);
	}

	public abstract VirtualModel getNewVirtualModel();

	@Override
	public FlexoConcept getNewFlexoConcept() {
		return getNewVirtualModel();
	}

	protected void performCreateModelSlots() {
		for (ModelSlotEntry entry : getModelSlotEntries()) {
			performCreateModelSlot(entry);
		}
	}

	protected void performCreateModelSlot(ModelSlotEntry entry) {
		Progress.progress(getLocales().localizedForKey("create_model_slot") + " " + entry.getModelSlotName());
		CreateModelSlot action = CreateModelSlot.actionType.makeNewEmbeddedAction(getNewVirtualModel(), null, this);
		action.setModelSlotName(entry.getModelSlotName());
		action.setDescription(entry.getModelSlotDescription());
		action.setTechnologyAdapter(entry.getTechnologyAdapter());
		action.setModelSlotClass(entry.getModelSlotClass());
		action.setMmRes(entry.getMetaModelResource());
		action.setVmRes(entry.getCompilationUnitResource());
		action.doAction();
	}

	protected FlexoBehaviourParameter createParameter(FlexoBehaviour behaviour, String parameterName, Type parameterType) {
		CreateGenericBehaviourParameter createParameter = CreateGenericBehaviourParameter.actionType.makeNewEmbeddedAction(behaviour, null,
				this);
		createParameter.setParameterName(parameterName);
		createParameter.setParameterType(parameterType);

		createParameter.doAction();
		FlexoBehaviourParameter returned = createParameter.getNewParameter();
		if (parameterType instanceof FlexoConceptInstanceType) {
			returned.setContainer(new DataBinding<FMLRTVirtualModelInstance>("this"));
		}
		return returned;
	}

	public static class ModelSlotEntry<TA extends TechnologyAdapter<TA>> extends PropertyChangedSupportDefaultImplementation {

		private final String defaultModelSlotName;
		private String modelSlotName;
		private String description;
		private TA technologyAdapter;
		private boolean required = true;
		private boolean readOnly = false;
		private Class<? extends ModelSlot<?>> modelSlotClass;

		private CompilationUnitResource compilationUnitResource;
		private FlexoMetaModelResource<?, ?, ?> metaModelResource;

		private LocalizedDelegate locales;

		public ModelSlotEntry(String defaultName, LocalizedDelegate locales) {
			super();
			defaultModelSlotName = defaultName;
			this.locales = locales;
		}

		public void delete() {
			modelSlotName = null;
			description = null;
			technologyAdapter = null;
			modelSlotClass = null;
		}

		public LocalizedDelegate getLocales() {
			return locales;
		}

		public Class<? extends ModelSlot<?>> getModelSlotClass() {
			if (modelSlotClass == null && technologyAdapter != null && technologyAdapter.getAvailableModelSlotTypes().size() > 0) {
				return technologyAdapter.getAvailableModelSlotTypes().get(0);
			}
			return modelSlotClass;
		}

		public void setModelSlotClass(Class<? extends ModelSlot<?>> modelSlotClass) {
			this.modelSlotClass = modelSlotClass;
			getPropertyChangeSupport().firePropertyChange("modelSlotClass", modelSlotClass != null ? null : false, modelSlotClass);
		}

		public String getModelSlotName() {
			if (modelSlotName == null) {
				return defaultModelSlotName;
			}
			return modelSlotName;
		}

		public void setModelSlotName(String modelSlotName) {
			this.modelSlotName = modelSlotName;
			getPropertyChangeSupport().firePropertyChange("modelSlotName", null, modelSlotName);
		}

		public String getModelSlotDescription() {
			return description;
		}

		public void setModelSlotDescription(String description) {
			this.description = description;
			getPropertyChangeSupport().firePropertyChange("modelSlotDescription", null, description);
		}

		public TA getTechnologyAdapter() {
			return technologyAdapter;
		}

		public void setTechnologyAdapter(TA technologyAdapter) {
			this.technologyAdapter = technologyAdapter;
			getPropertyChangeSupport().firePropertyChange("technologyAdapter", null, technologyAdapter);
			if (getModelSlotClass() != null && !technologyAdapter.getAvailableModelSlotTypes().contains(getModelSlotClass())) {
				// The ModelSlot class is not consistent anymore
				if (technologyAdapter.getAvailableModelSlotTypes().size() > 0) {
					setModelSlotClass(technologyAdapter.getAvailableModelSlotTypes().get(0));
				}
				else {
					setModelSlotClass(null);
				}
			}
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
			getPropertyChangeSupport().firePropertyChange("required", null, required);
		}

		public boolean isReadOnly() {
			return readOnly;
		}

		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
			getPropertyChangeSupport().firePropertyChange("readOnly", null, readOnly);
		}

		public CompilationUnitResource getCompilationUnitResource() {
			return compilationUnitResource;
		}

		public void setCompilationUnitResource(CompilationUnitResource compilationUnitResource) {
			if ((compilationUnitResource == null && this.compilationUnitResource != null)
					|| (compilationUnitResource != null && !compilationUnitResource.equals(this.compilationUnitResource))) {
				CompilationUnitResource oldValue = this.compilationUnitResource;
				this.compilationUnitResource = compilationUnitResource;
				getPropertyChangeSupport().firePropertyChange("compilationUnitResource", oldValue, compilationUnitResource);
			}
		}

		public FlexoMetaModelResource<?, ?, ?> getMetaModelResource() {
			return metaModelResource;
		}

		public void setMetaModelResource(FlexoMetaModelResource<?, ?, ?> metaModelResource) {
			if ((metaModelResource == null && this.metaModelResource != null)
					|| (metaModelResource != null && !metaModelResource.equals(this.metaModelResource))) {
				FlexoMetaModelResource<?, ?, ?> oldValue = this.metaModelResource;
				this.metaModelResource = metaModelResource;
				getPropertyChangeSupport().firePropertyChange("metaModelResource", oldValue, metaModelResource);
			}
		}

		public String getConfigurationErrorMessage() {

			if (StringUtils.isEmpty(getModelSlotName())) {
				return getLocales().localizedForKey("please_supply_valid_model_slot_name");
			}
			if (getTechnologyAdapter() == null) {
				return getLocales().localizedForKey("no_technology_adapter_defined_for") + " " + getModelSlotName();
			}
			if (getModelSlotClass() == null) {
				return getLocales().localizedForKey("no_model_slot_type_defined_for") + " " + getModelSlotName();
			}

			return null;
		}

		public String getConfigurationWarningMessage() {
			if (StringUtils.isEmpty(getModelSlotDescription())) {
				return getLocales().localizedForKey("it_is_recommanded_to_describe_model_slot") + " " + getModelSlotName();
			}
			return null;

		}

	}

}
