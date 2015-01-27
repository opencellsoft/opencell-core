package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CalendarDto;
import org.meveo.api.dto.DayInYearDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarTypeEnum;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.MonthEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DayInYearService;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CalendarApi extends BaseApi {

	@Inject
	private Logger log;

	@Inject
	private CalendarService calendarService;

	@Inject
	private DayInYearService dayInYearService;

	public void create(CalendarDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getName()) && !StringUtils.isBlank(postData.getType())) {
			Provider provider = currentUser.getProvider();

			if (calendarService.findByName(postData.getName(), provider) != null) {
				throw new EntityAlreadyExistsException(CalendarYearly.class, postData.getName());
			}

			CalendarYearly calendar = new CalendarYearly();
			calendar.setName(postData.getName());
			calendar.setDescription(postData.getDescription());
			try {
				calendar.setType(CalendarTypeEnum.valueOf(postData.getType()));
			} catch (IllegalArgumentException e) {
				log.error("type{}", e.getMessage());
			}
			if (postData.getDays() != null && postData.getDays().size() > 0) {
				List<DayInYear> days = new ArrayList<DayInYear>();
				for (DayInYearDto d : postData.getDays()) {
					try {
						DayInYear dayInYear = dayInYearService.findByMonthAndDay(MonthEnum.valueOf(d.getMonth()),
								d.getDay());
						if (dayInYear != null) {
							days.add(dayInYear);
						}
					} catch (IllegalArgumentException e) {
						log.warn("month={}", e.getMessage());
					}
				}

				calendar.setDays(days);
			}

			calendarService.create(calendar, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getType())) {
				missingParameters.add("type");
			}
		}
	}

	public void update(CalendarDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getName()) && !StringUtils.isBlank(postData.getType())) {
			Provider provider = currentUser.getProvider();

			Calendar calendar = calendarService.findByName(postData.getName(), provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(CalendarYearly.class, postData.getName());
			}

			calendar.setDescription(postData.getDescription());
			try {
				calendar.setType(CalendarTypeEnum.valueOf(postData.getType()));
			} catch (IllegalArgumentException e) {
				log.error("type{}", e.getMessage());
			}
			if (calendar instanceof CalendarYearly) {
				if (postData.getDays() != null && postData.getDays().size() > 0) {
					List<DayInYear> days = new ArrayList<DayInYear>();
					for (DayInYearDto d : postData.getDays()) {
						try {
							DayInYear dayInYear = dayInYearService.findByMonthAndDay(MonthEnum.valueOf(d.getMonth()),
									d.getDay());
							if (dayInYear != null) {
								days.add(dayInYear);
							}
						} catch (IllegalArgumentException e) {
							log.warn("month={}", e.getMessage());
						}
					}

					((CalendarYearly) calendar).setDays(days);
				}
			}

			calendarService.create(calendar, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getName())) {
				missingParameters.add("name");
			}
			if (StringUtils.isBlank(postData.getType())) {
				missingParameters.add("type");
			}
		}
	}

	public CalendarDto find(String calendarCode, Provider provider) throws MeveoApiException {
		CalendarDto result = new CalendarDto();

		if (!StringUtils.isBlank(calendarCode)) {
			Calendar calendar = calendarService.findByName(calendarCode, provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class, calendarCode);
			}

			result = new CalendarDto(calendar);
		} else {
			if (StringUtils.isBlank(calendarCode)) {
				missingParameters.add("calendarCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

		return result;
	}

	public void remove(String calendarCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(calendarCode)) {
			Calendar calendar = calendarService.findByName(calendarCode, provider);
			if (calendar == null) {
				throw new EntityDoesNotExistsException(Calendar.class, calendarCode);
			}

			calendarService.remove(calendar);
		} else {
			if (StringUtils.isBlank(calendarCode)) {
				missingParameters.add("calendarCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}
