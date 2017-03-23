package org.meveo.model.catalog.mixin;

import java.util.List;

import org.meveo.model.Auditable;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.crm.Provider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Edward P. Legaspi
 **/
public interface OfferTemplateCategoryMixin {

	@JsonProperty("parentCategoryCode")
	String getParentCategoryCode();

	@JsonProperty("code")
	String getCode();

	@JsonProperty("name")
	String getName();

	@JsonProperty("description")
	String getDescription();

	@JsonIgnore
	OfferTemplateCategory getOfferTemplateCategory();

	@JsonIgnore
	List<OfferTemplateCategory> getChildren();

	@JsonIgnore
	List<ProductOffering> getProductOffering();

	@JsonIgnore
	Auditable getAuditable();

	@JsonIgnore
	Provider getProvider();

}
