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

package org.openflexo.foundation.fml.rt.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.action.ActionGroup;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionFactory;
import org.openflexo.foundation.fml.FlexoBehaviour;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.VirtualModelInstanceObject;
import org.openflexo.localization.LocalizedDelegate;

/**
 * Factory for {@link FlexoBehaviourAction} (an execution environment of a {@link FlexoBehaviour} on a given {@link FlexoConceptInstance} as
 * a {@link FlexoAction})
 * 
 * @author sylvain
 *
 * @param <A>
 *            type of FlexoAction
 * @param <FB>
 *            type of {@link FlexoBehaviour}
 * @param <O>
 *            type of {@link FlexoConceptInstance} on which this action applies
 */
public abstract class FlexoBehaviourActionFactory<A extends FlexoBehaviourAction<A, FB, O>, FB extends FlexoBehaviour, O extends FlexoConceptInstance>
		extends FlexoActionFactory<A, O, VirtualModelInstanceObject> {

	private final FB behaviour;
	private final O flexoConceptInstance;

	public FlexoBehaviourActionFactory(FB behaviour, O flexoConceptInstance, ActionGroup actionGroup, int actionCategory) {
		super(behaviour.getName(), actionGroup, actionCategory);
		this.behaviour = behaviour;
		this.flexoConceptInstance = flexoConceptInstance;
	}

	@Override
	public final boolean isEnabled(O object, Vector<VirtualModelInstanceObject> globalSelection) {
		return isEnabledForSelection(object, globalSelection);
	}

	@Override
	public boolean isVisibleForSelection(O object, Vector<VirtualModelInstanceObject> globalSelection) {
		return true;
	}

	public final FB getBehaviour() {
		return behaviour;
	}

	public final O getFlexoConceptInstance() {
		return flexoConceptInstance;
	}

	@Override
	public LocalizedDelegate getLocales(FlexoServiceManager serviceManager) {
		if (getBehaviour() != null) {
			return getBehaviour().getLocales();
		}
		return super.getLocales(serviceManager);
	}
}
