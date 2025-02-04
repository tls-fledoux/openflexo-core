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

package org.openflexo.foundation.fml.action;

import java.security.InvalidParameterException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.InvalidNameException;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.action.NotImplementedException;
import org.openflexo.foundation.action.TechnologySpecificFlexoAction;
import org.openflexo.foundation.fml.FMLModelFactory;
import org.openflexo.foundation.fml.FMLObject;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.FlexoConceptObject;
import org.openflexo.foundation.fml.FlexoEvent;
import org.openflexo.foundation.fml.InconsistentFlexoConceptHierarchyException;
import org.openflexo.foundation.fml.InnerConceptsFacet;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.toolbox.StringUtils;

public class CreateFlexoEvent extends AbstractCreateFlexoConcept<CreateFlexoEvent, FlexoConceptObject, FMLObject>
		implements TechnologySpecificFlexoAction<FMLTechnologyAdapter> {

	private static final Logger logger = Logger.getLogger(CreateFlexoEvent.class.getPackage().getName());

	public static FlexoActionFactory<CreateFlexoEvent, FlexoConceptObject, FMLObject> actionType = new FlexoActionFactory<CreateFlexoEvent, FlexoConceptObject, FMLObject>(
			"flexo_event", FlexoActionFactory.newMenu, FlexoActionFactory.defaultGroup, FlexoActionFactory.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateFlexoEvent makeNewAction(FlexoConceptObject focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
			return new CreateFlexoEvent(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoConceptObject object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(FlexoConceptObject object, Vector<FMLObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(CreateFlexoEvent.actionType, VirtualModel.class);
		FlexoObjectImpl.addActionForClass(CreateFlexoEvent.actionType, InnerConceptsFacet.class);
	}

	private String newFlexoEventName;
	private String newFlexoEventDescription;
	private FlexoEvent newFlexoEvent;

	public boolean switchNewlyCreatedFlexoConcept = false;

	CreateFlexoEvent(FlexoConceptObject focusedObject, Vector<FMLObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public Class<? extends FMLTechnologyAdapter> getTechnologyAdapterClass() {
		return FMLTechnologyAdapter.class;
	}

	public VirtualModel getFocusedVirtualModel() {
		if (getFocusedObject() instanceof VirtualModel) {
			return (VirtualModel) getFocusedObject();
		}
		if (getFocusedObject() != null) {
			return getFocusedObject().getDeclaringCompilationUnit().getVirtualModel();
		}
		return null;
	}

	@Override
	protected void doAction(Object context)
			throws NotImplementedException, InvalidParameterException, InconsistentFlexoConceptHierarchyException, InvalidNameException {

		FMLModelFactory factory = getFocusedObject().getFMLModelFactory();

		newFlexoEvent = factory.newFlexoEvent();
		newFlexoEvent.setName(getNewFlexoEventName());

		VirtualModel virtualModel = getFocusedObject().getOwningVirtualModel();

		virtualModel.addToFlexoConcepts(newFlexoEvent);

		performSetParentConcepts();
		performCreateProperties();
		performCreateBehaviours();
		performCreateInspectors();
		performPostProcessings();

	}

	@Override
	public FlexoEvent getNewFlexoConcept() {
		return newFlexoEvent;
	}

	public String getNewFlexoEventName() {
		return newFlexoEventName;
	}

	public void setNewFlexoEventName(String newFlexoEventName) {
		if ((newFlexoEventName == null && this.newFlexoEventName != null)
				|| (newFlexoEventName != null && !newFlexoEventName.equals(this.newFlexoEventName))) {
			String oldValue = this.newFlexoEventName;
			this.newFlexoEventName = newFlexoEventName;
			getPropertyChangeSupport().firePropertyChange("newFlexoEventName", oldValue, newFlexoEventName);
		}
	}

	public String getNewFlexoEventDescription() {
		return newFlexoEventDescription;
	}

	public void setNewFlexoEventDescription(String newFlexoEventDescription) {
		if ((newFlexoEventDescription == null && this.newFlexoEventDescription != null)
				|| (newFlexoEventDescription != null && !newFlexoEventDescription.equals(this.newFlexoEventDescription))) {
			String oldValue = this.newFlexoEventDescription;
			this.newFlexoEventDescription = newFlexoEventDescription;
			getPropertyChangeSupport().firePropertyChange("newFlexoEventDescription", oldValue, newFlexoEventDescription);
		}
	}

	@Override
	public boolean isValid() {
		if (StringUtils.isEmpty(newFlexoEventName)) {
			return false;
		}
		else if (getFocusedObject().getDeclaringCompilationUnit().getFlexoConcept(newFlexoEventName) != null) {
			return false;
		}
		return true;
	}

	@Override
	public int getExpectedProgressSteps() {
		return 10;
	}

}
