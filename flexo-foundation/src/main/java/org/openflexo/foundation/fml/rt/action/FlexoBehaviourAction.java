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

package org.openflexo.foundation.fml.rt.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.ListParameter;
import org.openflexo.foundation.fml.URIParameter;
import org.openflexo.foundation.fml.binding.FMLBindingFactory;
import org.openflexo.foundation.fml.binding.FlexoBehaviourBindingModel;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.foundation.fml.rt.TypeAwareModelSlotInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceObject;
import org.openflexo.foundation.fml.rt.editionaction.MatchFlexoConceptInstance;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.utils.OperationCancelledException;
import org.openflexo.toolbox.StringUtils;

/**
 * This abstract class is the root class for all actions which can be performed at conceptual or design level, generally on a view tool
 * (such as a diagram).<br>
 * An {@link FlexoBehaviourAction} represents the execution (in the "instances" world) of an {@link FlexoBehaviour}.<br>
 * To be used and executed on Openflexo platform, it is wrapped in a {@link FlexoAction}.<br>
 * 
 * 
 * @author sylvain
 * 
 * @param <A>
 */
public abstract class FlexoBehaviourAction<A extends FlexoBehaviourAction<A, FB, O>, FB extends FlexoBehaviour, O extends VirtualModelInstanceObject>
		extends FlexoAction<A, O, VirtualModelInstanceObject> implements RunTimeEvaluationContext {

	private static final Logger logger = Logger.getLogger(FlexoBehaviourAction.class.getPackage().getName());

	public static final String PARAMETER_VALUE_CHANGED = "parameterValueChanged";

	protected Hashtable<String, Object> variables;
	protected ParameterValues parameterValues;
	protected Hashtable<ListParameter, List> parameterListValues;

	private MatchingSet defaultMatchingSet = null;

	protected Object returnedValue = null;

	public boolean escapeParameterRetrievingWhenValid = true;

	public FlexoBehaviourAction(FlexoActionType<A, O, VirtualModelInstanceObject> actionType, O focusedObject,
			List<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		variables = new Hashtable<String, Object>();
		parameterValues = new ParameterValues();
		parameterListValues = new Hashtable<ListParameter, List>();
	}

	@Override
	public boolean isValid() {
		if (!super.isValid()) {
			return false;
		}
		if (getFlexoBehaviour() == null) {
			return false;
		}
		return true;
	}

	/**
	 * Compute and store default parameters, and return a flag indicating if all parameters declared as "mandatory" could be successfully
	 * filled
	 * 
	 * @return
	 */
	// TODO: we must order this if dependancies are not resolved using basic sequence
	public boolean retrieveDefaultParameters() {
		boolean returned = true;
		FlexoBehaviour flexoBehaviour = getFlexoBehaviour();
		// logger.info("BEGIN retrieveDefaultParameters() for " + flexoBehaviour);
		for (final FlexoBehaviourParameter parameter : flexoBehaviour.getParameters()) {
			Object defaultValue = parameter.getDefaultValue(this);
			// logger.info("Parameter " + parameter.getName() + " default value = " + defaultValue);
			if (defaultValue != null) {
				parameterValues.put(parameter, defaultValue);
			}
			if (parameter instanceof ListParameter) {
				List list = (List) ((ListParameter) parameter).getList(this);
				parameterListValues.put((ListParameter) parameter, list);
			}
			// logger.info("Parameter " + parameter.getName() + " valid=" + parameter.isValid(this, defaultValue));
			if (!parameter.isValid(this, defaultValue)) {
				// logger.info("Parameter " + parameter + " is not valid for value " + defaultValue);
				returned = false;
			}
		}
		// logger.info("END retrieveDefaultParameters() for " + flexoBehaviour);
		return returned;
	}

	/**
	 * Return a flag indicating if all parameters declared as "mandatory" have been set
	 * 
	 * @return
	 */
	public boolean areRequiredParametersSetAndValid() {
		FlexoBehaviour flexoBehaviour = getFlexoBehaviour();
		for (final FlexoBehaviourParameter parameter : flexoBehaviour.getParameters()) {
			if (!parameter.isValid(this, parameterValues.get(parameter))) {
				return false;
			}
		}
		return true;
	}

	public FlexoResourceCenter<?> getResourceCenter() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getResourceCenter();
		}
		return null;
	}

	public FlexoConcept getFlexoConcept() {
		if (getFlexoBehaviour() != null) {
			return getFlexoBehaviour().getFlexoConcept();
		}
		return null;
	}

	/**
	 * Calling this method will register a new variable in the run-time context provided by this {@link FlexoBehaviourAction} instance in
	 * the context of its implementation of {@link RunTimeEvaluationContext}.<br>
	 * Variable is initialized with supplied name and value
	 * 
	 * @param variableName
	 * @param value
	 */
	@Override
	public void declareVariable(String variableName, Object value) {
		if (value != null) {
			variables.put(variableName, value);
		}
		else {
			variables.remove(variableName);
		}
	}

	/**
	 * Calling this method will dereference variable identified by supplied name
	 * 
	 * @param variableName
	 */
	@Override
	public void dereferenceVariable(String variableName) {
		variables.remove(variableName);
	}

	public Object getParameterValue(FlexoBehaviourParameter parameter) {
		/*System.out.println("On me demande la valeur du parametre " + parameter.getName() + " a priori c'est "
				+ parameterValues.get(parameter));*/
		if (parameter instanceof URIParameter) {
			if (parameterValues.get(parameter) == null
					|| parameterValues.get(parameter) instanceof String && StringUtils.isEmpty((String) parameterValues.get(parameter))) {
				return ((URIParameter) parameter).getDefaultValue(this);
			}
		}
		return parameterValues.get(parameter);
	}

	public void setParameterValue(FlexoBehaviourParameter parameter, Object value) {
		// System.out.println("setParameterValue " + value + " for parameter " + parameter.getName());
		parameterValues.put(parameter, value);
		/*for (FlexoBehaviourParameter p : getEditionScheme().getParameters()) {
			if (p instanceof URIParameter) {
				// System.out.println("Hop, je recalcule l'uri, ici");
			}
		}*/
	}

	public List getParameterListValue(ListParameter parameter) {
		/*System.out.println("On me demande la valeur du parametre " + parameter.getName() + " a priori c'est "
				+ parameterValues.get(parameter));*/
		return parameterListValues.get(parameter);
	}

	public abstract FB getFlexoBehaviour();

	@Override
	public AbstractVirtualModelInstance getVirtualModelInstance() {
		return retrieveVirtualModelInstance();
	}

	public abstract AbstractVirtualModelInstance<?, ?> retrieveVirtualModelInstance();

	/**
	 * Return the {@link FlexoConceptInstance} on which this {@link FlexoBehaviour} is applied.<br>
	 * An {@link FlexoBehaviourAction} may concern an existing {@link FlexoConceptInstance} or may also refer to an
	 * {@link FlexoConceptInstance} instance to be created (if related {@link FlexoBehaviour} is a {@link CreationSchemeAction}).
	 * 
	 * @return
	 */
	@Override
	public abstract FlexoConceptInstance getFlexoConceptInstance();

	/**
	 * This is the internal code performing execution of the control graph of {@link FlexoBehaviour}
	 */
	protected void executeControlGraph() throws OperationCancelledException, FlexoException {

		if (getFlexoBehaviour() != null && getFlexoBehaviour().getControlGraph() != null) {
			try {
				getFlexoBehaviour().getControlGraph().execute(this);
			} catch (ReturnException e) {
				returnedValue = e.getReturnedValue();
			} catch (OperationCancelledException e) {
				throw e;
			} catch (Exception e) {
				logger.warning("Unexpected exception while executing FML control graph: " + e);
				System.err.println(getFlexoBehaviour().getFMLRepresentation());
				e.printStackTrace();
				throw new FlexoException(e);
			}
			if (defaultMatchingSet != null) {
				finalizeDefaultMatchingSet();
			}
		}

	}

	public Object getReturnedValue() {
		return returnedValue;
	}

	public MatchingSet initiateDefaultMatchingSet(MatchFlexoConceptInstance action) {
		if (defaultMatchingSet == null) {
			defaultMatchingSet = new MatchingSet(action, this);
		}
		return defaultMatchingSet;
	}

	public void finalizeDefaultMatchingSet() {
		if (defaultMatchingSet != null) {
			for (FlexoConceptInstance fci : new ArrayList<>(defaultMatchingSet.getUnmatchedInstances())) {
				fci.delete();
			}
		}
		defaultMatchingSet = null;
	}

	/**
	 * Override when required
	 * 
	 * @param action
	 */
	public <T> void hasPerformedAction(EditionAction action, T object) {
	}

	@Override
	public Object getValue(BindingVariable variable) {

		if (variables.get(variable.getVariableName()) != null) {
			return variables.get(variable.getVariableName());
		}

		if (variable.getVariableName().equals(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY)) {
			return getParametersValues();
		}
		else if (variable.getVariableName().equals(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY)) {
			return getFlexoBehaviour().getParameters();
		}
		else if (variable.getVariableName().equals(FMLBindingFactory.FLEXO_CONCEPT_INSTANCE)) {
			return getFlexoConceptInstance();
		}
		else if (variable.getVariableName().equals(FMLBindingFactory.VIRTUAL_MODEL_INSTANCE)) {
			if (getFlexoConceptInstance() instanceof AbstractVirtualModelInstance) {
				return getFlexoConceptInstance();
			}
			return getVirtualModelInstance();
		}

		// Not found at this level, delegate it to the FlexoConceptInstance
		if (getFlexoConceptInstance() != null) {
			return getFlexoConceptInstance().getValue(variable);
		}

		logger.warning("Unexpected variable requested in FlexoBehaviourAction: " + variable + " of " + variable.getClass());
		return null;

	}

	@Override
	public void setValue(Object value, BindingVariable variable) {

		if (variables.get(variable.getVariableName()) != null) {
			variables.put(variable.getVariableName(), value);
			return;
		}
		else if (variable.getVariableName().equals(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY)) {
			logger.warning(
					"Forbidden write access " + FlexoBehaviourBindingModel.PARAMETERS_PROPERTY + " in " + this + " of " + getClass());
			return;
		}
		else if (variable.getVariableName().equals(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY)) {
			logger.warning("Forbidden write access " + FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY + " in " + this + " of "
					+ getClass());
			return;
		}

		if (getFlexoConceptInstance() != null) {
			getFlexoConceptInstance().setValue(value, variable);
			return;
		}

		logger.warning(
				"Unexpected variable requested in settable context in FlexoBehaviourAction: " + variable + " of " + variable.getClass());
	}

	public ParameterValues getParametersValues() {
		return parameterValues;
	}

	public boolean parameterValueChanged() {
		setChanged();
		notifyObservers(new DataModification(PARAMETER_VALUE_CHANGED, null, getParametersValues()));
		return true;
	}

	public class ParameterValues extends Hashtable<FlexoBehaviourParameter, Object> {

		@Override
		public synchronized Object put(FlexoBehaviourParameter parameter, Object value) {
			if (value == null) {
				return null;
			}
			Object returned = super.put(parameter, value);
			for (FlexoBehaviourParameter p : parameter.getFlexoBehaviour().getParameters()) {
				if (p != parameter && p instanceof URIParameter && ((URIParameter) p).getModelSlot() instanceof TypeAwareModelSlot) {
					URIParameter uriParam = (URIParameter) p;
					TypeAwareModelSlot modelSlot = uriParam.getModelSlot();
					String newURI;
					try {
						newURI = uriParam.getBaseURI().getBindingValue(FlexoBehaviourAction.this);

						newURI = modelSlot.generateUniqueURIName(
								(TypeAwareModelSlotInstance) getVirtualModelInstance().getModelSlotInstance(modelSlot), newURI);
						logger.info("Generated new URI " + newURI + " for " + getVirtualModelInstance().getModelSlotInstance(modelSlot));
						// NPE Protection
						if (newURI != null) {
							super.put(uriParam, newURI);
						}
					} catch (TypeMismatchException e) {
						e.printStackTrace();
					} catch (NullReferenceException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			parameterValueChanged();
			return returned;
		}
	}

	public String retrieveFullURI(FlexoBehaviourParameter parameter) {
		if (parameter instanceof URIParameter) {
			URIParameter uriParam = (URIParameter) parameter;
			if (uriParam.getModelSlot() instanceof TypeAwareModelSlot) {
				TypeAwareModelSlot modelSlot = uriParam.getModelSlot();
				return modelSlot.generateUniqueURI((TypeAwareModelSlotInstance) getVirtualModelInstance().getModelSlotInstance(modelSlot),
						(String) getParameterValue(parameter));
			}
		}
		return "Invalid URI";
	}

	@Override
	public void debug(String aLogString, FlexoConceptInstance fci, FlexoBehaviour behaviour) {
		if (getEditor() != null) {
			getEditor().getFMLConsole().debug(aLogString, fci, behaviour);
		}
	}

	@Override
	public void log(String aLogString, LogLevel logLevel, FlexoConceptInstance fci, FlexoBehaviour behaviour) {
		if (getEditor() != null) {
			getEditor().getFMLConsole().log(aLogString, logLevel, fci, behaviour);
		}
	}
}
