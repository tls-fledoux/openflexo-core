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

package org.openflexo.foundation.fml.rt.editionaction;

import java.util.logging.Logger;

import org.openflexo.foundation.fml.FMLMigration;
import org.openflexo.foundation.fml.rt.FMLExecutionException;
import org.openflexo.foundation.fml.rt.FMLRTVirtualModelInstance;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;

/**
 * Generic base action used to instanciate a {@link FMLRTVirtualModelInstance} at top-level or in a given {@link FMLRTVirtualModelInstance}.
 * 
 * @author sylvain
 * 
 * @param <FCI>
 *            type of {@link FMLRTVirtualModelInstance} beeing created by this action
 */

@ModelEntity(isAbstract = true)
@ImplementationClass(AbstractAddFMLRTVirtualModelInstance.AbstractAddFMLRTVirtualModelInstanceImpl.class)
@FMLMigration("ExpressionAction should be used instead")
@Deprecated
public interface AbstractAddFMLRTVirtualModelInstance
		extends AbstractAddVirtualModelInstance<FMLRTVirtualModelInstance, FMLRTVirtualModelInstance> {

	public static abstract class AbstractAddFMLRTVirtualModelInstanceImpl
			extends AbstractAddVirtualModelInstanceImpl<FMLRTVirtualModelInstance, FMLRTVirtualModelInstance>
			implements AbstractAddFMLRTVirtualModelInstance {

		static final Logger logger = Logger.getLogger(AbstractAddFMLRTVirtualModelInstance.class.getPackage().getName());

		@Override
		public Class<FMLRTVirtualModelInstance> getVirtualModelInstanceClass() {
			return FMLRTVirtualModelInstance.class;
		}

		@Override
		public FMLRTVirtualModelInstance execute(RunTimeEvaluationContext evaluationContext) throws FMLExecutionException {
			// System.out.println("Now create a FMLRTVirtualModelInstance");
			return super.execute(evaluationContext);
		}
	}

}
