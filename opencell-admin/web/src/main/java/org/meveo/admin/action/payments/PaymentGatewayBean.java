/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.payment.GatewayPaymentNamesEnum;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.payment.PaymentMethodApi;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.util.PaymentGatewayClass;

/**
 * Standard backing bean for {@link PaymentGateway} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentGatewayBean extends CustomFieldBean<PaymentGateway> {
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link PaymentGateway} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PaymentGatewayService paymentGatewayeService;

    @Inject
    private PaymentMethodApi paymentMethodApi;

    private String redirectionUrl;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentGatewayBean() {
        super(PaymentGateway.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * @return payment gateway.
     * 
     */
    @Override
    public PaymentGateway initEntity() {
        super.initEntity();
        if (entity.getId() == null) {
            entity.setType(PaymentGatewayTypeEnum.CUSTOM);
        }
        return entity;
    }

    @Override
    public void search() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.search();
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getBackViewSave();
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentGateway> getPersistenceService() {
        return paymentGatewayeService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public List<PaymentMethodEnum> getAllowedPaymentMethods() {
        return appProvider.getPaymentMethods();
    }

    /**
     * Autocomplete method for implementationClassName filter field - search classes with @PaymentGatewayClass annotation
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.service.payments");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for service payments  package", e);
            return null;
        }

        String queryLc = query.toLowerCase();
        List<String> classNames = new ArrayList<String>();
        for (Class clazz : classes) {
            if (clazz.getName().toLowerCase().contains(queryLc) && (clazz.isAnnotationPresent(PaymentGatewayClass.class))) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }

    public void getIngenicoUrl(
            String customerAccountCode,
            String returnUrl
    ) throws BusinessException {

        getIngenicoUrl(
                customerAccountCode,
                returnUrl,
                false,
                "INGENICO_GC",
                null,
                null,
                null,
                null,
                null
        );
    }
    public void getIngenicoUrl(
            String customerAccountCode,
            String returnUrl,
            Boolean skipAuthentication
    ) throws BusinessException {

        getIngenicoUrl(
                customerAccountCode,
                returnUrl,
                skipAuthentication,
                "INGENICO_GC",
                null,
                null,
                null,
                null,
                null
        );
    }
    /**
     * Example of use case of paymentMethodApi.createIframeUrl
     * @param customerAccountCode
     * @throws BusinessException
     */
    public void getIngenicoUrl(
            String customerAccountCode,
            String returnUrl,
            Boolean skipAuthentication,
            String gatewayPaymentName,
            String amount,
            String authorizationMode,
            String countryCode,
            String currencyCode,
            String locale
    ) throws BusinessException {


        if (StringUtils.isBlank(amount)) amount = "100";
        if (StringUtils.isBlank(authorizationMode)) authorizationMode = "FINAL_AUTHORIZATION";
        if (StringUtils.isBlank(countryCode)) countryCode = "US";
        if (StringUtils.isBlank(currencyCode)) currencyCode = "EUR";
        if (StringUtils.isBlank(locale)) locale = "en_GB";
        if (StringUtils.isBlank(skipAuthentication)) skipAuthentication = false;
        if (StringUtils.isBlank(gatewayPaymentName)) gatewayPaymentName = "INGENICO_GC";



        HostedCheckoutInput hostedCheckoutInput = new HostedCheckoutInput();
        hostedCheckoutInput.setCustomerAccountCode(customerAccountCode);
        hostedCheckoutInput.setReturnUrl(returnUrl);
        hostedCheckoutInput.setAmount(amount);
        hostedCheckoutInput.setAuthorizationMode(authorizationMode);
        hostedCheckoutInput.setCountryCode(countryCode);
        hostedCheckoutInput.setCurrencyCode(currencyCode);
        hostedCheckoutInput.setLocale(locale);
        hostedCheckoutInput.setSkipAuthentication(skipAuthentication);
        hostedCheckoutInput.setGatewayPaymentName(GatewayPaymentNamesEnum.valueOf(gatewayPaymentName));


        this.redirectionUrl = paymentMethodApi.getHostedCheckoutUrl(hostedCheckoutInput);

    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public void setRedirectionUrl(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
    }

}
