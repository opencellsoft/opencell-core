package org.meveo.apiv2.securityDeposit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableHugeEntity.class)
public interface HugeEntity {

    String getEntityClass();

    List<String> getHugeLists();

    List<String> getMandatoryFilterFields();
}
