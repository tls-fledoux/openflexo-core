/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-rt-technologyadapter-ui, a component of the software infrastructure 
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

package org.openflexo.fml.rt.controller.widget;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.components.widget.FIBProjectObjectSelector;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.FlexoConcept;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.fml.ViewPoint;
import org.openflexo.foundation.fml.ViewType;
import org.openflexo.foundation.fml.VirtualModelInstanceType;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.fml.rt.FlexoConceptInstance;
import org.openflexo.foundation.fml.rt.View;
import org.openflexo.foundation.fml.rt.ViewRepository;
import org.openflexo.foundation.fml.rt.VirtualModelInstance;
import org.openflexo.foundation.fml.rt.rm.ViewResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;

/**
 * Widget allowing to select an object managed by {@link FMLRTTechnologyAdapter}
 * 
 * The scope of searched object is either:
 * <ul>
 * <li>the whole environment (all is foundable in all resource centers), if {@link FlexoServiceManager} has been set</li>
 * <li>a resource center, if {@link FlexoResourceCenter} has been set</li>
 * <li>a view, if {@link View} has been set</li>
 * <li>a virtual model instance, if {@link VirtualModelInstance} has been set</li>
 * </ul>
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public abstract class FIBAbstractFMLRTObjectSelector<T extends FlexoConceptInstance> extends FIBProjectObjectSelector<T> {

	static final Logger logger = Logger.getLogger(FIBAbstractFMLRTObjectSelector.class.getPackage().getName());

	private FlexoServiceManager serviceManager;
	private FlexoResourceCenter<?> resourceCenter;
	private View view;
	private AbstractVirtualModelInstance<?, ?> virtualModelInstance;
	private Type expectedType;
	private FlexoConceptInstanceType defaultExpectedType;

	public FIBAbstractFMLRTObjectSelector(T editedObject) {
		super(editedObject);
		defaultExpectedType = editedObject != null ? FlexoConceptInstanceType.getFlexoConceptInstanceType(editedObject.getFlexoConcept())
				: FlexoConceptInstanceType.UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE;
	}

	@Override
	public void delete() {
		super.delete();
		view = null;
		virtualModelInstance = null;
		expectedType = null;
		defaultExpectedType = null;
	}

	@Override
	public String renderedString(T editedObject) {
		if (editedObject != null) {
			return editedObject.getStringRepresentation();
		}
		return "";
	}

	@Override
	public void setServiceManager(FlexoServiceManager serviceManager) {
		super.setServiceManager(serviceManager);
		getPropertyChangeSupport().firePropertyChange("rootObject", null, getRootObject());
	}

	public Object getRootObject() {

		if (getVirtualModelInstance() != null) {
			return getVirtualModelInstance();
		}
		else if (getView() != null) {
			return getView();
		}
		else if (getProject() != null) {
			FlexoServiceManager sm = getProject().getServiceManager();
			FMLRTTechnologyAdapter fmlRTTA = sm.getTechnologyAdapterService().getTechnologyAdapter(FMLRTTechnologyAdapter.class);
			return fmlRTTA.getViewRepository(getProject()).getRootFolder();
		}
		else if (getResourceCenter() != null) {
			FlexoServiceManager sm = getResourceCenter().getServiceManager();
			FMLRTTechnologyAdapter fmlRTTA = sm.getTechnologyAdapterService().getTechnologyAdapter(FMLRTTechnologyAdapter.class);
			return fmlRTTA.getViewRepository(getResourceCenter()).getRootFolder();
		}
		else if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(FMLRTTechnologyAdapter.class);
		}
		return null;
	}

	/*public List<FlexoConceptInstance> getEPInstances(FlexoConcept ep) {
	
		if (getVirtualModelInstance() != null) {
			if (getVirtualModelInstance().getVirtualModel() == ep) {
				return Collections.singletonList((FlexoConceptInstance) getVirtualModelInstance());
			}
			return getVirtualModelInstance().getFlexoConceptInstances(ep);
		}
		else if (getView() != null) {
			List<FlexoConceptInstance> returned = new ArrayList<FlexoConceptInstance>();
			for (AbstractVirtualModelInstance<?, ?> vmi : getView().getVirtualModelInstances()) {
				returned.addAll(vmi.getFlexoConceptInstances(ep));
			}
			return returned;
		}
		else if (getProject() != null) {
			List<FlexoConceptInstance> returned = new ArrayList<FlexoConceptInstance>();
			for (ViewResource vr : getProject().getViewLibrary().getAllResources()) {
				for (AbstractVirtualModelInstance<?, ?> vmi : vr.getView().getVirtualModelInstances()) {
					returned.addAll(vmi.getFlexoConceptInstances(ep));
				}
			}
			return returned;
		}
		return null;
	}*/

	@Override
	public boolean isAcceptableValue(Object o) {
		if (!super.isAcceptableValue(o)) {
			return false;
		}
		if (!(o instanceof FlexoConceptInstance)) {
			return false;
		}
		if (!(getExpectedType() instanceof FlexoConceptInstanceType)) {
			return false;
		}
		FlexoConceptInstance fci = (FlexoConceptInstance) o;
		FlexoConceptInstanceType fciType = (FlexoConceptInstanceType) getExpectedType();
		return (fciType.getFlexoConcept() == null) || (fciType.getFlexoConcept().isAssignableFrom(fci.getFlexoConcept()));

	}

	public FlexoResourceCenter<?> getResourceCenter() {
		return resourceCenter;
	}

	public void setResourceCenter(FlexoResourceCenter<?> resourceCenter) {
		if ((resourceCenter == null && this.resourceCenter != null)
				|| (resourceCenter != null && !resourceCenter.equals(this.resourceCenter))) {
			FlexoResourceCenter<?> oldValue = this.resourceCenter;
			this.resourceCenter = resourceCenter;
			getPropertyChangeSupport().firePropertyChange("resourceCenter", oldValue, resourceCenter);
			getPropertyChangeSupport().firePropertyChange("rootObject", null, getRootObject());
		}
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		if ((view == null && this.view != null) || (view != null && !view.equals(this.view))) {
			View oldValue = this.view;
			this.view = view;
			getPropertyChangeSupport().firePropertyChange("view", oldValue, view);
			getPropertyChangeSupport().firePropertyChange("rootObject", null, getRootObject());
		}
	}

	public AbstractVirtualModelInstance<?, ?> getVirtualModelInstance() {
		return virtualModelInstance;
	}

	public void setVirtualModelInstance(AbstractVirtualModelInstance<?, ?> virtualModelInstance) {

		if ((virtualModelInstance == null && this.virtualModelInstance != null)
				|| (virtualModelInstance != null && !virtualModelInstance.equals(this.virtualModelInstance))) {
			AbstractVirtualModelInstance<?, ?> oldValue = this.virtualModelInstance;
			this.virtualModelInstance = virtualModelInstance;
			getPropertyChangeSupport().firePropertyChange("virtualModelInstance", oldValue, virtualModelInstance);
			getPropertyChangeSupport().firePropertyChange("rootObject", null, getRootObject());
		}
	}

	public Type getExpectedType() {
		if (expectedType == null) {
			return defaultExpectedType;
		}
		return expectedType;
	}

	public void setExpectedType(Type expectedType) {

		if ((expectedType == null && this.expectedType != null) || (expectedType != null && !expectedType.equals(this.expectedType))) {
			Type oldValue = this.expectedType;
			this.expectedType = expectedType;
			getPropertyChangeSupport().firePropertyChange("expectedType", oldValue, expectedType);
		}
	}

	public List<ViewResource> getViewResources(RepositoryFolder<?, ?> folder) {
		if (folder.getResourceRepository() instanceof ViewRepository) {
			return (List) folder.getResources();
		}
		return null;
	}

	public boolean isFolderVisible(RepositoryFolder<?, ?> folder) {
		if (!folder.containsResources()) {
			return false;
		}
		if (getExpectedType() instanceof FlexoConceptInstanceType) {
			for (ViewResource r : getViewResources(folder)) {
				if (r.isLoaded() && isViewVisible(r.getLoadedResourceData())) {
					return true;
				}
			}
			// System.out.println("excluding folder " + view);
			return false;
		}
		return true;
	}

	public boolean isViewVisible(View view) {
		if (getExpectedType() instanceof ViewType) {
			// We are expecting a View of following type
			ViewPoint viewPointType = ((ViewType) getExpectedType()).getViewPoint();
			return (viewPointType == null) || (viewPointType.isAssignableFrom(view.getVirtualModel()));

		}
		else if ((getExpectedType() instanceof VirtualModelInstanceType) || (getExpectedType() instanceof FlexoConceptInstanceType)) {
			for (AbstractVirtualModelInstance<?, ?> vmi : view.getVirtualModelInstances()) {
				if (isVirtualModelInstanceVisible(vmi)) {
					return true;
				}
			}
			// System.out.println("excluding view " + view);
			return false;
		}
		return true;
	}

	public boolean isVirtualModelInstanceVisible(AbstractVirtualModelInstance<?, ?> virtualModelInstance) {
		if (getExpectedType() instanceof VirtualModelInstanceType) {
			// We are expecting a VMI of following type
			VirtualModel vmType = ((VirtualModelInstanceType) getExpectedType()).getVirtualModel();
			return (vmType == null) || (vmType.isAssignableFrom(virtualModelInstance.getVirtualModel()));

		}
		if (getExpectedType() instanceof FlexoConceptInstanceType) {
			// In this case, we should display VMI whose contains FCI
			FlexoConcept conceptType = ((FlexoConceptInstanceType) getExpectedType()).getFlexoConcept();
			if (conceptType == null) {
				return true;
			}
			VirtualModel vmType = conceptType.getOwningVirtualModel();
			/*if (!vmType.isAssignableFrom(virtualModelInstance.getVirtualModel())) {
				System.out.println("excluding " + virtualModelInstance);
			}*/
			return vmType.isAssignableFrom(virtualModelInstance.getVirtualModel());
		}
		return true;
	}

}
