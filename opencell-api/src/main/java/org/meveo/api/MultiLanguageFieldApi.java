package org.meveo.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.CatMessagesDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.response.CatMessagesListDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.IEntity;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.MultiLanguageFieldService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.TradingLanguageService;

public class MultiLanguageFieldApi extends BaseApi {

    private ParamBean paramBean = ParamBean.getInstance();

    @Inject
    private MultiLanguageFieldService multiLanguageFieldService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    /**
     * Find entity field translations for a particular entity, field (optional) and a language (optional)
     * 
     * @param entityClassName Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name
     * @param languageCode 3 letter language code
     * @return A list of field value translations
     * @throws MeveoApiException
     */
    @SuppressWarnings({ "rawtypes" })
    public CatMessagesDto find(String entityClassName, String code, Date validFrom, Date validTo, String fieldname, String languageCode) throws MeveoApiException {

        if (StringUtils.isBlank(entityClassName)) {
            missingParameters.add("entityClass");
        }
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Class entityClass = null;
        try {
            entityClass = Class.forName(entityClassName);
        } catch (ClassNotFoundException e) {
            throw new InvalidParameterException(e.getMessage());
        }

        List<String> fields = null;
        if (fieldname != null) {
            fields = Arrays.asList(fieldname);
        } else {
            fields = multiLanguageFieldService.getMultiLanguageFields(entityClass);
        }

        Collection<String> languageCodes = null;
        if (languageCode != null) {
            languageCodes = Arrays.asList(languageCode);
        } else {
            languageCodes = tradingLanguageService.listLanguageCodes();
        }

        PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass);

        IEntity entity = findEntity(persistenceService, entityClassName, code, validFrom, validTo);

        CatMessagesDto messageDto = convertEntity(entity, fields, languageCodes);

        return messageDto;
    }

    /**
     * Remove field value translation for a given entity, field (optional) and language (optional)
     * 
     * @param entityClassName Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void remove(String entityClassName, String code, Date validFrom, Date validTo, String fieldname, String languageCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(entityClassName)) {
            missingParameters.add("entityClass");
        }
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Class entityClass = null;
        try {
            entityClass = Class.forName(entityClassName);
        } catch (ClassNotFoundException e) {
            throw new InvalidParameterException(e.getMessage());
        }

        PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass);

        IEntity entity = findEntity(persistenceService, entityClassName, code, validFrom, validTo);

        List<String> fields = null;
        if (fieldname != null) {
            fields = Arrays.asList(fieldname);
        } else {
            fields = multiLanguageFieldService.getMultiLanguageFields(entityClass);
        }

        for (String field : fields) {

            try {
                if (StringUtils.isBlank(languageCode)) {
                    FieldUtils.writeField(entity, field + "I18n", null, true);

                } else {
                    Map<String, String> translatedValues = (Map<String, String>) FieldUtils.readField(entity, field + "I18n", true);
                    translatedValues.remove(languageCode);
                    FieldUtils.writeField(entity, field + "I18n", translatedValues, true);
                }

            } catch (IllegalAccessException e) {
                log.error("Failed to read value of field {}", field + "I18n", e);
                throw new InvalidParameterException("fieldname", fieldname);
            }

        }

        persistenceService.update(entity);

    }

    /**
     * Set translated entity field values
     * 
     * @param postData
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void createOrUpdate(CatMessagesDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getEntityClass())) {
            missingParameters.add("entityClass");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Class entityClass = null;
        try {
            entityClass = Class.forName(postData.getEntityClass());
        } catch (ClassNotFoundException e) {
            throw new InvalidParameterException(e.getMessage());
        }

        if (StringUtils.isBlank(postData.getFieldName())) {
            postData.setFieldName("description");
        }

        if (!(StringUtils.isBlank(postData.getDefaultDescription())) && (StringUtils.isBlank(postData.getDefaultValue()))) {
            postData.setDefaultValue(postData.getDefaultDescription());
        }

        if (postData.getTranslatedDescriptions() != null && !postData.getTranslatedDescriptions().isEmpty()
                && (postData.getTranslatedValues() == null || postData.getTranslatedValues().isEmpty())) {
            postData.setTranslatedValues(postData.getTranslatedValues());
        }

        PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass);
        IEntity entity = findEntity(persistenceService, postData.getEntityClass(), postData.getCode(), postData.getValidFrom(), postData.getValidTo());

        Map<String, String> translatedValues = convertMultiLanguageToMapOfValues(postData.getTranslatedValues());

        try {
            FieldUtils.writeField(entity, postData.getFieldName(), postData.getDefaultValue(), true);
            FieldUtils.writeField(entity, postData.getFieldName() + "I18n", translatedValues, true);
        } catch (IllegalAccessException e) {
            log.error("Failed to set value to field {}", postData.getFieldName(), e);
            throw new InvalidParameterException("fieldname", postData.getFieldName());
        }
        persistenceService.update(entity);
    }

    /**
     * List entity field value translations for a given entity type (optional), field (optional) and language (optional). Note: will provide ONLY those entities that have at least one of multilanguage fields translated. 
     * 
     * @param entityClassName Entity class name
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @return A list of entity field value translations
     * @throws MeveoApiException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public CatMessagesListDto list(String entityClassName, String fieldname, String languageCode) throws MeveoApiException {
        CatMessagesListDto catMessagesListDto = new CatMessagesListDto();

        Collection<Class> entityClasses = null;
        if (entityClassName != null) {
            Class entityClass = null;
            try {
                entityClass = Class.forName(entityClassName);
                entityClasses = Arrays.asList(entityClass);
            } catch (ClassNotFoundException e) {
                throw new InvalidParameterException(e.getMessage());
            }
        } else {
            entityClasses = multiLanguageFieldService.getMultiLanguageFieldMapping().keySet();
        }

        for (Class entityClass : entityClasses) {

            List<String> fields = null;
            if (fieldname != null) {
                fields = Arrays.asList(fieldname);
            } else {
                fields = multiLanguageFieldService.getMultiLanguageFields(entityClass);
            }

            Collection<String> languageCodes = null;
            if (languageCode != null) {
                languageCodes = Arrays.asList(languageCode);
            } else {
                languageCodes = tradingLanguageService.listLanguageCodes();
            }

            PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass);

            String sql = null;
            for (String field : fields) {
                if (sql == null) {
                    sql = (sql == null ? " a." : " and a.") + field + "I18n is not null ";
                }
            }

            Map<String, Object> filters = new HashMap<>();
            filters.put(PersistenceService.SEARCH_SQL, sql);
            PaginationConfiguration paginationConfig = new PaginationConfiguration(filters);
            List<IEntity> entities = persistenceService.list(paginationConfig);
            for (IEntity entity : entities) {
                CatMessagesDto messageDto = convertEntity(entity, fields, languageCodes);
                catMessagesListDto.getCatMessage().add(messageDto);
            }
        }

        return catMessagesListDto;
    }

    @SuppressWarnings("rawtypes")
    private IEntity findEntity(PersistenceService persistenceService, String entityClass, String code, Date validFrom, Date validTo) throws MeveoApiException {
        IEntity entity = null;
        // If Entity is versioned
        if (MethodUtils.getAccessibleMethod(persistenceService.getClass(), "findByCode", String.class, Date.class, Date.class) != null) {
            try {
                entity = (IEntity) MethodUtils.invokeExactMethod(persistenceService, "findByCode", code, validFrom, validTo);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to find an entity by validity dates", e);
                throw new MeveoApiException(e);
            }
            if (entity == null) {
                String datePattern = paramBean.getDateTimeFormat();
                throw new EntityDoesNotExistsException(entityClass,
                    code + " / " + DateUtils.formatDateWithPattern(validFrom, datePattern) + " / " + DateUtils.formatDateWithPattern(validTo, datePattern));
            }

            // If entity is not versioned
        } else {
            entity = ((BusinessService) persistenceService).findByCode(code);
            if (entity == null) {
                throw new EntityDoesNotExistsException(entityClass, code);
            }
        }

        return entity;
    }

    @SuppressWarnings("unchecked")
    private CatMessagesDto convertEntity(IEntity entity, List<String> fields, Collection<String> languageCodes) throws MeveoApiException {

        CatMessagesDto messageDto = new CatMessagesDto();
        messageDto.setEntityClass(entity.getClass().getName());

        if (entity instanceof BusinessEntity) {
            messageDto.setCode(((BusinessEntity) entity).getCode());
        }

        try {
            Field validityField = FieldUtils.getField(entity.getClass(), "validity", true);
            if (validityField != null) {
                DatePeriod validity = (DatePeriod) FieldUtils.readField(entity, "validity", true);
                if (validity != null) {
                    messageDto.setValidFrom(validity.getFrom());
                    messageDto.setValidTo(validity.getTo());
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Failed to read value of field Validity", e);
            throw new MeveoApiException(e);
        }

        for (String field : fields) {

            try {
                messageDto.setDefaultValue((String) FieldUtils.readField(entity, field, true));
                messageDto.setDefaultDescription(messageDto.getDefaultValue());

                Map<String, String> translatedValues = (Map<String, String>) FieldUtils.readField(entity, field + "I18n", true);
                if (translatedValues != null && !translatedValues.isEmpty()) {
                    messageDto.setTranslatedValues(new ArrayList<>());
                    for (String language : languageCodes) {
                        if (translatedValues.containsKey(language)) {
                            messageDto.getTranslatedValues().add(new LanguageDescriptionDto(language, translatedValues.get(language)));
                        }
                    }
                    if (messageDto.getTranslatedValues().isEmpty()) {
                        messageDto.setTranslatedValues(null);
                    }
                }

            } catch (IllegalAccessException e) {
                log.error("Failed to read value of field {}", field, e);
                throw new InvalidParameterException("fieldname", field);
            }

        }
        return messageDto;
    }
}
