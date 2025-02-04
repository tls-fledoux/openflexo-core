/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.rt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.PrimitiveType;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.ActionScheme;
import org.openflexo.foundation.fml.CreationScheme;
import org.openflexo.foundation.fml.DeletionScheme;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoBehaviourParameter;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.CreateGenericBehaviourParameter;
import org.openflexo.foundation.fml.action.CreatePrimitiveRole;
import org.openflexo.foundation.fml.controlgraph.EmptyControlGraph;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.fml.editionaction.FetchRequestCondition;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rm.CompilationUnitResourceFactory;
import org.openflexo.foundation.fml.rt.action.ActionSchemeAction;
import org.openflexo.foundation.fml.rt.action.ActionSchemeActionFactory;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreationSchemeAction;
import org.openflexo.foundation.fml.rt.action.DeletionSchemeAction;
import org.openflexo.foundation.fml.rt.action.DeletionSchemeActionFactory;
import org.openflexo.foundation.fml.rt.editionaction.SelectFlexoConceptInstance;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test the instantiation of a VirtualModel whose instances have {@link FMLControlledDiagramVirtualModelNature}
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestVirtualModelInstanceIndexes extends OpenflexoProjectAtRunTimeTestCase {

	static FlexoEditor editor;
	static VirtualModel viewPoint;
	static VirtualModel virtualModel;

	static FlexoConcept flexoConcept;
	static FlexoConcept flexoConceptA;

	public static final String VIEWPOINT_NAME = "TestViewPoint";
	public static final String VIEWPOINT_URI = "http://openflexo.org/test/TestResourceCenter/TestViewPoint.fml";
	public static final String VIRTUAL_MODEL_NAME = "TestVirtualModel";

	private static DirectoryResourceCenter resourceCenter;

	private static FlexoProject<File> project;
	private static FMLRTVirtualModelInstance newView;
	private static FMLRTVirtualModelInstance newVirtualModelInstance;

	private static FlexoConceptInstance john;
	private static FlexoConceptInstance mary;
	private static FlexoConceptInstance jacky1;
	private static FlexoConceptInstance jacky2;
	private static FlexoConceptInstance junior;
	private static FlexoConceptInstance sofia;

	/**
	 * Test the VP creation
	 * 
	 * @throws ModelDefinitionException
	 * @throws SaveResourceException
	 * @throws IOException
	 */
	@Test
	@TestOrder(1)
	public void testCreateViewPoint() throws SaveResourceException, ModelDefinitionException, IOException {
		instanciateTestServiceManager();

		resourceCenter = makeNewDirectoryResourceCenter();
		assertNotNull(resourceCenter);
		System.out.println("ResourceCenter= " + resourceCenter);

		FMLTechnologyAdapter fmlTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		CompilationUnitResourceFactory factory = fmlTechnologyAdapter.getCompilationUnitResourceFactory();

		CompilationUnitResource newVirtualModelResource = factory.makeTopLevelCompilationUnitResource(VIEWPOINT_NAME, VIEWPOINT_URI,
				fmlTechnologyAdapter.getGlobalRepository(resourceCenter).getRootFolder(), true);
		viewPoint = newVirtualModelResource.getLoadedResourceData().getVirtualModel();

		assertTrue(viewPoint.getResource().getDirectory() != null);
		assertTrue(viewPoint.getResource().getIODelegate().exists());

		assertEquals(viewPoint, viewPoint.getFlexoConcept());
	}

	/**
	 * Test the VirtualModel creation
	 * 
	 * @throws ModelDefinitionException
	 */
	@Test
	@TestOrder(2)
	public void testCreateVirtualModel() throws SaveResourceException, ModelDefinitionException {

		FMLTechnologyAdapter fmlTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		CompilationUnitResourceFactory factory = fmlTechnologyAdapter.getCompilationUnitResourceFactory();
		CompilationUnitResource newVMResource = factory.makeContainedCompilationUnitResource(VIRTUAL_MODEL_NAME, viewPoint.getResource(),
				true);
		virtualModel = newVMResource.getLoadedResourceData().getVirtualModel();

		assertTrue(ResourceLocator.retrieveResourceAsFile(virtualModel.getResource().getDirectory()).exists());
		assertTrue(virtualModel.getResource().getIODelegate().exists());

		assertEquals(viewPoint, virtualModel.getContainerVirtualModel());
		assertEquals(virtualModel, virtualModel.getDeclaringCompilationUnit().getVirtualModel());
		// assertEquals(null, newVirtualModel.getOwningVirtualModel());

		assertEquals(viewPoint, virtualModel.getContainerVirtualModel());

		assertEquals(virtualModel, virtualModel.getFlexoConcept());
	}

	/**
	 * Test the FlexoConcept creation
	 */
	@Test
	@TestOrder(3)
	public void testCreateEditor() {
		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);
	}

	/**
	 * Test the FlexoConcept creation
	 */
	@Test
	@TestOrder(4)
	public void testCreateFlexoConcept() throws SaveResourceException {

		CreateFlexoConcept addEP = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addEP.setNewFlexoConceptName("FlexoConcept");

		addEP.doAction();

		flexoConcept = addEP.getNewFlexoConcept();

		assertNotNull(flexoConcept);

		assertEquals(virtualModel, flexoConcept.getOwner());
		assertEquals(virtualModel, flexoConcept.getOwningVirtualModel());
		assertEquals(flexoConcept, flexoConcept.getFlexoConcept());
		assertEquals(virtualModel, flexoConcept.getResourceData().getVirtualModel());

		CreatePrimitiveRole createFirstNameRoleInFlexoConcept = CreatePrimitiveRole.actionType.makeNewAction(flexoConcept, null, editor);
		createFirstNameRoleInFlexoConcept.setRoleName("firstName");
		createFirstNameRoleInFlexoConcept.setPrimitiveType(PrimitiveType.String);
		createFirstNameRoleInFlexoConcept.doAction();

		CreatePrimitiveRole createLastNameRoleInFlexoConcept = CreatePrimitiveRole.actionType.makeNewAction(flexoConcept, null, editor);
		createLastNameRoleInFlexoConcept.setRoleName("lastName");
		createLastNameRoleInFlexoConcept.setPrimitiveType(PrimitiveType.String);
		createLastNameRoleInFlexoConcept.doAction();

		CreatePrimitiveRole createAgeRoleInFlexoConcept = CreatePrimitiveRole.actionType.makeNewAction(flexoConcept, null, editor);
		createAgeRoleInFlexoConcept.setRoleName("age");
		createAgeRoleInFlexoConcept.setPrimitiveType(PrimitiveType.Integer);
		createAgeRoleInFlexoConcept.doAction();

		assertEquals(3, flexoConcept.getFlexoProperties().size());

		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(flexoConcept, null, editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.doAction();
		CreationScheme creationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		CreateGenericBehaviourParameter createParameter1 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				editor);
		createParameter1.setParameterType(String.class);
		createParameter1.setParameterName("aFirstName");
		createParameter1.doAction();
		FlexoBehaviourParameter firstNameParam = createParameter1.getNewParameter();
		assertNotNull(firstNameParam);

		CreateGenericBehaviourParameter createParameter2 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				editor);
		createParameter2.setParameterType(String.class);
		createParameter2.setParameterName("aLastName");
		createParameter2.doAction();
		FlexoBehaviourParameter lastNameParam = createParameter2.getNewParameter();
		assertNotNull(lastNameParam);

		CreateGenericBehaviourParameter createParameter3 = CreateGenericBehaviourParameter.actionType.makeNewAction(creationScheme, null,
				editor);
		createParameter3.setParameterType(String.class);
		createParameter3.setParameterName("anAge");
		createParameter3.doAction();
		FlexoBehaviourParameter ageParam = createParameter3.getNewParameter();
		assertNotNull(ageParam);

		assertTrue(creationScheme.getParameters().contains(firstNameParam));
		assertTrue(creationScheme.getParameters().contains(lastNameParam));
		assertTrue(creationScheme.getParameters().contains(ageParam));

		CreateEditionAction createEditionAction1 = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				editor);
		createEditionAction1.setEditionActionClass(ExpressionAction.class);
		createEditionAction1.setAssignation(new DataBinding<>("firstName"));
		createEditionAction1.doAction();
		AssignationAction<?> action1 = (AssignationAction<?>) createEditionAction1.getNewEditionAction();
		((ExpressionAction<?>) action1.getAssignableAction()).setExpression(new DataBinding<>("parameters.aFirstName"));

		CreateEditionAction createEditionAction2 = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				editor);
		createEditionAction2.setEditionActionClass(ExpressionAction.class);
		createEditionAction2.setAssignation(new DataBinding<>("lastName"));
		createEditionAction2.doAction();
		AssignationAction<?> action2 = (AssignationAction<?>) createEditionAction2.getNewEditionAction();
		((ExpressionAction<?>) action2.getAssignableAction()).setExpression(new DataBinding<>("parameters.aLastName"));

		CreateEditionAction createEditionAction3 = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				editor);
		createEditionAction3.setEditionActionClass(ExpressionAction.class);
		createEditionAction3.setAssignation(new DataBinding<>("age"));
		createEditionAction3.doAction();
		AssignationAction<?> action3 = (AssignationAction<?>) createEditionAction3.getNewEditionAction();
		((ExpressionAction<?>) action3.getAssignableAction()).setExpression(new DataBinding<>("parameters.anAge"));

		CreateFlexoBehaviour createDeletionScheme = CreateFlexoBehaviour.actionType.makeNewAction(flexoConcept, null, editor);
		createDeletionScheme.setFlexoBehaviourClass(DeletionScheme.class);
		createDeletionScheme.doAction();
		DeletionScheme deletionScheme = (DeletionScheme) createDeletionScheme.getNewFlexoBehaviour();
		assertTrue(deletionScheme.getControlGraph() instanceof EmptyControlGraph);

		virtualModel.getResource().save();

		// System.out.println("Saved: " + ((VirtualModelResource)
		// newVirtualModel.getResource()).getFile());
		System.out.println("Saved: " + virtualModel.getResource().getIODelegate().toString());

	}

	/**
	 * Test the FlexoConcept creation
	 */
	@Test
	@TestOrder(4)
	public void testCreateFlexoConceptA() throws SaveResourceException {

		CreateFlexoConcept addEP = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addEP.setNewFlexoConceptName("FlexoConceptA");

		addEP.doAction();

		flexoConceptA = addEP.getNewFlexoConcept();

		assertNotNull(flexoConceptA);

		assertEquals(virtualModel, flexoConceptA.getOwner());
		assertEquals(virtualModel, flexoConceptA.getOwningVirtualModel());
		assertEquals(flexoConceptA, flexoConceptA.getFlexoConcept());
		assertEquals(virtualModel, flexoConceptA.getResourceData().getVirtualModel());

		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(flexoConceptA, null, editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.doAction();
		CreationScheme creationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();
		assertTrue(creationScheme.getControlGraph() instanceof EmptyControlGraph);

		CreateFlexoBehaviour createDeletionScheme = CreateFlexoBehaviour.actionType.makeNewAction(flexoConceptA, null, editor);
		createDeletionScheme.setFlexoBehaviourClass(DeletionScheme.class);
		createDeletionScheme.doAction();
		DeletionScheme deletionScheme = (DeletionScheme) createDeletionScheme.getNewFlexoBehaviour();
		assertTrue(deletionScheme.getControlGraph() instanceof EmptyControlGraph);

		virtualModel.getResource().save();

		// System.out.println("Saved: " + ((VirtualModelResource)
		// newVirtualModel.getResource()).getFile());
		System.out.println("Saved: " + virtualModel.getResource().getIODelegate().toString());

	}

	@Test
	@TestOrder(6)
	public void testCreateRequests() {

		makeRequest("selectLastName", "selected.lastName == \"Smith\"");
		makeRequest("selectFirstName", "\"John\" == selected.firstName");
		makeRequest("selectAge", "selected.age == 11");
		makeRequest("selectLastNamePlusAge", "selected.lastName+'-'+selected.age == \"Smith-43\"");
		makeRequest("selectLastNameAndAge", "selected.lastName == \"Smith\"", "selected.age==43");

		System.out.println("FML=\n" + virtualModel.getFMLPrettyPrint());

	}

	private static ActionScheme makeRequest(String requestName, String... conditionExpressions) {

		CreateFlexoBehaviour createActionScheme = CreateFlexoBehaviour.actionType.makeNewAction(virtualModel, null, editor);
		createActionScheme.setFlexoBehaviourClass(ActionScheme.class);
		createActionScheme.setFlexoBehaviourName(requestName);
		createActionScheme.doAction();
		ActionScheme request = (ActionScheme) createActionScheme.getNewFlexoBehaviour();

		CreateEditionAction createSelectFlexoConceptInstanceAction = CreateEditionAction.actionType.makeNewAction(request.getControlGraph(),
				null, editor);
		createSelectFlexoConceptInstanceAction.setEditionActionClass(SelectFlexoConceptInstance.class);
		createSelectFlexoConceptInstanceAction.setReturnStatement(true);

		createSelectFlexoConceptInstanceAction.doAction();

		SelectFlexoConceptInstance<?> selectFlexoConceptInstance = (SelectFlexoConceptInstance<?>) createSelectFlexoConceptInstanceAction
				.getBaseEditionAction();
		selectFlexoConceptInstance.setFlexoConceptType(flexoConcept);
		selectFlexoConceptInstance.setReceiver(new DataBinding<>("this"));
		selectFlexoConceptInstance.setContainer(new DataBinding<>("this"));

		for (String conditionExpression : conditionExpressions) {
			FetchRequestCondition condition = selectFlexoConceptInstance.createCondition();
			condition.setCondition(new DataBinding<Boolean>(conditionExpression));
		}

		return request;
	}

	@Test
	@TestOrder(7)
	public void testCreateProject() {
		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
	}

	/**
	 * Instantiate in project a View conform to the ViewPoint
	 */
	@Test
	@TestOrder(7)
	public void testCreateView() {
		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType
				.makeNewAction(project.getVirtualModelInstanceRepository().getRootFolder(), null, editor);
		action.setNewVirtualModelInstanceName("MyView");
		action.setNewVirtualModelInstanceTitle("Test creation of a new view");
		action.setVirtualModel(viewPoint);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		newView = action.getNewVirtualModelInstance();
		assertNotNull(newView);
		assertNotNull(newView.getResource());
		assertTrue(ResourceLocator.retrieveResourceAsFile(((FMLRTVirtualModelInstanceResource) newView.getResource()).getDirectory())
				.exists());
		assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());
	}

	/**
	 * Instantiate in project a FMLRTVirtualModelInstance conform to the VirtualModel
	 */
	@Test
	@TestOrder(8)
	public void testCreateVirtualModelInstance() {

		log("testCreateVirtualModelInstance()");

		CreateBasicVirtualModelInstance action = CreateBasicVirtualModelInstance.actionType.makeNewAction(newView, null, editor);
		action.setNewVirtualModelInstanceName("MyVirtualModelInstance");
		action.setNewVirtualModelInstanceTitle("Test creation of a new FMLRTVirtualModelInstance");
		action.setVirtualModel(virtualModel);

		action.doAction();

		if (!action.hasActionExecutionSucceeded()) {
			fail(action.getThrownException().getMessage());
		}

		assertTrue(action.hasActionExecutionSucceeded());
		newVirtualModelInstance = action.getNewVirtualModelInstance();
		assertNotNull(newVirtualModelInstance);
		assertNotNull(newVirtualModelInstance.getResource());
		assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());

		assertFalse(newVirtualModelInstance.isModified());

	}

	/**
	 * Try to populate FMLRTVirtualModelInstance
	 * 
	 * @throws SaveResourceException
	 */
	@Test
	@TestOrder(9)
	public void testPopulateVirtualModelInstance() throws SaveResourceException {

		log("testPopulateVirtualModelInstance()");

		FMLRTVirtualModelInstanceResource vmiRes = (FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource();
		assertFalse(newVirtualModelInstance.isModified());

		@SuppressWarnings("unused")
		FlexoConceptInstance a1 = createFlexoConceptAInstance();
		@SuppressWarnings("unused")
		FlexoConceptInstance a2 = createFlexoConceptAInstance();
		@SuppressWarnings("unused")
		FlexoConceptInstance a3 = createFlexoConceptAInstance();

		john = createInstance("John", "Smith", 43);
		mary = createInstance("Mary", "Smith", 43);
		jacky1 = createInstance("Jacky1", "Smith", 11);
		jacky2 = createInstance("Jacky2", "Smith", 11);
		junior = createInstance("Junior", "Smith", 7);

		System.out.println(vmiRes.getFactory().stringRepresentation(vmiRes.getLoadedResourceData()));

		assertTrue(serviceManager.getResourceManager().getUnsavedResources().contains(newVirtualModelInstance.getResource()));

		newVirtualModelInstance.getResource().save();
		assertTrue(((FMLRTVirtualModelInstanceResource) newVirtualModelInstance.getResource()).getIODelegate().exists());
		assertFalse(newVirtualModelInstance.isModified());

		assertEquals(5, newVirtualModelInstance.getFlexoConceptInstances(flexoConcept).size());
		assertEquals(3, newVirtualModelInstance.getFlexoConceptInstances(flexoConceptA).size());
	}

	private static FlexoConceptInstance createInstance(String firstName, String lastName, int age) {
		CreationScheme creationScheme = flexoConcept.getCreationSchemes().get(0);
		CreationSchemeAction action = new CreationSchemeAction(creationScheme, newVirtualModelInstance, null, editor);
		FlexoBehaviourParameter firstNameP = creationScheme.getParameter("aFirstName");
		FlexoBehaviourParameter lastNameP = creationScheme.getParameter("aLastName");
		FlexoBehaviourParameter ageP = creationScheme.getParameter("anAge");
		action.setParameterValue(firstNameP, firstName);
		action.setParameterValue(lastNameP, lastName);
		action.setParameterValue(ageP, age);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		return action.getFlexoConceptInstance();
	}

	private static FlexoConceptInstance createFlexoConceptAInstance() {
		CreationSchemeAction action = new CreationSchemeAction(flexoConceptA.getCreationSchemes().get(0), newVirtualModelInstance, null,
				editor);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		return action.getFlexoConceptInstance();
	}

	private static void deleteInstance(FlexoConceptInstance fci) {
		DeletionSchemeActionFactory actionType = new DeletionSchemeActionFactory(fci.getFlexoConcept().getDefaultDeletionScheme(), fci);
		DeletionSchemeAction action = actionType.makeNewAction(fci, null, editor);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
	}

	@Test
	@TestOrder(10)
	public void testIndexes1() {

		System.out.println("FML VP: " + viewPoint.getFMLPrettyPrint());
		System.out.println("FML VM: " + virtualModel.getFMLPrettyPrint());

		performRequest("selectFirstName", john);

	}

	@Test
	@TestOrder(11)
	public void testIndexes2() {

		performRequest("selectLastName", john, mary, jacky1, jacky2, junior);

	}

	@Test
	@TestOrder(12)
	public void testIndexes3() {

		performRequest("selectAge", jacky1, jacky2);

	}

	@Test
	@TestOrder(13)
	public void testIndexes4() {

		performRequest("selectLastNamePlusAge", john, mary);

	}

	@Test
	@TestOrder(14)
	public void testIndexes5() {

		performRequest("selectLastNameAndAge", john, mary);

	}

	private static void performRequest(String requestName, FlexoConceptInstance... expectedResult) {

		ActionScheme request1 = (ActionScheme) virtualModel.getFlexoBehaviour(requestName);
		ActionSchemeActionFactory request1AT = new ActionSchemeActionFactory(request1, newVirtualModelInstance);
		ActionSchemeAction action = request1AT.makeNewAction(newVirtualModelInstance, null, editor);

		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());

		List<FlexoConceptInstance> response = (List<FlexoConceptInstance>) action.getReturnedValue();

		assertSameList(response, expectedResult);

	}

	@Test
	@TestOrder(15)
	public void testRepopulateVirtualModelInstance() {

		log("testRepopulateVirtualModelInstance()");

		sofia = createInstance("Sofia", "Smith", 0);

		assertEquals(6, newVirtualModelInstance.getFlexoConceptInstances(flexoConcept).size());

		performRequest("selectLastName", john, mary, jacky1, jacky2, junior, sofia);

	}

	@Test
	@TestOrder(16)
	public void testModifySomeContents() {

		log("testModifySomeContents()");

		sofia.setFlexoPropertyValue((FlexoProperty<String>) flexoConcept.getAccessibleProperty("lastName"), "Wesson");

		performRequest("selectLastName", john, mary, jacky1, jacky2, junior);

	}

	@Test
	@TestOrder(17)
	public void testRemoveSomeContents() {

		log("testRemoveSomeContents()");
		deleteInstance(sofia);
		deleteInstance(junior);

		assertEquals(4, newVirtualModelInstance.getFlexoConceptInstances(flexoConcept).size());

		performRequest("selectLastName", john, mary, jacky1, jacky2);

	}

	/**
	 * Instantiate in project a FMLRTVirtualModelInstance conform to the VirtualModel
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	/*@Test
	@TestOrder(20)
	public void testReloadProject() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
	
		log("testReloadProject()");
	
		instanciateTestServiceManager();
	
		serviceManager.getResourceCenterService().addToResourceCenters(
				resourceCenter = new DirectoryResourceCenter(resourceCenter.getDirectory(), serviceManager.getResourceCenterService()));
	
		editor = reloadProject(project.getDirectory());
		project = editor.getProject();
		assertNotNull(editor);
		assertNotNull(project);
	
		System.out.println("All resources=" + project.getAllResources());
		assertEquals(2, project.getAllResources().size());
		assertNotNull(project.getResource(newView.getURI()));
	
		ViewResource newViewResource = project.getViewLibrary().getView(newView.getURI());
		assertNotNull(newViewResource);
		assertNull(newViewResource.getLoadedResourceData());
		newViewResource.loadResourceData(null);
		assertNotNull(newView = newViewResource.getView());
	
		assertEquals(1, newViewResource.getVirtualModelInstanceResources().size());
		VirtualModelInstanceResource vmiResource = newViewResource.getVirtualModelInstanceResources().get(0);
		assertNotNull(vmiResource);
		assertNull(vmiResource.getLoadedResourceData());
		vmiResource.loadResourceData(null);
		assertNotNull(newVirtualModelInstance = vmiResource.getVirtualModelInstance());
	
		assertEquals(5, newVirtualModelInstance.getFlexoConceptInstances().size());
		// FlexoConceptInstance fci = newVirtualModelInstance.getFlexoConceptInstances().get(0);
		// assertNotNull(fci);
	
		assertNotNull(flexoConcept = virtualModel.getFlexoConcept(flexoConcept.getURI()));
	
	}*/
}
