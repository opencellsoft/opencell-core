package org.meveo.api.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.ServiceChargeTemplateRecurringDto;
import org.meveo.api.dto.catalog.ServiceChargeTemplateSubscriptionDto;
import org.meveo.api.dto.catalog.ServiceChargeTemplateTerminationDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.catalog.ServiceUsageChargeTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidImageData;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceChargeTemplateRecurring;
import org.meveo.model.catalog.ServiceChargeTemplateSubscription;
import org.meveo.model.catalog.ServiceChargeTemplateTermination;
import org.meveo.model.catalog.ServiceChargeTemplateUsage;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.BusinessServiceModelService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateRecurringService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateSubscriptionService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateTerminationService;
import org.meveo.service.catalog.impl.ServiceChargeTemplateUsageService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ServiceTemplateApi extends BaseCrudApi<ServiceTemplate, ServiceTemplateDto> {

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @Inject
    private ServiceChargeTemplateRecurringService serviceChargeTemplateRecurringService;

    @Inject
    private ServiceChargeTemplateSubscriptionService serviceChargeTemplateSubscriptionService;

    @Inject
    private ServiceChargeTemplateTerminationService serviceChargeTemplateTerminationService;

    @Inject
    private ServiceChargeTemplateUsageService serviceUsageChargeTemplateService;

    @Inject
    private CounterTemplateService counterTemplateService;
    
    @Inject
    private BusinessServiceModelService businessServiceModelService;

    private void createServiceChargeTemplateRecurring(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {
        

        if (postData.getServiceChargeTemplateRecurrings() != null) {
            for (ServiceChargeTemplateRecurringDto serviceChargeTemplateDto : postData.getServiceChargeTemplateRecurrings().getServiceChargeTemplateRecurring()) {
                List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

                RecurringChargeTemplate chargeTemplate = recurringChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode());
                if (chargeTemplate == null) {
                    throw new EntityDoesNotExistsException(RecurringChargeTemplate.class, serviceChargeTemplateDto.getCode());
                }

                for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
                    if (!walletCode.equals(WalletTemplate.PRINCIPAL)) {
                        WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode);
                        if (walletTemplate == null) {
                            throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
                        }
                        wallets.add(walletTemplate);
                    }
                }

                ServiceChargeTemplateRecurring serviceChargeTemplate = new ServiceChargeTemplateRecurring();
                serviceChargeTemplate.setChargeTemplate(chargeTemplate);
                serviceChargeTemplate.setWalletTemplates(wallets);
                serviceChargeTemplate.setServiceTemplate(serviceTemplate);
                serviceChargeTemplateRecurringService.create(serviceChargeTemplate);
            }
        }
    }

    private void createServiceChargeTemplateSubscription(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {
        

        if (postData.getServiceChargeTemplateSubscriptions() != null) {
            for (ServiceChargeTemplateSubscriptionDto serviceChargeTemplateDto : postData.getServiceChargeTemplateSubscriptions().getServiceChargeTemplateSubscription()) {
                List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

                OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode());
                if (chargeTemplate == null) {
                    throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, serviceChargeTemplateDto.getCode());
                }

                for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
                    if (!walletCode.equals(WalletTemplate.PRINCIPAL)) {
                        WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode);
                        if (walletTemplate == null) {
                            throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
                        }
                        wallets.add(walletTemplate);
                    }
                }

                ServiceChargeTemplateSubscription serviceChargeTemplate = new ServiceChargeTemplateSubscription();
                serviceChargeTemplate.setChargeTemplate(chargeTemplate);
                serviceChargeTemplate.setWalletTemplates(wallets);
                serviceChargeTemplate.setServiceTemplate(serviceTemplate);
                serviceChargeTemplateSubscriptionService.create(serviceChargeTemplate);
            }
        }
    }

    private void createServiceChargeTemplateTermination(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {
        

        if (postData.getServiceChargeTemplateTerminations() != null) {
            for (ServiceChargeTemplateTerminationDto serviceChargeTemplateDto : postData.getServiceChargeTemplateTerminations().getServiceChargeTemplateTermination()) {
                List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

                OneShotChargeTemplate chargeTemplate = oneShotChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode());
                if (chargeTemplate == null) {
                    throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, serviceChargeTemplateDto.getCode());
                }

                for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
                    if (!walletCode.equals(WalletTemplate.PRINCIPAL)) {
                        WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode);
                        if (walletTemplate == null) {
                            throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
                        }
                        wallets.add(walletTemplate);
                    }
                }

                ServiceChargeTemplateTermination serviceChargeTemplate = new ServiceChargeTemplateTermination();
                serviceChargeTemplate.setChargeTemplate(chargeTemplate);
                serviceChargeTemplate.setWalletTemplates(wallets);
                serviceChargeTemplate.setServiceTemplate(serviceTemplate);
                serviceChargeTemplateTerminationService.create(serviceChargeTemplate);
            }
        }
    }

    private void createServiceChargeTemplateUsage(ServiceTemplateDto postData, ServiceTemplate serviceTemplate) throws MeveoApiException, BusinessException {
        

        if (postData.getServiceChargeTemplateUsages() != null) {
            for (ServiceUsageChargeTemplateDto serviceChargeTemplateDto : postData.getServiceChargeTemplateUsages().getServiceChargeTemplateUsage()) {
                List<WalletTemplate> wallets = new ArrayList<WalletTemplate>();

                UsageChargeTemplate chargeTemplate = usageChargeTemplateService.findByCode(serviceChargeTemplateDto.getCode());
                if (chargeTemplate == null) {
                    throw new EntityDoesNotExistsException(UsageChargeTemplate.class, serviceChargeTemplateDto.getCode());
                }

                for (String walletCode : serviceChargeTemplateDto.getWallets().getWallet()) {
                    if (!walletCode.equals(WalletTemplate.PRINCIPAL)) {
                        WalletTemplate walletTemplate = walletTemplateService.findByCode(walletCode);
                        if (walletTemplate == null) {
                            throw new EntityDoesNotExistsException(WalletTemplate.class, walletCode);
                        }
                        wallets.add(walletTemplate);
                    }
                }

                ServiceChargeTemplateUsage serviceChargeTemplate = new ServiceChargeTemplateUsage();

                // search for counter
                if (!StringUtils.isBlank(serviceChargeTemplateDto.getCounterTemplate())) {
                    CounterTemplate counterTemplate = (CounterTemplate) counterTemplateService.findByCode(serviceChargeTemplateDto.getCounterTemplate());
                    if (counterTemplate == null) {
                        throw new EntityDoesNotExistsException(CounterTemplate.class, serviceChargeTemplateDto.getCounterTemplate());
                    }

                    serviceChargeTemplate.setCounterTemplate(counterTemplate);
                }

                serviceChargeTemplate.setChargeTemplate(chargeTemplate);
                serviceChargeTemplate.setWalletTemplates(wallets);
                serviceChargeTemplate.setServiceTemplate(serviceTemplate);
                serviceUsageChargeTemplateService.create(serviceChargeTemplate);
            }
        }
    }

    public ServiceTemplate create(ServiceTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");            
        }
        
        handleMissingParametersAndValidate(postData);        

        // check if code already exists
        if (serviceTemplateService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(ServiceTemplateService.class, postData.getCode());
        }

        Calendar invoicingCalendar = null;
        if (postData.getInvoicingCalendar() != null) {
            invoicingCalendar = calendarService.findByCode(postData.getInvoicingCalendar());
            if (invoicingCalendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getInvoicingCalendar());
            }
        }
        
    	BusinessServiceModel businessService = null;
		if (!StringUtils.isBlank(postData.getSomCode())) {
			businessService = businessServiceModelService.findByCode(postData.getSomCode());
			if (businessService == null) {
				throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getSomCode());
			}
		}

        ServiceTemplate serviceTemplate = new ServiceTemplate();
        serviceTemplate.setBusinessServiceModel(businessService);
        serviceTemplate.setCode(postData.getCode());
        serviceTemplate.setDescription(postData.getDescription());
        serviceTemplate.setLongDescription(postData.getLongDescription());
        serviceTemplate.setInvoicingCalendar(invoicingCalendar);
        try {
			saveImage(serviceTemplate, postData.getImagePath(), postData.getImageBase64());
		} catch (IOException e1) {
			log.error("Invalid image data={}", e1.getMessage());
			throw new InvalidImageData();
		}

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), serviceTemplate, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        serviceTemplateService.create(serviceTemplate);

        // check for recurring charges
        createServiceChargeTemplateRecurring(postData, serviceTemplate);

        // check for subscription charges
        createServiceChargeTemplateSubscription(postData, serviceTemplate);

        // check for termination charges
        createServiceChargeTemplateTermination(postData, serviceTemplate);

        // check for usage charges
        createServiceChargeTemplateUsage(postData, serviceTemplate);
        
        return serviceTemplate;
    }

    public ServiceTemplate update(ServiceTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        
        handleMissingParametersAndValidate(postData);
        
        // check if code already exists
        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(postData.getCode());
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplateService.class, postData.getCode());
        }
        serviceTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());
        serviceTemplate.setDescription(postData.getDescription());
        serviceTemplate.setLongDescription(postData.getLongDescription());

        Calendar invoicingCalendar = null;
        if (postData.getInvoicingCalendar() != null) {
            invoicingCalendar = calendarService.findByCode(postData.getInvoicingCalendar());
            if (invoicingCalendar == null) {
                throw new EntityDoesNotExistsException(Calendar.class, postData.getInvoicingCalendar());
            }
        }
        serviceTemplate.setInvoicingCalendar(invoicingCalendar);
        
        BusinessServiceModel businessService = null;
		if (!StringUtils.isBlank(postData.getSomCode())) {
			businessService = businessServiceModelService.findByCode(postData.getSomCode());
			if (businessService == null) {
				throw new EntityDoesNotExistsException(BusinessServiceModel.class, postData.getSomCode());
			}
		}
		serviceTemplate.setBusinessServiceModel(businessService);

        setAllWalletTemplatesToNull(serviceTemplate);
        try {
			saveImage(serviceTemplate, postData.getImagePath(), postData.getImageBase64());
		} catch (IOException e1) {
			log.error("Invalid image data={}", e1.getMessage());
			throw new InvalidImageData();
		}

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), serviceTemplate, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        serviceTemplate = serviceTemplateService.update(serviceTemplate);

        serviceChargeTemplateRecurringService.removeByServiceTemplate(serviceTemplate);
        serviceChargeTemplateSubscriptionService.removeByServiceTemplate(serviceTemplate);
        serviceChargeTemplateTerminationService.removeByServiceTemplate(serviceTemplate);
        serviceUsageChargeTemplateService.removeByServiceTemplate(serviceTemplate);

        // check for recurring charges
        createServiceChargeTemplateRecurring(postData, serviceTemplate);

        // check for subscription charges
        createServiceChargeTemplateSubscription(postData, serviceTemplate);

        // check for termination charges
        createServiceChargeTemplateTermination(postData, serviceTemplate);

        // check for usage charges
        createServiceChargeTemplateUsage(postData, serviceTemplate);
        
        return serviceTemplate;
    }

    /* (non-Javadoc)
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public ServiceTemplateDto find(String serviceTemplateCode) throws MeveoApiException {
        return find(serviceTemplateCode, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    public ServiceTemplateDto find(String serviceTemplateCode, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (StringUtils.isBlank(serviceTemplateCode)) {
            missingParameters.add("serviceTemplateCode");
            handleMissingParameters();
        }

        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode);
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
        }
        ServiceTemplateDto result = new ServiceTemplateDto(serviceTemplate, entityToDtoConverter.getCustomFieldsDTO(serviceTemplate, inheritCF));
        return result;
    }
    
    private void setAllWalletTemplatesToNull(ServiceTemplate serviceTemplate) {
        List<ServiceChargeTemplateRecurring> listRec = new ArrayList<ServiceChargeTemplateRecurring>();
        for (ServiceChargeTemplateRecurring recurring : serviceTemplate.getServiceRecurringCharges()) {
            recurring.setWalletTemplates(null);
            listRec.add(recurring);
        }
        serviceTemplate.setServiceRecurringCharges(listRec);

        List<ServiceChargeTemplateSubscription> listSubs = new ArrayList<ServiceChargeTemplateSubscription>();
        for (ServiceChargeTemplateSubscription subscription : serviceTemplate.getServiceSubscriptionCharges()) {
            subscription.setWalletTemplates(null);
            listSubs.add(subscription);
        }
        serviceTemplate.setServiceSubscriptionCharges(listSubs);

        List<ServiceChargeTemplateTermination> listTerms = new ArrayList<ServiceChargeTemplateTermination>();
        for (ServiceChargeTemplateTermination termination : serviceTemplate.getServiceTerminationCharges()) {
            termination.setWalletTemplates(null);
            listTerms.add(termination);
        }
        serviceTemplate.setServiceTerminationCharges(listTerms);

        List<ServiceChargeTemplateUsage> listUsages = new ArrayList<ServiceChargeTemplateUsage>();
        for (ServiceChargeTemplateUsage usage : serviceTemplate.getServiceUsageCharges()) {
            usage.setWalletTemplates(null);
            listUsages.add(usage);
        }
        serviceTemplate.setServiceUsageCharges(listUsages);
    }

    public void remove(String serviceTemplateCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(serviceTemplateCode)) {
            missingParameters.add("serviceTemplateCode");
            handleMissingParameters();
        }

        ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceTemplateCode);
        if (serviceTemplate == null) {
            throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceTemplateCode);
        }

        setAllWalletTemplatesToNull(serviceTemplate);
        
        //deleteImage(serviceTemplate);
        
        serviceTemplateService.remove(serviceTemplate);

    }

    public ServiceTemplate createOrUpdate(ServiceTemplateDto postData) throws MeveoApiException, BusinessException {    	
        if (serviceTemplateService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
    }
}