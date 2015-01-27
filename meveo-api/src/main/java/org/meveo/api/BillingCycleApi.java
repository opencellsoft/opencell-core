package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.catalog.impl.CalendarService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BillingCycleApi extends BaseApi {

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	private CalendarService calendarService;

	public void create(BillingCycleDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCalendar()) && postData.getInvoiceDateDelay() != null
				&& postData.getDueDateDelay() != null) {
			Provider provider = currentUser.getProvider();

			if (billingCycleService.findByBillingCycleCode(postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(BillingCycle.class, postData.getCode());
			}

			Calendar calendar = calendarService.findByName(postData.getCalendar(), provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {

			}
			if (StringUtils.isBlank(postData.getDescription())) {

			}
			if (StringUtils.isBlank(postData.getCalendar())) {

			}
			if (postData.getInvoiceDateDelay() == null) {

			}
			if (postData.getDueDateDelay() == null) {

			}
		}
	}

	public void update(BillingCycleDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCalendar()) && postData.getInvoiceDateDelay() != null
				&& postData.getDueDateDelay() != null) {

		} else {
			if (StringUtils.isBlank(postData.getCode())) {

			}
			if (StringUtils.isBlank(postData.getDescription())) {

			}
			if (StringUtils.isBlank(postData.getCalendar())) {

			}
			if (postData.getInvoiceDateDelay() == null) {

			}
			if (postData.getDueDateDelay() == null) {

			}
		}
	}

	public void find(String billingCycleCode, Provider provider) throws MeveoApiException {

	}

	public void remove(String billingCycleCode, Provider provider) throws MeveoApiException {

	}

}
