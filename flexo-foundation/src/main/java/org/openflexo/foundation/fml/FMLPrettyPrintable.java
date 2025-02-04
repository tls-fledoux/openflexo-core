/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.foundation.fml;

import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;

/**
 * Implemented by all FMLObjects which are pretty-printable in FML language<br>
 * 
 * Pretty-print is operated here using a {@link FMLPrettyPrintDelegate}
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
public interface FMLPrettyPrintable extends FMLObject {

	@PropertyIdentifier(type = FMLPrettyPrintDelegate.class)
	public static final String PRETTY_PRINT_DELEGATE_KEY = "prettyPrintDelegate";

	// Very important here:
	// ignoreForEquality=true MUST remains commented out for the update schemes to work well
	// But it must be present to locally run migration tests
	@Getter(value = PRETTY_PRINT_DELEGATE_KEY, ignoreType = true/*, ignoreForEquality = true*/)
	public FMLPrettyPrintDelegate<?> getPrettyPrintDelegate();

	@Setter(PRETTY_PRINT_DELEGATE_KEY)
	public void setPrettyPrintDelegate(FMLPrettyPrintDelegate<?> delegate);

	public String getFMLPrettyPrint();

	public String getNormalizedFML();
}
