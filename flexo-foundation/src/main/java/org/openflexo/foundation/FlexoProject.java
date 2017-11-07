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

package org.openflexo.foundation;

import java.util.Date;
import java.util.List;

import org.openflexo.foundation.project.FlexoProjectImpl;
import org.openflexo.foundation.resource.FlexoProjectReference;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ProjectImportLoopException;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Embedded;
import org.openflexo.model.annotations.Finder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.validation.Validable;
import org.openflexo.toolbox.FlexoVersion;

/**
 * This class represents an Openflexo project.<br>
 * 
 * A {@link FlexoProject} is a logical container of resources located in project directory.<br>
 * 
 * The purpose of a {@link FlexoProject} is to get a self-contained and transportable set of resources.<br>
 * 
 * @author sylvain
 * @param <I>
 *            type of serialization artefact this project stores
 */
@ModelEntity
@ImplementationClass(FlexoProjectImpl.class)
@XMLElement
public interface FlexoProject<I>
		extends ResourceRepository<FlexoResource<?>, I>, FlexoResourceCenter<I>, Validable, ResourceData<FlexoProject<I>> {

	@PropertyIdentifier(type = String.class)
	public static final String URI_KEY = "uri";
	@PropertyIdentifier(type = Date.class)
	public static final String CREATION_DATE_KEY = "creationDate";
	@PropertyIdentifier(type = String.class)
	public static final String CREATION_USER_ID_KEY = "creationUserId";
	@PropertyIdentifier(type = FlexoVersion.class)
	public static final String PROJECT_VERSION_KEY = "projectVersion";
	@PropertyIdentifier(type = FlexoProjectReference.class, cardinality = Cardinality.LIST)
	public static final String IMPORTED_PROJECTS = "importedProjects";
	@PropertyIdentifier(type = String.class)
	String DESCRIPTION_KEY = "description";

	@Getter(value = DESCRIPTION_KEY)
	@XMLAttribute
	public String getDescription();

	@Setter(DESCRIPTION_KEY)
	public void setDescription(String description);

	@Getter(value = URI_KEY)
	@XMLAttribute
	public String getURI();

	@Setter(URI_KEY)
	public void setURI(String projectURI);

	@Getter(value = CREATION_DATE_KEY)
	@XMLAttribute
	public Date getCreationDate();

	@Setter(CREATION_DATE_KEY)
	public void setCreationDate(Date creationDate);

	@Getter(value = CREATION_USER_ID_KEY)
	@XMLAttribute
	public String getCreationUserId();

	@Setter(CREATION_USER_ID_KEY)
	public void setCreationUserId(String creationUser);

	@Getter(value = PROJECT_VERSION_KEY, isStringConvertable = true)
	@XMLAttribute
	public FlexoVersion getProjectVersion();

	@Setter(PROJECT_VERSION_KEY)
	public void setProjectVersion(FlexoVersion version);

	@Finder(collection = IMPORTED_PROJECTS, attribute = FlexoProjectReference.URI, isMultiValued = false)
	public FlexoProjectReference getProjectReferenceWithURI(String uri);

	public FlexoProjectReference getProjectReferenceWithURI(String projectURI, boolean searchRecursively);

	@Finder(collection = IMPORTED_PROJECTS, attribute = FlexoProjectReference.NAME, isMultiValued = true)
	public List<FlexoProjectReference> getProjectReferenceWithName(String name);

	public List<FlexoProjectReference> getProjectReferenceWithName(String name, boolean searchRecursively);

	@Getter(value = IMPORTED_PROJECTS, cardinality = Cardinality.LIST, inverse = FlexoProjectReference.PROJECT_DATA)
	@XMLElement(xmlTag = "ImportedProjects")
	@Embedded
	public List<FlexoProjectReference> getImportedProjects();

	@Setter(value = IMPORTED_PROJECTS)
	public void setImportedProjects(List<FlexoProjectReference> importedProjects);

	@Adder(IMPORTED_PROJECTS)
	public void addToImportedProjects(FlexoProjectReference projectReference)
			throws ProjectImportLoopException, ProjectLoadingCancelledException;

	@Remover(value = IMPORTED_PROJECTS)
	public void removeFromImportedProjects(FlexoProjectReference projectReference);

	public String canImportProject(FlexoProject project);

	public void removeFromImportedProjects(FlexoProject project);

}
