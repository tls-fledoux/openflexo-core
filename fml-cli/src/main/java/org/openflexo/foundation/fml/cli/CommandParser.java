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

package org.openflexo.foundation.fml.cli;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.cli.command.AbstractCommand;
import org.openflexo.foundation.fml.parser.lexer.CustomLexer;
import org.openflexo.foundation.fml.parser.lexer.CustomLexer.EntryPointKind;
import org.openflexo.foundation.fml.parser.node.Start;
import org.openflexo.foundation.fml.parser.parser.Parser;
import org.openflexo.p2pp.RawSource;

/**
 * This class provides the parsing service for FML commands. This includes syntactic and semantics analyzer.<br>
 * 
 * SableCC is used to generate the grammar located in fml-parser.<br>
 * 
 * @author sylvain
 */
public class CommandParser {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CommandParser.class.getPackage().getName());

	/**
	 * This is the method to invoke to perform a parsing. Syntactic and (some) semantics analyzer are performed and returned value is an
	 * Expression conform to expression abstract syntactic tree
	 * 
	 * @param anExpression
	 * @return
	 * @throws ParseException
	 *             if parsing expression lead to an error
	 */
	public static AbstractCommand<?> parse(String aCommand, AbstractCommandInterpreter commandInterpreter) throws ParseException {
		try {
			// System.out.println("Parsing: " + anExpression);

			RawSource rawSource = new RawSource(new StringReader(aCommand));

			// Create a Parser instance.
			Parser p = new Parser(new CustomLexer(new PushbackReader(new StringReader(aCommand)), EntryPointKind.Command));

			// Parse the input.
			Start tree = p.parse();

			// Apply the semantics analyzer.
			if (commandInterpreter != null) {
				CommandSemanticsAnalyzer t = new CommandSemanticsAnalyzer(commandInterpreter, tree, rawSource);
				tree.apply(t);
				return t.getCommand();
			}
			else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage() + " while parsing " + aCommand);
		}
	}

}
