package org.meveo.apiv2.dunning;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableDunningLevel.class)
public interface DunningLevel extends Resource {

	@Nullable
	@JsonProperty("description")
	@Schema(description = "dunning level description")
	String getDescription();

	@JsonProperty("isReminderLevel")
	@Schema(description = "Is a reminder level")
	@Value.Default
	default boolean isReminderLevel() {
		return false;
	}

	@JsonProperty("isActiveLevel")
	@Schema(description = "Is an active level")
	@Value.Default
	default boolean isActiveLevel() {
		return true;
	}

	@Nullable
	@JsonProperty("dunningLevelDaysOverdue")
	@Schema(description = "Days overdue")
	Integer getDunningLevelDaysOverdue();

	@JsonProperty("isSoftDeclineLevel")
	@Schema(description = "Soft decline")
	@Value.Default
	default boolean isSoftDeclineLevel() {
		return false;
	}

	@Nullable
	@JsonProperty("dunningLevelMinBalance")
	@Schema(description = "Dunning level min balance")
	BigDecimal getDunningLevelMinBalance();

	@Nullable
	@JsonProperty("dunningLevelMinBalanceCurrency")
	@Schema(description = "Dunning level currency")
	@Value.Default
	default String getDunningLevelMinBalanceCurrency() {
		return "EUR";
	}

	@Nullable
	@JsonProperty("dunningLevelChargeType")
	@Schema(description = "Dunning level charge type")
	String getDunningLevelChargeType();

	@Nullable
	@JsonProperty("dunningLevelChargeValue")
	@Schema(description = "Dunning level charge value")
	BigDecimal getDunningLevelChargeValue();

	@Nullable
	@JsonProperty("dunningLevelChargeCurrency")
	@Schema(description = "Dunning level charge currency")
	String getDunningLevelChargeCurrency();

	@JsonProperty("isEndOfDunningLevel")
	@Schema(description = "End of dunning level")
	@Value.Default
	default boolean isEndOfDunningLevel() {
		return false;
	}

	@Nullable
	@Schema(description = "Dunning level actions")
	@JsonProperty("dunningActions")
	List<String> getDunningActions();

}
