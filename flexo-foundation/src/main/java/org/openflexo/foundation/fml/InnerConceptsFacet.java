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

package org.openflexo.foundation.fml;

import java.util.Collection;
import java.util.List;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingModel;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.validation.Validable;

/**
 * A structural facet defined for {@link VirtualModel}<br>
 * Presents concepts defined in related virtual model
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(InnerConceptsFacet.InnerConceptsFacetImpl.class)
public interface InnerConceptsFacet extends FlexoConceptObject, FlexoFacet<FlexoConcept> {

	@Override
	public FlexoConcept getFlexoConcept();

	public void setFlexoConcept(FlexoConcept flexoConcept);

	/**
	 * Return all {@link FlexoConcept} that are declared in related {@link VirtualModel}
	 * 
	 * @return
	 */
	public List<FlexoConcept> getFlexoConcepts();

	/**
	 * Return all {@link FlexoConcept} that are at the top of inheritance hierarchy for related {@link VirtualModel} (Return all
	 * {@link FlexoConcept} defined in this {@link VirtualModel} which have no parent)
	 *
	 * @return
	 */
	// public List<FlexoConcept> getInheritanceRootFlexoConcepts();

	/**
	 * Return all {@link FlexoConcept} that are at the top of embedding hierarchy for related {@link VirtualModel}
	 *
	 * @return
	 */
	public List<FlexoConcept> getEmbeddingRootFlexoConcepts();

	/**
	 * Return all {@link VirtualModel} that are contained in this {@link VirtualModel} if {@link #getFlexoConcept()} is a
	 * {@link VirtualModel}
	 * 
	 * @return
	 */
	public List<VirtualModel> getContainedVirtualModels();

	public void notifiedConceptsChanged();

	public abstract class InnerConceptsFacetImpl extends FlexoConceptObjectImpl implements InnerConceptsFacet {

		private FlexoConcept flexoConcept;

		@Override
		public FlexoConcept getFlexoConcept() {
			return flexoConcept;
		}

		@Override
		public void setFlexoConcept(FlexoConcept flexoConcept) {
			FlexoConcept oldValue = this.flexoConcept;
			BindingModel oldBM = getFlexoConcept() != null ? getFlexoConcept().getBindingModel() : null;
			this.flexoConcept = flexoConcept;
			BindingModel newBM = getFlexoConcept() != null ? getFlexoConcept().getBindingModel() : null;
			getPropertyChangeSupport().firePropertyChange(Bindable.BINDING_MODEL_PROPERTY, oldBM, newBM);
			getPropertyChangeSupport().firePropertyChange("flexoConcept", oldValue, flexoConcept);
		}

		@Override
		public BindingModel getBindingModel() {
			return getFlexoConcept().getBindingModel();
		}

		@Override
		public FlexoConcept getObject() {
			return getFlexoConcept();
		}

		@Override
		public List<VirtualModel> getContainedVirtualModels() {
			if (getFlexoConcept() instanceof VirtualModel) {
				return ((VirtualModel) getFlexoConcept()).getVirtualModels();
			}
			return null;
		}

		@Override
		public List<FlexoConcept> getFlexoConcepts() {
			if (getFlexoConcept() instanceof VirtualModel) {
				return ((VirtualModel) getFlexoConcept()).getAllRootFlexoConcepts();
				// return ((VirtualModel) getFlexoConcept()).getFlexoConcepts();
			}
			return getFlexoConcept().getChildFlexoConcepts();
		}

		/*@Override
		public List<FlexoConcept> getInheritanceRootFlexoConcepts() {
			if (getFlexoConcept() instanceof VirtualModel) {
				return ((VirtualModel) getFlexoConcept()).getAllRootFlexoConcepts();
			}
			return getFlexoConcept().getChildFlexoConcepts();
		}*/

		@Override
		public List<FlexoConcept> getEmbeddingRootFlexoConcepts() {
			if (getFlexoConcept() instanceof VirtualModel) {
				return ((VirtualModel) getFlexoConcept()).getAllRootFlexoConcepts();
			}
			return getFlexoConcept().getEmbeddedFlexoConcepts();
		}

		@Override
		public void notifiedConceptsChanged() {
			getPropertyChangeSupport().firePropertyChange("flexoConcepts", null, getFlexoConcepts());
			// getPropertyChangeSupport().firePropertyChange("inheritanceRootFlexoConcepts", null, getInheritanceRootFlexoConcepts());
			getPropertyChangeSupport().firePropertyChange("embeddingRootFlexoConcepts", null, getEmbeddingRootFlexoConcepts());
		}

		@Override
		public Collection<? extends Validable> getEmbeddedValidableObjects() {
			if (getFlexoConcept() instanceof VirtualModel) {
				return ((VirtualModel) getFlexoConcept()).getFlexoConcepts();
			}
			return getFlexoConcept().getEmbeddedFlexoConcepts();
		}

	}
}
