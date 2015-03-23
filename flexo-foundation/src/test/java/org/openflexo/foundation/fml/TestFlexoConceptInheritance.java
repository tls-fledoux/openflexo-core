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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.foundation.fml.FlexoProperty.OverridenPropertiesMustBeTypeCompatible;
import org.openflexo.foundation.fml.PrimitiveRole.PrimitiveType;
import org.openflexo.foundation.fml.ViewPoint.ViewPointImpl;
import org.openflexo.foundation.fml.VirtualModel.VirtualModelImpl;
import org.openflexo.foundation.fml.action.CreateAbstractProperty;
import org.openflexo.foundation.fml.action.CreateExpressionProperty;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.CreateFlexoRole;
import org.openflexo.foundation.fml.binding.ViewPointBindingModel;
import org.openflexo.foundation.fml.binding.VirtualModelBindingModel;
import org.openflexo.foundation.fml.rm.ViewPointResource;
import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.model.validation.ValidationReport;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * This unit test is intented to test {@link FlexoConcept} inheritance features, as well as "isAbstract" management and
 * {@link FlexoProperty} inheritance and shadowing
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFlexoConceptInheritance extends OpenflexoProjectAtRunTimeTestCase {

	static FlexoEditor editor;
	static ViewPoint viewPoint;
	static VirtualModel virtualModel;

	static FlexoConcept flexoConceptA;
	static FlexoConcept flexoConceptB;
	static FlexoConcept flexoConceptC;
	static FlexoConcept flexoConceptD;

	public static AbstractProperty<String> property1InA;
	public static AbstractProperty<Boolean> property2InA;
	public static AbstractProperty<Number> property3InA;
	public static AbstractProperty<FlexoConceptInstanceType> property4InA;
	public static AbstractProperty<String> property5InA;
	public static PrimitiveRole<String> property6InA;

	public static PrimitiveRole<Boolean> property2InB;
	public static AbstractProperty<Integer> property3InB;
	public static PrimitiveRole<String> property7InB;

	public static FlexoConceptInstanceRole property4InC;
	public static PrimitiveRole<String> property8InC;

	public static PrimitiveRole<String> property1InD;
	public static ExpressionProperty<Integer> property3InD;
	public static PrimitiveRole<String> property5InD;
	public static PrimitiveRole<String> property9InD;

	static FlexoProject project;
	static View newView;
	static VirtualModelInstance vmi;
	static FlexoConceptInstance a;

	/**
	 * Init
	 */
	@Test
	@TestOrder(1)
	public void init() {
		instanciateTestServiceManager();

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

		System.out.println("ResourceCenter= " + resourceCenter);
	}

	/**
	 * Test {@link ViewPoint} creation, check {@link BindingModel}
	 */
	@Test
	@TestOrder(2)
	public void testCreateViewPoint() {
		viewPoint = ViewPointImpl.newViewPoint("TestViewPoint", "http://openflexo.org/test/TestViewPoint", resourceCenter.getDirectory(),
				serviceManager.getViewPointLibrary());
		// assertTrue(((ViewPointResource) viewPoint.getResource()).getDirectory().exists());
		// assertTrue(((ViewPointResource) viewPoint.getResource()).getFile().exists());
		assertTrue(((ViewPointResource) viewPoint.getResource()).getDirectory() != null);
		assertTrue(((ViewPointResource) viewPoint.getResource()).getFlexoIODelegate().exists());

		System.out.println("ViewPoint BindingModel = " + viewPoint.getBindingModel());
		assertNotNull(viewPoint.getBindingModel());
		assertEquals(2, viewPoint.getBindingModel().getBindingVariablesCount());
		assertNotNull(viewPoint.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(viewPoint.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));

	}

	/**
	 * Test {@link VirtualModel} creation, check {@link BindingModel}
	 */
	@Test
	@TestOrder(3)
	public void testCreateVirtualModel() throws SaveResourceException {

		virtualModel = VirtualModelImpl.newVirtualModel("VirtualModel", viewPoint);
		assertTrue(ResourceLocator.retrieveResourceAsFile(((VirtualModelResource) virtualModel.getResource()).getDirectory()).exists());
		assertTrue(((VirtualModelResource) virtualModel.getResource()).getFlexoIODelegate().exists());

		assertNotNull(virtualModel.getBindingModel());
		assertEquals(4, virtualModel.getBindingModel().getBindingVariablesCount());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertEquals(ViewType.getViewType(viewPoint),
				virtualModel.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY).getType());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.VIRTUAL_MODEL_INSTANCE_PROPERTY));
		assertEquals(VirtualModelInstanceType.getVirtualModelInstanceType(virtualModel), virtualModel.getBindingModel()
				.bindingVariableNamed(VirtualModelBindingModel.VIRTUAL_MODEL_INSTANCE_PROPERTY).getType());

		// We disconnect VirtualModel from ViewPoint, and we check BindingModel evolution
		viewPoint.removeFromVirtualModels(virtualModel);
		System.out.println("VirtualModel BindingModel = " + virtualModel.getBindingModel());
		assertNotNull(virtualModel.getBindingModel());
		assertEquals(2, virtualModel.getBindingModel().getBindingVariablesCount());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.VIRTUAL_MODEL_INSTANCE_PROPERTY));
		// assertEquals(VirtualModelInstanceType.getVirtualModelInstanceType(virtualModel1), virtualModel1.getBindingModel()
		// .bindingVariableNamed(VirtualModelBindingModel.VIRTUAL_MODEL_INSTANCE_PROPERTY).getType());

		// We reconnect VirtualModel again, and we check BindingModel evolution
		viewPoint.addToVirtualModels(virtualModel);
		System.out.println("VirtualModel BindingModel = " + virtualModel.getBindingModel());
		assertEquals(4, virtualModel.getBindingModel().getBindingVariablesCount());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertEquals(ViewType.getViewType(viewPoint),
				virtualModel.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY).getType());
		assertNotNull(virtualModel.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.VIRTUAL_MODEL_INSTANCE_PROPERTY));
		assertEquals(VirtualModelInstanceType.getVirtualModelInstanceType(virtualModel), virtualModel.getBindingModel()
				.bindingVariableNamed(VirtualModelBindingModel.VIRTUAL_MODEL_INSTANCE_PROPERTY).getType());

	}

	/**
	 * Test FlexoConceptA creation, with 6 properties
	 */
	@Test
	@TestOrder(4)
	public void testCreateFlexoConceptA() throws SaveResourceException {

		log("testCreateFlexoConceptA()");

		CreateFlexoConcept addConceptA = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptA.setNewFlexoConceptName("FlexoConceptA");
		addConceptA.doAction();

		flexoConceptA = addConceptA.getNewFlexoConcept();

		System.out.println("flexoConceptA = " + flexoConceptA);
		assertNotNull(flexoConceptA);

		CreateAbstractProperty createProperty1inA = CreateAbstractProperty.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty1inA.setPropertyName("property1");
		createProperty1inA.setPropertyType(String.class);
		createProperty1inA.doAction();
		assertTrue(createProperty1inA.hasActionExecutionSucceeded());
		assertNotNull(property1InA = (AbstractProperty<String>) createProperty1inA.getNewFlexoProperty());

		CreateAbstractProperty createProperty2inA = CreateAbstractProperty.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty2inA.setPropertyName("property2");
		createProperty2inA.setPropertyType(Boolean.class);
		createProperty2inA.doAction();
		assertTrue(createProperty2inA.hasActionExecutionSucceeded());
		assertNotNull(property2InA = (AbstractProperty<Boolean>) createProperty2inA.getNewFlexoProperty());

		CreateAbstractProperty createProperty3inA = CreateAbstractProperty.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty3inA.setPropertyName("property3");
		createProperty3inA.setPropertyType(Number.class);
		createProperty3inA.doAction();
		assertTrue(createProperty3inA.hasActionExecutionSucceeded());
		assertNotNull(property3InA = (AbstractProperty<Number>) createProperty3inA.getNewFlexoProperty());

		CreateAbstractProperty createProperty4inA = CreateAbstractProperty.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty4inA.setPropertyName("property4");
		createProperty4inA.setPropertyType(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE);
		createProperty4inA.doAction();
		assertTrue(createProperty4inA.hasActionExecutionSucceeded());
		assertNotNull(property4InA = (AbstractProperty<FlexoConceptInstanceType>) createProperty4inA.getNewFlexoProperty());

		CreateAbstractProperty createProperty5inA = CreateAbstractProperty.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty5inA.setPropertyName("property5");
		createProperty5inA.setPropertyType(String.class);
		createProperty5inA.doAction();
		assertTrue(createProperty5inA.hasActionExecutionSucceeded());
		assertNotNull(property5InA = (AbstractProperty<String>) createProperty5inA.getNewFlexoProperty());

		CreateFlexoRole createProperty6inA = CreateFlexoRole.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty6inA.setRoleName("property6");
		createProperty6inA.setFlexoRoleClass(PrimitiveRole.class);
		createProperty6inA.setPrimitiveType(PrimitiveType.String);
		createProperty6inA.setPropertyCardinality(PropertyCardinality.One);
		createProperty6inA.doAction();
		assertTrue(createProperty6inA.hasActionExecutionSucceeded());
		assertNotNull(property6InA = (PrimitiveRole<String>) createProperty6inA.getNewFlexoRole());

		assertEquals(6, flexoConceptA.getFlexoProperties().size());
		assertEquals(6, flexoConceptA.getDeclaredProperties().size());
		assertEquals(6, flexoConceptA.getAccessibleProperties().size());
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty1inA.getNewFlexoProperty()));
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty2inA.getNewFlexoProperty()));
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty3inA.getNewFlexoProperty()));
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty4inA.getNewFlexoProperty()));
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty5inA.getNewFlexoProperty()));
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty6inA.getNewFlexoProperty()));
		assertEquals(flexoConceptA.getDeclaredProperties(), flexoConceptA.getAccessibleProperties());

		assertSame(property1InA, flexoConceptA.getAccessibleProperty("property1"));
		assertEquals(String.class, property1InA.getType());
		assertEquals(String.class, property1InA.getResultingType());

		assertSame(property2InA, flexoConceptA.getAccessibleProperty("property2"));
		assertEquals(Boolean.class, property2InA.getType());
		assertEquals(Boolean.class, property2InA.getResultingType());

		assertSame(property3InA, flexoConceptA.getAccessibleProperty("property3"));
		assertEquals(Number.class, property3InA.getType());
		assertEquals(Number.class, property3InA.getResultingType());

		assertSame(property4InA, flexoConceptA.getAccessibleProperty("property4"));
		assertEquals(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE, property4InA.getType());
		assertEquals(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE, property4InA.getResultingType());

		assertSame(property5InA, flexoConceptA.getAccessibleProperty("property5"));
		assertEquals(String.class, property5InA.getType());
		assertEquals(String.class, property5InA.getResultingType());

		assertSame(property6InA, flexoConceptA.getAccessibleProperty("property6"));
		assertEquals(String.class, property6InA.getType());
		assertEquals(String.class, property6InA.getResultingType());

		// Because concept define some abstract properties, it is abstract
		assertTrue(flexoConceptA.isAbstract());

		// We try to force to make it non abstract, and check that it is still abstract
		flexoConceptA.setAbstract(false);
		assertTrue(flexoConceptA.isAbstract());

		System.out.println("FML=" + virtualModel.getFMLRepresentation());

		((VirtualModelResource) virtualModel.getResource()).save(null);

	}

	/**
	 * Test FlexoConceptB creation, define some overriden properties
	 */
	@Test
	@TestOrder(5)
	public void testCreateFlexoConceptB() throws SaveResourceException {

		log("testCreateFlexoConceptB()");

		CreateFlexoConcept addConceptB = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptB.setNewFlexoConceptName("FlexoConceptB");
		addConceptB.doAction();

		flexoConceptB = addConceptB.getNewFlexoConcept();

		flexoConceptB.addToParentFlexoConcepts(flexoConceptA);

		System.out.println("flexoConceptB = " + flexoConceptB);
		assertNotNull(flexoConceptB);

		CreateFlexoRole createProperty2inB = CreateFlexoRole.actionType.makeNewAction(flexoConceptB, null, editor);
		createProperty2inB.setRoleName("property2");
		createProperty2inB.setFlexoRoleClass(PrimitiveRole.class);
		createProperty2inB.setPrimitiveType(PrimitiveType.Boolean);
		createProperty2inB.setPropertyCardinality(PropertyCardinality.One);
		createProperty2inB.doAction();
		assertTrue(createProperty2inB.hasActionExecutionSucceeded());
		assertNotNull(property2InB = (PrimitiveRole<Boolean>) createProperty2inB.getNewFlexoRole());

		// Property3 is overriden by an AbstractProperty with a more specialized type
		CreateAbstractProperty createProperty3inB = CreateAbstractProperty.actionType.makeNewAction(flexoConceptB, null, editor);
		createProperty3inB.setPropertyName("property3");
		createProperty3inB.setPropertyType(Integer.class);
		createProperty3inB.doAction();
		assertTrue(createProperty3inB.hasActionExecutionSucceeded());
		assertNotNull(property3InB = (AbstractProperty<Integer>) createProperty3inB.getNewFlexoProperty());

		CreateFlexoRole createProperty7inB = CreateFlexoRole.actionType.makeNewAction(flexoConceptB, null, editor);
		createProperty7inB.setRoleName("property7");
		createProperty7inB.setFlexoRoleClass(PrimitiveRole.class);
		createProperty7inB.setPrimitiveType(PrimitiveType.String);
		createProperty7inB.setPropertyCardinality(PropertyCardinality.One);
		createProperty7inB.doAction();
		assertTrue(createProperty7inB.hasActionExecutionSucceeded());
		assertNotNull(property7InB = (PrimitiveRole<String>) createProperty7inB.getNewFlexoRole());

		assertEquals(3, flexoConceptB.getFlexoProperties().size());
		assertEquals(3, flexoConceptB.getDeclaredProperties().size());
		assertEquals(7, flexoConceptB.getAccessibleProperties().size());

		assertTrue(flexoConceptB.getDeclaredProperties().contains(createProperty2inB.getNewFlexoProperty()));
		assertTrue(flexoConceptB.getDeclaredProperties().contains(createProperty3inB.getNewFlexoProperty()));
		assertTrue(flexoConceptB.getDeclaredProperties().contains(createProperty7inB.getNewFlexoProperty()));

		assertSame(property2InB, flexoConceptB.getAccessibleProperty("property2"));
		assertEquals(Boolean.class, property2InB.getType());
		assertEquals(Boolean.class, property2InB.getResultingType());
		assertSameList(property2InB.getSuperProperties(), property2InA);
		assertSameList(property2InB.getAllSuperProperties(), property2InA);

		assertSame(property3InB, flexoConceptB.getAccessibleProperty("property3"));
		assertEquals(Integer.class, property3InB.getType());
		assertEquals(Integer.class, property3InB.getResultingType());
		assertSameList(property3InB.getSuperProperties(), property3InA);
		assertSameList(property3InB.getAllSuperProperties(), property3InA);

		assertSame(property7InB, flexoConceptB.getAccessibleProperty("property7"));
		assertEquals(String.class, property7InB.getType());
		assertEquals(String.class, property7InB.getResultingType());
		assertEquals(0, property7InB.getSuperProperties().size());
		assertEquals(0, property7InB.getAllSuperProperties().size());

		// Because concept define some abstract properties, it is abstract
		assertTrue(flexoConceptB.isAbstract());

		// We try to force to make it non abstract, and check that it is still abstract
		flexoConceptB.setAbstract(false);
		assertTrue(flexoConceptB.isAbstract());

		System.out.println("FML=" + virtualModel.getFMLRepresentation());

		((VirtualModelResource) virtualModel.getResource()).save(null);

	}

	/**
	 * Test FlexoConceptC creation, define some overriden properties
	 */
	@Test
	@TestOrder(6)
	public void testCreateFlexoConceptC() throws SaveResourceException {

		log("testCreateFlexoConceptC()");

		CreateFlexoConcept addConceptC = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptC.setNewFlexoConceptName("FlexoConceptC");
		addConceptC.doAction();

		flexoConceptC = addConceptC.getNewFlexoConcept();

		flexoConceptC.addToParentFlexoConcepts(flexoConceptA);

		System.out.println("flexoConceptC = " + flexoConceptC);
		assertNotNull(flexoConceptC);

		CreateFlexoRole createProperty4InC = CreateFlexoRole.actionType.makeNewAction(flexoConceptC, null, editor);
		createProperty4InC.setRoleName("property4");
		createProperty4InC.setFlexoRoleClass(FlexoConceptInstanceRole.class);
		createProperty4InC.setFlexoConceptInstanceType(flexoConceptA);
		createProperty4InC.setPropertyCardinality(PropertyCardinality.ZeroOne);
		createProperty4InC.doAction();
		assertTrue(createProperty4InC.hasActionExecutionSucceeded());
		assertNotNull(property4InC = (FlexoConceptInstanceRole) createProperty4InC.getNewFlexoRole());

		CreateFlexoRole createProperty8InC = CreateFlexoRole.actionType.makeNewAction(flexoConceptC, null, editor);
		createProperty8InC.setRoleName("property8");
		createProperty8InC.setFlexoRoleClass(PrimitiveRole.class);
		createProperty8InC.setPrimitiveType(PrimitiveType.String);
		createProperty8InC.setPropertyCardinality(PropertyCardinality.One);
		createProperty8InC.doAction();
		assertTrue(createProperty8InC.hasActionExecutionSucceeded());
		assertNotNull(property8InC = (PrimitiveRole<String>) createProperty8InC.getNewFlexoRole());

		assertEquals(2, flexoConceptC.getFlexoProperties().size());
		assertEquals(2, flexoConceptC.getDeclaredProperties().size());
		assertEquals(7, flexoConceptC.getAccessibleProperties().size());

		assertTrue(flexoConceptC.getDeclaredProperties().contains(createProperty4InC.getNewFlexoProperty()));
		assertTrue(flexoConceptC.getDeclaredProperties().contains(createProperty8InC.getNewFlexoProperty()));

		assertSame(property4InC, flexoConceptC.getAccessibleProperty("property4"));
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA), property4InC.getType());
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA), property4InC.getResultingType());
		assertSameList(property4InC.getSuperProperties(), property4InA);
		assertSameList(property4InC.getAllSuperProperties(), property4InA);

		assertSame(property8InC, flexoConceptC.getAccessibleProperty("property8"));
		assertEquals(String.class, property8InC.getType());
		assertEquals(String.class, property8InC.getResultingType());
		assertEquals(0, property8InC.getSuperProperties().size());
		assertEquals(0, property8InC.getAllSuperProperties().size());

		// Because concept define some abstract properties, it is abstract
		assertTrue(flexoConceptC.isAbstract());

		// We try to force to make it non abstract, and check that it is still abstract
		flexoConceptC.setAbstract(false);
		assertTrue(flexoConceptC.isAbstract());

		System.out.println("FML=" + virtualModel.getFMLRepresentation());

		((VirtualModelResource) virtualModel.getResource()).save(null);

	}

	/**
	 * Test FlexoConceptC creation, inheriting from both B and C, and define some overriden properties
	 */
	@Test
	@TestOrder(7)
	public void testCreateFlexoConceptD() throws SaveResourceException {

		log("testCreateFlexoConceptD()");

		CreateFlexoConcept addConceptD = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptD.setNewFlexoConceptName("FlexoConceptD");
		addConceptD.doAction();

		flexoConceptD = addConceptD.getNewFlexoConcept();

		flexoConceptD.addToParentFlexoConcepts(flexoConceptB);
		flexoConceptD.addToParentFlexoConcepts(flexoConceptC);

		System.out.println("flexoConceptD = " + flexoConceptD);
		assertNotNull(flexoConceptD);

		CreateFlexoRole createProperty1InD = CreateFlexoRole.actionType.makeNewAction(flexoConceptD, null, editor);
		createProperty1InD.setRoleName("property1");
		createProperty1InD.setFlexoRoleClass(PrimitiveRole.class);
		createProperty1InD.setPrimitiveType(PrimitiveType.String);
		createProperty1InD.setPropertyCardinality(PropertyCardinality.One);
		createProperty1InD.doAction();
		assertTrue(createProperty1InD.hasActionExecutionSucceeded());
		assertNotNull(property1InD = (PrimitiveRole<String>) createProperty1InD.getNewFlexoRole());

		CreateExpressionProperty createProperty3InD = CreateExpressionProperty.actionType.makeNewAction(flexoConceptD, null, editor);
		createProperty3InD.setPropertyName("property3");
		createProperty3InD.setExpression(new DataBinding<Integer>("property1.length"));
		createProperty3InD.doAction();
		assertTrue(createProperty3InD.hasActionExecutionSucceeded());
		assertNotNull(property3InD = (ExpressionProperty<Integer>) createProperty3InD.getNewFlexoProperty());

		CreateFlexoRole createProperty5InD = CreateFlexoRole.actionType.makeNewAction(flexoConceptD, null, editor);
		createProperty5InD.setRoleName("property5");
		createProperty5InD.setFlexoRoleClass(PrimitiveRole.class);
		createProperty5InD.setPrimitiveType(PrimitiveType.String);
		createProperty5InD.setPropertyCardinality(PropertyCardinality.One);
		createProperty5InD.doAction();
		assertTrue(createProperty5InD.hasActionExecutionSucceeded());
		assertNotNull(property5InD = (PrimitiveRole<String>) createProperty5InD.getNewFlexoRole());

		CreateFlexoRole createProperty9InD = CreateFlexoRole.actionType.makeNewAction(flexoConceptD, null, editor);
		createProperty9InD.setRoleName("property9");
		createProperty9InD.setFlexoRoleClass(PrimitiveRole.class);
		createProperty9InD.setPrimitiveType(PrimitiveType.String);
		createProperty9InD.setPropertyCardinality(PropertyCardinality.One);
		createProperty9InD.doAction();
		assertTrue(createProperty9InD.hasActionExecutionSucceeded());
		assertNotNull(property9InD = (PrimitiveRole<String>) createProperty9InD.getNewFlexoRole());

		assertEquals(4, flexoConceptD.getFlexoProperties().size());
		assertEquals(4, flexoConceptD.getDeclaredProperties().size());

		assertEquals(9, flexoConceptD.getAccessibleProperties().size());

		assertTrue(flexoConceptD.getDeclaredProperties().contains(createProperty1InD.getNewFlexoProperty()));
		assertTrue(flexoConceptD.getDeclaredProperties().contains(createProperty3InD.getNewFlexoProperty()));
		assertTrue(flexoConceptD.getDeclaredProperties().contains(createProperty5InD.getNewFlexoProperty()));
		assertTrue(flexoConceptD.getDeclaredProperties().contains(createProperty9InD.getNewFlexoProperty()));

		assertSame(property1InD, flexoConceptD.getAccessibleProperty("property1"));
		assertEquals(String.class, property1InD.getType());
		assertEquals(String.class, property1InD.getResultingType());
		assertSameList(property1InD.getSuperProperties(), property1InA);

		assertSame(property2InB, flexoConceptD.getAccessibleProperty("property2"));
		assertSameList(property2InB.getSuperProperties(), property2InA);

		assertSame(property3InD, flexoConceptD.getAccessibleProperty("property3"));
		assertEquals(Integer.TYPE, property3InD.getType());
		assertEquals(Integer.TYPE, property3InD.getResultingType());
		assertSameList(property3InD.getSuperProperties(), property3InA, property3InB);

		assertSame(property4InC, flexoConceptD.getAccessibleProperty("property4"));
		assertSameList(property4InC.getSuperProperties(), property4InA);

		assertSame(property5InD, flexoConceptD.getAccessibleProperty("property5"));
		assertSameList(property5InD.getSuperProperties(), property5InA);

		assertSame(property6InA, flexoConceptD.getAccessibleProperty("property6"));
		assertEquals(0, property6InA.getSuperProperties().size());

		assertSame(property7InB, flexoConceptD.getAccessibleProperty("property7"));
		assertEquals(0, property7InB.getSuperProperties().size());

		assertSame(property8InC, flexoConceptD.getAccessibleProperty("property8"));
		assertEquals(0, property8InC.getSuperProperties().size());

		assertSame(property9InD, flexoConceptD.getAccessibleProperty("property9"));
		assertEquals(String.class, property9InD.getType());
		assertEquals(String.class, property9InD.getResultingType());
		assertEquals(0, property9InD.getSuperProperties().size());

		// Because concept define some abstract properties, it is abstract
		assertFalse(flexoConceptD.isAbstract());

		// We try to force to make it abstract
		flexoConceptD.setAbstract(true);
		assertTrue(flexoConceptD.isAbstract());

		flexoConceptD.setAbstract(false);

		System.out.println("FML=" + virtualModel.getFMLRepresentation());

		((VirtualModelResource) virtualModel.getResource()).save(null);

	}

	@Test
	@TestOrder(19)
	public void testViewPointIsValid() {

		assertViewPointIsValid(viewPoint);

		// We change the type of property3 in B with incompatible type and we check that an error occurs
		property3InB.setType(String.class);

		ValidationReport report = validate(virtualModel);
		assertEquals(2, report.getErrors().size());
		assertTrue(report.getErrors().get(0).getValidationRule() instanceof OverridenPropertiesMustBeTypeCompatible);
		assertTrue(report.getErrors().get(1).getValidationRule() instanceof OverridenPropertiesMustBeTypeCompatible);

		property3InB.setType(Integer.class);
		assertObjectIsValid(virtualModel);

	}
}
