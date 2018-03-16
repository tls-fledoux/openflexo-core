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

package org.openflexo.foundation.fml.cli.command.directive;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.cli.CommandInterpreter;
import org.openflexo.foundation.fml.cli.command.Directive;
import org.openflexo.foundation.fml.cli.command.DirectiveDeclaration;
import org.openflexo.foundation.fml.cli.parser.node.ALoadDirective;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;

/**
 * Represents load resource directive in FML command-line interpreter
 * 
 * Usage: load <resource> where <resource> represents a resource
 * 
 * @author sylvain
 * 
 */
@DirectiveDeclaration(
		keyword = "load",
		usage = "load <resource>",
		description = "Load resource denoted by supplied resource uri",
		syntax = "load <resource>")
public class LoadResource extends Directive {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LoadResource.class.getPackage().getName());

	private FlexoResource<?> resource;

	public LoadResource(ALoadDirective node, CommandInterpreter commandInterpreter) {
		super(node, commandInterpreter);
		resource = getResource(node.getResourceUri().getText());
	}

	public FlexoResource<?> getResource() {
		return resource;
	}

	@Override
	public void execute() {
		if (resource.isLoaded()) {
			System.out.println("Resource " + resource.getURI() + " already loaded");
		}
		else {
			try {
				resource.loadResourceData(null);
				System.out.println("Loaded " + resource.getURI() + ".");
			} catch (FileNotFoundException e) {
				System.err.println("Cannot find resource " + resource.getURI());
			} catch (ResourceLoadingCancelledException e) {
			} catch (FlexoException e) {
				System.err.println("Cannot load resource " + resource.getURI() + " : " + e.getMessage());
			}
		}
	}
}
