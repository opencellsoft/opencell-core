package org.meveo.api.communication;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.communication.EmailTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
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
		if(StringUtils.isBlank(emailTemplateDto.getCode())){
			missingParameters.add("code");
		}
		if(StringUtils.isBlank(emailTemplateDto.getSubject())){
			missingParameters.add("subject");
		}
		handleMissingParameters();
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode(), currentUser.getProvider());
		if(emailTemplate!=null){
			throw new EntityAlreadyExistsException(EmailTemplate.class, emailTemplateDto.getCode());
		}
		emailTemplate=new EmailTemplate();
		emailTemplate.setCode(emailTemplateDto.getCode());
		emailTemplate.setDescription(emailTemplateDto.getDescription());
		emailTemplate.setMedia(emailTemplateDto.getMedia());
		if(!StringUtils.isBlank(emailTemplateDto.getTagStartDelimiter())){
			emailTemplate.setTagStartDelimiter(emailTemplateDto.getTagStartDelimiter());
		}
		if(!StringUtils.isBlank(emailTemplateDto.getTagEndDelimiter())){
			emailTemplate.setTagEndDelimiter(emailTemplateDto.getTagEndDelimiter());
		}
		emailTemplate.setStartDate(emailTemplateDto.getStartDate());
		emailTemplate.setEndDate(emailTemplateDto.getEndDate());
		emailTemplate.setType(emailTemplateDto.getType());
		emailTemplate.setSubject(emailTemplateDto.getSubject());
		emailTemplate.setHtmlContent(emailTemplateDto.getHtmlContent());
		emailTemplate.setTextContent(emailTemplateDto.getTextContent());
		emailTemplateService.create(emailTemplate, currentUser);
	}
	public void update(EmailTemplateDto emailTemplateDto,User currentUser) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateDto.getCode())){
			missingParameters.add("code");
		}
		if(StringUtils.isBlank(emailTemplateDto.getSubject())){
			missingParameters.add("subject");
		}
		handleMissingParameters();
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode(), currentUser.getProvider());
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateDto.getCode());
		}
		emailTemplate.setDescription(emailTemplateDto.getDescription());
		if(!StringUtils.isBlank(emailTemplateDto.getMedia())){
			emailTemplate.setMedia(emailTemplateDto.getMedia());
		}
		if(!StringUtils.isBlank(emailTemplateDto.getTagStartDelimiter())){
			emailTemplate.setTagStartDelimiter(emailTemplateDto.getTagStartDelimiter());
		}
		if(!StringUtils.isBlank(emailTemplateDto.getTagEndDelimiter())){
			emailTemplate.setTagEndDelimiter(emailTemplateDto.getTagEndDelimiter());
		}
		emailTemplate.setStartDate(emailTemplateDto.getStartDate());
		emailTemplate.setEndDate(emailTemplateDto.getEndDate());
		emailTemplate.setType(emailTemplateDto.getType());
		emailTemplate.setSubject(emailTemplateDto.getSubject());
		emailTemplate.setHtmlContent(emailTemplateDto.getHtmlContent());
		emailTemplate.setTextContent(emailTemplateDto.getTextContent());
		emailTemplateService.update(emailTemplate, currentUser);
	}
	public EmailTemplateDto find(String emailTemplateCode,Provider currentProvider) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateCode)){
			missingParameters.add("emailTemplateCode");
			handleMissingParameters();
		}
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateCode, currentProvider);
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
		}
		return new EmailTemplateDto(emailTemplate);
	}
	public void remove(String emailTemplateCode, User currentUser) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateCode)){
			missingParameters.add("emailTemplateCode");
		}
		handleMissingParameters();
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateCode, currentUser.getProvider());
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
		}
		emailTemplateService.remove(emailTemplate, currentUser);
	}
	public List<EmailTemplateDto> list(Provider provider) throws MeveoApiException{
		List<EmailTemplateDto> result=new ArrayList<EmailTemplateDto>();
		List<EmailTemplate> emailTemplates=emailTemplateService.list(provider);
		if(emailTemplates!=null){
			for(EmailTemplate emailTemplate:emailTemplates){
				result.add(new EmailTemplateDto(emailTemplate));
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

