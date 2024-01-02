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

package org.meveo.api.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.GDPRInfoDto;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.billing.DiscountPlanInstanceDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.account.BillingAccountsResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.invoice.InvoiceApi;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecureMethodParameter;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.api.security.parameter.ObjectPropertyParser;
import org.meveo.commons.utils.BeanUtils;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.shared.Title;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.IsoIcdService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.crm.impl.ProviderContactService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;
import org.meveo.service.tax.TaxCategoryService;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class BillingAccountApi extends AccountEntityApi {

    @Inject
    private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private TradingCountryService tradingCountryService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private IsoIcdService isoIcdService;
    
    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;
    
    @EJB
    private AccountHierarchyApi accountHierarchyApi;

    @Inject
    private InvoiceApi invoiceApi;

    @Inject
    private InvoiceTypeService invoiceTypeService;

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private UserAccountApi userAccountApi;

    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    @Inject
    private EmailTemplateService emailTemplateService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private TaxCategoryService taxCategoryService;

    @Inject
    private PaymentMethodService paymentMethodService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;
    
    @Inject
    private ProviderContactService providerContactService;

    @Inject
    private ProviderService providerService;
    
    @Inject
    private TagService tagService;

    @Inject
    private TitleService titleService;

    @Inject
    private PriceListService priceListService;

    public BillingAccount create(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        return create(postData, true);
    }

    public BillingAccount create(BillingAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return create(postData, true, null, null);
    }

    public BillingAccount create(BillingAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel,
                                 CustomerAccount associatedCA) throws MeveoApiException, BusinessException {

        if(StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(BillingAccount.class.getName(), postData);
        }
        if (StringUtils.isBlank(postData.getCustomerAccount())) {
            missingParameters.add("customerAccount");
        }
        if (StringUtils.isBlank(postData.getBillingCycle())) {
            missingParameters.add("billingCycle");
        }
        if (StringUtils.isBlank(postData.getCountry())) {
            missingParameters.add("country");
        }
        if (StringUtils.isBlank(postData.getLanguage())) {
            missingParameters.add("language");
        }
        if (postData.getElectronicBilling() != null && postData.getElectronicBilling()) {
            if (StringUtils.isBlank(postData.getEmail())) {
                missingParameters.add("email");
            }
            if (postData.getMailingType() != null && StringUtils.isBlank(postData.getEmailTemplate())) {
                missingParameters.add("emailTemplate");
            }
        }

        handleMissingParameters(postData);

        if (billingAccountService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(BillingAccount.class, postData.getCode());
        }

        BillingAccount billingAccount = new BillingAccount();

        dtoToEntity(billingAccount, postData, checkCustomFields, businessAccountModel, associatedCA);

        processTags(postData,billingAccount);

        billingAccountService.createBillingAccount(billingAccount);
        if (postData.getIsCompany() != null) {
            billingAccount.setIsCompany(postData.getIsCompany());
        }

        // instantiate the discounts
        if (postData.getDiscountPlansForInstantiation() != null) {
            List<DiscountPlan> discountPlans = new ArrayList<>();
            for (DiscountPlanDto discountPlanDto : postData.getDiscountPlansForInstantiation()) {
                DiscountPlan dp = discountPlanService.findByCode(discountPlanDto.getCode());
                if (dp == null) {
                    throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanDto.getCode());
                }

                discountPlanService.detach(dp);
                dp = DiscountPlanDto.copyFromDto(discountPlanDto, dp);

                // populate customFields
                try {
                    populateCustomFields(discountPlanDto.getCustomFields(), dp, true);
                } catch (MissingParameterException | InvalidParameterException e) {
                    log.error("Failed to associate custom field instance to an entity: {} {}", discountPlanDto.getCode(), e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to associate custom field instance to an entity {}", discountPlanDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + discountPlanDto.getCode());
                }

                discountPlans.add(dp);
            }

            billingAccountService.instantiateDiscountPlans(billingAccount, discountPlans);
        }

        return billingAccount;
    }

    private void processTags(BillingAccountDto postData, BillingAccount billingAccount) {
    	Set<String> tagCodes = postData.getTagCodes();
		if(tagCodes != null && !tagCodes.isEmpty()){
			List<Tag> tags=new ArrayList<Tag>();
			for(String code:tagCodes) {
				Tag tag=tagService.findByCode(code);
				if(tag == null) {
					throw new EntityDoesNotExistsException(Tag.class,code);
				}
				tags.add(tag);
			}
			billingAccount.setTags(tags);
		}
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = ObjectPropertyParser.class, property = "code", entityClass = BillingAccount.class))
    public BillingAccount update(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        return update(postData, true);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = ObjectPropertyParser.class, property = "code", entityClass = BillingAccount.class))
    public BillingAccount update(BillingAccountDto postData, boolean checkCustomFields) throws MeveoApiException, BusinessException {
        return update(postData, true, null);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = ObjectPropertyParser.class, property = "code", entityClass = BillingAccount.class))
    public BillingAccount update(BillingAccountDto postData, boolean checkCustomFields, BusinessAccountModel businessAccountModel) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(postData);

        BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
        }

        if (!StringUtils.isBlank(postData.getUpdatedCode())) {
            if (billingAccountService.findByCode(postData.getUpdatedCode()) != null) {
                throw new EntityAlreadyExistsException(BillingAccount.class, postData.getUpdatedCode());
            }
        }

        dtoToEntity(billingAccount, postData, checkCustomFields, businessAccountModel, null);
        processTags(postData,billingAccount);

        billingAccount = billingAccountService.update(billingAccount);

        // terminate discounts
        if (postData.getDiscountPlansForTermination() != null) {
            List<DiscountPlanInstance> dpis = new ArrayList<>();
            for (String dpiCode : postData.getDiscountPlansForTermination()) {
                DiscountPlanInstance dpi = discountPlanInstanceService.findByBillingAccountAndCode(billingAccount, dpiCode);
                if (dpi == null) {
                    throw new EntityDoesNotExistsException(DiscountPlanInstance.class, dpiCode);
                }

                dpis.add(dpi);
            }

            if (!dpis.isEmpty()) {
                billingAccountService.terminateDiscountPlans(billingAccount, dpis);
            }
        }

        // instantiate the discounts
        if (postData.getDiscountPlansForInstantiation() != null) {
            List<DiscountPlan> discountPlans = new ArrayList<>();
            for (DiscountPlanDto discountPlanDto : postData.getDiscountPlansForInstantiation()) {
                DiscountPlan dp = discountPlanService.findByCode(discountPlanDto.getCode());
                if (dp == null) {
                    throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanDto.getCode());
                }

                discountPlanService.detach(dp);
                dp = DiscountPlanDto.copyFromDto(discountPlanDto, dp);

                // populate customFields
                try {
                    populateCustomFields(discountPlanDto.getCustomFields(), dp, false);
                } catch (MissingParameterException | InvalidParameterException e) {
                    log.error("Failed to associate custom field instance to an entity: {} {}", discountPlanDto.getCode(), e.getMessage());
                    throw e;
                } catch (Exception e) {
                    log.error("Failed to associate custom field instance to an entity {}", discountPlanDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + discountPlanDto.getCode());
                }

                discountPlans.add(dp);
            }

            billingAccountService.instantiateDiscountPlans(billingAccount, discountPlans);
        }
        return billingAccount;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param billingAccount Entity to populate
     * @param postData DTO entity object to populate from
     * @param checkCustomFields Should a check be made if CF field is required
     * @param businessAccountModel Business account model
     **/
    private void dtoToEntity(BillingAccount billingAccount, BillingAccountDto postData, boolean checkCustomFields,
                             BusinessAccountModel businessAccountModel, CustomerAccount associatedCA) {

        boolean isNew = billingAccount.getId() == null;

        if (postData.getMailingType() != null) {
            if (StringUtils.isBlank(postData.getMailingType())) {
                billingAccount.setMailingType(null);
            } else {
                billingAccount.setMailingType(MailingTypeEnum.getByLabel(postData.getMailingType()));
            }
        }

        if (postData.getEmailTemplate() != null) {
            if (StringUtils.isBlank(postData.getEmailTemplate())) {
                billingAccount.setEmailTemplate(null);
            } else {
                EmailTemplate emailTemplate = emailTemplateService.findByCode(postData.getEmailTemplate());
                if (emailTemplate == null) {
                    throw new EntityDoesNotExistsException(EmailTemplate.class, postData.getEmailTemplate());
                } else {
                    billingAccount.setEmailTemplate(emailTemplate);
                }
            }
        }

        if (postData.getMinimumInvoiceSubCategory() != null) {
            if (StringUtils.isBlank(postData.getMinimumInvoiceSubCategory())) {
                billingAccount.setMinimumInvoiceSubCategory(null);
            }
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(postData.getMinimumInvoiceSubCategory());
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, postData.getMinimumInvoiceSubCategory());
            } else {
                billingAccount.setMinimumInvoiceSubCategory(invoiceSubCategory);
            }
        }

        if (postData.getCustomerAccount() != null) {
            CustomerAccount customerAccount =
                    associatedCA != null ? associatedCA : customerAccountService.findByCode(postData.getCustomerAccount());
            if (customerAccount == null) {
                throw new EntityDoesNotExistsException(CustomerAccount.class, postData.getCustomerAccount());

            } else if (!isNew && !billingAccount.getCustomerAccount().equals(customerAccount)) {
                // a safeguard to allow this only if all the WO/RT have been invoiced.
                Long countNonTreatedWO = walletOperationService.countNonTreatedWOByBA(billingAccount);
                if (countNonTreatedWO > 0) {
                    throw new BusinessApiException("Can not change the parent account. Billing account have non treated WO");
                }
                Long countNonInvoicedRT = ratedTransactionService.countNotInvoicedRTByBA(billingAccount);
                if (countNonInvoicedRT > 0) {
                    throw new BusinessApiException("Can not change the parent account. Billing account have non invoiced RT");
                }
            }
            billingAccount.setCustomerAccount(customerAccount);
        }

        if (!StringUtils.isBlank(postData.getTradingCurrency())) {
            TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getTradingCurrency());
            if (tradingCurrency == null) {
                throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getTradingCurrency());
            }
            if(tradingCurrency.isDisabled())
            {
                throw new BusinessApiException("The billing account should not have a disabled trading currency");
            }
            billingAccount.setTradingCurrency(tradingCurrency);
        }else if(billingAccount.getCustomerAccount().getTradingCurrency() != null) {
            billingAccount.setTradingCurrency(billingAccount.getCustomerAccount().getTradingCurrency());
        }else {
        	if(providerService.getProvider().getCurrency() != null) {
                TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(providerService.getProvider().getCurrency().getCurrencyCode());
                billingAccount.setTradingCurrency(tradingCurrency);
        	}
        }
	    PaymentMethod paymentMethod = null;
        if (Objects.nonNull(postData.getPaymentMethod())) {
            paymentMethod = paymentMethodService.findById(postData.getPaymentMethod().getId());
            if (paymentMethod == null) {
                throw new EntityNotFoundException("payment method not found!");
            }
        }
	    billingAccount.setPaymentMethod(paymentMethod);

        if (postData.getBillingCycle() != null) {
            BillingCycle billingCycle = billingCycleService.findByCode(postData.getBillingCycle());
            if (billingCycle == null) {
                throw new EntityDoesNotExistsException(BillingCycle.class, postData.getBillingCycle());
            }
            billingAccount.setBillingCycle(billingCycle);
        }

        if (postData.getCountry() != null) {
            TradingCountry tradingCountry = tradingCountryService.findByCode(postData.getCountry());
            if (tradingCountry == null) {
                throw new EntityDoesNotExistsException(TradingCountry.class, postData.getCountry());
            }
            billingAccount.setTradingCountry(tradingCountry);
        }

        if (postData.getLanguage() != null) {
            TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getLanguage());
            if (tradingLanguage == null) {
                throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getLanguage());
            }
            billingAccount.setTradingLanguage(tradingLanguage);
        }

        if (!StringUtils.isBlank(postData.getPhone())) {
            postData.getContactInformation().setPhone(postData.getPhone());
        }
        if (!StringUtils.isBlank(postData.getEmail())) {
            postData.getContactInformation().setEmail(postData.getEmail());
        }

        if (postData.getTaxCategoryCode() != null) {
            if (StringUtils.isBlank(postData.getTaxCategoryCode())) {
                billingAccount.setTaxCategory(null);

            } else {
                TaxCategory taxCategory = taxCategoryService.findByCode(postData.getTaxCategoryCode());
                if (taxCategory == null) {
                    throw new EntityDoesNotExistsException(TaxCategory.class, postData.getTaxCategoryCode());
                }
                billingAccount.setTaxCategory(taxCategory);
            }
        }

        if (postData.getCheckThreshold() != null) {
            billingAccount.setCheckThreshold(postData.getCheckThreshold());
        } else if (isNew && postData.getInvoicingThreshold() != null) {
            billingAccount.setCheckThreshold(ThresholdOptionsEnum.AFTER_DISCOUNT);
        }
        if (postData.isThresholdPerEntity() != null) {
            billingAccount.setThresholdPerEntity(postData.isThresholdPerEntity());
        }
        updateAccount(billingAccount, postData, checkCustomFields);

        if (postData.getNextInvoiceDate() != null) {
            billingAccount.setNextInvoiceDate(postData.getNextInvoiceDate());
        }
        if (postData.getSubscriptionDate() != null) {
            billingAccount.setSubscriptionDate(postData.getSubscriptionDate());
        }
        if (postData.getTerminationDate() != null) {
            billingAccount.setTerminationDate(postData.getTerminationDate());
        }
        if (postData.getElectronicBilling() != null) {
            billingAccount.setElectronicBilling(postData.getElectronicBilling());
        }
        if (postData.getCcedEmails() != null) {
            billingAccount.setCcedEmails(postData.getCcedEmails());
        }
        if (billingAccount.getElectronicBilling() && billingAccount.getContactInformation().getEmail() == null) {
            missingParameters.add("email");
            if (billingAccount.getMailingType() != null && billingAccount.getEmailTemplate() == null) {
                missingParameters.add("emailTemplate");
            }
            handleMissingParameters();
        }

        if (postData.getInvoicingThreshold() != null) {
            billingAccount.setInvoicingThreshold(postData.getInvoicingThreshold());
        }
        if (postData.isThresholdPerEntity() != null) {
        	billingAccount.setThresholdPerEntity(postData.isThresholdPerEntity());
        }
        if (postData.getPhone() != null) {
            billingAccount.getContactInformation().setPhone(postData.getPhone());
        }
        if (postData.getMinimumAmountEl() != null) {
            billingAccount.setMinimumAmountEl(postData.getMinimumAmountEl());
        }
        if (postData.getMinimumLabelEl() != null) {
            billingAccount.setMinimumLabelEl(postData.getMinimumLabelEl());
        }
        if (postData.getStatus() != null) {
        	billingAccount.setStatus(postData.getStatus());
        }

        if (businessAccountModel != null) {
            billingAccount.setBusinessAccountModel(businessAccountModel);
        }

        if (postData.getPrimaryContact() != null) {
            if (StringUtils.isBlank(postData.getPrimaryContact())) {
                billingAccount.setPrimaryContact(null);
            } else {
                ProviderContact primaryContact = providerContactService.findByCode(postData.getPrimaryContact());
                if (primaryContact == null) {
                    throw new EntityDoesNotExistsException(ProviderContact.class, postData.getPrimaryContact());
                } else {
                    billingAccount.setPrimaryContact(primaryContact);
                }
            }
        }

        if(postData.getIsCompany() != null) {
        	billingAccount.setIsCompany(postData.getIsCompany());
        }

        if(postData.getLegalEntityType() != null) {
        	var titleDto = postData.getLegalEntityType();
        	if(StringUtils.isEmpty(titleDto.getCode()))
        		missingParameters.add("legalEntityType.code");
        	handleMissingParameters();
        	Title title = titleService.findByCode(titleDto.getCode());
        	if(title == null)
        		title = new Title(titleDto.getCode(), titleDto.getIsCompany());
        	if(!StringUtils.isEmpty(titleDto.getDescription()))
        		title.setDescription(titleDto.getDescription());
        	if(titleDto.getLanguageDescriptions() != null && !titleDto.getLanguageDescriptions().isEmpty()) {
        		title.setDescriptionI18n(
        								titleDto.getLanguageDescriptions()
        											.stream().collect(Collectors.toMap(LanguageDescriptionDto::getLanguageCode, LanguageDescriptionDto::getDescription)));
        	}
        	if(title.getId() == null)
        		titleService.create(title);
        	billingAccount.setLegalEntityType(title);
        }
        // exemptionReason is mandatory billingAccount.taxCategory==EXEMPTED
        if ("EXEMPTED".equalsIgnoreCase(postData.getTaxCategoryCode()) && StringUtils.isBlank(postData.getExemptionReason())) {
            throw new BusinessApiException("Exemption Reason is mandatory for EXEMPTED TaxCategory");
        }

        if (StringUtils.isNotBlank(postData.getExemptionReason())) {
            billingAccount.setExemptionReason(postData.getExemptionReason());
        } else {
            billingAccount.setExemptionReason(null);
        }

        // Optional default PriceList
        if(Objects.nonNull(postData.getPriceListCode())) {
            if(postData.getPriceListCode().isBlank()) {
                billingAccount.setPriceList(null);
            } else {
                PriceList priceList = Optional.ofNullable(priceListService.findByCode(postData.getPriceListCode())).orElseThrow(() -> new EntityDoesNotExistsException(PriceList.class, postData.getPriceListCode()));
                billingAccount.setPriceList(priceList);
            }
        }

        // Update payment method information in a customer account.
        // ONLY used to handle deprecated billingAccountDto.paymentMethod and billingAccountDto.bankCoordinates fields. Use
        createOrUpdatePaymentMethodInCA(postData, billingAccount);

        // Validate and populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), billingAccount, isNew, checkCustomFields);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = BillingAccount.class))
    public BillingAccountDto find(String billingAccountCode) throws MeveoApiException {
        return find(billingAccountCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, false);
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = BillingAccount.class))
    public BillingAccountDto find(String billingAccountCode, CustomFieldInheritanceEnum inheritCF, boolean includeUserAccounts) throws MeveoApiException {
        if (StringUtils.isBlank(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
            handleMissingParameters();
        }
        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }

        BillingAccountDto billingAccountDto = accountHierarchyApi.billingAccountToDto(billingAccount, inheritCF, includeUserAccounts);

        if (billingAccount.getDiscountPlanInstances() != null && !billingAccount.getDiscountPlanInstances().isEmpty()) {
            billingAccountDto.setDiscountPlanInstances(billingAccount.getDiscountPlanInstances().stream()
                .map(p -> new DiscountPlanInstanceDto(p, entityToDtoConverter.getCustomFieldsDTO(p, CustomFieldInheritanceEnum.INHERIT_NONE))).collect(Collectors.toList()));
        }

        return billingAccountDto;
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = BillingAccount.class))
    public void remove(String billingAccountCode) throws MeveoApiException {
        if (StringUtils.isBlank(billingAccountCode)) {
            missingParameters.add("billingAccountCode");
            handleMissingParameters();
        }
        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }
        try {
            billingAccountService.remove(billingAccount);
            billingAccountService.commit();
        } catch (Exception e) {
            if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                throw new DeleteReferencedEntityException(BillingAccount.class, billingAccountCode);
            }
            throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
        }
    }

    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(entityClass = CustomerAccount.class))
    public BillingAccountsDto listByCustomerAccount(String customerAccountCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(customerAccountCode)) {
            missingParameters.add("customerAccountCode");
            handleMissingParameters();
        }

        CustomerAccount customerAccount = customerAccountService.findByCode(customerAccountCode);
        if (customerAccount == null) {
            throw new EntityDoesNotExistsException(CustomerAccount.class, customerAccountCode);
        }

        BillingAccountsDto result = new BillingAccountsDto();
        List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount, GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
        if (billingAccounts != null) {
            for (BillingAccount ba : billingAccounts) {
                BillingAccountDto billingAccountDto = accountHierarchyApi.billingAccountToDto(ba);

                List<Invoice> invoices = ba.getInvoices();
                if (invoices != null && invoices.size() > 0) {
                    List<InvoiceDto> invoicesDto = new ArrayList<InvoiceDto>();
                    if (invoices != null && invoices.size() > 0) {
                        for (Invoice invoice : invoices) {
                            if (invoiceTypeService.getListAdjustementCode().equals(invoice.getInvoiceType().getCode())) {
                                InvoiceDto invoiceDto = invoiceApi.invoiceToDto(invoice, false, false);
                                invoicesDto.add(invoiceDto);
                            }
                        }
                        billingAccountDto.setInvoices(invoicesDto);
                    }
                }

                result.getBillingAccount().add(billingAccountDto);
            }
        }

        return result;
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "billingAccounts.billingAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = BillingAccount.class) })
    public BillingAccountsResponseDto list(PagingAndFiltering pagingAndFiltering) {
        BillingAccountsResponseDto result = new BillingAccountsResponseDto();
        result.setPaging( pagingAndFiltering );

        List<BillingAccount> billingAccounts = billingAccountService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (billingAccounts != null) {
            for (BillingAccount billingAccount : billingAccounts) {
                result.getBillingAccounts().getBillingAccount().add(new BillingAccountDto(billingAccount));
            }
        }

        return result;
    }

    /**
     * Create or update Billing Account based on Billing Account Code
     * 
     * @param postData posted data to API
     * @return the billing account
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    @SecuredBusinessEntityMethod(validate = @SecureMethodParameter(parser = ObjectPropertyParser.class, property = "code", entityClass = BillingAccount.class))
    public BillingAccount createOrUpdate(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(postData.getCode()) && billingAccountService.findByCode(postData.getCode()) != null) {
            return update(postData);
        } else {
            return create(postData);
        }
    }

    public BillingAccount terminate(BillingAccountDto postData) throws MeveoApiException {
        SubscriptionTerminationReason terminationReason = null;
        try {
            terminationReason = subscriptionTerminationReasonService.findByCodeReason(postData.getTerminationReason());
        } catch (Exception e) {
            log.error("error = {}", e);
        }
        if (terminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
        }

        BillingAccount billingAccount = billingAccountService.findByCode(postData.getCode());
        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, postData.getCode());
        }

        try {
            billingAccountService.billingAccountTermination(billingAccount, postData.getTerminationDate(), terminationReason);
        } catch (BusinessException e) {
            log.error("Failed terminating a billingAccount with code={}. {}", postData.getCode(), e.getMessage());
            throw new MeveoApiException("Failed terminating billingAccount with code=" + postData.getCode());
        }

        return billingAccount;
    }

    public List<CounterInstance> filterCountersByPeriod(String billingAccountCode, Date date) throws MeveoApiException, BusinessException {

        BillingAccount billingAccount = billingAccountService.findByCode(billingAccountCode);

        if (billingAccount == null) {
            throw new EntityDoesNotExistsException(BillingAccount.class, billingAccountCode);
        }

        if (date == null) {
            throw new MeveoApiException("date is null");
        }

        return new ArrayList<>(billingAccountService.filterCountersByPeriod(billingAccount.getCounters(), date).values());
    }

    public void createOrUpdatePartial(BillingAccountDto postData) throws MeveoApiException, BusinessException {
        BillingAccountDto existedBillingAccountDto = null;
        try {
            existedBillingAccountDto = find(postData.getCode());
        } catch (Exception e) {
            existedBillingAccountDto = null;
        }
        log.debug("createOrUpdate billingAccount {}", postData);
        if (existedBillingAccountDto == null) {// create
            create(postData);
        } else {// update
            if (postData.getTerminationDate() != null) {
                if (StringUtils.isBlank(postData.getTerminationReason())) {
                    missingParameters.add("billingAccount.terminationReason");
                    handleMissingParametersAndValidate(postData);
                }
                terminate(postData);
            } else {

                if (!StringUtils.isBlank(postData.getCustomerAccount())) {
                    existedBillingAccountDto.setCustomerAccount(postData.getCustomerAccount());
                }

                if (!StringUtils.isBlank(postData.getBillingCycle())) {
                    existedBillingAccountDto.setBillingCycle(postData.getBillingCycle());
                }
                if (!StringUtils.isBlank(postData.getCountry())) {
                    existedBillingAccountDto.setCountry(postData.getCountry());
                }
                if (!StringUtils.isBlank(postData.getLanguage())) {
                    existedBillingAccountDto.setLanguage(postData.getLanguage());
                }

                //
                if (postData.getNextInvoiceDate() != null) {
                    existedBillingAccountDto.setNextInvoiceDate(postData.getNextInvoiceDate());
                }
                if (postData.getSubscriptionDate() != null) {
                    existedBillingAccountDto.setSubscriptionDate(postData.getSubscriptionDate());
                }
                if (postData.getTerminationDate() != null) {
                    existedBillingAccountDto.setTerminationDate(postData.getTerminationDate());
                }
                if (postData.getElectronicBilling() != null) {
                    existedBillingAccountDto.setElectronicBilling(postData.getElectronicBilling());
                }
                if (!StringUtils.isBlank(postData.getEmail())) {
                    existedBillingAccountDto.setEmail(postData.getEmail());
                }
                if (postData.getMinimumAmountEl() != null) {
                    existedBillingAccountDto.setMinimumAmountEl(postData.getMinimumAmountEl());
                }
                if (postData.getMinimumAmountElSpark() != null) {
                    existedBillingAccountDto.setMinimumAmountElSpark(postData.getMinimumAmountElSpark());
                }
                if (postData.getMinimumLabelEl() != null) {
                    existedBillingAccountDto.setMinimumLabelEl(postData.getMinimumLabelEl());
                }
                if (postData.getMinimumLabelElSpark() != null) {
                    existedBillingAccountDto.setMinimumLabelElSpark(postData.getMinimumLabelElSpark());
                }
                if (postData.getInvoicingThreshold() != null) {
                    existedBillingAccountDto.setInvoicingThreshold(postData.getInvoicingThreshold());
                }
                if(postData.isThresholdPerEntity() != null) {
                	existedBillingAccountDto.setThresholdPerEntity(postData.isThresholdPerEntity());
                }

                accountHierarchyApi.populateNameAddress(existedBillingAccountDto, postData);
                if (postData.getCustomFields() != null && !postData.getCustomFields().isEmpty()) {
                    existedBillingAccountDto.setCustomFields(postData.getCustomFields());
                }
                update(existedBillingAccountDto);
            }
        }
    }

    /**
     * Update payment method information in a customer account. ONLY used to handle deprecated billingAccountDto.paymentMethod and billingAccountDto.bankCoordinates fields. Use
     * CustomerAccounDto.paymentMethods instead.
     * 
     * @param postData Billing account DTO
     * @param billingAccount Billing account to update if necessary
     * @throws MeveoApiException
     * @throws BusinessException General business exception
     */
    private void createOrUpdatePaymentMethodInCA(BillingAccountDto postData, BillingAccount billingAccount) throws MeveoApiException, BusinessException {

        if (postData.getPaymentMethodType() == null) {
            return;
        }

        CustomerAccount customerAccount = billingAccount.getCustomerAccount();

        if (postData.getPaymentMethodType() == PaymentMethodEnum.CARD) {
            throw new InvalidParameterException("paymentMethod", "Card");
        } else if (postData.getPaymentMethodType() == PaymentMethodEnum.DIRECTDEBIT) {
            if (postData.getBankCoordinates() == null) {
                throw new MissingParameterException("bankCoordinates");
            }

            if (StringUtils.isBlank(postData.getBankCoordinates().getIban())) {
                throw new MissingParameterException("iban");
            }
        }

        boolean found = false;
        boolean updateCA = false;

        if (customerAccount.getPaymentMethods() != null) {
            for (PaymentMethod paymentMethod : customerAccount.getPaymentMethods()) {
                if (postData.getPaymentMethodType() == paymentMethod.getPaymentType()) {

                    if (postData.getPaymentMethodType().isSimple()) {
                        found = true;
                        break;

                    } else if (postData.getPaymentMethodType() == PaymentMethodEnum.DIRECTDEBIT) {

                        paymentMethod = PersistenceUtils.initializeAndUnproxy(paymentMethod);
                        if (postData.getBankCoordinates().getIban() != null && ((DDPaymentMethod) paymentMethod).getBankCoordinates() != null
                                && postData.getBankCoordinates().getIban().equals(((DDPaymentMethod) paymentMethod).getBankCoordinates().getIban())) {
                            found = true;
                            BankCoordinates bankCoordinatesFromDto = postData.getBankCoordinates().fromDto();

                            if (!BeanUtils.isIdentical(bankCoordinatesFromDto, ((DDPaymentMethod) paymentMethod).getBankCoordinates())) {
                                ((DDPaymentMethod) paymentMethod).setBankCoordinates(bankCoordinatesFromDto);
                                updateCA = true;
                            }
                            break;
                        }

                    }
                }
            }
        }

        if (!found) {
            PaymentMethod paymentMethodFromDto = null;
            if (postData.getPaymentMethodType().isSimple()) {
                paymentMethodFromDto = (new PaymentMethodDto(postData.getPaymentMethodType())).fromDto(customerAccount, null, currentUser);
            } else if (postData.getPaymentMethodType() == PaymentMethodEnum.DIRECTDEBIT) {
                paymentMethodFromDto = (new PaymentMethodDto(postData.getPaymentMethodType(), postData.getBankCoordinates(), null, null)).fromDto(customerAccount, null, currentUser);
            }

            if (customerAccount.getPaymentMethods() == null) {
                customerAccount.setPaymentMethods(new ArrayList<>());
            }
            customerAccount.getPaymentMethods().add(paymentMethodFromDto);

            updateCA = true;
        }

        if (updateCA) {
            customerAccount = customerAccountService.update(customerAccount);
            billingAccount.setCustomerAccount(customerAccount);
        }
    }

    /**
     * Exports a json representation of the BillingAcount hierarchy. It include subscription, accountOperations and invoices.
     * 
     * @param ba the selected BillingAccount
     * @return DTO representation of BillingAccount
     */
    public BillingAccountDto exportBillingAccountHierarchy(BillingAccount ba, List<GDPRInfoDto> billingAccountGDPR) {
        BillingAccountDto result = new BillingAccountDto(ba, billingAccountGDPR);

        if (ba.getInvoices() != null && !ba.getInvoices().isEmpty()) {
            for (Invoice invoice : ba.getInvoices()) {
                result.getInvoices().add(invoiceApi.invoiceToDto(invoice, true, false));
            }
        }

        if (ba.getUsersAccounts() != null && !ba.getUsersAccounts().isEmpty()) {
            for (UserAccount ua : ba.getUsersAccounts()) {
            	List<GDPRInfoDto> userAccountGdpr = customFieldTemplateService.findCFMarkAsAnonymize(ua);
                result.getUserAccounts().getUserAccount().add(userAccountApi.exportUserAccountHierarchy(ua, userAccountGdpr));
            }
        }

        return result;
    }

}
