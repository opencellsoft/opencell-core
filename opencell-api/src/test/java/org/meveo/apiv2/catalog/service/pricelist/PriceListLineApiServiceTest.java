package org.meveo.apiv2.catalog.service.pricelist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.ImmutablePriceListLineDto;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PriceListLineService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
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
                                                .build();

        PriceList priceList = new PriceList();
        priceList.setCode(priceListCode);
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
}