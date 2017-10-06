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

package org.openflexo.foundation.technologyadapter;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.foundation.fml.rt.InferedFMLRTModelSlot;
import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * 
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(UseModelSlotDeclaration.UseModelSlotDeclarationImpl.class)
@XMLElement
public interface UseModelSlotDeclaration extends FlexoObject {

	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";
	@PropertyIdentifier(type = Class.class)
	public static final String MODEL_SLOT_CLASS_KEY = "modelSlotClass";

	@Getter(value = MODEL_SLOT_CLASS_KEY)
	@XMLAttribute
	public Class<? extends ModelSlot<?>> getModelSlotClass();

	@Setter(MODEL_SLOT_CLASS_KEY)
	public void setModelSlotClass(Class<? extends ModelSlot<?>> modelSlotClass);

	@Getter(value = VIRTUAL_MODEL_KEY, inverse = VirtualModel.USE_DECLARATIONS_KEY)
	@CloningStrategy(StrategyType.IGNORE)
	public VirtualModel getVirtualModel();

	@Setter(VIRTUAL_MODEL_KEY)
	public void setVirtualModel(VirtualModel virtualModel);

	@Deprecated
	public VirtualModelInstanceType getInferedVirtualModelInstanceType(VirtualModel vm, FlexoServiceManager sm);

	public abstract class UseModelSlotDeclarationImpl extends FlexoObjectImpl implements UseModelSlotDeclaration {

		// Hacking area
		// I'm not proud of that, this should be handled from a more elegant way
		// TODO: find a better solution
		@Deprecated
		@Override
		public VirtualModelInstanceType getInferedVirtualModelInstanceType(VirtualModel vm, FlexoServiceManager sm) {
			if (sm != null && InferedFMLRTModelSlot.class.isAssignableFrom(getModelSlotClass())) {
				TechnologyAdapterService taService = sm.getTechnologyAdapterService();
				TechnologyAdapter ta = taService.getTechnologyAdapterForModelSlot(getModelSlotClass());
				if (ta != null) {
					return ta.getInferedVirtualModelInstanceType(vm, (Class<? extends InferedFMLRTModelSlot<?, ?>>) getModelSlotClass());
				}
			}
			return null;
		}
	}
}
