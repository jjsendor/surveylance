package pl.edu.agh.iosr.surveylance.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.hibernate.HibernateTransactionDecorator;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.services.Dispatcher;

import pl.edu.agh.iosr.surveylance.security.AccessController;
import pl.edu.agh.iosr.surveylance.service.MailService;
import pl.edu.agh.iosr.surveylance.service.impl.MailServiceImpl;

/**
 * Class responsible for web application module configuration.
 * 
 * @author kuba
 * @author kornel
 */
public class AppModule {

	/**
	 * Binds DAOs and services with implementations.
	 * 
	 * @param binder
	 *            {@link ServiceBinder} object
	 */
	public static void bind(ServiceBinder binder) {
		binder.bind(AccessController.class).withId("AccessController");

		// binding services
		binder.bind(MailService.class, MailServiceImpl.class);
	}

	/**
	 * Contributes application defaults.
	 * 
	 * @param configuration
	 *            {@link MappedConfiguration} object
	 */
	public static void contributeApplicationDefaults(
			MappedConfiguration<String, String> configuration) {
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
		configuration.add(SymbolConstants.SCRIPTS_AT_TOP, "true");
		configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "false");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en,pl");
	}

	/**
	 * Contributes master dispatcher for web application.
	 * 
	 * @param configuration
	 *            order configuration of dispatchers
	 * @param accessController
	 *            access controller dispatcher
	 */
	public void contributeMasterDispatcher(
			OrderedConfiguration<Dispatcher> configuration,
			@InjectService("AccessController") Dispatcher accessController) {
		configuration.add("AccessController", accessController,
				"before:PageRender");
	}

	/**
	 * Decorates all transactional method.
	 */
	@Match("*DAO")
	public static <T> T decorateTransactionally(
			HibernateTransactionDecorator decorator, Class<T> serviceInterface,
			T delegate, String serviceId) {
		return decorator.build(serviceInterface, delegate, serviceId);
	}

}
