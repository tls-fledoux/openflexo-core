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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.fml.action.CreateEditionAction;
import org.openflexo.foundation.fml.binding.FlexoBehaviourBindingModel;
import org.openflexo.foundation.fml.binding.ViewPointBindingModel;
import org.openflexo.foundation.fml.binding.VirtualModelBindingModel;
import org.openflexo.foundation.fml.controlgraph.Sequence;
import org.openflexo.foundation.fml.editionaction.DeclarationAction;
import org.openflexo.foundation.fml.editionaction.ExpressionAction;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * This unit test is intended to test Sequence manipulations
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestSequence extends OpenflexoTestCase {

	static FlexoEditor editor;

	static DeclarationAction declaration1, declaration2, declaration3, declaration4;

	static FlexoBehaviour behaviour;

	/**
	 * Instanciate compound test resource center
	 */
	@Test
	@TestOrder(1)
	public void testinstanciateTestServiceManager() {
		instanciateTestServiceManager();

		System.out.println("ServiceManager= " + serviceManager);
		assertNotNull(serviceManager);

		editor = new DefaultFlexoEditor(null, serviceManager);
		assertNotNull(editor);

	}

	/**
	 * Test the loading
	 */
	@Test
	@TestOrder(2)
	public void testLoadViewPoint() {

		ViewPointLibrary vpLib = serviceManager.getViewPointLibrary();

		System.out.println("VPLibrary=" + vpLib);
		assertNotNull(vpLib);

		System.out.println("All vp= " + vpLib.getViewPoints());

		assertEquals(0, vpLib.getLoadedViewPoints().size());

		ViewPoint viewPoint = vpLib.getViewPoint("http://openflexo.org/test/TestSequence");

		System.out.println("ViewPoint=" + viewPoint);

		assertNotNull(viewPoint);

		VirtualModel virtualModel = viewPoint.getVirtualModelNamed("VM");
		System.out.println("virtualModel=" + virtualModel);
		assertNotNull(virtualModel);

		behaviour = virtualModel.getDeclaredFlexoBehaviour("testBehaviour");
		System.out.println("behaviour=" + behaviour);
		assertNotNull(behaviour);

		assertTrue(behaviour.getControlGraph() instanceof Sequence);
		Sequence sequence1 = (Sequence) behaviour.getControlGraph();

		assertTrue(sequence1.getControlGraph1() instanceof DeclarationAction);
		declaration1 = (DeclarationAction) sequence1.getControlGraph1();

		assertTrue(sequence1.getControlGraph2() instanceof Sequence);
		Sequence sequence2 = (Sequence) sequence1.getControlGraph2();

		assertTrue(sequence2.getControlGraph1() instanceof DeclarationAction);
		declaration2 = (DeclarationAction) sequence2.getControlGraph1();

		assertTrue(sequence2.getControlGraph1() instanceof DeclarationAction);
		declaration3 = (DeclarationAction) sequence2.getControlGraph2();

		System.out.println("declaration1.BM=" + declaration1.getBindingModel());
		System.out.println("declaration2.BM=" + declaration2.getBindingModel());
		System.out.println("declaration3.BM=" + declaration3.getBindingModel());

		assertEquals(8, declaration1.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));

		assertEquals(9, declaration2.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed("variable1"));

		assertEquals(10, declaration3.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed("variable1"));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed("variable2"));
	}

	@Test
	@TestOrder(3)
	public void testAppendDeclaration() {

		CreateEditionAction createDeclaration4 = CreateEditionAction.actionType.makeNewAction(declaration1, null, editor);
		// createDeclarePatternRoleInCondition1.actionChoice =
		// CreateEditionActionChoice.BuiltInAction;
		createDeclaration4.setEditionActionClass(ExpressionAction.class);
		createDeclaration4.setDeclarationVariableName("variable4");

		System.out.println("--------------> OK, je rajoute la declaration");
		createDeclaration4.doAction();
		System.out.println("<-------------- C'est fait");

		declaration4 = (DeclarationAction) createDeclaration4.getNewEditionAction();
		((ExpressionAction) declaration4.getAssignableAction()).setExpression(new DataBinding<Object>("4"));

		System.out.println("FML=" + behaviour.getFMLRepresentation());

		assertTrue(behaviour.getControlGraph() instanceof Sequence);
		Sequence sequence1 = (Sequence) behaviour.getControlGraph();
		assertSame(declaration1, sequence1.getControlGraph1());

		assertTrue(sequence1.getControlGraph2() instanceof Sequence);
		Sequence sequence2 = (Sequence) sequence1.getControlGraph2();
		assertSame(declaration4, sequence2.getControlGraph1());

		assertTrue(sequence2.getControlGraph2() instanceof Sequence);
		Sequence sequence3 = (Sequence) sequence2.getControlGraph2();
		assertSame(declaration2, sequence3.getControlGraph1());
		assertSame(declaration3, sequence3.getControlGraph2());

		System.out.println("declaration1.BM=" + declaration1.getBindingModel());
		System.out.println("declaration4.BM=" + declaration2.getBindingModel());
		System.out.println("declaration2.BM=" + declaration2.getBindingModel());
		System.out.println("declaration3.BM=" + declaration3.getBindingModel());

		assertEquals(8, declaration1.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration1.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));

		assertEquals(9, declaration4.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));
		assertNotNull(declaration4.getBindingModel().bindingVariableNamed("variable1"));

		assertEquals(10, declaration2.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed("variable1"));
		assertNotNull(declaration2.getBindingModel().bindingVariableNamed("variable4"));

		assertEquals(11, declaration3.getBindingModel().getBindingVariablesCount());
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(VirtualModelBindingModel.REFLEXIVE_ACCESS_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.PROJECT_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.RC_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(ViewPointBindingModel.VIEW_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed(FlexoBehaviourBindingModel.PARAMETERS_DEFINITION_PROPERTY));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed("variable1"));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed("variable4"));
		assertNotNull(declaration3.getBindingModel().bindingVariableNamed("variable2"));

	}

}
