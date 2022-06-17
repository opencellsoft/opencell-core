package org.meveo.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.cpq.CommercialOrderApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.OrderProductDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.quote.QuoteOfferService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommercialOrderApiTest {
	
	static class CommercialOrderApiMock extends CommercialOrderApi {
        @Override
        protected ICustomFieldEntity populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity) throws MeveoApiException {
        return null;
    }
    }

	@InjectMocks()
    private CommercialOrderApi commercialOrderApi = new CommercialOrderApiMock();

    @Mock
    private AuditLogService auditLogService;
    
    @Mock
    private OfferTemplateService offerTemplateService;
    
    @Mock
    private QuoteOfferService quoteOfferService;
    
    @Mock
    private QuoteVersionService quoteVersionService;
    
    @Mock
    private DiscountPlanService discountPlanService;
    
    @Mock
    private SubscriptionService subscriptionService;
    
    @Mock
    private CommercialOrderService commercialOrderService;
    
    @Mock
    @CurrentUser
    private MeveoUser currentUser;

    @Before
    public void setUp() {
    	OfferTemplate offerTemplate = new OfferTemplate();
    	offerTemplate.setCode("OT1");
        when(commercialOrderService.findById(1L)).thenReturn(new CommercialOrder());
        when(commercialOrderService.findById(2L)).thenReturn(null);
    }

    @Test
    public void populateOrderProduct_withNoQuantity() {
        try {
        	OrderProductDto orderProductDto = new OrderProductDto();
        	OrderOffer orderOffer = new OrderOffer();
        	OrderProduct orderProduct = new OrderProduct();
        	commercialOrderApi.populateOrderProduct(orderProductDto, orderOffer, orderProduct);
        } catch (Exception exception) {
            assertTrue(exception instanceof MeveoApiException);
        }

    }
    
    @Test
    public void populateOrderProduct_withCommercialOrderNotFound() {
        try {
        	OrderProductDto orderProductDto = new OrderProductDto();
        	OrderOffer orderOffer = new OrderOffer();
        	orderProductDto.setCommercialOrderId(2L);
        	OrderProduct orderProduct = new OrderProduct();
        	commercialOrderApi.populateOrderProduct(orderProductDto, orderOffer, orderProduct);
        } catch (Exception exception) {
            assertTrue(exception instanceof EntityDoesNotExistsException);
        }

    }
    
    @Test
    public void populateOrderProduct() {
        
    	OrderProductDto orderProductDto = new OrderProductDto();
    	orderProductDto.setQuantity(new BigDecimal(1));
    	orderProductDto.setCommercialOrderId(1L);
    	OrderOffer orderOffer = new OrderOffer();
    	OrderProduct orderProduct = new OrderProduct();
    	commercialOrderApi.populateOrderProduct(orderProductDto, orderOffer, orderProduct);
    	assertNotNull(orderOffer);
    }
    
}
