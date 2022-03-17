package org.meveo.service.settings;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.service.settings.impl.OpenOrderSettingService;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OpenOrderSettingServiceTest {

    @InjectMocks
    private OpenOrderSettingService openOrderSettingService;

    @Test
    public void checkOpenOrderSetting_ApplyMaximumValidityValue_0() {

        OpenOrderSetting openOrderSetting = new OpenOrderSetting();
        openOrderSetting.setApplyMaximumValidityValue(0);
        openOrderSetting.setDefineMaximumValidityValue(1);
        try {
            openOrderSettingService.checkParameters(openOrderSetting);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals(exception.getMessage(), "Apply Maximum Validity Value must be greater than 0");
        }

    }

    @Test
    public void checkOpenOrderSetting_DefineMaximumValidityValue_0() {

        OpenOrderSetting openOrderSetting = new OpenOrderSetting();
        openOrderSetting.setApplyMaximumValidityValue(1);
        openOrderSetting.setDefineMaximumValidityValue(0);
        try {
            openOrderSettingService.checkParameters(openOrderSetting);
        } catch (Exception exception) {
            Assert.assertTrue(exception instanceof InvalidParameterException);
            Assert.assertEquals(exception.getMessage(), "Define Maximum Validity Value must be greater than 0");
        }
    }
}
