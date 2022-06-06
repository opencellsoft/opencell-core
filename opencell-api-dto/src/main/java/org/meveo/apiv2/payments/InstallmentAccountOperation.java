package org.meveo.apiv2.payments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import org.immutables.value.Value;
import org.meveo.apiv2.AcountReceivable.AccountOperationAndSequence;
import org.meveo.apiv2.AcountReceivable.ImmutableMatchingAccountOperation;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableInstallmentAccountOperation.class)
public interface InstallmentAccountOperation {

    @Schema(description = "AccountOperation Id")
    @Nonnull
    Long getId();
}