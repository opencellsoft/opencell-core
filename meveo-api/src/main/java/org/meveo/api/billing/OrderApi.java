package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.slf4j.Logger;
import org.tmf.dsmapi.catalog.resource.order.OrderItem;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@Stateless
public class OrderApi {

    @Inject
    private Logger log;

    @Inject
    private SubscriptionService subscriptionService;
    @Inject
    private OfferTemplateService offerTemplateService;
    @Inject
    private UserAccountService userAccountService;
    @Inject
    private ServiceInstanceService serviceInstanceService;
    @Inject
    private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

    public ProductOrder createProductOrder(ProductOrder productOrder, User currentUser) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
            BusinessException, EntityDoesNotExistsException, Exception {
        // Validate order

        return null;

        //
        //
        // List<OrderItem> orders = productOrder.getOrderItem();
        // if (orders != null) {
        // Provider provider = currentUser.getProvider();
        // for (OrderItem orderItem : orders) {
        // if (!StringUtils.isBlank(orderItem.getId()) && orderItem.getBillingAccount() != null && orderItem.getBillingAccount().size() == 1
        // && !StringUtils.isBlank(orderItem.getProductOffering()) && !StringUtils.isBlank(orderItem.getProductOffering().getId())
        // && !StringUtils.isBlank(productOrder.getOrderDate()) && !StringUtils.isBlank(productOrder.getDescription())) {
        // String billingAccountId = orderItem.getBillingAccount().get(0).getId();
        // if (billingAccountId == null) {
        // throw new MeveoApiException("orderitem's billingAccount is null");
        // }
        // UserAccount userAccount = userAccountService.findByCode(billingAccountId, provider);
        // if (userAccount == null) {
        // throw new EntityDoesNotExistsException(UserAccount.class, billingAccountId);
        // }
        // log.debug("find userAccount by {}", billingAccountId);
        //
        // OfferTemplate offerTemplate = offerTemplateService.findByCode(orderItem.getProductOffering().getId(), provider);
        // if (offerTemplate == null) {
        // throw new EntityDoesNotExistsException(OfferTemplate.class, orderItem.getProductOffering().getId());
        // }
        // log.debug("find offerTemplate by {}", orderItem.getProductOffering().getId());
        // Subscription subscription = subscriptionService.findByCode(orderItem.getId(), provider);
        // log.debug("find subscription {}", subscription);
        //
        // if (subscription == null) {// sub is new
        // subscription = new Subscription();
        // subscription.setCode(orderItem.getId());
        // subscription.setDescription(orderItem.getAppointment());
        // subscription.setUserAccount(userAccount);
        // subscription.setOffer(offerTemplate);
        // Calendar calendar = Calendar.getInstance();
        // calendar.setTime(productOrder.getOrderDate());
        // calendar.set(Calendar.HOUR_OF_DAY, 0);
        // calendar.set(Calendar.MINUTE, 0);
        // calendar.set(Calendar.SECOND, 0);
        // calendar.set(Calendar.MILLISECOND, 0);
        //
        // subscription.setSubscriptionDate(calendar.getTime());
        //
        // subscriptionService.create(subscription, currentUser, provider);
        // // instantiate
        // // activate
        // instanciationAndActiveService(subscription, orderItem, currentUser);
        // } else {
        //
        // if (!subscription.getUserAccount().getCode().equalsIgnoreCase(orderItem.getBillingAccount().get(0).getId())) {
        // throw new MeveoApiException("Sub's userAccount doesn't match with orderitem's billingAccount");
        // }
        // if (!subscription.getOffer().getCode().equalsIgnoreCase(orderItem.getProductOffering().getId())) {
        // throw new MeveoApiException("Sub's offer doesn't match with orderitem's productOffer");
        // }
        // List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
        // if (serviceInstances != null) {
        // for (ServiceInstance serviceInstance : serviceInstances) {
        // if (!validateServiceInstance(serviceInstance, orderItem)) {
        // terminateService(serviceInstance, productOrder.getOrderDate(), orderItem, currentUser);
        // }
        // }
        // }
        // instanciationAndActiveService(subscription, orderItem, currentUser);
        // }
        // }
        // }
        // }
    }

    private List<ProductCharacteristic> getProductCharacteristic(OrderItem orderItem) {
        List<ProductCharacteristic> productCharacteristic = null;
        Product product = orderItem.getProduct();
        if (!StringUtils.isBlank(product)) {
            productCharacteristic = product.getProductCharacteristic();
        }
        return productCharacteristic;
    }

    private ProductCharacteristic findProductCharacteristic(ServiceInstance serviceInstance, OrderItem orderItem) {
        ProductCharacteristic result = null;
        List<ProductCharacteristic> productCharacteristic = getProductCharacteristic(orderItem);
        if (productCharacteristic != null) {
            for (ProductCharacteristic c : productCharacteristic) {
                if (serviceInstance.getCode().equalsIgnoreCase(c.getValue())) {
                    result = c;
                    break;
                }
            }
        }
        return result;
    }

    private boolean validateServiceInstance(ServiceInstance serviceInstance, OrderItem orderItem) {
        ProductCharacteristic productCharacteristic = findProductCharacteristic(serviceInstance, orderItem);
        return productCharacteristic != null && "service".equalsIgnoreCase(productCharacteristic.getName());
    }

    private String getTerminationReason(OrderItem orderItem) {
        String result = null;
        Product product = orderItem.getProduct();
        if (product != null) {
            List<ProductCharacteristic> productCharacteristic = orderItem.getProduct().getProductCharacteristic();
            if (productCharacteristic != null) {
                for (ProductCharacteristic c : productCharacteristic) {
                    if (!StringUtils.isBlank(c.getName()) && c.getName().equalsIgnoreCase("terminationReason")) {
                        result = c.getValue();
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void terminateService(ServiceInstance selectedServiceInstance, Date terminationDate, OrderItem orderItem, User currentUser) throws IncorrectSusbcriptionException,
            IncorrectServiceInstanceException, BusinessException, Exception {

        if (selectedServiceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
            String terminationReason = getTerminationReason(orderItem);
            if (StringUtils.isBlank(terminationReason)) {
                throw new MeveoApiException("terminationReasion is null");
            }
            if (StringUtils.isBlank(terminationDate)) {
                throw new MeveoApiException("terminationDate is null");
            }
            SubscriptionTerminationReason subscriptionTerminationReason = subscriptionTerminationReasonService.findByCodeReason(terminationReason, currentUser.getProvider());
            if (subscriptionTerminationReason == null) {
                throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, terminationReason);
            }
            serviceInstanceService.terminateService(selectedServiceInstance, terminationDate, subscriptionTerminationReason, currentUser);
        }
    }

    private void activateService(ServiceInstance selectedServiceInstance, User currentUser) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
            BusinessException {
        if (selectedServiceInstance.getStatus() != InstanceStatusEnum.ACTIVE && selectedServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
            serviceInstanceService.serviceActivation(selectedServiceInstance, null, null, currentUser);
        }
    }

    private void instanciationAndActiveService(Subscription subscription, OrderItem orderItem, User currentUser) throws IncorrectSusbcriptionException,
            IncorrectServiceInstanceException, BusinessException {
        if (orderItem.getProduct() != null && orderItem.getProduct().getProductCharacteristic() != null) {
            List<ProductCharacteristic> productCharacteristic = orderItem.getProduct().getProductCharacteristic();
            for (ProductCharacteristic c : productCharacteristic) {
                if ("service".equalsIgnoreCase(c.getName()) && !StringUtils.isBlank(c.getValue())) {
                    ServiceInstance serviceInstance = serviceInstanceService.findActivatedByCodeAndSubscription(c.getValue(), subscription);
                    if (serviceInstance == null) {
                        ServiceTemplate serviceTemplate = findServiceTemplateByCode(subscription.getOffer(), c.getValue());
                        if (serviceTemplate != null) {
                            serviceInstance = new ServiceInstance();
                            serviceInstance.setProvider(serviceTemplate.getProvider());
                            serviceInstance.setCode(serviceTemplate.getCode());
                            serviceInstance.setDescription(serviceTemplate.getDescription());
                            serviceInstance.setServiceTemplate(serviceTemplate);
                            serviceInstance.setSubscription(subscription);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            serviceInstance.setSubscriptionDate(calendar.getTime());
                            serviceInstance.setQuantity(new BigDecimal(1));
                            serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);
                        }
                    }
                    if (serviceInstance != null)
                        activateService(serviceInstance, currentUser);
                }
            }
        }
    }

    private ServiceTemplate findServiceTemplateByCode(OfferTemplate offerTemplate, String serviceCode) {
        List<OfferServiceTemplate> serviceTemplates = offerTemplate.getOfferServiceTemplates();
        if (serviceTemplates != null) {
            for (OfferServiceTemplate serviceTemplate : serviceTemplates) {
                if (serviceTemplate.getServiceTemplate().getCode().equalsIgnoreCase(serviceCode)) {
                    return serviceTemplate.getServiceTemplate();
                }
            }
        }
        return null;
    }

    public ProductOrder getProductOrder(String orderId, User currentUser) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ProductOrder> findProductOrders(Map<String, List<String>> filterCriteria, User currentUser) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProductOrder updatePartiallyProductOrder(ProductOrder productOrder, User currentUser) {
        // TODO Auto-generated method stub
        return null;
    }

    public void deleteProductOrder(String orderId, User currentUser) {
        // TODO Auto-generated method stub

    }

    public ProductOrder createProductOrderOld(ProductOrder productOrder, User currentUser) throws IncorrectSusbcriptionException, IncorrectServiceInstanceException,
            BusinessException, EntityDoesNotExistsException, Exception {
        List<OrderItem> orders = productOrder.getOrderItem();
        if (orders != null) {
            Provider provider = currentUser.getProvider();
            for (OrderItem orderItem : orders) {
                if (!StringUtils.isBlank(orderItem.getId()) && orderItem.getBillingAccount() != null && orderItem.getBillingAccount().size() == 1
                        && !StringUtils.isBlank(orderItem.getProductOffering()) && !StringUtils.isBlank(orderItem.getProductOffering().getId())
                        && !StringUtils.isBlank(productOrder.getOrderDate()) && !StringUtils.isBlank(productOrder.getDescription())) {
                    String billingAccountId = orderItem.getBillingAccount().get(0).getId();
                    if (billingAccountId == null) {
                        throw new MeveoApiException("orderitem's billingAccount is null");
                    }
                    UserAccount userAccount = userAccountService.findByCode(billingAccountId, provider);
                    if (userAccount == null) {
                        throw new EntityDoesNotExistsException(UserAccount.class, billingAccountId);
                    }
                    log.debug("find userAccount by {}", billingAccountId);

                    OfferTemplate offerTemplate = offerTemplateService.findByCode(orderItem.getProductOffering().getId(), provider);
                    if (offerTemplate == null) {
                        throw new EntityDoesNotExistsException(OfferTemplate.class, orderItem.getProductOffering().getId());
                    }
                    log.debug("find offerTemplate by {}", orderItem.getProductOffering().getId());
                    Subscription subscription = subscriptionService.findByCode(orderItem.getId(), provider);
                    log.debug("find subscription {}", subscription);

                    if (subscription == null) {// sub is new
                        subscription = new Subscription();
                        subscription.setCode(orderItem.getId());
                        subscription.setDescription(orderItem.getAppointment());
                        subscription.setUserAccount(userAccount);
                        subscription.setOffer(offerTemplate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(productOrder.getOrderDate());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        subscription.setSubscriptionDate(calendar.getTime());

                        subscriptionService.create(subscription, currentUser, provider);
                        // instantiate
                        // activate
                        instanciationAndActiveService(subscription, orderItem, currentUser);
                    } else {

                        if (!subscription.getUserAccount().getCode().equalsIgnoreCase(orderItem.getBillingAccount().get(0).getId())) {
                            throw new MeveoApiException("Sub's userAccount doesn't match with orderitem's billingAccount");
                        }
                        if (!subscription.getOffer().getCode().equalsIgnoreCase(orderItem.getProductOffering().getId())) {
                            throw new MeveoApiException("Sub's offer doesn't match with orderitem's productOffer");
                        }
                        List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
                        if (serviceInstances != null) {
                            for (ServiceInstance serviceInstance : serviceInstances) {
                                if (!validateServiceInstance(serviceInstance, orderItem)) {
                                    terminateService(serviceInstance, productOrder.getOrderDate(), orderItem, currentUser);
                                }
                            }
                        }
                        instanciationAndActiveService(subscription, orderItem, currentUser);
                    }
                }
            }
        }
        return productOrder;
    }
}