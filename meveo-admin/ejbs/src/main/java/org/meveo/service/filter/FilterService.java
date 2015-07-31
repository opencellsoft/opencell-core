package org.meveo.service.filter;

import javax.ejb.Stateless;

import org.meveo.model.filter.Filter;
import org.meveo.service.base.PersistenceService;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilterService extends PersistenceService<Filter> {

	public Filter parse(String xmlInput) throws XStreamException {
		Filter result = new Filter();

		XStream xstream = new XStream();
		result = (Filter) xstream.fromXML(xmlInput);

		return result;
	}

}
