package org.meveo.admin.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Edward P. Legaspi
 **/
public class XmlUtil {

	public static boolean validate(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return false;
		}

		builder.setErrorHandler(new SimpleErrorHandler());

		try {
			builder.parse(new InputSource(new StringReader(xml)));
		} catch (SAXException | IOException e) {
			return false;
		}

		return true;
	}

}
