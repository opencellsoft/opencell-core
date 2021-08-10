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

package org.meveo.apiv2.ordering.resource.mappers;

import static java.lang.Boolean.FALSE;
import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.apiv2.billing.*;
import org.meveo.apiv2.billing.impl.InvoiceMapper;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.billing.Invoice;

public class InvoiceMapperTest {

    private Random random = new Random();

	private InvoiceMapper invoiceMapper = new InvoiceMapper();
 
	public <T> T instantiateRandomObject(Class<T> clazz, boolean onlyBasicFields) throws Exception {
		T instance = null;
		if(Resource.class.isAssignableFrom(clazz)) {
			Method builderMethod = clazz.getMethod("builder");
			Object builder = builderMethod.invoke(null);
			final Class builderClass = builder.getClass();
			final Method build = builderClass.getMethod("build");
			
			for (Field field : clazz.getDeclaredFields()) {
				final String name = field.getName();
				if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && !Collection.class.isAssignableFrom(field.getType())) {
					
					Method accessor = builderClass.getMethod(name, field.getType());
					Object value = getRandomValueForField(field, onlyBasicFields);
					accessor.invoke(builder, value);
				}
			}
			instance =  (T)build.invoke(builder);
		} else {
			final Constructor<T> constructor = clazz.getConstructor();
			if(constructor!=null) {
				Object[] cargs = new Object[constructor.getParameterCount()];
				instance = constructor.newInstance(cargs);
				for (Field field : clazz.getDeclaredFields()) {
					if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
						field.setAccessible(true);
						Object value = getRandomValueForField(field, onlyBasicFields);
						field.set(instance, value);
					}
				}
			} else {
				System.out.println("WARNING: NO CONSTRUCTOR FOR "+clazz);
			}
		}
		return instance;
	}
 
	private Object getRandomValueForField(Field field, boolean onlyBasicFields) throws Exception {
		Class<?> type = field.getType();
		if (type.isEnum()) {
			Object[] enumValues = type.getEnumConstants();
			return enumValues[random.nextInt(enumValues.length)];
		} else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			return random.nextInt();
		} else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			return random.nextLong();
		} else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			return random.nextDouble();
		} else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			return random.nextFloat();
		} else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			return random.nextBoolean();
		} else if (type.equals(String.class)) {
			return UUID.randomUUID().toString();
		} else if (type.equals(BigInteger.class)) {
			return BigInteger.valueOf(random.nextInt());
		} else if (type.equals(BigDecimal.class)) {
			return BigDecimal.valueOf(random.nextInt());
		} else if (type.equals(Date.class)) {
			return new Date();
		} else if(!onlyBasicFields) {
			return instantiateRandomObject(type, onlyBasicFields);
		}
		return null;
	}
	
	
    @Test
	public void test_immutable_to_entity_init_mapping() throws Exception {
		ImmutableInvoice resource = instantiateRandomObject(ImmutableInvoice.class, true);
		Invoice entity = invoiceMapper.toEntity(resource);
		assertInvoiceAndResourceAreEquals(resource, entity);
		
	}

    @Test
	public void test_entity_to_immutable_init_mapping() throws Exception {
		Invoice entity = instantiateRandomObject(Invoice.class, true);
		org.meveo.apiv2.billing.Invoice resource = invoiceMapper.toResource(entity);
		
		assertInvoiceAndResourceAreEquals(resource, entity);
		
	}
    
	private void assertInvoiceAndResourceAreEquals(org.meveo.apiv2.billing.Invoice resource, Invoice entity) {
		assertThat(resource.getAlias()).isEqualTo(entity.getAlias());
		assertThat(resource.getAmount()).isEqualTo(entity.getAmount());
    	assertThat(resource.getAmountTax()).isEqualTo(entity.getAmountTax());
    	assertThat(resource.getAmountWithoutTax()).isEqualTo(entity.getAmountWithoutTax());
    	assertThat(resource.getAmountWithTax()).isEqualTo(entity.getAmountWithTax());
    	assertThat(resource.getComment()).isEqualTo(entity.getComment());
    	assertThat(resource.getDiscount()).isEqualTo(entity.getDiscount());
    	assertThat(resource.getAlias()).isEqualTo(entity.getDescription());
    	assertThat(resource.getDiscountRate()).isEqualTo(entity.getDiscountRate());
    	assertThat(resource.getDiscountAmount()).isEqualTo(entity.getDiscountAmount());
    	assertThat(resource.getDraft()).isEqualTo(entity.getDraft());
    	assertThat(resource.getDueBalance()).isEqualTo(entity.getDueBalance());
    	assertThat(resource.getDueDate()).isEqualTo(entity.getDueDate());
    	assertThat(resource.getEmailSentDate()).isEqualTo(entity.getEmailSentDate());
    	assertThat(resource.getEndDate()).isEqualTo(entity.getEndDate());
    	assertThat(resource.getExternalRef()).isEqualTo(entity.getExternalRef());
    	assertThat(resource.getIban()).isEqualTo(entity.getIban());
    	assertThat(resource.getId()).isEqualTo(entity.getId());
    	assertThat(resource.getInitialCollectionDate()).isEqualTo(entity.getInitialCollectionDate());
    	assertThat(resource.getInvoiceAdjustmentCurrentProviderNb()).isEqualTo(entity.getInvoiceAdjustmentCurrentProviderNb());
    	assertThat(resource.getInvoiceAdjustmentCurrentSellerNb()).isEqualTo(entity.getInvoiceAdjustmentCurrentSellerNb());
    	assertThat(resource.getInvoiceNumber()).isEqualTo(entity.getInvoiceNumber());
    	assertThat(resource.getInvoiceDate()).isEqualTo(entity.getInvoiceDate());
    	assertThat(resource.getNetToPay()).isEqualTo(entity.getNetToPay());
    	assertThat(resource.getPaymentMethodType()).isEqualTo(entity.getPaymentMethodType());
    	assertThat(resource.getPaymentStatus()).isEqualTo(entity.getPaymentStatus());
    	assertThat(resource.getPaymentStatusDate()).isEqualTo(entity.getPaymentStatusDate());
    	assertThat(resource.getPdfDate()).isEqualTo(entity.getPdfDate());
    	assertThat(resource.getPdfFilename()).isEqualTo(entity.getPdfFilename());
    	assertThat(resource.getPreviousInvoiceNumber()).isEqualTo(entity.getPreviousInvoiceNumber());
    	assertThat(resource.getProductDate()).isEqualTo(entity.getProductDate());
    	assertThat(resource.getRejectReason()).isEqualTo(entity.getRejectReason());
		assertThat(resource.getRawAmount()).isEqualTo(entity.getRawAmount());
		assertThat(resource.getStatusDate()).isEqualTo(entity.getStatusDate());
		assertThat(resource.getStatus()).isEqualTo(entity.getStatus());
		assertThat(resource.getStartDate()).isEqualTo(entity.getStartDate());
		assertThat(resource.getXmlDate()).isEqualTo(entity.getXmlDate());
		assertThat(resource.getXmlFilename()).isEqualTo(entity.getXmlFilename());
	}

	@Test
	public void shouldReturnGenerateInvoiceRequestDto() {
		GenerateInvoiceInput invoiceInput = ImmutableGenerateInvoiceInput.builder()
				.billingAccountCode("code")
				.invoicingDate(new Date())
				.targetType("BILLINGACCOUNT")
				.isDraft(FALSE)
				.build();

		GenerateInvoiceRequestDto generateInvoiceRequest = invoiceMapper.toGenerateInvoiceRequestDto(invoiceInput);

		assertEquals("code", generateInvoiceRequest.getBillingAccountCode());
		assertEquals("BILLINGACCOUNT", generateInvoiceRequest.getTargetType());
		assertEquals(FALSE, generateInvoiceRequest.getGeneratePDF());
		assertEquals(FALSE, generateInvoiceRequest.getGenerateXML());
		assertEquals(FALSE, generateInvoiceRequest.getGenerateAO());
	}

	@Test
	public void shouldCreateGenerateInvoiceResult() {
		Invoice invoice = new Invoice();
		invoice.setId(1L);
		invoice.setAmountTax(new BigDecimal(10l));
		invoice.setAmount(new BigDecimal(20l));
		invoice.setAmountWithoutTax(new BigDecimal(10l));
		invoice.setAmountWithTax(new BigDecimal(20l));
		invoice.setInvoiceNumber("INV_122222");
		invoice.setTemporaryInvoiceNumber("TMP_122222");
		invoice.setDueDate(new Date());
		invoice.setNetToPay(new BigDecimal(20l));
		invoice.setStatus(VALIDATED);
		invoice.setInvoiceDate(new Date());

		GenerateInvoiceResult result = invoiceMapper.toGenerateInvoiceResult(invoice, "COM", 1L);

		assertEquals("COM", result.getInvoiceTypeCode());
		assertEquals("INV_122222", result.getInvoiceNumber());
		assertEquals(VALIDATED, result.getStatus());
		assertEquals(new BigDecimal(20l), result.getAmount());
	}
}