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
package org.openflexo.foundation.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;

/**
 * @author gpolet
 * 
 */
public class SetPropertyAction extends FlexoAction<SetPropertyAction, FlexoObject, FlexoObject> {

	private static final Logger logger = Logger.getLogger(SetPropertyAction.class.getPackage().getName());

	public static class SetPropertyActionType extends FlexoActionType<SetPropertyAction, FlexoObject, FlexoObject> {

		protected SetPropertyActionType(String actionName) {
			super(actionName);
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return false;
		}

		@Override
		public SetPropertyAction makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new SetPropertyAction(focusedObject, globalSelection, editor);
		}

		/**
		 * Overrides getPersistentProperties
		 * 
		 * @see org.openflexo.foundation.action.FlexoActionType#getPersistentProperties()
		 */
		@Override
		protected String[] getPersistentProperties() {
			return new String[] { "key", "value" };
		}

	}

	public static final SetPropertyActionType actionType = new SetPropertyActionType("set_property");

	private String key;

	private Object value;

	private Object previousValue;

	private String localizedPropertyName;

	private boolean performValidate = true;

	static {
		FlexoObjectImpl.addActionForClass(actionType, FlexoObject.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected SetPropertyAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		previousValue = getFocusedObject().objectForKey(getKey());
		/*if (getFocusedObject() instanceof PortRegistery && getKey().equals("isVisible")) {
			if (previousValue != null && !previousValue.equals(getValue())) {
				OpenPortRegistery.actionType.makeNewEmbeddedAction(((PortRegistery) getFocusedObject()).getProcess(), null, this)
						.doAction();
			}
			return;
		}*/
		try {
			getFocusedObject().setObjectForKey(getValue(), getKey());
		} catch (Exception exception) {
			if (exception.getCause() instanceof FlexoException) {
				throw (FlexoException) exception.getCause();
			}
			logger.warning("Unexpected exception: see log for details");
			exception.printStackTrace();
			if (exception.getCause() instanceof Exception) {
				throw new UnexpectedException((Exception) exception.getCause());
			}
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static SetPropertyAction makeAction(FlexoObject object, String key, Object value, FlexoEditor editor) {
		SetPropertyAction returned = actionType.makeNewAction(object, null, editor);
		returned.setKey(key);
		returned.setValue(value);
		return returned;
	}

	public String getLocalizedPropertyName() {
		return localizedPropertyName;
	}

	public void setLocalizedPropertyName(String localizedPropertyName) {
		this.localizedPropertyName = localizedPropertyName;
	}

	public Object getPreviousValue() {
		return previousValue;
	}

	public boolean getPerformValidate() {
		return performValidate;
	}

	public void setPerformValidate(boolean b) {
		this.performValidate = b;
	}

}
