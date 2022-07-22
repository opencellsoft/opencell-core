package org.meveo.apiv2.ordering.resource.oo;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableAvailableOpenOrder.class)
public interface AvailableOpenOrder {

	@NotNull
	Long getOpenOrderId();
	
	@NotNull
	String getOpenOrderNumber();
	
	@NotNull
	Date getStartDate();

	@NotNull
	String getExternalReference();

	@NotNull
	List<Long> getProducts();

	@NotNull
	List<Long> getArticles();
}
