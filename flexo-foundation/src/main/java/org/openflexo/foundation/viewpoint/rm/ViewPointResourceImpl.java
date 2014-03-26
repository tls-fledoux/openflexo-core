package org.openflexo.foundation.viewpoint.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.collections.IteratorUtils;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.InconsistentDataException;
import org.openflexo.foundation.InvalidModelDefinitionException;
import org.openflexo.foundation.InvalidXMLException;
import org.openflexo.foundation.resource.FlexoFileNotFoundException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.utils.XMLUtils;
import org.openflexo.foundation.viewpoint.FlexoConcept;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointImpl;
import org.openflexo.foundation.viewpoint.ViewPointModelFactory;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModelTechnologyAdapter;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.StringUtils;

public abstract class ViewPointResourceImpl extends PamelaResourceImpl<ViewPoint, ViewPointModelFactory> implements ViewPointResource {

	static final Logger logger = Logger.getLogger(FlexoXMLFileResourceImpl.class.getPackage().getName());

	public static ViewPointResource makeViewPointResource(String name, String uri, File viewPointDirectory,
			FlexoServiceManager serviceManager) {
		try {
			ModelFactory factory = new ModelFactory(ViewPointResource.class);
			ViewPointResourceImpl returned = (ViewPointResourceImpl) factory.newInstance(ViewPointResource.class);
			String baseName = viewPointDirectory.getName().substring(0, viewPointDirectory.getName().length() - VIEWPOINT_SUFFIX.length());
			File xmlFile = new File(viewPointDirectory, baseName + ".xml");
			returned.setName(name);
			returned.setURI(uri);
			returned.setVersion(new FlexoVersion("0.1"));
			returned.setModelVersion(new FlexoVersion("1.0"));
			returned.setFile(xmlFile);
			returned.setDirectory(viewPointDirectory);

			// If ViewPointLibrary not initialized yet, we will do it later in ViewPointLibrary.initialize() method
			if (serviceManager.getViewPointLibrary() != null) {
				returned.setViewPointLibrary(serviceManager.getViewPointLibrary());
				serviceManager.getViewPointLibrary().registerViewPoint(returned);
			}

			returned.setServiceManager(serviceManager);
			returned.setFactory(new ViewPointModelFactory(returned));

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ViewPointResource retrieveViewPointResource(File viewPointDirectory, FlexoServiceManager serviceManager) {
		try {
			ModelFactory factory = new ModelFactory(ViewPointResource.class);
			ViewPointResourceImpl returned = (ViewPointResourceImpl) factory.newInstance(ViewPointResource.class);
			String baseName = viewPointDirectory.getName().substring(0, viewPointDirectory.getName().length() - VIEWPOINT_SUFFIX.length());
			File xmlFile = new File(viewPointDirectory, baseName + ".xml");
			ViewPointInfo vpi = findViewPointInfo(viewPointDirectory);
			if (vpi == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setFile(xmlFile);
			returned.setDirectory(viewPointDirectory);
			returned.setURI(vpi.uri);
			returned.setName(vpi.name);
			if (StringUtils.isNotEmpty(vpi.version)) {
				returned.setVersion(new FlexoVersion(vpi.version));
			}
			/*boolean hasBeenConverted = false;
			if (StringUtils.isEmpty(vpi.modelVersion)) {
				// This is the old model, convert to new model
				convertViewPoint(viewPointDirectory, xmlFile);
				hasBeenConverted = true;
			}*/

			
			/*
			 * Will be activitated when the convertion will be fully compliant
			 */
			/*if(isAn16Viewpoint(returned)){
				logger.fine("Converting viewpoint " + xmlFile.getAbsolutePath());
				convertViewPoint16ToViewpoint17(returned);
			}*/
			
			
			if (StringUtils.isEmpty(vpi.modelVersion)) {
				returned.setModelVersion(new FlexoVersion("0.1"));
			} else {
				returned.setModelVersion(new FlexoVersion(vpi.modelVersion));
			}

			returned.setFactory(new ViewPointModelFactory(returned));

			// If ViewPointLibrary not initialized yet, we will do it later in ViewPointLibrary.initialize() method
			if (serviceManager.getViewPointLibrary() != null) {
				returned.setViewPointLibrary(serviceManager.getViewPointLibrary());
				serviceManager.getViewPointLibrary().registerViewPoint(returned);
			}

			returned.setServiceManager(serviceManager);

			logger.fine("ViewPointResource " + xmlFile.getAbsolutePath() + " version " + returned.getModelVersion());

			// Now look for virtual models
			returned.exploreVirtualModels();

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void exploreVirtualModels() {
		if (getDirectory().exists() && getDirectory().isDirectory()) {
			for (File f : getDirectory().listFiles()) {
				if (f.isDirectory()) {
					File virtualModelFile = new File(f, f.getName() + ".xml");
					if (virtualModelFile.exists()) {
						// TODO: we must find something more efficient
						try {
							Document d = XMLUtils.readXMLFile(virtualModelFile);
							if (d.getRootElement().getName().equals("VirtualModel")) {
								VirtualModelResource virtualModelResource = VirtualModelResourceImpl.retrieveVirtualModelResource(f,
										virtualModelFile, this, getServiceManager());
								addToContents(virtualModelResource);
							} /*else if (d.getRootElement().getName().equals("DiagramSpecification")) {
								DiagramSpecificationResource diagramSpecificationResource = DiagramSpecificationResourceImpl
										.retrieveDiagramSpecificationResource(f, virtualModelFile, this, getViewPointLibrary());
								addToContents(diagramSpecificationResource);
								}*/
						} catch (JDOMException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	@Override
	public VirtualModelTechnologyAdapter getTechnologyAdapter() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(VirtualModelTechnologyAdapter.class);
		}
		return null;
	}

	@Override
	public ViewPoint getViewPoint() {

		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 */
	@Override
	public ViewPoint loadResourceData(IProgress progress) throws FlexoFileNotFoundException, IOFlexoException, InvalidXMLException,
			InconsistentDataException, InvalidModelDefinitionException {

		ViewPointImpl returned = (ViewPointImpl) super.loadResourceData(progress);

		String baseName = getDirectory().getName().substring(0, getDirectory().getName().length() - 10);

		returned.init(baseName,/* getDirectory(), getFile(),*/getViewPointLibrary());

		for (VirtualModel vm : returned.getVirtualModels()) {
			for (FlexoConcept ep : vm.getFlexoConcepts()) {
				ep.finalizeFlexoConceptDeserialization();
			}
			vm.clearIsModified();
		}

		returned.clearIsModified();

		return returned;
	}

	@Override
	public Class<ViewPoint> getResourceDataClass() {
		return ViewPoint.class;
	}

	/**
	 * Return flag indicating if this resource is loadable<br>
	 * By default, such resource is loadable if based on 1.6 architecture (model version greater or equals to 1.0)
	 * 
	 * @return
	 */
	@Override
	public boolean isLoadable() {
		return !isDeprecatedVersion();
	}

	@Override
	public boolean isDeprecatedVersion() {
		return getModelVersion().isLesserThan(new FlexoVersion("1.0"));
	}
	
	public static boolean isAn16Viewpoint(ViewPointResource viewpointResource){
		try {
			for (File f : viewpointResource.getDirectory().listFiles()) {
				if (f.isDirectory()) {
					for (File file : f.listFiles()) {
						if (file.getName().endsWith(".palette")) {
							if(contains16Elements(XMLUtils.readXMLFile(file)))
								return true;
						}
						if (file.getName().endsWith(".diagram")) {
							if(contains16Elements(XMLUtils.readXMLFile(file)))
								return true;
						}
						if (file.getName().endsWith(".xml")) {
							if(contains16Elements(XMLUtils.readXMLFile(file)))
								return true;
						}
					}
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private static boolean contains16Elements(Document document){
		if(document.getDescendants(new ElementFilter("EditionPattern")
							.or(new ElementFilter("ContainedEditionPatternInstancePatternRole")
							.or(new ElementFilter("ContainedEMFObjectIndividualPatternRole")
							.or(new ElementFilter("ContainedShapePatternRole")
							.or(new ElementFilter("ContainedConnectorPatternRole")
							.or(new ElementFilter("ContainedOWLIndividualPatternRole")
							.or(new ElementFilter("ContainedExcelRowPatternRole")
							.or(new ElementFilter("ContainedExcelCellPatternRole")
							.or(new ElementFilter("ContainedExcelSheetPatternRole")
							.or(new ElementFilter("EditionPatternInstanceParameter")
							.or(new ElementFilter("MatchEditionPatternInstance")
							.or(new ElementFilter("CreateEditionPatternInstanceParameter")
							.or(new ElementFilter("Palette")
							.or(new ElementFilter("PaletteElement")
							.or(new ElementFilter("Shema")
							.or(new ElementFilter("ContainedShape")
							.or(new ElementFilter("ContainedConnector")
							.or(new ElementFilter("FromShape")
							.or(new ElementFilter("ToShape")
							.or(new ElementFilter("Border")
							.or(new ElementFilter("AddEditionPatternInstance")
							.or(new ElementFilter("AddEditionPatternInstanceParameter")
							.or(new ElementFilter("AddressedSelectEditionPatternInstance")
							.or(new ElementFilter("AddressedSelectFlexoConceptInstance")
							)))))))))))))))))))))))).hasNext()){
			return true;
		}
		return false;
	}

	private static class ViewPointInfo {
		public String uri;
		public String version;
		public String name;
		public String modelVersion;
	}

	private static ViewPointInfo findViewPointInfo(File viewpointDirectory) {
		Document document;
		try {
			logger.fine("Try to find infos for " + viewpointDirectory);

			String baseName = viewpointDirectory.getName().substring(0, viewpointDirectory.getName().length() - 10);
			File xmlFile = new File(viewpointDirectory, baseName + ".xml");

			if (xmlFile.exists()) {

				document = readXMLFile(xmlFile);
				Element root = getElement(document, "ViewPoint");
				if (root != null) {
					ViewPointInfo returned = new ViewPointInfo();
					Iterator<Attribute> it = root.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = it.next();
						if (at.getName().equals("uri")) {
							logger.fine("Returned " + at.getValue());
							returned.uri = at.getValue();
						} else if (at.getName().equals("name")) {
							logger.fine("Returned " + at.getValue());
							returned.name = at.getValue();
						} else if (at.getName().equals("version")) {
							logger.fine("Returned " + at.getValue());
							returned.version = at.getValue();
						} else if (at.getName().equals("modelVersion")) {
							logger.fine("Returned " + at.getValue());
							returned.modelVersion = at.getValue();
						}
					}
					if (StringUtils.isEmpty(returned.name)) {
						if (StringUtils.isNotEmpty(returned.uri)) {
							if (returned.uri.indexOf("/") > -1) {
								returned.name = returned.uri.substring(returned.uri.lastIndexOf("/") + 1);
							} else if (returned.uri.indexOf("\\") > -1) {
								returned.name = returned.uri.substring(returned.uri.lastIndexOf("\\") + 1);
							} else {
								returned.name = returned.uri;
							}
						}
					}
					return returned;
				}
			} else {
				logger.warning("While analysing viewpoint candidate: " + viewpointDirectory.getAbsolutePath() + " cannot find file "
						+ xmlFile.getAbsolutePath());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return null;
	}

	public static void convertViewPoint(ViewPointResource viewPointResource) {

		File viewPointDirectory = viewPointResource.getDirectory();
		File xmlFile = viewPointResource.getFile();

		logger.info("Converting " + viewPointDirectory.getAbsolutePath());

		File diagramSpecificationDir = new File(viewPointDirectory, "DiagramSpecification");
		diagramSpecificationDir.mkdir();

		logger.fine("Creating directory " + diagramSpecificationDir.getAbsolutePath());

		try {
			Document viewPointDocument = XMLUtils.readXMLFile(xmlFile);
			Document diagramSpecificationDocument = XMLUtils.readXMLFile(xmlFile);

			for (File f : viewPointDirectory.listFiles()) {
				if (!f.equals(xmlFile) && !f.equals(diagramSpecificationDir) && !f.getName().endsWith("~")) {
					if (f.getName().endsWith(".shema")) {
						try {
							File renamedExampleDiagramFile = new File(f.getParentFile(), f.getName().substring(0, f.getName().length() - 6)
									+ ".diagram");
							FileUtils.rename(f, renamedExampleDiagramFile);
							f = renamedExampleDiagramFile;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					File destFile = new File(diagramSpecificationDir, f.getName());
					FileUtils.rename(f, destFile);
					logger.fine("Moving file " + f.getAbsolutePath() + " to " + destFile.getAbsolutePath());
				}
				if (f.getName().endsWith("~")) {
					f.delete();
				}
			}

			Element diagramSpecification = XMLUtils.getElement(diagramSpecificationDocument, "ViewPoint");
			diagramSpecification.setName("DiagramSpecification");
			FileOutputStream fos = new FileOutputStream(new File(diagramSpecificationDir, "DiagramSpecification.xml"));
			Format prettyFormat = Format.getPrettyFormat();
			prettyFormat.setLineSeparator(LineSeparator.SYSTEM);
			XMLOutputter outputter = new XMLOutputter(prettyFormat);
			try {
				outputter.output(diagramSpecificationDocument, fos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			fos.flush();
			fos.close();
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		((ViewPointResourceImpl) viewPointResource).exploreVirtualModels();

	}

	@Override
	public List<VirtualModelResource> getVirtualModelResources() {
		ViewPoint vp = getViewPoint();
		return getContents(VirtualModelResource.class);
	}

	@Override
	public boolean delete() {
		if (super.delete()) {
			getServiceManager().getResourceManager().addToFilesToDelete(getDirectory());
			return true;
		}
		return false;
	}

	public static void convertViewPoint16ToViewpoint17(ViewPointResource viewPointResource) {

		File viewPointDirectory = viewPointResource.getDirectory();

		List<File> paletteFiles = new ArrayList<File>();
		List<File> exampleDiagramFiles = new ArrayList<File>();
		List<File> virtualModels = new ArrayList<File>();

		logger.info("Converting " + viewPointDirectory.getAbsolutePath());

		/*
		 *  Find the resources
		 */
		try {
			for (File f : viewPointResource.getDirectory().listFiles()) {
				if (f.isDirectory()) {
					
					// Initialize files list
					paletteFiles.clear();
					exampleDiagramFiles.clear();
					virtualModels.clear();
					
					// Find palette files if any
					for (File palette : f.listFiles()) {
						if (palette.getName().endsWith(".palette")) {
							paletteFiles.add(palette);
							// Store the old file
							FileUtils.copyFileToFile(palette, new File(f, palette.getName()+".old16"));
						}
					}
					// Find diagram files if any
					for (File exampleDiagram : f.listFiles()) {
						if (exampleDiagram.getName().endsWith(".diagram")) {
							exampleDiagramFiles.add(exampleDiagram);
							// Store the old file
							FileUtils.copyFileToFile(exampleDiagram, new File(f, exampleDiagram.getName()+".old16"));
						}
					}
					// Find virtualmodels files if any
					File virtualModelFile = new File(f, f.getName() + ".xml");
					if (virtualModelFile.exists()) {
						virtualModels.add(virtualModelFile);
						// Store the old file
						FileUtils.copyFileToFile(virtualModelFile, new File(f, virtualModelFile.getName()+".old16"));
					}
					// Execute the convertion
					convertVirtualModels16ToVirtualModels17(viewPointResource, virtualModelFile, paletteFiles, exampleDiagramFiles);
				}
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void convertVirtualModels16ToVirtualModels17(ViewPointResource viewPointResource, File virtualModelFile,
			List<File> paletteFiles, List<File> exampleDiagramFiles) {
		try {
			if(virtualModelFile.exists()){
				Document d = XMLUtils.readXMLFile(virtualModelFile);
				if (d.getRootElement().getName().equals("VirtualModel")) {
					convertVirtualModel16ToVirtualModel17(d);
					XMLUtils.saveXMLFile(d, virtualModelFile);
				}
				if (d.getRootElement().getName().equals("DiagramSpecification")) {
					convertDiagramSpecification16ToVirtualModel17(virtualModelFile, d, paletteFiles, exampleDiagramFiles,viewPointResource);
					
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void convertDiagramSpecification16ToVirtualModel17(File file, Document diagram, List<File> oldPaletteFiles,
			List<File> oldExampleDiagramFiles,ViewPointResource viewPointResource) {
		
		// Create the diagram specification and a virtual model with a diagram typed model slot referencing this diagram specification
		
		final String ADDRESSED_DIAGRAM_MODEL_SLOT = "AddressedDiagramModelSlot";
		final String MODELSLOT_VIRTUAL_MODEL_MODEL_SLOT = "ModelSlot_VirtualModelModelSlot";
		final String MODELSLOT_TYPED_DIAGRAM_MODEL_SLOT = "ModelSlot_TypedDiagramModelSlot";
		
		try {
			String diagramName = file.getName().replace(".xml", "");
			// Create a folder that contains the diagram specification
			File diagramSpecificationFolder = new File(file.getParentFile()+"/"+ diagramName + ".diagramspecification");
			diagramSpecificationFolder.mkdir();
			
			// Create the diagram specificaion xml file
			File diagramSpecificationFile = new File(diagramSpecificationFolder, file.getName());
			Document diagramSpecification = new Document();
			Element rootElement = new Element("DiagramSpecification");
			Attribute name = new Attribute("name", diagramName);
			Attribute diagramSpecificationURI = new Attribute("uri", "http://diagramspecification_"+diagramName);
			diagramSpecification.addContent(rootElement);
			rootElement.getAttributes().add(name);
			rootElement.getAttributes().add(diagramSpecificationURI);
			XMLUtils.saveXMLFile(diagramSpecification, diagramSpecificationFile);
			
			// Copy the palette files inside diagram specification repository
			ArrayList<File> newPaletteFiles = new ArrayList<File>();
			for(File paletteFile : oldPaletteFiles){
				File newFile = new File(diagramSpecificationFolder +"/"+ paletteFile.getName());
				FileUtils.rename(paletteFile, newFile);
				newPaletteFiles.add(newFile);
				Document palette = XMLUtils.readXMLFile(newFile);
				convertNames16ToNames17(palette);
				XMLUtils.saveXMLFile(palette, newFile);
			}
		
			// Copy the example diagram files inside diagram specification repository
			ArrayList<File> newExampleDiagramFiles = new ArrayList<File>();
			for(File exampleDiagramFile : oldExampleDiagramFiles){
				File newFile = new File(diagramSpecificationFolder +"/"+ exampleDiagramFile.getName());
				FileUtils.rename(exampleDiagramFile, newFile);
				newExampleDiagramFiles.add(newFile);
				Document exampleDiagram = XMLUtils.readXMLFile(newFile);
				exampleDiagram.getRootElement().setAttribute("uri", diagramSpecificationURI.getValue()+"/"+exampleDiagram.getRootElement().getAttributeValue("name"));
				convertNames16ToNames17(exampleDiagram);
				XMLUtils.saveXMLFile(exampleDiagram, newFile);
			}
			
			// Change class name for previews
			Iterator<? extends Content> previewClassesElementIterator = diagram.getDescendants(new ElementFilter("ShapeGraphicalRepresentation").or(new ElementFilter("ConnectorGraphicalRepresentation")));
			while(previewClassesElementIterator.hasNext()){
				Element previewClassElement = (Element)previewClassesElementIterator.next();
				previewClassElement.removeAttribute("className");
			}
			
			removeNamedElements(diagram, "StartShapeGraphicalRepresentation");
			removeNamedElements(diagram, "EndShapeGraphicalRepresentation");
			removeNamedElements(diagram, "ArtifactFromShapeGraphicalRepresentation");
			removeNamedElements(diagram, "ArtifactToShapeGraphicalRepresentation");
			
			// Retrieve diagram drop schemes
			Iterator<Element> dropSchemeElements = diagram.getDescendants(new ElementFilter("DropScheme"));
			List<Element> dropSchemes = IteratorUtils.toList(dropSchemeElements);
			
			// Retrieve Diagram Model slots references
			Iterator<? extends Content> thisModelSlotsIterator = diagram.getDescendants(new ElementFilter("DiagramModelSlot").or(new ElementFilter(ADDRESSED_DIAGRAM_MODEL_SLOT)));
			List<Element> thisModelSlots = IteratorUtils.toList(thisModelSlotsIterator);	
			
			// Retrieve the DiagramModelSlot (this), and transform it to a virtual model slot with a virtual model uri
			int thisID=0;
			String newThisUri = "http://"+diagramName;
			Element typedDiagramModelSlot = null;
			boolean foundThis = false;
			for(Element thisMs : thisModelSlots){
				// Retriev the ID and URI of this DiagramModelSlot
				if(thisMs.getAttribute("name")!=null && thisMs.getAttributeValue("name").equals("this") && !foundThis){
					// Store its ID and its URI
					thisID = thisMs.getAttribute("id").getIntValue();
					if(thisMs.getAttributeValue("virtualModelURI")!=null){
						newThisUri = thisMs.getAttributeValue("virtualModelURI");
						thisMs.removeAttribute("virtualModelURI");
						thisMs.removeAttribute("name");
						thisMs.getAttribute("id").setName("idref");
						thisMs.setAttribute("idref", Integer.toString(thisID));
					}
					// Replace by a Typed model slot
					typedDiagramModelSlot = new Element(MODELSLOT_TYPED_DIAGRAM_MODEL_SLOT);
					typedDiagramModelSlot.setAttribute("metaModelURI",diagramSpecificationURI.getValue());
					typedDiagramModelSlot.setAttribute("name", "typedDiagramModelSlot");
					typedDiagramModelSlot.setAttribute("id", Integer.toString(computeNewID(diagram)));
					foundThis=true;
				}
			}
			// Replace the Diagram Model Slot by a Virtual Model Model slot
			for(Element thisMs : thisModelSlots){
				if(hasSameID(thisMs, thisID) && thisMs.getName().equals("DiagramModelSlot")){
					thisMs.setName("VirtualModelModelSlot");
					thisMs.getAttributes().add(new Attribute("virtualModelURI", newThisUri));
					thisMs.getAttributes().add(new Attribute("name", "this"));
					thisMs.getAttributes().add(new Attribute("id", Integer.toString(thisID)));
					thisMs.removeAttribute("idref");
				}
			}
			// Update ids for all model slots
			Iterator<? extends Content> diagramModelSlotsIterator = diagram.getDescendants(new ElementFilter("DiagramModelSlot").or(new ElementFilter(ADDRESSED_DIAGRAM_MODEL_SLOT)));
			List<Element> thisDiagramModelSlots = IteratorUtils.toList(diagramModelSlotsIterator);
			for(Element diagramMs :  thisDiagramModelSlots){
				if(diagramMs.getAttribute("id")!=null && typedDiagramModelSlot!=null){
					diagramMs.setAttribute("id", typedDiagramModelSlot.getAttributeValue("id"));
				}
				if(diagramMs.getAttribute("idref")!=null){
					// Change to TypedDiagramModelSlot
					if(diagramMs.getParentElement().getName().equals("AddShape")||
							diagramMs.getParentElement().getName().equals("AddConnector")||
							diagramMs.getParentElement().getName().equals("ContainedShapePatternRole")||
							diagramMs.getParentElement().getName().equals("ContainedConnectorPatternRole")){
						diagramMs.setAttribute("idref", typedDiagramModelSlot.getAttributeValue("id"));
						diagramMs.setName("TypedDiagramModelSlot");
					}
					else{
						diagramMs.setName(MODELSLOT_VIRTUAL_MODEL_MODEL_SLOT);
					}
				}
			}
	
			// Change all the "diagram" binding with "this"
			for(Content content : diagram.getDescendants()){
				if(content instanceof Element){
					Element element = (Element) content;
					for(Attribute attribute : element.getAttributes()){
						if(attribute.getValue().startsWith("diagram")){
							attribute.setValue(attribute.getValue().replace("diagram", "this"));
						}
					}
				}
			}
			
			// Update the diagram palette element bindings
			ArrayList<Element> paletteElementBindings = new ArrayList<Element>();
			for (File paletteFile : newPaletteFiles){
				Document palette= XMLUtils.readXMLFile(paletteFile);
				String paletteUri = viewPointResource.getURI() + "/" + palette.getRootElement().getAttribute("name").getValue();
				Iterator<? extends Content> paletteElements = palette.getDescendants(new ElementFilter("DiagramPaletteElement"));
				while(paletteElements.hasNext()){
					Element paletteElement = (Element)paletteElements.next();
					Element binding = createPaletteElementBinding(paletteElement, paletteUri, dropSchemes);
					if(binding!=null)
						paletteElementBindings.add(binding);
				}
			}
			// Add the Palette Element Bindings to the TypedDiagramModelSlot
			if(!paletteElementBindings.isEmpty()){
				typedDiagramModelSlot.addContent(paletteElementBindings);	
			}
			if(typedDiagramModelSlot!=null){
				diagram.getRootElement().addContent(typedDiagramModelSlot);
			}
			
			// Remove elements
			removeNamedElements(diagram, "PrimaryRepresentationConnectorPatternRole");
			removeNamedElements(diagram, "PrimaryRepresentationShapePatternRole");
			
			// Update names
			convertNames16ToNames17(diagram);
			convertOldNameToNewNames("DiagramSpecification", "VirtualModel", diagram);
			
			// Save the file
			XMLUtils.saveXMLFile(diagram, file);
		
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void convertVirtualModel16ToVirtualModel17(Document document) {
		convertNames16ToNames17(document);
	}

	private static void convertNames16ToNames17(Document document) {
		// Convert properties
		convertProperties16ToProperties17(document);
		// Edition Patterns
		convertOldNameToNewNames("EditionPattern", "FlexoConcept", document);
		// Pattern Roles
		convertOldNameToNewNames("ContainedEditionPatternInstancePatternRole", "FlexoConceptInstanceRole", document);
		convertOldNameToNewNames("EditionPatternInstancePatternRole", "FlexoConceptInstanceRole", document);
		
		convertOldNameToNewNames("ContainedShapePatternRole", "ShapeRole", document);
		convertOldNameToNewNames("ShapePatternRole", "ShapeRole", document);
		
		convertOldNameToNewNames("ContainedEMFObjectIndividualPatternRole", "EMFObjectIndividualRole", document);
		convertOldNameToNewNames("EMFObjectIndividualPatternRole", "EMFObjectIndividualRole", document);
		
		convertOldNameToNewNames("ContainedConnectorPatternRole", "ConnectorRole", document);
		convertOldNameToNewNames("ConnectorPatternRole", "ConnectorRole", document);
		
		convertOldNameToNewNames("ContainedOWLIndividualPatternRole", "OWLIndividualRole", document);
		convertOldNameToNewNames("OWLIndividualPatternRole", "OWLIndividualRole", document);
		
		convertOldNameToNewNames("ContainedExcelCellPatternRole", "ExcelCellRole", document);
		convertOldNameToNewNames("ExcelCellPatternRole", "ExcelCellRole", document);
		
		convertOldNameToNewNames("ContainedExcelSheetPatternRole", "ExcelSheetRole", document);
		convertOldNameToNewNames("ExcelSheetPatternRole", "ExcelSheetRole", document);
		
		convertOldNameToNewNames("ContainedExcelRowPatternRole", "ExcelRowRole", document);
		convertOldNameToNewNames("ExcelRowPatternRole", "ExcelRowRole", document);
		
		// Actions
		convertOldNameToNewNames("EditionPatternInstanceParameter", "FlexoConceptInstanceParameter", document);
		convertOldNameToNewNames("MatchEditionPatternInstance", "MatchFlexoConceptInstance", document);
		convertOldNameToNewNames("CreateEditionPatternInstanceParameter", "CreateFlexoConceptInstanceParameter", document);
		
		//convertOldNameToNewNames("SelectEditionPatternInstance", "SelectFlexoConceptInstance", document);
		// Retrieve Fetch Actions
		IteratorIterable<? extends Content> fetchElementsIterator = document.getDescendants(new ElementFilter("SelectEditionPatternInstance").or(new ElementFilter("AddressedSelectEditionPatternInstance")));
		List<Element> fetchElements = IteratorUtils.toList(fetchElementsIterator);
		for(Element fetchElement : fetchElements){
			if(fetchElement.getParentElement().getName().equals("FetchRequestIterationAction")){
				fetchElement.setName("FetchRequest_SelectFlexoConceptInstance");
			}
		}
		
		convertOldNameToNewNames("AddEditionPatternInstance", "AddFlexoConceptInstance", document);
		convertOldNameToNewNames("AddEditionPatternInstanceParameter", "AddFlexoConceptInstanceParameter", document);
		convertOldNameToNewNames("AddressedSelectEditionPatternInstance", "SelectFlexoConceptInstance", document);
		convertOldNameToNewNames("AddressedSelectFlexoConceptInstance", "SelectFlexoConceptInstance", document);
		
		
		// Model Slots
		convertOldNameToNewNames("EMFModelSlot", "ModelSlot_EMFModelSlot", document);
		convertOldNameToNewNames("XMLModelSlot", "ModelSlot_XMLModelSlot", document);
		convertOldNameToNewNames("XSDModelSlot", "ModelSlot_XSDModelSlot", document);
		convertOldNameToNewNames("BasicExcelModelSlot", "ModelSlot_BasicExcelModelSlot", document);
		convertOldNameToNewNames("SemanticsExcelModelSlot", "ModelSlot_SemanticsExcelModelSlot", document);
		convertOldNameToNewNames("BasicPowerpointModelSlot", "ModelSlot_BasicPowerpointModelSlot", document);
		convertOldNameToNewNames("SemanticsPowerpointModelSlot", "ModelSlot_SemanticsPowerpointModelSlot", document);
		convertOldNameToNewNames("OWLModelSlot", "ModelSlot_OWLModelSlot", document);
		convertOldNameToNewNames("VirtualModelModelSlot", "ModelSlot_VirtualModelModelSlot", document);
		// Palettes/ExampleDiagrams
		convertOldNameToNewNames("Palette", "DiagramPalette", document);
		convertOldNameToNewNames("PaletteElement", "DiagramPaletteElement", document);
		convertOldNameToNewNames("Shema", "Diagram", document);
		convertOldNameToNewNames("ContainedShape", "Shape", document);
		convertOldNameToNewNames("ContainedConnector", "Connector", document);
		convertOldNameToNewNames("FromShape", "StartShape", document);
		convertOldNameToNewNames("ToShape", "EndShape", document);
		convertOldNameToNewNames("Border", "ShapeBorder", document);
		
		
		// Connection to ModelSlots
		convertOldNameToNewNames("AddressedEMFModelSlot", "EMFModelSlot", document);
		convertOldNameToNewNames("AddressedXMLModelSlot", "XMLModelSlot", document);
		convertOldNameToNewNames("AddressedXSDModelSlot", "XSDModelSlot", document);
		convertOldNameToNewNames("AddressedBasicExcelModelSlot", "BasicExcelModelSlot", document);
		convertOldNameToNewNames("AddressedSemanticsExcelModelSlot", "SemanticsExcelModelSlot", document);
		convertOldNameToNewNames("AddressedBasicPowerpointModelSlot", "BasicPowerpointModelSlot", document);
		convertOldNameToNewNames("AddressedSemanticsPowerpointModelSlot", "SemanticsPowerpointModelSlot", document);
		convertOldNameToNewNames("AddressedOWLModelSlot", "OWLModelSlot", document);
		convertOldNameToNewNames("AddressedVirtualModelModelSlot", "VirtualModelModelSlot", document);
		convertOldNameToNewNames("AddressedDiagramModelSlot", "TypedDiagramModelSlot", document);
		
	}

	private static void convertProperties16ToProperties17(Document document) {
		// All elements
		addProperty("userID", "FLX", document, null);
		// Pattern roles
		changePropertyName("editionPatternTypeURI", "flexoConceptTypeURI", document, "ContainedEditionPatternInstancePatternRole");
		changePropertyName("editionPatternTypeURI", "flexoConceptTypeURI", document, "AddressedSelectEditionPatternInstance");
		changePropertyName("editionPatternTypeURI", "flexoConceptTypeURI", document, "SelectEditionPatternInstance");
		changePropertyName("editionPatternTypeURI", "flexoConceptTypeURI", document, "EditionPatternInstanceParameter");
		changePropertyName("parentEditionPattern", "parentFlexoConcept", document, "EditionPattern");
		
		
		
		changePropertyName("editionPattern", "flexoConcept", document, "PaletteElement");
		removeProperty("patternRole", document, "ContainedEMFObjectIndividualPatternRole");
		removeProperty("patternRole", document, "ContainedEditionPatternInstancePatternRole");
	}

	private static int computeNewID(Document document){
		int id=1;
		for(Content content : document.getDescendants()){
			if(content instanceof Element){
				Element element = (Element)content;
				if(id<retrieveID(element))
					id=retrieveID(element)+1;
			}
		}
		return id+1;
	}
	
	private static Element createPaletteElementBinding(Element paletteElement, String paletteUri, List<Element> dropSchemeElements ){
		Attribute ep=null,ds=null;
		if (paletteElement.getAttribute("flexoConcept") != null) {
			ep = paletteElement.getAttribute("flexoConcept");
		}
		if (paletteElement.getAttribute("dropSchemeName") != null) {
			ds = paletteElement.getAttribute("dropSchemeName");
		}
		if(ep!=null && ds!=null){
			Element paletteElementBinding = new Element("FMLDiagramPaletteElementBinding");
			Attribute paletteElementId = new Attribute("paletteElementID", paletteUri+"#"+ep.getValue());
			paletteElementBinding.getAttributes().add(paletteElementId);
			//Find cooresponding dropscheme
			for(Element dropScheme :dropSchemeElements){
				if(dropScheme.getAttributeValue("name").equals(ds.getValue())){
					Element dropSchemeRef = new Element("DropScheme");
					dropSchemeRef.setAttribute("idref", dropScheme.getAttributeValue("id"));
					paletteElementBinding.addContent(dropSchemeRef);
				}
			}
			return paletteElementBinding;
		}
		return null;
	}
	
	private static boolean hasSameID(Element element, int id){
		try {
			if((element.getAttribute("id")!=null 
				&& element.getAttribute("id").getIntValue()==id)
				||(element.getAttribute("idref")!=null 
				&& element.getAttribute("idref").getIntValue()==id)){
				return true;
			}
		} catch (DataConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private static int retrieveID(Element element){
		try {
			if(element.getAttribute("id")!=null )
				return element.getAttribute("id").getIntValue();
			if(element.getAttribute("idref")!=null )
				return element.getAttribute("idref").getIntValue();
		} catch (DataConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static void convertOldNameToNewNames(String oldName, String newName, Document document) {
		for (Content content : document.getDescendants()) {
			if (content instanceof Element) {
				Element element = (Element) content;
				if (element.getName().equals(oldName)) {
					element.setName(newName);
				}
			}
		}
	}

	private static void addProperty(String property, String value, Document document, String elementName) {
		for (Content content : document.getDescendants()) {
			if (content instanceof Element) {
				Element element = (Element) content;
				if (elementName == null || elementName.equals(element.getName())) {
					element.setAttribute(property, value);
				}
			}
		}
	}

	private static void removeProperty(String property, Document document, String elementName) {
		for (Content content : document.getDescendants()) {
			if (content instanceof Element) {
				Element element = (Element) content;
				if (elementName == null || elementName.equals(element.getName())) {
					element.removeAttribute(property);
				}
			}
		}
	}
	
	private static void removeNamedElements(Document document, String elementName) {
		ArrayList<Element> parentElements = new ArrayList<Element>();
		for(Content content :document.getDescendants()){
			if(content instanceof Element){
				Element element = (Element)content;
				if(!element.getChildren(elementName).isEmpty())
					parentElements.add(element);
			}
		}
		for(Element parent : parentElements){
			parent.removeChildren(elementName);
		}
		parentElements = null;
	}
	
	

	private static void changePropertyName(String oldPropertyName, String newPropertyName, Document document, String elementName) {
		for (Content content : document.getDescendants()) {
			if (content instanceof Element) {
				Element element = (Element) content;
				if (elementName == null || elementName.equals(element.getName())) {
					if (element.getAttribute(oldPropertyName) != null) {
						element.getAttribute(oldPropertyName).setName(newPropertyName);
					}
				}
			}
		}
	}

}
