package org.openflexo.fib.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.logging.Logger;

import org.junit.runner.RunWith;
import org.openflexo.OpenflexoProjectAtRunTimeTestCaseWithGUI;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.validation.ValidationError;
import org.openflexo.fib.model.validation.ValidationReport;
import org.openflexo.fib.view.widget.DefaultFIBCustomComponent;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.rm.Resource;
import org.openflexo.test.OrderedRunner;

/**
 * Provides a JUnit 4 generic environment of Openflexo-core for a FIB testing purposes in graphics environment
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public abstract class OpenflexoFIBTestCaseWithProjectAtRunTime extends OpenflexoProjectAtRunTimeTestCaseWithGUI {

	static final Logger logger = Logger.getLogger(OpenflexoFIBTestCaseWithProjectAtRunTime.class.getPackage().getName());

	public void validateFIB(Resource fibResource) {
		try {
			System.out.println("Validating fib file " + fibResource);
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(fibResource);
			if (component == null) {
				fail("Component not found: " + fibResource.getURI());
			}
			ValidationReport validationReport = component.validate();
			for (ValidationError error : validationReport.getErrors()) {
				logger.severe("FIBComponent validation error: Object: " + error.getObject() + " message: " + error.getMessage());
			}
			assertEquals(0, validationReport.getErrorNb());
		} finally {
			FIBLibrary.instance().removeFIBComponentFromCache(fibResource);
		}
	}

	public <T> DefaultFIBCustomComponent<T> instanciateFIB(Resource fibResource, T context, final Class<T> contextType) {
		return new DefaultFIBCustomComponent<T>(fibResource, context, FlexoLocalization.getMainLocalizer()) {

			@Override
			public Class<T> getRepresentedType() {
				return contextType;
			}

			@Override
			public void delete() {
			}

		};

	}
}
