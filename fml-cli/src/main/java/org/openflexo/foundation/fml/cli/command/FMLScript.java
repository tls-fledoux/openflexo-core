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

package org.openflexo.foundation.fml.cli.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.cli.AbstractCommandInterpreter;
import org.openflexo.foundation.fml.cli.ScriptSemanticsAnalyzer;
import org.openflexo.foundation.fml.parser.node.Node;

/**
 * Represents a command in FML command-line interpreter
 * 
 * @author sylvain
 * 
 */
public class FMLScript {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FMLScript.class.getPackage().getName());

	private Node node;
	private ScriptSemanticsAnalyzer scriptSemanticsAnalyzer;

	private List<AbstractCommand> commands;

	public FMLScript(Node node, ScriptSemanticsAnalyzer scriptSemanticsAnalyzer) {
		this.node = node;
		this.scriptSemanticsAnalyzer = scriptSemanticsAnalyzer;
		commands = new ArrayList<>();
	}

	public Node getNode() {
		return node;
	}

	public ScriptSemanticsAnalyzer getScriptSemanticsAnalyzer() {
		return scriptSemanticsAnalyzer;
	}

	public AbstractCommandInterpreter getCommandInterpreter() {
		return getScriptSemanticsAnalyzer().getCommandInterpreter();
	}

	public PrintStream getOutStream() {
		return getCommandInterpreter().getOutStream();
	}

	public PrintStream getErrStream() {
		return getCommandInterpreter().getErrStream();
	}

	public List<AbstractCommand> getCommands() {
		return commands;
	}

	public void addToCommands(AbstractCommand command) {
		commands.add(command);
	}

	/**
	 * Execute this {@link FMLScript}
	 * 
	 */
	public void execute() {
		for (AbstractCommand command : getCommands()) {
			logger.info(">>> Execute " + command);
			getOutStream().println(getCommandInterpreter().getPrompt() + " > " + command);
			command.execute();
		}
	}

}
