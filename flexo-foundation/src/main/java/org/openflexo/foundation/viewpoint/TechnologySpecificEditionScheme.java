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

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Implemented by all {@link FlexoBehaviour} specific to a technology
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
public interface TechnologySpecificEditionScheme extends FlexoBehaviour {

	/**
	 * Return the {@link TechnologyAdapter} of technical space where this concept exists
	 * 
	 * @return
	 */
	public TechnologyAdapter getTechnologyAdapter();
}
