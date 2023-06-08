package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.esignature.SigantureAuthentificationMode;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableSigners.class)
public interface Signers {

	@Nullable
	InfoSigner getInfo();
	
	@Nullable
	SigantureAuthentificationMode getSignature_authentication_mode();
	@Nullable
	List<SignatureFields> getFields();
	
}
