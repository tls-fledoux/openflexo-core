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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.ParameterizedTypeImpl;
import org.openflexo.connie.type.PrimitiveType;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.InvalidNameException;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.action.CreateFlexoBehaviour;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.CreateFlexoConceptInstanceRole;
import org.openflexo.foundation.fml.action.CreatePrimitiveRole;
import org.openflexo.foundation.fml.editionaction.AssignationAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rm.CompilationUnitResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.action.CreateBasicVirtualModelInstance;
import org.openflexo.foundation.fml.rt.action.CreationSchemeAction;
import org.openflexo.foundation.fml.rt.rm.FMLRTVirtualModelInstanceResource;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * This unit test is intented to test {@link FlexoRole} cardinality
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFlexoRoleCardinality extends OpenflexoProjectAtRunTimeTestCase {

	public static final String VIEWPOINT_NAME = "TestViewPoint";
	public static final String VIEWPOINT_URI = "http://openflexo.org/test/TestResourceCenter/TestViewPoint.fml";
	public static final String VIRTUAL_MODEL_NAME = "TestVirtualModel";

	static FlexoEditor editor;
	static VirtualModel viewPoint;
	static VirtualModel virtualModel;

	static FlexoConcept flexoConcept1;
	static FlexoConcept flexoConcept2;

	public static PrimitiveRole<String> aStringInA;
	public static PrimitiveRole<Boolean> someBooleanInA;
	public static PrimitiveRole<Integer> someIntegerInA;
	public static FlexoConceptInstanceRole someFlexoConcept2;

	static FlexoProject<File> project;
	static FMLRTVirtualModelInstance newView;
	static FMLRTVirtualModelInstance vmi;
	static FlexoConceptInstance fci;
	public static FlexoConceptInstance[] fci2;

	private static DirectoryResourceCenter resourceCenter;

	/**
	 * Init
	 * 
	 * @throws IOException
	 */
	@Test
	@TestOrder(1)
	public void init() throws IOException {
		instanciateTestServiceManager();

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

		resourceCenter = makeNewDirectoryResourceCenter();
		assertNotNull(resourceCenter);
		System.out.println("ResourceCenter= " + resourceCenter);
	}

	/**
	 * Test {@link VirtualModel} creation, check {@link BindingModel}
	 * 
	 * @throws ModelDefinitionException
	 * @throws SaveResourceException
	 */
	@Test
	@TestOrder(2)
	public void testCreateViewPoint() throws SaveResourceException, ModelDefinitionException {

		FMLTechnologyAdapter fmlTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		CompilationUnitResourceFactory factory = fmlTechnologyAdapter.getCompilationUnitResourceFactory();

		CompilationUnitResource newVirtualModelResource = factory.makeTopLevelCompilationUnitResource(VIEWPOINT_NAME, VIEWPOINT_URI,
				fmlTechnologyAdapter.getGlobalRepository(resourceCenter).getRootFolder(), true);
		viewPoint = newVirtualModelResource.getLoadedResourceData().getVirtualModel();

		// viewPoint = ViewPointImpl.newViewPoint("TestViewPoint",
		// "http://openflexo.org/test/TestViewPoint",
		// resourceCenter.getDirectory(),
		// serviceManager.getViewPointLibrary(), resourceCenter);
		// assertTrue(((VirtualModelResource)
		// viewPoint.getResource()).getDirectory().exists());
		// assertTrue(((VirtualModelResource)
		// viewPoint.getResource()).getFile().exists());
		assertTrue(viewPoint.getResource().getDirectory() != null);
		assertTrue(viewPoint.getResource().getIODelegate().exists());

		System.out.println("ViewPoint BindingModel = " + viewPoint.getBindingModel());
		assertNotNull(viewPoint.getBindingModel());

	}

	/**
	 * Test {@link VirtualModel} creation, check {@link BindingModel}
	 * 
	 * @throws ModelDefinitionException
	 */
	@Test
	@TestOrder(3)
	public void testCreateVirtualModel() throws SaveResourceException, ModelDefinitionException {

		FMLTechnologyAdapter fmlTechnologyAdapter = serviceManager.getTechnologyAdapterService()
				.getTechnologyAdapter(FMLTechnologyAdapter.class);
		CompilationUnitResourceFactory factory = fmlTechnologyAdapter.getCompilationUnitResourceFactory();
		CompilationUnitResource newVMResource = factory.makeContainedCompilationUnitResource("VM1", viewPoint.getResource(), true);
		virtualModel = newVMResource.getLoadedResourceData().getVirtualModel();

		// virtualModel = VirtualModelImpl.newVirtualModel("VM1", viewPoint);
		assertTrue(ResourceLocator.retrieveResourceAsFile(virtualModel.getResource().getDirectory()).exists());
		assertTrue(virtualModel.getResource().getIODelegate().exists());

		assertNotNull(virtualModel.getBindingModel());

	}

	/**
	 * Test FlexoConcept creation, check BindingModel
	 * 
	 * @throws InvalidNameException
	 */
	@Test
	@TestOrder(4)
	public void testCreateFlexoConcept() throws SaveResourceException, InvalidNameException {

		CreateFlexoConcept addEP1 = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addEP1.setNewFlexoConceptName("FlexoConcept1");
		addEP1.doAction();

		flexoConcept1 = addEP1.getNewFlexoConcept();

		System.out.println("flexoConcept1 = " + flexoConcept1);
		assertNotNull(flexoConcept1);

		CreateFlexoConcept addEP2 = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addEP2.setNewFlexoConceptName("FlexoConcept2");
		addEP2.doAction();

		flexoConcept2 = addEP2.getNewFlexoConcept();

		System.out.println("flexoConcept2 = " + flexoConcept2);
		assertNotNull(flexoConcept2);

		virtualModel.getResource().save();

		CreatePrimitiveRole createPR1 = CreatePrimitiveRole.actionType.makeNewAction(flexoConcept1, null, editor);
		createPR1.setRoleName("aStringInA");
		createPR1.setPrimitiveType(PrimitiveType.String);
		createPR1.setCardinality(PropertyCardinality.One);
		createPR1.doAction();

		CreatePrimitiveRole createPR2 = CreatePrimitiveRole.actionType.makeNewAction(flexoConcept1, null, editor);
		createPR2.setRoleName("someBooleanInA");
		createPR2.setPrimitiveType(PrimitiveType.Boolean);
		createPR2.setCardinality(PropertyCardinality.ZeroMany);
		createPR2.doAction();

		CreatePrimitiveRole createPR3 = CreatePrimitiveRole.actionType.makeNewAction(flexoConcept1, null, editor);
		createPR3.setRoleName("someIntegerInA");
		createPR3.setPrimitiveType(PrimitiveType.Integer);
		createPR3.setCardinality(PropertyCardinality.OneMany);
		createPR3.doAction();

		CreateFlexoConceptInstanceRole createPR4 = CreateFlexoConceptInstanceRole.actionType.makeNewAction(flexoConcept1, null, editor);
		createPR4.setRoleName("someFlexoConcept2");
		createPR4.setFlexoConceptInstanceType(flexoConcept2);
		createPR4.setCardinality(PropertyCardinality.ZeroMany);
		createPR4.doAction();

		assertEquals(4, flexoConcept1.getFlexoProperties().size());
		assertTrue(flexoConcept1.getFlexoProperties().contains(createPR1.getNewFlexoRole()));
		assertTrue(flexoConcept1.getFlexoProperties().contains(createPR2.getNewFlexoRole()));
		assertTrue(flexoConcept1.getFlexoProperties().contains(createPR3.getNewFlexoRole()));
		assertTrue(flexoConcept1.getFlexoProperties().contains(createPR4.getNewFlexoRole()));

		aStringInA = (PrimitiveRole<String>) flexoConcept1.getAccessibleProperty("aStringInA");
		assertNotNull(aStringInA);
		assertEquals(String.class, aStringInA.getType());
		assertEquals(String.class, aStringInA.getResultingType());
		someBooleanInA = (PrimitiveRole<Boolean>) flexoConcept1.getAccessibleProperty("someBooleanInA");
		assertNotNull(someBooleanInA);
		assertEquals(Boolean.TYPE, someBooleanInA.getType());
		assertEquals(new ParameterizedTypeImpl(List.class, Boolean.TYPE), someBooleanInA.getResultingType());
		someIntegerInA = (PrimitiveRole<Integer>) flexoConcept1.getAccessibleProperty("someIntegerInA");
		assertNotNull(someIntegerInA);
		assertEquals(Integer.TYPE, someIntegerInA.getType());
		assertEquals(new ParameterizedTypeImpl(List.class, Integer.TYPE), someIntegerInA.getResultingType());
		someFlexoConcept2 = (FlexoConceptInstanceRole) flexoConcept1.getAccessibleProperty("someFlexoConcept2");
		assertNotNull(someFlexoConcept2);

		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConcept2), someFlexoConcept2.getType());
		assertEquals(new ParameterizedTypeImpl(List.class, FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConcept2)),
				someFlexoConcept2.getResultingType());

		CreateFlexoBehaviour createCreationScheme = CreateFlexoBehaviour.actionType.makeNewAction(flexoConcept1, null, editor);
		createCreationScheme.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme.setFlexoBehaviourName("creationScheme");
		createCreationScheme.doAction();
		CreationScheme creationScheme = (CreationScheme) createCreationScheme.getNewFlexoBehaviour();

		CreateEditionAction createEditionAction1 = CreateEditionAction.actionType.makeNewAction(creationScheme.getControlGraph(), null,
				editor);
		// createEditionAction1.actionChoice =
		// CreateEditionActionChoice.BuiltInAction;
		createEditionAction1.setEditionActionClass(ExpressionAction.class);
		createEditionAction1.setAssignation(new DataBinding<>("aStringInA"));
		createEditionAction1.doAction();
		AssignationAction<?> action1 = (AssignationAction<?>) createEditionAction1.getNewEditionAction();
		((ExpressionAction<?>) action1.getAssignableAction()).setExpression(new DataBinding<>("\"foo\""));
		action1.setName("action1");

		assertTrue(action1.getAssignation().isValid());
		assertTrue(((ExpressionAction<?>) action1.getAssignableAction()).getExpression().isValid());

		assertTrue(flexoConcept1.getFlexoBehaviours().contains(creationScheme));
		assertTrue(flexoConcept1.getCreationSchemes().contains(creationScheme));

		// We create now an empty creation scheme for FlexoConcept 2
		CreateFlexoBehaviour createCreationScheme2 = CreateFlexoBehaviour.actionType.makeNewAction(flexoConcept2, null, editor);
		createCreationScheme2.setFlexoBehaviourClass(CreationScheme.class);
		createCreationScheme2.setFlexoBehaviourName("creationScheme");
		createCreationScheme2.doAction();
		CreationScheme creationScheme2 = (CreationScheme) createCreationScheme2.getNewFlexoBehaviour();
		assertTrue(flexoConcept2.getFlexoBehaviours().contains(creationScheme2));
		assertTrue(flexoConcept2.getCreationSchemes().contains(creationScheme2));

		System.out.println("FML=" + virtualModel.getFMLPrettyPrint());
	}

	@Test
	@TestOrder(19)
	public void testViewPointIsValid() {

		assertVirtualModelIsValid(viewPoint);

	}

	@Test
	@TestOrder(20)
	public void testInstanciateVirtualModelInstances() {

		log("testInstanciateVirtualModelInstances()");

		editor = createStandaloneProject("TestProject");
		project = (FlexoProject<File>) editor.getProject();
		System.out.println("Created project " + project.getProjectDirectory());
		assertTrue(project.getProjectDirectory().exists());
		assertTrue(project.getResource().getIODelegate().exists());

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
		try {
			newView.getResource().save();
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
		// assertTrue(((ViewResource)
		// newView.getResource()).getDirectory().exists());
		// assertTrue(((ViewResource)
		// newView.getResource()).getFile().exists());
		assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());

		assertNotNull(project.getResource(newView.getURI()));
		assertNotNull(project.getVirtualModelInstanceRepository().getResource(newView.getURI()));

		CreateBasicVirtualModelInstance createVMI = CreateBasicVirtualModelInstance.actionType.makeNewAction(newView, null, editor);
		createVMI.setNewVirtualModelInstanceName("MyVirtualModelInstance");
		createVMI.setNewVirtualModelInstanceTitle("Test creation of a new FMLRTVirtualModelInstance");
		createVMI.setVirtualModel(virtualModel);
		createVMI.doAction();
		assertTrue(createVMI.hasActionExecutionSucceeded());
		vmi = createVMI.getNewVirtualModelInstance();
		assertNotNull(vmi);
		assertNotNull(vmi.getResource());

		assertTrue(((FMLRTVirtualModelInstanceResource) newView.getResource()).getIODelegate().exists());
		assertEquals(virtualModel, vmi.getFlexoConcept());
		assertEquals(virtualModel, vmi.getVirtualModel());

	}

	@Test
	@TestOrder(21)
	public void testInstanciateFlexoConceptInstances() {

		log("testInstanciateFlexoConceptInstances()");

		CreationScheme creationScheme = flexoConcept1.getFlexoBehaviours(CreationScheme.class).get(0);
		assertNotNull(creationScheme);

		CreationSchemeAction creationSchemeCreationAction = new CreationSchemeAction(creationScheme, vmi, null, editor);
		assertNotNull(creationSchemeCreationAction);
		creationSchemeCreationAction.doAction();
		assertTrue(creationSchemeCreationAction.hasActionExecutionSucceeded());

		fci = creationSchemeCreationAction.getNewFlexoConceptInstance();
		assertNotNull(fci);

		assertEquals(flexoConcept1, fci.getFlexoConcept());

		CreationScheme creationScheme2 = flexoConcept2.getFlexoBehaviours(CreationScheme.class).get(0);
		assertNotNull(creationScheme2);

		fci2 = new FlexoConceptInstance[3];
		for (int i = 0; i < 3; i++) {
			CreationSchemeAction creationSchemeCreation2Action = new CreationSchemeAction(creationScheme2, vmi, null, editor);
			assertNotNull(creationSchemeCreation2Action);
			creationSchemeCreation2Action.doAction();
			assertTrue(creationSchemeCreation2Action.hasActionExecutionSucceeded());
			fci2[i] = creationSchemeCreation2Action.getNewFlexoConceptInstance();
			assertNotNull(fci2[i]);
			assertEquals(flexoConcept2, fci2[i].getFlexoConcept());
		}
	}

	@Test
	@TestOrder(22)
	public void testSingleCardinality() {

		log("testSingleCardinality()");

		assertEquals("foo", fci.getFlexoActor("aStringInA"));
		assertEquals("foo", fci.getFlexoActor(aStringInA));

		fci.setFlexoActor("anOtherValue", aStringInA);
		assertEquals("anOtherValue", fci.getFlexoActor(aStringInA));

		fci.setFlexoActor("value1", aStringInA);
		assertEquals("value1", fci.getFlexoActor(aStringInA));

		aStringInA.setCardinality(PropertyCardinality.ZeroMany);
		fci.addToFlexoActors("value2", aStringInA);
		assertSameList(fci.getFlexoActorList(aStringInA), "value1", "value2");

		fci.addToFlexoActors("value3", aStringInA);
		assertSameList(fci.getFlexoActorList(aStringInA), "value1", "value2", "value3");

		// System.out.println("actors= " + fci.getFlexoActorList(aStringInA));

		fci.setFlexoActor("value4", aStringInA);
		assertSameList(fci.getFlexoActorList(aStringInA), "value4");

		aStringInA.setCardinality(PropertyCardinality.ZeroOne);

		assertEquals("value4", fci.getFlexoActor(aStringInA));

		fci.nullifyFlexoActor(aStringInA);
		assertTrue(fci.getFlexoActorList(aStringInA).isEmpty());

		assertEquals(null, fci.getFlexoActor(aStringInA));

		fci.setFlexoActor("foo", aStringInA);

		assertEquals("foo", fci.getFlexoActor(aStringInA));

	}

	@Test
	@TestOrder(23)
	public void testMultipleCardinality() {

		log("testMultipleCardinality()");

		fci.addToFlexoActors(1, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 1);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

		fci.addToFlexoActors(2, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 1, 2);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

		fci.addToFlexoActors(3, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 1, 2, 3);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

		fci.removeFromFlexoActors(2, someIntegerInA);

		assertSameList(fci.getFlexoActorList(someIntegerInA), 1, 3);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

		fci.removeFromFlexoActors(1, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 3);
		assertEquals(3, (int) fci.getFlexoActor(someIntegerInA));

		fci.removeFromFlexoActors(3, someIntegerInA);
		assertTrue(fci.getFlexoActorList(someIntegerInA).isEmpty());
		assertEquals(null, fci.getFlexoActor(someIntegerInA));

		fci.addToFlexoActors(1, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 1);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

		fci.addToFlexoActors(2, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 1, 2);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

		fci.addToFlexoActors(3, someIntegerInA);
		assertSameList(fci.getFlexoActorList(someIntegerInA), 1, 2, 3);
		assertEquals(1, (int) fci.getFlexoActor(someIntegerInA));

	}

	@Test
	@TestOrder(24)
	public void testMultipleCardinality2() {

		log("testMultipleCardinality2()");

		fci.addToFlexoActors(fci2[0], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

		fci.addToFlexoActors(fci2[1], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0], fci2[1]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

		fci.addToFlexoActors(fci2[2], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0], fci2[1], fci2[2]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

		fci.removeFromFlexoActors(fci2[1], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0], fci2[2]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

		fci.removeFromFlexoActors(fci2[0], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[2]);
		assertEquals(fci2[2], fci.getFlexoActor(someFlexoConcept2));

		fci.removeFromFlexoActors(fci2[2], someFlexoConcept2);
		assertTrue(fci.getFlexoActorList(someFlexoConcept2).isEmpty());
		assertEquals(null, fci.getFlexoActor(someFlexoConcept2));

		fci.addToFlexoActors(fci2[0], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

		fci.addToFlexoActors(fci2[1], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0], fci2[1]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

		fci.addToFlexoActors(fci2[2], someFlexoConcept2);
		assertSameList(fci.getFlexoActorList(someFlexoConcept2), fci2[0], fci2[1], fci2[2]);
		assertEquals(fci2[0], fci.getFlexoActor(someFlexoConcept2));

	}

	@Test
	@TestOrder(30)
	public void saveVirtualModelInstance() {

		log("saveVirtualModelInstance()");

		System.out.println("Saving " + vmi.getResource().getIODelegate().stringRepresentation());

		System.out.println(((PamelaResource<?, ?>) vmi.getResource()).getFactory().stringRepresentation(vmi));

		try {
			vmi.getResource().save();
		} catch (SaveResourceException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Reload the project
	 * 
	 * @throws FlexoException
	 * @throws ResourceLoadingCancelledException
	 * @throws FileNotFoundException
	 */
	@Test
	@TestOrder(31)
	public void testReloadProject() throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {

		log("testReloadProject()");

		FlexoProject<File> oldProject = project;
		String oldViewURI = newView.getURI();
		editor = reloadProject(project);
		project = (FlexoProject<File>) editor.getProject();

		assertNotSame(oldProject, project);
		assertNotNull(editor);
		assertNotNull(project);
		FMLRTVirtualModelInstanceResource newViewResource = project.getVirtualModelInstanceRepository().getVirtualModelInstance(oldViewURI);
		assertNotNull(newViewResource);
		newViewResource.loadResourceData();
		assertNotNull(newViewResource.getLoadedResourceData());
		newView = newViewResource.getLoadedResourceData();

		System.out.println("All resources=" + project.getAllResources());
		assertNotNull(project.getResource(oldViewURI));

		FMLRTVirtualModelInstance reloadedVMI = (FMLRTVirtualModelInstance) newView.getVirtualModelInstance("MyVirtualModelInstance");
		assertNotNull(reloadedVMI);

		System.out.println(((PamelaResource<?, ?>) reloadedVMI.getResource()).getFactory().stringRepresentation(reloadedVMI));

		FlexoConceptInstance reloadedFCI = reloadedVMI.getFlexoConceptInstances(flexoConcept1).get(0);
		assertNotNull(reloadedFCI);
		assertEquals("foo", reloadedFCI.getFlexoActor(aStringInA));

		assertSameList(reloadedFCI.getFlexoActorList(someIntegerInA), 1, 2, 3);
		assertEquals(1, (int) reloadedFCI.getFlexoActor(someIntegerInA));

		FlexoConceptInstance[] reloadedFci2 = new FlexoConceptInstance[5];
		for (int i = 0; i < 3; i++) {
			reloadedFci2[i] = reloadedFCI.getFlexoActorList(someFlexoConcept2).get(i);
			assertNotNull(reloadedFci2[i]);
			assertEquals(flexoConcept2, reloadedFci2[i].getFlexoConcept());
		}

		assertSameList(reloadedFCI.getFlexoActorList(someFlexoConcept2), reloadedFci2[0], reloadedFci2[1], reloadedFci2[2]);
		assertEquals(reloadedFci2[0], reloadedFCI.getFlexoActor(someFlexoConcept2));

	}

}
