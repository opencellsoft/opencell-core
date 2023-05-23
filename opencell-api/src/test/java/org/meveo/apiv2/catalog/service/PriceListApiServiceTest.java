package org.meveo.apiv2.catalog.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.PriceListService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

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
    public void updateStatus_activate_givenSellerAsApplicatioNRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setSellers(Set.of(new Seller()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenCustomerCategoryAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setCustomerCategories(Set.of(new CustomerCategory()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenBrandAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setBrands(Set.of(new CustomerBrand()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenCreditCategoryAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setCreditCategories(Set.of(new CreditCategory()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenCountriesAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setCountries(Set.of(new Country()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenCurrencyAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setCurrencies(Set.of(new Currency()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenLegalEntityAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setLegalEntities(Set.of(new Title()));

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus
        priceListApiService.updateStatus(priceListCode, status);

        // Then check
        verify(priceListService).findByCode(priceListCode);

        assertThat(priceList.getStatus()).isEqualTo(PriceListStatusEnum.ACTIVE);
    }

    @Test
    public void updateStatus_activate_givenPaymentMethodAsApplicationRuleShouldActivatePriceList() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);
        priceList.setPaymentMethods(Set.of(new CheckPaymentMethod()));

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

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.ACTIVE, VersionStatusEnum.DRAFT);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Only DRAFT PriceList are eligible to ACTIVE status");

    }

    @Test
    public void updateStatus_activate_givenPriceListWithoutApplicationRulesShouldTriggerError() {

        // Given valid priceListCode and status
        var priceListCode = "myPriceListCode";
        var status = PriceListStatusEnum.ACTIVE;

        PriceList priceList = newPriceList(priceListCode, PriceListStatusEnum.DRAFT, VersionStatusEnum.PUBLISHED);

        when(priceListService.findByCode(anyString())).thenReturn(priceList);

        // When updateStatus

        assertThatThrownBy(() -> priceListApiService.updateStatus(priceListCode, status))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("Cannot activate PriceList without application rules");

    }

    private static PriceList newPriceList(String priceListCode, PriceListStatusEnum draft, VersionStatusEnum published) {
        var priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(draft);
        PriceListLine priceListLineWithRate = new PriceListLine();
        priceListLineWithRate.setCode(priceListCode + "-01");
        priceListLineWithRate.setRate(BigDecimal.valueOf(100L));
        PriceListLine priceListLineWithPP = new PriceListLine();
        priceListLineWithPP.setCode(priceListCode + "-02");
        PricePlanMatrix pricePlan = new PricePlanMatrix();
        PricePlanMatrixVersion ppv = new PricePlanMatrixVersion();
        ppv.setStatus(published);
        pricePlan.setVersions(List.of(ppv));
        priceListLineWithPP.setPricePlan(pricePlan);
        priceList.setLines(Set.of(priceListLineWithRate, priceListLineWithPP));
        return priceList;
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