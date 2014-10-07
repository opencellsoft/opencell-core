/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.model.catalog;

import java.util.Date;

import javax.persistence.EntityManager;

import org.meveo.model.BeforeDBTest;
import org.meveo.model.shared.DateUtils;
import org.testng.Assert;

public class CalendarTest {

	// @Test(groups = { "db" })
	public void testNextCalendarDate() {
		EntityManager em = BeforeDBTest.factory.createEntityManager();
		Calendar cal = em.find(Calendar.class, 1L);
		Date nextDate = cal.nextCalendarDate(DateUtils.newDate(2010,
				java.util.Calendar.JANUARY, 5, 0, 0, 0));
		Assert.assertEquals(nextDate, DateUtils.newDate(2010,
				java.util.Calendar.JANUARY, 31, 0, 0, 0));
		Date nextDate2 = cal.nextCalendarDate(DateUtils.newDate(2010,
				java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
		Assert.assertEquals(nextDate2, DateUtils.newDate(2011,
				java.util.Calendar.JANUARY, 31, 0, 0, 0));
		Date nextDate3 = cal.nextCalendarDate(DateUtils.newDate(2010,
				java.util.Calendar.JANUARY, 31, 0, 0, 0));
		Assert.assertEquals(nextDate3, DateUtils.newDate(2011,
				java.util.Calendar.JANUARY, 31, 0, 0, 0));
		em.close();
	}

	// @Test(groups = { "db" })
	public void testPreviousCalendarDate() {
		EntityManager em = BeforeDBTest.factory.createEntityManager();
		Calendar cal = em.find(Calendar.class, 1L);
		Date nextDate = cal.previousCalendarDate(DateUtils.newDate(2010,
				java.util.Calendar.FEBRUARY, 5, 0, 0, 0));
		Assert.assertEquals(nextDate, DateUtils.newDate(2010,
				java.util.Calendar.JANUARY, 31, 0, 0, 0));
		Date nextDate2 = cal.previousCalendarDate(DateUtils.newDate(2010,
				java.util.Calendar.JANUARY, 5, 0, 0, 0));
		Assert.assertEquals(nextDate2, DateUtils.newDate(2009,
				java.util.Calendar.JANUARY, 31, 0, 0, 0));
		// Date nextDate3 = cal.previousCalendarDate(DateUtils.newDate(2010,
		// java.util.Calendar.JANUARY, 31, 0, 0, 0));
		// Assert.assertEquals(nextDate3, DateUtils.newDate(2009,
		// java.util.Calendar.JANUARY, 31, 0, 0, 0));
		em.close();
	}

}
