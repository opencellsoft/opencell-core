package org.meveo.model.settings;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Embeddable
public class OpenOrderSetting extends BusinessEntity {
   @Type(type = "numeric_boolean")
    @Column(name = "use_open_orders")
    private boolean useOpenOrders = false;

   @Type(type = "numeric_boolean")
    @Column(name = "apply_maximum_validity")
    private boolean applyMaximumValidity = false;

    @Column(name = "apply_maximum_validity_value")
    private Integer applyMaximumValidityValue;

    @Column(name = "apply_maximum_validity_unit")
    private MaximumValidityUnitEnum applyMaximumValidityUnit;

    @Type(type = "numeric_boolean")
    @Column(name = "define_maximum_validity")
    private boolean defineMaximumValidity = false;

     @Column(name = "define_maximum_validity_value")
    private Integer defineMaximumValidityValue;

     @Type(type = "numeric_boolean")
    @Column(name = "use_managment_validation_for_oo_quotation")
    private boolean useManagmentValidationForOOQuotation = false;

}



