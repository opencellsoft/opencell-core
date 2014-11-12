package org.meveo.api.rest;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.LoggerProducer;
import org.meveo.api.LoggerInject;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class LoggerTest {

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap
				.create(WebArchive.class, "logger-test.war")
				.addClasses(LoggerProducer.class, LoggerInject.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		return result;
	}

	@Inject
	private LoggerInject loggerInject;

	@Test
	public void testInjection() {
		loggerInject.log();
	}

}
