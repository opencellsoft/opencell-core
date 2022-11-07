package org.meveo.apiv2.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.rating.CDRStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableCdrDtoInput.class)
public interface CdrDtoInput extends Resource {

    @Nullable
    public Date getEventDate();
    @Nullable
    public BigDecimal getQuantity();
    @Nullable
    public String getParameter1();
    @Nullable
    public String getParameter2();
    @Nullable
    public String getParameter3();
    @Nullable
    public String getParameter4();
    @Nullable
    public String getParameter5();
    @Nullable
    public String getParameter6();
    @Nullable
    public String getParameter7();
    @Nullable
    public String getParameter8();
    @Nullable
    public String getParameter9();
    @Nullable
    public Date getDateParam1();
    @Nullable
    public Date getDateParam2();
    @Nullable
    public Date getDateParam3();
    @Nullable
    public Date getDateParam4();
    @Nullable
    public Date getDateParam5();
    @Nullable
    public BigDecimal getDecimalParam1();
    @Nullable
    public BigDecimal getDecimalParam2();
    @Nullable
    public BigDecimal getDecimalParam3();
    @Nullable
    public BigDecimal getDecimalParam4();
    @Nullable
    public BigDecimal getDecimalParam5(); 
    @Nullable
    public String getAccessCode();
    @Nullable
    public Resource getHeaderEDRId();
    @Nullable
    public String getExtraParam();
    @Nullable
    public String getRejectReason();
    @Nullable
    public CDRStatusEnum getStatus();
}