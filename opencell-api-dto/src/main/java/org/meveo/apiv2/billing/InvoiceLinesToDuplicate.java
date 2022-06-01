package org.meveo.apiv2.billing;

import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInvoiceLinesToDuplicate.class)
public interface InvoiceLinesToDuplicate {

    List<Long> getInvoiceLineIds();

    @Nullable
    Boolean getSkipValidation();
}