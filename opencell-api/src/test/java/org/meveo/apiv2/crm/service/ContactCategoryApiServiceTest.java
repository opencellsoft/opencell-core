package org.meveo.apiv2.crm.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.crm.ContactCategoryDto;
import org.meveo.apiv2.crm.ImmutableContactCategoryDto;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.communication.contact.ContactCategory;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.intcrm.impl.ContactCategoryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContactCategoryApiServiceTest {

	@InjectMocks
	private ContactCategoryApiService contactCategoryApiService;

	@Mock
	private ContactCategoryService contactCategoryService;

	@Mock
	private CustomFieldTemplateService customFieldTemplateService;

	@Test
	public void testCreateContactCategory() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(null);
		when(customFieldTemplateService.findByAppliesTo(any(ICustomFieldEntity.class))).thenReturn(null);
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code("cc_code").description("cc_description").customFields(new CustomFieldsDto()).build();
		contactCategoryApiService.create(postData);
	}

	@Test(expected = EntityAlreadyExistsException.class)
	public void testCreateContactCategoryAlreadyExists() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(new ContactCategory());
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code("cc_code").description("cc_description").customFields(new CustomFieldsDto()).build();
		contactCategoryApiService.create(postData);
	}

	@Test
	public void testUpdateContactCategory() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(new ContactCategory());
		when(customFieldTemplateService.findByAppliesTo(any(ICustomFieldEntity.class))).thenReturn(null);
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code("cc_code").description("cc_description").customFields(new CustomFieldsDto()).build();
		contactCategoryApiService.update("cc_code", postData);
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void testUpdateContactCategoryDoesnotExists() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(null);
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code("cc_code").description("cc_description").customFields(new CustomFieldsDto()).build();
		contactCategoryApiService.update("cc_code", postData);
	}

	@Test
	public void testDeleteContactCategory() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(new ContactCategory());
		
		Mockito.mock(EntityManager.class);
		when(contactCategoryService.getEntityManager()).thenReturn(Mockito.mock(EntityManager.class));
		
		contactCategoryApiService.delete("cc_code");
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void testDeleteContactCategoryDoesnotExists() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(null);
		
		contactCategoryApiService.delete("cc_code");
	}
	
}
