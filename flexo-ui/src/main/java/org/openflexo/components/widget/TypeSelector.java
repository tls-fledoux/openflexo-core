/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.openflexo.ApplicationContext;
import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.connie.type.GenericArrayTypeImpl;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.WilcardTypeImpl;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.PrimitiveRole.PrimitiveType;
import org.openflexo.foundation.fml.TechnologyAdapterTypeFactory;
import org.openflexo.gina.ApplicationFIBLibrary.ApplicationFIBLibraryImpl;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent;
import org.openflexo.gina.swing.utils.LoadedClassesInfo;
import org.openflexo.gina.swing.utils.LoadedClassesInfo.ClassInfo;
import org.openflexo.gina.swing.utils.logging.FlexoLoggingViewer;
import org.openflexo.gina.swing.view.JFIBView;
import org.openflexo.gina.swing.view.SwingViewFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.swing.TextFieldCustomPopup;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.PropertyChangedSupportDefaultImplementation;
import org.openflexo.view.controller.FlexoFIBController;

/**
 * Widget allowing to edit a binding
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class TypeSelector extends TextFieldCustomPopup<Type>
		implements FIBCustomComponent<Type>, HasPropertyChangeSupport, PropertyChangeListener {
	static final Logger LOGGER = Logger.getLogger(TypeSelector.class.getPackage().getName());

	public static Resource FIB_FILE_NAME = ResourceLocator.locateResource("Fib/TypeSelector.fib");
	public static Resource JAVA_CLASS_EDITOR_FIB_FILE_NAME = ResourceLocator.locateResource("Fib/ClassEditor.fib");

	public static final Object JAVA_TYPE = new Object() {
		@Override
		public String toString() {
			return "Java type";
		}
	};
	public static final Object JAVA_LIST = new Object() {
		@Override
		public String toString() {
			return "Java list of";
		}
	};
	public static final Object JAVA_MAP = new Object() {
		@Override
		public String toString() {
			return "Java map of";
		}
	};
	public static final Object JAVA_ARRAY = new Object() {
		@Override
		public String toString() {
			return "Java array of";
		}
	};
	public static final Object JAVA_WILDCARD = new Object() {
		@Override
		public String toString() {
			return "Java wilcard";
		}
	};

	private Type _revertValue;

	protected TypeSelectorDetailsPanel _selectorPanel;

	private final List<Object> choices;
	private Object choice;

	private final PropertyChangeSupport pcSupport;

	private final List<GenericParameter> genericParameters;
	private final List<GenericBound> upperBounds;
	private final List<GenericBound> lowerBounds;

	private Type keyType = Object.class;

	private FlexoServiceManager serviceManager;

	public class GenericParameter extends PropertyChangedSupportDefaultImplementation {
		private TypeVariable<?> typeVariable;
		private Type type;

		public GenericParameter(TypeVariable<?> typeVariable, Type type) {
			super();
			this.typeVariable = typeVariable;
			this.type = type;
		}

		public GenericParameter(TypeVariable<?> typeVariable) {
			super();
			this.typeVariable = typeVariable;
		}

		public void delete() {
			this.typeVariable = null;
			this.type = null;
		}

		public TypeVariable<?> getTypeVariable() {
			return typeVariable;
		}

		public void setTypeVariable(TypeVariable<?> typeVariable) {
			if (typeVariable != this.typeVariable) {
				TypeVariable<?> oldValue = this.typeVariable;
				this.typeVariable = typeVariable;
				getPropertyChangeSupport().firePropertyChange("typeVariable", oldValue, typeVariable);
			}
		}

		public Type getType() {
			if (type == null) {
				return getUpperBound();
			}
			return type;
		}

		public void setType(Type type) {

			if ((type == null && this.type != null) || (type != null && !type.equals(this.type))) {
				Type oldValue = this.type;
				this.type = type;

				setEditedObject(makeParameterizedType(getBaseClass()));

				/*Type[] params = new Type[genericParameters.size()];
				for (int i = 0; i < genericParameters.size(); i++) {
					params[i] = genericParameters.get(i).getType();
				}
				setEditedObject(new ParameterizedTypeImpl(getBaseClass(), params));*/

				getPropertyChangeSupport().firePropertyChange("type", oldValue, type);
			}
		}

		@Override
		public String toString() {
			return typeVariable.getName() + "=" + TypeUtils.fullQualifiedRepresentation(type);
		}

		public ImageIcon getIcon() {
			return isValid() ? IconLibrary.VALID_ICON : IconLibrary.UNFIXABLE_ERROR_ICON;
		}

		public boolean isValid() {
			return (TypeUtils.isTypeAssignableFrom(getUpperBound(), getType()));
		}

		public Type getUpperBound() {
			if (getTypeVariable() != null && getTypeVariable().getBounds().length > 0) {
				return getTypeVariable().getBounds()[0];
			}
			return Object.class;
		}

		public String getTypeStringRepresentation() {
			return TypeUtils.simpleRepresentation(getType());
		}
	}

	public class GenericBound extends PropertyChangedSupportDefaultImplementation {
		private Type type;

		public GenericBound(Type type) {
			super();
			this.type = type;
		}

		public GenericBound() {
			this(null);
		}

		public void delete() {
			this.type = null;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {

			if ((type == null && this.type != null) || (type != null && !type.equals(this.type))) {
				Type oldValue = this.type;
				this.type = type;

				/*Type[] params = new Type[genericParameters.size()];
				for (int i = 0; i < genericParameters.size(); i++) {
					params[i] = genericParameters.get(i).getType();
				}
				setEditedObject(new ParameterizedTypeImpl(getBaseClass(), params));*/

				updateWildcardType();

				getPropertyChangeSupport().firePropertyChange("type", oldValue, type);
			}
		}

		@Override
		public String toString() {
			return TypeUtils.fullQualifiedRepresentation(type);
		}

		public ImageIcon getIcon() {
			return isValid() ? IconLibrary.VALID_ICON : IconLibrary.UNFIXABLE_ERROR_ICON;
		}

		public boolean isValid() {
			return true;
		}

		public String getTypeStringRepresentation() {
			return TypeUtils.simpleRepresentation(getType());
		}
	}

	public TypeSelector(Type editedObject) {
		super(editedObject);
		setRevertValue(editedObject);
		setFocusable(true);
		pcSupport = new PropertyChangeSupport(this);
		choices = new ArrayList<Object>();
		/*choices.add(VIEW_TYPE);
		choices.add(VIRTUAL_MODEL_INSTANCE_TYPE);
		choices.add(FLEXO_CONCEPT_INSTANCE_TYPE);*/

		genericParameters = new ArrayList<GenericParameter>();
		upperBounds = new ArrayList<GenericBound>();
		lowerBounds = new ArrayList<GenericBound>();

		updateChoices();
		if (editedObject == null) {
			// choice = PrimitiveType.String;
			choice = null;
		}

		fireEditedObjectChanged();
	}

	private final Map<Class<? extends CustomType>, CustomTypeEditor<?>> customTypeEditors = new HashMap<>();
	private final Map<Class<? extends CustomType>, TechnologyAdapterTypeFactory<?>> customTypeFactories = new HashMap<>();

	public <T extends CustomType> CustomTypeEditor<T> getCustomTypeEditor(Class<T> typeClass) {
		return (CustomTypeEditor<T>) customTypeEditors.get(typeClass);
	}

	public <T extends CustomType> TechnologyAdapterTypeFactory<T> getCustomTypeFactory(Class<T> typeClass) {
		return (TechnologyAdapterTypeFactory<T>) customTypeFactories.get(typeClass);
	}

	private void updateChoices() {
		choices.clear();

		// First add the primitives
		for (PrimitiveType primitiveType : PrimitiveType.values()) {
			choices.add(primitiveType);
		}

		// Then the technology specific types
		if (getServiceManager() != null) {
			for (Class<? extends CustomType> customType : getServiceManager().getTechnologyAdapterService().getCustomTypeFactories()
					.keySet()) {
				// System.out.println("Found type " + customType);
				choices.add(customType);
				TechnologyAdapterTypeFactory<?> specificFactory = (TechnologyAdapterTypeFactory<?>) getServiceManager()
						.getTechnologyAdapterService().getCustomTypeFactories().get(customType);
				if (specificFactory != null) {
					// System.out.println("Found specific factory : " + specificFactory);
					customTypeFactories.put(customType, specificFactory);
				}
				// System.out.println("Factory " + specificFactory);
				if (getServiceManager() instanceof ApplicationContext) {
					CustomTypeEditor<?> specificEditor = ((ApplicationContext) getServiceManager()).getTechnologyAdapterControllerService()
							.getCustomTypeEditor(customType);
					if (specificEditor != null) {
						// System.out.println("Found specific editor : " + specificEditor);
						customTypeEditors.put(customType, specificEditor);
					}
				}
			}
		}

		// Then all java types
		choices.add(JAVA_TYPE);
		choices.add(JAVA_LIST);
		choices.add(JAVA_MAP);
		choices.add(JAVA_ARRAY);
		choices.add(JAVA_WILDCARD);
	}

	public List<Object> getChoices() {
		return choices;
	}

	public Object getChoice() {
		return choice;
	}

	public String getPresentationName(Object aChoice) {
		if (aChoice instanceof Class) {
			CustomTypeEditor<?> editor = getCustomTypeEditor((Class<? extends CustomType>) aChoice);
			if (editor != null) {
				return editor.getPresentationName();
			}
		}
		return aChoice.toString();
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public void setChoice(Object choice) {
		if (this.choice != choice) {
			Class<?> oldBaseClass = getBaseClass();
			TechnologyAdapterTypeFactory<?> oldCustomTypeFactory = getCurrentCustomTypeFactory();
			CustomTypeEditor<?> oldCustomTypeEditor = getCurrentCustomTypeEditor();

			/*boolean oldIsViewType = isViewType();
			boolean oldIsVirtualModelInstanceType = isVirtualModelInstanceType();
			boolean oldIsFlexoConceptInstanceType = isFlexoConceptInstanceType();*/

			boolean oldIsJavaType = isJavaType();
			boolean oldIsPrimitiveType = isPrimitiveType();
			boolean oldIsJavaList = isJavaList();
			boolean oldIsJavaMap = isJavaMap();
			boolean oldHasBaseJavaClass = hasBaseJavaClass();
			boolean oldIsJavaWildcard = isJavaWildcard();
			boolean oldIsCustomType = isCustomType();

			Object old = this.choice;

			if (old instanceof TechnologyAdapterTypeFactory) {
				((TechnologyAdapterTypeFactory<?>) old).getPropertyChangeSupport().removePropertyChangeListener(this);
			}

			this.choice = choice;

			if (isCustomType()) {
				TechnologyAdapterTypeFactory factory = getCurrentCustomTypeFactory();
				factory.getPropertyChangeSupport().addPropertyChangeListener(this);
				if (getEditedObject() instanceof CustomType) {
					try {
						factory.configureFactory((CustomType) getEditedObject());
					} catch (ClassCastException e) {
						// That may happen if we have changed from a CustomTypefactory to another CustomTypefactory
						setEditedObject(factory.makeCustomType(null));
					}
				}
				else {
					setEditedObject(factory.makeCustomType(null));
				}
			}
			else if (choice instanceof PrimitiveType) {
				setEditedObject(((PrimitiveType) choice).getType());
			}
			else {
				// Will cause the edited object to be recomputed from new configuration values
				setBaseClass(oldBaseClass);
			}
			getPropertyChangeSupport().firePropertyChange("choice", old, choice);
			/*getPropertyChangeSupport().firePropertyChange("isViewType", oldIsViewType, isViewType());
			getPropertyChangeSupport().firePropertyChange("isVirtualModelInstanceType", oldIsVirtualModelInstanceType,
					isVirtualModelInstanceType());
			getPropertyChangeSupport().firePropertyChange("isFlexoConceptInstanceType", oldIsFlexoConceptInstanceType,
					isFlexoConceptInstanceType());*/
			getPropertyChangeSupport().firePropertyChange("isJavaType", oldIsJavaType, isJavaType());
			getPropertyChangeSupport().firePropertyChange("isPrimitiveType", oldIsPrimitiveType, isPrimitiveType());
			getPropertyChangeSupport().firePropertyChange("isJavaList", oldIsJavaList, isJavaList());
			getPropertyChangeSupport().firePropertyChange("isJavaMap", oldIsJavaMap, isJavaMap());
			getPropertyChangeSupport().firePropertyChange("hasBaseJavaClass", oldHasBaseJavaClass, hasBaseJavaClass());
			getPropertyChangeSupport().firePropertyChange("isJavaWildcard", oldIsJavaWildcard, isJavaWildcard());
			getPropertyChangeSupport().firePropertyChange("isCustomType", oldIsCustomType, isCustomType());
			getPropertyChangeSupport().firePropertyChange("currentCustomTypeFactory", oldCustomTypeFactory, getCurrentCustomTypeFactory());
			getPropertyChangeSupport().firePropertyChange("currentCustomTypeEditor", oldCustomTypeEditor, getCurrentCustomTypeEditor());
			if (isJavaType()) {
				getPropertyChangeSupport().firePropertyChange("loadedClassesInfo", null, getLoadedClassesInfo());
			}
		}
	}

	public boolean isJavaType() {
		return (choice instanceof PrimitiveType || choice == JAVA_TYPE || choice == JAVA_LIST || choice == JAVA_MAP || choice == JAVA_ARRAY
				|| choice == JAVA_WILDCARD);
	}

	public boolean isPrimitiveType() {
		return getPrimitiveType() != null;
	}

	public boolean isJavaList() {
		return (choice == JAVA_LIST);
	}

	public boolean isJavaMap() {
		return (choice == JAVA_MAP);
	}

	public boolean isJavaWildcard() {
		return (choice == JAVA_WILDCARD);
	}

	public boolean isCustomType() {
		return (choice instanceof Class);
	}

	public TechnologyAdapterTypeFactory<?> getCurrentCustomTypeFactory() {
		if (isCustomType()) {
			return customTypeFactories.get(choice);
		}
		return null;
	}

	public CustomTypeEditor<?> getCurrentCustomTypeEditor() {
		if (isCustomType()) {
			return customTypeEditors.get(choice);
		}
		return null;
	}

	public boolean hasBaseJavaClass() {
		return isJavaType() && (choice != JAVA_WILDCARD);
	}

	public boolean hasGenericParameters() {
		return genericParameters.size() > 0;
	}

	public Type getKeyType() {
		return keyType;
	}

	public void setKeyType(Type keyType) {
		if (!this.keyType.equals(keyType)) {
			this.keyType = keyType;
			getPropertyChangeSupport().firePropertyChange("keyType", null, keyType);
			updateEditedType();
		}
	}

	public String getKeyTypeStringRepresentation() {
		return TypeUtils.simpleRepresentation(getKeyType());
	}

	public PrimitiveType getPrimitiveType() {
		for (PrimitiveType primitiveType : PrimitiveType.values()) {
			if (primitiveType.getType().equals(getEditedObject())) {
				return primitiveType;
			}
		}
		return null;
	}

	public FlexoServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(FlexoServiceManager serviceManager) {

		if (serviceManager != this.serviceManager) {
			FlexoServiceManager oldValue = this.serviceManager;
			this.serviceManager = serviceManager;
			updateChoices();
			getPropertyChangeSupport().firePropertyChange("serviceManager", oldValue, serviceManager);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("propertyChange with " + evt);
		if (isCustomType() && evt.getSource() == getCurrentCustomTypeFactory()) {
			// propertyChanged in CustomTypeFactory, regenerate new Type
			setEditedObject(getCurrentCustomTypeFactory().makeCustomType(null));
		}
	}

	/*private void updateCustomTypes() {
		for (Object o : new ArrayList<Object>(choices)) {
			if (o instanceof CustomTypeFactory) {
				choices.remove(o);
			}
		}
	
		for (Class<? extends CustomType> customTypeClass : serviceManager.getTechnologyAdapterService().getCustomTypeFactories().keySet()) {
			CustomTypeFactory<?> ctfactory = serviceManager.getTechnologyAdapterService().getCustomTypeFactories().get(customTypeClass);
			choices.add(ctfactory);
		}
	
		getPropertyChangeSupport().firePropertyChange("choices", null, choices);
	}*/

	@Override
	public void setEditedObject(Type object) {
		super.setEditedObject(object);
	}

	@Override
	public void fireEditedObjectChanged() {

		super.fireEditedObjectChanged();

		Class<?> baseClass = getBaseClass();

		// First try to find the type of object
		PrimitiveType primitiveType = getPrimitiveType();
		if (primitiveType != null) {
			setChoice(primitiveType);
		}
		else {
			if (getEditedObject() instanceof CustomType && serviceManager != null) {
				CustomTypeFactory ctFactory = serviceManager.getTechnologyAdapterService().getCustomTypeFactories()
						.get(getEditedObject().getClass());
				if (ctFactory != null && choices.contains(ctFactory.getCustomType())) {
					setChoice(ctFactory.getCustomType());
					ctFactory.configureFactory((CustomType) getEditedObject());
				}
			}
			else if (getEditedObject() instanceof Class) {
				setChoice(JAVA_TYPE);
			}
		}
		if (isJavaType()) {
			updateGenericParameters(baseClass);
			getLoadedClassesInfo().setSelectedClassInfo(LoadedClassesInfo.getClass(baseClass));
			getPropertyChangeSupport().firePropertyChange("loadedClassesInfo", null, getLoadedClassesInfo());
		}

	}

	public List<GenericParameter> getGenericParameters() {
		return genericParameters;
	}

	public List<GenericBound> getUpperBounds() {
		return upperBounds;
	}

	public GenericBound createUpperBound() {
		GenericBound returned = new GenericBound(Object.class);
		upperBounds.add(returned);
		getPropertyChangeSupport().firePropertyChange("upperBounds", null, returned);
		updateWildcardType();
		return returned;
	}

	public void deleteUpperBound(GenericBound bound) {
		bound.delete();
		upperBounds.remove(bound);
		getPropertyChangeSupport().firePropertyChange("upperBounds", bound, null);
		updateWildcardType();
	}

	public List<GenericBound> getLowerBounds() {
		return lowerBounds;
	}

	public GenericBound createLowerBound() {
		GenericBound returned = new GenericBound(Object.class);
		lowerBounds.add(returned);
		getPropertyChangeSupport().firePropertyChange("lowerBounds", null, returned);
		updateWildcardType();
		return returned;
	}

	public void deleteLowerBound(GenericBound bound) {
		bound.delete();
		lowerBounds.remove(bound);
		getPropertyChangeSupport().firePropertyChange("lowerBounds", bound, null);
		updateWildcardType();
	}

	private void updateGenericParameters(Class<?> baseClass) {
		if (baseClass == null || baseClass.getTypeParameters().length == 0) {
			genericParameters.clear();
		}
		else {
			List<GenericParameter> genericParametersToRemove = new ArrayList<GenericParameter>(genericParameters);
			for (TypeVariable<?> tv : baseClass.getTypeParameters()) {
				GenericParameter foundGenericParameter = null;
				for (GenericParameter gp : genericParameters) {
					if (gp.getTypeVariable() == tv) {
						foundGenericParameter = gp;
						genericParametersToRemove.remove(foundGenericParameter);
						break;
					}
				}
				if (foundGenericParameter == null) {
					genericParameters.add(new GenericParameter(tv));
				}
			}
			for (GenericParameter gpToRemove : genericParametersToRemove) {
				genericParameters.remove(gpToRemove);
			}
		}

		getPropertyChangeSupport().firePropertyChange("genericParameters", null, genericParameters);
		getPropertyChangeSupport().firePropertyChange("hasGenericParameters", false, true);
	}

	public Class<?> getBaseClass() {
		if (choice == JAVA_LIST) {
			if (getEditedObject() instanceof ParameterizedType
					&& ((ParameterizedType) getEditedObject()).getActualTypeArguments().length > 0) {
				return TypeUtils.getBaseClass(((ParameterizedType) getEditedObject()).getActualTypeArguments()[0]);
			}
			return Object.class;
		}
		if (choice == JAVA_MAP) {
			if (getEditedObject() instanceof ParameterizedType
					&& ((ParameterizedType) getEditedObject()).getActualTypeArguments().length > 1) {
				return TypeUtils.getBaseClass(((ParameterizedType) getEditedObject()).getActualTypeArguments()[1]);
			}
			return Object.class;
		}
		return TypeUtils.getBaseClass(getEditedObject());
	}

	private ParameterizedType makeParameterizedType(Class<?> baseClass) {
		Type[] params = new Type[genericParameters.size()];
		for (int i = 0; i < genericParameters.size(); i++) {
			params[i] = genericParameters.get(i).getType();
		}
		return new ParameterizedTypeImpl(baseClass, params);
	}

	public void setBaseClass(Class<?> baseClass) {

		if (baseClass != getBaseClass()) {

			if (choice == JAVA_LIST) {
				if (hasGenericParameters()) {
					setEditedObject(new ParameterizedTypeImpl((List.class), makeParameterizedType(baseClass)));
				}
				else {
					setEditedObject(new ParameterizedTypeImpl((List.class), baseClass));
				}
			}
			else if (choice == JAVA_MAP) {
				if (hasGenericParameters()) {
					setEditedObject(new ParameterizedTypeImpl((Map.class), new Type[] { getKeyType(), makeParameterizedType(baseClass) }));
				}
				else {
					setEditedObject(new ParameterizedTypeImpl((Map.class), new Type[] { getKeyType(), baseClass }));
				}
			}
			else if (choice == JAVA_ARRAY) {
				if (hasGenericParameters()) {
					setEditedObject(new GenericArrayTypeImpl(makeParameterizedType(baseClass)));
				}
				else {
					setEditedObject(new GenericArrayTypeImpl(baseClass));
				}
			}
			else if (choice == JAVA_WILDCARD) {
				updateWildcardType();
			}
			else {
				if (hasGenericParameters()) {
					setEditedObject(makeParameterizedType(baseClass));
				}
				else {
					setEditedObject(baseClass);
				}
			}
		}
	}

	private void updateEditedType() {
		setBaseClass(getBaseClass());
	}

	private void updateWildcardType() {
		Type[] upper = new Type[upperBounds.size()];
		for (int i = 0; i < upperBounds.size(); i++)
			upper[i] = upperBounds.get(i).getType();
		Type[] lower = new Type[lowerBounds.size()];
		for (int i = 0; i < lowerBounds.size(); i++)
			lower[i] = lowerBounds.get(i).getType();
		setEditedObject(new WilcardTypeImpl(upper, lower));
	}

	// private boolean isListeningLoadedClassesInfo = false;

	private LoadedClassesInfo loadedClassesInfo = null;

	public LoadedClassesInfo getLoadedClassesInfo() {

		if (loadedClassesInfo == null) {
			loadedClassesInfo = new LoadedClassesInfo(getBaseClass());
			loadedClassesInfo.getPropertyChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("selectedClassInfo")) {
						if (isJavaType()) {
							ClassInfo classInfo = (ClassInfo) evt.getNewValue();
							if (classInfo != null) {
								setBaseClass(classInfo.getClazz());
							}
						}
					}
				}

			});
		}
		return loadedClassesInfo;
	}

	@Override
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
			_selectorPanel = null;
		}

	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(Type oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
		if (oldValue != null) {
			_revertValue = oldValue;
		}
		else {
			_revertValue = null;
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public Type getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(Type editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected TypeSelectorDetailsPanel makeCustomPanel(Type editedObject) {
		return new TypeSelectorDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Type editedObject) {
		// logger.info("updateCustomPanel with "+editedObject+" _selectorPanel="+_selectorPanel);
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
	}

	public class TypeSelectorDetailsPanel extends ResizablePanel {
		private final FIBComponent fibComponent;
		private JFIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected TypeSelectorDetailsPanel(Type aClass) {
			super();

			fibComponent = ApplicationFIBLibraryImpl.instance().retrieveFIBComponent(FIB_FILE_NAME, true);
			controller = new CustomFIBController(fibComponent);
			fibView = (JFIBView<?, ?>) controller.buildView(fibComponent, true);

			controller.setDataObject(TypeSelector.this);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void delete() {
			controller.delete();
			fibView.delete();
			controller = null;
			fibView = null;
		}

		public CustomFIBController getController() {
			return controller;
		}

		public void update() {
			controller.setDataObject(TypeSelector.this);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public class CustomFIBController extends FlexoFIBController {
			public CustomFIBController(FIBComponent component) {
				super(component, SwingViewFactory.INSTANCE);
			}

			public void apply() {
				// setEditedObject(LoadedClassesInfo.instance().getSelectedClassInfo().getRepresentedClass());
				TypeSelector.this.apply();
			}

			public void cancel() {
				TypeSelector.this.cancel();
			}

			public void reset() {
				setEditedObject(null);
				TypeSelector.this.apply();
			}

			public void classChanged() {
				// System.out.println("Class changed !!!");
			}

		}

	}

	@Override
	public void apply() {

		if (isCustomType()) {
			System.out.println("On fait apply dans TypeSelector");
			Type newType = getCurrentCustomTypeEditor().getEditedType();
			System.out.println("newType=" + newType);
			setEditedObject(newType);
		}

		setRevertValue(getEditedObject());
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	public TypeSelectorDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	public Class<Type> getRepresentedType() {
		return Type.class;
	}

	@Override
	public String renderedString(Type editedObject) {
		if (editedObject == null) {
			return "";
		}
		return TypeUtils.simpleRepresentation(editedObject);
	}

	public Resource getJavaClassEditorComponentResource() {
		return JAVA_CLASS_EDITOR_FIB_FILE_NAME;
	}

	/**
	 * This main allows to launch an application testing the TypeSelector
	 * 
	 * @param args
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SecurityException, IOException {

		Resource loggingFile = ResourceLocator.locateResource("Config/logging_INFO.properties");
		FlexoLoggingManager.initialize(-1, true, loggingFile, Level.INFO, null);
		final JDialog dialog = new JDialog((Frame) null, false);

		final TypeSelector selector = new TypeSelector(String.class);
		selector.setRevertValue(Object.class);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selector.delete();
				dialog.dispose();
				System.exit(0);
			}
		});

		JButton logButton = new JButton("Logs");
		logButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FlexoLoggingViewer.showLoggingViewer(FlexoLoggingManager.instance(), ApplicationFIBLibraryImpl.instance(), dialog);
			}
		});

		JPanel panel = new JPanel(new VerticalLayout());
		panel.add(selector);

		panel.add(closeButton);
		panel.add(logButton);

		dialog.setPreferredSize(new Dimension(550, 600));
		dialog.getContentPane().add(panel);
		dialog.pack();

		dialog.setVisible(true);
	}
}
