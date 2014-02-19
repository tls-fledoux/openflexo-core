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
package org.openflexo.inspector;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.components.widget.FIBIndividualSelector;
import org.openflexo.components.widget.FIBPropertySelector;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBModelFactory;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.utils.FlexoObjectReference;
import org.openflexo.foundation.view.FlexoConceptInstance;
import org.openflexo.foundation.viewpoint.FlexoConcept;
import org.openflexo.foundation.viewpoint.FlexoConceptInstanceType;
import org.openflexo.foundation.viewpoint.LocalizedDictionary;
import org.openflexo.foundation.viewpoint.inspector.CheckboxInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ClassInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.DataPropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IndividualInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IntegerInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ObjectPropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.PropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextAreaInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextFieldInspectorEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

/**
 * Represent a FIBComponent used as an inspector for a particular class instance
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FIBInspector.FIBInspectorImpl.class)
@XMLElement
public interface FIBInspector extends FIBPanel {

	public FIBInspector getSuperInspector();

	public FIBTabPanel getTabPanel();

	public void appendSuperInspectors(ModuleInspectorController inspectorController);

	public boolean updateEditionPatternInstanceInspector(FlexoConceptInstance object);

	public boolean updateFlexoObjectInspector(FlexoObject object);

	public String getXMLRepresentation();

	@Override
	public FIBModelFactory getFactory();

	public abstract class FIBInspectorImpl extends FIBPanelImpl implements FIBInspector {

		static final Logger logger = Logger.getLogger(FIBInspector.class.getPackage().getName());

		private boolean superInspectorWereAppened = false;

		private FIBInspector superInspector;

		private final Vector<FlexoConcept> currentFlexoConcepts = new Vector<FlexoConcept>();
		private final Hashtable<FlexoConcept, FIBTab> tabsForEPIReference = new Hashtable<FlexoConcept, FIBTab>();
		private final Hashtable<FlexoConcept, FIBTab> tabsForEPI = new Hashtable<FlexoConcept, FIBTab>();

		@Override
		public FIBModelFactory getFactory() {
			return ModuleInspectorController.INSPECTOR_FACTORY;
		}

		@Override
		public FIBInspector getSuperInspector() {
			return superInspector;
		}

		@Override
		public void appendSuperInspectors(ModuleInspectorController inspectorController) {
			if (getDataType() == null) {
				return;
			}
			if (getDataType() instanceof Class) {
				FIBInspector superInspector = inspectorController.inspectorForClass(((Class) getDataType()).getSuperclass());
				if (superInspector != null) {
					superInspector.appendSuperInspectors(inspectorController);
					this.superInspector = superInspector;
					appendSuperInspector(superInspector);
				}
			}
		}

		@Override
		public String toString() {
			return "Inspector[" + getDataType() + "]";
		}

		protected void appendSuperInspector(FIBInspector superInspector) {
			if (!superInspectorWereAppened) {
				append((FIBInspector) superInspector.cloneObject());
				superInspectorWereAppened = true;
				// logger.info("< Appened " + superInspector + " to " + this);
			}
		}

		@Override
		public FIBTabPanel getTabPanel() {
			if (getSubComponents().size() > 0 && getSubComponents().get(0) instanceof FIBTabPanel) {
				return (FIBTabPanel) getSubComponents().get(0);
			}
			return null;
		}

		@Override
		public String getXMLRepresentation() {
			return getFactory().stringRepresentation(this);
		}

		private boolean ensureCreationOfTabForEPIReference(FlexoConcept ep) {
			FIBTab returned = tabsForEPIReference.get(ep);
			if (returned == null) {
				// System.out.println("Creating FIBTab for " + ep);
				String epIdentifier = getFlexoConceptIdentifierForEPIReference(ep);
				returned = makeFIBTab(ep, epIdentifier);
				tabsForEPIReference.put(ep, returned);
				getTabPanel().addToSubComponents(returned, null, 0);
				return true;
			}
			return false;
		}

		private boolean ensureCreationOfTabForEPI(FlexoConcept ep) {
			FIBTab returned = tabsForEPI.get(ep);
			if (returned == null) {
				// System.out.println("Creating FIBTab for " + ep);
				String epIdentifier = getFlexoConceptIdentifierForEPI(ep);
				returned = makeFIBTab(ep, epIdentifier);
				tabsForEPI.put(ep, returned);
				getTabPanel().addToSubComponents(returned, null, 0);
				return true;
			}
			return false;
		}

		/**
		 * This method looks after object's FlexoConcept references to know if we need to structurally change inspector by adding or
		 * removing tabs, which all correspond to one and only one FlexoConcept
		 * 
		 * Note: only object providing support as primary role are handled here
		 * 
		 * @param object
		 * @return a boolean indicating if a new tab was created
		 */
		@Override
		public boolean updateFlexoObjectInspector(FlexoObject object) {

			boolean returned = false;

			currentFlexoConcepts.clear();

			Set<FlexoConcept> flexoConceptsToDisplay = new HashSet<FlexoConcept>();

			for (FlexoConcept ep : tabsForEPIReference.keySet()) {
				if (object.getFlexoConceptInstance(ep) == null) {
					tabsForEPIReference.get(ep).setVisible(DataBinding.makeFalseBinding());
				}
			}

			if (object.getEditionPatternReferences() != null) {
				for (FlexoObjectReference<FlexoConceptInstance> ref : object.getEditionPatternReferences()) {
					FlexoConceptInstance epi = ref.getObject();
					flexoConceptsToDisplay.add(epi.getFlexoConcept());
					if (ensureCreationOfTabForEPIReference(epi.getFlexoConcept())) {
						returned = true;
					}
					FIBTab tab = tabsForEPIReference.get(epi.getFlexoConcept());
					tab.setVisible(DataBinding.makeTrueBinding());
					currentFlexoConcepts.add(epi.getFlexoConcept());
				}
				// updateBindingModel();
			}

			/*for (FIBComponent c : getTabPanel().getSubComponents()) {
				System.out.println("> Tab: " + c + " visible=" + c.getVisible());
				if (StringUtils.isNotEmpty(c.getVisible().toString())) {
					FIBWidget w = (FIBWidget) ((FIBContainer) c).getSubComponents().get(1);
					try {
						logger.info("Getting this "
								+ XMLCoder.encodeObjectWithMapping(w, FIBLibrary.getFIBMapping(), StringEncoder.getDefaultInstance()));
					} catch (InvalidObjectSpecificationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (AccessorInvocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DuplicateSerializationIdentifierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("data=" + w.getData());
					BindingValue v = (BindingValue) w.getData().getBinding();
					System.out.println("bv=" + v);
					System.out.println("0:" + v.getBindingPathElementAtIndex(0) + " of " + v.getBindingPathElementAtIndex(0).getClass());
					System.out.println("1:" + v.getBindingPathElementAtIndex(1) + " of " + v.getBindingPathElementAtIndex(1).getClass());
				}
			}*/

			return returned;
		}

		/**
		 * This method looks after object's FlexoConcept references to know if we need to structurally change inspector by adding or
		 * removing tabs, which all correspond to one and only one FlexoConcept
		 * 
		 * Note: only object providing support as primary role are handled here
		 * 
		 * @param object
		 * @return a boolean indicating if a new tab was created
		 */
		@Override
		public boolean updateEditionPatternInstanceInspector(FlexoConceptInstance object) {

			boolean returned = false;

			currentFlexoConcepts.clear();

			for (FlexoConcept ep : tabsForEPI.keySet()) {
				if (object.getFlexoConcept() == ep) {
					tabsForEPI.get(ep).setVisible(DataBinding.makeFalseBinding());
				}
			}

			if (ensureCreationOfTabForEPI(object.getFlexoConcept())) {
				returned = true;
			}
			FIBTab tab = tabsForEPI.get(object.getFlexoConcept());
			tab.setVisible(DataBinding.makeTrueBinding());
			currentFlexoConcepts.add(object.getFlexoConcept());

			// VERY IMPORTANT
			// We MUST here redefine the type of inspected data
			BindingVariable bv = getBindingModel().bindingVariableNamed("data");
			if (bv != null && object != null) {
				bv.setType(FlexoConceptInstanceType.getFlexoConceptInstanceType(object.getFlexoConcept()));
			}

			return returned;
		}

		/*@Override
		protected void createBindingModel() {
			super.createBindingModel();
			for (int i = 0; i < currentFlexoConcepts.size(); i++) {
				FlexoConcept ep = currentFlexoConcepts.get(i);
				_bindingModel.addToBindingVariables(new EditionPatternInstanceBindingVariable(ep, i));
			}
		}*/

		private FIBWidget makeWidget(final InspectorEntry entry, FIBTab newTab) {
			if (entry instanceof TextFieldInspectorEntry) {
				FIBTextField tf = getFactory().newFIBTextField();
				tf.setValidateOnReturn(true); // Avoid too many ontologies manipulations
				newTab.addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return tf;
			} else if (entry instanceof TextAreaInspectorEntry) {
				FIBTextArea ta = getFactory().newFIBTextArea();
				ta.setValidateOnReturn(true); // Avoid to many ontologies manipulations
				ta.setUseScrollBar(true);
				ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
				newTab.addToSubComponents(ta, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true));
				return ta;
			} else if (entry instanceof CheckboxInspectorEntry) {
				FIBCheckBox cb = getFactory().newFIBCheckBox();
				newTab.addToSubComponents(cb, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return cb;
			} else if (entry instanceof IntegerInspectorEntry) {
				FIBNumber number = getFactory().newFIBNumber();
				number.setNumberType(NumberType.IntegerType);
				newTab.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return number;
			} else if (entry instanceof IndividualInspectorEntry) {
				IndividualInspectorEntry individualEntry = (IndividualInspectorEntry) entry;
				FIBCustom individualSelector = getFactory().newFIBCustom();
				individualSelector.setComponentClass(FIBIndividualSelector.class);
				// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
				// component.context = xxx
				FIBCustomAssignment projectAssignment = getFactory().newInstance(FIBCustomAssignment.class);
				projectAssignment.setOwner(individualSelector);
				projectAssignment.setVariable(new DataBinding<Object>("component.project"));
				projectAssignment.setValue(new DataBinding<Object>("data.project"));
				projectAssignment.setMandatory(true);
				individualSelector.addToAssignments(projectAssignment);

				/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));*/
				/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector,
						new DataBinding("component.contextOntologyURI"), new DataBinding('"' + individualEntry.getViewPoint()
								.getViewpointOntology().getURI() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return entry.getBindingFactory();
							}
						}, true));*/
				// Quick and dirty hack to configure IndividualSelector: refactor this when new binding model will be in use
				IFlexoOntologyClass conceptClass = null;
				if (individualEntry.getIsDynamicConceptValue()) {
					// conceptClass = classEntry.evaluateConceptValue(action);
					// TODO: implement proper scheme with new binding support
					logger.warning("Please implement me !!!!!!!!!");
				} else {
					conceptClass = individualEntry.getConcept();
				}
				if (conceptClass != null) {
					FIBCustomAssignment conceptClassAssignment = getFactory().newInstance(FIBCustomAssignment.class);
					conceptClassAssignment.setOwner(individualSelector);
					conceptClassAssignment.setVariable(new DataBinding<Object>("component.typeURI"));
					conceptClassAssignment.setValue(new DataBinding('"' + conceptClass.getURI() + '"'));
					conceptClassAssignment.setMandatory(true);
					individualSelector.addToAssignments(conceptClassAssignment);
					/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.typeURI"),
							new DataBinding('"' + conceptClass.getURI() + '"'), true));*/
				}
				if (StringUtils.isNotEmpty(individualEntry.getRenderer())) {
					FIBCustomAssignment rendererAssignment = getFactory().newInstance(FIBCustomAssignment.class);
					rendererAssignment.setOwner(individualSelector);
					rendererAssignment.setVariable(new DataBinding<Object>("component.renderer"));
					rendererAssignment.setValue(new DataBinding('"' + individualEntry.getRenderer() + '"'));
					rendererAssignment.setMandatory(true);
					individualSelector.addToAssignments(rendererAssignment);
					/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.renderer"),
							new DataBinding('"' + individualEntry.getRenderer() + '"'), true));*/
				}

				newTab.addToSubComponents(individualSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return individualSelector;
			} else if (entry instanceof ClassInspectorEntry) {
				ClassInspectorEntry classEntry = (ClassInspectorEntry) entry;
				FIBCustom classSelector = getFactory().newFIBCustom();
				classSelector.setComponentClass(org.openflexo.components.widget.FIBClassSelector.class);
				// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
				// component.context = xxx
				FIBCustomAssignment projectAssignment = getFactory().newInstance(FIBCustomAssignment.class);
				projectAssignment.setOwner(classSelector);
				projectAssignment.setVariable(new DataBinding<Object>("component.project"));
				projectAssignment.setValue(new DataBinding<Object>("data.project"));
				projectAssignment.setMandatory(true);
				classSelector.addToAssignments(projectAssignment);
				/*classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));*/
				/*classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.contextOntologyURI"),
						new DataBinding('"' + classEntry.getViewPoint().getViewpointOntology().getURI() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return entry.getBindingFactory();
							}
						}, true));*/
				// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
				IFlexoOntologyClass conceptClass = null;
				if (classEntry.getIsDynamicConceptValue()) {
					// conceptClass = classEntry.evaluateConceptValue(action);
					// TODO: implement proper scheme with new binding support
					logger.warning("Please implement me !!!!!!!!!");
				} else {
					conceptClass = classEntry.getConcept();
				}
				if (conceptClass != null) {
					FIBCustomAssignment rootClassAssignment = getFactory().newInstance(FIBCustomAssignment.class);
					rootClassAssignment.setOwner(classSelector);
					rootClassAssignment.setVariable(new DataBinding<Object>("component.rootClassURI"));
					rootClassAssignment.setValue(new DataBinding<Object>('"' + conceptClass.getURI() + '"'));
					rootClassAssignment.setMandatory(true);
					classSelector.addToAssignments(rootClassAssignment);
					/*	classSelector.addToAssignments(new FIBCustomAssignment(classSelector,
								new DataBinding<Object>("component.rootClassURI"), new DataBinding<Object>('"' + conceptClass.getURI() + '"'),
								true));*/
				}
				newTab.addToSubComponents(classSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return classSelector;
			} else if (entry instanceof PropertyInspectorEntry) {
				PropertyInspectorEntry propertyEntry = (PropertyInspectorEntry) entry;
				FIBCustom propertySelector = getFactory().newFIBCustom();
				propertySelector.setComponentClass(FIBPropertySelector.class);
				// Quick and dirty hack to configure FIBPropertySelector: refactor this when new binding model will be in use
				// component.context = xxx
				FIBCustomAssignment projectAssignment = getFactory().newInstance(FIBCustomAssignment.class);
				projectAssignment.setOwner(propertySelector);
				projectAssignment.setVariable(new DataBinding<Object>("component.project"));
				projectAssignment.setValue(new DataBinding<Object>("data.project"));
				projectAssignment.setMandatory(true);
				propertySelector.addToAssignments(projectAssignment);
				/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));*/
				/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.contextOntologyURI"),
						new DataBinding('"' + propertyEntry.getViewPoint().getViewpointOntology().getURI() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return entry.getBindingFactory();
							}
						}, true));*/

				// Quick and dirty hack to configure FIBPropertySelector: refactor this when new binding model will be in use
				IFlexoOntologyClass domainClass = null;
				if (propertyEntry.getIsDynamicDomainValue()) {
					// domainClass = propertyEntry.evaluateDomainValue(action);
					// TODO: implement proper scheme with new binding support
					logger.warning("Please implement me !!!!!!!!!");
				} else {
					domainClass = propertyEntry.getDomain();
				}
				if (domainClass != null) {
					FIBCustomAssignment domainClassAssignment = getFactory().newInstance(FIBCustomAssignment.class);
					domainClassAssignment.setOwner(propertySelector);
					domainClassAssignment.setVariable(new DataBinding<Object>("component.domainClassURI"));
					domainClassAssignment.setValue(new DataBinding<Object>('"' + domainClass.getURI() + '"'));
					domainClassAssignment.setMandatory(true);
					propertySelector.addToAssignments(domainClassAssignment);
					/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
							"component.domainClassURI"), new DataBinding<Object>('"' + domainClass.getURI() + '"'), true));*/
				}
				if (propertyEntry instanceof ObjectPropertyInspectorEntry) {
					IFlexoOntologyClass rangeClass = null;
					if (propertyEntry.getIsDynamicDomainValue()) {
						// domainClass = propertyEntry.evaluateDomainValue(action);
						// TODO: implement proper scheme with new binding support
						logger.warning("Please implement me !!!!!!!!!");
					} else {
						rangeClass = ((ObjectPropertyInspectorEntry) propertyEntry).getRange();
					}
					if (rangeClass != null) {
						FIBCustomAssignment rangeClassAssignment = getFactory().newInstance(FIBCustomAssignment.class);
						rangeClassAssignment.setOwner(propertySelector);
						rangeClassAssignment.setVariable(new DataBinding<Object>("component.rangeClassURI"));
						rangeClassAssignment.setValue(new DataBinding<Object>('"' + rangeClass.getURI() + '"'));
						rangeClassAssignment.setMandatory(true);
						propertySelector.addToAssignments(rangeClassAssignment);
						/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
								"component.rangeClassURI"), new DataBinding<Object>('"' + rangeClass.getURI() + '"'), true));*/
					}
				}
				if (propertyEntry instanceof ObjectPropertyInspectorEntry) {
					FIBCustomAssignment selectDataPropertiesAssignment = getFactory().newInstance(FIBCustomAssignment.class);
					selectDataPropertiesAssignment.setOwner(propertySelector);
					selectDataPropertiesAssignment.setVariable(new DataBinding<Object>("component.selectDataProperties"));
					selectDataPropertiesAssignment.setValue(new DataBinding<Object>("false"));
					selectDataPropertiesAssignment.setMandatory(true);
					propertySelector.addToAssignments(selectDataPropertiesAssignment);
					/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
							"component.selectDataProperties"), new DataBinding<Object>("false"), true));*/
				} else if (propertyEntry instanceof DataPropertyInspectorEntry) {
					FIBCustomAssignment selectObjectPropertiesAssignment = getFactory().newInstance(FIBCustomAssignment.class);
					selectObjectPropertiesAssignment.setOwner(propertySelector);
					selectObjectPropertiesAssignment.setVariable(new DataBinding<Object>("component.selectObjectProperties"));
					selectObjectPropertiesAssignment.setValue(new DataBinding<Object>("false"));
					selectObjectPropertiesAssignment.setMandatory(true);
					propertySelector.addToAssignments(selectObjectPropertiesAssignment);
					/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
							"component.selectObjectProperties"), new DataBinding<Object>("false"), true));*/
				}

				// Quick and dirty hack to configure PropertySelector: refactor this when new binding model will be in use
				/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.domainClassURI"),
						new DataBinding('"' + ((PropertyInspectorEntry) entry)._getDomainURI() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return entry.getBindingFactory();
							}
						}, true));*/
				newTab.addToSubComponents(propertySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return propertySelector;
			}

			FIBLabel unknown = getFactory().newFIBLabel();
			unknown.setLabel("???");
			newTab.addToSubComponents(unknown, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return unknown;

		}

		private FIBTab makeFIBTab(FlexoConcept ep, String epIdentifier) {
			// logger.info("makeFIBTab " + refIndex + " for " + ep);
			FIBTab newTab = createFIBTabForFlexoConcept(ep);
			appendInspectorEntries(ep, epIdentifier, newTab);
			newTab.finalizeDeserialization();
			return newTab;
		}

		protected void appendInspectorEntries(FlexoConcept ep, String epIdentifier, FIBTab newTab) {
			for (FlexoConcept parentEP : ep.getParentFlexoConcepts()) {
				appendInspectorEntries(parentEP, epIdentifier, newTab);
			}
			LocalizedDictionary localizedDictionary = ep.getViewPoint().getLocalizedDictionary();
			for (final InspectorEntry entry : ep.getInspector().getEntries()) {
				FIBLabel label = getFactory().newFIBLabel();
				String entryLabel = localizedDictionary.getLocalizedForKeyAndLanguage(entry.getLabel(),
						FlexoLocalization.getCurrentLanguage());
				if (entryLabel == null) {
					entryLabel = entry.getLabel();
				}
				label.setLabel(entryLabel);
				newTab.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
				FIBWidget widget = makeWidget(entry, newTab);
				widget.setBindingFactory(entry.getBindingFactory());
				widget.setData(new DataBinding<Object>(epIdentifier + "." + entry.getData().toString()));
				widget.setReadOnly(entry.getIsReadOnly());
			}
		}

		protected FIBTab createFIBTabForFlexoConcept(FlexoConcept ep) {
			String epIdentifier = getFlexoConceptIdentifierForEPIReference(ep);
			FIBTab newTab = getFactory().newFIBTab();
			newTab.setTitle(ep.getInspector().getInspectorTitle());
			newTab.setLayout(Layout.twocols);
			newTab.setUseScrollBar(true);
			// newTab.setDataClass(FlexoConceptInstance.class);
			// newTab.setData(new DataBinding("data.editionPatternReferences.get["+refIndex+"].editionPatternInstance"));
			// newTab.setData(new DataBinding("data.editionPatternReferences.firstElement.editionPatternInstance"));
			newTab.setName(epIdentifier + "Panel");
			return newTab;
		}

		protected String getFlexoConceptIdentifierForEPIReference(FlexoConcept ep) {
			// Instead of just referencing ep name, reference the URI (in case of in same VP, many EP have same name)
			return "data.getEditionPatternInstance(\"" + ep.getURI() + "\")";
		}

		protected String getFlexoConceptIdentifierForEPI(FlexoConcept ep) {
			return "data";
		}
	}
}
