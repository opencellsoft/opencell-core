package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSignatureFields.class)
public interface SignatureFields {
	
	default int getPage() { return 1;}
	
	default int getWidth() { return 1;}
	
	default int getX(){ return 1;}
	
	default int getY(){ return 1;}
}
