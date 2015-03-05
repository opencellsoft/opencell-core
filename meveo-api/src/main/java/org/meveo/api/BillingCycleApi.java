package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
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

			Calendar calendar = calendarService.findByCode(postData.getCalendar(), provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
			}

			BillingCycle billingCycle = new BillingCycle();
			billingCycle.setCode(postData.getCode());
			billingCycle.setDescription(postData.getDescription());
			billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
			billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
			billingCycle.setDueDateDelay(postData.getDueDateDelay());
			billingCycle.setCalendar(calendar);

			billingCycleService.create(billingCycle, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCalendar())) {
				missingParameters.add("calendar");
			}
			if (postData.getInvoiceDateDelay() == null) {
				missingParameters.add("invoiceDateDelay");
			}
			if (postData.getDueDateDelay() == null) {
				missingParameters.add("dueDateDelay");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(BillingCycleDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCalendar()) && postData.getInvoiceDateDelay() != null
				&& postData.getDueDateDelay() != null) {
			Provider provider = currentUser.getProvider();

			BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(postData.getCode(), provider);

			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, postData.getCode());
			}

			Calendar calendar = calendarService.findByCode(postData.getCalendar(), provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class, postData.getCalendar());
			}

			billingCycle.setDescription(postData.getDescription());
			billingCycle.setBillingTemplateName(postData.getBillingTemplateName());
			billingCycle.setInvoiceDateDelay(postData.getInvoiceDateDelay());
			billingCycle.setDueDateDelay(postData.getDueDateDelay());
			billingCycle.setCalendar(calendar);

			billingCycleService.update(billingCycle, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCalendar())) {
				missingParameters.add("calendar");
			}
			if (postData.getInvoiceDateDelay() == null) {
				missingParameters.add("invoiceDateDelay");
			}
			if (postData.getDueDateDelay() == null) {
				missingParameters.add("dueDateDelay");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public BillingCycleDto find(String billingCycleCode, Provider provider) throws MeveoApiException {
		BillingCycleDto result = new BillingCycleDto();

		if (!StringUtils.isBlank(billingCycleCode)) {
			BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
			}

			result = new BillingCycleDto(billingCycle);
		} else {
			if (StringUtils.isBlank(billingCycleCode)) {
				missingParameters.add("billingCycleCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String billingCycleCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(billingCycleCode)) {
			BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(billingCycleCode, provider);
			if (billingCycle == null) {
				throw new EntityDoesNotExistsException(BillingCycle.class, billingCycleCode);
			}

			billingCycleService.remove(billingCycle);
		} else {
			if (StringUtils.isBlank(billingCycleCode)) {
				missingParameters.add("billingCycleCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
}
