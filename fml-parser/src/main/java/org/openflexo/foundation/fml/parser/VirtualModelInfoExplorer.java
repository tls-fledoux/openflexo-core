/**
 * 
 * Copyright (c) 2019, Openflexo
 * 
 * This file is part of FML-parser, a component of the software infrastructure 
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

package org.openflexo.foundation.fml.parser;

import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Constant;
import org.openflexo.foundation.fml.ElementImportDeclaration;
import org.openflexo.foundation.fml.FMLCompilationUnit;
import org.openflexo.foundation.fml.NamespaceDeclaration;
import org.openflexo.foundation.fml.VirtualModel;
import org.openflexo.foundation.fml.parser.analysis.DepthFirstAdapter;
import org.openflexo.foundation.fml.parser.fmlnodes.ElementImportNode;
import org.openflexo.foundation.fml.parser.fmlnodes.NamespaceDeclarationNode;
import org.openflexo.foundation.fml.parser.node.AConceptDecl;
import org.openflexo.foundation.fml.parser.node.AFmlCompilationUnit;
import org.openflexo.foundation.fml.parser.node.AModelDecl;
import org.openflexo.foundation.fml.parser.node.ANamedUriImportImportDecl;
import org.openflexo.foundation.fml.parser.node.ANamespaceDecl;
import org.openflexo.foundation.fml.parser.node.ASingleAnnotationAnnotation;
import org.openflexo.foundation.fml.parser.node.AUriImportImportDecl;
import org.openflexo.foundation.fml.parser.node.AUseDecl;
import org.openflexo.foundation.fml.parser.node.Start;
import org.openflexo.foundation.fml.rm.CompilationUnitResource.VirtualModelInfo;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.toolbox.StringUtils;

/**
 * A visitor allowing to find some infos on a {@link VirtualModel}
 * 
 * @author sylvain
 * 
 */
public class VirtualModelInfoExplorer extends DepthFirstAdapter /*implements BindingEvaluationContext*/ {

	private static final Logger logger = Logger.getLogger(VirtualModelInfoExplorer.class.getPackage().getName());

	private final MainSemanticsAnalyzer analyzer;

	private VirtualModelInfo info;

	private FMLCompilationUnit compilationUnit;

	public VirtualModelInfoExplorer(Start tree, MainSemanticsAnalyzer analyzer) {
		super();
		this.analyzer = analyzer;
		info = new VirtualModelInfo();

		tree.apply(this);

		for (ElementImportDeclaration importDeclaration : compilationUnit.getElementImports()) {
			try {
				String importedResourceURI = importDeclaration.getResourceReference().getBindingValue(compilationUnit);
				// System.out.println("Found import " + importedResourceURI);
				if (StringUtils.isNotEmpty(importedResourceURI)) {
					info.dependencies.add(importedResourceURI);
				}
				else {
					logger.warning("Cannot not find resource identified by " + importDeclaration.getResourceReference());
				}
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullReferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ReflectiveOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// System.out.println(analyzer.getRawSource().debug());
	}

	public VirtualModelInfo getVirtualModelInfo() {
		return info;
	}

	@Override
	public void inAFmlCompilationUnit(AFmlCompilationUnit node) {

		super.inAFmlCompilationUnit(node);
		compilationUnit = analyzer.getFactory().newCompilationUnit();
	}

	@Override
	public void inAModelDecl(AModelDecl node) {
		super.inAModelDecl(node);
		info.name = node.getUidentifier().getText();
	}

	@Override
	public void inAConceptDecl(AConceptDecl node) {
		super.inAConceptDecl(node);
		info.flexoConcepts.add(node.getUidentifier().getText());
	}

	@Override
	public void inASingleAnnotationAnnotation(ASingleAnnotationAnnotation node) {
		super.inASingleAnnotationAnnotation(node);

		String key = analyzer.makeFullQualifiedIdentifier(node.getIdentifier());
		DataBinding<?> valueExpression = ExpressionFactory.makeDataBinding(node.getConditionalExp(), compilationUnit,
				BindingDefinitionType.GET, Object.class, analyzer, null);
		if (valueExpression.getExpression() instanceof Constant) {
			String text = analyzer.getText(node.getConditionalExp());
			if (text.startsWith("\"") && text.endsWith("\"")) {
				text = text.substring(1, text.length() - 1);
			}
			if (key.equalsIgnoreCase(VirtualModel.URI_KEY)) {
				info.uri = text;
			}
			if (key.equals(VirtualModel.VERSION_KEY)) {
				info.version = text;
			}
		}

	}

	@Override
	public void inAUseDecl(AUseDecl node) {
		// TODO Auto-generated method stub
		super.inAUseDecl(node);
		Class<? extends ModelSlot<?>> modelSlotClass = null;
		try {
			modelSlotClass = (Class<? extends ModelSlot<?>>) Class.forName(analyzer.makeFullQualifiedIdentifier(node.getIdentifier()));
			info.requiredModelSlotList = (info.requiredModelSlotList == null ? modelSlotClass.getName()
					: info.requiredModelSlotList + "," + modelSlotClass.getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void inANamespaceDecl(ANamespaceDecl node) {
		super.inANamespaceDecl(node);
		NamespaceDeclarationNode importNode = new NamespaceDeclarationNode(node, analyzer);
		NamespaceDeclaration nsDeclaration = importNode.getModelObject();
		compilationUnit.addToNamespaces(nsDeclaration);
	}

	@Override
	public void inAUriImportImportDecl(AUriImportImportDecl node) {
		super.inAUriImportImportDecl(node);
		ElementImportNode importNode = new ElementImportNode(node, analyzer);
		ElementImportDeclaration importDeclaration = importNode.getModelObject();
		compilationUnit.addToElementImports(importDeclaration);
	}

	@Override
	public void inANamedUriImportImportDecl(ANamedUriImportImportDecl node) {
		super.inANamedUriImportImportDecl(node);
		ElementImportNode importNode = new ElementImportNode(node, analyzer);
		ElementImportDeclaration importDeclaration = importNode.getModelObject();
		compilationUnit.addToElementImports(importDeclaration);
	}
}
