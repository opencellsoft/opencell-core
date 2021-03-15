package org.meveo.admin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Edward P. Legaspi
 **/
public class SimpleErrorHandler implements ErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleErrorHandler.class);
	
	public void warning(SAXParseException e) throws SAXException {
		log.info(e.getMessage());
	}

	public void error(SAXParseException e) throws SAXException {
		log.info(e.getMessage());
	}

	public void fatalError(SAXParseException e) throws SAXException {
		log.info(e.getMessage());
	}
	
}
