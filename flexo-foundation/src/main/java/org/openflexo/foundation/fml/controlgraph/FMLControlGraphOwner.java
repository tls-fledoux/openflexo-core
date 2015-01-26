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

package org.openflexo.foundation.fml.controlgraph;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.fml.FlexoConceptObject;
import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents an object that "owns" a control graph
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(DefaultFMLControlGraphOwner.class) })
public abstract interface FMLControlGraphOwner extends FlexoConceptObject {

	/**
	 * Return control graph identified by supplied owner's context
	 * 
	 * @param ownerContext
	 * @return
	 */
	public FMLControlGraph getControlGraph(String ownerContext);

	/**
	 * Sets control graph identified by supplied owner's context
	 * 
	 * @param controlGraph
	 * @param ownerContext
	 */
	public void setControlGraph(FMLControlGraph controlGraph, String ownerContext);

	/**
	 * Return base BindingModel to be used for supplied control graph
	 * 
	 * @param controlGraph
	 * @return
	 */
	public BindingModel getBaseBindingModel(FMLControlGraph controlGraph);

	/**
	 * This method will apply reduction rules to the current control graph<br>
	 * This means that adequate structural modifications will be performed to reduce the complexity of this control graph owner<br>
	 * (unnecessary EmptyControlGraph will be removed, for example)
	 */
	public void reduce();

	/**
	 * Called when the structure of supplied control graph has changed
	 */
	public void controlGraphChanged(FMLControlGraph controlGraph);

	@Implementation
	public abstract class FMLControlGraphOwnerImpl implements FMLControlGraphOwner {
		@Override
		public void controlGraphChanged(FMLControlGraph controlGraph) {
			if (this instanceof FMLControlGraph) {
				FMLControlGraph cg = (FMLControlGraph) this;
				cg.getPropertyChangeSupport().firePropertyChange("flattenedSequence", null, cg.getFlattenedSequence());
				cg.getOwner().controlGraphChanged(cg.getOwner().getControlGraph(cg.getOwnerContext()));
			}
		}

	}
}
