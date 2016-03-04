package org.openflexo.foundation;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.openflexo.foundation.resource.DefaultResourceCenterService;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.GitResourceCenter;
import org.openflexo.rm.ClasspathResourceLocatorImpl;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;
import org.openflexo.toolbox.FileUtils;

/**
 * Provide a git environment for tests cases
 * @author Arkantea
 *
 */
public class OpenFlexoTestCaseWithGit extends OpenflexoProjectAtRunTimeTestCase {
	
	private static File testResourceCenterDirectory2;
	
	protected static FlexoServiceManager instanciateTestServiceManager(final boolean generateCompoundTestResourceCenter) {
		File previousResourceCenterDirectoryToRemove = null;
		if (testResourceCenterDirectory != null && testResourceCenterDirectory.exists()) {
			previousResourceCenterDirectoryToRemove = testResourceCenterDirectory;
		}
		serviceManager = new DefaultFlexoServiceManager() {

			@Override
			protected FlexoEditingContext createEditingContext() {
				// In unit tests, we do NOT want to be warned against unexpected edits
				return FlexoEditingContext.createInstance(false);
			}

			@Override
			protected FlexoEditor createApplicationEditor() {
				return new FlexoTestEditor(null, this);
			}

			@Override
			protected FlexoResourceCenterService createResourceCenterService() {
				try {
					File tempFile = File.createTempFile("Temp", "");
					testResourceCenterDirectory = new File(tempFile.getParentFile(), tempFile.getName() + "TestResourceCenter");
					tempFile.delete();
					testResourceCenterDirectory.mkdirs();

					File tempFile2 = File.createTempFile("Temp", "");
					testResourceCenterDirectory2 = new File(tempFile2.getParentFile(), tempFile2.getName() + "TestResourceCenter");
					tempFile2.delete();
					testResourceCenterDirectory2.mkdirs();
					
					System.out.println("Creating TestResourceCenter [compound: " + generateCompoundTestResourceCenter + "] "
							+ testResourceCenterDirectory);
					System.out.println("***************** WARNING WARNING WARNING ************************");

					if (generateCompoundTestResourceCenter) {

						ClasspathResourceLocatorImpl locator = new ClasspathResourceLocatorImpl();

						List<Resource> toto = locator.locateAllResources("TestResourceCenter");
						for (Resource tstRC : toto) {
							System.out.println(tstRC.toString());
							FileUtils.copyResourceToDir(tstRC, testResourceCenterDirectory);
						}
					}
					else {

						Resource tstRC = ResourceLocator.locateResource("TestResourceCenter");
						System.out.println("Ressource Container Uri "+tstRC.getURI());
						for (Resource resource : tstRC.getContents()) {
							System.out.println("Resource URI : "+resource.getURI());
						}
						System.out.println("Copied from " + tstRC);
						FileUtils.copyResourceToDir(tstRC, testResourceCenterDirectory);
					}


					FlexoResourceCenterService rcService = DefaultResourceCenterService.getNewInstance();
//					rcService.addToResourceCenters(
//							resourceCenter = new DirectoryResourceCenter(testResourceCenterDirectory, TEST_RESOURCE_CENTER_URI));
//					System.out.println("Copied TestResourceCenter to " + testResourceCenterDirectory);
					try {
						rcService.addToResourceCenters(new GitResourceCenter(testResourceCenterDirectory,testResourceCenterDirectory));
						rcService.addToResourceCenters(new GitResourceCenter(testResourceCenterDirectory2,testResourceCenterDirectory2));
					} catch (IllegalStateException | GitAPIException e) {
						e.printStackTrace();
					}
					// ici il y a des truc a voir
					
					return rcService;
				} catch (IOException e) {
					e.printStackTrace();
					fail();
					return null;
				}

			}

		};

		if (previousResourceCenterDirectoryToRemove != null) {
			if (testResourceCenterDirectoriesToRemove == null) {
				testResourceCenterDirectoriesToRemove = new ArrayList<File>();
			}
			testResourceCenterDirectoriesToRemove.add(previousResourceCenterDirectoryToRemove);
		}
		return serviceManager;
	}
	
	
}
