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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.parser.lexer.Lexer;
import org.openflexo.foundation.fml.parser.node.Start;
import org.openflexo.foundation.fml.parser.parser.Parser;

/**
 * This class provides the parsing service for FML. This includes syntaxic and semantics analyzer.<br>
 * 
 * SableCC is used to perform this. To compile and generate the grammar, please invoke {@link CompileFMLParser} located in src/dev/java. The
 * grammar is located in src/main/resources/FML/fml-grammar.sablecc<br>
 * Generated code is located in org.openflexo.foundation.fml.parser.analysis, org.openflexo.foundation.fml.parser.lexer,
 * org.openflexo.foundation.fml.parser.node, org.openflexo.foundation.fml.parser.parser
 * 
 * @author sylvain
 */
public class FMLParser {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FMLParser.class.getPackage().getName());

	/**
	 * This is the method to invoke to perform a parsing. Syntaxic and (some) semantics analyzer are performed and returned value is an
	 * Expression conform to AnTAR expression abstract syntaxic tree
	 * 
	 * @param anExpression
	 * @return
	 * @throws ParseException
	 *             if expression was not parsable
	 */
	public static FMLCompilationUnit parse(File inputFile, FMLModelFactory modelFactory) throws ParseException {

		// TODO: handle close of all streams !!!!

		try (FileReader in = new FileReader(inputFile)) {
			System.out.println("Parsing: " + inputFile);

			// Create a Parser instance.
			Parser p = new Parser(new Lexer(new PushbackReader(new BufferedReader(in), 1024)));

			// Parse the input.
			Start tree = p.parse();

			// Apply the semantics analyzer.
			FMLSemanticsAnalyzer t = new FMLSemanticsAnalyzer(modelFactory, tree, readRawSource(new FileInputStream(inputFile)));

			return t.getCompilationUnit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage() + " while parsing " + inputFile);
		}
	}

	/**
	 * Read raw source of the file
	 * 
	 * @param ioDelegate
	 * @throws IOException
	 */
	private static List<String> readRawSource(InputStream inputStream) throws IOException {
		List<String> returned = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			String nextLine = null;
			do {
				nextLine = br.readLine();
				if (nextLine != null) {
					returned.add(nextLine);
				}
			} while (nextLine != null);
		}
		return returned;
	}

}
