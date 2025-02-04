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

import java.io.FileNotFoundException;

import org.openflexo.connie.type.CustomTypeFactory;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

/**
 * Represent the type of an instance of a {@link VirtualModel}
 * 
 * @author sylvain
 * 
 */
public class VirtualModelInstanceType extends FlexoConceptInstanceType {

	public static VirtualModelInstanceType UNDEFINED_VIRTUAL_MODEL_INSTANCE_TYPE = new VirtualModelInstanceType((VirtualModel) null);

	public VirtualModelInstanceType(VirtualModel aVirtualModel) {
		super(aVirtualModel);
	}

	public VirtualModelInstanceType(String virtualModelURI, CustomTypeFactory<?> factory) {
		super(virtualModelURI, factory);
	}

	@Override
	public Class<?> getBaseClass() {
		return FMLRTVirtualModelInstance.class;
	}

	public VirtualModel getVirtualModel() {
		return (VirtualModel) getFlexoConcept();
	}

	public void redefineWithURI(String virtualModelURI) {
		this.conceptURI = virtualModelURI;
	}

	@Override
	protected void resolve(CustomTypeFactory<?> factory) {
		if (factory instanceof VirtualModelInstanceTypeFactory) {
			VirtualModel virtualModel;
			virtualModel = ((VirtualModelInstanceTypeFactory) factory).resolveVirtualModel(this);
			if (virtualModel != null) {
				flexoConcept = virtualModel;
				this.customTypeFactory = null;
			}
		}
		else {
			super.resolve(factory);
		}
	}

	@Override
	public String simpleRepresentation() {
		if (flexoConcept == null) {
			if (getLastPath() != null) {
				return getLastPath();
			}
		}
		return super.simpleRepresentation();
	}

	public static VirtualModelInstanceType getVirtualModelInstanceType(VirtualModel aVirtualModel) {
		if (aVirtualModel != null) {
			return (VirtualModelInstanceType) aVirtualModel.getInstanceType();
		}
		// logger.warning("Trying to get a VirtualModelInstanceType for a null VirtualModel");
		return UNDEFINED_VIRTUAL_MODEL_INSTANCE_TYPE;
	}

	public interface VirtualModelInstanceTypeFactory<T extends VirtualModelInstanceType> extends CustomTypeFactory<T> {
		public VirtualModel resolveVirtualModel(T typeToResolve);
	}

	/**
	 * Base implementation
	 * 
	 * @author sylvain
	 * 
	 */
	public static abstract class AbstractVirtualModelInstanceTypeFactory<T extends VirtualModelInstanceType, TA extends TechnologyAdapter<TA>>
			extends TechnologyAdapterTypeFactory<T, TA> implements VirtualModelInstanceTypeFactory<T> {

		public AbstractVirtualModelInstanceTypeFactory(TA technologyAdapter) {
			super(technologyAdapter);
		}

		public abstract T getType(VirtualModel virtualModel);

		public abstract T getType(String configuration, CustomTypeFactory<?> factory);

		@Override
		public T makeCustomType(String configuration) {

			VirtualModel virtualModel = null;

			if (configuration != null) {
				CompilationUnitResource compilationUnitResource = getTechnologyAdapter().getTechnologyAdapterService().getServiceManager()
						.getVirtualModelLibrary().getCompilationUnitResource(configuration);
				if (compilationUnitResource != null && compilationUnitResource.isLoaded()) {
					virtualModel = compilationUnitResource.getLoadedResourceData().getVirtualModel();
				}
			}
			else {
				virtualModel = getVirtualModelType();
			}

			if (virtualModel != null) {
				return getType(virtualModel);
			}
			// We don't return UNDEFINED_FLEXO_CONCEPT_INSTANCE_TYPE because we want here a mutable type
			// if FlexoConcept might be resolved later
			return getType(configuration, this);
		}

		private VirtualModel virtualModelType;

		public VirtualModel getVirtualModelType() {
			return virtualModelType;
		}

		public void setVirtualModelType(VirtualModel virtualModelType) {
			if (virtualModelType != this.virtualModelType) {
				VirtualModel oldVirtualModelType = this.virtualModelType;
				this.virtualModelType = virtualModelType;
				getPropertyChangeSupport().firePropertyChange("virtualModelType", oldVirtualModelType, virtualModelType);
			}
		}

		@Override
		public String toString() {
			return "Instance of VirtualModel";
		}

		@Override
		public void configureFactory(VirtualModelInstanceType type) {
			if (type != null) {
				setVirtualModelType(type.getVirtualModel());
			}
		}
	}

	/**
	 * Factory for {@link VirtualModelInstanceType} instances
	 * 
	 * @author sylvain
	 * 
	 */
	public static class DefaultVirtualModelInstanceTypeFactory
			extends AbstractVirtualModelInstanceTypeFactory<VirtualModelInstanceType, FMLTechnologyAdapter> {

		public DefaultVirtualModelInstanceTypeFactory(FMLTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@Override
		public Class<VirtualModelInstanceType> getCustomType() {
			return VirtualModelInstanceType.class;
		}

		@Override
		public VirtualModelInstanceType getType(String configuration, CustomTypeFactory<?> factory) {
			return new VirtualModelInstanceType(configuration, this);
		}

		@Override
		public VirtualModelInstanceType getType(VirtualModel virtualModel) {
			return getVirtualModelInstanceType(virtualModel);
		}

		@Override
		public VirtualModel resolveVirtualModel(VirtualModelInstanceType typeToResolve) {
			try {
				return getTechnologyAdapter().getTechnologyAdapterService().getServiceManager().getVirtualModelLibrary()
						.getVirtualModel(typeToResolve.conceptURI);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

}
