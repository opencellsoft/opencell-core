package org.meveo.model.persistence;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.PostgreSQLJsonbJdbcType;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BlobJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.meveo.commons.encryption.IEncryptable;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.custom.CustomFieldValue;
import org.meveo.model.crm.custom.CustomFieldValues;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * JSON type field mapping that adapts both for Oracle's Clob and Postgresql's JsonB type field based on a system parameter.<br/>
 * A value of -Dopencell.json.db.type=clob will implement Oracle's Clob and a missing or any other value will assume Postgresql JsonB implementation.
 * 
 * @author Andrius Karpavicius
 */
public class CustomFieldJsonDataType extends AbstractClassJavaType<CustomFieldValues> implements IEncryptable {

    private static final long serialVersionUID = -297846596273026071L;

    public static final CustomFieldJsonDataType INSTANCE = new CustomFieldJsonDataType();

    /**
     * Is encryption of CF values enabled?
     */
    public static boolean IS_ENCRYPT_CF = TRUE_STR.equalsIgnoreCase(ParamBean.getInstance().getProperty(ENCRYPT_CUSTOM_FIELDS_PROPERTY, FALSE_STR))
            && !StringUtils.isBlank(ParamBean.getInstance().getProperty(OPENCELL_SHA_KEY_PROPERTY, null));

    /**
     * Json field type in DB. To distinquish what Hibernate data type mapper should be used to interpret the value and convert from json to an object. <br/>
     * Currently supported value is "clob" for oracle implementation. Any other or missing value will use a default value of "jsonb" for postgresql.
     */
    private static String JSON_DB_TYPE = "opencell.json.db.type";

    /**
     * Is clob used to store JSON fields?
     */
    public static boolean IS_CLOB = "clob".equalsIgnoreCase(System.getProperty(JSON_DB_TYPE));

    public CustomFieldJsonDataType() {
        super(CustomFieldValues.class);
    }

    @Override
    public MutabilityPlan<CustomFieldValues> getMutabilityPlan() {
        return CustomFieldValueJsonDataTypeMutabilityPlan.INSTANCE;
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators indicators) {
        return IS_CLOB ? BlobJdbcType.DEFAULT : PostgreSQLJsonbJdbcType.INSTANCE;
    }

    @Override
    public String toString(CustomFieldValues value) {
        if (value == null) {
            return null;
        }

        if (IS_ENCRYPT_CF) {
            return encryptCfs(value);
        }

        return value.asJson();
    }

    @Override
    public CustomFieldValues fromString(CharSequence string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }

        Map<String, List<CustomFieldValue>> cfValues = JacksonUtil.fromString(string.toString(), new TypeReference<Map<String, List<CustomFieldValue>>>() {
        });
        return new CustomFieldValues(decryptCfs(cfValues));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X unwrap(CustomFieldValues value, Class<X> type, WrapperOptions options) {

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKK CF value to unwrap is {}, to a type {}", (value != null ? value.getClass() : null), type);

        if (value == null || toString(value) == null) {
            return null;

        } else if (CharacterStream.class.isAssignableFrom(type)) {
            return (X) new CharacterStreamImpl(toString(value));

        } else if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);

        } else if (JsonNode.class.isAssignableFrom(type)) {

            return (X) JacksonUtil.toJsonNode(toString(value));
        }

        throw unknownUnwrap(type);
    }

    @Override
    public <X> CustomFieldValues wrap(X value, WrapperOptions options) {

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKKKK CF value to wrap is " + (value != null ? value.getClass() : null));

        if (value == null) {
            return null;

        } else if (String.class.isInstance(value)) {
            return fromString((String) value);

            // Support for Oracle's CLOB type field
        } else if (value instanceof Clob) {
            String clobString = null;
            try {
                clobString = IOUtils.toString(((Clob) value).getCharacterStream());

            } catch (IOException | SQLException e) {
                throw new RuntimeException("Failed to read clob value", e);
            }
            return fromString(clobString);

            // Support for Postgresql JsonB type field
        } else {
            return fromString(value.toString());
        }
    }

    @Override
    public boolean areEqual(CustomFieldValues one, CustomFieldValues another) {

        // Logger log = LoggerFactory.getLogger(getClass());
        // log.error("AKKKK CF value is equal check");

        boolean equals = super.areEqual(one, another);

        if (equals && one != null && CollectionUtils.isNotEmpty(one.getDirtyCfValues())) {
            if (another != null && CollectionUtils.isNotEmpty(another.getDirtyCfValues())) {

                if (!CollectionUtils.isEqualCollection(one.getDirtyCfValues(), another.getDirtyCfValues())) {
                    return false;
                } else {
                    return true;
                }

            } else {
                return false;
            }
        }
        return equals;
    }

    private Map<String, List<CustomFieldValue>> decryptCfs(Map<String, List<CustomFieldValue>> cfValues) {
        for (Entry<String, List<CustomFieldValue>> listCfs : cfValues.entrySet()) {
            for (CustomFieldValue cf : listCfs.getValue()) {
                if (cf.getStringValue() != null && cf.getStringValue().startsWith(ENCRYPTION_CHECK_STRING)) {
                    cf.setStringValue(IS_ENCRYPT_CF ? decrypt(cf.getStringValue()) : cf.getStringValue());
                } else if (cf.getListValue() != null) {
                    List<Object> listValues = new ArrayList<>();
                    for (Object object : cf.getListValue()) {
                        if (object instanceof String) {
                            String valueString = (String) object;
                            if (valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                listValues.add(IS_ENCRYPT_CF ? decrypt(valueString) : valueString);
                            } else {
                                listValues.add(valueString);
                            }
                        } else {
                            listValues.add(object);
                        }
                    }
                    cf.setListValue(listValues);
                } else if (cf.getMapValue() != null) {
                    Map<String, Object> mapValues = new LinkedHashMap<>();
                    for (Entry<String, Object> object : cf.getkeyValueMap().entrySet()) {
                        if (object.getValue() instanceof String) {
                            String valueString = (String) object.getValue();
                            if (valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                mapValues.put(object.getKey(), IS_ENCRYPT_CF ? decrypt(valueString) : valueString);
                            } else {
                                mapValues.put(object.getKey(), valueString);
                            }
                        } else {
                            mapValues.put(object.getKey(), object.getValue());
                        }
                    }
                    cf.setMapValue(mapValues);
                }
            }
        }
        return cfValues;
    }

    private String encryptCfs(CustomFieldValues cfValues) {
        for (Entry<String, List<CustomFieldValue>> listCfs : cfValues.getValuesByCode().entrySet()) {
            for (CustomFieldValue cf : listCfs.getValue()) {
                if (cf.getStringValue() != null && !cf.getStringValue().startsWith(ENCRYPTION_CHECK_STRING)) {
                    cf.setStringValue(encrypt(cf.getStringValue()));
                } else if (cf.getListValue() != null) {
                    List<Object> listValues = new ArrayList<>();
                    for (Object object : cf.getListValue()) {
                        if (object instanceof String) {
                            String valueString = (String) object;
                            if (!valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                listValues.add(encrypt(valueString));
                            } else {
                                listValues.add(valueString);
                            }
                        } else {
                            listValues.add(object);
                        }
                    }
                    cf.setListValue(listValues);
                } else if (cf.getMapValue() != null) {
                    Map<String, Object> mapValues = new LinkedHashMap<>();
                    for (Entry<String, Object> object : cf.getkeyValueMap().entrySet()) {
                        if (object.getValue() instanceof String) {
                            String valueString = (String) object.getValue();
                            if (!valueString.startsWith(ENCRYPTION_CHECK_STRING)) {
                                mapValues.put(object.getKey(), encrypt(valueString));
                            } else {
                                mapValues.put(object.getKey(), valueString);
                            }
                        } else {
                            mapValues.put(object.getKey(), object.getValue());
                        }
                    }
                    cf.setMapValue(mapValues);
                }
            }
        }
        return cfValues.toString();
    }
}