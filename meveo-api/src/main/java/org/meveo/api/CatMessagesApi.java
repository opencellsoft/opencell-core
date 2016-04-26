package org.meveo.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CatMessagesDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.response.CatMessagesListDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IAuditable;
import org.meveo.model.IProvider;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.admin.User;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.reflections.Reflections;

public class CatMessagesApi extends BaseApi {

	@Inject
	private CatMessagesService catMessagesService;

	/**
	 Retrieves a CatMessages by code.
	 
	 @param catMessagesCode
	 @param languageCode
	 @param provider
	 @return
	 @throws MeveoApiException
	 */
	public CatMessagesDto find(String catMessagesCode, String languageCode, Provider provider)
			throws MeveoApiException {

		if (StringUtils.isBlank(catMessagesCode)) {
			missingParameters.add("catMessagesCode");
		}

		if (StringUtils.isBlank(languageCode)) {
			missingParameters.add("languageCode");
		}

		handleMissingParameters();

		CatMessages translation = catMessagesService.findByCodeAndLanguage(catMessagesCode, languageCode, provider);
		if(translation == null){
			throw new EntityDoesNotExistsException(CatMessages.class, catMessagesCode);
		}
		
		CatMessagesDto messageDto = null;
		BusinessEntity entity = getEntityByMessageCode(catMessagesCode);
		if (entity != null) {
			messageDto = new CatMessagesDto();
			messageDto.setCode(entity.getCode());
			messageDto.setDefaultDescription(entity.getDescription());
			messageDto.setEntityClass(entity.getClass().getSimpleName());
			List<CatMessages> messages = new ArrayList<>();
			messages.add(translation);
			List<LanguageDescriptionDto> translations = getTranslations(messages);
			messageDto.setTranslatedDescriptions(translations);
		} else {
			throw new EntityDoesNotExistsException(BusinessEntity.class, catMessagesCode);
		}

		return messageDto;
	}

	public void remove(String catMessagesCode, String languageCode, Provider provider) throws MeveoApiException {

		if (StringUtils.isBlank(catMessagesCode)) {
			missingParameters.add("catMessagesCode");
		}

		if (StringUtils.isBlank(languageCode)) {
			missingParameters.add("languageCode");
		}

		handleMissingParameters();

		CatMessages catMessages = catMessagesService.findByCodeAndLanguage(catMessagesCode, languageCode, provider);

		if (catMessages == null) {
			throw new EntityDoesNotExistsException(CatMessages.class, catMessagesCode);
		}

		catMessagesService.remove(catMessages);
		 
	}

	public void createOrUpdate(CatMessagesDto postData, User currentUser) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getEntityClass())) {
			missingParameters.add("entityClass");
		}
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}

		handleMissingParameters();

		// retrieve entity
		BusinessEntity entity = fetchBusinessEntity(postData.getEntityClass(), postData.getCode());

		// update default description only if it is not blank
		if (!StringUtils.isBlank(postData.getDefaultDescription())) {
			entity.setDescription(postData.getDefaultDescription());
			update(entity, currentUser);
		}
		// save translations
		saveTranslations(entity, postData.getTranslatedDescriptions(), currentUser);

	}
	
	private BusinessEntity fetchBusinessEntity(String className, String code) throws MeveoApiException {
		// check if entities exist
		Class<?> entityClass = getEntityClass(className);
		BusinessEntity entity = null;
		
		if (entityClass != null) {
			QueryBuilder qb = new QueryBuilder(entityClass, "c");
			qb.addCriterion("code", "=", code, false);
			entity = (BusinessEntity) qb.getQuery(catMessagesService.getEntityManager()).getSingleResult();
			if (entity == null) {
				throw new EntityDoesNotExistsException(BusinessEntity.class, code);
			}
		} else {
			throw new EntityDoesNotExistsException(className, code);
		}
		
		return entity;
	}

	private void saveTranslations(BusinessEntity entity, List<LanguageDescriptionDto> translations, User currentUser)
			throws MeveoApiException, BusinessException {
		
		// loop over translations
		CatMessages message = null;
		boolean isBlankDescription = false;
		String messageCode = catMessagesService.getMessageCode(entity);
		
		for (LanguageDescriptionDto translation : translations) {
			isBlankDescription = StringUtils.isBlank(translation.getDescription());
			// check if translation exists
			message = null;
			message = catMessagesService.findByCodeAndLanguage(messageCode, translation.getLanguageCode(),
					currentUser.getProvider());
			// create/update/delete translations
			if (message != null && !isBlankDescription) { // message exists and description is not blank
				message.setDescription(translation.getDescription());
				update(message, currentUser);
			} else if (message != null && isBlankDescription) { // message exists and description is blank
				remove(message, currentUser);
			} else if (message == null && !isBlankDescription) { // message does not exist and description is not blank
				message = new CatMessages();
				message.setMessageCode(messageCode);
				message.setLanguageCode(translation.getLanguageCode());
				message.setDescription(translation.getDescription());
				create(message, currentUser);
			}
		}
	}

	public CatMessagesListDto list(Provider provider) {
		CatMessagesListDto catMessagesListDto = new CatMessagesListDto();
		List<CatMessages> catMessagesList = catMessagesService.list();

		if (catMessagesList != null && !catMessagesList.isEmpty()) {
			
			CatMessagesDto messageDto = null;
			BusinessEntity entity = null;
			Map<String, List<CatMessages>> entities = parseEntities(catMessagesList);
			List<CatMessages> messageList = null;
			List<LanguageDescriptionDto> translations = null;

			for (String messageCode : entities.keySet()) {
				messageList = entities.get(messageCode);
				entity = getEntityByMessageCode(messageCode);
				if (entity != null) {
					messageDto = new CatMessagesDto();
					messageDto.setCode(entity.getCode());
					messageDto.setDefaultDescription(entity.getDescription());
					messageDto.setEntityClass(entity.getClass().getSimpleName());
					translations = getTranslations(messageList);
					messageDto.setTranslatedDescriptions(translations);
					catMessagesListDto.getCatMessage().add(messageDto);
				}
			}
		}
		return catMessagesListDto;
	}

	private void create(AuditableEntity entity, User currentUser) throws MeveoApiException, BusinessException {
		log.trace("start of create {} entity={}", entity.getClass().getSimpleName(), entity);
		if (entity instanceof IAuditable) {
			((IAuditable) entity).updateAudit(currentUser);
		}
		if (entity instanceof IProvider && (((IProvider) entity).getProvider() == null)) {
			((BaseEntity) entity).setProvider(currentUser.getProvider());
		}
		catMessagesService.getEntityManager().persist(entity);
		log.debug("end of create {}. entity id={}.", entity.getClass().getSimpleName(), entity.getId());
	}

	private void update(AuditableEntity entity, User currentUser) throws MeveoApiException, BusinessException {
		log.trace("start of update {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());

		if (entity instanceof IAuditable) {
			((IAuditable) entity).updateAudit(currentUser);
		}
		if (entity instanceof IProvider && (((IProvider) entity).getProvider() == null)) {
			((BaseEntity) entity).setProvider(currentUser.getProvider());
		}
		entity = catMessagesService.getEntityManager().merge(entity);
		log.debug("end of update {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());
	}

	private void remove(AuditableEntity entity, User currentUser) {
		log.trace("start of remove {} entity (id={}) ..", entity.getClass().getSimpleName(), entity.getId());
		if (entity instanceof IProvider && (((IProvider) entity).getProvider() == null)) {
			((BaseEntity) entity).setProvider(currentUser.getProvider());
		}
		catMessagesService.getEntityManager().remove(entity);
		log.debug("end of remove {} entity (id={}).", entity.getClass().getSimpleName(), entity.getId());
	}

	private BusinessEntity getEntityByMessageCode(String messageCode) {
		BusinessEntity entity = null;
		if (!StringUtils.isBlank(messageCode) && messageCode.contains("_")) {
			try {
				String[] classAndId = messageCode.split("_");
				String className = classAndId[0];
				long entityId = Long.parseLong(classAndId[1]);
				entity = getEntityByClassNameAndId(className, entityId);
			} catch (NumberFormatException e) {
				log.warn("Invalid Entity Id.  Will return null entity.", e);
			}
		}
		return entity;
	}

	private BusinessEntity getEntityByClassNameAndId(String className, long entityId) {
		Class<?> entityClass = getEntityClass(className);
		BusinessEntity entity = null;
		if (entityClass != null) {
			try {
				log.trace("start of find {} by id (id={}) ..", entityClass.getSimpleName(), entityId);
				entity = (BusinessEntity) catMessagesService.getEntityManager().find(entityClass, entityId);
				log.debug("end of find {} by id (id={}). Result found={}.", entityClass.getSimpleName(), entityId,
						entity != null);
			} catch (NoResultException e) {
				log.warn("Error encountered while retrieving business entity.  Will return null.", e);
			}
		}
		return entity;
	}

	private Class<?> getEntityClass(String className) {
		Class<?> entityClass = null;
		if (!StringUtils.isBlank(className)) {
			Reflections reflections = new Reflections("org.meveo.model");
			Set<Class<?>> multiLanguageClasses = reflections.getTypesAnnotatedWith(MultilanguageEntity.class);
			for (Class<?> multiLanguageClass : multiLanguageClasses) {
				if (className.equals(multiLanguageClass.getSimpleName())) {
					entityClass = multiLanguageClass;
					break;
				}
			}
		}
		return entityClass;
	}

	private Map<String, List<CatMessages>> parseEntities(List<CatMessages> messageList) {
		Map<String, List<CatMessages>> messageMap = null;
		String messageCode = null;
		List<CatMessages> list = null;
		for (CatMessages message : messageList) {
			if (messageMap == null) {
				messageMap = new HashMap<>();
			}
			messageCode = message.getMessageCode();
			if (!StringUtils.isBlank(messageCode)) {
				list = messageMap.get(messageCode);
				if (list == null) {
					list = new ArrayList<>();
					messageMap.put(messageCode, list);
				}
				list.add(message);
			}
		}
		return messageMap;
	}

	private List<LanguageDescriptionDto> getTranslations(List<CatMessages> messageList) {
		List<LanguageDescriptionDto> translations = null;
		LanguageDescriptionDto translation = null;
		for (CatMessages message : messageList) {
			if (translations == null) {
				translations = new ArrayList<>();
			}
			translation = new LanguageDescriptionDto();
			translation.setLanguageCode(message.getLanguageCode());
			translation.setDescription(message.getDescription());
			translations.add(translation);
		}
		return translations;
	}	
}
