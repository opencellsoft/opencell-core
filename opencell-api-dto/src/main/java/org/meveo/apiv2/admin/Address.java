package org.meveo.apiv2.admin;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly=true)
public interface Address extends Serializable {

	@Nullable
	String getAddress1();

	@Nullable
	String getAddress2();

	@Nullable
	String getAddress3();

	@Nullable
	String getZipCode();

	@Nullable
	String getCity();

	@Nullable
	String getCountry();

	@Nullable
	String getState();
}
