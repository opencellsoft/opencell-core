package org.meveo.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.BadRequestException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.TaxDto;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UntdidTaxationCategory;
import org.meveo.service.billing.impl.AccountingCodeService;
import org.meveo.service.billing.impl.UntdidTaxationCategoryService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TaxApiTest {

    @InjectMocks
    private TaxApi taxApi;

    @Mock
    private TaxService taxService;

    @Mock
    private AccountingCodeService accountingCodeService;

    @Mock
    private UntdidTaxationCategoryService untdidTaxationCategoryService;
    
    @Mock
    private CustomFieldTemplateService customFieldTemplateService;

    @Test
    public void testCompositeTaxCreation() {
        Tax subTax1 = new Tax();
        subTax1.setId(1L);
        subTax1.setComposite(false);
        subTax1.setCode("SUB_TAX_01");
        subTax1.setDescription("SUB TAX 01");
        subTax1.setPercent(BigDecimal.TEN);
        Tax subTax2 = new Tax();
        subTax2.setId(2L);
        subTax2.setComposite(false);
        subTax2.setCode("SUB_TAX_02");
        subTax2.setDescription("SUB TAX 02");
        subTax2.setPercent(BigDecimal.ONE);

        TaxDto taxDto = new TaxDto();
        when(accountingCodeService.findByCode("ACC_CODE")).thenReturn(new AccountingCode());
        when(untdidTaxationCategoryService.getByCode("TAXATION_CAT")).thenReturn(new UntdidTaxationCategory());
        when(taxService.findById(1L)).thenReturn(subTax1);
        when(taxService.findById(2L)).thenReturn(subTax2);
        when(taxApi.populateCustomFields(new CustomFieldsDto(), any(), true, true))
                .thenReturn(null);
        when(customFieldTemplateService.findByAppliesTo(any(ICustomFieldEntity.class))).thenReturn(null);

        taxDto.setCode("COMPOSITE_TAX");
        taxDto.setDescription("COMPOSITE TAX");
        taxDto.setAccountingCode("ACC_CODE");
        taxDto.setComposite(Boolean.TRUE);
        List<TaxDto> subTaxes = new ArrayList<>();
        subTaxes.add(new TaxDto(1L));
        subTaxes.add(new TaxDto(2L));
        taxDto.setSubTaxes(subTaxes);
        taxDto.setTaxationCategory("TAXATION_CAT");
        
        Tax tax = taxApi.create(taxDto);

        Assert.assertEquals(tax.getPercent(), new BigDecimal(11));
    }

    @Test
    public void testSimpleTaxCreation() {
        TaxDto taxDto = new TaxDto();
        when(accountingCodeService.findByCode("ACC_CODE")).thenReturn(new AccountingCode());
        when(untdidTaxationCategoryService.getByCode("TAXATION_CAT")).thenReturn(new UntdidTaxationCategory());
        when(taxApi.populateCustomFields(new CustomFieldsDto(), any(), true, true))
                .thenReturn(null);
        when(customFieldTemplateService.findByAppliesTo(any(ICustomFieldEntity.class))).thenReturn(null);

        taxDto.setCode("COMPOSITE_TAX");
        taxDto.setDescription("COMPOSITE TAX");
        taxDto.setAccountingCode("ACC_CODE");
        taxDto.setComposite(Boolean.FALSE);
        taxDto.setPercent(BigDecimal.TEN);
        taxDto.setTaxationCategory("TAXATION_CAT");
        
        Tax tax = taxApi.create(taxDto);

        Assert.assertEquals(tax.getPercent(), BigDecimal.TEN);
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailIfTaxSubTaxesNotRespectingTheCorrectNumber() {
        Tax subTax1 = new Tax();
        subTax1.setId(1L);
        List<TaxDto> subTaxes = new ArrayList<>();
        subTaxes.add(new TaxDto(1L));
        TaxDto taxDto = new TaxDto();
        taxDto.setCode("COMPOSITE_TAX");
        taxDto.setDescription("COMPOSITE TAX");
        taxDto.setAccountingCode("ACC_CODE");
        taxDto.setSubTaxes(subTaxes);
        taxDto.setComposite(Boolean.TRUE);

        taxApi.create(taxDto);
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailIfTOneOfSubTaxesIsComposite() {
        Tax subTax1 = new Tax();
        subTax1.setId(1L);
        subTax1.setComposite(true);
        List<TaxDto> subTaxes = new ArrayList<>();
        subTaxes.add(new TaxDto(1L));

        TaxDto taxDto = new TaxDto();
        taxDto.setCode("COMPOSITE_TAX");
        taxDto.setDescription("COMPOSITE TAX");
        taxDto.setAccountingCode("ACC_CODE");
        taxDto.setSubTaxes(subTaxes);
        taxDto.setComposite(Boolean.TRUE);

        taxApi.create(taxDto);
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailIfTTaxIsSimpleAndPercentIsNull() {
        TaxDto taxDto = new TaxDto();
        taxDto.setCode("SIMPLE_TAX");
        taxDto.setDescription("Simple TAX");
        taxDto.setAccountingCode("ACC_CODE");
        taxDto.setComposite(Boolean.FALSE);

        taxApi.create(taxDto);
    }

    @Test(expected = BadRequestException.class)
    public void shouldFailIfTTaxIsSimpleAndASubTaxesIsPresent() {
        Tax subTax1 = new Tax();
        subTax1.setId(1L);
        subTax1.setComposite(true);
        List<TaxDto> subTaxes = new ArrayList<>();
        subTaxes.add(new TaxDto(1L));

        TaxDto taxDto = new TaxDto();
        taxDto.setCode("SIMPLE_TAX");
        taxDto.setDescription("Simple TAX");
        taxDto.setAccountingCode("ACC_CODE");
        taxDto.setComposite(Boolean.FALSE);
        taxDto.setSubTaxes(subTaxes);

        taxApi.create(taxDto);
    }
}