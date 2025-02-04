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

package org.openflexo.foundation.fml.binding;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;

/**
 * A {@link FlexoPropertyPathElement} representing a {@link ModelSlot}, as a binding path applicable to an instance of a given
 * {@link FlexoConcept}<br>
 * Type of parent MUST be an instance of {@link FlexoConcept}
 * 
 * @author sylvain
 *
 * @param <P>
 */
@ModelEntity
@ImplementationClass(ModelSlotPathElement.ModelSlotPathElementImpl.class)
public interface ModelSlotPathElement<MS extends ModelSlot<?>> extends FlexoPropertyPathElement<MS> {

	public MS getModelSlot();

	public static abstract class ModelSlotPathElementImpl<MS extends ModelSlot<?>> extends FlexoPropertyPathElementImpl<MS>
			implements FlexoPropertyPathElement<MS> {

		private static final Logger logger = Logger.getLogger(ModelSlotPathElement.class.getPackage().getName());

		public ModelSlotPathElementImpl() {
			super();
		}

		/*public ModelSlotPathElementImpl(IBindingPathElement parent, MS modelSlot) {
			super(parent, modelSlot);
		}*/

		public MS getModelSlot() {
			return getFlexoProperty();
		}
	}
}
