package org.meveo.apiv2.admin.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.provider.Provider;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.model.order.OrderLineTypeEnum;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentPlanPolicy;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.IsoIcdService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CreditCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningPauseReasonsService;

@Interceptors({ WsRestApiInterceptor.class })
public class ProviderResourceImpl implements ProviderResource {
    @Inject
    private ProviderService providerService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private CountryService countryService;

    @Inject
    private LanguageService languageService;

    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private BaseEntityService baseEntityService;

    @Inject
    private CreditCategoryService creditCategoryService;

    @Inject
    private DunningPauseReasonsService dunningPauseReasonsService;
    
    @Inject
    private IsoIcdService isoIcdService;

    @Override
    public Response updateProvider(String providerCode, Provider provider) {
        org.meveo.model.crm.Provider providerByCode = providerService.findByCode(providerCode);
        if (providerByCode == null) {
            throw new EntityDoesNotExistsException("provider with code " + providerCode + " does not exist.");
        }
        org.meveo.model.crm.Provider providerUpdateInfos = provider.toEntity();
        if (provider.getEmail() == null && providerByCode.getEmail() == null ) {
            throw new InvalidParameterException("provider's email is mandatory.");
        }
        if (provider.getDescription() != null) {
            providerByCode.setDescription(providerUpdateInfos.getDescription());
        }
        if (provider.getActive() != null) {
            providerByCode.setActive(providerUpdateInfos.isActive());
        }
        if (provider.getMultiLanguageFlag() != null) {
            providerByCode.setMultilanguageFlag(providerUpdateInfos.getMultilanguageFlag());
        }
        if (provider.getMultiCurrencyFlag() != null) {
            providerByCode.setMulticurrencyFlag(providerUpdateInfos.getMulticurrencyFlag());
        }
        if (provider.getMultiCountryFlag() != null) {
            providerByCode.setMulticountryFlag(providerUpdateInfos.getMulticountryFlag());
        }

        if (provider.getEnterprise() != null) {
            providerByCode.setEntreprise(providerUpdateInfos.isEntreprise());
        }

        if (provider.getLevelDuplication() != null) {
            providerByCode.setLevelDuplication(providerUpdateInfos.isLevelDuplication());
        }

        if (provider.getRecognizeRevenue() != null) {
            providerByCode.setRecognizeRevenue(providerUpdateInfos.isRecognizeRevenue());
        }

        if (provider.getAutomaticInvoicing() != null) {
            providerByCode.setRecognizeRevenue(provider.getAutomaticInvoicing());
        }

        if (provider.getAmountValidation() != null) {
            providerByCode.setAmountValidation(provider.getAmountValidation());
        }

        if (provider.getDisplayFreeTransacInInvoice() != null) {
            providerByCode.setDisplayFreeTransacInInvoice(provider.getDisplayFreeTransacInInvoice());
        }

        if (provider.getPaymentDeferral() != null) {
            providerByCode.setPaymentDeferral(provider.getPaymentDeferral());
        }

        if (provider.getPaymentPlan() != null) {
            providerByCode.setPaymentPlan(provider.getPaymentPlan());
        }

        if (provider.getRounding() != null) {
            providerByCode.setRounding(providerUpdateInfos.getRounding());
        }

        if (provider.getInvoiceRounding() != null) {
            providerByCode.setInvoiceRounding(providerUpdateInfos.getInvoiceRounding());
        }

        if (provider.getPrepaidReservationExpirationDelayinMillisec() != null) {
            providerByCode.setPrepaidReservationExpirationDelayinMillisec(providerUpdateInfos.getPrepaidReservationExpirationDelayinMillisec());
        }

        if (provider.getRoundingMode() != null) {
            providerByCode.setRoundingMode(providerUpdateInfos.getRoundingMode());
        }

        if (provider.getInvoiceRoundingMode() != null) {
            providerByCode.setRoundingMode(providerUpdateInfos.getRoundingMode());
        }

        if (provider.getDiscountAccountingCode() != null) {
            providerByCode.setDiscountAccountingCode(providerUpdateInfos.getDiscountAccountingCode());
        }

        if (provider.getEmail() != null) {
            providerByCode.setEmail(providerUpdateInfos.getEmail());
        }

        if (provider.getBankCoordinates() != null) {
            providerByCode.setBankCoordinates(providerUpdateInfos.getBankCoordinates());
        }

        if (Boolean.TRUE.equals(provider.getPaymentPlan()))
            checkPaymentPlanPolicy(provider, providerByCode, providerUpdateInfos);

        if (provider.getMaximumDelay() != null) {
            providerByCode.setMaximumDelay(providerUpdateInfos.getMaximumDelay());
        }

        if (provider.getMaximumDeferralPerInvoice() != null) {
            providerByCode.setMaximumDeferralPerInvoice(providerUpdateInfos.getMaximumDeferralPerInvoice());
        }

        if (provider.getCurrency() != null) {
            checkAndAddCurrency(providerByCode, providerUpdateInfos);
        }

        if (provider.getCountry() != null) {
            checkAndAddCountry(providerByCode, providerUpdateInfos);
        }

        if (provider.getLanguage() != null) {
            checkAndAddLanguage(providerByCode, providerUpdateInfos);
        }

        if (provider.getCustomer() != null) {
            checkAndCustomer(providerByCode, providerUpdateInfos);
        }

        if (provider.getCustomerAccount() != null) {
            checkAndCustomerAccount(providerByCode, providerUpdateInfos);
        }

        if (provider.getBillingAccount() != null) {
            checkAndBillingAccount(providerByCode, providerUpdateInfos);
        }

        if (provider.getUserAccount() != null) {
            checkAndUserAccount(providerByCode, providerUpdateInfos);
        }

        if (provider.getInvoiceConfiguration() != null) {
            checkAndAddInvoiceConfiguration(providerByCode, providerUpdateInfos);
        }

        if (provider.getPaymentMethods() != null) {
            providerByCode.setPaymentMethods(provider.getPaymentMethods().stream().filter(StringUtils::isNotBlank).map(PaymentMethodEnum::valueOf).collect(Collectors.toList()));
        }

        if (CollectionUtils.isNotEmpty(provider.getAllowedManualRefundMethods())) {
            providerByCode.setAllowedManualRefundMethods(provider.getAllowedManualRefundMethods()
                    .stream()
                    .filter(StringUtils::isNotBlank)
                    .map(PaymentMethodEnum::valueOf)
                    .collect(Collectors.toList()));
        } else {
            providerByCode.setAllowedManualRefundMethods(Collections.emptyList());
        }
        
        if (provider.getPortalMessage() != null) {
        	if (provider.getPortalMessage().length() > 500) {
                throw new InvalidParameterException("Max size is 500 characters.");
            } 
            providerByCode.setPortalMessage(providerUpdateInfos.getPortalMessage());
        }

        if (provider.getCurrentMatchingCode() != null) {
            providerByCode.setCurrentMatchingCode(providerUpdateInfos.getCurrentMatchingCode());
        }
        
        if (provider.getOrderLineTypes() != null) {
            providerByCode.setOrderLineTypes(provider.getOrderLineTypes().stream().filter(StringUtils::isNotBlank).map(OrderLineTypeEnum::valueOf).collect(Collectors.toList()));
        }
        
        if (provider.getIsoICDCode() != null) {
            IsoIcd isoIcd = isoIcdService.findByCode(provider.getIsoICDCode());
            if (isoIcd == null) {
                throw new EntityDoesNotExistsException(IsoIcd.class, provider.getIsoICDCode());
            }
            providerByCode.setIcdId(isoIcd);
        }
        
        providerService.update(providerByCode);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
    }

    private void checkAndAddInvoiceConfiguration(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        InvoiceConfiguration invoiceConfiguration = (InvoiceConfiguration) baseEntityService.tryToFindByEntityClassAndId(InvoiceConfiguration.class,
                providerUpdateInfos.getInvoiceConfiguration().getId());
        if (invoiceConfiguration == null) {
            throw new EntityDoesNotExistsException(InvoiceConfiguration.class, providerUpdateInfos.getInvoiceConfiguration().getId());
        }
        providerByCode.setInvoiceConfiguration(invoiceConfiguration);
    }

    private void checkAndUserAccount(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        UserAccount userAccount = userAccountService.findById(providerUpdateInfos.getUserAccount().getId());
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, providerUpdateInfos.getUserAccount().getId());
        }
        providerByCode.setUserAccount(userAccount);
    }

    private void checkAndBillingAccount(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        BillingAccount billingAccount = billingAccountService.findById(providerUpdateInfos.getBillingAccount().getId());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, providerUpdateInfos.getBillingAccount().getId());
        }
        providerByCode.setBillingAccount(billingAccount);
    }

    private void checkAndCustomerAccount(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        CustomerAccount customerAccount = customerAccountService.findById(providerUpdateInfos.getCustomerAccount().getId());
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, providerUpdateInfos.getCustomerAccount().getId());
        }
        providerByCode.setCustomerAccount(customerAccount);
    }

    private void checkAndCustomer(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        Customer customer = customerService.findById(providerUpdateInfos.getCustomer().getId());
        if (customer == null) {
            throw new EntityDoesNotExistsException(Customer.class, providerUpdateInfos.getCustomer().getId());
        }
        providerByCode.setCustomer(customer);
    }

    private void checkAndAddLanguage(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        Language language = languageService.findById(providerUpdateInfos.getLanguage().getId());
        if (language == null) {
            throw new EntityDoesNotExistsException(Language.class, providerUpdateInfos.getCountry().getId());
        }
        providerByCode.setLanguage(language);
    }

    private void checkAndAddCountry(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        Country country = countryService.findById(providerUpdateInfos.getCountry().getId());
        if (country == null) {
            throw new EntityDoesNotExistsException(Country.class, providerUpdateInfos.getCountry().getId());
        }
        providerByCode.setCountry(country);
    }

    private void checkAndAddCurrency(org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        Currency currency = currencyService.findById(providerUpdateInfos.getCurrency().getId());
        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, providerUpdateInfos.getCurrency().getId());
        }
        providerByCode.setCurrency(currency);
    }

    private void checkPaymentPlanPolicy(Provider provider, org.meveo.model.crm.Provider providerByCode, org.meveo.model.crm.Provider providerUpdateInfos) {
        PaymentPlanPolicy paymentPlanPolicy = provider.getPaymentPlanPolicy();
        if (paymentPlanPolicy != null) {
            List<CreditCategory> listAllowedCreditCategories = new ArrayList<>();
            if (paymentPlanPolicy.getAllowedCreditCategories() != null) {
                updateCreditCategories(providerByCode);
            }
            for (CreditCategory elementAllowedCreditCategories : providerUpdateInfos.getPaymentPlanPolicy().getAllowedCreditCategories()) {
                CreditCategory creditCategory = creditCategoryService.findById(elementAllowedCreditCategories.getId());
                if (creditCategory == null) {
                    throw new EntityDoesNotExistsException(CreditCategory.class, elementAllowedCreditCategories.getId());
                }
                creditCategory.setProvider(providerByCode);
                listAllowedCreditCategories.add(creditCategory);
            }
            providerUpdateInfos.getPaymentPlanPolicy().setAllowedCreditCategories(listAllowedCreditCategories);
            if (paymentPlanPolicy.getDunningDefaultPauseReason() != null && paymentPlanPolicy.getDunningDefaultPauseReason().getId() != null) {
                DunningPauseReason dunningPauseReason = dunningPauseReasonsService.findById(paymentPlanPolicy.getDunningDefaultPauseReason().getId());
                if (dunningPauseReason == null) {
                    throw new EntityDoesNotExistsException(DunningPauseReason.class, paymentPlanPolicy.getDunningDefaultPauseReason().getId());
                }
                providerUpdateInfos.getPaymentPlanPolicy().setDunningDefaultPauseReason(dunningPauseReason);
            }
            providerByCode.setPaymentPlanPolicy(providerUpdateInfos.getPaymentPlanPolicy());
        }
    }

    private void updateCreditCategories(org.meveo.model.crm.Provider providerByCode) {
        for (CreditCategory elementCreditCategorie : creditCategoryService.list()) {
            if (elementCreditCategorie.getProvider() != null && Objects.equals(elementCreditCategorie.getProvider().getId(), providerByCode.getId())) {
                elementCreditCategorie.setProvider(null);
                creditCategoryService.update(elementCreditCategorie);
            }
        }
    }
}

