package org.openflexo.vpm.controller.action;

import java.awt.Dimension;
import java.awt.Image;
import java.util.logging.Logger;

import org.openflexo.ApplicationContext;
import org.openflexo.components.wizard.WizardStep;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.viewpoint.FlexoConcept;
import org.openflexo.foundation.viewpoint.FlexoConceptObject;
import org.openflexo.foundation.viewpoint.FlexoRole;
import org.openflexo.foundation.viewpoint.PrimitiveRole.PrimitiveType;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.action.CreateFlexoRole;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoController;

public class CreateFlexoRoleWizard extends AbstractCreateFMLElementWizard<CreateFlexoRole, FlexoConceptObject, ViewPointObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CreateFlexoRoleWizard.class.getPackage().getName());

	private static final String DUPLICATED_NAME = FlexoLocalization.localizedForKey("this_name_is_already_used_please_choose_an_other_one");
	private static final String EMPTY_NAME = FlexoLocalization.localizedForKey("flexo_role_must_have_an_non_empty_and_unique_name");
	private static final String NO_ROLE_TYPE = FlexoLocalization.localizedForKey("please_choose_a_role_type");
	private static final String NO_FLEXO_CONCEPT_TYPE = FlexoLocalization.localizedForKey("please_choose_type_of_flexo_concept");
	private static final String NO_PRIMITIVE_TYPE = FlexoLocalization.localizedForKey("please_choose_primitive_type");
	private static final String NO_INDIVIDUAL_TYPE = FlexoLocalization.localizedForKey("please_choose_individual_type");
	private static final String RECOMMANDED_DESCRIPTION = FlexoLocalization.localizedForKey("it_is_recommanded_to_describe_flexo_role");

	private final DescribeFlexoRole describeFlexoRole;

	private static final Dimension DIMENSIONS = new Dimension(600, 500);

	public CreateFlexoRoleWizard(CreateFlexoRole action, FlexoController controller) {
		super(action, controller);
		addStep(describeFlexoRole = new DescribeFlexoRole());
	}

	@Override
	public String getWizardTitle() {
		return FlexoLocalization.localizedForKey("create_flexo_role");
	}

	@Override
	public Image getDefaultPageImage() {
		return IconFactory.getImageIcon(VPMIconLibrary.FLEXO_ROLE_BIG_ICON, IconLibrary.NEW_32_32).getImage();
	}

	public DescribeFlexoRole getDescribeFlexoRole() {
		return describeFlexoRole;
	}

	@Override
	public Dimension getPreferredSize() {
		return DIMENSIONS;
	}

	/**
	 * This step is used to set {@link VirtualModel} to be used, as well as name and title of the {@link VirtualModelInstance}
	 * 
	 * @author sylvain
	 *
	 */
	@FIBPanel("Fib/Wizard/CreateFlexoConcept/DescribeFlexoRole.fib")
	public class DescribeFlexoRole extends WizardStep {

		public ApplicationContext getServiceManager() {
			return getController().getApplicationContext();
		}

		public CreateFlexoRole getAction() {
			return CreateFlexoRoleWizard.this.getAction();
		}

		public ViewPoint getViewPoint() {
			return CreateFlexoRoleWizard.this.getViewPoint();
		}

		@Override
		public String getTitle() {
			return FlexoLocalization.localizedForKey("describe_flexo_role");
		}

		@Override
		public boolean isValid() {

			if (StringUtils.isEmpty(getRoleName())) {
				setIssueMessage(EMPTY_NAME, IssueMessageType.ERROR);
				return false;
			} else if (getFlexoConcept().getFlexoRole(getRoleName()) != null) {
				setIssueMessage(DUPLICATED_NAME, IssueMessageType.ERROR);
				return false;
			} else if (getFlexoRoleClass() == null) {
				setIssueMessage(NO_ROLE_TYPE, IssueMessageType.ERROR);
				return false;
			} else if (getAction().isFlexoConceptInstance() && getFlexoConceptInstanceType() == null) {
				setIssueMessage(NO_FLEXO_CONCEPT_TYPE, IssueMessageType.ERROR);
				return false;
			} else if (getAction().isPrimitive() && getPrimitiveType() == null) {
				setIssueMessage(NO_PRIMITIVE_TYPE, IssueMessageType.ERROR);
				return false;
			} else if (getAction().isIndividual() && getIndividualType() == null) {
				setIssueMessage(NO_INDIVIDUAL_TYPE, IssueMessageType.ERROR);
				return false;
			} else if (StringUtils.isEmpty(getDescription())) {
				setIssueMessage(RECOMMANDED_DESCRIPTION, IssueMessageType.WARNING);
			}

			return true;
		}

		public String getRoleName() {
			return getAction().getRoleName();
		}

		public void setRoleName(String roleName) {
			if ((roleName == null && getRoleName() != null) || (roleName != null && !roleName.equals(getRoleName()))) {
				String oldValue = getRoleName();
				getAction().setRoleName(roleName);
				getPropertyChangeSupport().firePropertyChange("roleName", oldValue, roleName);
				checkValidity();
			}
		}

		public String getDescription() {
			return getAction().getDescription();
		}

		public void setDescription(String description) {
			if ((description == null && getDescription() != null) || (description != null && !description.equals(getDescription()))) {
				String oldValue = getDescription();
				getAction().setDescription(description);
				getPropertyChangeSupport().firePropertyChange("description", oldValue, description);
				checkValidity();
			}
		}

		public ModelSlot<?> getModelSlot() {
			return getAction().getModelSlot();
		}

		public void setModelSlot(ModelSlot<?> modelSlot) {
			if (getModelSlot() != modelSlot) {
				ModelSlot<?> oldValue = getModelSlot();
				getAction().setModelSlot(modelSlot);
				getPropertyChangeSupport().firePropertyChange("modelSlot", oldValue, modelSlot);
				getPropertyChangeSupport().firePropertyChange("flexoRoleClass", null, getModelSlot());
				getPropertyChangeSupport().firePropertyChange("roleName", null, getRoleName());
				checkValidity();
			}
		}

		public Class<? extends FlexoRole> getFlexoRoleClass() {
			return getAction().getFlexoRoleClass();
		}

		public void setFlexoRoleClass(Class<? extends FlexoRole> flexoRoleClass) {
			if (getFlexoRoleClass() != flexoRoleClass) {
				Class<? extends FlexoRole> oldValue = getFlexoRoleClass();
				getAction().setFlexoRoleClass(flexoRoleClass);
				getPropertyChangeSupport().firePropertyChange("flexoRoleClass", oldValue, flexoRoleClass);
				getPropertyChangeSupport().firePropertyChange("roleName", null, getRoleName());
				checkValidity();
			}
		}

		public IFlexoOntologyClass getIndividualType() {
			return getAction().getIndividualType();
		}

		public void setIndividualType(IFlexoOntologyClass individualType) {
			if (getIndividualType() != individualType) {
				IFlexoOntologyClass oldValue = getIndividualType();
				getAction().setIndividualType(individualType);
				getPropertyChangeSupport().firePropertyChange("individualType", oldValue, individualType);
				checkValidity();
			}
		}

		public FlexoConcept getFlexoConceptInstanceType() {
			return getAction().getFlexoConceptInstanceType();
		}

		public void setFlexoConceptInstanceType(FlexoConcept flexoConceptInstanceType) {
			if (getFlexoConceptInstanceType() != flexoConceptInstanceType) {
				FlexoConcept oldValue = getFlexoConceptInstanceType();
				getAction().setFlexoConceptInstanceType(flexoConceptInstanceType);
				getPropertyChangeSupport().firePropertyChange("flexoConceptInstanceType", oldValue, flexoConceptInstanceType);
				checkValidity();
			}
		}

		public PrimitiveType getPrimitiveType() {
			return getAction().getPrimitiveType();
		}

		public void setPrimitiveType(PrimitiveType primitiveType) {
			if (getPrimitiveType() != primitiveType) {
				PrimitiveType oldValue = getPrimitiveType();
				getAction().setPrimitiveType(primitiveType);
				getPropertyChangeSupport().firePropertyChange("primitiveType", oldValue, primitiveType);
				checkValidity();
			}
		}

	}

}
