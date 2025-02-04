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

package org.openflexo.foundation.ontology.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.foundation.fml.AbstractCreationScheme;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.TypeAwareModelSlotInstance;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyIndividual;
import org.openflexo.foundation.ontology.fml.IndividualRole;
import org.openflexo.foundation.ontology.fml.editionaction.AddIndividual;
import org.openflexo.foundation.ontology.fml.rt.FlexoOntologyModelSlotInstance;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;

/**
 * Implementation of a {@link TypeAwareModelSlot} in a given technology conform to {@link IFlexoOntology}<br>
 * We implements here a model/metamodel conformance This model slot provides a symbolic access to a <br>
 * {@link IFlexoOntology} (model) conform to a {@link IFlexoOntology} (meta-model) with basic conformance <br>
 * contract. <br>
 * 
 * @see FlexoModel
 * @see FlexoMetaModel
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(FlexoOntologyModelSlot.FlexoOntologyModelSlotImpl.class)
public interface FlexoOntologyModelSlot<M extends FlexoModel<M, MM> & IFlexoOntology<TA>, MM extends FlexoMetaModel<MM> & IFlexoOntology<TA>, TA extends TechnologyAdapter<TA>>
		extends TypeAwareModelSlot<M, MM> {

	/**
	 * Instantiate a new IndividualRole
	 * 
	 * @param ontClass
	 * @return
	 */
	public IndividualRole<?> makeIndividualRole(IFlexoOntologyClass<?> type);

	public AddIndividual<? extends FlexoOntologyModelSlot<M, MM, TA>, M, IFlexoOntologyIndividual<TA>, TA> makeAddIndividualAction(
			IndividualRole<?> individualRole, AbstractCreationScheme creationScheme);

	public abstract Class<? extends IndividualRole<?>> getIndividualRoleClass();

	public static abstract class FlexoOntologyModelSlotImpl<M extends FlexoModel<M, MM> & IFlexoOntology<TA>, MM extends FlexoMetaModel<MM> & IFlexoOntology<TA>, TA extends TechnologyAdapter<TA>>
			extends TypeAwareModelSlotImpl<M, MM> implements FlexoOntologyModelSlot<M, MM, TA> {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(FlexoOntologyModelSlot.class.getPackage().getName());

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public TypeAwareModelSlotInstance<M, MM, ?> makeActorReference(M object, FlexoConceptInstance fci) {

			AbstractVirtualModelInstanceModelFactory<?> factory = fci.getFactory();
			FlexoOntologyModelSlotInstance returned = factory.newInstance(FlexoOntologyModelSlotInstance.class);
			returned.setModelSlot(this);
			returned.setFlexoConceptInstance(fci);
			returned.setAccessedResourceData(object);
			return returned;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class<? extends IndividualRole<?>> getIndividualRoleClass() {
			return (Class) getFlexoRoleClass(IndividualRole.class);
		}

		/**
		 * Instantiate a new IndividualRole
		 * 
		 * @param ontClass
		 * @return
		 */
		@Override
		public IndividualRole<?> makeIndividualRole(IFlexoOntologyClass<?> type) {
			IndividualRole<?> returned = makeFlexoRole(getIndividualRoleClass());
			returned.setModelSlot(this);
			returned.setOntologicType(type);
			return returned;
		}

		@Override
		public AddIndividual<? extends FlexoOntologyModelSlot<M, MM, TA>, M, IFlexoOntologyIndividual<TA>, TA> makeAddIndividualAction(
				IndividualRole<?> flexoRole, AbstractCreationScheme creationScheme) {
			@SuppressWarnings("unchecked")
			Class<? extends AddIndividual<? extends FlexoOntologyModelSlot<M, MM, TA>, M, IFlexoOntologyIndividual<TA>, TA>> addIndividualClass = (Class<? extends AddIndividual<? extends FlexoOntologyModelSlot<M, MM, TA>, M, IFlexoOntologyIndividual<TA>, TA>>) getEditionActionClass(
					AddIndividual.class);
			AddIndividual<? extends FlexoOntologyModelSlot<M, MM, TA>, M, IFlexoOntologyIndividual<TA>, TA> returned = makeEditionAction(
					addIndividualClass);
			returned.getReceiver().setUnparsedBinding(getName());
			// returned.setModelSlot(this);

			if (creationScheme.getParameter("uri") != null) {
				returned.setIndividualName(new DataBinding<>("parameters.uri"));
			}
			return returned;
		}

		public abstract String getURIForObject(
				FlexoOntologyModelSlotInstance<M, MM, ? extends FlexoOntologyModelSlot<M, MM, TA>, TA> msInstance, Object o);

		public abstract Object retrieveObjectWithURI(
				FlexoOntologyModelSlotInstance<M, MM, ? extends FlexoOntologyModelSlot<M, MM, TA>, TA> msInstance, String objectURI);

		/**
		 * Return flag indicating if this model slot implements a strict meta-modelling contract (return true if and only if a model in this
		 * technology can be conform to only one metamodel). Otherwise, this is simple metamodelling (a model is conform to exactely one
		 * metamodel)
		 * 
		 * @return
		 */
		@Override
		public abstract boolean isStrictMetaModelling();

		@Override
		public String getModelSlotDescription() {
			return "Model conform to " + getMetaModelURI();
		}

	}
}
