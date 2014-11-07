package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class LoggerInject {

	@Inject
	private Logger log;
	
	public void log() {
		log.debug("test");
	}
	
}
