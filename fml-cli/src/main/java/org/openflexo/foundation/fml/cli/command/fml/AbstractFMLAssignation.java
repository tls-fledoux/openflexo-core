/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.cli.command.fml;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingPath;
import org.openflexo.foundation.fml.cli.AbstractCommandSemanticsAnalyzer;
import org.openflexo.foundation.fml.cli.command.FMLCommandExecutionException;
import org.openflexo.foundation.fml.cli.command.FMLCommand;
import org.openflexo.foundation.fml.parser.node.ACommandAssign;
import org.openflexo.foundation.fml.parser.node.Node;
import org.openflexo.foundation.fml.parser.node.PCommandAssign;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;

/**
 * A FMLCommand which can be assigned
 * 
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractFMLAssignation.AbstractFMLAssignationImpl.class)
public interface AbstractFMLAssignation<N extends Node> extends FMLCommand<N> {

	public void create(N node, PCommandAssign assignNode, AbstractCommandSemanticsAnalyzer commandSemanticsAnalyzer);

	public static abstract class AbstractFMLAssignationImpl<N extends Node> extends FMLCommandImpl<N> implements AbstractFMLAssignation<N> {

		private static final Logger logger = Logger.getLogger(AbstractFMLAssignation.class.getPackage().getName());

		private DataBinding<?> assignation;

		// This is the local BindingModel augmented with new variable declaration if required
		private BindingModel bindingModel;
		private BindingVariable localDeclarationVariable;

		@Override
		public void create(N node, PCommandAssign assignNode, AbstractCommandSemanticsAnalyzer commandSemanticsAnalyzer) {
			performSuperInitializer(node, commandSemanticsAnalyzer);

			if (assignNode instanceof ACommandAssign) {
				ACommandAssign directiveAssign = (ACommandAssign) assignNode;
				assignation = retrieveAssignation(directiveAssign.getLeftHandSide());
				System.out.println("assignation=" + assignation);
			}
		}

		@Override
		public void init() {
			super.init();
			if (assignation != null) {
				System.out.println("On declare l'assignation de type : " + getAssignableType());
				assignation.setDeclaredType(getAssignableType());
				if (assignation.isNewVariableDeclaration()) {
					if (getParentCommand() != null) {
						bindingModel = new BindingModel(getBindingModel());
						localDeclarationVariable = new BindingVariable(getAssignationVariable(), getAssignableType());
						bindingModel.addToBindingVariables(localDeclarationVariable);
					}
					/*else {
						localDeclarationVariable = getCommandInterpreter().declareVariable(getAssignationVariable(), getAssignableType());
					}*/
				}
			}
		}

		public String getAssignToString() {
			if (assignation != null) {
				return assignation.toString() + " = ";
			}
			return "";
		}

		public String getAssignationVariable() {
			if (assignation.isSimpleVariable()) {
				BindingPath bindingPath = (BindingPath) assignation.getExpression();
				return bindingPath.getBindingVariable().getVariableName();
			}
			return null;
		}

		@Override
		public BindingModel getInferedBindingModel() {
			if (bindingModel != null) {
				return bindingModel;
			}
			return super.getInferedBindingModel();
		}

		public abstract Type getAssignableType();

		protected abstract Object performExecute() throws FMLCommandExecutionException;

		@Override
		public final Object execute() throws FMLCommandExecutionException {
			super.execute();
			output.clear();
			String cmdOutput;

			Object assignedValue = performExecute();

			if (assignation != null) {
				if (assignation.isValid()) {
					try {
						assignation.setBindingValue(assignedValue, getCommandInterpreter());
						cmdOutput = "Assigned " + assignedValue + " to " + assignation;

						output.add(cmdOutput);
						getOutStream().println(cmdOutput);
					} catch (TypeMismatchException e) {
						cmdOutput = "Cannot execute " + assignation;

						output.add(cmdOutput);
						throw new FMLCommandExecutionException(cmdOutput, e);
					} catch (NullReferenceException e) {
						cmdOutput = "Cannot execute " + assignation;

						output.add(cmdOutput);
						throw new FMLCommandExecutionException(cmdOutput, e);
					} catch (ReflectiveOperationException e) {
						cmdOutput = "Cannot execute " + assignation;

						output.add(cmdOutput);
						throw new FMLCommandExecutionException(cmdOutput, e);
					} catch (NotSettableContextException e) {
						cmdOutput = "Cannot execute " + assignation;

						output.add(cmdOutput);
						throw new FMLCommandExecutionException(cmdOutput, e);
					}
				}
				else if (assignation.isNewVariableDeclaration() || getParentCommand() == null) {
					if (localDeclarationVariable == null) {
						localDeclarationVariable = getCommandInterpreter().declareVariable(getAssignationVariable(), getAssignableType());
					}
					getCommandInterpreter().setVariableValue(localDeclarationVariable, assignedValue);
					cmdOutput = "Declared new variable " + localDeclarationVariable.getVariableName() + "=" + assignedValue;

					output.add(cmdOutput);
					getOutStream().println(cmdOutput);
				}
			}
			return assignedValue;

		}
	}
}
