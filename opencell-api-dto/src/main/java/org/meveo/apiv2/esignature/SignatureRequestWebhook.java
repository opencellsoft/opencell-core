package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.esignature.DeliveryMode;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableSignatureRequestWebhook.class)
public interface SignatureRequestWebhook extends Serializable {
	
	@Nullable
	String getId();
	@Nullable
	String getStatus();
	@Nullable
	String getName();
	@Nullable
	@JsonProperty("delivery_mode")
	DeliveryMode getDeliveryMode();
	@Nullable
	@JsonProperty("created_at")
	Date getCreateAt();
	@Nullable
	String getTimezone();
	@JsonProperty("email_custom_note")
	@Nullable
	String getEmailCustomNote();
	@JsonProperty("expiration_date")
	@Nullable
	Date getExpirationDate();
	@Nullable
	String getSource();
	@JsonProperty("ordered_signers")
	boolean getOrderSigners();
	@JsonProperty("external_id")
	@Nullable
	String getExternalId();
	
	@Nullable
	List<SignerWebhook> getSigners();
	
	@Nullable
	Map<String, Object> getApprovers();
	
	@Nullable
	Object getSender();
	
	@Nullable
	Map<String, Object> getDocuments();
	
	@Nullable
	@JsonProperty("reminder_settings")
	Object getReminderSettings();
	
}
