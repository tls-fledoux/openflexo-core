/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
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

import java.util.ArrayList;
import java.util.Set;

import org.openflexo.foundation.fml.rt.action.FlexoBehaviourAction;

/**
 * Synchronous implementation for {@link FMLRunTimeEngine}<br>
 * FML is executed synchronously in the calling Thread
 * 
 * @author sylvain
 * 
 */
public class SynchronousFMLRunTimeEngine extends DefaultFMLRunTimeEngine {

	public SynchronousFMLRunTimeEngine() {
	}

	// Synchronous implementation
	@Override
	public void execute(FlexoBehaviourAction<?, ?, ?> behaviourExecution) {
		behaviourExecution.doAction();
	}

	// Synchronous implementation
	@Override
	public void receivedEvent(FlexoEventInstance event) {
		if (event != null) {
			Set<EventInstanceListener> listeners = listeningInstances.get(event.getSourceVirtualModelInstance());
			if (listeners != null) {
				for (EventInstanceListener l : new ArrayList<>(listeners)) {
					if (l.getListener().getEvent().isAssignableFrom(event.getFlexoEvent())) {
						fireEventListener(l.getInstanceBeeingListening(), l.getListener(), event);
					}
				}
			}
		}
	}

}
