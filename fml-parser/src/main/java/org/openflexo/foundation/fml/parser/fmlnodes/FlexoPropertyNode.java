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

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.type.CustomType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FlexoProperty;
import org.openflexo.foundation.fml.JavaImportDeclaration;
import org.openflexo.foundation.fml.parser.FMLObjectNode;
import org.openflexo.foundation.fml.parser.FMLSemanticsAnalyzer;
import org.openflexo.foundation.fml.parser.node.AIdentifierVariableDeclarator;
import org.openflexo.foundation.fml.parser.node.AInitializerVariableDeclarator;
import org.openflexo.foundation.fml.parser.node.Node;
import org.openflexo.foundation.fml.parser.node.PVariableDeclarator;
import org.openflexo.foundation.fml.parser.node.TIdentifier;

/**
 * @author sylvain
 * 
 */
public abstract class FlexoPropertyNode<N extends Node, T extends FlexoProperty<?>> extends FMLObjectNode<N, T> {

	private static final Logger logger = Logger.getLogger(FlexoPropertyNode.class.getPackage().getName());

	public FlexoPropertyNode(N astNode, FMLSemanticsAnalyzer analyser) {
		super(astNode, analyser);
	}

	public FlexoPropertyNode(T property, FMLSemanticsAnalyzer analyser) {
		super(property, analyser);
	}

	@Override
	public FlexoPropertyNode<N, T> deserialize() {
		if (getParent() instanceof VirtualModelNode) {
			((VirtualModelNode) getParent()).getFMLObject().addToFlexoProperties(getFMLObject());
		}
		return this;
	}

	protected TIdentifier getName(PVariableDeclarator variableDeclarator) {
		if (variableDeclarator instanceof AIdentifierVariableDeclarator) {
			return ((AIdentifierVariableDeclarator) variableDeclarator).getIdentifier();
		}
		if (variableDeclarator instanceof AInitializerVariableDeclarator) {
			return ((AInitializerVariableDeclarator) variableDeclarator).getIdentifier();
		}
		return null;
	}

	protected FMLCompilationUnit getCompilationUnit() {
		return getAnalyser().getCompilationUnit();
	}

	protected String serializeType(Type type, FMLCompilationUnit compilationUnit) {
		if (type instanceof CustomType) {
			// TODO: handle imports of required stuff
			return ((CustomType) type).simpleRepresentation();
		}
		else {
			Class<?> rawType = TypeUtils.getRawType(type);

			if (!TypeUtils.isPrimitive(rawType)) {

				boolean typeWasFound = false;
				for (JavaImportDeclaration importDeclaration : compilationUnit.getJavaImports()) {
					if (importDeclaration.getFullQualifiedClassName().equals(rawType.getName())) {
						typeWasFound = true;
						break;
					}
				}
				if (!typeWasFound) {
					System.out.println("Type pas trouve !, j'ajoute " + rawType.getName());
					JavaImportDeclaration newJavaImportDeclaration = getAnalyser().getFactory().newJavaImportDeclaration();
					newJavaImportDeclaration.setFullQualifiedClassName(rawType.getName());
					compilationUnit.addToJavaImports(newJavaImportDeclaration);
				}
				else {
					System.out.println("Type trouve !");
				}
			}

			return TypeUtils.simpleRepresentation(type);

		}
	}

}
