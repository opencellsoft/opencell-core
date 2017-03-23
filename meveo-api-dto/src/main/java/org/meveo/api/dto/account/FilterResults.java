package org.meveo.api.dto.account;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.meveo.model.BusinessEntity;

/**
 * This annotation is used to mark if an {@link AccountDto} so it will be
 * possible for {@link AccountDtoFilter} to parse and filter its child entities.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface FilterResults {

	/**
	 * Identifies the DTO property that returns the child entities of the
	 * {@link AccountDto}. e.g. if "customerAccounts.customerAccount" is passed
	 * into this attribute, then the value of
	 * "dto.customerAccounts.customerAccount" will be parsed and filtered.
	 * 
	 * @return
	 */
	String property();

	/**
	 * Identifies the type of object each of the child entities. e.g. if
	 * CustomerAccount.class is passed into this attribute, then each entity
	 * retrieved from the child entities will be instantiated as a
	 * CustomerAccount object.
	 * 
	 * @return
	 */
	Class<? extends BusinessEntity> entityClass();

}
