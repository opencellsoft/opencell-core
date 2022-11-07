package org.meveo.apiv2.securityDeposit.financeSettings.impl;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.meveo.apiv2.securityDeposit.financeSettings.impl.FinanceSettingsMapper.AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL;
import static org.meveo.apiv2.securityDeposit.financeSettings.impl.FinanceSettingsMapper.AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL;

import org.junit.Before;
import org.junit.Test;
import org.meveo.apiv2.securityDeposit.FinanceSettings;
import org.meveo.apiv2.securityDeposit.ImmutableFinanceSettings;

public class FinanceSettingsMapperTest {

    private FinanceSettingsMapper financeSettingsMapper;

    @Before
    public void setUp() {
        financeSettingsMapper = new FinanceSettingsMapper();
    }

    @Test
    public void shouldAddDefaultELsIfUseAuxiliaryAndELsEmpty() {
        FinanceSettings resource = buildResource(true, "", "");
        org.meveo.model.securityDeposit.FinanceSettings entity =
                financeSettingsMapper.toEntity(new org.meveo.model.securityDeposit.FinanceSettings(), resource);

        assertTrue(!entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl().isBlank());
        assertTrue(!entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl().isBlank());
        assertEquals(AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL, entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl());
        assertEquals(AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL, entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl());
    }

    @Test
    public void shouldAddDefaultELsIfUseAuxiliaryAndELsNull() {
        FinanceSettings resource = buildResource(true, null, null);
        org.meveo.model.securityDeposit.FinanceSettings entity =
                financeSettingsMapper.toEntity(new org.meveo.model.securityDeposit.FinanceSettings(), resource);

        assertTrue(!entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl().isBlank());
        assertTrue(!entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl().isBlank());
        assertEquals(AUXILIARY_ACCOUNTING_CODE_DEFAULT_EL,
                entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl());
        assertEquals(AUXILIARY_ACCOUNTING_DEFAULT_LABEL_EL,
                entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl());
    }

    @Test
    public void shouldUseProvidedELs() {
        FinanceSettings resource =
                buildResource(true, "#{ca.code}", "#{ca.description}");
        org.meveo.model.securityDeposit.FinanceSettings entity =
                financeSettingsMapper.toEntity(new org.meveo.model.securityDeposit.FinanceSettings(), resource);

        assertTrue(!entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl().isBlank());
        assertTrue(!entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl().isBlank());
        assertEquals("#{ca.code}", entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl());
        assertEquals("#{ca.description}", entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl());
    }

    @Test
    public void shouldNotAddDefaultELsIfUseAuxiliaryIsFalse() {
        FinanceSettings resource = buildResource(false, null, "");
        org.meveo.model.securityDeposit.FinanceSettings entity =
                financeSettingsMapper.toEntity(new org.meveo.model.securityDeposit.FinanceSettings(), resource);

        assertTrue(isNull(entity.getAuxiliaryAccounting().getAuxiliaryAccountCodeEl()));
        assertTrue(entity.getAuxiliaryAccounting().getAuxiliaryAccountLabelEl().isBlank());
    }

    private FinanceSettings buildResource(boolean auxiliaryAccounting,
                                          String auxiliaryAccountingCode, String auxiliaryAccountingLabel) {
        return ImmutableFinanceSettings.builder()
                .useSecurityDeposit(FALSE)
                .autoRefund(TRUE)
                .useAuxiliaryAccounting(auxiliaryAccounting)
                .auxiliaryAccountCodeEl(auxiliaryAccountingCode)
                .auxiliaryAccountLabelEl(auxiliaryAccountingLabel)
                .activateDunning(FALSE)
                .build();
    }
}