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

package org.meveo.api.rest.account.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.account.CustomerApi;
import org.meveo.api.account.CustomerSequenceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.billing.CounterInstanceDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.response.account.GetCustomerCategoryResponseDto;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.dto.sequence.CustomerSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.CustomerRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomerRsImpl extends BaseRs implements CustomerRs {

    @Inject
    private CustomerApi customerApi;

    @Inject
    private CustomerSequenceApi customerSequenceApi;

    @Override
    public ActionStatus create(CustomerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Customer customer = customerApi.create(postData);
            result.setEntityCode(customer.getCode());
            result.setEntityId(customer.getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CustomerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setEntityId(customerApi.update(postData).getId());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomerResponseDto find(String customerCode, CustomFieldInheritanceEnum inheritCF, boolean includeCustomerAccounts) {
        GetCustomerResponseDto result = new GetCustomerResponseDto();

        try {
            result.setCustomer(customerApi.find(customerCode, inheritCF, includeCustomerAccounts));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCustomerResponseDto findV2(String customerCode, CustomFieldInheritanceEnum inheritCF, boolean includeCustomerAccounts) {
        return find(customerCode, inheritCF, includeCustomerAccounts);
    }

    @Override
    public ActionStatus remove(String customerCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.remove(customerCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomersResponseDto list47(CustomerDto postData, Integer firstRow, Integer numberOfRows, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        try {
            return customerApi.list(postData, new PagingAndFiltering(null, null, offset != null ? offset : firstRow, limit != null ? limit : numberOfRows, sortBy, sortOrder));
        } catch (Exception e) {
            CustomersResponseDto result = new CustomersResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public CustomersResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder, CustomFieldInheritanceEnum inheritCF) {
        try {
            return customerApi.list(null, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder), inheritCF);
        } catch (Exception e) {
            CustomersResponseDto result = new CustomersResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public CustomersResponseDto list() {
        try {
            return customerApi.listGetAll(null, GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering());
        } catch (Exception e) {
            CustomersResponseDto result = new CustomersResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public CustomersResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        try {
            return customerApi.list(null, pagingAndFiltering);
        } catch (Exception e) {
            CustomersResponseDto result = new CustomersResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public CustomersResponseDto listPostV2(PagingAndFiltering pagingAndFiltering) {
        return listPost(pagingAndFiltering);
    }

    @Override
    public ActionStatus createBrand(CustomerBrandDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customerApi.createBrand(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createBrandV2(CustomerBrandDto postData) {
        return createBrand(postData);
    }

    @Override
    public ActionStatus createCategory(CustomerCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customerApi.createCategory(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createCategoryV2(CustomerCategoryDto postData) {
        return createCategory(postData);
    }

    @Override
    public ActionStatus updateCategory(CustomerCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.updateCategory(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCategoryV2(CustomerCategoryDto postData) {
        return updateCategory(postData);
    }

    @Override
    public ActionStatus createOrUpdateCategory(CustomerCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.createOrUpdateCategory(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    /**
     * Find customer category by customer category code
     * 
     * @param categoryCode customer category code
     * @return GetCustomerCategoryResponseDto
     * 
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    @Override
    public GetCustomerCategoryResponseDto findCategory(String categoryCode) {
        GetCustomerCategoryResponseDto result = new GetCustomerCategoryResponseDto();

        try {
            result.setCustomerCategory(customerApi.findCategory(categoryCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCustomerCategoryResponseDto findCategoryV2(String categoryCode) {
        return findCategory(categoryCode);
    }

    @Override
    public ActionStatus removeBrandV2(String brandCode) {
        return removeBrand(brandCode);
    }

    @Override
    public ActionStatus removeBrand(String brandCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.removeBrand(brandCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeCategoryV2(String categoryCode) {
        return removeCategory(categoryCode);
    }

    @Override
    public ActionStatus removeCategory(String categoryCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.removeCategory(categoryCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(CustomerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Customer customer = customerApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(customer.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBrand(CustomerBrandDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.updateBrand(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBrandV2(CustomerBrandDto postData) {
        return updateBrand(postData);
    }

    @Override
    public ActionStatus createOrUpdateBrand(CustomerBrandDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.createOrUpdateBrand(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus exportCustomerHierarchy(String customerCode) {
        ActionStatus result = new ActionStatus();

        try {
            customerApi.exportCustomerHierarchy(customerCode, httpServletResponse);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus exportCustomerHierarchyV2(String customerCode) {
        return exportCustomerHierarchy(customerCode);
    }

    @Override
    public ActionStatus anonymizeGdpr(String customerCode) {
        ActionStatus result = new ActionStatus();

        try {
            customerApi.anonymizeGdpr(customerCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus anonymizeGdprV2(String customerCode) {
        return anonymizeGdpr(customerCode);
    }

    @Override
    public ActionStatus updateCustomerNumberSequence(GenericSequenceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            customerApi.updateCustomerNumberSequence(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GenericSequenceValueResponseDto getNextCustomerNumber() {
        GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

        try {
            result = customerApi.getNextCustomerNumber();
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createCustomerSequence(CustomerSequenceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            customerSequenceApi.createCustomerSequence(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateCustomerSequence(CustomerSequenceDto postData) {
        ActionStatus result = new ActionStatus();

        try {
            customerSequenceApi.updateCustomerSequence(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GenericSequenceValueResponseDto getNextCustomerSequenceNumber(String code) {
        GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

        try {
            result = customerSequenceApi.getNextNumber(code);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCountersInstancesResponseDto filterCustomerCountersByPeriod(String customerCode, Date date) {
        GetCountersInstancesResponseDto result = new GetCountersInstancesResponseDto();

        try {
            List<CounterInstance> counters = customerApi.filterCountersByPeriod(customerCode, date);
            for (CounterInstance ci : counters) {
                result.getCountersInstances().getCounterInstance().add(new CounterInstanceDto(ci));
            }

            result.getCountersInstances().getCounterInstance().sort(Comparator.comparing(CounterInstanceDto::getCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetCountersInstancesResponseDto filterCustomerCountersByPeriodV2(String customerCode, Date date) {
        return filterCustomerCountersByPeriod(customerCode, date);
    }

    public ActionStatus mq1(Integer threads, Integer count, Integer batchSize, String isTempQueue, String msgType) {

        return customerApi.mq1(threads, count, batchSize, isTempQueue, msgType);
    }

    public ActionStatus mq3(Integer threads, Integer count, Integer batchSize, String isTempQueue, String msgType) {

        return customerApi.mq3(threads, count, batchSize, isTempQueue, msgType);
    }

    public ActionStatus mq5SingleTx(Integer threads, Integer count, Integer batchSize, String isTempQueue, String msgType) {

        return customerApi.mq5SingleTx(threads, count, batchSize, isTempQueue, msgType);
    }

}
