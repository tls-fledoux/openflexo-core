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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.fml.AbstractVirtualModel;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceRole;
import org.openflexo.foundation.fml.FlexoConceptObject;
import org.openflexo.foundation.fml.FlexoConceptStructuralFacet;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.IndividualRole;
import org.openflexo.foundation.fml.PrimitiveRole;
import org.openflexo.foundation.fml.PrimitiveRole.PrimitiveType;
import org.openflexo.foundation.fml.RoleCardinality;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.rt.FMLRTModelSlot;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.toolbox.StringUtils;

public class CreateFlexoRole extends FlexoAction<CreateFlexoRole, FlexoConceptObject, FMLObject> {

	private static final Logger logger = Logger.getLogger(CreateFlexoRole.class.getPackage().getName());

	private List<Class<? extends FlexoRole<?>>> vmAvailableFlexoRoleTypes = null;

	public static FlexoActionType<CreateFlexoRole, FlexoConceptObject, FMLObject> actionType = new FlexoActionType<CreateFlexoRole, FlexoConceptObject, FMLObject>(
			"create_flexo_role", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateFlexoRole makeNewAction(FlexoConceptObject focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new CreateFlexoRole(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoConceptObject object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoConceptObject object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateFlexoRole.actionType, FlexoConcept.class);
		FlexoObjectImpl.addActionForClass(CreateFlexoRole.actionType, FlexoConceptStructuralFacet.class);
	}

	private String roleName;
	private String description;
	private ModelSlot<?> modelSlot;
	private Class<? extends FlexoRole> flexoRoleClass;
	private IFlexoOntologyClass individualType;
	private FlexoConcept flexoConceptInstanceType;
	private PrimitiveType primitiveType = PrimitiveType.String;
	private RoleCardinality roleCardinality = RoleCardinality.ZeroOne;

	private FlexoRole<?> newFlexoRole;

	CreateFlexoRole(FlexoConceptObject focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		setModelSlot(retrieveDefaultModelSlot());
	}

	public FlexoConcept getFlexoConcept() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getFlexoConcept();
		}
		return null;
	}

	public String getRoleName() {
		if (StringUtils.isEmpty(roleName) && modelSlot != null && flexoRoleClass != null) {
			return getFlexoConcept().getAvailableRoleName(modelSlot.defaultFlexoRoleName(flexoRoleClass));
		}
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
		getPropertyChangeSupport().firePropertyChange("roleName", null, roleName);
	}

	public RoleCardinality getRoleCardinality() {
		return roleCardinality;
	}

	public void setRoleCardinality(RoleCardinality roleCardinality) {
		if (roleCardinality != getRoleCardinality()) {
			RoleCardinality oldRoleCardinality = getRoleCardinality();
			this.roleCardinality = roleCardinality;
			getPropertyChangeSupport().firePropertyChange("roleCardinality", oldRoleCardinality, roleCardinality);
		}
	}

	public List<Class<? extends FlexoRole<?>>> getAvailableFlexoRoleTypes() {
		if (vmAvailableFlexoRoleTypes == null) {
			vmAvailableFlexoRoleTypes = new ArrayList<Class<? extends FlexoRole<?>>>();
			vmAvailableFlexoRoleTypes.add(FlexoConceptInstanceRole.class);
			vmAvailableFlexoRoleTypes.add((Class<? extends FlexoRole<?>>) PrimitiveRole.class);

		}
		if (getModelSlot() != null) {
			return getModelSlot().getAvailableFlexoRoleTypes();
		} else {
			FlexoConcept fc = (FlexoConcept) this.getFocusedObject();
			if (fc != null) {
				AbstractVirtualModel<?> vm = fc.getOwningVirtualModel();
				if (vm != null) {
					return vmAvailableFlexoRoleTypes;
				}
			}
		}
		return null;
	}

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParameterException {
		logger.info("Add flexo role, flexoRoleClass=" + flexoRoleClass);
		logger.info("modelSlot = " + modelSlot);

		if (flexoRoleClass != null) {
			if (modelSlot != null) {
				newFlexoRole = modelSlot.makeFlexoRole(flexoRoleClass);
				newFlexoRole.setModelSlot(modelSlot);
			} else {
				FMLModelFactory factory = getFocusedObject().getFMLModelFactory();
				newFlexoRole = factory.newInstance(flexoRoleClass);
			}

			if (newFlexoRole != null) {

				if (isIndividual()) {
					((IndividualRole<?>) newFlexoRole).setOntologicType(individualType);
				}
				if (isFlexoConceptInstance()) {
					((FlexoConceptInstanceRole) newFlexoRole).setFlexoConceptType(flexoConceptInstanceType);
				}
				if (PrimitiveRole.class.isAssignableFrom(flexoRoleClass)) {
					((PrimitiveRole) newFlexoRole).setPrimitiveType(primitiveType);
					logger.info("Created " + newFlexoRole + " with " + primitiveType);
				}

				newFlexoRole.setRoleName(getRoleName());
				newFlexoRole.setCardinality(getRoleCardinality());
				newFlexoRole.setDescription(description);
				getFlexoConcept().addToFlexoRoles(newFlexoRole);
			}
		}

	}

	public FlexoRole<?> getNewFlexoRole() {
		return newFlexoRole;
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isEmpty(getRoleName())) {
			return false;
		} else if (getFlexoConcept().getFlexoRole(getRoleName()) != null) {
			return false;
		}
		return true;
	}

	public boolean isIndividual() {
		if (flexoRoleClass == null) {
			return false;
		}
		return IndividualRole.class.isAssignableFrom(flexoRoleClass);
	}

	public boolean isFlexoConceptInstance() {
		if (flexoRoleClass == null) {
			return false;
		}
		return FlexoConceptInstanceRole.class.isAssignableFrom(flexoRoleClass);
	}

	public boolean isPrimitive() {
		if (flexoRoleClass == null) {
			return false;
		}
		return PrimitiveRole.class.isAssignableFrom(flexoRoleClass);
	}

	public AbstractVirtualModel<?> getModelSlotVirtualModel() {
		if (modelSlot instanceof FMLRTModelSlot) {
			if (((FMLRTModelSlot) modelSlot).getVirtualModelResource() != null) {
				return ((FMLRTModelSlot) modelSlot).getVirtualModelResource().getVirtualModel();
			}
		} else if (modelSlot == null) {
			return getFlexoConcept().getVirtualModel();
		}
		return null;
	}

	public List<ModelSlot<?>> getAvailableModelSlots() {

		if (getFocusedObject() instanceof VirtualModel) {
			return ((VirtualModel) getFocusedObject()).getModelSlots();
		} else if (getFocusedObject() != null && getFocusedObject().getOwningVirtualModel() != null) {
			return getFocusedObject().getOwningVirtualModel().getModelSlots();
		}
		return null;
	}

	private ModelSlot<?> retrieveDefaultModelSlot() {
		if (getFocusedObject() instanceof VirtualModel && ((VirtualModel) getFocusedObject()).getModelSlots().size() > 0) {
			return ((VirtualModel) getFocusedObject()).getModelSlots().get(0);
		} else if (getFocusedObject() != null && getFocusedObject().getOwningVirtualModel() != null
				&& getFocusedObject().getOwningVirtualModel().getModelSlots().size() > 0) {
			return getFocusedObject().getOwningVirtualModel().getModelSlots().get(0);
		}
		return null;
	}

	/**
	 * Return a metamodel adressed by a model slot
	 * 
	 * @return
	 */
	public FlexoMetaModel<?> getAdressedFlexoMetaModel() {
		if (modelSlot instanceof TypeAwareModelSlot) {
			TypeAwareModelSlot<?, ?> typeAwareModelSlot = (TypeAwareModelSlot<?, ?>) modelSlot;
			return typeAwareModelSlot.getMetaModelResource().getMetaModelData();
		}
		return null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		getPropertyChangeSupport().firePropertyChange("description", null, description);
	}

	public ModelSlot<?> getModelSlot() {
		return modelSlot;
	}

	public TechnologyAdapter getTechnologyAdapterForModelSlot() {
		if (modelSlot != null) {
			return modelSlot.getModelSlotTechnologyAdapter();
		} else {
			return getFlexoConcept().getOwningVirtualModel().getTechnologyAdapter();
		}
	}

	public void setModelSlot(ModelSlot<?> modelSlot) {
		this.modelSlot = modelSlot;
		getPropertyChangeSupport().firePropertyChange("modelSlot", null, modelSlot);
		getPropertyChangeSupport().firePropertyChange("roleName", null, getRoleName());
		getPropertyChangeSupport().firePropertyChange("availableFlexoRoleTypes", null, getAvailableFlexoRoleTypes());
		getPropertyChangeSupport().firePropertyChange("modelSlotVirtualModel", null, getModelSlotVirtualModel());
		if (getFlexoRoleClass() != null && !getAvailableFlexoRoleTypes().contains(getFlexoRoleClass())) {
			if (getAvailableFlexoRoleTypes().size() > 0) {
				setFlexoRoleClass(getAvailableFlexoRoleTypes().get(0));
			} else {
				setFlexoRoleClass(null);
			}
		}
		if (modelSlot != null && getFlexoRoleClass() == null && getAvailableFlexoRoleTypes().size() > 0) {
			setFlexoRoleClass(getAvailableFlexoRoleTypes().get(0));
		}
	}

	public Class<? extends FlexoRole> getFlexoRoleClass() {
		return flexoRoleClass;
	}

	public void setFlexoRoleClass(Class<? extends FlexoRole> flexoRoleClass) {
		this.flexoRoleClass = flexoRoleClass;
		getPropertyChangeSupport().firePropertyChange("flexoRoleClass", flexoRoleClass != null ? null : false, flexoRoleClass);
		getPropertyChangeSupport().firePropertyChange("isIndividual", !isIndividual(), isIndividual());
		getPropertyChangeSupport().firePropertyChange("isFlexoConceptInstance", !isFlexoConceptInstance(), isFlexoConceptInstance());
		getPropertyChangeSupport().firePropertyChange("isPrimitive", !isPrimitive(), isPrimitive());
	}

	public IFlexoOntologyClass getIndividualType() {
		return individualType;
	}

	public void setIndividualType(IFlexoOntologyClass individualType) {
		this.individualType = individualType;
		getPropertyChangeSupport().firePropertyChange("individualType", flexoRoleClass != null ? null : false, flexoRoleClass);
	}

	public FlexoConcept getFlexoConceptInstanceType() {
		return flexoConceptInstanceType;
	}

	public void setFlexoConceptInstanceType(FlexoConcept flexoConceptInstanceType) {
		this.flexoConceptInstanceType = flexoConceptInstanceType;
		getPropertyChangeSupport().firePropertyChange("flexoConceptInstanceType", null, flexoConceptInstanceType);
	}

	public PrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	public void setPrimitiveType(PrimitiveType primitiveType) {
		this.primitiveType = primitiveType;
		getPropertyChangeSupport().firePropertyChange("primitiveType", null, primitiveType);
	}

}
