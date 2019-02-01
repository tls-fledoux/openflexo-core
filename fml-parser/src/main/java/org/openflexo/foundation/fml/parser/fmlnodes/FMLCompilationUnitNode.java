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

import java.util.ArrayList;
import java.util.List;

import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.JavaImportDeclaration;
import org.openflexo.foundation.fml.parser.FMLObjectNode;
import org.openflexo.foundation.fml.parser.FMLSemanticsAnalyzer;
import org.openflexo.foundation.fml.parser.node.AFmlCompilationUnit;

/**
 * @author sylvain
 * 
 */
public class FMLCompilationUnitNode extends FMLObjectNode<AFmlCompilationUnit, FMLCompilationUnit> {

	public FMLCompilationUnitNode(AFmlCompilationUnit astNode, FMLSemanticsAnalyzer analyser) {
		super(astNode, analyser);
	}

	@Override
	public FMLCompilationUnit buildFMLObjectFromAST() {
		return getFactory().newCompilationUnit();
	}

	@Override
	public FMLCompilationUnitNode deserialize() {
		return this;
	}

	@Override
	protected List<PrettyPrintableContents> preparePrettyPrint(PrettyPrintContext context) {
		List<PrettyPrintableContents> returned = new ArrayList<>();
		for (JavaImportDeclaration javaImportDeclaration : getFMLObject().getJavaImports()) {
			returned.add(new ChildContents("", javaImportDeclaration, "\n", context));
		}
		returned.add(new ChildContents("\n", getFMLObject().getVirtualModel(), "\n", context));
		return returned;
	}

	@Override
	public String updateFMLRepresentation(PrettyPrintContext context) {

		// System.out.println("********* updateFMLRepresentation for CompilationUnit " + getFMLObject());

		// Abnormal case: even the VirtualModel is not defined
		if (getFMLObject() == null || getFMLObject().getVirtualModel() == null) {
			return getLastParsed();
		}

		return updatePrettyPrintForChildren(context);
	}

	/**
	 * Return the number of the starting line (all line numbers start with 1), where underlying model object is textually serialized,
	 * inclusive
	 * 
	 * @return
	 */
	@Override
	public int getStartLine() {
		return 1;
	}

	/**
	 * Return the number of the starting char (starting at 1) in starting line, where underlying model object is textually serialized,
	 * inclusive
	 * 
	 * @return
	 */
	@Override
	public int getStartChar() {
		return 1;
	}

	/**
	 * Return the number of the ending line (all line numbers start with 1), where underlying model object is textually serialized,
	 * inclusive
	 * 
	 * @return
	 */
	@Override
	public int getEndLine() {
		return getRawSource().size();
	}

	/**
	 * Return the number of the ending char (starting at 1) in ending line, where underlying model object is textually serialized, inclusive
	 * 
	 * @return
	 */
	@Override
	public int getEndChar() {
		return getRawSource().get(getRawSource().size() - 1).length();
	}

}
