package org.meveo.apiv2.admin.providers;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.provider.Provider;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.*;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;
import org.meveo.service.payments.impl.PaymentService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProviderResourceImpl implements ProviderResource{
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

    @Override
    public Response updateDunningTemplate(String providerCode, Provider provider) {
        org.meveo.model.crm.Provider providerByCode = providerService.findByCode(providerCode);
        if(providerByCode == null) {
            throw new EntityDoesNotExistsException("provider with code "+providerCode+" does not exist.");
        }
        org.meveo.model.crm.Provider providerUpdateInfos = provider.toEntity();
        if(provider.getDescription() != null) {
            providerByCode.setDescription(providerUpdateInfos.getDescription());
        }
        if(provider.getActive() != null) {
            providerByCode.setActive(providerUpdateInfos.isActive());
        }
        if(provider.getMultiLanguageFlag() != null) {
            providerByCode.setMultilanguageFlag(providerUpdateInfos.getMultilanguageFlag());
        }
        if(provider.getMultiCurrencyFlag() != null) {
            providerByCode.setMulticurrencyFlag(providerUpdateInfos.getMulticurrencyFlag());
        }
        if(provider.getMultiCountryFlag() != null) {
            providerByCode.setMulticountryFlag(providerUpdateInfos.getMulticountryFlag());
        }

        if(provider.getEnterprise() != null) {
            providerByCode.setEntreprise(providerUpdateInfos.isEntreprise());
        }

        if(provider.getLevelDuplication() != null) {
            providerByCode.setLevelDuplication(providerUpdateInfos.isLevelDuplication());
        }

        if(provider.getRecognizeRevenue() != null) {
            providerByCode.setRecognizeRevenue(providerUpdateInfos.isRecognizeRevenue());
        }

        if(provider.getAutomaticInvoicing() != null) {
            providerByCode.setRecognizeRevenue(provider.getAutomaticInvoicing());
        }

        if(provider.getAmountValidation() != null) {
            providerByCode.setAmountValidation(provider.getAmountValidation());
        }

        if(provider.getDisplayFreeTransacInInvoice() != null) {
            providerByCode.setDisplayFreeTransacInInvoice(provider.getDisplayFreeTransacInInvoice());
        }

        if(provider.getPaymentDeferral() != null) {
            providerByCode.setPaymentDeferral(provider.getPaymentDeferral());
        }

        if(provider.getPaymentPlan() != null) {
            providerByCode.setPaymentPlan(provider.getPaymentPlan());
        }

        if(provider.getRounding() != null) {
            providerByCode.setRounding(providerUpdateInfos.getRounding());
        }

        if(provider.getInvoiceRounding() != null) {
            providerByCode.setInvoiceRounding(providerUpdateInfos.getInvoiceRounding());
        }

        if(provider.getPrepaidReservationExpirationDelayinMillisec() != null) {
            providerByCode.setPrepaidReservationExpirationDelayinMillisec(providerUpdateInfos.getPrepaidReservationExpirationDelayinMillisec());
        }

        if(provider.getRoundingMode() != null) {
            providerByCode.setRoundingMode(providerUpdateInfos.getRoundingMode());
        }

        if(provider.getInvoiceRoundingMode() != null) {
            providerByCode.setRoundingMode(providerUpdateInfos.getRoundingMode());
        }

        if(provider.getDiscountAccountingCode() != null) {
            providerByCode.setDiscountAccountingCode(providerUpdateInfos.getDiscountAccountingCode());
        }

        if(provider.getEmail() != null) {
            providerByCode.setEmail(providerUpdateInfos.getEmail());
        }

        if(provider.getBankCoordinates() != null) {
            providerByCode.setBankCoordinates(providerUpdateInfos.getBankCoordinates());
        }

        if(provider.getMaximumDelay() != null) {
            providerByCode.setMaximumDelay(providerUpdateInfos.getMaximumDelay());
        }
        
        if(provider.getMaximumDeferralPerInvoice() != null) {
            providerByCode.setMaximumDeferralPerInvoice(providerUpdateInfos.getMaximumDeferralPerInvoice());
        }
        
        if(provider.getCurrency() != null) {
            Currency currency = currencyService.findById(providerUpdateInfos.getCurrency().getId());
            if(currency == null) {
                throw new EntityDoesNotExistsException("currency with id "+providerUpdateInfos.getCurrency().getId()+" does not exist.");
            }
            providerByCode.setCurrency(currency);
        }

        if(provider.getCountry() != null) {
            Country country = countryService.findById(providerUpdateInfos.getCountry().getId());
            if(country == null) {
                throw new EntityDoesNotExistsException("country with id "+providerUpdateInfos.getCountry().getId()+" does not exist.");
            }
            providerByCode.setCountry(country);
        }

        if(provider.getLanguage() != null) {
            Language language = languageService.findById(providerUpdateInfos.getLanguage().getId());
            if(language == null) {
                throw new EntityDoesNotExistsException("language with id "+providerUpdateInfos.getCountry().getId()+" does not exist.");
            }
            providerByCode.setLanguage(language);
        }

        if(provider.getCustomer() != null) {
            Customer customer = customerService.findById(providerUpdateInfos.getCustomer().getId());
            if(customer == null) {
                throw new EntityDoesNotExistsException("customer with id "+providerUpdateInfos.getCustomer().getId()+" does not exist.");
            }
            providerByCode.setCustomer(customer);
        }

        if(provider.getCustomerAccount() != null) {
            CustomerAccount customerAccount = customerAccountService.findById(providerUpdateInfos.getCustomerAccount().getId());
            if(customerAccount == null) {
                throw new EntityDoesNotExistsException("CustomerAccount with id "+providerUpdateInfos.getCustomerAccount().getId()+" does not exist.");
            }
            providerByCode.setCustomerAccount(customerAccount);
        }

        if(provider.getBillingAccount() != null) {
            BillingAccount billingAccount = billingAccountService.findById(providerUpdateInfos.getBillingAccount().getId());
            if(billingAccount == null) {
                throw new EntityDoesNotExistsException("billingAccount with id "+providerUpdateInfos.getBillingAccount().getId()+" does not exist.");
            }
            providerByCode.setBillingAccount(billingAccount);
        }

        if(provider.getUserAccount() != null) {
            UserAccount userAccount = userAccountService.findById(providerUpdateInfos.getUserAccount().getId());
            if(userAccount == null) {
                throw new EntityDoesNotExistsException("user Account with id "+providerUpdateInfos.getUserAccount().getId()+" does not exist.");
            }
            providerByCode.setUserAccount(userAccount);
        }

        if(provider.getInvoiceConfiguration() != null) {
            InvoiceConfiguration invoiceConfiguration = (InvoiceConfiguration) baseEntityService.tryToFindByEntityClassAndId(InvoiceConfiguration.class, providerUpdateInfos.getInvoiceConfiguration().getId());
            if(invoiceConfiguration == null) {
                throw new EntityDoesNotExistsException("invoice Configuration with id "+providerUpdateInfos.getInvoiceConfiguration().getId()+" does not exist.");
            }
            providerByCode.setInvoiceConfiguration(invoiceConfiguration);
        }

        if(provider.getPaymentMethods() != null) {
            providerByCode.setPaymentMethods(provider.getPaymentMethods().stream()
                            .filter(Predicate.not(String::isBlank))
                            .map(paymentEnum -> {
                                PaymentMethodEnum paymentMethodEnum = PaymentMethodEnum.valueOf(paymentEnum);
                                if(paymentMethodEnum == null){
                                    throw new BusinessApiException("paymentMethodEnum "+providerUpdateInfos.getPaymentMethods()+" does not exist.");
                                }
                                return paymentMethodEnum;
                            })
                    .collect(Collectors.toList()));
        }
        providerService.update(providerByCode);
        return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\"}}").build();
    }
}
