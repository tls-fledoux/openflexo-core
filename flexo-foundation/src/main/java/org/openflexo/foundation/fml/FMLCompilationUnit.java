/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Fml-parser, a component of the software infrastructure 
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

import java.util.List;

import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FMLCompilationUnit.FMLCompilationUnitImpl.class)
public interface FMLCompilationUnit extends FMLObject, FMLPrettyPrintable {

	@PropertyIdentifier(type = JavaImportDeclaration.class, cardinality = Cardinality.LIST)
	public static final String JAVA_IMPORTS_KEY = "javaImports";
	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";

	/**
	 * Return list of {@link JavaImportDeclaration} explicitely declared in this {@link FMLCompilationUnit}
	 * 
	 * @return
	 */
	@Getter(value = JAVA_IMPORTS_KEY, cardinality = Cardinality.LIST, inverse = JavaImportDeclaration.COMPILATION_UNIT_KEY)
	@XMLElement
	@CloningStrategy(StrategyType.CLONE)
	public List<JavaImportDeclaration> getJavaImports();

	@Adder(JAVA_IMPORTS_KEY)
	public void addToJavaImports(JavaImportDeclaration javaImportDeclaration);

	@Remover(JAVA_IMPORTS_KEY)
	public void removeFromJavaImports(JavaImportDeclaration javaImportDeclaration);

	/**
	 * Return the {@link VirtualModel} defined by this FMLCompilationUnit
	 * 
	 * @return
	 */
	@Getter(value = VIRTUAL_MODEL_KEY)
	public VirtualModel getVirtualModel();

	@Setter(VIRTUAL_MODEL_KEY)
	public void setVirtualModel(VirtualModel virtualModel);

	public abstract class FMLCompilationUnitImpl extends FMLObjectImpl implements FMLCompilationUnit {

		@Override
		public FMLModelFactory getFMLModelFactory() {

			FMLModelFactory returned = super.getFMLModelFactory();
			if (returned == null) {
				return getDeserializationFactory();
			}
			return returned;
		}

		@Override
		public VirtualModel getResourceData() {
			return getVirtualModel();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return "<not_implemented:" + getStringRepresentation() + ">";
		}

	}

}
