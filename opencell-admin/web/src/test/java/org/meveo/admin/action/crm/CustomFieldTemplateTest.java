package org.meveo.admin.action.crm;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.jsf.converter.BigDecimalXDigitsConverter;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.primefaces.component.inputtext.InputText;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * A Test for rounding a custom field has double type
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomFieldTemplateTest {

    private static final String DEFAULT_CODE = "CFT_CODE";
    private static final String DEFAULT_DESCRIPTION = "CFT_DESCRIPTION";
    private static final String DEFAULT_APPLIES_TO = "Seller";
    private static final Integer EXPECTED_NUMBER_DECIMAL = 6;
    private static final RoundingModeEnum EXPECTED_ROUNDING_MODE = RoundingModeEnum.NEAREST;

    @Mock
    private CustomFieldTemplateService customFieldTemplateService;

    @Mock
    private BigDecimalXDigitsConverter bigDecimalXDigitsConverter;

    private CustomFieldTemplate entity;

    @Test
    public void createCftDoubleDefaultDecimalAndRoundingMode() throws BusinessException {
        entity = createEntity();
        entity.setFieldType(CustomFieldTypeEnum.DOUBLE);
        entity.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        doAnswer(new CustomFieldTemplateDefaultValuesAnswer()).when(customFieldTemplateService).create(any(CustomFieldTemplate.class));
        customFieldTemplateService.create(entity);
        verify(customFieldTemplateService).create(entity);

    }

    @Test
    public void createCftDoubleWithDecimalAndRoundingMode() throws BusinessException {
        entity = createEntity();
        entity.setFieldType(CustomFieldTypeEnum.DOUBLE);
        entity.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        entity.setNbDecimal(EXPECTED_NUMBER_DECIMAL);
        entity.setRoundingMode(EXPECTED_ROUNDING_MODE);
        doAnswer(new CustomFieldTemplateValuesAnswer()).when(customFieldTemplateService).create(any(CustomFieldTemplate.class));
        customFieldTemplateService.create(entity);
        verify(customFieldTemplateService).create(entity);

    }

    @Test
    public void convertDoubleToBigDecimal_WithDefaultNbrDigitsAndRoundingMode(){
        when(bigDecimalXDigitsConverter.getAsObject(any(FacesContext.class),any(UIComponent.class),anyString())).thenReturn(new BigDecimal(0.25));
        FacesContext facesContext = new FacesContextWrapper() {
            @Override
            public FacesContext getWrapped() {
                return null;
            }
        };

        UIComponent inputText = new InputText();
        BigDecimal convertedValue = (BigDecimal)bigDecimalXDigitsConverter.getAsObject(facesContext,inputText,"0.2587412");
        Assert.assertNotNull(convertedValue);
        Assert.assertEquals(new BigDecimal(0.25),convertedValue);

    }

    @Test
    public void convertDoubleToBigDecimal_WithNbrDigitsAndRoundingMode(){
        when(bigDecimalXDigitsConverter.getAsObject(any(FacesContext.class),any(UIComponent.class),anyString())).thenAnswer(new BigDecimalXDigitsConverterAnswer());
        FacesContext facesContext = new FacesContextWrapper() {
            @Override
            public FacesContext getWrapped() {
                return null;
            }
        };

        UIComponent inputText = new InputText();
        inputText.getAttributes().put("nbDecimal",4);
        inputText.getAttributes().put("roundingMode",RoundingModeEnum.UP);
        BigDecimal convertedValue = (BigDecimal)bigDecimalXDigitsConverter.getAsObject(facesContext,inputText,"0.2587912");
        Assert.assertNotNull(convertedValue);
        Assert.assertEquals(new BigDecimal("0.2588"),convertedValue);

    }



    public static CustomFieldTemplate createEntity(){
        CustomFieldTemplate customFieldTemplate = new CustomFieldTemplate();
        customFieldTemplate.setCode(DEFAULT_CODE);
        customFieldTemplate.setDescription(DEFAULT_DESCRIPTION);
        customFieldTemplate.setAppliesTo(DEFAULT_APPLIES_TO);
        return customFieldTemplate;
    }

    private static class CustomFieldTemplateDefaultValuesAnswer implements Answer {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Object[] arguments = invocation.getArguments();
            CustomFieldTemplate customFieldTemplate = null;
            if (arguments != null && arguments.length > 0 && arguments[0] != null ) {
                customFieldTemplate = (CustomFieldTemplate ) arguments[0];
            }
            assertThat(customFieldTemplate,is(notNullValue()));
            assertThat(customFieldTemplate.getNbDecimal(),is(nullValue()));
            assertThat(customFieldTemplate.getRoundingMode(),is(nullValue()));
            return null;
        }
    }

    private static class CustomFieldTemplateValuesAnswer implements Answer {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Object[] arguments = invocation.getArguments();
            CustomFieldTemplate customFieldTemplate = null;
            if (arguments != null && arguments.length > 0 && arguments[0] != null ) {
                customFieldTemplate = (CustomFieldTemplate ) arguments[0];
            }
            assertThat(customFieldTemplate,is(notNullValue()));
            assertEquals(customFieldTemplate.getNbDecimal(),EXPECTED_NUMBER_DECIMAL);
            assertEquals(customFieldTemplate.getRoundingMode(),EXPECTED_ROUNDING_MODE);
            return null;
        }
    }

    private static class BigDecimalXDigitsConverterAnswer implements Answer<BigDecimal> {

        @Override
        public BigDecimal answer(InvocationOnMock invocation) throws Throwable {
            Object[] arguments = invocation.getArguments();
            if (arguments != null && arguments.length > 2 && arguments[1] != null ) {
                UIComponent  uiComponent = (UIComponent ) arguments[1];
                Integer nbDecimal = (Integer)uiComponent.getAttributes().get("nbDecimal");
                RoundingModeEnum rd = (RoundingModeEnum)uiComponent.getAttributes().get("roundingMode");
                BigDecimal number = new BigDecimal(arguments[2].toString());
                return NumberUtils.round(number,nbDecimal,rd);
            }
            return null;
        }
    }
}
