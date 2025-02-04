/**
 * 
 * Copyright (c) 2019, Openflexo
 * 
 * This file is part of FML-parser, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.parser.fmlnodes;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.BasicProperty;
import org.openflexo.foundation.fml.parser.FMLCompilationUnitSemanticsAnalyzer;
import org.openflexo.foundation.fml.parser.node.AIdentifierVariableDeclarator;
import org.openflexo.foundation.fml.parser.node.AInitializerExpressionVariableDeclarator;
import org.openflexo.foundation.fml.parser.node.AInitializerFmlActionVariableDeclarator;
import org.openflexo.foundation.fml.parser.node.AJavaInnerConceptDecl;
import org.openflexo.foundation.fml.parser.node.PVariableDeclarator;
import org.openflexo.p2pp.RawSource.RawSourceFragment;

/**
 * @author sylvain
 * 
 */
public abstract class BasicPropertyNode<T extends BasicProperty<?>> extends FlexoPropertyNode<AJavaInnerConceptDecl, T> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(BasicPropertyNode.class.getPackage().getName());

	public BasicPropertyNode(AJavaInnerConceptDecl astNode, FMLCompilationUnitSemanticsAnalyzer analyzer) {
		super(astNode, analyzer);
	}

	public BasicPropertyNode(T property, FMLCompilationUnitSemanticsAnalyzer analyzer) {
		super(property, analyzer);
	}

	protected RawSourceFragment getVisibilityFragment() {
		if (getASTNode() != null && getASTNode().getVisibility() != null) {
			return getFragment(getASTNode().getVisibility());
		}
		return null;
	}

	protected RawSourceFragment getCardinalityFragment() {
		if (getASTNode() != null && getASTNode().getCardinality() != null) {
			return getFragment(getASTNode().getCardinality());
		}
		return null;
	}

	protected RawSourceFragment getTypeFragment() {
		if (getASTNode() != null) {
			return getFragment(getASTNode().getType());
		}
		return null;
	}

	protected RawSourceFragment getNameFragment() {
		if (getASTNode() != null) {
			PVariableDeclarator variableDeclarator = getASTNode().getVariableDeclarator();
			if (variableDeclarator instanceof AIdentifierVariableDeclarator) {
				return getFragment(((AIdentifierVariableDeclarator) variableDeclarator).getLidentifier());
			}
			else if (variableDeclarator instanceof AInitializerExpressionVariableDeclarator) {
				return getFragment(((AInitializerExpressionVariableDeclarator) variableDeclarator).getLidentifier());
			}
			else if (variableDeclarator instanceof AInitializerFmlActionVariableDeclarator) {
				return getFragment(((AInitializerFmlActionVariableDeclarator) variableDeclarator).getLidentifier());
			}

		}
		return null;
	}

	protected RawSourceFragment getAssignFragment() {
		if (getASTNode() != null) {
			PVariableDeclarator variableDeclarator = getASTNode().getVariableDeclarator();
			if (variableDeclarator instanceof AIdentifierVariableDeclarator) {
				return null;
			}
			else if (variableDeclarator instanceof AInitializerExpressionVariableDeclarator) {
				return getFragment(((AInitializerExpressionVariableDeclarator) variableDeclarator).getAssign());
			}
			else if (variableDeclarator instanceof AInitializerFmlActionVariableDeclarator) {
				return getFragment(((AInitializerFmlActionVariableDeclarator) variableDeclarator).getAssign());
			}

		}
		return null;
	}

	protected RawSourceFragment getDefaultValueFragment() {
		if (getASTNode() != null) {
			PVariableDeclarator variableDeclarator = getASTNode().getVariableDeclarator();
			if (variableDeclarator instanceof AIdentifierVariableDeclarator) {
				return null;
			}
			else if (variableDeclarator instanceof AInitializerExpressionVariableDeclarator) {
				return getFragment(((AInitializerExpressionVariableDeclarator) variableDeclarator).getExpression());
			}
			else if (variableDeclarator instanceof AInitializerFmlActionVariableDeclarator) {
				return getFragment(((AInitializerFmlActionVariableDeclarator) variableDeclarator).getFmlActionExp());
			}

		}
		return null;
	}

	protected RawSourceFragment getSemiFragment() {
		if (getASTNode() != null) {
			return getFragment(getASTNode().getSemi());
		}
		return null;
	}

}
