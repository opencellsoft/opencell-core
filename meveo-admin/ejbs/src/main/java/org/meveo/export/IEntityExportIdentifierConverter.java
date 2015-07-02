package org.meveo.export;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.Inheritance;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.Query;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.model.IEntity;
import org.meveo.model.crm.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Xstream converter to serialise/deserialize an entity into/from a short version.
 * 
 * When serialising, exports only the attributes that uniquely identify an entity in DB (e.g. code instead of ID). Names of attributes are set with @ExportIdentifier annotation in
 * an entity class
 * 
 * When deserialising, a lookup is done to DB to retrieve a reference to an entity
 * 
 * @author Andrius Karpavicius
 * 
 */
public class IEntityExportIdentifierConverter implements Converter {

    private ExportImportConfig exportImportConfig;

    private EntityManager em;
    private boolean referenceFKById;
    private boolean ignoreNotFoundFK;
    private Provider forceToProvider;
    private Map<String, Long> providerMap = new HashMap<String, Long>();
    private IEntityClassConverter iEntityClassConverter;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 
     * @param exportImportConfig Export/import process configuration
     */
    public IEntityExportIdentifierConverter(ExportImportConfig exportImportConfig) {
        this.exportImportConfig = exportImportConfig;
    }

    /**
     * 
     * @param exportImportConfig Export/import process configuration
     * @param em Entity managed to retrieve an entity from DB during import process
     * @param referenceFKById Should ID be used as a preferred way of retrieving an entity from DB - used when no ID clash can occur(e.g.import to a clean DB, import to a clone of
     *        DB)
     * @param ignoreNotFoundFK Ignore if entity was not found. Otherwise a runtime exception will be thrown
     * @param forceToProvider
     * @param iEntityClassConverter A converter for full entity conversion
     */
    public IEntityExportIdentifierConverter(ExportImportConfig exportImportConfig, EntityManager em, boolean referenceFKById, boolean ignoreNotFoundFK, Provider forceToProvider,
            IEntityClassConverter iEntityClassConverter) {
        this.exportImportConfig = exportImportConfig;
        this.em = em;
        this.referenceFKById = referenceFKById;
        this.ignoreNotFoundFK = ignoreNotFoundFK;
        this.forceToProvider = forceToProvider;
        this.iEntityClassConverter = iEntityClassConverter;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean canConvert(final Class clazz) {

        if (HibernateProxy.class.isAssignableFrom(clazz)) {
            return false;
        }

        boolean isIEntity = IEntity.class.isAssignableFrom(clazz);

        boolean willConvert = isIEntity && !exportImportConfig.isExportFull(clazz);
        if (willConvert) {
            log.debug("Will be using " + this.getClass().getSimpleName() + " for " + clazz);
        }
        return willConvert;
    }

    /**
     * Exports only the attributes that uniquely identify an entity in DB (e.g. code instead of ID). Names of attributes are set with @ExportIdentifier annotation in an entity
     * class. ID value is always exported for debugging purpose. Depending on a template configuration, when explicitly told to export entity as ID value, only the ID value will be
     * exported.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void marshal(final Object object, final HierarchicalStreamWriter writer, final MarshallingContext context) {

        Class baseClass = object.getClass();

        String[] exportIdentifiers = exportImportConfig.getExportIdsForClass(baseClass);

        writer.addAttribute("id", ((IEntity) object).getId().toString());
        // Append class name when serialising an abstract or inheritance class' implementation
        if (Modifier.isAbstract(baseClass.getModifiers()) || baseClass.isAnnotationPresent(Inheritance.class) || baseClass.getSuperclass().isAnnotationPresent(Inheritance.class)) {
            writer.addAttribute("class", baseClass.getCanonicalName());
        }

        for (String attributeName : exportIdentifiers) {
            try {
                Object attributeValue = getAttributeValue(object, attributeName);
                if (attributeValue == null) {
                    log.error("Attribute {} value is null for entity id={} of type {}", attributeName, ((IEntity) object).getId(), baseClass.getName());
                    writer.addAttribute(attributeName + "_error", String.format("Attribute %s value is null", attributeName));
                    continue;
                }
                if (attributeValue instanceof Provider) {
                    attributeValue = ((Provider) attributeValue).getCode();
                }

                writer.addAttribute(attributeName, attributeValue.toString());

            } catch (IllegalArgumentException | IllegalAccessException e) {
                log.error("No attribute {} found on entity of type {}", attributeName, baseClass.getName());
                writer.addAttribute(attributeName + "_error", String.format("Attribute %s not found", attributeName));
            }
        }
    }

    /**
     * Get an attribute value. Handles composed attribute cases (e.g. provider.code)
     * 
     * @param object Object to get attribute value from
     * @param attributeName Attribute name. Can be a composed attribute name
     * @return Attribute value
     * @throws IllegalAccessException
     */
    private Object getAttributeValue(Object object, String attributeName) throws IllegalAccessException {

        Object value = object;
        StringTokenizer tokenizer = new StringTokenizer(attributeName, ".");
        while (tokenizer.hasMoreElements()) {
            String attrName = tokenizer.nextToken();
            value = FieldUtils.readField(value, attrName, true);
            if (value instanceof HibernateProxy) {
                value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
            }
        }
        return value;
    }

    /**
     * A lookup is done to DB using provided attributes to retrieve a reference to an entity
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {

        Class expectedType = context.getRequiredType();

        // If unmarshaling a provider entity and were told to explicitly force it to be given provider
        if (forceToProvider != null && Provider.class.isAssignableFrom(expectedType)) {
            log.trace("Forcing provider to {}", forceToProvider);
            return forceToProvider;
        }

        // // This was a solution to large data amount processing with JPA transaction on each entity deserialisation, but it gives issues with references between the objects
        // EntityManager em = (EntityManager) context.get("em");

        String idValue = reader.getAttribute("id");

        // Obtain a reference to an entity by ID
        if (referenceFKById && idValue != null) {
            // TODO maybe should check if an entity actually exists
            Object entity = em.find(expectedType, Long.parseLong(idValue));
            if (entity == null) {
                if (ignoreNotFoundFK) {
                    log.debug("Entity " + expectedType.getName() + " not found and will be ignored. Lookup by id=" + idValue);
                    return null;
                } else {
                    throw new ImportFKNotFoundException(expectedType, idValue, null, null);
                }
            }
            return entity;

            // Obtain a reference to an entity by other attributes
        } else {
            Map<String, Object> parameters = new HashMap<String, Object>();

            final Iterator<String> it = reader.getAttributeNames();
            while (it.hasNext()) {
                final String attrName = it.next();
                String attrValue = reader.getAttribute(attrName);

                // Ignore ID field if looking up entity by non-id attributes
                if ("id".equals(attrName)) {
                    continue;

                    // Ignore system attributes
                } else if ("class".equals(attrName) || EntityExportImportService.REFERENCE_ID_ATTRIBUTE.equals(attrName)) {
                    continue;

                    // Look up a provider by code or force it to a value provided
                } else if ("provider".equals(attrName) || attrName.endsWith(".provider")) {// TODO A check by field type would be better
                    Provider provider = forceToProvider;
                    if (provider == null) {
                        if (providerMap.containsKey(attrValue)) {
                            provider = em.getReference(Provider.class, providerMap.get(attrValue));
                        } else {
                            try {
                                provider = em.createQuery("select p from Provider p where p.code=:code", Provider.class).setParameter("code", attrValue).getSingleResult();
                                providerMap.put(attrValue, provider.getId());
                            } catch (NoResultException e) {
                                if (ignoreNotFoundFK) {
                                    log.debug("Entity Provider not found by code " + attrValue + " and FK for " + expectedType.getSimpleName() + "." + attrName
                                            + " will be ignored");
                                    return null;
                                } else {
                                    Map<String, Object> providerParams = new HashMap<String, Object>();
                                    providerParams.put("code", attrValue);
                                    throw new ImportFKNotFoundException(Provider.class, null, providerParams, e.getClass());
                                }
                            }
                        }
                    }
                    parameters.put(attrName, provider);

                    // Other attributes are used as found
                } else {
                    parameters.put(attrName, attrValue);
                }
            }

            // Probably the entity was exported as full in older versions and now trying to import as a reference
            if (parameters.isEmpty()) {
                log.debug("Entity {} not found. Reason: no parameters were found to query with. id={}. Will be passed to iEntityClassConverter.", expectedType.getName(), idValue);
                return context.convertAnother(context.currentObject(), expectedType, iEntityClassConverter);
            }
            // Construct a query to retrieve an entity by the attributes
            StringBuilder sql = new StringBuilder("select o from " + expectedType.getName() + " o where ");
            boolean firstWhere = true;
            for (Entry<String, Object> param : parameters.entrySet()) {
                if (!firstWhere) {
                    sql.append(" and ");
                }
                sql.append(String.format(" %s=:%s", param.getKey(), param.getKey().replace('.', '_')));
                firstWhere = false;
            }
            Query query = em.createQuery(sql.toString());
            for (Entry<String, Object> param : parameters.entrySet()) {

                Parameter<?> sqlParam = query.getParameter(param.getKey().replace('.', '_'));
                if (!sqlParam.getParameterType().isAssignableFrom(param.getValue().getClass())) {
                    if (Enum.class.isAssignableFrom(sqlParam.getParameterType())) {
                        for (Object enumValue : sqlParam.getParameterType().getEnumConstants()) {
                            if (((Enum) enumValue).name().equals(param.getValue())) {
                                param.setValue(enumValue);
                            }
                        }
                    } else if (Integer.class.isAssignableFrom(sqlParam.getParameterType())) {
                        param.setValue(Integer.parseInt((String) param.getValue()));
                    } else if (Long.class.isAssignableFrom(sqlParam.getParameterType())) {
                        param.setValue(Long.parseLong((String) param.getValue()));
                    }

                }
                query.setParameter(param.getKey().replace('.', '_'), param.getValue());
            }

            try {
                IEntity entity = (IEntity) query.getSingleResult();
                log.trace("Found entity {} id={} with attributes {}", entity.getClass().getName(), entity.getId(), parameters);
                // if (!em.contains(entity)) { // Should not be happening as entity manager is passed in unmarshal call instead of converter constructor
                // log.trace("Entity {} id={} is detached. Will be refreshed", entity.getClass().getName(), entity.getId());
                // entity = em.merge(entity);
                // }
                return entity;

            } catch (NoResultException | NonUniqueResultException e) {
                if (ignoreNotFoundFK || exportImportConfig.isIgnoreFKToClass(expectedType)) {
                    log.debug("Entity {} not found and will be ignored. Reason: {}. Lookup attributes: [id={}] {}", expectedType.getName(), e.getClass().getName(), idValue,
                        parameters);
                    return null;
                } else {
                    throw new ImportFKNotFoundException(expectedType, idValue, parameters, e.getClass());
                }
            }
        }
    }
}
