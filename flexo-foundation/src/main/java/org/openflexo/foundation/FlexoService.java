/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import org.openflexo.toolbox.StringUtils;

/**
 * Implements a service (an object with operational state) in the Openflexo architecture. A {@link FlexoService} is responsible for a
 * particular task, and works in conjunction with other services within a {@link FlexoServiceManager} which receives and broadcast all
 * {@link ServiceNotification} to all registered services.
 * 
 * @author sylvain
 * 
 */
public interface FlexoService {

	/**
	 * Called by the {@link FlexoServiceManager} to register the service manager
	 * 
	 * @param serviceManager
	 */
	public void register(FlexoServiceManager serviceManager);

	/**
	 * Return the {@link FlexoServiceManager} where this {@link FlexoService} is registered. If not registered return null. This is the
	 * implemenation responsability to register itself the service manager
	 * 
	 * @return
	 */
	public FlexoServiceManager getServiceManager();

	/**
	 * Called after registration, after all services have been notified that this service has been registered
	 */
	public void initialize();

	/**
	 * Called to stop the service
	 */
	public void stop();

	/**
	 * Return name of the service
	 * 
	 * @return
	 */
	public String getServiceName();

	/**
	 * Return status of the service
	 * 
	 * @return
	 */
	public Status getStatus();

	/**
	 * Return indicating general status of this FlexoService<br>
	 * This is the display value of 'service <service> status' as given in FML command-line interpreter
	 * 
	 * @return
	 */
	public String getDisplayableStatus();

	/**
	 * Return collection of all available {@link ServiceOperation} available for this {@link FlexoService}
	 * 
	 * @return
	 */
	public Collection<ServiceOperation<?>> getAvailableServiceOperations();

	/**
	 * Make a new {@link ServiceOperation} available
	 * 
	 * @param serviceOperation
	 */
	public void addToAvailableServiceOperations(ServiceOperation<?> serviceOperation);

	/**
	 * Receives a new {@link ServiceNotification} broadcasted from the {@link FlexoServiceManager}
	 * 
	 * @param caller
	 * @param notification
	 */
	public void receiveNotification(FlexoService caller, ServiceNotification notification);

	/**
	 * A notification broadcasted by the {@link FlexoServiceManager}
	 * 
	 * @author sylvain
	 * 
	 */
	public static interface ServiceNotification {
	}

	/**
	 * Represent the status of a {@link FlexoService}
	 * 
	 * @author sylvain
	 *
	 */
	public static enum Status {
		Registered, Started, Stopped
	}

	/**
	 * Represent an operation which is available for a FlexoService<br>
	 * Such action is callable and executable with options
	 * 
	 * @author sylvain
	 *
	 */
	public static interface ServiceOperation<S extends FlexoService> {

		public abstract String getOperationName();

		public abstract String usage(S service);

		public abstract String getSyntax(S service);

		public abstract String description();

		public String getArgument();

		// public List<ServiceOperationOption> getOptions();

		public void execute(S service, PrintStream out, PrintStream err, Object argument, Map<String, ?> options);

		public String getStringRepresentation(Object argumentValue);
	}

	public static class ServiceOperationOption {
		private String optionName;
		private String optionType;

		public String getOptionName() {
			return optionName;
		}

		public void setOptionName(String optionName) {
			this.optionName = optionName;
		}

		public String getOptionType() {
			return optionType;
		}

		public void setOptionType(String optionType) {
			this.optionType = optionType;
		}

	}

	public static HelpOnService HELP_ON_SERVICE = new HelpOnService();
	public static DisplayServiceStatus DISPLAY_SERVICE_STATUS = new DisplayServiceStatus();
	public static StartService START_SERVICE = new StartService();
	public static StopService STOP_SERVICE = new StopService();

	public static class HelpOnService implements ServiceOperation<FlexoService> {

		private HelpOnService() {
		}

		@Override
		public String getOperationName() {
			return "usage";
		}

		@Override
		public String usage(FlexoService service) {
			return "service " + service.getServiceName() + " usage";
		}

		@Override
		public String getSyntax(FlexoService service) {
			return "service " + service.getServiceName() + " " + getOperationName();
		}

		@Override
		public String description() {
			return "display this help";
		}

		@Override
		public String getArgument() {
			return null;
		}

		/*@Override
		public String getArgumentOption() {
			return null;
		}
		
		@Override
		public List<ServiceOperationOption> getOptions() {
			return null;
		}*/

		@Override
		public void execute(FlexoService service, PrintStream out, PrintStream err, Object argument, Map<String, ?> options) {
			out.println("Usage: ");
			for (ServiceOperation serviceOperation : service.getAvailableServiceOperations()) {
				out.println(" " + serviceOperation.usage(service)
						+ StringUtils.buildWhiteSpaceIndentation(30 - serviceOperation.usage(service).length()) + " : "
						+ serviceOperation.description());
			}
		}

		@Override
		public String getStringRepresentation(Object argumentValue) {
			return getOperationName();
		}

	}

	public static class DisplayServiceStatus implements ServiceOperation<FlexoService> {
		private DisplayServiceStatus() {
		}

		@Override
		public String getOperationName() {
			return "status";
		}

		@Override
		public String usage(FlexoService service) {
			return "service " + service.getServiceName() + " status";
		}

		@Override
		public String getSyntax(FlexoService service) {
			return "service " + service.getServiceName() + " " + getOperationName();
		}

		@Override
		public String description() {
			return "display status of service";
		}

		@Override
		public String getArgument() {
			return null;
		}

		/*@Override
		public String getArgumentOption() {
			return null;
		}
		
		@Override
		public List<ServiceOperationOption> getOptions() {
			return null;
		}*/

		@Override
		public void execute(FlexoService service, PrintStream out, PrintStream err, Object argument, Map<String, ?> options) {
			out.println(service.getDisplayableStatus());
		}

		@Override
		public String getStringRepresentation(Object argumentValue) {
			return getOperationName();
		}

	}

	public static class StartService implements ServiceOperation<FlexoService> {
		private StartService() {
		}

		@Override
		public String getOperationName() {
			return "start";
		}

		@Override
		public String usage(FlexoService service) {
			return "service " + service.getServiceName() + " start";
		}

		@Override
		public String getSyntax(FlexoService service) {
			return "service " + service.getServiceName() + " " + getOperationName();
		}

		@Override
		public String description() {
			return "start service";
		}

		@Override
		public String getArgument() {
			return null;
		}

		/*@Override
		public String getArgumentOption() {
			return null;
		}
		
		@Override
		public List<ServiceOperationOption> getOptions() {
			return null;
		}*/

		@Override
		public void execute(FlexoService service, PrintStream out, PrintStream err, Object argument, Map<String, ?> options) {
			if (service.getStatus() != Status.Started) {
				service.initialize();
				out.println("Service has been started");
			}
			else {
				out.println("Service already started");
			}
		}

		@Override
		public String getStringRepresentation(Object argumentValue) {
			return getOperationName();
		}

	}

	public static class StopService implements ServiceOperation<FlexoService> {
		private StopService() {
		}

		@Override
		public String getOperationName() {
			return "stop";
		}

		@Override
		public String usage(FlexoService service) {
			return "service " + service.getServiceName() + " stop";
		}

		@Override
		public String getSyntax(FlexoService service) {
			return "service " + service.getServiceName() + " " + getOperationName();
		}

		@Override
		public String description() {
			return "stop service";
		}

		@Override
		public String getArgument() {
			return null;
		}

		/*@Override
		public String getArgumentOption() {
			return null;
		}
		
		@Override
		public List<ServiceOperationOption> getOptions() {
			return null;
		}*/

		@Override
		public void execute(FlexoService service, PrintStream out, PrintStream err, Object argument, Map<String, ?> options) {
			if (service.getStatus() == Status.Started) {
				service.stop();
				out.println("Service has been stopped");
			}
			else {
				out.println("Service not started");
			}
		}

		@Override
		public String getStringRepresentation(Object argumentValue) {
			return getOperationName();
		}

	}
}
