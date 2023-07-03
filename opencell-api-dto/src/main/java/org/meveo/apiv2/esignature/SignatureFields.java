package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSignatureFields.class)
public interface SignatureFields {
	
	int getPage() ;
	
	int getWidth() ;
	
	int getX();
	
	int getY();
}
