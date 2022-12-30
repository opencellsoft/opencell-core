package org.meveo.apiv2.crm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContactCategoryApiServiceTest {

	private static final String CC_DESCRIPTION = "cc_description";

	private static final String CC_CODE = "cc_code";

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
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code(CC_CODE).description(CC_DESCRIPTION).customFields(new CustomFieldsDto()).build();
		ContactCategory entity = contactCategoryApiService.create(postData);
		assertNotNull(entity);
		assertEquals(CC_CODE, entity.getCode());
		assertEquals(CC_DESCRIPTION, entity.getDescription());
	}

	@Test(expected = EntityAlreadyExistsException.class)
	public void testCreateContactCategoryAlreadyExists() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(new ContactCategory());
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code(CC_CODE).description(CC_DESCRIPTION).customFields(new CustomFieldsDto()).build();
		contactCategoryApiService.create(postData);
		fail("A EntityAlreadyExistsException should be raised");
	}

	@Test
	public void testUpdateContactCategory() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(new ContactCategory());
		when(customFieldTemplateService.findByAppliesTo(any(ICustomFieldEntity.class))).thenReturn(null);
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code(CC_CODE).description(CC_DESCRIPTION).customFields(new CustomFieldsDto()).build();
		ContactCategory entity = contactCategoryApiService.update(CC_CODE, postData);
		
		assertNotNull(entity);
		assertEquals(CC_DESCRIPTION, entity.getDescription());
	}

	@Test(expected = EntityDoesNotExistsException.class)
	public void testUpdateContactCategoryDoesnotExists() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(null);
		
		ContactCategoryDto postData = ImmutableContactCategoryDto.builder().code(CC_CODE).description(CC_DESCRIPTION).customFields(new CustomFieldsDto()).build();
		contactCategoryApiService.update(CC_CODE, postData);
		
		fail("A EntityDoesNotExistsException should be raised");
	}

	@Test
	public void testDeleteContactCategory() {
		when(contactCategoryService.findByCode(anyString())).thenReturn(new ContactCategory());
		
		contactCategoryApiService.delete(CC_CODE);
		
		assertTrue("Successfully deleted", true);
	}
	
}
