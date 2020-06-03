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

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.parser.analysis.DepthFirstAdapter;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLInstancePropertyValueNode;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLInstancesListPropertyValueNode;
import org.openflexo.foundation.fml.parser.fmlnodes.FMLSimplePropertyValueNode;
import org.openflexo.foundation.fml.parser.fmlnodes.WrappedFMLObjectNode;
import org.openflexo.foundation.fml.parser.node.AFullQualifiedQualifiedInstance;
import org.openflexo.foundation.fml.parser.node.AInstanceQualifiedArgument;
import org.openflexo.foundation.fml.parser.node.AListInstancesQualifiedArgument;
import org.openflexo.foundation.fml.parser.node.AMatchActionFmlActionExp;
import org.openflexo.foundation.fml.parser.node.ASimpleQualifiedArgument;
import org.openflexo.foundation.fml.parser.node.ASimpleQualifiedInstance;
import org.openflexo.foundation.fml.parser.node.Node;
import org.openflexo.foundation.fml.parser.node.Token;
import org.openflexo.p2pp.P2PPNode;
import org.openflexo.p2pp.RawSource;
import org.openflexo.p2pp.RawSource.RawSourceFragment;
import org.openflexo.toolbox.ChainedCollection;

/**
 * Base class implementing semantics analyzer, based on sablecc grammar visitor<br>
 * 
 * @author sylvain
 * 
 */
public abstract class FMLSemanticsAnalyzer extends DepthFirstAdapter {

	private FMLModelFactory factory;

	// Stack of FMLObjectNode beeing build during semantics analyzing
	protected Stack<FMLObjectNode<?, ?, ?>> fmlNodes = new Stack<>();

	private Node rootNode;

	public FMLSemanticsAnalyzer(FMLModelFactory factory, Node rootNode) {
		this.factory = factory;
		this.rootNode = rootNode;
	}

	public final FMLModelFactory getFactory() {
		return factory;
	}

	public final void setFactory(FMLModelFactory factory) {
		this.factory = factory;
	}

	public abstract MainSemanticsAnalyzer getMainAnalyzer();

	public Node getRootNode() {
		return rootNode;
	}

	public final FlexoServiceManager getServiceManager() {
		if (getFactory() != null) {
			return getFactory().getServiceManager();
		}
		return null;
	}

	protected final void finalizeDeserialization(FMLObjectNode<?, ?, ?> node) {
		node.finalizeDeserialization();
		for (P2PPNode<?, ?> child : node.getChildren()) {
			finalizeDeserialization((FMLObjectNode<?, ?, ?>) child);
		}
	}

	protected void push(FMLObjectNode<?, ?, ?> fmlNode) {
		if (!fmlNodes.isEmpty()) {
			FMLObjectNode<?, ?, ?> current = fmlNodes.peek();
			current.addToChildren(fmlNode);
		}
		fmlNodes.push(fmlNode);
	}

	protected <N extends FMLObjectNode<?, ?, ?>> N pop() {
		N builtFMLNode = (N) fmlNodes.pop();
		builtFMLNode.deserialize();
		// builtFMLNode.initializePrettyPrint();
		return builtFMLNode;
	}

	public FMLObjectNode<?, ?, ?> peek() {
		if (!fmlNodes.isEmpty()) {
			return fmlNodes.peek();
		}
		return null;
	}

	public FMLObjectNode<?, ?, ?> getCurrentNode() {
		return peek();
	}

	public FragmentManager getFragmentManager() {
		return getMainAnalyzer().getFragmentManager();
	}

	/**
	 * Return original version of last serialized raw source, FOR THE ENTIRE compilation unit
	 * 
	 * @return
	 */
	public RawSource getRawSource() {
		return getMainAnalyzer().getRawSource();
	}

	/**
	 * Return fragment matching supplied node in AST
	 * 
	 * @param token
	 * @return
	 */
	public RawSourceFragment getFragment(Node node) {
		if (node instanceof Token) {
			Token token = (Token) node;
			return getRawSource().makeFragment(getRawSource().makePositionBeforeChar(token.getLine(), token.getPos()),
					getRawSource().makePositionBeforeChar(token.getLine(), token.getPos() + token.getText().length()));
		}
		else {
			return getMainAnalyzer().getFragmentManager().retrieveFragment(node);
		}
	}

	/**
	 * Return fragment matching supplied nodes in AST
	 * 
	 * @param token
	 * @return
	 */
	public RawSourceFragment getFragment(Node node, Node otherNode) {
		return getFragment(node, Collections.singletonList(otherNode));
	}

	/**
	 * Return fragment matching supplied nodes in AST
	 * 
	 * @param token
	 * @return
	 */
	public RawSourceFragment getFragment(Node node, List<? extends Node> otherNodes) {
		ChainedCollection<Node> collection = new ChainedCollection<>();
		collection.add(node);
		collection.add(otherNodes);
		return getMainAnalyzer().getFragmentManager().getFragment(collection);
	}

	public String getText(Node node) {
		return getFragment(node).getRawText();
	}

	// Hack used to detect that we are not deserializing FML property values but a MatchingCriteria
	// MatchingCriterias must be replaced with MatchCondition as in InitiateMatching
	// TODO fix this hack
	protected boolean insideMatchAction = false;

	@Override
	public void inAMatchActionFmlActionExp(AMatchActionFmlActionExp node) {
		super.inAMatchActionFmlActionExp(node);
		insideMatchAction = true;
	}

	@Override
	public void outAMatchActionFmlActionExp(AMatchActionFmlActionExp node) {
		super.outAMatchActionFmlActionExp(node);
		insideMatchAction = false;
	}

	protected boolean handleFMLArgument() {
		return !insideMatchAction;
	}

	@Override
	public final void inASimpleQualifiedArgument(ASimpleQualifiedArgument node) {
		super.inASimpleQualifiedArgument(node);
		if (handleFMLArgument()) {
			System.out.println("ENTER in " + peek() + " with " + node);
			push(getMainAnalyzer().retrieveFMLNode(node, n -> new FMLSimplePropertyValueNode(n, getMainAnalyzer())));
		}
	}

	@Override
	public final void outASimpleQualifiedArgument(ASimpleQualifiedArgument node) {
		super.outASimpleQualifiedArgument(node);
		if (handleFMLArgument()) {
			pop();
			System.out.println("EXIT from " + peek() + " with " + node);
		}
	}

	@Override
	public final void inAInstanceQualifiedArgument(AInstanceQualifiedArgument node) {
		super.inAInstanceQualifiedArgument(node);
		if (handleFMLArgument()) {
			System.out.println("ENTER in " + peek() + " with " + node);
			push(getMainAnalyzer().retrieveFMLNode(node, n -> new FMLInstancePropertyValueNode(n, getMainAnalyzer())));
		}
	}

	@Override
	public final void outAInstanceQualifiedArgument(AInstanceQualifiedArgument node) {
		super.outAInstanceQualifiedArgument(node);
		if (handleFMLArgument()) {
			pop();
			System.out.println("EXIT from " + peek() + " with " + node);
		}
	}

	@Override
	public final void inAListInstancesQualifiedArgument(AListInstancesQualifiedArgument node) {
		super.inAListInstancesQualifiedArgument(node);
		if (handleFMLArgument()) {
			System.out.println("ENTER in " + peek() + " with " + node);
			push(getMainAnalyzer().retrieveFMLNode(node, n -> new FMLInstancesListPropertyValueNode(n, getMainAnalyzer())));
		}
	}

	@Override
	public final void outAListInstancesQualifiedArgument(AListInstancesQualifiedArgument node) {
		super.outAListInstancesQualifiedArgument(node);
		if (handleFMLArgument()) {
			pop();
			System.out.println("EXIT from " + peek() + " with " + node);
		}
	}

	@Override
	public final void inASimpleQualifiedInstance(ASimpleQualifiedInstance node) {
		super.inASimpleQualifiedInstance(node);
		if (handleFMLArgument()) {
			System.out.println("ENTER in " + peek() + " with " + node);
			push(getMainAnalyzer().retrieveFMLNode(node, n -> new WrappedFMLObjectNode(n, getMainAnalyzer())));
		}
	}

	@Override
	public final void outASimpleQualifiedInstance(ASimpleQualifiedInstance node) {
		super.outASimpleQualifiedInstance(node);
		if (handleFMLArgument()) {
			pop();
			System.out.println("EXIT from " + peek() + " with " + node);
		}
	}

	@Override
	public final void inAFullQualifiedQualifiedInstance(AFullQualifiedQualifiedInstance node) {
		super.inAFullQualifiedQualifiedInstance(node);
		if (handleFMLArgument()) {
			System.out.println("ENTER in " + peek() + " with " + node);
			push(getMainAnalyzer().retrieveFMLNode(node, n -> new WrappedFMLObjectNode(n, getMainAnalyzer())));
		}
	}

	@Override
	public final void outAFullQualifiedQualifiedInstance(AFullQualifiedQualifiedInstance node) {
		super.outAFullQualifiedQualifiedInstance(node);
		if (handleFMLArgument()) {
			pop();
			System.out.println("EXIT from " + peek() + " with " + node);
		}
	}

}
