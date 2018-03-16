package org.meveo.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    @Inject
    private MultiLanguageFieldService multiLanguageFieldService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    /**
     * Find entity field translations for a particular entity, field (defaults to "description") and a language (optional).
     * 
     * @param entityClassName Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name. Defaults to "description" if not provided
     * @param languageCode 3 letter language code
     * @return A list of field value translations
     * @throws MeveoApiException meveo api exception.
     */
    @SuppressWarnings({ "rawtypes" })
    public CatMessagesListDto find(String entityClassName, String code, Date validFrom, Date validTo, String fieldname, String languageCode) throws MeveoApiException {

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
            throw new InvalidParameterException("Unknown classname " + entityClassName + ". Please provide a full classname");
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

        CatMessagesListDto catMessagesListDto = new CatMessagesListDto();
        catMessagesListDto.getCatMessage().addAll(convertEntity(entity, fields, languageCodes));

        return catMessagesListDto;
    }

    /**
     * Remove field value translation for a given entity, field (optional) and language (optional).
     * 
     * @param entityClassName Entity class name
     * @param code Entity code
     * @param validFrom Validity dates - from
     * @param validTo Validity dates - to
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
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
            throw new InvalidParameterException("Unknown classname " + entityClassName + ". Please provide a full classname");
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
     * Set translated entity field values.
     * 
     * @param translationInfos translation.
     * @throws MeveoApiException meveo api exception.
     * @throws BusinessException business exception.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void update(List<CatMessagesDto> translationInfos) throws MeveoApiException, BusinessException {

        for (CatMessagesDto translationInfo : translationInfos) {

            if (StringUtils.isBlank(translationInfo.getEntityClass())) {
                missingParameters.add("entityClass");
            }
            if (StringUtils.isBlank(translationInfo.getCode())) {
                missingParameters.add("code");
            }

            handleMissingParameters();

            Class entityClass = null;
            try {
                entityClass = Class.forName(translationInfo.getEntityClass());
            } catch (ClassNotFoundException e) {
                throw new InvalidParameterException("Unknown classname " + translationInfo.getEntityClass() + ". Please provide a full classname");
            }

            if (StringUtils.isBlank(translationInfo.getFieldName())) {
                translationInfo.setFieldName("description");
            }

            if (!(StringUtils.isBlank(translationInfo.getDefaultDescription())) && (StringUtils.isBlank(translationInfo.getDefaultValue()))) {
                translationInfo.setDefaultValue(translationInfo.getDefaultDescription());
            }

            if (translationInfo.getTranslatedDescriptions() != null && !translationInfo.getTranslatedDescriptions().isEmpty()
                    && (translationInfo.getTranslatedValues() == null || translationInfo.getTranslatedValues().isEmpty())) {
                translationInfo.setTranslatedValues(translationInfo.getTranslatedValues());
            }

            PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entityClass);
            IEntity entity = findEntity(persistenceService, translationInfo.getEntityClass(), translationInfo.getCode(), translationInfo.getValidFrom(),
                translationInfo.getValidTo());

            try {
                Map<String, String> currentValue = (Map<String, String>) FieldUtils.readField(entity, translationInfo.getFieldName() + "I18n", true);

                Map<String, String> translatedValues = convertMultiLanguageToMapOfValues(translationInfo.getTranslatedValues(), currentValue);

                if (translationInfo.getDefaultValue() != null) {
                    FieldUtils.writeField(entity, translationInfo.getFieldName(), translationInfo.getDefaultValue(), true);
                }
                FieldUtils.writeField(entity, translationInfo.getFieldName() + "I18n", translatedValues, true);
            } catch (IllegalAccessException e) {
                log.error("Failed to set value to field {}", translationInfo.getFieldName(), e);
                throw new InvalidParameterException("fieldname", translationInfo.getFieldName());
            }
            persistenceService.update(entity);
        }
    }

    /**
     * List entity field value translations for a given entity type (optional), field (optional) and language (optional). Note: will provide ONLY those entities that have at least
     * one of multilanguage fields translated.
     * 
     * @param entityClassName Entity class name
     * @param fieldname Field name. Optional
     * @param languageCode 3 letter language code. Optional
     * @return A list of entity field value translations
     * @throws MeveoApiException meveo api exception.
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
                throw new InvalidParameterException("Unknow classname " + entityClassName + ". Please provide a full classname");
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
                sql = (sql == null ? " a." : sql + " or a.") + field + "I18n is not null ";
            }

            sql = " 1=:one and (" + sql + ")";

            Map<String, Object> filters = new HashMap<>();
            filters.put(PersistenceService.SEARCH_SQL, new Object[] { sql, "one", 1 });
            PaginationConfiguration paginationConfig = new PaginationConfiguration(filters);
            List<IEntity> entities = persistenceService.list(paginationConfig);
            for (IEntity entity : entities) {
                List<CatMessagesDto> messageDtos = convertEntity(entity, fields, languageCodes);
                catMessagesListDto.getCatMessage().addAll(messageDtos);
            }
        }

        return catMessagesListDto;
    }

    @SuppressWarnings("rawtypes")
    private IEntity findEntity(PersistenceService persistenceService, String entityClass, String code, Date validFrom, Date validTo) throws MeveoApiException {
        IEntity entity = null;
        // If Entity is versioned
        Method method = MethodUtils.getAccessibleMethod(persistenceService.getClass(), "findByCode", String.class, Date.class, Date.class);
        if (method != null) {
            try {
                entity = (IEntity) method.invoke(persistenceService, code, validFrom, validTo);
                // entity = (IEntity) MethodUtils.invokeExactMethod(persistenceService, "findByCode", code, validFrom, validTo);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Failed to find an entity by validity dates", e);
                throw new MeveoApiException(e);
            }
            if (entity == null) {
                String datePattern = paramBeanFactory.getInstance().getDateTimeFormat();
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
    private List<CatMessagesDto> convertEntity(IEntity entity, List<String> fields, Collection<String> languageCodes) throws MeveoApiException {

        List<CatMessagesDto> translations = new ArrayList<>();

        for (String field : fields) {

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

            try {
                messageDto.setFieldName(field);
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
                messageDto.setTranslatedDescriptions(messageDto.getTranslatedValues());

                if (messageDto.getDefaultValue() != null || messageDto.getTranslatedValues() != null) {
                    translations.add(messageDto);
                }

            } catch (IllegalAccessException e) {
                log.error("Failed to read value of field {}", field, e);
                throw new InvalidParameterException("fieldname", field);
            }
        }
        return translations;
    }
}
