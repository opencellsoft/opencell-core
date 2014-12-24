/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarYearly;
import org.meveo.model.catalog.DayInYear;
import org.meveo.model.catalog.HourInDay;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.DayInYearService;
import org.meveo.service.catalog.impl.HourInDayService;
import org.primefaces.model.DualListModel;

@Named
@ConversationScoped
public class CalendarBean extends BaseBean<Calendar> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link Calendar} service. Extends {@link PersistenceService}. */
	@Inject
	private CalendarService calendarService;

	@Inject
	private DayInYearService dayInYearService;

	private DualListModel<DayInYear> dayInYearListModel;

	@Inject
	private HourInDayService hourInDayService;

	private DualListModel<HourInDay> hourInDayListModel;

	@Inject
	@RequestParam()
	private Instance<String> classType;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CalendarBean() {
		super(Calendar.class);
	}

	public Calendar getInstance() throws InstantiationException,
			IllegalAccessException {
		// TODO: should take classType into account
		return CalendarYearly.class.newInstance();
	}

	public Calendar initEntity() {

		log.debug("instantiating calendar of type " + this.getClass());
		if (getObjectId() != null) {
			if (getFormFieldsToFetch() == null) {
				entity = (Calendar) getPersistenceService().findById(
						getObjectId());
			} else {
				entity = (Calendar) getPersistenceService().findById(
						getObjectId(), getFormFieldsToFetch());
			}
			// getPersistenceService().detach(entity);
		} else {
			try {
				entity = getInstance();
				if (entity instanceof BaseEntity) {
					((BaseEntity) entity).setProvider(getCurrentProvider());
				}
			} catch (InstantiationException e) {
				log.error("Unexpected error!", e);
				throw new IllegalStateException(
						"could not instantiate a class, abstract class");
			} catch (IllegalAccessException e) {
				log.error("Unexpected error!", e);
				throw new IllegalStateException(
						"could not instantiate a class, constructor not accessible");
			}
		}

		return entity;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Calendar> getPersistenceService() {
		return calendarService;
	}

	public DualListModel<DayInYear> getDayInYearModel() {
		if (dayInYearListModel == null) {
			List<DayInYear> perksSource = dayInYearService.list();
			List<DayInYear> perksTarget = new ArrayList<DayInYear>();
			if (((CalendarYearly) getEntity()).getDays() != null) {
				perksTarget.addAll(((CalendarYearly) getEntity()).getDays());
			}
			perksSource.removeAll(perksTarget);
			dayInYearListModel = new DualListModel<DayInYear>(perksSource,
					perksTarget);
		}
		return dayInYearListModel;
	}

	public void setDayInYearModel(DualListModel<DayInYear> perks) {
		((CalendarYearly) getEntity()).setDays((List<DayInYear>) perks
				.getTarget());
	}

	public DualListModel<HourInDay> getHourInDayModel() {
		if (hourInDayListModel == null) {
			List<HourInDay> perksSource = hourInDayService.list();
			List<HourInDay> perksTarget = new ArrayList<HourInDay>();
			if (((CalendarDaily) getEntity()).getHours() != null) {
				perksTarget.addAll(((CalendarDaily) getEntity()).getHours());
			}
			perksSource.removeAll(perksTarget);
			hourInDayListModel = new DualListModel<HourInDay>(perksSource,
					perksTarget);
		}
		return hourInDayListModel;
	}

	public void setHourInDayModel(DualListModel<HourInDay> perks) {
		((CalendarDaily) getEntity()).setHours((List<HourInDay>) perks
				.getTarget());
	}

	@Override
	protected String getDefaultSort() {
		return "name";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}
}