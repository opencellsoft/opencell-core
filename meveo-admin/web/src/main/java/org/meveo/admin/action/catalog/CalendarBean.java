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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.faces.component.EditableValueHolder;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.DiscriminatorValue;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CalendarDaily;
import org.meveo.model.catalog.CalendarPeriod;
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
public class CalendarBean extends StatelessBaseBean<Calendar> {

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

    @Inject
    private ResourceBundle resourceMessages;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CalendarBean() {
        super(Calendar.class);
    }

    public Calendar getInstance() throws InstantiationException, IllegalAccessException {

//        Calendar calendar = CalendarYearly.class.newInstance();
//        calendar.setCalendarType(CalendarYearly.class.getAnnotation(DiscriminatorValue.class).value());

        Calendar calendar = CalendarDaily.class.newInstance();
        calendar.setCalendarType(CalendarDaily.class.getAnnotation(DiscriminatorValue.class).value());

        return calendar;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Calendar> getPersistenceService() {
        return calendarService;
    }

    public DualListModel<DayInYear> getDayInYearModel() {

        if (dayInYearListModel == null && getEntity() instanceof CalendarYearly) {
            List<DayInYear> perksSource = dayInYearService.list();
            List<DayInYear> perksTarget = new ArrayList<DayInYear>();
            if (((CalendarYearly) getEntity()).getDays() != null) {
                perksTarget.addAll(((CalendarYearly) getEntity()).getDays());
            }
            perksSource.removeAll(perksTarget);
            dayInYearListModel = new DualListModel<DayInYear>(perksSource, perksTarget);
        }
        return dayInYearListModel;
    }

    public void setDayInYearModel(DualListModel<DayInYear> perks) {
        ((CalendarYearly) getEntity()).setDays((List<DayInYear>) perks.getTarget());
    }

    public DualListModel<HourInDay> getHourInDayModel() {
        if (hourInDayListModel == null && getEntity() instanceof CalendarDaily) {
            List<HourInDay> perksSource = hourInDayService.list();
            List<HourInDay> perksTarget = new ArrayList<HourInDay>();
            if (((CalendarDaily) getEntity()).getHours() != null) {
                perksTarget.addAll(((CalendarDaily) getEntity()).getHours());
            }
            perksSource.removeAll(perksTarget);
            hourInDayListModel = new DualListModel<HourInDay>(perksSource, perksTarget);
        }
        return hourInDayListModel;
    }

    public void setHourInDayModel(DualListModel<HourInDay> perks) {
        ((CalendarDaily) getEntity()).setHours((List<HourInDay>) perks.getTarget());
    }

    @Override
    protected String getDefaultSort() {
        return "name";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider");
    }

    public Map<String, String> getCalendarTypes() {
        Map<String, String> values = new HashMap<String, String>();

        values.put("DAILY", resourceMessages.getString("calendar.calendarType.DAILY"));
        values.put("YEARLY", resourceMessages.getString("calendar.calendarType.YEARLY"));
        values.put("PERIOD", resourceMessages.getString("calendar.calendarType.PERIOD"));

        return values;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void changeCalendarType(AjaxBehaviorEvent event) {

        String newType = (String) ((EditableValueHolder) event.getComponent()).getValue();

        Class[] classes = { CalendarYearly.class, CalendarDaily.class, CalendarPeriod.class };
        for (Class clazz : classes) {

            if (newType.equalsIgnoreCase(((DiscriminatorValue) clazz.getAnnotation(DiscriminatorValue.class)).value())) {

                try {
                    Calendar calendar = (Calendar) clazz.newInstance();

                    calendar.setCalendarType(((DiscriminatorValue) clazz.getAnnotation(DiscriminatorValue.class)).value());
                    setEntity(calendar);
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("Failed to instantiate a calendar", e);
                }
                return;
            }
        }
    }
}