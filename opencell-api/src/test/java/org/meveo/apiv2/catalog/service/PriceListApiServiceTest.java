package org.meveo.apiv2.catalog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.enums.PriceVersionTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.model.pricelist.PriceListTypeEnum;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.PriceListLineService;
import org.meveo.service.catalog.impl.PriceListService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PriceListApiServiceTest {

    @InjectMocks
    private PriceListApiService priceListApiService;

    @Mock
    private PriceListService priceListService;

    @Mock
    private PriceListLineService priceListLineService;

    @Mock
    private PricePlanMatrixService pricePlanMatrixService;

    @Mock
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

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

    /*
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
    */

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

    @Test
    public void duplicate_shouldDuplicate() {

        // given an existing priceList
        PriceList existingPLi = new PriceList();
        existingPLi.setCode("a-code");
        existingPLi.setDescription("a-description");
        existingPLi.setStatus(PriceListStatusEnum.ACTIVE);
        existingPLi.setValidFrom(new GregorianCalendar(2023, Calendar.MAY, 1).getTime());
        existingPLi.setValidUntil(new GregorianCalendar(2023, Calendar.MAY, 31).getTime());
        existingPLi.setApplicationStartDate(new GregorianCalendar(2023, Calendar.MAY, 1).getTime());
        existingPLi.setApplicationEndDate(new GregorianCalendar(2023, Calendar.MAY, 31).getTime());

        // Customer Fields
        existingPLi.setCfValue("customField1", "Value 1");
        existingPLi.setCfValue("customField2", "Value 2");
        existingPLi.setCfValue("customField3", "Value 3");

        Seller seller = new Seller();
        seller.setCode("TEST-SELLER");

        existingPLi.setSellers(Set.of(seller));

        OfferTemplateCategory offerTemplateCategory = new OfferTemplateCategory();
        offerTemplateCategory.setCode("OFFER-CATEGORY");
        OfferTemplate offerTemplate = new OfferTemplate();
        offerTemplate.setCode("TEST-OT");
        ProductLine productLine = new ProductLine();
        productLine.setCode("PRODUCT-LINE");
        Product product = new Product();
        product.setCode("TEST-PRODUCT");
        product.setProductLine(productLine);
        UsageChargeTemplate chargeTemplate = new UsageChargeTemplate();
        chargeTemplate.setCode("TEST-CHARGE-TEMPLATE");

        PriceListLine line = new PriceListLine();
        line.setCode(existingPLi.getCode()+"-0");
        line.setPriceList(existingPLi);
        line.setOfferCategory(offerTemplateCategory);
        line.setOfferTemplate(offerTemplate);
        line.setChargeTemplate(chargeTemplate);
        line.setProduct(product);
        line.setProductCategory(productLine);
        line.setPriceListType(PriceListTypeEnum.PERCENTAGE);
        line.setRate(BigDecimal.valueOf(123L));

        existingPLi.setLines(Set.of(line));

        // Price Plan
        PricePlanMatrix pricePlan = new PricePlanMatrix();
        pricePlan.setCode("PPM-CODE");
        PricePlanMatrixVersion ppmv = new PricePlanMatrixVersion();
        ppmv.setLabel("PV_01");
        ppmv.setVersion(1);
        ppmv.setPriceVersionType(PriceVersionTypeEnum.FIXED);

        PricePlanMatrixLine ppml = new PricePlanMatrixLine();
        ppml.setDescription("PPML Description");
        ppmv.setLines(Set.of(ppml));

        pricePlan.setVersions(List.of(ppmv));
        line.setPricePlan(pricePlan);

        when(pricePlanMatrixService.findDuplicateCode(pricePlan)).thenReturn(pricePlan.getCode()+"-COPY");

        PricePlanMatrixVersion duplicatedPPMV = new PricePlanMatrixVersion(ppmv);
        duplicatedPPMV.setStatus(VersionStatusEnum.DRAFT);
        duplicatedPPMV.setPriceVersionType(ppmv.getPriceVersionType());

        when(pricePlanMatrixVersionService.duplicate(any(PricePlanMatrixVersion.class), any(PricePlanMatrix.class), any(), any(VersionStatusEnum.class), any(PriceVersionTypeEnum.class), anyBoolean(), anyInt()))
                .thenReturn(duplicatedPPMV);

        when(priceListService.findByCode(existingPLi.getCode())).thenReturn(existingPLi);

        when(priceListService.findDuplicateCode(existingPLi, "-COPY")).thenReturn(existingPLi.getCode()+"-COPY");

        // when

        priceListApiService.duplicate(existingPLi.getCode());

        ArgumentCaptor<PriceList> saveCaptor = ArgumentCaptor.forClass(PriceList.class);
        verify(priceListService).create(saveCaptor.capture());

        // Then, check result
        assertThat(saveCaptor.getValue()).isNotNull();
        PriceList duplicatedPLi = saveCaptor.getValue();
        assertThat(duplicatedPLi.getCode()).isEqualTo(existingPLi.getCode()+"-COPY");
        assertThat(duplicatedPLi.getDescription()).isEqualTo(existingPLi.getDescription());
        assertThat(duplicatedPLi.getSellers()).hasSize(1).map(Seller::getCode).contains(seller.getCode());
        assertThat(duplicatedPLi.getLines()).hasSize(1);

        // Check Line
        PriceListLine duplicatedLine = duplicatedPLi.getLines()
                .iterator()
                .next();

        assertThat(duplicatedLine.getCode()).isEqualTo(line.getCode()+"-COPY");
        assertThat(duplicatedLine.getOfferCategory()).isNotNull().hasFieldOrPropertyWithValue("code", line.getOfferCategory().getCode());
        assertThat(duplicatedLine.getOfferTemplate()).isNotNull().hasFieldOrPropertyWithValue("code", line.getOfferTemplate().getCode());
        assertThat(duplicatedLine.getProductCategory()).isNotNull().hasFieldOrPropertyWithValue("code", line.getProductCategory().getCode());
        assertThat(duplicatedLine.getProduct()).isNotNull().hasFieldOrPropertyWithValue("code", line.getProduct().getCode());
        assertThat(duplicatedLine.getChargeTemplate()).isNotNull().hasFieldOrPropertyWithValue("code", line.getChargeTemplate().getCode());
        assertThat(duplicatedLine.getAmount()).isEqualTo(line.getAmount());
        assertThat(duplicatedLine.getPriceListType()).isEqualTo(PriceListTypeEnum.PERCENTAGE);

        // Check duplicated Custom Fields
        assertThat(duplicatedPLi.getCfValues()).isNotNull();
        assertThat(duplicatedPLi.getCfValue("customField1")).isEqualTo("Value 1");
        assertThat(duplicatedPLi.getCfValue("customField2")).isEqualTo("Value 2");
        assertThat(duplicatedPLi.getCfValue("customField3")).isEqualTo("Value 3");


        // check priceplan
        assertThat(duplicatedLine.getPricePlan()).isNotNull();
        PricePlanMatrix ppmToCheck = duplicatedLine.getPricePlan();
        assertThat(ppmToCheck.getCode()).isEqualTo(pricePlan.getCode()+"-COPY");
        assertThat(ppmToCheck.getVersions()).isNotEmpty();
        assertThat(ppmToCheck.getVersions().size()).isEqualTo(pricePlan.getVersions().size());
        assertThat(ppmToCheck.getVersions().get(0).getStatus()).isEqualTo(VersionStatusEnum.DRAFT);


    }

    @Test
    public void duplicate_givenMissingPLiShouldDuplicate() {

        // given a fake non existing PLi Code
        String priceListCode = "fake-code";

        // when + then
        assertThatThrownBy(() -> priceListApiService.duplicate(priceListCode)).isInstanceOf(EntityDoesNotExistsException.class);


    }
}