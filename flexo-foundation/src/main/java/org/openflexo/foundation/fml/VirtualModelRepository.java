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

package org.openflexo.foundation.fml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.fml.rm.VirtualModelResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResourceRepository;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;

/**
 * A {@link VirtualModelRepository} references {@link VirtualModelResource} stored in a given {@link FlexoResourceCenter}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(VirtualModelRepository.VirtualModelRepositoryImpl.class)
public interface VirtualModelRepository<I>
		extends TechnologyAdapterResourceRepository<VirtualModelResource, FMLTechnologyAdapter, VirtualModel, I> {

	public List<VirtualModelResource> getTopLevelVirtualModelResources();

	public static <I> VirtualModelRepository<I> instanciateNewRepository(FMLTechnologyAdapter technologyAdapter,
			FlexoResourceCenter<I> resourceCenter) throws IOException {
		ModelFactory factory;
		try {
			factory = new ModelFactory(VirtualModelRepository.class);
			VirtualModelRepository<I> newRepository = factory.newInstance(VirtualModelRepository.class);
			newRepository.setTechnologyAdapter(technologyAdapter);
			newRepository.setResourceCenter(resourceCenter);
			newRepository.setBaseArtefact(resourceCenter.getBaseArtefact());
			newRepository.getRootFolder().setRepositoryContext(null);
			return newRepository;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static abstract class VirtualModelRepositoryImpl<I>
			extends TechnologyAdapterResourceRepositoryImpl<VirtualModelResource, FMLTechnologyAdapter, VirtualModel, I>
			implements VirtualModelRepository<I> {

		@SuppressWarnings("unused")
		private static final Logger logger = Logger.getLogger(TechnologyAdapterResourceRepository.class.getPackage().getName());

		private List<VirtualModelResource> topLevelVirtualModelResources = null;

		@Override
		public List<VirtualModelResource> getTopLevelVirtualModelResources() {
			if (topLevelVirtualModelResources == null) {
				topLevelVirtualModelResources = new ArrayList<>();
				for (VirtualModelResource r : getAllResources()) {
					if (r.getContainer() == null) {
						topLevelVirtualModelResources.add(r);
					}
				}
			}
			return topLevelVirtualModelResources;
		}

		@Override
		public void unregisterResource(VirtualModelResource flexoResource) {
			super.unregisterResource(flexoResource);
			topLevelVirtualModelResources = null;
			getPropertyChangeSupport().firePropertyChange("topLevelVirtualModelResources", null, getTopLevelVirtualModelResources());
		}

		@Override
		public void registerResource(VirtualModelResource resource, RepositoryFolder<VirtualModelResource, I> parentFolder) {
			super.registerResource(resource, parentFolder);
			topLevelVirtualModelResources = null;
			getPropertyChangeSupport().firePropertyChange("topLevelVirtualModelResources", null, getTopLevelVirtualModelResources());
		}

		@Override
		public void registerResource(VirtualModelResource resource, VirtualModelResource parentResource) {
			super.registerResource(resource, parentResource);
			topLevelVirtualModelResources = null;
			getPropertyChangeSupport().firePropertyChange("topLevelVirtualModelResources", null, getTopLevelVirtualModelResources());
		}

	}
}
