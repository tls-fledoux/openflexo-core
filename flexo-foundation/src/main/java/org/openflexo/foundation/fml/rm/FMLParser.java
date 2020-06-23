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

package org.openflexo.foundation.fml.rm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.FMLModelFactory;

/**
 * API specifying the parsing service for FML.<br>
 * 
 * @author sylvain
 */
public interface FMLParser {

	public FMLCompilationUnit parse(String data, FMLModelFactory modelFactory/*, EntryPointKind entryPointKind*/)
			throws ParseException, IOException;

	public FMLCompilationUnit parse(InputStream inputStream, FMLModelFactory modelFactory) throws ParseException, IOException;

	public FMLCompilationUnit parse(File file, FMLModelFactory modelFactory) throws ParseException, IOException;

	public void initPrettyPrint(FMLCompilationUnit fmlCompilationUnit);

	public static class ParseException extends Exception {

		/**
		 * Constructs a new parse exception with the specified detail message.
		 * 
		 * @param message
		 *            the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
		 */
		public ParseException(String message) {
			super(message);
		}

	}
}
