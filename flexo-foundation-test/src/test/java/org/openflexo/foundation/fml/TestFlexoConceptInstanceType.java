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

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.BindingModel;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.fml.action.CreateAbstractProperty;
import org.openflexo.foundation.fml.action.CreateFlexoConcept;
import org.openflexo.foundation.fml.action.CreateFlexoConceptInstanceRole;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rm.CompilationUnitResourceFactory;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.resource.DirectoryResourceCenter;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.test.OpenflexoProjectAtRunTimeTestCase;
import org.openflexo.pamela.exceptions.ModelDefinitionException;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;
import org.openflexo.toolbox.FileUtils;

/**
 * This unit test is intented to test {@link FlexoConceptInstanceType} management
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestFlexoConceptInstanceType extends OpenflexoProjectAtRunTimeTestCase {

	public static final String VIEWPOINT_NAME = "TestFlexoConceptInstanceViewPoint";
	public static final String VIEWPOINT_URI = "http://openflexo.org/test/TestResourceCenter/TestFlexoConceptInstanceViewPoint.fml";
	public static final String VIRTUAL_MODEL_NAME = "TestVirtualModel";

	static FlexoEditor editor;
	static VirtualModel viewPoint;
	static VirtualModel virtualModel;

	static FlexoConcept flexoConceptA;
	static FlexoConcept flexoConceptB;
	static FlexoConcept flexoConceptC;

	public static AbstractProperty<FlexoConceptInstanceType> property4InA;
	public static AbstractProperty<FlexoConceptInstanceType> property4InB;
	public static FlexoConceptInstanceRole property4InC;

	static FlexoProject<?> project;
	static FMLRTVirtualModelInstance newView;
	static FMLRTVirtualModelInstance vmi;
	static FlexoConceptInstance a;

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

		// viewPoint = ViewPointImpl.newViewPoint(VIEWPOINT_NAME, VIEWPOINT_URI,
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
		CompilationUnitResource newVMResource = factory.makeContainedCompilationUnitResource(VIRTUAL_MODEL_NAME, viewPoint.getResource(),
				true);
		virtualModel = newVMResource.getLoadedResourceData().getVirtualModel();

		// virtualModel = VirtualModelImpl.newVirtualModel(VIRTUAL_MODEL_NAME,
		// viewPoint);
		assertTrue(ResourceLocator.retrieveResourceAsFile(virtualModel.getResource().getDirectory()).exists());
		assertTrue(virtualModel.getResource().getIODelegate().exists());

		assertNotNull(virtualModel.getBindingModel());

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

		assertNotNull(flexoConceptA);

		CreateAbstractProperty createProperty4inA = CreateAbstractProperty.actionType.makeNewAction(flexoConceptA, null, editor);
		createProperty4inA.setPropertyName("property4");
		createProperty4inA.setPropertyType(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE);
		createProperty4inA.doAction();
		assertTrue(createProperty4inA.hasActionExecutionSucceeded());
		assertNotNull(property4InA = (AbstractProperty<FlexoConceptInstanceType>) createProperty4inA.getNewFlexoProperty());

		assertEquals(1, flexoConceptA.getFlexoProperties().size());
		assertEquals(1, flexoConceptA.getDeclaredProperties().size());
		assertEquals(1, flexoConceptA.getAccessibleProperties().size());
		assertTrue(flexoConceptA.getDeclaredProperties().contains(createProperty4inA.getNewFlexoProperty()));
		assertEquals(flexoConceptA.getDeclaredProperties(), flexoConceptA.getAccessibleProperties());

		assertSame(property4InA, flexoConceptA.getAccessibleProperty("property4"));
		assertEquals(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE, property4InA.getType());
		assertEquals(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE, property4InA.getResultingType());

		// Because concept define some abstract properties, abstract is required
		assertTrue(flexoConceptA.abstractRequired());

		System.out.println("FML=" + virtualModel.getFMLPrettyPrint());

		virtualModel.getResource().save();

	}

	/**
	 * Test FlexoConceptB creation, define some overriden properties
	 */
	@Test
	@TestOrder(5)
	public void testCreateFlexoConceptB() throws SaveResourceException, InconsistentFlexoConceptHierarchyException {

		log("testCreateFlexoConceptB()");

		CreateFlexoConcept addConceptB = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptB.setNewFlexoConceptName("FlexoConceptB");
		addConceptB.doAction();

		flexoConceptB = addConceptB.getNewFlexoConcept();

		flexoConceptB.addToParentFlexoConcepts(flexoConceptA);

		System.out.println("flexoConceptB = " + flexoConceptB);
		assertNotNull(flexoConceptB);

		CreateAbstractProperty createProperty4inB = CreateAbstractProperty.actionType.makeNewAction(flexoConceptB, null, editor);
		createProperty4inB.setPropertyName("property4");
		createProperty4inB.setPropertyType(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA));
		createProperty4inB.doAction();
		assertTrue(createProperty4inB.hasActionExecutionSucceeded());
		assertNotNull(property4InB = (AbstractProperty<FlexoConceptInstanceType>) createProperty4inB.getNewFlexoProperty());

		assertEquals(1, flexoConceptB.getFlexoProperties().size());
		assertEquals(1, flexoConceptB.getDeclaredProperties().size());
		assertEquals(1, flexoConceptB.getAccessibleProperties().size());

		assertTrue(flexoConceptB.getDeclaredProperties().contains(createProperty4inB.getNewFlexoProperty()));

		assertSame(property4InB, flexoConceptB.getAccessibleProperty("property4"));
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA), property4InB.getType());
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA), property4InB.getResultingType());
		assertSameList(property4InB.getSuperProperties(), property4InA);
		assertSameList(property4InB.getAllSuperProperties(), property4InA);

		// Because concept define some abstract properties, it is abstract
		assertTrue(flexoConceptB.abstractRequired());

		System.out.println("FML=" + virtualModel.getFMLPrettyPrint());

		virtualModel.getResource().save();

	}

	/**
	 * Test FlexoConceptC creation, define some overriden properties
	 */
	@Test
	@TestOrder(6)
	public void testCreateFlexoConceptC() throws SaveResourceException, InconsistentFlexoConceptHierarchyException {

		log("testCreateFlexoConceptC()");

		CreateFlexoConcept addConceptC = CreateFlexoConcept.actionType.makeNewAction(virtualModel, null, editor);
		addConceptC.setNewFlexoConceptName("FlexoConceptC");
		addConceptC.doAction();

		flexoConceptC = addConceptC.getNewFlexoConcept();

		flexoConceptC.addToParentFlexoConcepts(flexoConceptB);

		System.out.println("flexoConceptC = " + flexoConceptC);
		assertNotNull(flexoConceptC);

		CreateFlexoConceptInstanceRole createProperty4InC = CreateFlexoConceptInstanceRole.actionType.makeNewAction(flexoConceptC, null,
				editor);
		createProperty4InC.setRoleName("property4");
		createProperty4InC.setFlexoConceptInstanceType(flexoConceptB);
		createProperty4InC.setCardinality(PropertyCardinality.ZeroOne);
		createProperty4InC.doAction();
		assertTrue(createProperty4InC.hasActionExecutionSucceeded());
		assertNotNull(property4InC = createProperty4InC.getNewFlexoRole());

		assertEquals(1, flexoConceptC.getFlexoProperties().size());
		assertEquals(1, flexoConceptC.getDeclaredProperties().size());
		assertEquals(1, flexoConceptC.getAccessibleProperties().size());

		assertTrue(flexoConceptC.getDeclaredProperties().contains(createProperty4InC.getNewFlexoProperty()));

		assertSame(property4InC, flexoConceptC.getAccessibleProperty("property4"));
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptB), property4InC.getType());
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptB), property4InC.getResultingType());
		assertSameList(property4InC.getSuperProperties(), property4InB);
		assertSameList(property4InC.getAllSuperProperties(), property4InA, property4InB);

		// Because concept define no abstract properties, it is not abstract
		assertFalse(flexoConceptC.isAbstract());

		System.out.println("FML=" + virtualModel.getFMLPrettyPrint());

		virtualModel.getResource().save();

	}

	@Test
	@TestOrder(19)
	public void testViewPointIsValid() throws SaveResourceException {

		assertEquals(0, validate(viewPoint).getErrorsCount());
		assertEquals(2, validate(virtualModel).getErrorsCount());

		flexoConceptA.setAbstract(true);
		flexoConceptB.setAbstract(true);
		virtualModel.getResource().save();

		assertVirtualModelIsValid(viewPoint);
		assertVirtualModelIsValid(virtualModel);

	}

	/**
	 * Reload the ViewPoint<br>
	 * We first re-init a full ServiceManager, and copy the just created ViewPoint<br>
	 * The goal is to let the FileSystem monitoring system detects the new directory and instantiate ViewPoint
	 * 
	 * @throws IOException
	 */
	@Test
	@TestOrder(20)
	public void testReloadViewPoint() throws IOException {

		log("testReloadViewPoint()");

		CompilationUnitResource viewPointResource = viewPoint.getResource();

		instanciateTestServiceManager();
		resourceCenter = makeNewDirectoryResourceCenter();

		File directory = ResourceLocator.retrieveResourceAsFile(viewPointResource.getDirectory());
		File newDirectory = new File(((FileSystemBasedResourceCenter) resourceCenter).getRootDirectory(), directory.getName());
		newDirectory.mkdirs();

		try {
			FileUtils.copyContentDirToDir(directory, newDirectory);
			// We wait here for the thread monitoring ResourceCenters to detect
			// new files
			((FileSystemBasedResourceCenter) resourceCenter).performDirectoryWatchingNow();
		} catch (IOException e) {
			e.printStackTrace();
		}

		CompilationUnitResource retrievedVPResource = serviceManager.getVirtualModelLibrary().getCompilationUnitResource(VIEWPOINT_URI);
		assertNotNull(retrievedVPResource);

		VirtualModel reloadedViewPoint = retrievedVPResource.getCompilationUnit().getVirtualModel();
		assertEquals(reloadedViewPoint, reloadedViewPoint.getFlexoConcept());
		assertEquals(reloadedViewPoint, reloadedViewPoint.getResourceData().getVirtualModel());

		VirtualModel reloadedVirtualModel = reloadedViewPoint.getVirtualModelNamed(VIRTUAL_MODEL_NAME);
		assertNotNull(reloadedVirtualModel);

		assertNotNull(flexoConceptA = reloadedVirtualModel.getFlexoConcept("FlexoConceptA"));
		assertNotNull(flexoConceptB = reloadedVirtualModel.getFlexoConcept("FlexoConceptB"));
		assertNotNull(flexoConceptC = reloadedVirtualModel.getFlexoConcept("FlexoConceptC"));

		assertEquals(1, flexoConceptA.getFlexoProperties().size());
		assertEquals(1, flexoConceptA.getDeclaredProperties().size());
		assertEquals(1, flexoConceptA.getAccessibleProperties().size());

		assertNotNull(property4InA = (AbstractProperty<FlexoConceptInstanceType>) flexoConceptA.getAccessibleProperty("property4"));
		assertEquals(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE, property4InA.getType());
		assertEquals(FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE, property4InA.getResultingType());

		assertTrue(flexoConceptA.getDeclaredProperties().contains(property4InA));
		assertEquals(flexoConceptA.getDeclaredProperties(), flexoConceptA.getAccessibleProperties());

		// Because concept define some abstract properties, it is abstract
		assertTrue(flexoConceptA.abstractRequired());

		assertNotNull(property4InB = (AbstractProperty<FlexoConceptInstanceType>) flexoConceptB.getAccessibleProperty("property4"));

		assertEquals(1, flexoConceptB.getFlexoProperties().size());
		assertEquals(1, flexoConceptB.getDeclaredProperties().size());
		assertEquals(1, flexoConceptB.getAccessibleProperties().size());

		assertTrue(flexoConceptB.getDeclaredProperties().contains(property4InB));
		assertSame(property4InB, flexoConceptB.getAccessibleProperty("property4"));

		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA), property4InB.getType());
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptA), property4InB.getResultingType());
		assertSameList(property4InB.getSuperProperties(), property4InA);
		assertSameList(property4InB.getAllSuperProperties(), property4InA);

		// Because concept define some abstract properties, it is abstract
		// assertTrue(flexoConceptB.isAbstract());

		assertNotNull(property4InC = (FlexoConceptInstanceRole) flexoConceptC.getAccessibleProperty("property4"));

		assertEquals(1, flexoConceptC.getFlexoProperties().size());
		assertEquals(1, flexoConceptC.getDeclaredProperties().size());
		assertEquals(1, flexoConceptC.getAccessibleProperties().size());

		assertTrue(flexoConceptC.getDeclaredProperties().contains(property4InC));

		assertSame(property4InC, flexoConceptC.getAccessibleProperty("property4"));

		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptB), property4InC.getType());
		assertEquals(FlexoConceptInstanceType.getFlexoConceptInstanceType(flexoConceptB), property4InC.getResultingType());
		assertSameList(property4InC.getSuperProperties(), property4InB);
		assertSameList(property4InC.getAllSuperProperties(), property4InA, property4InB);

		// Because concept define no abstract properties, it is not abstract
		// assertFalse(flexoConceptC.isAbstract());

	}
}
