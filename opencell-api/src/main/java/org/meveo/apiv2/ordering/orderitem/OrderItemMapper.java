package org.meveo.apiv2.ordering.orderitem;

import org.meveo.apiv2.NotYetImplementedResource;
import org.meveo.apiv2.ResourceMapper;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.orderItem.ImmutableOrderItem;
import org.meveo.apiv2.ordering.orderItem.ImmutableProductInstance;
import org.meveo.apiv2.ordering.orderItem.ImmutableSubscription;
import org.meveo.apiv2.ordering.orderItem.ProductInstance;
import org.meveo.apiv2.ordering.orderItem.ImmutableServiceInstance;
import org.meveo.apiv2.ordering.product.ProductResource;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.order.Order;
import org.meveo.model.order.OrderItem;
import org.meveo.model.shared.DateUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class OrderItemMapper extends ResourceMapper<org.meveo.apiv2.ordering.orderItem.OrderItem, OrderItem> {

    private static final String CF_CLASSIC_PRICE_PRD = "CF_CLASSIC_PRICE_PRD";

    @Override
    public org.meveo.apiv2.ordering.orderItem.OrderItem toResource(OrderItem entity) {
        if(entity==null){
            return null;
        }
        return ImmutableOrderItem.builder()
                .id(entity.getId())
                .itemId(entity.getItemId())
                .status(entity.getStatus())
                .action(entity.getAction())
                .order(buildImmutableResource(NotYetImplementedResource.class, entity.getOrder()))
                .productInstance(toProductInstancesResources(entity.getProductInstances()))
                .userAccount(buildImmutableResource(NotYetImplementedResource.class, entity.getUserAccount()))
                .subscription(toSubscriptionResource(entity.getSubscription()))
                .build();
    }

    private List<ProductInstance> toProductInstancesResources(List<org.meveo.model.billing.ProductInstance> productInstances) {
        if(productInstances==null){
            return null;
        }
        return productInstances.stream()
                .map(productInstance -> ImmutableProductInstance.builder()
                        .id(productInstance.getId())
                        .code(productInstance.getCode())
                        .product(buildImmutableResource(ProductResource.class, productInstance.getProductTemplate()))
                        .quantity(productInstance.getQuantity().longValue())
                        .productPrice(getProductPrice(productInstance.getProductTemplate()))
                        .seller(ImmutableResource.builder().id(productInstance.getSeller().getId()).build())
                        .build())
                .collect(Collectors.toList());
    }

    private Double getProductPrice(ProductTemplate productTemplate) {
        if(productTemplate!=null){
            return productTemplate.hasCfValue(CF_CLASSIC_PRICE_PRD)? (Double) productTemplate.getCfValue(CF_CLASSIC_PRICE_PRD) : null;
        }
        return null;
    }

    private org.meveo.apiv2.ordering.orderItem.Subscription toSubscriptionResource(Subscription subscription) {
        if(subscription==null){
            return null;
        }
        return ImmutableSubscription.builder()
                .id(subscription.getId())
                .code(subscription.getCode())
                .userAccount(buildImmutableResource(NotYetImplementedResource.class, subscription.getUserAccount()))
                .seller(buildImmutableResource(NotYetImplementedResource.class, subscription.getSeller()))
                .offerTemplate(buildImmutableResource(NotYetImplementedResource.class, subscription.getOffer()))
                .subscriptionDate(DateUtils.formatDateWithPattern(subscription.getSubscriptionDate(), DateUtils.DATE_PATTERN))
                .endAgreementDate(DateUtils.formatDateWithPattern(subscription.getEndAgreementDate(), DateUtils.DATE_PATTERN))
                .serviceInstances(subscription.getServiceInstances().stream()
                        .map(serviceInstance ->
                                ImmutableServiceInstance.builder()
                                        .serviceTemplate(buildImmutableResource(NotYetImplementedResource.class, serviceInstance.getServiceTemplate()))
                                        .build()
                        ).collect(Collectors.toList()))
                .build();
    }

    @Override
    public OrderItem toEntity(org.meveo.apiv2.ordering.orderItem.OrderItem resource) {
        OrderItem orderItem=new OrderItem();
        orderItem.setId(resource.getId());
        orderItem.setItemId(resource.getItemId());
        if(resource.getStatus() != null){
            orderItem.setStatus(resource.getStatus());
        } else {
            orderItem.setStatus(null);
        }

        if(resource.getAction() != null){
            orderItem.setAction(resource.getAction());
        }

        if(resource.getOrder() != null){
            Order order = new Order();
            order.setId(resource.getOrder().getId());
            orderItem.setOrder(order);
        }

        if(resource.getSubscription() != null){
            orderItem.setSubscription(constructSubscription(resource.getSubscription()));
        }

        if(resource.getUserAccount() != null){
            UserAccount userAccount = new UserAccount();
            userAccount.setId(resource.getUserAccount().getId());
            orderItem.setUserAccount(userAccount);
        }
        if(resource.getProductInstance() != null){
            orderItem.setProductInstances(resource.getProductInstance().stream()
                    .map(productInstanceResource -> {
                        org.meveo.model.billing.ProductInstance productInstance = new org.meveo.model.billing.ProductInstance();
                        productInstance.setQuantity(new BigDecimal(productInstanceResource.getQuantity()));
                        productInstance.setCode(productInstanceResource.getCode());
                        if(productInstanceResource.getSeller() != null){
                            Seller seller = new Seller();
                            seller.setId(productInstanceResource.getSeller().getId());
                            productInstance.setSeller(seller);
                        }
                        if(productInstanceResource.getProduct() != null){
                            ProductTemplate productTemplate = new ProductTemplate();
                            productTemplate.setId(productInstanceResource.getProduct().getId());
                            productInstance.setProductTemplate(productTemplate);
                            //productInstance.setCfValue("CF_CLASSIC_PRICE_PRD", new Double(productInstanceResource.getProductPrice()));
                        }
                        return productInstance;
                    })
                    .collect(Collectors.toList()));
        }

        return orderItem;
    }

    private Subscription constructSubscription(org.meveo.apiv2.ordering.orderItem.Subscription resourceSubscription) {
        Subscription subscription = new Subscription();

        subscription.setId(resourceSubscription.getId());
        subscription.setCode(resourceSubscription.getCode());

        subscription.setSubscriptionDate(DateUtils.parseDateWithPattern(resourceSubscription.getSubscriptionDate(), DateUtils.DATE_PATTERN));
        subscription.setEndAgreementDate(DateUtils.parseDateWithPattern(resourceSubscription.getEndAgreementDate(), DateUtils.DATE_PATTERN));

        if(resourceSubscription.getOfferTemplate() != null){
            OfferTemplate offer = new OfferTemplate();
            offer.setId(resourceSubscription.getOfferTemplate().getId());
            subscription.setOffer(offer);
        }
        if(resourceSubscription.getUserAccount() != null){
            UserAccount userAccount = new UserAccount();
            userAccount.setId(resourceSubscription.getUserAccount().getId());
            subscription.setUserAccount(userAccount);
        }
        if(resourceSubscription.getSeller() != null){
            Seller seller = new Seller();
            seller.setId(resourceSubscription.getSeller().getId());
            subscription.setSeller(seller);
        }

        if(resourceSubscription.getServiceInstances() != null){
        subscription.setServiceInstances(resourceSubscription.getServiceInstances().stream()
                .map(resourceService -> {
                    ServiceInstance serviceInstance = new ServiceInstance();
                    ServiceTemplate serviceTemplate = new ServiceTemplate();
                    serviceTemplate.setId(resourceService.getServiceTemplate().getId());
                    serviceInstance.setServiceTemplate(serviceTemplate);
                    return serviceInstance;
                })
                .collect(Collectors.toList()));
        }
        return subscription;
    }
}
