/**
 * 
 * Copyright (c) 2013-2015, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexo-ui, a component of the software infrastructure 
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

package org.openflexo.view.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.ApplicationContext;
import org.openflexo.components.widget.FIBTechnologyBrowser;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.CheckboxParameter;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConceptInstanceParameter;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.FlexoResourceParameter;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.foundation.fml.GenericBehaviourParameter;
import org.openflexo.foundation.fml.IntegerParameter;
import org.openflexo.foundation.fml.ListParameter;
import org.openflexo.foundation.fml.TextAreaParameter;
import org.openflexo.foundation.fml.TextFieldParameter;
import org.openflexo.foundation.fml.ViewType;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.foundation.fml.binding.FlexoConceptBindingFactory;
import org.openflexo.foundation.fml.controlgraph.ConditionalAction;
import org.openflexo.foundation.fml.controlgraph.IncrementalIterationAction;
import org.openflexo.foundation.fml.controlgraph.IterationAction;
import org.openflexo.foundation.fml.controlgraph.WhileAction;
import org.openflexo.foundation.fml.editionaction.AddToListAction;
import org.openflexo.foundation.fml.editionaction.DeleteAction;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.fml.editionaction.LogAction;
import org.openflexo.foundation.fml.editionaction.NotifyPropertyChangedAction;
import org.openflexo.foundation.fml.editionaction.RemoveFromListAction;
import org.openflexo.foundation.fml.inspector.CheckboxInspectorEntry;
import org.openflexo.foundation.fml.inspector.InspectorEntry;
import org.openflexo.foundation.fml.inspector.IntegerInspectorEntry;
import org.openflexo.foundation.fml.inspector.TextAreaInspectorEntry;
import org.openflexo.foundation.fml.inspector.TextFieldInspectorEntry;
import org.openflexo.foundation.fml.rt.action.FlexoBehaviourAction;
import org.openflexo.foundation.fml.rt.editionaction.AddFlexoConceptInstance;
import org.openflexo.foundation.fml.rt.editionaction.AddSubView;
import org.openflexo.foundation.fml.rt.editionaction.AddVirtualModelInstance;
import org.openflexo.foundation.fml.rt.editionaction.MatchFlexoConceptInstance;
import org.openflexo.foundation.fml.rt.editionaction.SelectFlexoConceptInstance;
import org.openflexo.foundation.nature.ProjectNature;
import org.openflexo.foundation.nature.ProjectNatureService;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.technologyadapter.TechnologyObject;
import org.openflexo.gina.model.FIBComponent;
import org.openflexo.gina.model.FIBComponent.HorizontalScrollBarPolicy;
import org.openflexo.gina.model.FIBComponent.VerticalScrollBarPolicy;
import org.openflexo.gina.model.FIBModelFactory;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.container.FIBPanel;
import org.openflexo.gina.model.container.FIBTab;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints;
import org.openflexo.gina.model.container.layout.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.gina.model.widget.FIBCheckBox;
import org.openflexo.gina.model.widget.FIBCheckboxList;
import org.openflexo.gina.model.widget.FIBCustom;
import org.openflexo.gina.model.widget.FIBLabel;
import org.openflexo.gina.model.widget.FIBNumber;
import org.openflexo.gina.model.widget.FIBNumber.NumberType;
import org.openflexo.gina.model.widget.FIBTextArea;
import org.openflexo.gina.model.widget.FIBTextField;
import org.openflexo.gina.swing.utils.CustomTypeEditor;
import org.openflexo.gina.utils.InspectorGroup;
import org.openflexo.icon.FMLIconLibrary;
import org.openflexo.icon.FMLRTIconLibrary;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This class represents a technology-specific controller provided by a {@link TechnologyAdapter}<br>
 * A {@link TechnologyAdapterController} works above conceptual layer provided by a {@link TechnologyAdapter}, and manages all tooling
 * dedicated to technology-specific management of a {@link TechnologyAdapter}<br>
 * This controller makes the bindings between Openflexo controllers/editors layer and the {@link TechnologyAdapter}
 * 
 * @author sylvain
 * 
 */
public abstract class TechnologyAdapterController<TA extends TechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(TechnologyAdapterController.class.getPackage().getName());

	private TechnologyAdapterControllerService technologyAdapterControllerService;

	private Map<Class<? extends CustomType>, CustomTypeEditor> customTypeEditors = new HashMap<>();

	/**
	 * Returns applicable {@link ProjectNatureService}
	 * 
	 * @return
	 */
	public TechnologyAdapterControllerService getTechnologyAdapterControllerService() {
		return technologyAdapterControllerService;
	}

	/**
	 * Sets applicable {@link ProjectNatureService}
	 * 
	 * @param technologyAdapterService
	 */
	public void setTechnologyAdapterService(TechnologyAdapterControllerService technologyAdapterControllerService) {
		this.technologyAdapterControllerService = technologyAdapterControllerService;
	}

	/**
	 * Return TechnologyAdapter
	 * 
	 * @return
	 */
	public final TA getTechnologyAdapter() {
		return technologyAdapterControllerService.getServiceManager().getService(TechnologyAdapterService.class)
				.getTechnologyAdapter(getTechnologyAdapterClass());
	}

	/**
	 * Return TechnologyAdapter class
	 * 
	 * @return
	 */
	public abstract Class<TA> getTechnologyAdapterClass();

	/**
	 * Return the locales relative to this technology
	 * 
	 * @return
	 */
	public LocalizedDelegate getLocales() {
		return getTechnologyAdapter().getLocales();
	}

	/**
	 * Called to activate the {@link TechnologyAdapterController} We do it for all loaded modules. This means that all features and GUIs
	 * available with this technology adapter will be made available to module<br>
	 * 
	 * From a technical point of view, we first initialize inspectors and then actions
	 */
	public void activate() {
		if (getServiceManager() != null) {
			ModuleLoader moduleLoader = getServiceManager().getModuleLoader();
			if (moduleLoader != null) {
				for (FlexoModule<?> module : moduleLoader.getLoadedModuleInstances()) {
					activate(module);
				}
			}
			// Here we iterate on all technology browsers that have been built for this TechnologyAdapter
			// We just have initialized some new actions, that have to be reflected in already existing browsers
			for (FIBTechnologyBrowser<TA> b : technologyBrowsers) {
				b.initializeFIBComponent();
			}

			// initTechnologySpecificTypeEditors(getServiceManager().getTechnologyAdapterService());
		}
		isActivated = true;
	}

	/**
	 * Called to activate the {@link TechnologyAdapter}
	 */
	public void disactivate() {
		isActivated = false;
	}

	/**
	 * Called to activate the {@link TechnologyAdapterController} We do it for all loaded modules. This means that all features and GUIs
	 * available with this technology adapter will be made available to module<br>
	 * 
	 * From a technical point of view, we first initialize inspectors and then actions
	 */
	public void activate(FlexoModule<?> module) {
		if (module.getFlexoController() != null) {
			initializeInspectors(module.getFlexoController());
			initializeActions(module.getFlexoController().getControllerActionInitializer());
		}
	}

	/**
	 * Called to activate the {@link TechnologyAdapter}
	 */
	public void disactivate(FlexoModule<?> module) {
	}

	private boolean isActivated = false;

	public boolean isActivated() {
		return isActivated;
	}

	/**
	 * Initialize actions for supplied module using supplied {@link ControllerActionInitializer}
	 * 
	 * @param actionInitializer
	 */
	protected abstract void initializeActions(ControllerActionInitializer actionInitializer);

	/**
	 * Initialize inspectors for supplied module using supplied {@link FlexoController}
	 * 
	 * @param controller
	 */
	protected abstract void initializeInspectors(FlexoController controller);

	/**
	 * Return inspector group for this technology
	 * 
	 * @return
	 */
	public abstract InspectorGroup getTechnologyAdapterInspectorGroup();

	/**
	 * Return inspector group for FML technology
	 * 
	 * @return
	 */
	public InspectorGroup getFMLTechnologyAdapterInspectorGroup() {
		for (TechnologyAdapterController<?> tac : getTechnologyAdapterControllerService().getLoadedAdapterControllers()) {
			if (tac.getTechnologyAdapter() instanceof FMLTechnologyAdapter) {
				return tac.getTechnologyAdapterInspectorGroup();
			}
		}
		return null;
	}

	public ApplicationContext getServiceManager() {
		return (ApplicationContext) getTechnologyAdapter().getTechnologyAdapterService().getServiceManager();
	}

	/**
	 * Return icon representing underlying technology, required size is 32x32
	 * 
	 * @return
	 */
	public abstract ImageIcon getTechnologyBigIcon();

	/**
	 * Return icon representing underlying technology, required size is 16x16
	 * 
	 * @return
	 */
	public abstract ImageIcon getTechnologyIcon();

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getModelIcon();

	/**
	 * Return icon representing a model of underlying technology
	 * 
	 * @return
	 */
	public abstract ImageIcon getMetaModelIcon();

	/**
	 * Return icon representing supplied {@link TechnologyObject}
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForTechnologyObject(TechnologyObject<?> object) {
		if (object != null) {
			return getIconForTechnologyObject((Class) object.getClass());
		}
		return null;
	}

	/**
	 * Return icon representing supplied {@link TechnologyObject} class
	 * 
	 * @param object
	 * @return
	 */
	public abstract ImageIcon getIconForTechnologyObject(Class<? extends TechnologyObject<?>> objectClass);

	/**
	 * Return icon representing supplied model slot class
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForModelSlot(Class<? extends ModelSlot<?>> modelSlotClass) {
		return getTechnologyIcon();
	}

	/**
	 * Return icon representing supplied flexo role class
	 * 
	 * @param object
	 * @return
	 */
	public abstract ImageIcon getIconForFlexoRole(Class<? extends FlexoRole<?>> flexoRoleClass);

	/**
	 * Return icon representing supplied edition action
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForEditionAction(Class<? extends EditionAction> editionActionClass) {

		if (AddFlexoConceptInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.FLEXO_CONCEPT_INSTANCE_ICON, IconLibrary.DUPLICATE);
		}
		else if (AddVirtualModelInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.VIRTUAL_MODEL_INSTANCE_ICON, IconLibrary.DUPLICATE);
		}
		else if (AddSubView.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.VIEW_ICON, IconLibrary.DUPLICATE);
		}
		else if (SelectFlexoConceptInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.FLEXO_CONCEPT_INSTANCE_ICON, IconLibrary.IMPORT);
		}
		else if (MatchFlexoConceptInstance.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLRTIconLibrary.FLEXO_CONCEPT_INSTANCE_ICON, IconLibrary.SYNC);
		}
		else if (AddToListAction.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLIconLibrary.LIST_ICON, IconLibrary.POSITIVE_MARKER);
		}
		else if (RemoveFromListAction.class.isAssignableFrom(editionActionClass)) {
			return IconFactory.getImageIcon(FMLIconLibrary.LIST_ICON, IconLibrary.NEGATIVE_MARKER);
		}
		else if (DeleteAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.DELETE_ICON;
		}
		else if (ConditionalAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.CONDITIONAL_ACTION_ICON;
		}
		else if (IterationAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.ITERATION_ACTION_ICON;
		}
		else if (WhileAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.ITERATION_ACTION_ICON;
		}
		else if (IncrementalIterationAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.ITERATION_ACTION_ICON;
		}
		else if (ExpressionAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.EXPRESSION_ACTION_ICON;
		}
		else if (LogAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.LOG_ACTION_ICON;
		}
		else if (NotifyPropertyChangedAction.class.isAssignableFrom(editionActionClass)) {
			return FMLIconLibrary.NOTIFY_PROPERTY_CHANGED_ACTION_ICON;
		}
		return null;

	}

	/**
	 * Return icon representing supplied edition scheme
	 * 
	 * @param object
	 * @return
	 */
	public ImageIcon getIconForFlexoBehaviour(Class<? extends FlexoBehaviour> flexoBehaviourClass) {
		return null;
	}

	public abstract String getWindowTitleforObject(TechnologyObject<TA> object, FlexoController controller);

	/**
	 * Return boolean indicating if this TechnologyAdapter controller service support ModuleView rendering for supplied technology object
	 * 
	 * @param object
	 * @return
	 */
	public abstract boolean hasModuleViewForObject(TechnologyObject<TA> object, FlexoController controller);

	/**
	 * Return a newly created ModuleView for supplied technology object, if this TechnologyAdapter controller service support ModuleView
	 * rendering
	 * 
	 * @param object
	 * @return
	 */
	public abstract ModuleView<?> createModuleViewForObject(TechnologyObject<TA> object, FlexoController controller,
			FlexoPerspective perspective);

	public Resource getFIBPanelForObject(Object anObject) {
		if (anObject != null) {
			return getFIBPanelForClass(anObject.getClass());
		}
		return null;
	}

	private final Map<Class<?>, Resource> fibPanelsForClasses = new HashMap<Class<?>, Resource>() {
		@Override
		public Resource get(Object key) {
			if (containsKey(key)) {
				return super.get(key);
			}
			if (key instanceof Class) {
				Class<?> aClass = (Class<?>) key;
				// System.out.println("Searching FIBPanel for " + aClass);
				if (aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class) != null) {
					// System.out.println("Found annotation " + aClass.getAnnotation(FIBPanel.class));
					String fibPanelName = aClass.getAnnotation(org.openflexo.gina.annotation.FIBPanel.class).value();
					// System.out.println("fibPanelFile=" + fibPanel);
					Resource fibLocation = ResourceLocator.locateResource(fibPanelName);
					if (fibLocation != null) {
						// logger.info("Found " + fibPanel);
						put(aClass, fibLocation);
						return fibLocation;
					}
				}
				put(aClass, null);
				return null;
			}
			return null;
		}
	};

	public Resource getFIBPanelForClass(Class<?> aClass) {
		return TypeUtils.objectForClass(aClass, fibPanelsForClasses);
	}

	// ***************************************************************
	// Management of FlexoProject natures
	// ***************************************************************

	public final boolean hasSpecificFlexoProjectNature(FlexoProject project) {
		return getSpecificProjectNatures(project).size() > 0;
	}

	// Override when required
	public List<? extends ProjectNature> getSpecificProjectNatures(FlexoProject project) {
		return Collections.emptyList();
	}

	// Override when required
	public ModuleView<FlexoProject> createFlexoProjectModuleViewForSpecificNature(FlexoProject project, ProjectNature nature,
			FlexoController controller, FlexoPerspective perspective) {
		return null;
	}

	private final Map<FlexoController, TechnologyPerspective<TA>> technologyPerspectives = new HashMap<FlexoController, TechnologyPerspective<TA>>();

	public Map<FlexoController, TechnologyPerspective<TA>> getTechnologyPerspectives() {
		return technologyPerspectives;
	}

	public TechnologyPerspective<TA> getTechnologyPerspective(FlexoController controller) {
		TechnologyPerspective<TA> returned = technologyPerspectives.get(controller);
		if (returned == null) {
			returned = new TechnologyPerspective<TA>(getTechnologyAdapter(), controller);
			technologyPerspectives.put(controller, returned);
		}
		return returned;
	}

	public void installTechnologyPerspective(FlexoController controller) {
		controller.addToPerspectives(getTechnologyPerspective(controller));
	}

	/**
	 * Install specific perspectives for FML@Runtime model<br>
	 * Override this method when required
	 * 
	 * @param controller
	 */
	public void installFMLNatureSpecificPerspectives(FlexoController controller) {
	}

	/**
	 * Install specific perspectives for FML@Runtime model<br>
	 * Override this method when required
	 * 
	 * @param controller
	 */
	public void installFMLRTNatureSpecificPerspectives(FlexoController controller) {
	}

	/**
	 * Internally stores all technology browsers that have been built by this {@link TechnologyAdapterController}
	 */
	private final List<FIBTechnologyBrowser<TA>> technologyBrowsers = new ArrayList<FIBTechnologyBrowser<TA>>();

	/**
	 * Make technology browser
	 * 
	 * @param controller
	 * @return
	 */
	public final FIBTechnologyBrowser<TA> makeTechnologyBrowser(FlexoController controller) {
		FIBTechnologyBrowser<TA> returned = buildTechnologyBrowser(controller);
		technologyBrowsers.add(returned);
		return returned;
	}

	/**
	 * Override when required
	 * 
	 * @param controller
	 * @return
	 */
	protected FIBTechnologyBrowser<TA> buildTechnologyBrowser(FlexoController controller) {
		return new FIBTechnologyBrowser<TA>(getTechnologyAdapter(), controller, getTechnologyAdapter().getLocales());
	}

	/**
	 * Factory method used to instanciate a technology-specific FIBWidget for a given {@link InspectorEntry}<br>
	 * Provides a hook to specialize this method in a given technology
	 * 
	 * @param entry
	 * @param newTab
	 * @param fibModelFactory
	 * @return
	 */
	public FIBWidget makeWidget(final InspectorEntry entry, FIBTab newTab, FIBModelFactory fibModelFactory) {
		if (entry instanceof TextFieldInspectorEntry) {
			FIBTextField tf = fibModelFactory.newFIBTextField();
			tf.setValidateOnReturn(true); // Avoid too many ontologies manipulations
			newTab.addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return tf;
		}
		else if (entry instanceof TextAreaInspectorEntry) {
			FIBTextArea ta = fibModelFactory.newFIBTextArea();
			ta.setValidateOnReturn(true); // Avoid to many ontologies manipulations
			ta.setUseScrollBar(true);
			ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
			newTab.addToSubComponents(ta, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true));
			return ta;
		}
		else if (entry instanceof CheckboxInspectorEntry) {
			FIBCheckBox cb = fibModelFactory.newFIBCheckBox();
			newTab.addToSubComponents(cb, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return cb;
		}
		else if (entry instanceof IntegerInspectorEntry) {
			FIBNumber number = fibModelFactory.newFIBNumber();
			number.setNumberType(NumberType.IntegerType);
			newTab.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return number;
		}

		return null;
	}

	private FIBComponent makeFlexoConceptInstanceSelector(final FlexoBehaviourParameter parameter, FIBPanel panel, int index,
			FlexoBehaviourAction<?, ?, ?> action, FIBModelFactory fibModelFactory) {
		FIBCustom fciSelector = fibModelFactory.newFIBCustom();
		fciSelector.setBindingFactory(parameter.getBindingFactory());
		Class fciSelectorClass;
		try {
			fciSelectorClass = Class.forName("org.openflexo.fml.rt.controller.widget.FIBFlexoConceptInstanceSelector");
			fciSelector.setComponentClass(fciSelectorClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fciSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(fciSelector, new DataBinding<Object>("component.project"),
				new DataBinding<Object>("controller.editor.project"), true));

		String containerBinding = "";
		if (parameter.getFlexoBehaviour() instanceof CreationScheme) {
			containerBinding = "data." + FlexoConceptBindingFactory.VIRTUAL_MODEL_INSTANCE + "." + parameter.getContainer().toString();
		}
		else {
			containerBinding = "data." + FlexoConceptBindingFactory.FLEXO_CONCEPT_INSTANCE + "." + parameter.getContainer().toString();
		}

		if (parameter.getContainer().isSet() && parameter.getContainer().isValid()) {

			Type containerType = parameter.getContainer().getAnalyzedType();

			if (containerType instanceof ViewType) {
				fciSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(fciSelector, new DataBinding<Object>("component.view"),
						new DataBinding<Object>(containerBinding), true));
			}
			else if (containerType instanceof VirtualModelInstanceType) {
				fciSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(fciSelector,
						new DataBinding<Object>("component.virtualModelInstance"), new DataBinding<Object>(containerBinding), true));
			}
			else if (TypeUtils.isTypeAssignableFrom(FlexoResourceCenter.class, containerType)) {
				fciSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(fciSelector,
						new DataBinding<Object>("component.resourceCenter"), new DataBinding<Object>(containerBinding), true));
			}
		}
		else {

			// No container defined, set service manager
			fciSelector.addToAssignments(
					fibModelFactory.newFIBCustomAssignment(fciSelector, new DataBinding<Object>("component.serviceManager"),
							new DataBinding<Object>("controller.flexoController.applicationContext"), true));
		}

		fciSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(fciSelector, new DataBinding<Object>("component.expectedType"),
				new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".type"), true));
		return registerWidget(fciSelector, parameter, panel, index);

	}

	private FIBComponent makeVirtualModelInstanceSelector(final FlexoBehaviourParameter parameter, FIBPanel panel, int index,
			FlexoBehaviourAction<?, ?, ?> action, FIBModelFactory fibModelFactory) {
		FIBCustom vmiSelector = fibModelFactory.newFIBCustom();
		vmiSelector.setBindingFactory(parameter.getBindingFactory());
		Class fciSelectorClass;
		try {
			fciSelectorClass = Class.forName("org.openflexo.fml.rt.controller.widget.FIBVirtualModelInstanceSelector");
			vmiSelector.setComponentClass(fciSelectorClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		vmiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(vmiSelector, new DataBinding<Object>("component.project"),
				new DataBinding<Object>("controller.editor.project"), true));
		String containerBinding = "";
		if (parameter.getFlexoBehaviour() instanceof CreationScheme) {
			containerBinding = "data." + FlexoConceptBindingFactory.VIRTUAL_MODEL_INSTANCE + "." + parameter.getContainer().toString();
		}
		else {
			containerBinding = "data." + FlexoConceptBindingFactory.FLEXO_CONCEPT_INSTANCE + "." + parameter.getContainer().toString();
		}

		if (parameter.getContainer().isSet() && parameter.getContainer().isValid()) {

			Type containerType = parameter.getContainer().getAnalyzedType();

			if (containerType instanceof ViewType) {
				vmiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(vmiSelector, new DataBinding<Object>("component.view"),
						new DataBinding<Object>(containerBinding), true));
			}
			else if (TypeUtils.isTypeAssignableFrom(FlexoResourceCenter.class, containerType)) {
				vmiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(vmiSelector,
						new DataBinding<Object>("component.resourceCenter"), new DataBinding<Object>(containerBinding), true));
			}
		}
		else {
			// No container defined, set service manager
			vmiSelector.addToAssignments(
					fibModelFactory.newFIBCustomAssignment(vmiSelector, new DataBinding<Object>("component.serviceManager"),
							new DataBinding<Object>("controller.flexoController.applicationContext"), true));
		}

		// vmiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(vmiSelector, new DataBinding<Object>("component.view"),
		// new DataBinding<Object>(containerBinding), true));

		vmiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(vmiSelector, new DataBinding<Object>("component.expectedType"),
				new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".type"), true));
		return registerWidget(vmiSelector, parameter, panel, index);
	}

	private FIBComponent makeViewSelector(final FlexoBehaviourParameter parameter, FIBPanel panel, int index,
			FlexoBehaviourAction<?, ?, ?> action, FIBModelFactory fibModelFactory) {
		FIBCustom viewSelector = fibModelFactory.newFIBCustom();
		viewSelector.setBindingFactory(parameter.getBindingFactory());
		Class fciSelectorClass;
		try {
			fciSelectorClass = Class.forName("org.openflexo.fml.rt.controller.widget.FIBViewSelector");
			viewSelector.setComponentClass(fciSelectorClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(viewSelector, new DataBinding<Object>("component.project"),
				new DataBinding<Object>("controller.editor.project"), true));

		String containerBinding = "";
		if (parameter.getFlexoBehaviour() instanceof CreationScheme) {
			containerBinding = "data." + FlexoConceptBindingFactory.VIRTUAL_MODEL_INSTANCE + "." + parameter.getContainer().toString();
		}
		else {
			containerBinding = "data." + FlexoConceptBindingFactory.FLEXO_CONCEPT_INSTANCE + "." + parameter.getContainer().toString();
		}

		if (parameter.getContainer().isSet() && parameter.getContainer().isValid()) {

			Type containerType = parameter.getContainer().getAnalyzedType();

			if (TypeUtils.isTypeAssignableFrom(FlexoResourceCenter.class, containerType)) {
				viewSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(viewSelector,
						new DataBinding<Object>("component.resourceCenter"), new DataBinding<Object>(containerBinding), true));
			}
		}
		else {
			// No container defined, set service manager
			// No container defined, set service manager
			viewSelector.addToAssignments(
					fibModelFactory.newFIBCustomAssignment(viewSelector, new DataBinding<Object>("component.serviceManager"),
							new DataBinding<Object>("controller.flexoController.applicationContext"), true));
		}

		// viewSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(viewSelector, new DataBinding<Object>("component.view"),
		// new DataBinding<Object>(containerBinding), true));

		viewSelector
				.addToAssignments(fibModelFactory.newFIBCustomAssignment(viewSelector, new DataBinding<Object>("component.expectedType"),
						new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".type"), true));
		return registerWidget(viewSelector, parameter, panel, index);
	}

	/**
	 * Factory method used to instanciate a technology-specific FIBWidget for a given {@link FlexoBehaviourParameter}<br>
	 * Provides a hook to specialize this method in a given technology
	 * 
	 * @param parameter
	 * @param panel
	 * @param index
	 * @return
	 */
	public FIBComponent makeWidget(final FlexoBehaviourParameter parameter, FIBPanel panel, int index, FlexoBehaviourAction<?, ?, ?> action,
			FIBModelFactory fibModelFactory) {
		if (parameter instanceof GenericBehaviourParameter) {
			switch (parameter.getWidget()) {
				case TEXT_FIELD:
				case URI:
				case LOCALIZED_TEXT_FIELD:
					FIBTextField tf = fibModelFactory.newFIBTextField();
					tf.setName(parameter.getName() + "TextField");
					return registerWidget(tf, parameter, panel, index);
				case TEXT_AREA:
					FIBTextArea ta = fibModelFactory.newFIBTextArea();
					ta.setName(parameter.getName() + "TextArea");
					ta.setValidateOnReturn(true); // Avoid too many ontologies manipulations
					ta.setUseScrollBar(true);
					ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
					return registerWidget(ta, parameter, panel, index, true, true);
				case CHECKBOX:
					FIBCheckBox cb = fibModelFactory.newFIBCheckBox();
					cb.setName(parameter.getName() + "CheckBox");
					return registerWidget(cb, parameter, panel, index);
				case INTEGER:
					FIBNumber number = fibModelFactory.newFIBNumber();
					number.setName(parameter.getName() + "Number");
					number.setNumberType(NumberType.IntegerType);
					return registerWidget(number, parameter, panel, index, false, false);
				case FLOAT:
					FIBNumber numberF = fibModelFactory.newFIBNumber();
					numberF.setName(parameter.getName() + "Number");
					numberF.setNumberType(NumberType.DoubleType);
					return registerWidget(numberF, parameter, panel, index, false, false);
				case DROPDOWN:
				case RADIO_BUTTON:
					ListParameter listParameter = (ListParameter) parameter;
					FIBCheckboxList cbList = fibModelFactory.newFIBCheckboxList();
					cbList.setName(parameter.getName() + "CheckboxList");
					// TODO: repair this !!!
					logger.warning("This feature is no more implemented, please repair this !!!");
					cbList.setList(new DataBinding<List<?>>("data.parameters." + parameter.getName() + "TODO"));
					/*if (listParameter.getListType() == ListType.ObjectProperty) {
						cbList.setIteratorClass(IFlexoOntologyObjectProperty.class);
						cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
						cbList.setShowIcon(true);
						cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
						cbList.setVGap(-2);
					} else if (listParameter.getListType() == ListType.DataProperty) {
						cbList.setIteratorClass(IFlexoOntologyDataProperty.class);
						cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
						cbList.setShowIcon(true);
						cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
						cbList.setVGap(-2);
					} else if (listParameter.getListType() == ListType.Property) {
						cbList.setIteratorClass(IFlexoOntologyStructuralProperty.class);
						cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
						cbList.setShowIcon(true);
						cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
						cbList.setVGap(-2);
					}*/
					cbList.setUseScrollBar(true);
					cbList.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
					cbList.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);

					return registerWidget(cbList, parameter, panel, index, true, true);
				case CUSTOM_WIDGET:

					if (parameter.getType() instanceof ViewType) {
						return makeViewSelector(parameter, panel, index, action, fibModelFactory);
					}
					else if (parameter.getType() instanceof VirtualModelInstanceType) {
						return makeVirtualModelInstanceSelector(parameter, panel, index, action, fibModelFactory);
					}
					else if (parameter.getType() instanceof FlexoConceptInstanceType) {
						return makeFlexoConceptInstanceSelector(parameter, panel, index, action, fibModelFactory);
					}

					FIBLabel notFound = fibModelFactory.newFIBLabel("<not_found>");
					notFound.setName(parameter.getName() + "NotFound");
					return registerWidget(notFound, parameter, panel, index);
				default:
					break;
			}
		}

		if (parameter instanceof TextFieldParameter) {
			FIBTextField tf = fibModelFactory.newFIBTextField();
			tf.setName(parameter.getName() + "TextField");
			return registerWidget(tf, parameter, panel, index);
		}
		else if (parameter instanceof TextAreaParameter) {
			FIBTextArea ta = fibModelFactory.newFIBTextArea();
			ta.setName(parameter.getName() + "TextArea");
			ta.setValidateOnReturn(true); // Avoid too many ontologies manipulations
			ta.setUseScrollBar(true);
			ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
			return registerWidget(ta, parameter, panel, index, true, true);
		}
		else if (parameter instanceof CheckboxParameter) {
			FIBCheckBox cb = fibModelFactory.newFIBCheckBox();
			cb.setName(parameter.getName() + "CheckBox");
			return registerWidget(cb, parameter, panel, index);
		}
		else if (parameter instanceof IntegerParameter) {
			FIBNumber number = fibModelFactory.newFIBNumber();
			number.setName(parameter.getName() + "Number");
			number.setNumberType(NumberType.IntegerType);
			return registerWidget(number, parameter, panel, index);
		}
		else if (parameter instanceof ListParameter) {
			ListParameter listParameter = (ListParameter) parameter;
			FIBCheckboxList cbList = fibModelFactory.newFIBCheckboxList();
			cbList.setName(parameter.getName() + "CheckboxList");
			// TODO: repair this !!!
			logger.warning("This feature is no more implemented, please repair this !!!");
			cbList.setList(new DataBinding<List<?>>("data.parameters." + parameter.getName() + "TODO"));
			/*if (listParameter.getListType() == ListType.ObjectProperty) {
				cbList.setIteratorClass(IFlexoOntologyObjectProperty.class);
				cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			} else if (listParameter.getListType() == ListType.DataProperty) {
				cbList.setIteratorClass(IFlexoOntologyDataProperty.class);
				cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			} else if (listParameter.getListType() == ListType.Property) {
				cbList.setIteratorClass(IFlexoOntologyStructuralProperty.class);
				cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			}*/
			cbList.setUseScrollBar(true);
			cbList.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			cbList.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);

			return registerWidget(cbList, parameter, panel, index, true, true);
		}
		else if (parameter instanceof FlexoConceptInstanceParameter) {
			FIBCustom epiSelector = fibModelFactory.newFIBCustom();
			epiSelector.setBindingFactory(parameter.getBindingFactory());
			Class fciSelectorClass;
			try {
				fciSelectorClass = Class.forName("org.openflexo.fml.rt.controller.widget.FIBFlexoConceptInstanceSelector");
				epiSelector.setComponentClass(fciSelectorClass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			epiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(epiSelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.editor.project"), true));
			epiSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(epiSelector, new DataBinding<Object>("component.view"),
					new DataBinding<Object>("data.virtualModelInstance.view"), true));

			epiSelector
					.addToAssignments(
							fibModelFactory.newFIBCustomAssignment(epiSelector, new DataBinding<Object>("component.virtualModelInstance"),
									new DataBinding<Object>(
											"data." + ((FlexoConceptInstanceParameter) parameter).getVirtualModelInstance().toString()),
							true));

			epiSelector
					.addToAssignments(fibModelFactory.newFIBCustomAssignment(epiSelector, new DataBinding<Object>("component.flexoConcept"),
							new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".flexoConceptType"), true));
			// extra informations.
			epiSelector
					.addToAssignments(fibModelFactory.newFIBCustomAssignment(epiSelector, new DataBinding<Object>("component.virtualModel"),
							new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".modelSlotVirtualModel"), true));
			epiSelector.addToAssignments(
					fibModelFactory.newFIBCustomAssignment(epiSelector, new DataBinding<Object>("component.viewPointLibrary"),
							new DataBinding<Object>("data.serviceManager.viewPointLibrary"), true));

			return registerWidget(epiSelector, parameter, panel, index);
		}
		else if (parameter instanceof FlexoResourceParameter) {
			FIBCustom resourceSelector = fibModelFactory.newFIBCustom();
			resourceSelector.setBindingFactory(parameter.getBindingFactory());
			Class resourceSelectorClass;
			try {
				resourceSelectorClass = Class.forName("org.openflexo.components.widget.FIBResourceSelector");
				resourceSelector.setComponentClass(resourceSelectorClass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resourceSelector.addToAssignments(fibModelFactory.newFIBCustomAssignment(resourceSelector,
					new DataBinding<Object>("component.technologyAdapter"),
					new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".resourceTechnologyAdapter"), true));
			resourceSelector.addToAssignments(
					fibModelFactory.newFIBCustomAssignment(resourceSelector, new DataBinding<Object>("component.resourceDataClass"),
							new DataBinding<Object>("data.parametersDefinitions." + parameter.getName() + ".resourceDataType"), true));
			resourceSelector.addToAssignments(
					fibModelFactory.newFIBCustomAssignment(resourceSelector, new DataBinding<Object>("component.resourceManager"),
							new DataBinding<Object>("data.serviceManager.resourceManager"), true));

			return registerWidget(resourceSelector, parameter, panel, index);
		}

		return null;
	}

	protected FIBComponent registerWidget(FIBComponent widget, FlexoBehaviourParameter parameter, FIBPanel panel, int index) {
		return registerWidget(widget, parameter, panel, index, true, false);
	}

	protected FIBComponent registerWidget(FIBComponent widget, FlexoBehaviourParameter parameter, FIBPanel panel, int index,
			boolean expandHorizontally, boolean expandVertically) {
		((FIBWidget) widget).setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
		if (widget instanceof FIBWidget) {
			((FIBWidget) widget).setValueChangedAction(new DataBinding<Object>("controller.parameterValueChanged(data)"));
		}
		panel.addToSubComponents(widget, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, expandHorizontally, expandVertically),
				index);
		return widget;
	}

	/**
	 * Provides a hook to handle specific {@link FlexoBehaviourParameter} for a given technology
	 * 
	 * @param availableParameterTypes
	 */
	public void appendSpecificFlexoBehaviourParameters(List<Class<? extends FlexoBehaviourParameter>> availableParameterTypes) {
	}

	/**
	 * Hook allowing to register technology-specific types
	 * 
	 * @param converter
	 */
	/*public void initTechnologySpecificTypeEditors(TechnologyAdapterService taService) {
		for (Class<? extends CustomType> typeClass : taService.getCustomTypeFactories().keySet()) {
			CustomTypeEditor editor = makeCustomTypeEditor(typeClass);
			customTypeEditors.put(typeClass, editor);
		}
	}*/

	protected CustomTypeEditor makeCustomTypeEditor(Class<? extends CustomType> typeClass) {
		return null;
	}

	public <T extends CustomType> CustomTypeEditor<T> getCustomTypeEditor(Class<T> typeClass) {
		CustomTypeEditor<T> returned = customTypeEditors.get(typeClass);
		if (returned == null) {
			returned = makeCustomTypeEditor(typeClass);
			customTypeEditors.put(typeClass, returned);
		}
		return returned;
	}

}
