package org.meveo.service.billing;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.RatingService;

public class RatingServiceTest {

    @Test
    public void testCalculateAmmounts() {

        int enterprise = 0;
        int rounding = 1;
        int unitPriceWithoutTax = 2;
        int unitPriceWithTax = 3;
        int quantity = 4;
        int taxPercent = 5;
        int expectedUnitPriceWithoutTax = 6;
        int expectedUnitPriceWithTax = 7;
        int expectedUnitTax = 8;
        int expectedAmountWithoutTax = 9;
        int expectedAmountWithTax = 10;
        int expectedTax = 11;
        String[][] testValues = { { "1", "-1", "100", "0", "1", "20", "100", "120", "20", "100", "120", "20" },
                { "1", "10", "100.5", "0", "1", "7", "100.5", "107.535", "7.035", "100.5", "107.535", "7.035" },
                { "1", "1", "100.5", "0", "1", "7", "100.5", "107.535", "7.035", "100.5", "107.5", "7" },
                { "1", "10", "100.4", "0", "1", "7.32", "100.4", "107.74928", "7.34928", "100.4", "107.74928", "7.34928" },
                { "1", "2", "100.4", "0", "1", "7.32", "100.4", "107.74928", "7.34928", "100.4", "107.75", "7.35" },
                { "1", "1", "100.4", "0", "1", "7.32", "100.4", "107.74928", "7.34928", "100.4", "107.7", "7.3" },
                { "1", "10", "100.4", "0", "10", "7.32", "100.4", "107.74928", "7.34928", "1004", "1077.4928", "73.4928" },
                { "1", "2", "100.4", "0", "10", "7.32", "100.4", "107.74928", "7.34928", "1004", "1077.49", "73.49" },
                { "1", "1", "100.4", "0", "10", "7.32", "100.4", "107.74928", "7.34928", "1004", "1077.5", "73.5" },
                {"0","-1","0","120","1","20","100","120","20","100","120","20"},
                {"0","10","0","100.8","1","6.88","94.311377245509","100.8","6.488622754491","94.3113772455","100.8","6.4886227545"},
                {"0","1","0","100.8","1","6.88","94.311377245509","100.8","6.488622754491","94.3","100.8","6.5"},
                {"0","10","0","100.8","1","7.32","93.924711144242","100.8","6.875288855758","93.9247111442","100.8","6.8752888558"},
                {"0","2","0","100.8","1","7.32","93.924711144242","100.8","6.875288855758","93.92","100.8","6.88"},
                {"0","1","0","100.8","1","7.32","93.924711144242","100.8","6.875288855758","93.9","100.8","6.9"},
                {"0","10","0","100.8","10","7.32","93.924711144242","100.8","6.875288855758","939.2471114424","1008","68.7528885576"},
                {"0","2","0","100.8","10","7.32","93.924711144242","100.8","6.875288855758","939.25","1008","68.75"},
                {"0","1","0","100.8","10","7.32","93.924711144242","100.8","6.875288855758","939.2","1008","68.8"}
        };

        try {
            RatingService ratingService = new RatingService();

            Provider appProvider = new Provider();
            FieldUtils.writeField(ratingService, "appProvider", appProvider, true);

            for (String[] testValue : testValues) {

                appProvider.setEntreprise(testValue[enterprise].equals("1"));
                appProvider.setRounding(testValue[rounding].equals("-1") ? null : new Double(testValue[rounding]).intValue());

                WalletOperation wo = new WalletOperation();
                wo.setQuantity(new BigDecimal(testValue[quantity]));
                wo.setTaxPercent(new BigDecimal(testValue[taxPercent]));

                MethodUtils.invokeMethod(ratingService, true, "calculateAmounts",
                    new Object[] { wo, new BigDecimal(testValue[unitPriceWithoutTax]), new BigDecimal(testValue[unitPriceWithTax]) },
                    new Class[] { WalletOperation.class, BigDecimal.class, BigDecimal.class });

                Assert.assertTrue(new BigDecimal(testValue[expectedUnitPriceWithoutTax]) + "-" + wo.getUnitAmountWithoutTax(),
                    new BigDecimal(testValue[expectedUnitPriceWithoutTax]).compareTo(wo.getUnitAmountWithoutTax()) == 0);
                Assert.assertTrue(new BigDecimal(testValue[expectedUnitPriceWithTax]) + "-" + wo.getUnitAmountWithTax(),
                    new BigDecimal(testValue[expectedUnitPriceWithTax]).compareTo(wo.getUnitAmountWithTax()) == 0);
                Assert.assertTrue(new BigDecimal(testValue[expectedUnitTax]) + "-" + wo.getUnitAmountTax(),
                    new BigDecimal(testValue[expectedUnitTax]).compareTo(wo.getUnitAmountTax()) == 0);
                Assert.assertTrue(new BigDecimal(testValue[expectedAmountWithoutTax]) + "-" + wo.getAmountWithoutTax(),
                    new BigDecimal(testValue[expectedAmountWithoutTax]).compareTo(wo.getAmountWithoutTax()) == 0);
                Assert.assertTrue(new BigDecimal(testValue[expectedAmountWithTax]) + "-" + wo.getAmountWithTax(),
                    new BigDecimal(testValue[expectedAmountWithTax]).compareTo(wo.getAmountWithTax()) == 0);
                Assert.assertTrue(new BigDecimal(testValue[expectedTax]) + "-" + wo.getAmountTax(), new BigDecimal(testValue[expectedTax]).compareTo(wo.getAmountTax()) == 0);
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            e.printStackTrace();
        }

    }
}
