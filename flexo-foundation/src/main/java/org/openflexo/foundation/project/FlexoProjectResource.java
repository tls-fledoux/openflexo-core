/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexodiagram, a component of the software infrastructure 
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

package org.openflexo.foundation.project;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.PamelaXMLSerializableResource;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.XMLElement;

/**
 * This is the {@link FlexoResource} encoding a {@link FlexoProject}
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(FlexoProjectResourceImpl.class)
@XMLElement
public interface FlexoProjectResource<I> extends PamelaXMLSerializableResource<FlexoProject<I>, FlexoProjectFactory> {

	public FlexoProject<I> getFlexoProject();

	/**
	 * Return delegate RC if project is stand-alone (not contained in another RC)
	 */
	public FlexoResourceCenter<I> getDelegateResourceCenter();

	public void setDelegateResourceCenter(FlexoResourceCenter<I> delegateResourceCenter);

	/**
	 * When true, indicates that this {@link FlexoProject} has no parent {@link FlexoResourceCenter}
	 * 
	 * @return
	 */
	public boolean isStandAlone();

	public void setClosing();

	public void setClosed();
}
