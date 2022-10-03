package org.meveo.apiv2.generic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(as = ImmutableGenericFieldDetails.class)
public interface GenericFieldDetails {

    
    String getName();
    @Nullable
    String getHeader();
    @Nullable
    String getTransformation();
    @Nullable
    @Value.Default default Map<String, String> getMappings(){ return Collections.emptyMap();}
    @Nullable
    String getFormulaInputs();
    @Nullable
    String getFormula();

}
