/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.foundation.resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when one or more SaveResourceException were raised during resource saving
 * 
 * @author sguerin
 * 
 */
public class SaveResourceExceptionList extends Exception {

	protected List<SaveResourceException> _saveExceptions;

	public SaveResourceExceptionList(List<SaveResourceException> exceptions) {
		super();
		_saveExceptions = exceptions;
	}

	public SaveResourceExceptionList(SaveResourceException exception) {
		super();
		_saveExceptions = new ArrayList<>();
		registerNewException(exception);
	}

	public void registerNewException(SaveResourceException exception) {
		_saveExceptions.add(exception);
	}

	public List<SaveResourceException> getSaveExceptions() {
		return _saveExceptions;
	}

	public String errorFilesList() {
		StringBuilder sb = new StringBuilder();
		for (SaveResourceException excep : _saveExceptions) {
			sb.append(excep.getDelegate().toString()).append('\n');
		}
		return sb.toString();
	}
}
