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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.annotations.DeclareResourceTypes;
import org.openflexo.foundation.fml.rm.ViewPointResourceFactory;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;

/**
 * This class defines and implements the FML technology adapter (Flexo Modelling Language)
 * 
 * @author sylvain
 * 
 */
@DeclareResourceTypes({ ViewPointResourceFactory.class })
public class FMLTechnologyAdapter extends TechnologyAdapter {

	private static final Logger logger = Logger.getLogger(FMLTechnologyAdapter.class.getPackage().getName());

	public FMLTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "FML technology adapter";
	}

	@Override
	public String getLocalizationDirectory() {
		return "FlexoLocalization/FMLTechnologyAdapter";
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// no specific binding factory for this technology
		return null;
	}

	@Override
	public FMLTechnologyContextManager createTechnologyContextManager(final FlexoResourceCenterService service) {
		return new FMLTechnologyContextManager(this, service);
	}

	@Override
	public FMLTechnologyContextManager getTechnologyContextManager() {
		return (FMLTechnologyContextManager) super.getTechnologyContextManager();
	}

	public FlexoServiceManager getServiceManager() {
		return this.getTechnologyAdapterService().getServiceManager();
	}

	public ViewPointLibrary getViewPointLibrary() {
		return this.getServiceManager().getViewPointLibrary();
	}

	public <I> ViewPointRepository<I> getViewPointRepository(FlexoResourceCenter<I> resourceCenter) {
		ViewPointRepository<I> returned = resourceCenter.retrieveRepository(ViewPointRepository.class, this);
		if (returned == null) {
			returned = new ViewPointRepository<I>(this, resourceCenter);
			resourceCenter.registerRepository(returned, ViewPointRepository.class, this);
		}
		return returned;
	}

	@Override
	public <I> boolean isIgnorable(final FlexoResourceCenter<I> resourceCenter, final I contents) {
		if (resourceCenter.isIgnorable(contents, this)) {
			return true;
		}
		// TODO: ignore .viewpoint subcontents
		return false;
	}

	@Override
	public String getIdentifier() {
		return "FML";
	}

	public ViewPointResourceFactory getViewPointResourceFactory() {
		return getResourceFactory(ViewPointResourceFactory.class);
	}

	public List<ViewPointRepository<?>> getViewPointRepositories() {
		List<ViewPointRepository<?>> returned = new ArrayList<>();
		for (FlexoResourceCenter<?> rc : getServiceManager().getResourceCenterService().getResourceCenters()) {
			returned.add(getViewPointRepository(rc));
		}
		return returned;
	}

	@Override
	public void notifyRepositoryStructureChanged() {
		super.notifyRepositoryStructureChanged();
		getPropertyChangeSupport().firePropertyChange("getViewPointRepositories()", null, getViewPointRepositories());
	}
}
