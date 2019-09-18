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

package org.openflexo.foundation.fml.parser;

import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLCompilationUnitNode;
import org.openflexo.foundation.fml.parser.fmlnodes.FlexoConceptNode;
import org.openflexo.foundation.fml.parser.fmlnodes.JavaImportNode;
import org.openflexo.foundation.fml.parser.fmlnodes.MetaDataNode;
import org.openflexo.foundation.fml.parser.fmlnodes.NamedJavaImportNode;
import org.openflexo.foundation.fml.parser.fmlnodes.VirtualModelNode;
import org.openflexo.foundation.fml.parser.node.AAbstractPropertyInnerConceptDecl;
import org.openflexo.foundation.fml.parser.node.ABehaviourDeclarationInnerConceptDecl;
import org.openflexo.foundation.fml.parser.node.AConceptDecl;
import org.openflexo.foundation.fml.parser.node.AExpressionPropertyInnerConceptDecl;
import org.openflexo.foundation.fml.parser.node.AFmlCompilationUnit;
import org.openflexo.foundation.fml.parser.node.AGetDecl;
import org.openflexo.foundation.fml.parser.node.AGetSetPropertyInnerConceptDecl;
import org.openflexo.foundation.fml.parser.node.AJavaImportImportDecl;
import org.openflexo.foundation.fml.parser.node.AJavaInnerConceptDecl;
import org.openflexo.foundation.fml.parser.node.AModelDecl;
import org.openflexo.foundation.fml.parser.node.ANamedJavaImportImportDecl;
import org.openflexo.foundation.fml.parser.node.ASingleAnnotationAnnotation;
import org.openflexo.foundation.fml.parser.node.AValueAnnotationAnnotation;
import org.openflexo.foundation.fml.parser.node.Start;
import org.openflexo.p2pp.RawSource;

/**
 * This class implements the main semantics analyzer for a parsed FML compilation unit.<br>
 * 
 * @author sylvain
 * 
 */
public class MainSemanticsAnalyzer extends FMLSemanticsAnalyzer {

	private final TypeFactory typeFactory;
	private final FlexoPropertyFactory propertyFactory;
	private final FlexoBehaviourFactory behaviourFactory;

	// Raw source as when this analyzer was last parsed
	private RawSource rawSource;

	private FragmentManager fragmentManager;

	private FMLCompilationUnitNode compilationUnitNode;

	public MainSemanticsAnalyzer(FMLModelFactory factory, Start tree, RawSource rawSource) {
		super(factory, tree);
		this.rawSource = rawSource;
		fragmentManager = new FragmentManager(rawSource);
		typeFactory = new TypeFactory(this);
		propertyFactory = new FlexoPropertyFactory(this);
		behaviourFactory = new FlexoBehaviourFactory(this);
		if (tree != null) {
			tree.apply(this);
			finalizeDeserialization();
		}
	}

	@Override
	public MainSemanticsAnalyzer getMainAnalyzer() {
		return this;
	}

	@Override
	public Start getRootNode() {
		return (Start) super.getRootNode();
	}

	@Override
	public FragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public TypeFactory getTypeFactory() {
		return typeFactory;
	}

	public FlexoPropertyFactory getPropertyFactory() {
		return propertyFactory;
	}

	public FlexoBehaviourFactory getBehaviourFactory() {
		return behaviourFactory;
	}

	protected final void finalizeDeserialization() {
		compilationUnitNode.initializePrettyPrint(compilationUnitNode, compilationUnitNode.makePrettyPrintContext());
		typeFactory.resolveUnresovedTypes();
		finalizeDeserialization(compilationUnitNode);
	}

	public RawSource getRawSource() {
		return rawSource;
	}

	/*@Override
	public void defaultCase(Node node) {
		super.defaultCase(node);
		if (node instanceof Token && !fmlNodes.isEmpty()) {
			FMLObjectNode<?, ?> currentNode = fmlNodes.peek();
			if (currentNode != null) {
				// System.out.println("Token: " + ((Token) node).getText() + " de " + ((Token) node).getLine() + ":" + ((Token)
				// node).getPos()
				// + ":" + ((Token) node).getOffset());
				currentNode.handleToken((Token) node);
			}
		}
	}*/

	public FMLCompilationUnit getCompilationUnit() {
		return compilationUnitNode.getModelObject();
	}

	public FMLCompilationUnitNode getCompilationUnitNode() {
		return compilationUnitNode;
	}

	@Override
	public void inAFmlCompilationUnit(AFmlCompilationUnit node) {
		super.inAFmlCompilationUnit(node);
		push(compilationUnitNode = new FMLCompilationUnitNode(node, this));
	}

	@Override
	public void outAFmlCompilationUnit(AFmlCompilationUnit node) {
		super.outAFmlCompilationUnit(node);
		pop();
	}

	@Override
	public void inAJavaImportImportDecl(AJavaImportImportDecl node) {
		super.inAJavaImportImportDecl(node);
		push(new JavaImportNode(node, this));
	}

	@Override
	public void outAJavaImportImportDecl(AJavaImportImportDecl node) {
		super.outAJavaImportImportDecl(node);
		pop();
	}

	@Override
	public void inANamedJavaImportImportDecl(ANamedJavaImportImportDecl node) {
		super.inANamedJavaImportImportDecl(node);
		push(new NamedJavaImportNode(node, this));
	}

	@Override
	public void outANamedJavaImportImportDecl(ANamedJavaImportImportDecl node) {
		super.outANamedJavaImportImportDecl(node);
		pop();
	}

	@Override
	public void inAModelDecl(AModelDecl node) {
		super.inAModelDecl(node);
		push(new VirtualModelNode(node, this));
	}

	@Override
	public void outAModelDecl(AModelDecl node) {
		super.outAModelDecl(node);
		pop();
	}

	@Override
	public void inAConceptDecl(AConceptDecl node) {
		super.inAConceptDecl(node);
		push(new FlexoConceptNode(node, this));
	}

	@Override
	public void outAConceptDecl(AConceptDecl node) {
		super.outAConceptDecl(node);
		pop();
	}

	@Override
	public void inAAbstractPropertyInnerConceptDecl(AAbstractPropertyInnerConceptDecl node) {
		super.inAAbstractPropertyInnerConceptDecl(node);
		push(getPropertyFactory().makeAbstractPropertyNode(node));
	}

	@Override
	public void outAAbstractPropertyInnerConceptDecl(AAbstractPropertyInnerConceptDecl node) {
		super.outAAbstractPropertyInnerConceptDecl(node);
		pop();
	}

	@Override
	public void inAJavaInnerConceptDecl(AJavaInnerConceptDecl node) {
		super.inAJavaInnerConceptDecl(node);
		push(getPropertyFactory().makeBasicPropertyNode(node));
	}

	@Override
	public void outAJavaInnerConceptDecl(AJavaInnerConceptDecl node) {
		super.outAJavaInnerConceptDecl(node);
		pop();
	}

	@Override
	public void inAExpressionPropertyInnerConceptDecl(AExpressionPropertyInnerConceptDecl node) {
		super.inAExpressionPropertyInnerConceptDecl(node);
		push(getPropertyFactory().makeExpressionPropertyNode(node));
	}

	@Override
	public void outAExpressionPropertyInnerConceptDecl(AExpressionPropertyInnerConceptDecl node) {
		super.outAExpressionPropertyInnerConceptDecl(node);
		pop();
	}

	@Override
	public void inAGetSetPropertyInnerConceptDecl(AGetSetPropertyInnerConceptDecl node) {
		super.inAGetSetPropertyInnerConceptDecl(node);
		push(getPropertyFactory().makeGetSetPropertyNode(node));
	}

	@Override
	public void outAGetSetPropertyInnerConceptDecl(AGetSetPropertyInnerConceptDecl node) {
		super.outAGetSetPropertyInnerConceptDecl(node);
		pop();
	}

	@Override
	public void inAGetDecl(AGetDecl node) {
		// TODO Auto-generated method stub
		super.inAGetDecl(node);
	}

	@Override
	public void inABehaviourDeclarationInnerConceptDecl(ABehaviourDeclarationInnerConceptDecl node) {
		push(getBehaviourFactory().makeBehaviourNode(node.getBehaviourDecl()));
	}

	@Override
	public void outABehaviourDeclarationInnerConceptDecl(ABehaviourDeclarationInnerConceptDecl node) {
		pop();
	}

	@Override
	public void inASingleAnnotationAnnotation(ASingleAnnotationAnnotation node) {
		super.inASingleAnnotationAnnotation(node);
		// TODO
	}

	@Override
	public void outASingleAnnotationAnnotation(ASingleAnnotationAnnotation node) {
		super.outASingleAnnotationAnnotation(node);
		// TODO
	}

	@Override
	public void inAValueAnnotationAnnotation(AValueAnnotationAnnotation node) {
		super.inAValueAnnotationAnnotation(node);
		push(new MetaDataNode(node, this));
	}

	@Override
	public void outAValueAnnotationAnnotation(AValueAnnotationAnnotation node) {
		super.outAValueAnnotationAnnotation(node);
		pop();
	}

}
