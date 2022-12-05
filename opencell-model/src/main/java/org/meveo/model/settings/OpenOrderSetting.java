package org.meveo.model.settings;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "open_order_setting")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_setting_seq"), })
public class OpenOrderSetting extends BusinessEntity {
   @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "use_open_orders")
    private Boolean useOpenOrders = false;

   @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "apply_maximum_validity")
    private Boolean applyMaximumValidity = false;

    @Column(name = "apply_maximum_validity_value")
    private Integer applyMaximumValidityValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "apply_maximum_validity_unit")
    private MaximumValidityUnitEnum applyMaximumValidityUnit;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "define_maximum_validity")
    private Boolean defineMaximumValidity = false;

     @Column(name = "define_maximum_validity_value")
    private Integer defineMaximumValidityValue;

     @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "use_managment_validation_for_oo_quotation")
    private Boolean useManagmentValidationForOOQuotation = false;


 public Boolean getUseOpenOrders() {
  return useOpenOrders;
 }

 public void setUseOpenOrders(Boolean useOpenOrders) {
  this.useOpenOrders = useOpenOrders;
 }

 public Boolean getApplyMaximumValidity() {
  return applyMaximumValidity;
 }

 public void setApplyMaximumValidity(Boolean applyMaximumValidity) {
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

 public Boolean getDefineMaximumValidity() {
  return defineMaximumValidity;
 }

 public void setDefineMaximumValidity(Boolean defineMaximumValidity) {
  this.defineMaximumValidity = defineMaximumValidity;
 }

 public Integer getDefineMaximumValidityValue() {
  return defineMaximumValidityValue;
 }

 public void setDefineMaximumValidityValue(Integer defineMaximumValidityValue) {
  this.defineMaximumValidityValue = defineMaximumValidityValue;
 }

 public Boolean getUseManagmentValidationForOOQuotation() {
  return useManagmentValidationForOOQuotation;
 }

 public void setUseManagmentValidationForOOQuotation(Boolean useManagmentValidationForOOQuotation) {
  this.useManagmentValidationForOOQuotation = useManagmentValidationForOOQuotation;
 }
}



