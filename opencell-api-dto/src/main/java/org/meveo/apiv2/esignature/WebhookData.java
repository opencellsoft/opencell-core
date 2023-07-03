package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonDeserialize(as = ImmutableWebhookData.class)
public interface WebhookData {
	@Nullable
	@JsonProperty("signature_request")
	SignatureRequestWebhook getSignatureRequestWebhook();
}
