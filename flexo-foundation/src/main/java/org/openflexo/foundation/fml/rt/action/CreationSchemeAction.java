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
package org.openflexo.foundation.fml.rt.action;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.ListParameter;
import org.openflexo.foundation.fml.binding.FlexoRoleBindingVariable;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceObject;

public class CreationSchemeAction extends FlexoBehaviourAction<CreationSchemeAction, CreationScheme, VirtualModelInstance> {

	private static final Logger logger = Logger.getLogger(CreationSchemeAction.class.getPackage().getName());

	public static FlexoActionType<CreationSchemeAction, VirtualModelInstance, VirtualModelInstanceObject> actionType = new FlexoActionType<CreationSchemeAction, VirtualModelInstance, VirtualModelInstanceObject>(
			"create_flexo_concept_instance", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreationSchemeAction makeNewAction(VirtualModelInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
				FlexoEditor editor) {
			return new CreationSchemeAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(VirtualModelInstance object, Vector<VirtualModelInstanceObject> globalSelection) {
			return true;
		}

	};

	static {
		// FlexoObject.addActionForClass(actionType, DiagramElement.class);
		FlexoObjectImpl.addActionForClass(actionType, VirtualModelInstance.class);
		// FlexoObject.addActionForClass(actionType, View.class);
	}

	private VirtualModelInstance vmInstance;
	private CreationScheme _creationScheme;

	CreationSchemeAction(VirtualModelInstance focusedObject, Vector<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	// private Hashtable<EditionAction,FlexoModelObject> createdObjects;

	private FlexoConceptInstance flexoConceptInstance;

	@Override
	protected void doAction(Object context) throws NotImplementedException, InvalidParametersException, FlexoException {
		logger.info("Create FlexoConceptInstance using CreationScheme");
		logger.info("getFlexoConcept()=" + getFlexoConcept());

		retrieveMissingDefaultParameters();

		// getFlexoConcept().getViewPoint().getViewpointOntology().loadWhenUnloaded();

		// In case of this action is embedded in a CreateVirtualModelInstance action, the flexoConceptInstance (which will be here a
		// VirtualModelInstance) will be already initialized and should subsequently not been recreated)
		if (flexoConceptInstance == null) {
			if (getVirtualModelInstance() != null) {
				flexoConceptInstance = getVirtualModelInstance().makeNewFlexoConceptInstance(getFlexoConcept());
			} else {
				logger.warning("Could not create new FlexoConceptInstance because container VirtualModelInstance is null");
				throw new InvalidParametersException("VirtualModelInstance");
			}
		}

		applyEditionActions();

	}

	/**
	 * Used when creation of FlexoConceptInstance initialization is beeing delegated to an other component.<br>
	 * This happens for example in the case of VirtualModelInstance creation, where the creation of FlexoConceptInstance is performed in the
	 * {@link CreateVirtualModelInstance} action
	 * 
	 * @param flexoConceptInstance
	 */
	public void initWithFlexoConceptInstance(FlexoConceptInstance flexoConceptInstance) {
		this.flexoConceptInstance = flexoConceptInstance;
	}

	public boolean retrieveMissingDefaultParameters() {
		boolean returned = true;
		FlexoBehaviour flexoBehaviour = getFlexoBehaviour();
		for (final FlexoBehaviourParameter parameter : flexoBehaviour.getParameters()) {
			if (getParameterValue(parameter) == null) {
				logger.warning("Found not initialized parameter " + parameter);
				Object defaultValue = parameter.getDefaultValue(this);
				if (defaultValue != null) {
					logger.warning("Du coup je lui donne la valeur " + defaultValue);
					parameterValues.put(parameter, defaultValue);
					if (!parameter.isValid(this, defaultValue)) {
						logger.info("Parameter " + parameter + " is not valid for value " + defaultValue);
						returned = false;
					}
				}
			}
			if (parameter instanceof ListParameter) {
				List list = (List) ((ListParameter) parameter).getList(this);
				parameterListValues.put((ListParameter) parameter, list);
			}
		}
		return returned;
	}

	@Override
	public VirtualModelInstance getVirtualModelInstance() {
		if (vmInstance == null) {
			if (getFocusedObject() instanceof VirtualModelInstance) {
				vmInstance = getFocusedObject();
			}
		}
		return vmInstance;
	}

	public void setVirtualModelInstance(VirtualModelInstance vmInstance) {
		this.vmInstance = vmInstance;
	}

	public CreationScheme getCreationScheme() {
		return _creationScheme;
	}

	public void setCreationScheme(CreationScheme creationScheme) {
		_creationScheme = creationScheme;
	}

	@Override
	public CreationScheme getFlexoBehaviour() {
		return getCreationScheme();
	}

	@Override
	public FlexoConceptInstance getFlexoConceptInstance() {
		return flexoConceptInstance;
	}

	@Override
	public VirtualModelInstance retrieveVirtualModelInstance() {
		return getVirtualModelInstance();
	}

	/**
	 * This is the internal code performing execution of a single {@link EditionAction} defined to be part of the execution control graph of
	 * related {@link FlexoBehaviour}<br>
	 */
	/*@Override
	protected Object performAction(EditionAction action, Hashtable<EditionAction, Object> performedActions) throws FlexoException {
		Object assignedObject = super.performAction(action, performedActions);
		if (assignedObject != null && action instanceof AssignableAction) {
			AssignableAction assignableAction = (AssignableAction) action;
			if (assignableAction.getFlexoRole() != null) {
				getFlexoConceptInstance().setObjectForFlexoRole(assignedObject, assignableAction.getFlexoRole());
			}
		}

		return assignedObject;
	}*/

	@Override
	public Object getValue(BindingVariable variable) {
		return super.getValue(variable);
	}

	@Override
	public void setValue(Object value, BindingVariable variable) {
		if (variable instanceof FlexoRoleBindingVariable) {
			getFlexoConceptInstance().setFlexoActor(value, ((FlexoRoleBindingVariable) variable).getFlexoRole());
			return;
		}
		super.setValue(value, variable);
	}

}