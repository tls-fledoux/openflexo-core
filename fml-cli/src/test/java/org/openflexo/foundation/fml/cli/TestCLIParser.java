/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Cartoeditor, a component of the software infrastructure 
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

import org.junit.Assert;
import org.junit.Test;
import org.openflexo.foundation.test.OpenflexoTestCase;

/**
 * Test command parser
 * 
 * @author sylvain
 *
 */
public class TestCLIParser extends OpenflexoTestCase {

	private void assertParsable(String command) throws ParseException {
		CommandParser.parse(command, null);
	}

	private void assertNotParsable(String command) {
		try {
			CommandParser.parse(command, null);
			Assert.fail();
		} catch (ParseException e) {
			// Normal
		}
	}

	@Test
	public void testHelp() throws ParseException {
		assertParsable("help");
		assertNotParsable("help identifier");
	}

	@Test
	public void testHistory() throws ParseException {
		assertParsable("history");
		assertNotParsable("history identifier");
	}

	@Test
	public void testCd() throws ParseException {
		assertParsable("cd foo");
		assertParsable("cd foo.ext");
		assertNotParsable("cd");

		assertParsable("cd resources-resources");
		assertParsable("cd flexo-test-resources");
		assertParsable("cd flexo-test-resources/src/main/resources/TestResourceCenter/FML");
		assertParsable("cd /var/folders/r6/s7gmms4n437gbyr5vpdhb4d80000gn/T/TestResourceCenter4401486062616967125");
		assertParsable("cd GIT2-1");
		assertParsable("cd GIT2.1");
		assertParsable("cd GIT-2.1");
		assertParsable("cd GIT-2.1.0");
	}

	@Test
	public void testPwd() throws ParseException {
		assertParsable("pwd");
		assertNotParsable("pwd identifier");
	}

	@Test
	public void testLs() throws ParseException {
		assertParsable("ls");
		assertNotParsable("ls identifier");
	}

	@Test
	public void testQuit() throws ParseException {
		assertParsable("quit");
		assertNotParsable("quit identifier");
	}

	@Test
	public void testServices() throws ParseException {
		assertParsable("services");
		assertNotParsable("services identifier");
	}

	@Test
	public void testService() throws ParseException {
		assertParsable("service Service operation_without_argument");
		assertParsable("service Service operation_with_argument argument");
		assertParsable("service ResourceCenterService status");
		assertParsable("service ResourceCenterService add_rc aDirectory");
		assertNotParsable("service");
		assertNotParsable("service AService");
		assertNotParsable("service AService operation arg1 arg2");
	}

	@Test
	public void testActivate() throws ParseException {
		assertParsable("activate TA");
		assertParsable("activate FML");
		assertNotParsable("activate");
		assertNotParsable("activate TA arg");
	}

	@Test
	public void testResources() throws ParseException {
		assertParsable("resources");
		assertParsable("resources FML");
		assertParsable("resources RC");
	}

	@Test
	public void testOpen() throws ParseException {
		assertParsable("open project.prj");
		assertParsable("open Project.prj");
		assertNotParsable("open");
	}

	@Test
	public void testLoad() throws ParseException {
		assertParsable("load -r [\"http://full/path/resource/uri\"]");
		assertParsable("load -r [\"http://full/path/resource/uri.fml\"]");
		assertParsable("load -r [\"http://ensta-bretagne.fr/cyber/cta/CTA.fml\"]");
		assertNotParsable("load");
		assertParsable("load Foo");
	}

	@Test
	public void testMore() throws ParseException {
		assertParsable("more");
		assertParsable("more foo");
		assertParsable("more -r [\"http://full/path/resource/uri\"]");
		assertParsable("more -r[\"http://full/path/resource/uri.fml\"]");
		assertParsable("more -r [\"http://ensta-bretagne.fr/cyber/cta/CTA.fml\"]");
		assertNotParsable("more [\"http://full/path/resource/uri\"]");
	}

	@Test
	public void testEnter() throws ParseException {
		assertParsable("enter identifier");
		assertParsable("enter full.qualified.identifier");
		assertParsable("enter a.full.expression(args)");
		assertParsable("enter -r [\"http://full/path/resource/uri\"]");
		assertParsable("enter -r [\"http://full/path/resource/uri.fml\"]");
		assertParsable("enter -r [\"http://ensta-bretagne.fr/cyber/cta/CTA.fml\"]");
		assertNotParsable("enter");
		assertNotParsable("enter -r");
		assertNotParsable("enter expression1 expression2");
	}

	@Test
	public void testExit() throws ParseException {
		assertParsable("exit");
		assertNotParsable("exit identifier");
	}

	@Test
	public void testContext() throws ParseException {
		assertParsable("context");
		assertNotParsable("context identifier");
	}

	@Test
	public void testExpression() throws ParseException {
		assertParsable("a");
		assertParsable("a.b");
		assertParsable("a.b.c(1)");
		assertParsable("a(1)");
		assertParsable("a+b*c");
		assertParsable("1");
		assertParsable("3.14159");
	}

	@Test
	public void testAssignation() throws ParseException {
		assertParsable("v=a");
		assertParsable("v=a.b");
		assertParsable("v=a.b.c(1)");
		assertParsable("v=a(1)");
		assertParsable("v=a+b*c");
		assertParsable("v=1");
		assertParsable("v=3.14159");
		assertParsable("v.b=a");
	}

}
