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

package org.meveo.api.rest.crm.impl;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.crm.ContactApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.crm.ContactsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.crm.ContactsResponseDto;
import org.meveo.api.dto.response.crm.GetContactResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.crm.ContactRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.PathParam;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Interceptors({ WsRestApiInterceptor.class })
public class ContactRsImpl extends BaseRs implements ContactRs {

	@Inject
	ContactApi contactApi;

	@Override
	public ActionStatus create(ContactDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			Contact contact = contactApi.create(postData);
			result.setEntityId(contact.getId());
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(contact.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(ContactDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			contactApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(ContactDto postData) {
		ActionStatus result = new ActionStatus();
		try {
			Contact contact = contactApi.createOrUpdate(postData);
			result.setEntityId(contact.getId());
			if (StringUtils.isBlank(postData.getCode())) {
				result.setEntityCode(contact.getCode());
			}
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus addTag(@PathParam("code") String code, @PathParam("tag") String tag) {
		ActionStatus result = new ActionStatus();
		try {
			contactApi.addTag(code, tag);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public ActionStatus removeTag(@PathParam("code") String code, @PathParam("tag") String tag) {
		ActionStatus result = new ActionStatus();
		try {
			contactApi.removeTag(code, tag);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}
	
	@Override
	public ActionStatus remove(@PathParam("code") String code) {
		ActionStatus result = new ActionStatus();

		try {
			contactApi.remove(code);
		} catch (Exception e) {
			processException(e, result);
		}
		return result;
	}

	@Override
	public GetContactResponseDto find(String code) {
		GetContactResponseDto result = new GetContactResponseDto();
		try {
			result.setContact(contactApi.findByCode(code));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}
		return result;
	}

	@Override
	public ContactsResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
		try {
			return contactApi.list(null, pagingAndFiltering);
		} catch (Exception e) {
			ContactsResponseDto result = new ContactsResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}

	@Override
	public ContactsResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy,
			SortOrder sortOrder, CustomFieldInheritanceEnum inheritCF) {
		try {
			return contactApi.list(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder),
					inheritCF);
		} catch (Exception e) {
			ContactsResponseDto result = new ContactsResponseDto();
			processException(e, result.getActionStatus());
			return result;
		}
	}

	@Override
	public ContactsResponseDto listGetAll() {

		ContactsResponseDto result = new ContactsResponseDto();

		try {
			result = contactApi.listGetAll(GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ContactsResponseDto importCSVText(String context) {
		ContactsResponseDto result = new ContactsResponseDto();
		try {
			ContactsDto contacts = contactApi.importCSVText(context);
			result.setContacts(contacts);
			if(result.getContacts() != null && !result.getContacts().getContact().isEmpty())
				result.setActionStatus(new ActionStatus(ActionStatusEnum.FAIL, "The following contacts have failed to persist"));
			return result;
			
		} catch (Exception e) {
			processException(e, result.getActionStatus());
			return result;
		}
	}

	@Override
	public ActionStatus importCSVFile() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
