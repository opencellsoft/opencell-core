package org.meveo.model.settings;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;

@Entity
@Table(name = "open_order_setting")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_setting_seq"), })
public class OpenOrderSetting extends BusinessEntity {
   @Type(type = "numeric_boolean")
    @Column(name = "use_open_orders")
    private boolean useOpenOrders = false;

   @Type(type = "numeric_boolean")
    @Column(name = "apply_maximum_validity")
    private boolean applyMaximumValidity = false;

    @Column(name = "apply_maximum_validity_value")
    private Integer applyMaximumValidityValue;

    @Enumerated(EnumType.STRING)
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


 public boolean isUseOpenOrders() {
  return useOpenOrders;
 }

 public void setUseOpenOrders(boolean useOpenOrders) {
  this.useOpenOrders = useOpenOrders;
 }

 public boolean isApplyMaximumValidity() {
  return applyMaximumValidity;
 }

 public void setApplyMaximumValidity(boolean applyMaximumValidity) {
  this.applyMaximumValidity = applyMaximumValidity;
 }

 public Integer getApplyMaximumValidityValue() {
  return applyMaximumValidityValue;
 }

 public void setApplyMaximumValidityValue(Integer applyMaximumValidityValue) {
  this.applyMaximumValidityValue = applyMaximumValidityValue;
 }

 public MaximumValidityUnitEnum getApplyMaximumValidityUnit() {
  return applyMaximumValidityUnit;
 }

 public void setApplyMaximumValidityUnit(MaximumValidityUnitEnum applyMaximumValidityUnit) {
  this.applyMaximumValidityUnit = applyMaximumValidityUnit;
 }

 public boolean isDefineMaximumValidity() {
  return defineMaximumValidity;
 }

 public void setDefineMaximumValidity(boolean defineMaximumValidity) {
  this.defineMaximumValidity = defineMaximumValidity;
 }

 public Integer getDefineMaximumValidityValue() {
  return defineMaximumValidityValue;
 }

 public void setDefineMaximumValidityValue(Integer defineMaximumValidityValue) {
  this.defineMaximumValidityValue = defineMaximumValidityValue;
 }

 public boolean isUseManagmentValidationForOOQuotation() {
  return useManagmentValidationForOOQuotation;
 }

 public void setUseManagmentValidationForOOQuotation(boolean useManagmentValidationForOOQuotation) {
  this.useManagmentValidationForOOQuotation = useManagmentValidationForOOQuotation;
 }
}



