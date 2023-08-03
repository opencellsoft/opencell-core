package org.meveo.apiv2.billing;

import static org.junit.Assert.assertEquals;
import static org.meveo.apiv2.billing.ImmutableCdrListInput.builder;
import static org.meveo.model.billing.BillingRunStatusEnum.POSTVALIDATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.billing.service.MediationApiService;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.medina.impl.CDRService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import liquibase.pro.packaged.E;

@RunWith(MockitoJUnitRunner.class)
public class MediationApiServiceTest {

    @Spy
    @InjectMocks
    private MediationApiService mediationApiService;
    
    @Mock
    private CDRService cdrService;
    @Mock
    private AccessService accessService;

    @Mock
    protected ParamBeanFactory paramBeanFactory;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private String ipAddress = "127.0.0.1";

    @Before
    public void setup() {

    }

    @Test
    public void test_with_cdrListInput_null() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("The input params are required");
        mediationApiService.registerCdrList(null, ipAddress);
    }

    @Test
    public void test_with_cdrListInput_empty() {
        expectedEx.expect(BadRequestException.class);
        expectedEx.expectMessage("The cdrs list are required");

        CdrListInput cdrListInput = builder().build();
        mediationApiService.registerCdrList(cdrListInput, ipAddress);
    }

    private CDR createDummyCDR(Long id, Date eventDate, Double quantity, String accessCode, String param1) {
        CDR cdr = new CDR();
        cdr.setEventDate(eventDate);
        if(quantity != null)
            cdr.setQuantity(new BigDecimal(quantity));
        cdr.setAccessCode(accessCode);
        cdr.setParameter1(param1);
        cdr.setId(id);
        return cdr;
    }
    private CDR createDummyCDR(Long id, Date eventDate, Double quantity, String accessCode, String param1, CDRStatusEnum status) {
        CDR cdr = new CDR();
        cdr.setEventDate(eventDate);
        if(quantity != null)
            cdr.setQuantity(new BigDecimal(quantity));
        cdr.setAccessCode(accessCode);
        cdr.setParameter1(param1);
        cdr.setId(id);
        cdr.setStatus(status);
        return cdr;
    }
    
    @Test
    public void test_with_createCdr_ok() {
        List<CDR> cdrs = List.of(
            createDummyCDR(1L, new Date(), 1d, "AccessCode", "param1"),
            createDummyCDR(2L, new Date(), 10d, "AccessCode2", "XPARAM")
                );
        
        List<Access> accesss = new ArrayList<Access>();
        accesss.add(new Access());
        when(accessService.getActiveAccessByUserId(any())).thenReturn(accesss);
        when(cdrService.checkDuplicateCDR(any())).thenReturn(false);
        
        doNothing().when(cdrService).create(any());
        
        CdrDtoResponse response = mediationApiService.createCdr(cdrs, ProcessingModeEnum.PROCESS_ALL, true, true);

        assertEquals(0, response.getErrors().size());;
        
    }

    @Test
    public void test_with_createCdr_mandatory_field_ko() {
        List<CDR> cdrs = List.of(
            createDummyCDR(1L, null, 1d, "AccessCode", "param1"),
            createDummyCDR(2L, null, 10d, "AccessCode2", "XPARAM")
                );
        
        List<Access> accesss = new ArrayList<Access>();
        accesss.add(new Access());
        when(cdrService.checkDuplicateCDR(any())).thenReturn(false);
        
        doNothing().when(cdrService).create(any());
        
        CdrDtoResponse response = mediationApiService.createCdr(cdrs, ProcessingModeEnum.PROCESS_ALL, true, true);

        //assertEquals("missing paramters : [eventDate]", response.getErrors().get(0).getRejectReason());;
        
    }
    
    @Test
    public void test_with_update_ok() {
        List<Access> accesss = new ArrayList<Access>();
        accesss.add(new Access());
        when(accessService.getActiveAccessByUserId(any())).thenReturn(accesss);
        CDR cdr = createDummyCDR(1L, new Date(), 1d, "AccessCode", "param1");
        
        when(cdrService.findById(anyLong())).thenReturn(cdr);
        when(cdrService.update(cdr)).thenReturn(cdr);
        
        
        mediationApiService.updateCDR(1L, cdr);
        
    }
    
    @Test
    public void test_update_status_closed_to_open_KO() {
        List<Access> accesss = new ArrayList<Access>();
        accesss.add(new Access());
        CDR toBeUpdated = createDummyCDR(1L, new Date(), 1d, "AccessCode", "param1");
        toBeUpdated.setStatus(CDRStatusEnum.OPEN);
        CDR cdr = createDummyCDR(1L, new Date(), 1d, "AccessCode", "param1");
        cdr.setStatus(CDRStatusEnum.CLOSED);
        
        when(cdrService.findById(anyLong())).thenReturn(cdr);

        expectedEx.expect(BusinessException.class);
        expectedEx.expectMessage("Impossible to update CDR with the status CLOSED to another status.");

        mediationApiService.updateCDR(1L, toBeUpdated);
    }
    

    @Test
    public void test_update_mandatory_field_KO() {
        List<Access> accesss = new ArrayList<Access>();
        accesss.add(new Access());
        CDR toBeUpdated = createDummyCDR(1L, null, null, "AccessCode", "param1");
        toBeUpdated.setStatus(CDRStatusEnum.OPEN);
        CDR cdr = createDummyCDR(1L, new Date(), 1d, "AccessCode", "param1");
        cdr.setStatus(CDRStatusEnum.OPEN);
        
        when(cdrService.findById(anyLong())).thenReturn(cdr);

        expectedEx.expect(MissingParameterException.class);
        expectedEx.expectMessage("The following parameters are required or contain invalid values: eventDate, quantity.");
        
        
        mediationApiService.updateCDR(1L, toBeUpdated);
    }
    
    @Test
    public void test_with_delete_one_ok() {
        CDR cdr = createDummyCDR(1L, null, null, "AccessCode", "param1");
        cdr.setStatus(CDRStatusEnum.OPEN);

        when(cdrService.findById(anyLong())).thenReturn(cdr);
        
        doNothing().when(cdrService).remove(cdr);
        
        mediationApiService.deleteCdr(1L);
    }

    @Test
    public void test_with_delete_one_ko() {
        CDR cdr = createDummyCDR(1L, null, null, "AccessCode", "param1");
        cdr.setStatus(CDRStatusEnum.PROCESSED);

        when(cdrService.findById(anyLong())).thenReturn(cdr);
        var statusToBeDeleted = Arrays.asList(CDRStatusEnum.OPEN, CDRStatusEnum.TO_REPROCESS, CDRStatusEnum.ERROR, CDRStatusEnum.DISCARDED);

        expectedEx.expect(BusinessException.class);
        expectedEx.expectMessage("Only CDR with status : " + statusToBeDeleted.toString() + " can be deleted");
        
        mediationApiService.deleteCdr(1L);
    }
    

    @Test
    public void test_with_delete_multiple_ok() {
        List<CDR> cdrs = List.of(
            createDummyCDR(1L, null, 1d, "AccessCode", "param1", CDRStatusEnum.OPEN),
            createDummyCDR(2L, null, 10d, "AccessCode2", "XPARAM", CDRStatusEnum.OPEN)
                );

        when(cdrService.findById(anyLong())).thenReturn(cdrs.get(0), cdrs.get(1));
        
        CdrDtoResponse response =  mediationApiService.deleteCdrs(Arrays.asList(1L, 2L), ProcessingModeEnum.PROCESS_ALL, true, true);
        assertEquals(0, response.getErrors().size());
    }
    

    @Test
    public void test_with_delete_multiple_ko() {
        List<CDR> cdrs = List.of(
            createDummyCDR(1L, null, 1d, "AccessCode", "param1", CDRStatusEnum.CLOSED),
            createDummyCDR(2L, null, 10d, "AccessCode2", "XPARAM", CDRStatusEnum.OPEN)
                );

        when(cdrService.findById(anyLong())).thenReturn(cdrs.get(0), cdrs.get(1));
        
        CdrDtoResponse response =  mediationApiService.deleteCdrs(Arrays.asList(1L, 2L), ProcessingModeEnum.PROCESS_ALL, true, true);

        var statusToBeDeleted = Arrays.asList(CDRStatusEnum.OPEN, CDRStatusEnum.TO_REPROCESS, CDRStatusEnum.ERROR, CDRStatusEnum.DISCARDED);
        assertEquals("Only CDR with status : " + statusToBeDeleted.toString() + " can be deleted", response.getErrors().get(0).getRejectReason());
    }
    
}