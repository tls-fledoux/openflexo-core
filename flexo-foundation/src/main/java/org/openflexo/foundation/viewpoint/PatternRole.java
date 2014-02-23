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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.FlexoConceptInstance;
import org.openflexo.model.annotations.DeserializationFinalizer;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.Import;
import org.openflexo.model.annotations.Imports;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link PatternRole} is a structural element of an FlexoConcept, which plays a role in this {@link FlexoConcept}<br>
 * More formerly, a {@link PatternRole} is the specification of an object accessed at run-time (inside an {@link FlexoConcept} instance)
 * 
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(PatternRole.PatternRoleImpl.class)
@Imports({ @Import(FlexoConceptInstancePatternRole.class), @Import(OntologicObjectPatternRole.class), @Import(PrimitivePatternRole.class) })
public abstract interface PatternRole<T> extends FlexoConceptObject {

	@PropertyIdentifier(type = FlexoConcept.class)
	public static final String FLEXO_CONCEPT_KEY = "flexoConcept";
	@PropertyIdentifier(type = String.class)
	public static final String PATTERN_ROLE_NAME_KEY = "patternRoleName";
	@PropertyIdentifier(type = String.class)
	public static final String DESCRIPTION_KEY = "description";
	@PropertyIdentifier(type = ModelSlot.class)
	public static final String MODEL_SLOT_KEY = "modelSlot";

	@Override
	@Getter(value = FLEXO_CONCEPT_KEY, inverse = FlexoConcept.PATTERN_ROLES_KEY)
	public FlexoConcept getFlexoConcept();

	@Setter(FLEXO_CONCEPT_KEY)
	public void setFlexoConcept(FlexoConcept flexoConcept);

	@Getter(value = PATTERN_ROLE_NAME_KEY)
	@XMLAttribute(xmlTag = "patternRole")
	public String getPatternRoleName();

	@Setter(PATTERN_ROLE_NAME_KEY)
	public void setPatternRoleName(String patternRoleName);

	@Override
	@Getter(value = DESCRIPTION_KEY)
	@XMLElement
	public String getDescription();

	@Override
	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = MODEL_SLOT_KEY)
	@XMLElement
	public ModelSlot<?> getModelSlot();

	@Setter(MODEL_SLOT_KEY)
	public void setModelSlot(ModelSlot<?> modelSlot);

	@DeserializationFinalizer
	public void finalizePatternRoleDeserialization();

	public Type getType();

	public String getPreciseType();

	/**
	 * Encodes the default deletion strategy
	 * 
	 * @return
	 */
	public abstract boolean defaultBehaviourIsToBeDeleted();

	/**
	 * Instanciate run-time-level object encoding reference to object (see {@link ActorReference})
	 * 
	 * @param object
	 * @param epi
	 * @return
	 */
	public abstract ActorReference<T> makeActorReference(T object, FlexoConceptInstance epi);

	public static abstract class PatternRoleImpl<T> extends FlexoConceptObjectImpl implements PatternRole<T> {

		// private static final Logger logger = Logger.getLogger(PatternRole.class.getPackage().getName());

		private FlexoConcept _pattern;

		private ModelSlot<?> modelSlot;

		public PatternRoleImpl() {
			super();
		}

		@Override
		public String getURI() {
			return getFlexoConcept().getURI() + "." + getPatternRoleName();
		}

		@Override
		public ModelSlot<?> getModelSlot() {
			return modelSlot;
		}

		@Override
		public void setModelSlot(ModelSlot<?> modelSlot) {
			this.modelSlot = modelSlot;
			setChanged();
			notifyObservers(new DataModification("modelSlot", null, modelSlot));
		}

		@Override
		public void setFlexoConcept(FlexoConcept pattern) {
			_pattern = pattern;
		}

		@Override
		public FlexoConcept getFlexoConcept() {
			return _pattern;
		}

		@Override
		public VirtualModel getVirtualModel() {
			if (getFlexoConcept() != null) {
				return getFlexoConcept().getVirtualModel();
			}
			return null;
		}

		@Override
		public String getPatternRoleName() {
			return getName();
		}

		@Override
		public void setPatternRoleName(String patternRoleName) {
			setName(patternRoleName);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName()
					+ ":"
					+ getPatternRoleName()
					+ "[container="
					+ (getFlexoConcept() != null ? getFlexoConcept().getName() + "/"
							+ (getFlexoConcept().getVirtualModel() != null ? getFlexoConcept().getVirtualModel().getName() : "null")
							: "null") + "][" + Integer.toHexString(hashCode()) + "]";
		}

		@Override
		public abstract Type getType();

		@Override
		public abstract String getPreciseType();

		@Override
		public void finalizePatternRoleDeserialization() {
		}

		@Override
		public final BindingModel getBindingModel() {
			return getFlexoConcept().getBindingModel();
		}

		// public abstract boolean getIsPrimaryRole();

		// public abstract void setIsPrimaryRole(boolean isPrimary);

		@Override
		public abstract boolean defaultBehaviourIsToBeDeleted();

		@Override
		public abstract ActorReference<T> makeActorReference(T object, FlexoConceptInstance epi);

		// @Override
		// public abstract String getLanguageRepresentation();

	}

	public static class PatternRoleMustHaveAName extends ValidationRule<PatternRoleMustHaveAName, PatternRole> {
		public PatternRoleMustHaveAName() {
			super(PatternRole.class, "pattern_role_must_have_a_name");
		}

		@Override
		public ValidationIssue<PatternRoleMustHaveAName, PatternRole> applyValidation(PatternRole patternRole) {
			if (StringUtils.isEmpty(patternRole.getPatternRoleName())) {
				return new ValidationError<PatternRoleMustHaveAName, PatternRole>(this, patternRole, "pattern_role_has_no_name");
			}
			return null;
		}
	}

}
