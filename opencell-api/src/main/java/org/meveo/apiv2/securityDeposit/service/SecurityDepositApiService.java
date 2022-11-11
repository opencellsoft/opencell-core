package org.meveo.apiv2.securityDeposit.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.BaseEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.securityDeposit.FinanceSettings;
import org.meveo.model.securityDeposit.SecurityDeposit;
import org.meveo.model.securityDeposit.SecurityDepositStatusEnum;
import org.meveo.model.securityDeposit.SecurityDepositTemplate;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;
import org.meveo.service.securityDeposit.impl.SecurityDepositService;
import org.meveo.service.securityDeposit.impl.SecurityDepositTemplateService;

public class SecurityDepositApiService implements ApiService<SecurityDeposit> {

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private SecurityDepositService securityDepositService;

    @Inject
    private SecurityDepositTemplateService securityDepositTemplateService;

    @Inject
    private CurrencyService currencyService;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ProductService productService;

    @Inject
    private ServiceInstanceService serviceInstanceService;
    
    @Override
    public List<SecurityDeposit> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return null;
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<SecurityDeposit> findById(Long id) {
        return empty();
    }

    @Override
    public SecurityDeposit create(SecurityDeposit baseEntity) {
        return null;
    }

    @Override
    public Optional<SecurityDeposit> update(Long id, SecurityDeposit baseEntity) {
        return empty();
    }

    @Override
    public Optional<SecurityDeposit> patch(Long id, SecurityDeposit baseEntity) {
        return empty();
    }

    @Override
    public Optional<SecurityDeposit> delete(Long id) {
        return empty();
    }

    @Override
    public Optional<SecurityDeposit> findByCode(String code) {
        return empty();
    }

    public Optional<SecurityDeposit> instantiate(SecurityDeposit securityDepositInput) {

        // Check FinanceSettings.useSecurityDeposit
        FinanceSettings financeSettings = financeSettingsService.findLastOne();
        if (financeSettings == null || !financeSettings.isUseSecurityDeposit()) {
            throw new BadRequestException("instantiation is not allowed in general settings");
        }
        BigDecimal securityDepositAmount = securityDepositInput.getAmount();
        if (securityDepositAmount == null) {
            throw new EntityDoesNotExistsException("The Amount == null.");
        }
        linkRealEntities(securityDepositInput);        

        // Check Maximum amount per Security deposit
        BigDecimal maxAmountPerSecurityDeposit = financeSettings.getMaxAmountPerSecurityDeposit();
        if (maxAmountPerSecurityDeposit != null && securityDepositAmount.compareTo(maxAmountPerSecurityDeposit) > 0) {
            throw new BadRequestException("The amount is greater than the maximum per security deposit : " + maxAmountPerSecurityDeposit);
        }

        // Check Maximum Security deposit amount per Client
        BigDecimal maxAmountPerCustomer = financeSettings.getMaxAmountPerCustomer();
        if (maxAmountPerCustomer != null) {
            BigDecimal sumAmountPerCustomer = securityDepositService.sumAmountPerCustomer(securityDepositInput.getCustomerAccount());
            if (sumAmountPerCustomer != null) {
                BigDecimal totalAmount = securityDepositAmount.add(sumAmountPerCustomer);
                if (totalAmount.compareTo(maxAmountPerCustomer) > 0) {
                    throw new BadRequestException("Security deposit amount is greater than the maximum per customer");
                }
            }
        }

        SecurityDepositTemplate template = securityDepositInput.getTemplate();
        // UNIQUE Security deposit Name
        Long count = securityDepositService.countPerTemplate(template);
        String securityDepositName = securityDepositInput.getCode();
        if (StringUtils.isBlank(securityDepositName)) {
            securityDepositName = template.getTemplateName()+ "-" + count;
        }
        securityDepositInput.setCode(securityDepositName);
        securityDepositInput.setStatus(SecurityDepositStatusEnum.NEW);

        // Check validity dates
        if (financeSettings.isAutoRefund() && template.isAllowValidityDate() && template.isAllowValidityPeriod()) {
            if (securityDepositInput.getValidityDate() == null && securityDepositInput.getValidityPeriod() == null) {
                throw new BadRequestException("At least one of the two options (SD.validityDate or SD.validityPeriod) should be filled");
            }
        }
        if (securityDepositInput.getValidityDate() != null && ((isSameDay(securityDepositInput.getValidityDate(), new Date()))
                || securityDepositInput.getValidityDate().before(new Date()))) {
            throw new BadRequestException("Validity must be in the future");
        }
        if (securityDepositInput.getValidityPeriod() != null && securityDepositInput.getValidityPeriod() <= 0) {
            throw new BadRequestException("0 and negative values not allowed");
        }
        if (securityDepositInput.getValidityPeriod() != null && securityDepositInput.getValidityPeriodUnit() == null) {
            throw new BadRequestException("The validity period unit must be not null or empty");
        }

        // Check The min and max amount
        if (template.getMinAmount() != null && template.getMinAmount().compareTo(securityDepositAmount) > 0) {
            throw new BadRequestException("The amount should be greater or equal to minimum amount (of SD template) : " + template.getMinAmount());
        }
        if (template.getMaxAmount() != null && template.getMaxAmount().compareTo(securityDepositAmount) < 0) {
            throw new BadRequestException("The amount should be lesser or equal to maximum amount (of SD template) : " + template.getMaxAmount());
        }

        // Check The if subscription not null the serviceInstance cannot be null 
        if(securityDepositInput.getSubscription() != null && securityDepositInput.getServiceInstance() == null) {
            throw new BadRequestException("The service instance is mandatory if subscription is set");
        }

        securityDepositService.create(securityDepositInput);

        // Increment template.NumberOfInstantiation after each instantiation
        Integer numberOfInstantiation = template.getNumberOfInstantiation() != null ? template.getNumberOfInstantiation() : 0;
        template.setNumberOfInstantiation(++numberOfInstantiation);
        securityDepositTemplateService.update(template);

        return of(securityDepositInput);
    }

    public void linkRealEntities(SecurityDeposit securityDepositInput) {
        if (securityDepositInput.getTemplate() != null) {
            SecurityDepositTemplate securityDepositTemplate = securityDepositTemplateService.tryToFindByCodeOrId(securityDepositInput.getTemplate());
            securityDepositInput.setTemplate(securityDepositTemplate);
        }

        if (securityDepositInput.getCurrency() != null) {
            Currency currency = currencyService.findById(securityDepositInput.getCurrency().getId());
            validateNotNull(securityDepositInput.getCurrency(), currency);
            securityDepositInput.setCurrency(currency);
        }

        if (securityDepositInput.getCustomerAccount() != null) {
            CustomerAccount customerAccount = customerAccountService.tryToFindByCodeOrId(securityDepositInput.getCustomerAccount());
            securityDepositInput.setCustomerAccount(customerAccount);
        }

        if (securityDepositInput.getSubscription() != null) {
            Subscription subscription = subscriptionService.tryToFindByCodeOrId(securityDepositInput.getSubscription());
            securityDepositInput.setSubscription(subscription);
        }

        if (securityDepositInput.getServiceInstance() != null) {
            ServiceInstance serviceInstance = serviceInstanceService.tryToFindByCodeOrId(securityDepositInput.getServiceInstance());
            securityDepositInput.setServiceInstance(serviceInstance);
        }
    }

    private <B extends BaseEntity> void validateNotNull(B input, B result) {
        if (result == null) {
            StringBuilder sb = new StringBuilder(splitCamelCase(input.getClass().getSimpleName()));
            sb.append(" no found");
            if (input.getId() != null) {
                sb.append(" with id =").append(input.getId());
            }
            throw new EntityDoesNotExistsException(sb.toString());
        }
    }

    private String splitCamelCase(String s) {
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase("ExampleTest"), ' ');
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}