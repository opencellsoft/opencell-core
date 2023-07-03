package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.esignature.DeliveryMode;
import org.meveo.model.esignature.Operator;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableSigantureRequest.class)
public interface SigantureRequest {

	@Nullable
	Operator getOperator();
	@Nullable
	String getName();
	@Nullable
	DeliveryMode getDelivery_mode();
	@Nullable
	String getCustom_experience_id();
	@Nullable
	String getExternal_id();
	@Nullable
	List<FilesSignature> getFilesToSign();
	
	@Nullable
	List<Signers> getSigners();
}
