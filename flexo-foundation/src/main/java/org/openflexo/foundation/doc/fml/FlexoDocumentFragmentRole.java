/*
 * (c) Copyright 2013- Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.foundation.doc.fml;

import org.openflexo.foundation.doc.FlexoDocumentFragment;
import org.openflexo.foundation.fml.FlexoRole;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(FlexoDocumentFragmentRole.FlexoDocumentFragmentRoleImpl.class)
public interface FlexoDocumentFragmentRole<T extends FlexoDocumentFragment<?, ?>> extends FlexoRole<T> {

	public static abstract class FlexoDocumentFragmentRoleImpl<T extends FlexoDocumentFragment<?, ?>> extends FlexoRoleImpl<T> implements
			FlexoDocumentFragmentRole<T> {

	}
}
