package org.meveo.apiv2.catalog.service.pricelist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.ImmutablePriceListLineDto;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PriceListLineService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PriceListLineApiServiceTest {

    @InjectMocks
    private PriceListLineApiService priceListLineApiService;

    @Mock
    private PriceListService priceListService;

    @Mock
    private PriceListLineService priceListLineService;

    @Mock
    private OfferTemplateService offerTemplateService;

    @Mock
    private OfferTemplateCategoryService offerTemplateCategoryService;

    @Mock
    private ProductService productService;

    @Mock
    private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

    @Mock
    private ProductLineService productLineService;

    @Mock
    private CustomFieldTemplateService customFieldTemplateService;

    @Test
    public void create_shouldCreate() {
        // given
        var priceListCode = "a-price-list";
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .customFields(new CustomFieldsDto())
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        when(priceListService.findByCode(givenDto.getPriceListCode())).thenReturn(priceList);
        when(offerTemplateService.findByCode(givenDto.getOfferTemplateCode())).thenReturn(new OfferTemplate());
        when(offerTemplateCategoryService.findByCode(givenDto.getOfferCategoryCode())).thenReturn(new OfferTemplateCategory());
        when(productService.findByCode(givenDto.getProductCode())).thenReturn(new Product());
        when(chargeTemplateService.findByCode(givenDto.getChargeTemplateCode())).thenReturn(new UsageChargeTemplate());
        when(productLineService.findByCode(givenDto.getProductCategoryCode())).thenReturn(new ProductLine());

        // when
        priceListLineApiService.create(givenDto);

        // then
        ArgumentCaptor<PriceListLine> pllCaptor = ArgumentCaptor.forClass(PriceListLine.class);
        verify(priceListLineService).create(pllCaptor.capture());

        assertThat(pllCaptor.getValue()).isNotNull();
        PriceListLine entityToCheck = pllCaptor.getValue();

        assertThat(entityToCheck.getCode()).isEqualTo(priceListCode+"-1");
        assertThat(entityToCheck.getPriceList()).isNotNull();
        assertThat(entityToCheck.getPriceList()
                                .getCode()).isEqualTo(priceListCode);
        assertThat(entityToCheck.getOfferCategory()).isNotNull();
        assertThat(entityToCheck.getOfferTemplate()).isNotNull();
        assertThat(entityToCheck.getProduct()).isNotNull();
        assertThat(entityToCheck.getProductCategory()).isNotNull();
        assertThat(entityToCheck.getChargeTemplate()).isNotNull();
        assertThat(entityToCheck.getApplicationEl()).isEqualTo(givenDto.getApplicationEl());
        assertThat(entityToCheck.getAmount()).isEqualTo(givenDto.getAmount());
        assertThat(entityToCheck.getRate().doubleValue()).isEqualTo(givenDto.getRate());
        assertThat(entityToCheck.getDescription()).isEqualTo(givenDto.getDescription());
    }

    @Test
    public void create_givenActivePriceListShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .customFields(new CustomFieldsDto())
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ACTIVE);
        when(priceListService.findByCode(givenDto.getPriceListCode())).thenReturn(priceList);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.create(givenDto))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be created for PriceList status other than DRAFT");
    }

    @Test
    public void create_givenClosedPriceListShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .customFields(new CustomFieldsDto())
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.CLOSED);
        when(priceListService.findByCode(givenDto.getPriceListCode())).thenReturn(priceList);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.create(givenDto))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be created for PriceList status other than DRAFT");
    }

    @Test
    public void create_givenArchivedPriceListShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .customFields(new CustomFieldsDto())
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ARCHIVED);
        when(priceListService.findByCode(givenDto.getPriceListCode())).thenReturn(priceList);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.create(givenDto))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be created for PriceList status other than DRAFT");
    }


    @Test
    public void create_givenMissingMandatoryFieldsShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .build();

        // when
        assertThatThrownBy(() -> priceListLineApiService.create(givenDto))
                .isInstanceOf(MissingParameterException.class)
                .hasMessageContaining("priceListCode")
                .hasMessageContaining("chargeTemplateCode");

    }

    @Test
    public void update_shouldUpdate() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("a-price-list-line-code")
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        when(offerTemplateService.findByCode(givenDto.getOfferTemplateCode())).thenReturn(new OfferTemplate());
        when(offerTemplateCategoryService.findByCode(givenDto.getOfferCategoryCode())).thenReturn(new OfferTemplateCategory());
        when(productService.findByCode(givenDto.getProductCode())).thenReturn(new Product());
        when(chargeTemplateService.findByCode(givenDto.getChargeTemplateCode())).thenReturn(new UsageChargeTemplate());
        when(productLineService.findByCode(givenDto.getProductCategoryCode())).thenReturn(new ProductLine());

        PriceListLine priceListLineToUpdate = new PriceListLine();
        priceListLineToUpdate.setCode("e-price-list-line-code");
        priceListLineToUpdate.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToUpdate);

        // when
        priceListLineApiService.update(priceListLineId, givenDto);


        // then
        verify(priceListLineService).findById(priceListLineId);

        assertThat(priceListLineToUpdate.getCode()).isEqualTo(givenDto.getCode());
        assertThat(priceListLineToUpdate.getOfferCategory()).isNotNull();
        assertThat(priceListLineToUpdate.getOfferTemplate()).isNotNull();
        assertThat(priceListLineToUpdate.getProduct()).isNotNull();
        assertThat(priceListLineToUpdate.getProductCategory()).isNotNull();
        assertThat(priceListLineToUpdate.getChargeTemplate()).isNotNull();
        assertThat(priceListLineToUpdate.getApplicationEl()).isEqualTo(givenDto.getApplicationEl());
        assertThat(priceListLineToUpdate.getAmount()).isEqualTo(givenDto.getAmount());
        assertThat(priceListLineToUpdate.getRate().doubleValue()).isEqualTo(givenDto.getRate());
        assertThat(priceListLineToUpdate.getDescription()).isEqualTo(givenDto.getDescription());
    }

    @Test
    public void update_givenActivePriceListShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("a-price-list-line-code")
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ACTIVE);

        PriceListLine priceListLineToUpdate = new PriceListLine();
        priceListLineToUpdate.setCode("e-price-list-line-code");
        priceListLineToUpdate.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToUpdate);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.update(priceListLineId, givenDto))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be updated for PriceList status other than DRAFT");

    }

    @Test
    public void update_givenClosedPriceListShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("a-price-list-line-code")
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.CLOSED);

        PriceListLine priceListLineToUpdate = new PriceListLine();
        priceListLineToUpdate.setCode("e-price-list-line-code");
        priceListLineToUpdate.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToUpdate);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.update(priceListLineId, givenDto))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be updated for PriceList status other than DRAFT");

    }

    @Test
    public void update_givenArchivedPriceListShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("a-price-list-line-code")
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.ARCHIVED);

        PriceListLine priceListLineToUpdate = new PriceListLine();
        priceListLineToUpdate.setCode("e-price-list-line-code");
        priceListLineToUpdate.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToUpdate);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.update(priceListLineId, givenDto))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be updated for PriceList status other than DRAFT");

    }

    @Test
    public void update_givenSlimDtoShouldEmptyAllOptionalFields() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("a-price-list-line-code")
                                                .chargeTemplateCode("a-charge-template")
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        when(chargeTemplateService.findByCode(givenDto.getChargeTemplateCode())).thenReturn(new UsageChargeTemplate());

        PriceListLine priceListLineToUpdate = new PriceListLine();
        priceListLineToUpdate.setDescription("e-description");
        priceListLineToUpdate.setCode("e-price-list-line-code");
        priceListLineToUpdate.setRate(BigDecimal.valueOf(123));
        priceListLineToUpdate.setAmount(BigDecimal.valueOf(123));
        priceListLineToUpdate.setPriceList(new PriceList());
        priceListLineToUpdate.setProduct(new Product());
        priceListLineToUpdate.setProductCategory(new ProductLine());
        priceListLineToUpdate.setChargeTemplate(new RecurringChargeTemplate());
        priceListLineToUpdate.setOfferTemplate(new OfferTemplate());
        priceListLineToUpdate.setOfferCategory(new OfferTemplateCategory());
        priceListLineToUpdate.setApplicationEl("e-application-el");
        priceListLineToUpdate.setPriceList(priceList);


        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToUpdate);

        // when
        priceListLineApiService.update(priceListLineId, givenDto);


        // then
        verify(priceListLineService).findById(priceListLineId);

        assertThat(priceListLineToUpdate.getCode()).isEqualTo(givenDto.getCode());
        assertThat(priceListLineToUpdate.getOfferCategory()).isNull();
        assertThat(priceListLineToUpdate.getOfferTemplate()).isNull();
        assertThat(priceListLineToUpdate.getProduct()).isNull();
        assertThat(priceListLineToUpdate.getProductCategory()).isNull();
        assertThat(priceListLineToUpdate.getApplicationEl()).isNull();
        assertThat(priceListLineToUpdate.getAmount()).isNull();
        assertThat(priceListLineToUpdate.getRate()).isNull();
        assertThat(priceListLineToUpdate.getDescription()).isNull();
        assertThat(priceListLineToUpdate.getChargeTemplate()).isNotNull();
    }

    @Test
    public void update_givenIncorrectPriceListLineCodeShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("a-price-list-line-code")
                                                .description("a-description")
                                                .applicationEl("an-application-el")
                                                .offerCategoryCode("an-offer-category")
                                                .offerTemplateCode("an-offer-template")
                                                .productCode("a-product")
                                                .productCategoryCode("a-product-category")
                                                .chargeTemplateCode("a-charge-template")
                                                .rate(123.456d)
                                                .amount(BigDecimal.valueOf(123456.789))
                                                .pricePlanCode("a-price-plan-code")
                                                .build();

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.update(priceListLineId, givenDto))
                .isInstanceOf(EntityDoesNotExistsException.class)
                .hasMessageContaining(PriceListLine.class.getSimpleName())
                .hasMessageContaining(String.valueOf(priceListLineId));
    }

    @Test
    public void update_givenAnExistingNewPriceListLineCodeShouldTriggerError() {
        // given
        var priceListCode = "a-price-list";
        var priceListLineId = 10001L;
        var givenDto = ImmutablePriceListLineDto.builder()
                                                .priceListCode(priceListCode)
                                                .code("an-existing-pli-line-code")
                                                .chargeTemplateCode("a-charge-template")
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
        priceList.setStatus(PriceListStatusEnum.DRAFT);

        PriceListLine priceListLineToUpdate = new PriceListLine();
        priceListLineToUpdate.setDescription("e-description");
        priceListLineToUpdate.setCode("an-old-pli-line-code");
        priceListLineToUpdate.setRate(BigDecimal.valueOf(123));
        priceListLineToUpdate.setAmount(BigDecimal.valueOf(123));
        priceListLineToUpdate.setPriceList(new PriceList());
        priceListLineToUpdate.setProduct(new Product());
        priceListLineToUpdate.setProductCategory(new ProductLine());
        priceListLineToUpdate.setChargeTemplate(new RecurringChargeTemplate());
        priceListLineToUpdate.setOfferTemplate(new OfferTemplate());
        priceListLineToUpdate.setOfferCategory(new OfferTemplateCategory());
        priceListLineToUpdate.setApplicationEl("e-application-el");
        priceListLineToUpdate.setPriceList(priceList);


        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToUpdate);
        when(priceListLineService.findByCode(givenDto.getCode())).thenReturn(new PriceListLine());

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.update(priceListLineId, givenDto))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessageContaining(PriceListLine.class.getSimpleName());

    }

    @Test
    public void delete_shouldDeletePriceListLine() {
        // given
        PriceList priceList = new PriceList();
        priceList.setStatus(PriceListStatusEnum.DRAFT);
        Long priceListLineId = 10001L;
        PriceListLine priceListLineToDelete = new PriceListLine();
        priceListLineToDelete.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToDelete);

        // when
        priceListLineApiService.delete(priceListLineId);

        // then
        verify(priceListLineService).findById(priceListLineId);
        verify(priceListLineService).remove(priceListLineToDelete);
    }

    @Test
    public void delete_givenActivePriceListShouldTriggerError() {
        // given
        PriceList priceList = new PriceList();
        priceList.setStatus(PriceListStatusEnum.ACTIVE);
        Long priceListLineId = 10001L;
        PriceListLine priceListLineToDelete = new PriceListLine();
        priceListLineToDelete.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToDelete);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.delete(priceListLineId))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be deleted for PriceList status other than DRAFT");
    }

    @Test
    public void delete_givenClosedPriceListShouldTriggerError() {
        // given
        PriceList priceList = new PriceList();
        priceList.setStatus(PriceListStatusEnum.CLOSED);
        Long priceListLineId = 10001L;
        PriceListLine priceListLineToDelete = new PriceListLine();
        priceListLineToDelete.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToDelete);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.delete(priceListLineId))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be deleted for PriceList status other than DRAFT");
    }

    @Test
    public void delete_givenArchivedPriceListShouldTriggerError() {
        // given
        PriceList priceList = new PriceList();
        priceList.setStatus(PriceListStatusEnum.ARCHIVED);
        Long priceListLineId = 10001L;
        PriceListLine priceListLineToDelete = new PriceListLine();
        priceListLineToDelete.setPriceList(priceList);
        when(priceListLineService.findById(priceListLineId)).thenReturn(priceListLineToDelete);

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.delete(priceListLineId))
                .isInstanceOf(BusinessApiException.class)
                .hasMessageContaining("PriceList Line cannot be deleted for PriceList status other than DRAFT");
    }

    @Test
    public void delete_givenMissingPriceListLineShouldTriggerError() {
        // given
        Long priceListLineId = 10001L;
        PriceListLine priceListLineToDelete = new PriceListLine();

        // when + then
        assertThatThrownBy(() -> priceListLineApiService.delete(priceListLineId))
                .isInstanceOf(EntityDoesNotExistsException.class)
                .hasMessageContaining(PriceListLine.class.getSimpleName())
                .hasMessageContaining(String.valueOf(priceListLineId));
    }
}