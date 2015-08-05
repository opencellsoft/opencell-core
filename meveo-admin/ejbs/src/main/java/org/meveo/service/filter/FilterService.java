package org.meveo.service.filter;

import javax.ejb.Stateless;

import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.Projector;
import org.meveo.service.base.BusinessService;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class FilterService extends BusinessService<Filter> {

	public Filter parse(String xmlInput) throws XStreamException {
		Filter result = new Filter();

		XStream xstream = getXStream();
		result = (Filter) xstream.fromXML(xmlInput);

		return result;
	}

	private XStream getXStream() {
		XStream xStream = new XStream();
		// rename the selector field
		xStream.alias("selector", FilterSelector.class);

		// rename String to field, arrayList must be specify in the fieldName
		// setter
		ClassAliasingMapper orderConditionFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		orderConditionFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(OrderCondition.class, "fieldNames", new CollectionConverter(
				orderConditionFieldMapper));

		// rename projector exportField
		ClassAliasingMapper projectorExportFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorExportFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(Projector.class, "exportFields", new CollectionConverter(
				projectorExportFieldMapper));

		// rename projector displayField
		ClassAliasingMapper projectorDisplayFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorDisplayFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(Projector.class, "displayFields", new CollectionConverter(
				projectorDisplayFieldMapper));

		// rename projector ignore field
		ClassAliasingMapper projectorIgnoreFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorIgnoreFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(Projector.class, "ignoreIfNotFoundForeignKeys", new CollectionConverter(
				projectorIgnoreFieldMapper));

		return xStream;
	}

}
