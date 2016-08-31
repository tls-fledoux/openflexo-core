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

package org.openflexo.foundation.fml.rt.rm;

import org.openflexo.foundation.fml.AbstractVirtualModel;
import org.openflexo.foundation.fml.rm.AbstractVirtualModelResource;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstance;
import org.openflexo.foundation.fml.rt.AbstractVirtualModelInstanceModelFactory;
import org.openflexo.foundation.fml.rt.FMLRTTechnologyAdapter;
import org.openflexo.foundation.resource.FlexoProjectResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.PamelaResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;

/**
 * Base API for a {@link FlexoResource} encoding a {@link AbstractVirtualModelInstance}
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractVirtualModelInstanceResourceImpl.class)
public interface AbstractVirtualModelInstanceResource<VMI extends AbstractVirtualModelInstance<VMI, VM>, VM extends AbstractVirtualModel<VM>>
		extends PamelaResource<VMI, AbstractVirtualModelInstanceModelFactory<?>>, FlexoProjectResource<VMI>,
		TechnologyAdapterResource<VMI, FMLRTTechnologyAdapter> {

	public static final String VIRTUAL_MODEL_RESOURCE = "virtualModelResource";

	@Getter(value = VIRTUAL_MODEL_RESOURCE, ignoreType = true)
	public AbstractVirtualModelResource<VM> getVirtualModelResource();

	@Setter(VIRTUAL_MODEL_RESOURCE)
	public void setVirtualModelResource(AbstractVirtualModelResource<VM> virtualModelResource);

	public VMI getVirtualModelInstance();

	@Getter(value = CONTAINER, inverse = CONTENTS)
	@Override
	public ViewResource getContainer();

}
