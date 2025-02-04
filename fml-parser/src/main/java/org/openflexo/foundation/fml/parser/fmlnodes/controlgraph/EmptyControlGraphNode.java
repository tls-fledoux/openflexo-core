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

package org.openflexo.foundation.fml.parser.fmlnodes.controlgraph;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.controlgraph.EmptyControlGraph;
import org.openflexo.foundation.fml.parser.FMLCompilationUnitSemanticsAnalyzer;
import org.openflexo.foundation.fml.parser.node.Node;
import org.openflexo.p2pp.RawSource.RawSourceFragment;
import org.openflexo.p2pp.RawSource.RawSourcePosition;

/**
 * @author sylvain
 * 
 */
public class EmptyControlGraphNode extends ControlGraphNode<Node, EmptyControlGraph> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(EmptyControlGraphNode.class.getPackage().getName());

	public EmptyControlGraphNode(Node astNode, FMLCompilationUnitSemanticsAnalyzer analyzer) {
		super(astNode, analyzer);
	}

	public EmptyControlGraphNode(EmptyControlGraph sequence, FMLCompilationUnitSemanticsAnalyzer analyzer) {
		super(sequence, analyzer);
	}

	@Override
	public RawSourceFragment getLastParsedFragment() {
		return super.getLastParsedFragment();
	}

	// Tricky area : we should not take { } under account
	@Override
	public RawSourcePosition getStartPosition() {
		RawSourcePosition returned = super.getStartPosition();
		if (returned != null && returned.getCharAfter().equals('{')) {
			returned = returned.increment();
		}
		return returned;
	}

	// Tricky area : we should not take { } under account
	@Override
	public RawSourcePosition getEndPosition() {
		RawSourcePosition returned = super.getEndPosition();
		if (returned != null && returned.decrement().getCharAfter().equals('}')) {
			returned = returned.decrement();
		}
		return returned;
	}

	@Override
	public EmptyControlGraph buildModelObjectFromAST(Node astNode) {
		EmptyControlGraph returned = getFactory().newEmptyControlGraph();
		return returned;
	}

	@Override
	public void preparePrettyPrint(boolean hasParsedVersion) {
		super.preparePrettyPrint(hasParsedVersion);
		// Nothing to do
	}

}
