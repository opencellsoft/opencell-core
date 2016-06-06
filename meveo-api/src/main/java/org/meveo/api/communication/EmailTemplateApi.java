package org.meveo.api.communication;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.dto.communication.EmailTemplatesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.communication.impl.EmailTemplateService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 5:09:27 AM
 *
 */
@Stateless
public class EmailTemplateApi extends BaseApi {

	@Inject
	private EmailTemplateService emailTemplateService;
	public void create(EmailTemplateDto emailTemplateDto,User currentUser) throws MeveoApiException,BusinessException{
		if(StringUtils.isNotEmpty(emailTemplateDto.getCode())){
			EmailTemplate existedEmailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode(), currentUser.getProvider());
			if(existedEmailTemplate!=null){
				throw new EntityAlreadyExistsException(EmailTemplate.class, emailTemplateDto.getCode());
			}
			EmailTemplate emailTemplate=new EmailTemplate();
			emailTemplate.setCode(emailTemplateDto.getCode());
			emailTemplate.setDescription(emailTemplateDto.getDescription());
			emailTemplate.setMedia(emailTemplateDto.getMedia());
			emailTemplate.setTagStartDelimiter(emailTemplateDto.getTagStartDelimiter());
			emailTemplate.setTagEndDelimiter(emailTemplateDto.getTagEndDelimiter());
			emailTemplate.setStartDate(emailTemplateDto.getStartDate());
			emailTemplate.setEndDate(emailTemplateDto.getEndDate());
			emailTemplate.setType(emailTemplateDto.getType());
			emailTemplate.setSubject(emailTemplateDto.getSubject());
			emailTemplate.setHtmlContent(emailTemplateDto.getHtmlContent());
			emailTemplate.setTextContent(emailTemplateDto.getTextContent());
			emailTemplateService.create(emailTemplate, currentUser);
		}else{
			missingParameters.add("code");
			handleMissingParameters();
		}
	}
	public void update(EmailTemplateDto emailTemplateDto,User currentUser) throws MeveoApiException,BusinessException{
		if(StringUtils.isNotEmpty(emailTemplateDto.getCode())){
			EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode(), currentUser.getProvider());
			if(emailTemplate==null){
				throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateDto.getCode());
			}
			emailTemplate.setDescription(emailTemplateDto.getDescription());
			emailTemplate.setMedia(emailTemplateDto.getMedia());
			emailTemplate.setTagStartDelimiter(emailTemplateDto.getTagStartDelimiter());
			emailTemplate.setTagEndDelimiter(emailTemplateDto.getTagEndDelimiter());
			emailTemplate.setStartDate(emailTemplateDto.getStartDate());
			emailTemplate.setEndDate(emailTemplateDto.getEndDate());
			emailTemplate.setType(emailTemplateDto.getType());
			emailTemplate.setSubject(emailTemplateDto.getSubject());
			emailTemplate.setHtmlContent(emailTemplateDto.getHtmlContent());
			emailTemplate.setTextContent(emailTemplateDto.getTextContent());
			emailTemplateService.update(emailTemplate, currentUser);
		}else{
			missingParameters.add("code");
			handleMissingParameters();
		}
	}
	public EmailTemplateDto find(String code,Provider currentProvider) throws MeveoApiException,BusinessException{
		if(StringUtils.isEmpty(code)){
			missingParameters.add("code");
			handleMissingParameters();
		}
		EmailTemplate emailTemplate=emailTemplateService.findByCode(code, currentProvider);
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, code);
		}
		return new EmailTemplateDto(emailTemplate);
	}
	public void remove(String code,Provider provider) throws MeveoApiException{
		if(StringUtils.isNotEmpty(code)){
			EmailTemplate emailTemplate=emailTemplateService.findByCode(code, provider);
			if(emailTemplate==null){
				throw new EntityDoesNotExistsException(EmailTemplate.class, code);
			}
			emailTemplateService.remove(emailTemplate);
		}else{
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
	public EmailTemplatesDto list(Provider provider) throws MeveoApiException{
		EmailTemplatesDto result=new EmailTemplatesDto();
		List<EmailTemplate> emailTemplates=emailTemplateService.list(provider);
		if(emailTemplates!=null){
			for(EmailTemplate emailTemplate:emailTemplates){
				result.getEmailTemplates().add(new EmailTemplateDto(emailTemplate));
			}
		}
		return result;
	}
	public void createOrUpdate(EmailTemplateDto emailTemplateDto,User currentUser) throws MeveoApiException,BusinessException{
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode(), currentUser.getProvider());
		if(emailTemplate!=null){
			update(emailTemplateDto,currentUser);
		}else{
			create(emailTemplateDto,currentUser);
		}
	}
}

