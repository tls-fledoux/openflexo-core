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

import org.openflexo.foundation.fml.JavaImportDeclaration;
import org.openflexo.foundation.fml.parser.FMLSemanticsAnalyzer;
import org.openflexo.foundation.fml.parser.node.ANamedJavaImportImportDeclaration;

/**
 * @author sylvain
 * 
 */
public class NamedJavaImportNode extends AbstractJavaImportNode<ANamedJavaImportImportDeclaration> {

	public NamedJavaImportNode(ANamedJavaImportImportDeclaration astNode, FMLSemanticsAnalyzer analyser) {
		super(astNode, analyser);
	}

	public NamedJavaImportNode(JavaImportDeclaration importDeclaration, FMLSemanticsAnalyzer analyser) {
		super(importDeclaration, analyser);
	}

	@Override
	public JavaImportDeclaration buildFMLObjectFromAST() {
		JavaImportDeclaration returned = super.buildFMLObjectFromAST();
		returned.setFullQualifiedClassName(
				makeFullQualifiedIdentifier(getASTNode().getIdentifier(), getASTNode().getAdditionalIdentifiers()));
		returned.setAbbrev(getASTNode().getName().getText());
		return returned;
	}

	@Override
	public String getNormalizedFMLRepresentation(PrettyPrintContext context) {
		return "import " + getFMLObject().getFullQualifiedClassName() + " as " + getFMLObject().getAbbrev() + ";";
	}

	@Override
	public String updateFMLRepresentation(PrettyPrintContext context) {

		System.out.println("********* updateFMLRepresentation for CompilationUnit " + getFMLObject());

		// Abnormal case: mode object is not defined
		if (getFMLObject() == null) {
			return getLastParsed();
		}

		/*List<FMLPrettyPrintable> childrenObjects = new ArrayList();
		childrenObjects.add(getFMLObject().getVirtualModel());
		return buildFMLRepresentation(childrenObjects, context);*/

		return getNormalizedFMLRepresentation(context);
	}

}
