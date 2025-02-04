/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Pamela-core, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.rt;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.FlexoEnum;
import org.openflexo.pamela.exceptions.InvalidDataException;
import org.openflexo.pamela.factory.PamelaModelFactory;
import org.openflexo.pamela.model.StringConverterLibrary.Converter;

public class FlexoEnumValueConverter extends Converter<FlexoEnumInstance> {

	public FlexoEnumValueConverter() {
		super(FlexoEnumInstance.class);
	}

	@Override
	public FlexoEnumInstance convertFromString(String value, PamelaModelFactory factory) throws InvalidDataException {
		if (factory instanceof AbstractVirtualModelInstanceModelFactory) {
			FlexoServiceManager sm = ((AbstractVirtualModelInstanceModelFactory) factory).getResource().getServiceManager();
			String enumURI = value.substring(0, value.lastIndexOf("#"));
			String enumValueURI = value.substring(value.lastIndexOf("#") + 1);
			FlexoEnum flexoEnum = (FlexoEnum) sm.getVirtualModelLibrary().getFlexoConcept(enumURI, false);
			if (flexoEnum != null) {
				return flexoEnum.getInstance(enumValueURI);
			}
		}
		return null;
	}

	@Override
	public String convertToString(FlexoEnumInstance value) {
		if (value != null) {
			return value.getFlexoEnum().getURI() + "#" + value.getFlexoConcept().getName();
		}
		return null;
	}
}
