/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.view.controller.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.AddFlexoProperty;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class AddFlexoPropertyActionizer extends ActionInitializer<AddFlexoProperty, FlexoObject, FlexoObject> {

	public AddFlexoPropertyActionizer(ControllerActionInitializer actionInitializer) {
		super(AddFlexoProperty.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionVisibleCondition<AddFlexoProperty, FlexoObject, FlexoObject> getVisibleCondition() {
		return new FlexoActionVisibleCondition<AddFlexoProperty, FlexoObject, FlexoObject>() {
			@Override
			public boolean isVisible(FlexoActionType<AddFlexoProperty, FlexoObject, FlexoObject> actionType, FlexoObject object,
					Vector<FlexoObject> globalSelection, FlexoEditor editor) {
				return false;
			}
		};
	}

}
