package org.meveo.apiv2.catalog;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.immutables.value.Value;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutablePriceList.class)
public interface PriceList extends Resource {
	
	@Nullable
	@Schema(description = "The Price List name")
	@JsonProperty("name")
	String getName();
	
	@Nullable
	@Schema(description = "The Price List description")
	@JsonProperty("description")
	String getDescription();

	@Nullable
	@Schema(description = "The Price List validFrom")
	@JsonProperty("validFrom")
	Date getValidFrom();
	
	@Nullable
	@Schema(description = "The Price List validUntil")
	@JsonProperty("validUntil")
	Date getValidUntil();

	@Nullable
	@Schema(description = "The Price List application start date")
	@JsonProperty("applicationStartDate")
	Date getApplicationStartDate();
	
	@Nullable
	@Schema(description = "The Price List application end date")
	@JsonProperty("applicationEndDate")
	Date getApplicationEndDate();
	
	@Nullable
	@Schema(description = "The Price List status")
	String getStatus();

	@Nullable
	@Schema(description = "The Price List brands")
	@JsonProperty("brands")
	List<String> getBrands();
	
	@Nullable
	@Schema(description = "The Price List customer categories")
	@JsonProperty("customerCategories")
	List<String> getCustomerCategories();
	
	@Nullable
	@Schema(description = "The Price List credit categories")
	@JsonProperty("creditCategories")
	List<String> getCreditCategories();
	
	@Nullable
	@Schema(description = "The Price List credit countries")
	@JsonProperty("countries")
	List<String> getCountries();
	
	@Nullable
	@Schema(description = "The Price List currencies")
	@JsonProperty("currencies")
	List<String> getCurrencies();
	
	@Nullable
	@Schema(description = "The Price List legal entities")
	@JsonProperty("legalEntities")
	List<String> getLegalEntities();
	
	@Nullable
	@Schema(description = "The Price List payment methods")
	@JsonProperty("paymentMethods")
	Set<String> getPaymentMethods();
	
	@Nullable
	@Schema(description = "The Price List payment sellers")
	@JsonProperty("sellers")
	List<String> getSellers();

	@Nullable
	@Schema(description = "list of the custom field if any")
	CustomFieldsDto getCustomFields();

}