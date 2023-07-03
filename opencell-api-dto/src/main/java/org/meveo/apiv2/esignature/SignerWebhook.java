package org.meveo.apiv2.esignature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
@Value.Immutable
@JsonDeserialize(as = ImmutableSignerWebhook.class)
public interface SignerWebhook extends Serializable {
	@Nullable
	String getId();
	@Nullable
	List<Answers> getAnswers();
	@Nullable
	String getStatus();
	
	@Value.Immutable
	@JsonDeserialize(as = ImmutableAnswers.class)
	interface Answers extends  Serializable{
		@Nullable
		@JsonProperty("field_id")
		String getFieldId();
		@Nullable
		@JsonProperty("field_type")
		String getFieldType();
		@Nullable
		String getQuestion();
		@Nullable
		String getAnswer();
	}
}
