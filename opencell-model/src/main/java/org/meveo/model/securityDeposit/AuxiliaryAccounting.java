package org.meveo.model.securityDeposit;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class AuxiliaryAccounting {

    @Type(type = "numeric_boolean")
    @Column(name = "use_auxiliary_accounting")
    private boolean useAuxiliaryAccounting = false;

    @Column(name = "auxiliary_account_code_el")
    private String auxiliaryAccountCodeEl;

    @Column(name = "auxiliary_account_label_el")
    @Size(max = 2000)
    private String auxiliaryAccountLabelEl;

    public boolean isUseAuxiliaryAccounting() {
        return useAuxiliaryAccounting;
    }

    public void setUseAuxiliaryAccounting(boolean useAuxiliaryAccounting) {
        this.useAuxiliaryAccounting = useAuxiliaryAccounting;
    }

    public String getAuxiliaryAccountCodeEl() {
        return auxiliaryAccountCodeEl;
    }

    public void setAuxiliaryAccountCodeEl(String auxiliaryAccountCodeEl) {
        this.auxiliaryAccountCodeEl = auxiliaryAccountCodeEl;
    }

    public String getAuxiliaryAccountLabelEl() {
        return auxiliaryAccountLabelEl;
    }

    public void setAuxiliaryAccountLabelEl(String auxiliaryAccountLabelEl) {
        this.auxiliaryAccountLabelEl = auxiliaryAccountLabelEl;
    }
}