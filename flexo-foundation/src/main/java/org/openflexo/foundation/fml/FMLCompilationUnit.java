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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.fml.inspector.InspectorEntry;
import org.openflexo.foundation.fml.rm.CompilationUnitResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.UseModelSlotDeclaration;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.pamela.annotations.Adder;
import org.openflexo.pamela.annotations.CloningStrategy;
import org.openflexo.pamela.annotations.CloningStrategy.StrategyType;
import org.openflexo.pamela.annotations.Embedded;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Getter.Cardinality;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PastingPoint;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Remover;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.pamela.undo.CompoundEdit;
import org.openflexo.rm.BasicResourceImpl.LocatorNotFoundException;
import org.openflexo.rm.FileResourceImpl;
import org.openflexo.rm.Resource;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(FMLCompilationUnit.FMLCompilationUnitImpl.class)
public interface FMLCompilationUnit extends FMLObject, FMLPrettyPrintable, ResourceData<FMLCompilationUnit> {

	public static final String RESOURCE = "resource";
	@PropertyIdentifier(type = FlexoVersion.class)
	public static final String VERSION_KEY = "version";
	// @PropertyIdentifier(type = FlexoVersion.class)
	// public static final String MODEL_VERSION_KEY = "modelVersion";
	@PropertyIdentifier(type = JavaImportDeclaration.class, cardinality = Cardinality.LIST)
	public static final String JAVA_IMPORTS_KEY = "javaImports";
	@PropertyIdentifier(type = UseModelSlotDeclaration.class, cardinality = Cardinality.LIST)
	public static final String USE_DECLARATIONS_KEY = "useDeclarations";
	@PropertyIdentifier(type = VirtualModel.class)
	public static final String VIRTUAL_MODEL_KEY = "virtualModel";

	/**
	 * Return list of {@link JavaImportDeclaration} explicitely declared in this {@link FMLCompilationUnit}
	 * 
	 * @return
	 */
	@Getter(value = JAVA_IMPORTS_KEY, cardinality = Cardinality.LIST, inverse = JavaImportDeclaration.COMPILATION_UNIT_KEY)
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
	@Getter(value = VIRTUAL_MODEL_KEY, inverse = VirtualModel.COMPILATION_UNIT_KEY)
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public VirtualModel getVirtualModel();

	@Setter(VIRTUAL_MODEL_KEY)
	public void setVirtualModel(VirtualModel virtualModel);

	/**
	 * Return list of {@link UseModelSlotDeclaration} accessible from this {@link VirtualModel}<br>
	 * It includes the list of uses declarations accessible from parent and container
	 * 
	 * @return
	 */
	public List<UseModelSlotDeclaration> getAccessibleUseDeclarations();

	/**
	 * Return list of {@link UseModelSlotDeclaration} explicitely declared in this {@link VirtualModel}
	 * 
	 * @return
	 */
	@Getter(value = USE_DECLARATIONS_KEY, cardinality = Cardinality.LIST, inverse = UseModelSlotDeclaration.COMPILATION_UNIT_KEY)
	@XMLElement
	@Embedded
	@CloningStrategy(StrategyType.CLONE)
	public List<UseModelSlotDeclaration> getUseDeclarations();

	// @Setter(USE_DECLARATIONS_KEY)
	// public void setUseDeclarations(List<UseModelSlotDeclaration> useDecls);

	@Adder(USE_DECLARATIONS_KEY)
	@PastingPoint
	public void addToUseDeclarations(UseModelSlotDeclaration useDecl);

	@Remover(USE_DECLARATIONS_KEY)
	public void removeFromUseDeclarations(UseModelSlotDeclaration useDecl);

	/**
	 * Return boolean indicating if this VirtualModel uses supplied modelSlotClass
	 * 
	 * @param modelSlotClass
	 * @return
	 */
	@Deprecated
	public <MS extends ModelSlot<?>> boolean uses(Class<MS> modelSlotClass);

	/**
	 * Declare use of supplied modelSlotClass
	 * 
	 * @param modelSlotClass
	 * @return
	 */
	@Deprecated
	public <MS extends ModelSlot<?>> UseModelSlotDeclaration declareUse(Class<MS> modelSlotClass);

	/**
	 * Return resource for this virtual model
	 * 
	 * @return
	 */
	@Override
	@Getter(value = RESOURCE, ignoreType = true)
	// @CloningStrategy(value = StrategyType.FACTORY, factory = "cloneResource()")
	@CloningStrategy(StrategyType.IGNORE)
	public FlexoResource<FMLCompilationUnit> getResource();

	/**
	 * Sets resource for this virtual model
	 * 
	 * @param aName
	 */
	@Override
	@Setter(value = RESOURCE)
	public void setResource(FlexoResource<FMLCompilationUnit> aCompilationUnitResource);

	/**
	 * Convenient method used to retrieved {@link CompilationUnitResource}
	 * 
	 * @return
	 */
	public CompilationUnitResource getVirtualModelResource();

	/**
	 * Version of encoded {@link VirtualModel}
	 * 
	 * @return
	 */
	@Getter(value = VERSION_KEY, isStringConvertable = true)
	@XMLAttribute
	public FlexoVersion getVersion();

	@Setter(VERSION_KEY)
	public void setVersion(FlexoVersion version);

	/**
	 * Version of FML meta-model
	 * 
	 * @return
	 */
	/*@Getter(value = MODEL_VERSION_KEY, isStringConvertable = true)
	@XMLAttribute
	public FlexoVersion getModelVersion();*/

	/*@Setter(MODEL_VERSION_KEY)
	public void setModelVersion(FlexoVersion modelVersion);*/

	public LocalizedDelegate getLocalizedDictionary();

	/**
	 * Return FlexoConcept matching supplied id represented as a string, which could be either the name of FlexoConcept, or its URI
	 * 
	 * @param flexoConceptNameOrURI
	 * @return
	 */
	public FlexoConcept getFlexoConcept(String flexoConceptNameOrURI);

	/**
	 * Return the list of {@link TechnologyAdapter} used in the context of this {@link VirtualModel}
	 * 
	 * @return
	 */
	public List<TechnologyAdapter> getRequiredTechnologyAdapters();

	/**
	 * Load eventually unloaded contained VirtualModels<br>
	 * After this call return, we can safely assert that all contained {@link VirtualModel} are loaded.
	 */
	void loadContainedVirtualModelsWhenUnloaded();

	public VirtualModel getVirtualModelNamed(String virtualModelNameOrURI);

	public FMLObject getObject(String objectURI);

	public abstract class FMLCompilationUnitImpl extends FMLObjectImpl implements FMLCompilationUnit {

		private static final Logger logger = Logger.getLogger(FMLCompilationUnitImpl.class.getPackage().getName());

		private CompilationUnitResource resource;

		@Override
		public FMLModelFactory getFMLModelFactory() {

			if (getResource() != null) {
				return getResource().getFactory();
			}
			else {
				return getDeserializationFactory();
			}
		}

		@Override
		public BindingFactory getBindingFactory() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getBindingFactory();
			}
			return null;
		}

		@Override
		public FMLCompilationUnit getResourceData() {
			return this;
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return "<not_implemented:" + getStringRepresentation() + ">";
		}

		@Override
		public String toString() {
			return "FMLCompilationUnit";
		}

		@Override
		public String getURI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BindingModel getBindingModel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompilationUnitResource getResource() {
			return resource;
		}

		@Override
		public void setResource(FlexoResource<FMLCompilationUnit> resource) {
			this.resource = (CompilationUnitResource) resource;
		}

		/**
		 * Convenient method used to retrieved {@link CompilationUnitResource}
		 * 
		 * @return
		 */
		@Override
		public CompilationUnitResource getVirtualModelResource() {
			return getResource();
		}

		@Override
		public void finalizeDeserialization() {
			if (getVirtualModel() != null) {
				getVirtualModel().finalizeDeserialization();
			}
			super.finalizeDeserialization();
		}

		@Override
		public FlexoVersion getVersion() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getVersion();
			}
			if (getResource() != null) {
				return getResource().getVersion();
			}
			return null;
		}

		@Override
		public void setVersion(FlexoVersion aVersion) {
			if (requireChange(getVersion(), aVersion)) {
				if (getVirtualModel() != null) {
					getVirtualModel().setVersion(aVersion);
				}
				if (getResource() != null) {
					getResource().setVersion(aVersion);
				}
			}
		}

		/*@Override
		public FlexoVersion getModelVersion() {
			if (getResource() != null) {
				return getResource().getModelVersion();
			}
			return null;
		}
		
		@Override
		public void setModelVersion(FlexoVersion aVersion) {
			if (getResource() != null) {
				getResource().setModelVersion(aVersion);
			}
		}*/

		@Override
		public FlexoConcept getFlexoConcept(String flexoConceptNameOrURI) {
			if (getVirtualModel() != null) {
				return getVirtualModel().getFlexoConcept(flexoConceptNameOrURI);
			}
			return null;
		}

		@Override
		public FMLTechnologyAdapter getTechnologyAdapter() {
			if (getResource() != null) {
				return getResource().getTechnologyAdapter();
			}
			return null;
		}

		@Override
		public FMLObject getObject(String objectURI) {
			return getVirtualModelLibrary().getFMLObject(objectURI, true);
		}

		@Override
		public boolean delete(Object... context) {

			// Unregister the resource from the virtual model library
			if (getResource() != null && getVirtualModelLibrary() != null) {
				getVirtualModelLibrary().unregisterCompilationUnit(getResource());
			}

			/*if (bindingModel != null) {
				bindingModel.delete();
			}*/

			boolean returned = performSuperDelete(context);

			// Delete observers
			deleteObservers();

			return returned;
		}

		@Override
		public VirtualModelLibrary getVirtualModelLibrary() {
			if (getResource() != null) {
				return getResource().getVirtualModelLibrary();
			}
			return null;
		}

		/**
		 * Return the list of {@link TechnologyAdapter} used in the context of this {@link FMLCompilationUnit}
		 * 
		 * @return
		 */
		@Override
		public List<TechnologyAdapter> getRequiredTechnologyAdapters() {

			// TODO: implement this with #use
			List<TechnologyAdapter> returned = new ArrayList<>();
			returned.add(getTechnologyAdapter());
			for (ModelSlot<?> ms : getVirtualModel().getModelSlots()) {
				if (!returned.contains(ms.getModelSlotTechnologyAdapter())) {
					returned.add(ms.getModelSlotTechnologyAdapter());
				}
			}
			loadContainedVirtualModelsWhenUnloaded();
			for (VirtualModel vm : getVirtualModel().getVirtualModels()) {
				for (TechnologyAdapter<?> ta : vm.getCompilationUnit().getRequiredTechnologyAdapters()) {
					if (!returned.contains(ta)) {
						returned.add(ta);
					}
				}
			}
			return returned;
		}

		private boolean isLoading = false;

		/**
		 * Load eventually unloaded VirtualModels<br>
		 * After this call return, we can safely assert that all {@link VirtualModel} are loaded.
		 */
		@Override
		public void loadContainedVirtualModelsWhenUnloaded() {
			if (isLoading) {
				return;
			}
			if (!isLoading) {
				isLoading = true;
				if (getResource() != null) {
					for (org.openflexo.foundation.resource.FlexoResource<?> r : getResource().getContents()) {
						if (r instanceof CompilationUnitResource) {
							((CompilationUnitResource) r).getCompilationUnit();
						}
					}
				}
			}

			isLoading = false;
		}

		/**
		 * Return {@link VirtualModel} with supplied name or URI
		 * 
		 * @return
		 */
		@Override
		public VirtualModel getVirtualModelNamed(String virtualModelNameOrURI) {

			if (getResource() != null) {
				VirtualModel returned = getContainedVirtualModelNamed(getResource(), virtualModelNameOrURI);
				if (returned != null) {
					return returned;
				}
			}

			if (getVirtualModel().getContainerVirtualModel() != null) {
				return getVirtualModel().getContainerVirtualModel().getVirtualModelNamed(virtualModelNameOrURI);
			}

			if (getVirtualModelLibrary() != null) {
				try {
					return getVirtualModelLibrary().getVirtualModel(virtualModelNameOrURI);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ResourceLoadingCancelledException e) {
					e.printStackTrace();
				} catch (FlexoException e) {
					e.printStackTrace();
				}
			}

			// Not found
			return null;
		}

		private VirtualModel getContainedVirtualModelNamed(CompilationUnitResource resource, String virtualModelNameOrURI) {

			if (resource != null) {
				for (CompilationUnitResource vmRes : resource.getContents(CompilationUnitResource.class)) {
					if (vmRes.getName().equals(virtualModelNameOrURI)) {
						return vmRes.getCompilationUnit().getVirtualModel();
					}
					if (vmRes.getURI().equals(virtualModelNameOrURI)) {
						return vmRes.getCompilationUnit().getVirtualModel();
					}
					VirtualModel returned = getContainedVirtualModelNamed(vmRes, virtualModelNameOrURI);
					if (returned != null) {
						return returned;
					}
				}
			}

			// Not found
			return null;
		}

		private LocalizedDelegateImpl localized;

		private Resource getLocalizedDirectoryResource() {
			Resource virtualModelDirectory = getResource().getIODelegate().getSerializationArtefactAsResource().getContainer();
			List<? extends Resource> localizedDirs = virtualModelDirectory.getContents(Pattern.compile(".*/Localized"), false);
			if (localizedDirs.size() > 0) {
				return localizedDirs.get(0);
			}
			if (virtualModelDirectory instanceof FileResourceImpl) {
				try {
					return new FileResourceImpl(virtualModelDirectory.getLocator(),
							new File(((FileResourceImpl) virtualModelDirectory).getFile(), "Localized"));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LocatorNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			logger.warning("Cannot find localized directory for " + this);
			return null;
		}

		private LocalizedDelegateImpl instantiateOrLoadLocales() {
			if (getResource() != null) {
				Resource localizedDirectoryResource = getLocalizedDirectoryResource();
				if (localizedDirectoryResource == null) {
					return null;
				}
				boolean editSupport = getResource().getIODelegate().getSerializationArtefactAsResource() instanceof FileResourceImpl;
				logger.info("Reading locales from " + localizedDirectoryResource);
				LocalizedDelegateImpl returned = new LocalizedDelegateImpl(localizedDirectoryResource,
						getVirtualModel().getContainerVirtualModel() != null ? getVirtualModel().getContainerVirtualModel().getLocales()
								: getServiceManager().getLocalizationService().getFlexoLocalizer(),
						editSupport, editSupport);
				returned.setLocalizationRetriever(new Runnable() {
					@Override
					public void run() {
						searchNewLocalizedEntries();
					}
				});
				return returned;

			}
			return null;
		}

		@Override
		public LocalizedDelegate getLocalizedDictionary() {
			if (localized == null) {
				localized = instantiateOrLoadLocales();
				if (localized == null) {
					// Cannot load locales
					if (getServiceManager() != null) {
						return getServiceManager().getLocalizationService().getFlexoLocalizer();
					}
					return null;
				}
				// Converting old dictionaries
				/*if (getDeprecatedLocalizedDictionary() != null) {
					for (FMLLocalizedEntry fmlLocalizedEntry : getDeprecatedLocalizedDictionary().getLocalizedEntries()) {
						localized.registerNewEntry(fmlLocalizedEntry.getKey(), Language.get(fmlLocalizedEntry.getLanguage()),
								fmlLocalizedEntry.getValue());
					}
				}*/
			}
			return localized;
		}

		public void createLocalizedDictionaryWhenNonExistant() {
			if (localized == null) {
				logger.info("createLocalizedDictionary");
				localized = instantiateOrLoadLocales();
			}
		}

		/*@Override
		public FMLLocalizedDictionary getDeprecatedLocalizedDictionary() {
			if (isSerializing()) {
				return null;
			}
			return (FMLLocalizedDictionary) performSuperGetter(LOCALIZED_DICTIONARY_KEY);
		}*/

		private void searchNewEntriesForConcept(FlexoConcept concept) {
			// checkAndRegisterLocalized(concept.getName());
			for (FlexoBehaviour es : concept.getFlexoBehaviours()) {
				checkAndRegisterLocalized(es.getLabel(), normalizedKey -> es.setLabel(normalizedKey));
				// checkAndRegisterLocalized(es.getDescription());
				for (FlexoBehaviourParameter p : es.getParameters()) {
					checkAndRegisterLocalized(p.getName());
				}
				for (InspectorEntry entry : concept.getInspector().getEntries()) {
					checkAndRegisterLocalized(entry.getLabel(), normalizedKey -> entry.setLabel(normalizedKey));
				}
			}
		}

		private void searchNewLocalizedEntries() {
			logger.info("Search new entries for " + this);

			CompoundEdit ce = null;
			FMLModelFactory factory = null;

			factory = getFMLModelFactory();
			if (factory != null) {
				if (!factory.getEditingContext().getUndoManager().isBeeingRecording()) {
					ce = factory.getEditingContext().getUndoManager().startRecording("localize_virtual_model");
				}
			}

			searchNewEntriesForConcept(getVirtualModel());

			for (FlexoConcept concept : getVirtualModel().getFlexoConcepts()) {
				searchNewEntriesForConcept(concept);
			}

			if (factory != null) {
				if (ce != null) {
					factory.getEditingContext().getUndoManager().stopRecording(ce);
				}
				else if (factory.getEditingContext().getUndoManager().isBeeingRecording()) {
					factory.getEditingContext().getUndoManager()
							.stopRecording(factory.getEditingContext().getUndoManager().getCurrentEdition());
				}
			}

			// getViewPoint().setChanged();
			// getViewPoint().notifyObservers();
		}

		private String checkAndRegisterLocalized(String key, Consumer<String> updateKey) {

			// System.out.println("checkAndRegisterLocalized for " + key);
			if (StringUtils.isEmpty(key)) {
				return null;
			}

			String normalizedKey = StringUtils.toLocalizedKey(key.trim());

			if (!key.equals(normalizedKey)) {
				updateKey.accept(normalizedKey);
			}

			getLocalizedDictionary().addEntry(normalizedKey);
			return normalizedKey;
		}

		private String checkAndRegisterLocalized(String key) {

			// System.out.println("checkAndRegisterLocalized for " + key);
			if (StringUtils.isEmpty(key)) {
				return null;
			}

			getLocalizedDictionary().addEntry(key);
			return key;
		}

		/**
		 * Return list of {@link UseModelSlotDeclaration} accessible from this {@link VirtualModel}<br>
		 * It includes the list of uses declarations accessible from parent and container
		 * 
		 * @return
		 */
		@Override
		public List<UseModelSlotDeclaration> getAccessibleUseDeclarations() {
			// TODO: make better implementation
			if (getVirtualModel() != null) {
				return getVirtualModel().getAccessibleUseDeclarations();
			}
			return null;
		}

		@Override
		public <MS extends ModelSlot<?>> boolean uses(Class<MS> modelSlotClass) {
			// TODO: make better implementation
			if (getVirtualModel() != null) {
				return getVirtualModel().uses(modelSlotClass);
			}
			return false;
		}

		@Override
		public <MS extends ModelSlot<?>> UseModelSlotDeclaration declareUse(Class<MS> modelSlotClass) {
			// TODO: make better implementation
			if (getVirtualModel() != null) {
				return getVirtualModel().declareUse(modelSlotClass);
			}
			return null;
		}
	}

}
