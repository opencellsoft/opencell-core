package org.meveo.service.filter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.IEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.filter.AndCompositeFilterCondition;
import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterCondition;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrCompositeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.PrimitiveFilterCondition;
import org.meveo.model.filter.Projector;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.mapper.ClassAliasingMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

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

	/**
	 * Use in the UI when creating a filter hierarchy from xml.
	 * 
	 * @return
	 */
	private XStream getXStream() {
		XStream xStream = new XStream();
		// rename the selector field
		xStream.alias("andCompositeFilterCondition", AndCompositeFilterCondition.class);
		xStream.alias("filter", Filter.class);
		xStream.alias("filterCondition", FilterCondition.class);
		xStream.alias("filterSelector", FilterSelector.class);
		xStream.alias("nativeFilterCondition", NativeFilterCondition.class);
		xStream.alias("orCompositeFilterCondition", OrCompositeFilterCondition.class);
		xStream.alias("orderCondition", OrderCondition.class);
		xStream.alias("primitiveFilterCondition", PrimitiveFilterCondition.class);
		xStream.alias("projector", Projector.class);

		xStream.setMode(XStream.NO_REFERENCES);

		// rename String to field, arrayList must be specify in the fieldName
		// setter
		ClassAliasingMapper orderConditionFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		orderConditionFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(OrderCondition.class, "fieldNames", new CollectionConverter(
				orderConditionFieldMapper));

		// rename projector exportField
		ClassAliasingMapper projectorExportFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorExportFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(FilterSelector.class, "exportFields", new CollectionConverter(
				projectorExportFieldMapper));

		// rename projector displayField
		ClassAliasingMapper projectorDisplayFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorDisplayFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(FilterSelector.class, "displayFields", new CollectionConverter(
				projectorDisplayFieldMapper));

		// rename projector ignore field
		ClassAliasingMapper projectorIgnoreFieldMapper = new ClassAliasingMapper(xStream.getMapper());
		projectorIgnoreFieldMapper.addClassAlias("field", String.class);
		xStream.registerLocalConverter(FilterSelector.class, "ignoreIfNotFoundForeignKeys", new CollectionConverter(
				projectorIgnoreFieldMapper));

		return xStream;
	}

	public void applyOmittedFields(XStream xstream, Filter filter) {
		applyOmittedFields(xstream, filter, true);
	}

	public void applyOmittedFields(XStream xstream, Filter filter, boolean display) {
		List<String> displayOrExportFields = filter.getPrimarySelector().getDisplayFields();
		if (!display) {
			displayOrExportFields = filter.getPrimarySelector().getExportFields();
		}

		@SuppressWarnings("rawtypes")
		Class targetClass = ReflectionUtils.createObject(filter.getPrimarySelector().getTargetEntity()).getClass();
		List<Field> fields = new ArrayList<Field>();
		ReflectionUtils.getAllFields(fields, targetClass);

		// allFields - display = omit
		List<Field> displayFields = new ArrayList<>();
		for (Field field : fields) {
			for (String displayField : displayOrExportFields) {
				if (field.getName().equals(displayField)) {
					displayFields.add(field);
					break;
				}
			}
		}

		fields.removeAll(displayFields);

		// omit fields
		log.debug("Omitting fields={} from class={}", Arrays.asList(fields), targetClass.getName());
		for (Field field : fields) {
			xstream.omitField(field.getDeclaringClass(), field.getName());
		}
	}

	public boolean isMatch(NativeFilterCondition filter, Map<Object, Object> params) {
		try {
			return ((Boolean) ValueExpressionWrapper.evaluateExpression(filter.getEl(), params, Boolean.class))
					.booleanValue();
		} catch (BusinessException e) {
			return false;
		}
	}

	public String serializeEntities(XStream xstream, Filter filter, List<? extends IEntity> entities) {
		if (entities.isEmpty()) {
			log.info("No entities to serialize");
			return "";
		}

		Class<? extends Object> primaryTargetClass = ReflectionUtils.createObject(
				filter.getPrimarySelector().getTargetEntity()).getClass();
		xstream.alias(primaryTargetClass.getSimpleName().toLowerCase(), primaryTargetClass);

		// Add custom converters
		xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));
		xstream.registerConverter(new HibernateProxyConverter());
		xstream.setMode(XStream.NO_REFERENCES);

		return xstream.toXML(entities);
	}

	@SuppressWarnings("unchecked")
	public String filteredList(Filter filter, Provider provider) throws BusinessException {
		FilteredQueryBuilder fqb = new FilteredQueryBuilder(filter);

		try {
			Query query = fqb.getQuery(getEntityManager());
			log.debug("query={}", fqb.getSqlString());
			List<? extends IEntity> objects = (List<? extends IEntity>) query.getResultList();
			XStream xstream = new XStream() {
				@Override
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new HibernateMapper(next);
				}
			};

			applyOmittedFields(xstream, filter);

			// String result = xstream.toXML(countries);
			return serializeEntities(xstream, filter, objects);
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<? extends IEntity> filteredListAsObjects(Filter filter, Provider provider) throws BusinessException {
		FilteredQueryBuilder fqb = new FilteredQueryBuilder(filter);

		try {
			Query query = fqb.getQuery(getEntityManager());
			log.debug("query={}", fqb.getSqlString());
			List<? extends IEntity> objects = (List<? extends IEntity>) query.getResultList();
			XStream xstream = new XStream() {
				@Override
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new HibernateMapper(next);
				}
			};

			applyOmittedFields(xstream, filter);

			return objects;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	public String filteredList(String filterName, Integer firstRow, Integer numberOfRows, Provider provider)
			throws BusinessException {
		Filter filter = (Filter) findByCode(filterName, provider);
		return filteredList(filter, firstRow, numberOfRows);
	}

	@SuppressWarnings("unchecked")
	public String filteredList(Filter filter, Integer firstRow, Integer numberOfRows) throws BusinessException {
		FilteredQueryBuilder fqb = new FilteredQueryBuilder(filter);

		try {
			Query query = fqb.getQuery(getEntityManager());
			log.debug("query={}", fqb.getSqlString());
			fqb.applyPagination(query, firstRow, numberOfRows);
			List<? extends IEntity> objects = (List<? extends IEntity>) query.getResultList();
			XStream xstream = new XStream() {
				@Override
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new HibernateMapper(next);
				}
			};

			applyOmittedFields(xstream, filter);

			// String result = xstream.toXML(countries);
			return serializeEntities(xstream, filter, objects);
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Filter> findByPrimaryTargetClass(String className) {
		QueryBuilder qb = new QueryBuilder(Filter.class, "f", null, getCurrentProvider());
		qb.addCriterion("primarySelector.targetEntity", "=", className, true);

		try {
			return (List<Filter>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}
