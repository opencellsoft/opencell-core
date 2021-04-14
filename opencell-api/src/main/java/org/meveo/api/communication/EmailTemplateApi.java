/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.service.communication.impl.EmailTemplateService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 5:09:27 AM
 *
 */
@Stateless
public class EmailTemplateApi extends BaseApi {

	@Inject
	private EmailTemplateService emailTemplateService;
	public void create(EmailTemplateDto emailTemplateDto) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateDto.getCode())){
			addGenericCodeIfAssociated(EmailTemplate.class.getName(), emailTemplateDto);
		}
		if(StringUtils.isBlank(emailTemplateDto.getSubject())){
			missingParameters.add("subject");
		}
		handleMissingParameters();
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode());
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
		emailTemplateService.create(emailTemplate);
	}
	public void update(EmailTemplateDto emailTemplateDto) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateDto.getCode())){
			missingParameters.add("code");
		}
		if(StringUtils.isBlank(emailTemplateDto.getSubject())){
			missingParameters.add("subject");
		}
		handleMissingParameters();
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateDto.getCode());
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateDto.getCode());
		}
		emailTemplate.setCode(StringUtils.isBlank(emailTemplateDto.getUpdatedCode()) ? emailTemplateDto.getCode() : emailTemplateDto.getUpdatedCode());
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
		emailTemplateService.update(emailTemplate);
	}
	public EmailTemplateDto find(String emailTemplateCode) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateCode)){
			missingParameters.add("emailTemplateCode");
			handleMissingParameters();
		}
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateCode);
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
		}
		return new EmailTemplateDto(emailTemplate);
	}
	public void remove(String emailTemplateCode) throws MeveoApiException,BusinessException{
		if(StringUtils.isBlank(emailTemplateCode)){
			missingParameters.add("emailTemplateCode");
		}
		handleMissingParameters();
		EmailTemplate emailTemplate=emailTemplateService.findByCode(emailTemplateCode);
		if(emailTemplate==null){
			throw new EntityDoesNotExistsException(EmailTemplate.class, emailTemplateCode);
		}
		emailTemplateService.remove(emailTemplate);
	}
	public List<EmailTemplateDto> list() throws MeveoApiException{
		List<EmailTemplateDto> result=new ArrayList<EmailTemplateDto>();
		List<EmailTemplate> emailTemplates=emailTemplateService.list();
		if(emailTemplates!=null){
			for(EmailTemplate emailTemplate:emailTemplates){
				result.add(new EmailTemplateDto(emailTemplate));
			}
		}
		return result;
	}
	public void createOrUpdate(EmailTemplateDto emailTemplateDto) throws MeveoApiException,BusinessException{
		if(!StringUtils.isBlank(emailTemplateDto.getCode())
				&& emailTemplateService.findByCode(emailTemplateDto.getCode()) != null) {
			update(emailTemplateDto);
		} else {
			create(emailTemplateDto);
		}
	}
}

