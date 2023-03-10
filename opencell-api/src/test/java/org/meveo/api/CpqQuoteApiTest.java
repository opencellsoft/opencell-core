package org.meveo.api;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.billing.CpqQuoteApi;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.QuoteOfferDTO;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.QuoteVersionService;
import org.meveo.service.quote.QuoteOfferService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CpqQuoteApiTest {
	
	static class CpqQuoteApiMock extends CpqQuoteApi {
        @Override
        protected ICustomFieldEntity populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity) throws MeveoApiException {
        return null;
    }
    }

	@InjectMocks()
    private CpqQuoteApi cpqQuoteApi = new CpqQuoteApiMock();

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

    @Before
    public void setUp() {
    	OfferTemplate offerTemplate = new OfferTemplate();
    	offerTemplate.setCode("OT1");
        when(offerTemplateService.findById(1L)).thenReturn(offerTemplate);
        when(quoteVersionService.findByQuoteAndVersion("QC1", 1)).thenReturn(new QuoteVersion());
        when(discountPlanService.findByCode("dp1")).thenReturn(new DiscountPlan());
        when(subscriptionService.findByCode("s")).thenReturn(new Subscription());  
        when(quoteOfferService.findById(1L)).thenReturn(new QuoteOffer());
    }

    @Test
    public void createQuoteItem_withMissingParameters() {
        try {
        	cpqQuoteApi.createQuoteItem(new QuoteOfferDTO());
        } catch (Exception exception) {
            assertTrue(exception instanceof MissingParameterException);
        }

    }
    
    @Test
    public void createQuoteItem_withOfferIDOfferCodeIncoherent() {
        try {
        	
        	QuoteOfferDTO quoteOfferDTO = new QuoteOfferDTO();
        	quoteOfferDTO.setQuoteVersion(1);
        	quoteOfferDTO.setQuoteCode("QC1");
        	quoteOfferDTO.setOfferCode("OF1");
        	quoteOfferDTO.setOfferId(1L);
        	cpqQuoteApi.createQuoteItem(quoteOfferDTO);
        } catch (Exception exception) {
            assertTrue(exception instanceof MeveoApiException);
        }

    }
    
    @Test
    public void createQuoteItem_withOfferTemplateNotFound() {
        try {
        	
        	QuoteOfferDTO quoteOfferDTO = new QuoteOfferDTO();
        	quoteOfferDTO.setQuoteVersion(1);
        	quoteOfferDTO.setQuoteCode("QC1");
        	quoteOfferDTO.setOfferCode("OF1");
        	quoteOfferDTO.setOfferId(2L);
        	cpqQuoteApi.createQuoteItem(quoteOfferDTO);
        } catch (Exception exception) {
            assertTrue(exception instanceof EntityDoesNotExistsException);
        }

    }
    
    @Test
    public void createQuoteItem_withQuoteVersionNotFound() {
        try {
        	
        	QuoteOfferDTO quoteOfferDTO = new QuoteOfferDTO();
        	quoteOfferDTO.setQuoteVersion(2);
        	quoteOfferDTO.setQuoteCode("QC1");
        	quoteOfferDTO.setOfferCode("OT1");
        	quoteOfferDTO.setOfferId(1L);
        	cpqQuoteApi.createQuoteItem(quoteOfferDTO);
        } catch (Exception exception) {
            assertTrue(exception instanceof EntityDoesNotExistsException);
        }

    }
    
    @Test
    public void createQuoteItem() {
      try {
        	QuoteOfferDTO quoteOfferDTO = new QuoteOfferDTO();
        	quoteOfferDTO.setQuoteVersion(1);
        	quoteOfferDTO.setQuoteCode("QC1");
        	quoteOfferDTO.setOfferCode("OT1");
        	quoteOfferDTO.setOfferId(1L);
        	quoteOfferDTO.setQuoteLineType(OfferLineTypeEnum.AMEND);
        	quoteOfferDTO.setDiscountPlanCode("dp1");
        	quoteOfferDTO.setSubscriptionCode("s");
        	cpqQuoteApi.createQuoteItem(quoteOfferDTO);
        	verify(quoteOfferService).create(any());
      }catch(Exception e) {
          assertTrue(e instanceof MissingParameterException);
      }

    }
    
    //@Test
    public void updateQuoteItem() {
        try {	
        	QuoteOfferDTO quoteOfferDTO = new QuoteOfferDTO();
        	quoteOfferDTO.setQuoteOfferId(1L);
        	quoteOfferDTO.setQuoteVersion(1);
        	//quoteOfferDTO.setQuoteCode("QC1");
        	quoteOfferDTO.setOfferCode("OT1");
        	quoteOfferDTO.setOfferId(1L);
        	quoteOfferDTO.setQuoteLineType(OfferLineTypeEnum.AMEND);
        	quoteOfferDTO.setDiscountPlanCode("dp1");
        	quoteOfferDTO.setSubscriptionCode("s");
        	cpqQuoteApi.updateQuoteItem(quoteOfferDTO);
        	verify(quoteOfferService).update(any());
        }catch(Exception e) {
            assertTrue(e instanceof MissingParameterException);
        }

    }
}
