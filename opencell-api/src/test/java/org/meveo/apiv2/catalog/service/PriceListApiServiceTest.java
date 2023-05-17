package org.meveo.apiv2.catalog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.service.catalog.impl.PriceListService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PriceListApiServiceTest {

    @InjectMocks
    private PriceListApiService priceListApiService;

    @Mock
    private PriceListService priceListService;

    @Test
    public void updateStatus_givenMissingCodeShouldTriggerCode() {
        // given
        var priceListCode = "not-a-code";
        var status = PriceListStatusEnum.ACTIVE;

        // when + then
        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(EntityDoesNotExistsException.class)
                .hasMessageContaining(priceListCode);
    }

    @Test
    public void updateStatus_activate_shouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        PriceListLine priceListLineWithRate = new PriceListLine();
        priceListLineWithRate.setCode(priceListCode + "-01");
        priceListLineWithRate.setRate(BigDecimal.valueOf(100L));
        PriceListLine priceListLineWithPP = new PriceListLine();
        priceListLineWithPP.setCode(priceListCode + "-02");
        PricePlanMatrix pricePlan = new PricePlanMatrix();
        PricePlanMatrixVersion ppv = new PricePlanMatrixVersion();
        ppv.setStatus(VersionStatusEnum.PUBLISHED);
        pricePlan.setVersions(List.of(ppv));
        priceListLineWithPP.setPricePlan(pricePlan);
        priceList.setLines(Set.of(priceListLineWithRate, priceListLineWithPP));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);

    }

    @Test
    public void updateStatus_activate_givenPListWithoutLinesShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Cannot activate PriceList without lines");

    }

    @Test
    public void updateStatus_activate_givenPListLinesWithoutPriceAndPricePlanShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        PriceListLine priceListLineWithRate = new PriceListLine();
        priceListLineWithRate.setCode(priceListCode + "-01");
        priceList.setLines(Set.of(priceListLineWithRate));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Cannot activate PriceList without lines having a price or active PricePlan");

    }

    @Test
    public void updateStatus_activate_givenPListLinesWithoutActivePricePlanShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        PriceListLine priceListLineWithRate = new PriceListLine();
        priceListLineWithRate.setCode(priceListCode + "-01");
        priceList.setLines(Set.of(priceListLineWithRate));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Cannot activate PriceList without lines having a price or active PricePlan");

    }

    @Test
    public void updateStatus_activate_givenActivePriceListShouldNotActivate() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ACTIVE);
        PriceListLine priceListLineWithRate = new PriceListLine();
        priceListLineWithRate.setCode(priceListCode + "-01");
        priceListLineWithRate.setRate(BigDecimal.valueOf(100L));
        PriceListLine priceListLineWithPP = new PriceListLine();
        priceListLineWithPP.setCode(priceListCode + "-02");
        PricePlanMatrix pricePlan = new PricePlanMatrix();
        PricePlanMatrixVersion ppv = new PricePlanMatrixVersion();
        ppv.setStatus(VersionStatusEnum.DRAFT);
        pricePlan.setVersions(List.of(ppv));
        priceListLineWithPP.setPricePlan(pricePlan);
        priceList.setLines(Set.of(priceListLineWithRate, priceListLineWithPP));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Only DRAFT PriceList are eligible to ACTIVE status");

    }

    public void updateStatus_activate_givenPriceListWithoutApplicationRulesShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        PriceListLine priceListLineWithRate = new PriceListLine();
        priceListLineWithRate.setCode(priceListCode + "-01");
        priceListLineWithRate.setRate(BigDecimal.valueOf(100L));
        PriceListLine priceListLineWithPP = new PriceListLine();
        priceListLineWithPP.setCode(priceListCode + "-02");
        PricePlanMatrix pricePlan = new PricePlanMatrix();
        PricePlanMatrixVersion ppv = new PricePlanMatrixVersion();
        ppv.setStatus(VersionStatusEnum.PUBLISHED);
        pricePlan.setVersions(List.of(ppv));
        priceListLineWithPP.setPricePlan(pricePlan);
        priceList.setLines(Set.of(priceListLineWithRate, priceListLineWithPP));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Cannot activate PriceList without application rules");

    }

    @Test
    public void updateStatus_close_shouldClosePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.CLOSED;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ACTIVE);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);
        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.CLOSED);
    }

    @Test
    public void updateStatus_archive_shouldArchivePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ARCHIVED;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // when
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);
        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ARCHIVED);
    }

    @Test
    public void updateStatus_archive_givenActivePriceListShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ARCHIVED;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // when
        priceListApiService.updateStatus(priceListCode, status);

        // when + Then check
        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Only DRAFT PriceList are eligible to ARCHIVED status");
    }

    @Test
    public void updateStatus_givenUnsupportedStatusShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.DRAFT;

        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ACTIVE);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // Then check
        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Unsupported status");
    }
}