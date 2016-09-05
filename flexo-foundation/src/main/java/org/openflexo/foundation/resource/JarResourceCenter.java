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

package org.openflexo.foundation.resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.FMLTechnologyAdapter;
import org.openflexo.foundation.fml.ViewPointRepository;
import org.openflexo.foundation.resource.InJarFlexoIODelegate.InJarFlexoIODelegateImpl;
import org.openflexo.foundation.task.Progress;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.rm.ClasspathResourceLocatorImpl;
import org.openflexo.rm.InJarResourceImpl;
import org.openflexo.rm.JarResourceImpl;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.ClassPathUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

/**
 * A Jar resource center references a set of resources inside a Jar.
 * 
 * @author Vincent, xtof
 *
 * @param <R>
 */
public class JarResourceCenter<R extends FlexoResource<?>> extends ResourceRepository<FlexoResource<?>, InJarResourceImpl>
		implements FlexoResourceCenter<InJarResourceImpl> {

	protected static final Logger logger = Logger.getLogger(ResourceRepository.class.getPackage().getName());

	/**
	 * A jar file the resource center might interpret
	 */
	private final JarFile jarFile;

	/**
	 * A string that is used to identify the JarRC and build uri of resources included in the RC
	 * 
	 */
	private String rcBaseUri;

	/**
	 * A JarResource is the main element of a JarResource center. It contains a set of InJarResource elements.
	 */
	private JarResourceImpl jarResourceImpl;

	private final FlexoResourceCenterService rcService;

	// private final Map<TechnologyAdapter, ResourceRepository<?>> globalRepositories = new HashMap<>();

	/**
	 * Contructor based on a given JarResource
	 * 
	 * @param jarResourceImpl
	 */
	public JarResourceCenter(JarResourceImpl jarResourceImpl, FlexoResourceCenterService rcService) {
		super(null, null);
		this.rcService = rcService;
		this.jarFile = jarResourceImpl.getJarfile();
		this.jarResourceImpl = jarResourceImpl;

	}

	/**
	 * Constructor based on a given jarFile
	 * 
	 * @param jarFile
	 */
	public JarResourceCenter(JarFile jarFile, FlexoResourceCenterService rcService) {
		super(null, null);
		this.rcService = rcService;
		ClasspathResourceLocatorImpl locator = (ClasspathResourceLocatorImpl) ResourceLocator
				.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class);
		jarResourceImpl = (JarResourceImpl) locator.locateResource(jarFile.getName());
		if (jarResourceImpl == null) {
			try {
				jarResourceImpl = new JarResourceImpl(ResourceLocator.getInstanceForLocatorClass(ClasspathResourceLocatorImpl.class),
						jarFile);
			} catch (MalformedURLException e) {
				logger.warning("Unable to create a Jar Resource Center for jar " + jarFile.getName());
			}
		}
		this.jarFile = jarFile;
		locator.getJarResourcesList().put(jarResourceImpl.getRelativePath(), jarResourceImpl);
	}

	@Override
	public JarResourceCenter<R> getResourceCenter() {
		return this;
	}

	public JarResourceImpl getJarResourceImpl() {
		return jarResourceImpl;
	}

	@Override
	public String toString() {
		return super.toString() + " jar=" + (jarResourceImpl != null ? jarResourceImpl.toString() : null);
	}

	@Override
	public FlexoServiceManager getServiceManager() {
		if (getFlexoResourceCenterService() == null) {
			return super.getServiceManager();
		}
		return getFlexoResourceCenterService().getServiceManager();
	}

	public FlexoResourceCenterService getFlexoResourceCenterService() {
		return rcService;
	}

	@Override
	public void activateTechnology(TechnologyAdapter technologyAdapter) {

		logger.info("Activating resource center " + this + " with adapter " + technologyAdapter.getName());
		Progress.progress(technologyAdapter.getLocales().localizedForKey("initializing_adapter") + " " + technologyAdapter.getName());
		technologyAdapter.initializeResourceCenter(this);
	}

	/**
	 * Finalize the FlexoResourceCenter<br>
	 * 
	 * @param technologyAdapterService
	 */
	@Override
	public void disactivateTechnology(TechnologyAdapter technologyAdapter) {

		logger.info("Disactivating resource center " + this + " with adapter " + technologyAdapter.getName());
		// TODO
	}

	/**
	 * Returns an iterator over contained InJarResources
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<InJarResourceImpl> iterator() {
		return (Iterator<InJarResourceImpl>) getJarResourceImpl().getContents().iterator();
	}

	@Override
	public String getName() {
		if (jarFile != null) {
			return jarFile.getName();
		}
		return "unset";
	}

	private final HashMap<TechnologyAdapter, HashMap<Class<? extends ResourceRepository<?, InJarResourceImpl>>, ResourceRepository<?, InJarResourceImpl>>> repositories = new HashMap<>();

	private HashMap<Class<? extends ResourceRepository<?, InJarResourceImpl>>, ResourceRepository<?, InJarResourceImpl>> getRepositoriesForAdapter(
			TechnologyAdapter technologyAdapter) {
		HashMap<Class<? extends ResourceRepository<?, InJarResourceImpl>>, ResourceRepository<?, InJarResourceImpl>> map = repositories
				.get(technologyAdapter);
		if (map == null) {
			map = new HashMap<Class<? extends ResourceRepository<?, InJarResourceImpl>>, ResourceRepository<?, InJarResourceImpl>>();
			repositories.put(technologyAdapter, map);
		}
		return map;
	}

	@SuppressWarnings({ "hiding", "unchecked" })
	@Override
	public final <R extends ResourceRepository<?, InJarResourceImpl>> R getRepository(Class<? extends R> repositoryType,
			TechnologyAdapter technologyAdapter) {
		HashMap<Class<? extends ResourceRepository<?, InJarResourceImpl>>, ResourceRepository<?, InJarResourceImpl>> map = getRepositoriesForAdapter(
				technologyAdapter);
		return (R) map.get(repositoryType);
	}

	@SuppressWarnings("hiding")
	@Override
	public final <R extends ResourceRepository<?, InJarResourceImpl>> void registerRepository(R repository,
			Class<? extends R> repositoryType, TechnologyAdapter technologyAdapter) {
		HashMap<Class<? extends ResourceRepository<?, InJarResourceImpl>>, ResourceRepository<?, InJarResourceImpl>> map = getRepositoriesForAdapter(
				technologyAdapter);
		if (map.get(repositoryType) == null) {
			map.put(repositoryType, repository);
		}
		else {
			logger.warning("Repository already registered: " + repositoryType + " for " + repository);
		}
	}

	@Override
	public Collection<ResourceRepository<?, InJarResourceImpl>> getRegistedRepositories(TechnologyAdapter technologyAdapter) {
		return getRepositoriesForAdapter(technologyAdapter).values();
	}

	/**
	 * Register global repository for this resource center<br>
	 * It is stated that the global repository contains all resources which supplied technology adapter has discovered and may interpret<br>
	 * This is the resource repository which is generally given in GUIs (such as browsers) to display the contents of a resource center for
	 * a given technology
	 * 
	 * @param repository
	 * @param technologyAdapter
	 */
	/*@Override
	public final void registerGlobalRepository(ResourceRepository<?> repository, TechnologyAdapter technologyAdapter) {
		if (repository != null && technologyAdapter != null) {
			globalRepositories.put(technologyAdapter, repository);
		}
	}*/

	/**
	 * Return the global repository for this resource center and for supplied technology adapter<br>
	 * It is stated that the global repository contains all resources which supplied technology adapter has discovered and may interpret<br>
	 * This is the resource repository which is generally given in GUIs (such as browsers) to display the contents of a resource center for
	 * a given technology
	 * 
	 * @param technologyAdapter
	 * @return
	 */
	/*@Override
	public ResourceRepository<?> getGlobalRepository(TechnologyAdapter technologyAdapter) {
		if (technologyAdapter != null) {
			return globalRepositories.get(technologyAdapter);
		}
		return null;
	}*/

	@Override
	public <T extends ResourceData<T>> List<FlexoResource<T>> retrieveResource(String uri, Class<T> type, IProgress progress) {
		// TODO: provide support for class and version
		FlexoResource<T> uniqueResource = retrieveResource(uri, null, null, progress);
		return Collections.singletonList(uniqueResource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ResourceData<T>> FlexoResource<T> retrieveResource(String uri, FlexoVersion version, Class<T> type,
			IProgress progress) {
		// TODO: provide support for class and version
		return (FlexoResource<T>) retrieveResource(uri, progress);
	}

	@Override
	public FlexoResource<?> retrieveResource(String uri, IProgress progress) {
		return getResource(uri);
	}

	@Override
	public <R2 extends FlexoResource<?>> R2 getResource(InJarResourceImpl resourceArtefact, Class<R2> resourceClass) {
		// TODO
		return null;
	}

	/**
	 * Add all the jars from the class path to resource centers
	 * 
	 * @param rcService
	 */
	public static void addAllJarFromClassPath(FlexoResourceCenterService rcService) {
		for (JarFile file : ClassPathUtils.getClassPathJarFiles()) {
			addJarFile(file, rcService);
		}
	}

	/**
	 * Add the first jar from the class path found with this name Example : path of the jar in the class path :
	 * c:/a/b/c/org/openflexo/myjar.jar Name : org.openflexo.myjar Return the c:/a/b/c/org/openflexo/myjar.jar
	 * 
	 * @param rcService
	 */
	@SuppressWarnings("rawtypes")
	public static JarResourceCenter addNamedJarFromClassPath(FlexoResourceCenterService rcService, String name) {
		JarResourceCenter rc = null;
		for (JarFile file : ClassPathUtils.getClassPathJarFiles()) {
			if ((file.getName().endsWith(name + ".jar")) || (name.endsWith(".jar") && file.getName().endsWith(name))) {
				rc = addJarFile(file, rcService);
				break;
			}
		}
		return rc;
	}

	/**
	 * Add a resource center from a jar file
	 * 
	 * @param jarFile
	 * @param rcService
	 */
	@SuppressWarnings("rawtypes")
	public static JarResourceCenter addJarFile(JarFile jarFile, FlexoResourceCenterService rcService) {
		logger.info("Try to create a resource center from a jar file : " + jarFile.getName());
		JarResourceCenter rc = new JarResourceCenter(jarFile, rcService);
		rc.setDefaultBaseURI(jarFile.getName());
		rcService.addToResourceCenters(rc);
		rcService.storeDirectoryResourceCenterLocations();
		return rc;
	}

	@Override
	public Collection<? extends FlexoResource<?>> getAllResources(IProgress progress) {
		// TODO Not yet implemented
		return new ArrayList<FlexoResource<?>>();
	}

	@Override
	public void publishResource(FlexoResource<?> resource, FlexoVersion newVersion, IProgress progress) throws Exception {
		// TODO Not yet implemented
	}

	@Override
	public void update() throws IOException {
		// TODO Not yet implemented
	}

	@ModelEntity
	@XMLElement
	public static interface JarResourceCenterEntry extends ResourceCenterEntry<JarResourceCenter> {
		@PropertyIdentifier(type = File.class)
		public static final String JAR_KEY = "jar";

		@Getter(JAR_KEY)
		@XMLAttribute
		public File getFile();

		@Setter(JAR_KEY)
		public void setFile(File jar);

		@Implementation
		public static abstract class JarResourceCenterEntryImpl implements JarResourceCenterEntry {
			@Override
			public JarResourceCenter<?> makeResourceCenter(FlexoResourceCenterService rcService) {
				JarFile jarFile;
				try {
					jarFile = new JarFile(getFile());
					return new JarResourceCenter(jarFile, rcService);
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			public boolean isSystemEntry() {
				// For now, jarRC are only added from ClassPath
				return true;
			}

			@Override
			public void setIsSystemEntry(boolean isSystemEntry) {
				// Does Nothing
			}
		}

	}

	@Override
	public String getDefaultBaseURI() {
		return rcBaseUri;
	}

	@Override
	public void setDefaultBaseURI(String defaultBaseURI) {
		rcBaseUri = defaultBaseURI;

	}

	@Override
	public boolean isIgnorable(InJarResourceImpl artefact) {
		// TODO Not yet implemented
		return false;
	}

	// TODO Remove this
	@Override
	public ViewPointRepository getViewPointRepository() {
		if (getServiceManager() != null) {
			FMLTechnologyAdapter vmTA = getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(FMLTechnologyAdapter.class);
			return getRepository(ViewPointRepository.class, vmTA);
		}
		return null;
	}

	private JarResourceCenterEntry entry;

	@Override
	public ResourceCenterEntry<?> getResourceCenterEntry() {
		if (entry == null) {
			try {
				ModelFactory factory = new ModelFactory(JarResourceCenterEntry.class);
				entry = factory.newInstance(JarResourceCenterEntry.class);
				entry.setFile(new File(getJarResourceImpl().getRelativePath()));
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			}
		}
		return entry;
	}

	/**
	 * Stops the Resource Center (When needed)
	 */
	@Override
	public void stop() {
		// Nothing to do for now
	}

	@Override
	public String getDefaultResourceURI(FlexoResource<?> resource) {
		return resource.getName();
	}

	@Override
	public String retrieveName(InJarResourceImpl serializationArtefact) {
		if (serializationArtefact != null) {
			String returned = serializationArtefact.getURL().getFile();
			if (returned.endsWith("/")) {
				returned = returned.substring(0, returned.length() - 1);
			}
			System.out.println("On retourne " + returned);
			return returned;
		}
		return getName();
	}

	/**
	 * Return serialization artefact containing supplied serialization artefact (parent directory)
	 * 
	 * @param serializationArtefact
	 * @return
	 */
	@Override
	public InJarResourceImpl getContainer(InJarResourceImpl serializationArtefact) {
		return (InJarResourceImpl) serializationArtefact.getContainer();
	}

	/**
	 * Return list of serialization actefacts contained in supplied serialization actifact<br>
	 * Return empty list if supplied serialization artefact has no contents
	 * 
	 * @param serializationArtefact
	 * @return
	 */
	@Override
	public List<InJarResourceImpl> getContents(InJarResourceImpl serializationArtefact) {
		return (List) serializationArtefact.getContents();
	}

	@Override
	public boolean isDirectory(InJarResourceImpl serializationArtefact) {
		return serializationArtefact.isContainer();
	}

	@Override
	public boolean exists(InJarResourceImpl serializationArtefact) {
		return true;
	}

	@Override
	public boolean canRead(InJarResourceImpl serializationArtefact) {
		return true;
	}

	@Override
	public InJarResourceImpl createDirectory(String name, InJarResourceImpl parentDirectory) {
		// Not applicable
		return null;
	}

	@Override
	public InJarResourceImpl createEntry(String name, InJarResourceImpl parentDirectory) {
		// Not applicable
		return null;
	}

	@Override
	public InJarFlexoIODelegate makeFlexoIODelegate(InJarResourceImpl serializationArtefact,
			FlexoResourceFactory<?, ?, ?> resourceFactory) {
		return InJarFlexoIODelegateImpl.makeInJarFlexoIODelegate(serializationArtefact, resourceFactory);
	}

	@Override
	public FlexoIODelegate<InJarResourceImpl> makeDirectoryBasedFlexoIODelegate(InJarResourceImpl serializationArtefact,
			String directoryExtension, String fileExtension, FlexoResourceFactory<?, ?, ?> resourceFactory) {
		// Not applicable
		return null;
	}

	@Override
	public <R extends FlexoResource<?>> RepositoryFolder<R, InJarResourceImpl> getRepositoryFolder(
			FlexoIODelegate<InJarResourceImpl> ioDelegate, ResourceRepository<R, InJarResourceImpl> resourceRepository) {
		// TODO
		return null;
	}

}
