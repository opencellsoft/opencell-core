package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.account.CustomerApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.CustomerBrandDto;
import org.meveo.api.dto.account.CustomerCategoryDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.response.Paging;
import org.meveo.api.dto.response.account.CustomersResponseDto;
import org.meveo.api.dto.response.account.GetCustomerResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.account.CustomerRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomerRsImpl extends BaseRs implements CustomerRs {

    @Inject
    private CustomerApi customerApi;

    @Override
    public ActionStatus create(CustomerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CustomerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetCustomerResponseDto find(String customerCode) {
        GetCustomerResponseDto result = new GetCustomerResponseDto();

        try {
            result.setCustomer(customerApi.find(customerCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
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
    public CustomersResponseDto list(CustomerDto postData, Integer firstRow, Integer numberOfRows, Integer offset, Integer limit) {

        try {
            return customerApi.filterCustomer(postData, new Paging(offset != null ? offset : firstRow, limit != null ? limit : numberOfRows, null, null));
        } catch (Exception e) {
            CustomersResponseDto result = new CustomersResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
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
    public ActionStatus createOrUpdateCategory(CustomerCategoryDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.createOrUpdateCategory(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
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
            customerApi.createOrUpdate(postData);
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
    public ActionStatus createOrUpdateBrand(CustomerBrandDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customerApi.createOrUpdateBrand(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
