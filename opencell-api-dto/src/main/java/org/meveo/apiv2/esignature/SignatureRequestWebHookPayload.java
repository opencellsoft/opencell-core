package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Date;
@Value.Immutable
@JsonDeserialize(as = ImmutableSignatureRequestWebHookPayload.class)
public interface SignatureRequestWebHookPayload extends Serializable {
	
	@Nullable
	@JsonProperty("event_id")
	String getEventId();
	@Nullable
	@JsonProperty("event_name")
	String getEventName();
	@Nullable
	@JsonProperty("event_time")
	Date getEventTime();
	@Nullable
	@JsonProperty("subscription_id")
	String getSubscriptionId();
	@Nullable
	@JsonProperty("subscription_description")
	String getSubscriptionDescription();
	boolean getSandbox();
	@Nullable
	WebhookData getData();
	
	
}
