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

package org.openflexo.foundation.fml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.FlexoBehaviour.FlexoBehaviourImpl;
import org.openflexo.foundation.fml.rt.action.FlexoBehaviourAction;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

/**
 * Represents a parameter of a {@link FlexoBehaviour}
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FlexoBehaviourParameter.FlexoBehaviourParameterImpl.class)
@Imports({ @Import(CheckboxParameter.class), @Import(DropDownParameter.class), @Import(FloatParameter.class),
		@Import(IntegerParameter.class), @Import(ListParameter.class), @Import(TextAreaParameter.class), @Import(TextFieldParameter.class),
		@Import(FlexoConceptInstanceParameter.class), @Import(URIParameter.class), @Import(TechnologyObjectParameter.class),
		@Import(FlexoResourceParameter.class), @Import(FlexoVMIResourceParameter.class), @Import(GenericBehaviourParameter.class) })
public interface FlexoBehaviourParameter extends FlexoBehaviourObject, FunctionArgument {

	public static enum WidgetType {
		TEXT_FIELD, TEXT_AREA, URI, LOCALIZED_TEXT_FIELD, INTEGER, FLOAT, CHECKBOX, DROPDOWN, RADIO_BUTTON, CUSTOM_WIDGET;
		/*INDIVIDUAL,
		CLASS,
		PROPERTY,
		OBJECT_PROPERTY,
		DATA_PROPERTY,
		FLEXO_OBJECT,
		FLEXO_CONCEPT,
		TECHNOLOGY_OBJECT,
		TECHNOLOGY_RESOURCE,
		VMI_RESOURCE;*/
	}

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String DEFAULT_VALUE_KEY = "defaultValue";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = Type.class)
	public static final String TYPE_KEY = "type";
	@PropertyIdentifier(type = WidgetType.class)
	public static final String WIDGET_KEY = "widget";
	@PropertyIdentifier(type = boolean.class)
	public static final String IS_REQUIRED_KEY = "isRequired";
	@PropertyIdentifier(type = FlexoBehaviour.class)
	public static final String FLEXO_BEHAVIOUR_KEY = "flexoBehaviour";

	@Override
	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Override
	@Setter(NAME_KEY)
	public void setName(String name);

	@Getter(value = TYPE_KEY, isStringConvertable = true)
	@XMLAttribute
	public abstract Type getType();

	@Setter(TYPE_KEY)
	public void setType(Type aType);

	@Getter(value = WIDGET_KEY)
	@XMLAttribute
	public WidgetType getWidget();

	@Setter(WIDGET_KEY)
	public void setWidget(WidgetType widget);

	@Getter(value = DEFAULT_VALUE_KEY)
	@XMLAttribute
	public DataBinding<?> getDefaultValue();

	@Setter(DEFAULT_VALUE_KEY)
	public void setDefaultValue(DataBinding<?> defaultValue);

	@Override
	@Getter(value = DESCRIPTION_KEY)
	@XMLElement
	public String getDescription();

	@Override
	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = IS_REQUIRED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsRequired();

	@Setter(IS_REQUIRED_KEY)
	public void setIsRequired(boolean isRequired);

	public boolean isValid(FlexoBehaviourAction<?, ?, ?> action, Object value);

	public Object getDefaultValue(BindingEvaluationContext evaluationContext);

	public DataBinding<?> getContainer();

	public void setContainer(DataBinding<?> container);

	public Object getContainer(BindingEvaluationContext evaluationContext);

	public DataBinding<List<?>> getList();

	public void setList(DataBinding<List<?>> list);

	public Object getList(BindingEvaluationContext evaluationContext);

	public int getIndex();

	@Getter(value = FLEXO_BEHAVIOUR_KEY, inverse = FlexoBehaviour.PARAMETERS_KEY)
	@CloningStrategy(StrategyType.IGNORE)
	public FlexoBehaviour getBehaviour();

	@Setter(FLEXO_BEHAVIOUR_KEY)
	public void setBehaviour(FlexoBehaviour flexoBehaviour);

	public List<WidgetType> getAvailableWidgetTypes();

	public boolean isListType();

	public static abstract class FlexoBehaviourParameterImpl extends FlexoBehaviourObjectImpl implements FlexoBehaviourParameter {

		private static final Logger logger = Logger.getLogger(FlexoBehaviourParameter.class.getPackage().getName());

		private DataBinding<?> defaultValue;
		private DataBinding<?> container;
		private DataBinding<List<?>> list;

		public FlexoBehaviourParameterImpl() {
			super();
		}

		@Override
		public String getURI() {
			if (getFlexoBehaviour() != null) {
				return getFlexoBehaviour().getURI() + "." + getName();
			}
			return null;
		}

		@Override
		public String getStringRepresentation() {
			return (getOwningVirtualModel() != null ? getOwningVirtualModel().getStringRepresentation() : "null") + "#"
					+ (getFlexoConcept() != null ? getFlexoConcept().getName() : "null") + "."
					+ (getFlexoBehaviour() != null ? getFlexoBehaviour().getName() : "null") + "." + getName();
		}

		@Override
		public FlexoBehaviour getFlexoBehaviour() {
			return getBehaviour();
		}

		@Override
		public void setName(String name) {
			String oldSignature = getFlexoBehaviour() != null ? getFlexoBehaviour().getSignature() : null;
			super.setName(name);
			if (getFlexoBehaviour() != null) {
				((FlexoBehaviourImpl) getFlexoBehaviour()).updateSignature(oldSignature);
			}
		}

		@Override
		public void setType(Type aType) {
			performSuperSetter(TYPE_KEY, aType);
			listType = null;
			if (list != null) {
				list.setDeclaredType(getListType());
			}
			getPropertyChangeSupport().firePropertyChange("availableWidgetTypes", null, getAvailableWidgetTypes());
			getPropertyChangeSupport().firePropertyChange("isListType", !isListType(), isListType());
			if (!getAvailableWidgetTypes().contains(getWidget()) && getAvailableWidgetTypes().size() > 0) {
				setWidget(getAvailableWidgetTypes().get(0));
			}
		}

		@Override
		public String toString() {
			return "FlexoBehaviourParameter: " + getName();
		}

		@Override
		public int getIndex() {
			if (getBehaviour() != null) {
				return getBehaviour().getParameters().indexOf(this);
			}
			return -1;
		}

		@Override
		public FlexoConcept getFlexoConcept() {
			return getBehaviour() != null ? getBehaviour().getFlexoConcept() : null;
		}

		@Override
		public BindingModel getBindingModel() {
			if (getBehaviour() != null) {
				return getBehaviour().getBindingModel();
			}
			return null;
		}

		@Override
		public DataBinding<?> getContainer() {
			if (container == null) {
				container = new DataBinding<Object>(this, Object.class, BindingDefinitionType.GET);
				container.setBindingName("container");
			}
			return container;
		}

		@Override
		public void setContainer(DataBinding<?> container) {
			if (container != null) {
				container.setOwner(this);
				container.setBindingName("container");
				container.setDeclaredType(Object.class);
				container.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.container = container;
		}

		@Override
		public Object getContainer(BindingEvaluationContext evaluationContext) {
			if (getContainer().isValid()) {
				try {
					return getContainer().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public DataBinding<List<?>> getList() {
			if (list == null) {
				list = new DataBinding<List<?>>(this, getListType(), BindingDefinitionType.GET);
			}
			return list;
		}

		@Override
		public void setList(DataBinding<List<?>> list) {
			if (list != null) {
				list.setOwner(this);
				list.setBindingName("list");
				list.setDeclaredType(getListType());
				list.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.list = list;
		}

		private ParameterizedTypeImpl listType = null;

		private Type getListType() {
			if (listType == null) {
				listType = new ParameterizedTypeImpl(List.class, getType());
			}
			return listType;
		}

		@Override
		public Object getList(BindingEvaluationContext evaluationContext) {
			if (getList().isValid()) {
				try {
					return getList().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		public DataBinding<?> getDefaultValue() {
			if (defaultValue == null) {
				defaultValue = new DataBinding<Object>(this, getType(), BindingDefinitionType.GET);
				defaultValue.setBindingName("defaultValue");
			}
			return defaultValue;
		}

		@Override
		public void setDefaultValue(DataBinding<?> defaultValue) {
			if (defaultValue != null) {
				defaultValue.setOwner(this);
				defaultValue.setBindingName("defaultValue");
				defaultValue.setDeclaredType(getType());
				defaultValue.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.defaultValue = defaultValue;
		}

		@Override
		public Object getDefaultValue(BindingEvaluationContext evaluationContext) {
			// DiagramPaletteElement paletteElement = action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement() :
			// null;

			// System.out.println("Default value for "+element.getName()+" ???");
			/*if (getUsePaletteLabelAsDefaultValue() && paletteElement != null) {
				return paletteElement.getName();
			}*/
			if (getDefaultValue().isValid()) {
				try {
					return getDefaultValue().getBindingValue(evaluationContext);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		private boolean isRequired = false;

		@Override
		public boolean getIsRequired() {
			return isRequired;
		}

		@Override
		public final void setIsRequired(boolean flag) {
			isRequired = flag;
		}

		@Override
		public boolean isValid(FlexoBehaviourAction action, Object value) {
			if (!getIsRequired()) {
				return true;
			}

			if (value instanceof String) {
				return StringUtils.isNotEmpty((String) value);
			}

			return value != null;
		}

		@Override
		public Function getFunction() {
			return getFlexoBehaviour();
		}

		@Override
		public String getArgumentName() {
			return getName();
		}

		@Override
		public Type getArgumentType() {
			return getType();
		}

		@Override
		public boolean isListType() {
			return TypeUtils.isList(getType());
		}

		@Override
		public List<WidgetType> getAvailableWidgetTypes() {
			return getAvailableWidgetTypes(getType());
		}

		private static WidgetType[] STRING_WIDGET_TYPES_ARRAY = { WidgetType.TEXT_FIELD, WidgetType.TEXT_AREA, WidgetType.URI,
				WidgetType.LOCALIZED_TEXT_FIELD, WidgetType.DROPDOWN, WidgetType.RADIO_BUTTON, WidgetType.CUSTOM_WIDGET };
		private static WidgetType[] BOOLEAN_WIDGET_TYPES_ARRAY = { WidgetType.CHECKBOX, WidgetType.CUSTOM_WIDGET };
		private static WidgetType[] FLOAT_WIDGET_TYPES_ARRAY = { WidgetType.FLOAT, WidgetType.CUSTOM_WIDGET };
		private static WidgetType[] INTEGER_WIDGET_TYPES_ARRAY = { WidgetType.INTEGER, WidgetType.CUSTOM_WIDGET };
		private static WidgetType[] LIST_WIDGET_TYPES_ARRAY = { WidgetType.DROPDOWN, WidgetType.RADIO_BUTTON, WidgetType.CUSTOM_WIDGET };
		private static WidgetType[] CUSTOM_WIDGET_TYPES_ARRAY = { WidgetType.CUSTOM_WIDGET };

		private static List<WidgetType> STRING_WIDGET_TYPES = Arrays.asList(STRING_WIDGET_TYPES_ARRAY);
		private static List<WidgetType> BOOLEAN_WIDGET_TYPES = Arrays.asList(BOOLEAN_WIDGET_TYPES_ARRAY);
		private static List<WidgetType> FLOAT_WIDGET_TYPES = Arrays.asList(FLOAT_WIDGET_TYPES_ARRAY);
		private static List<WidgetType> INTEGER_WIDGET_TYPES = Arrays.asList(INTEGER_WIDGET_TYPES_ARRAY);
		private static List<WidgetType> LIST_WIDGET_TYPES = Arrays.asList(LIST_WIDGET_TYPES_ARRAY);
		private static List<WidgetType> CUSTOM_WIDGET_TYPES = Arrays.asList(CUSTOM_WIDGET_TYPES_ARRAY);

		public static List<WidgetType> getAvailableWidgetTypes(Type type) {
			if (TypeUtils.isString(type)) {
				return STRING_WIDGET_TYPES;
			}
			else if (TypeUtils.isBoolean(type)) {
				return BOOLEAN_WIDGET_TYPES;
			}
			else if (TypeUtils.isDouble(type) || TypeUtils.isFloat(type)) {
				return FLOAT_WIDGET_TYPES;
			}
			else if (TypeUtils.isLong(type) || TypeUtils.isInteger(type) || TypeUtils.isShort(type)) {
				return INTEGER_WIDGET_TYPES;
			}
			else if (TypeUtils.isList(type)) {
				return LIST_WIDGET_TYPES;
			}
			return CUSTOM_WIDGET_TYPES;
		}
	}
}
