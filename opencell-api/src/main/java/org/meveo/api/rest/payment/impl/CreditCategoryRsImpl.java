package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CreditCategoryDto;
import org.meveo.api.dto.response.payment.CreditCategoriesResponseDto;
import org.meveo.api.dto.response.payment.CreditCategoryResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.CreditCategoryApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.CreditCategoryRs;

/**
 * @author Edward P. Legaspi
 * @since 22 Aug 2017
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CreditCategoryRsImpl extends BaseRs implements CreditCategoryRs {

	@Inject
	private CreditCategoryApi creditCategoryApi;

	@Override
	public ActionStatus create(CreditCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			creditCategoryApi.create(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus update(CreditCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			creditCategoryApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}
	
	@Override
	public ActionStatus createOrUpdate(CreditCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			creditCategoryApi.createOrUpdate(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public CreditCategoryResponseDto find(String creditCategoryCode) {
		CreditCategoryResponseDto result = new CreditCategoryResponseDto();

		try {
			result.setCreditCategory(creditCategoryApi.find(creditCategoryCode));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public CreditCategoriesResponseDto list() {
		CreditCategoriesResponseDto result = new CreditCategoriesResponseDto();

		try {
			result.setCreditCategories(creditCategoryApi.list());
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ActionStatus remove(String creditCategoryCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			creditCategoryApi.remove(creditCategoryCode);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

}
