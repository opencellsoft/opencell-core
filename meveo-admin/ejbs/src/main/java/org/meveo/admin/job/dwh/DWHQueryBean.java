package org.meveo.admin.job.dwh;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.time.DateUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.model.dwh.MeasurementPeriodEnum;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.slf4j.Logger;

@Stateless
public class DWHQueryBean {

	@Inject
	private MeasurableQuantityService mqService;

	@Inject
	private MeasuredValueService mvService;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private Logger log;

	// iso 8601 date and datetime format
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:hh");

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void executeQuery(JobExecutionResultImpl result, String parameter, Provider provider)
			throws BusinessException {
		String measurableQuantityCode = parameter;
		Date toDate = new Date();

		if (!StringUtils.isBlank(parameter)) {
			if (parameter.indexOf("to=") > 0) {
				String s = parameter.substring(parameter.indexOf("to=") + 3);
				if (s.indexOf(";") > 0) {
					measurableQuantityCode = parameter.substring(parameter.indexOf(";") + 1);
					Date parsedDate = org.meveo.model.shared.DateUtils.guessDate(s.substring(0, s.indexOf(";")),
							"yyyy-MM-dd");
					if (parsedDate != null) {
						toDate = parsedDate;
					}
				} else {
					if (parameter.indexOf(";") > 0) {
						measurableQuantityCode = parameter.substring(0, parameter.indexOf(";"));
					} else {
						measurableQuantityCode = null;
					}
				}

			}
		}
		log.debug("measurableQuantityCode={}, toDate={}", measurableQuantityCode, toDate);
		// first we check that there is a measurable quantity for the given
		// provider
		List<MeasurableQuantity> mqList = new ArrayList<>();
		if (StringUtils.isBlank(measurableQuantityCode)) {
			mqList = mqService.listToBeExecuted(new Date());
		} else {
			MeasurableQuantity mq = mqService.findByCode(em, measurableQuantityCode, provider);
			if (mq == null) {
				result.registerError("Cannot find measurable quantity with code " + measurableQuantityCode);
				result.setReport("Cannot find measurable quantity with code " + measurableQuantityCode);
				return;
			}
			mqList.add(mq);
		}
		result.setNbItemsToProcess(mqList.size());
		for (MeasurableQuantity mq : mqList) {
			if (StringUtils.isBlank(mq.getSqlQuery())) {
				result.registerError("Measurable quantity with code " + measurableQuantityCode
						+ " has no SQL query set.");
				log.info("Measurable quantity with code {} has no SQL query set.", measurableQuantityCode);
				continue;
			} else if (mq.getSqlQuery().indexOf("#{provider}") < 0) {
				result.registerError("Measurable quantity with code " + measurableQuantityCode
						+ " must filter result by provider using the #{provider} variable.");
				log.info("Measurable quantity with code " + measurableQuantityCode
						+ " must filter result by provider using the #{provider} variable.");
			}

			try {
				while (mq.getLastMeasureDate() == null || mq.getNextMeasureDate().before(toDate)) {
					String queryStr = mq.getSqlQuery().replaceAll("#\\{date\\}", df.format(mq.getNextMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{dateTime\\}", tf.format(mq.getNextMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{nextDate\\}", df.format(mq.getLastMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{nextDateTime\\}", tf.format(mq.getLastMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{provider\\}", "" + provider.getId());
					log.debug("execute query:{}", queryStr);
					Query query = em.createNativeQuery(queryStr);
					@SuppressWarnings("unchecked")
					List<Object> results = query.getResultList();
					for (Object res : results) {
						MeasurementPeriodEnum mve = (mq.getMeasurementPeriod() != null) ? mq.getMeasurementPeriod()
								: MeasurementPeriodEnum.DAILY;
						BigDecimal value = BigDecimal.ZERO;
						Date date = mq.getNextMeasureDate();
						if (res instanceof Object[]) {
							Object[] resTab = (Object[]) res;
							value = new BigDecimal("" + resTab[0]);
							if (resTab.length > 1) {
								date = (Date) resTab[1];
							}
						} else {
							value = new BigDecimal("" + res);
						}
						date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
						MeasuredValue mv = mvService.getByDate(em, date, mve, mq);
						if (mv == null) {
							mv = new MeasuredValue();
						}
						mv.setProvider(provider);
						mv.setMeasurableQuantity(mq);
						mv.setMeasurementPeriod(mve);
						mv.setValue(value);
						mv.setDate(date);
						if (mv.getId() == null) {
							mvService.create(mv, null, provider);
						}
					}
					mq.increaseMeasureDate();
					result.registerSucces();
				}
			} catch (Exception e) {
				result.registerError("Measurable quantity with code " + measurableQuantityCode
						+ " contain invalid SQL query: " + e.getMessage());
				log.info("Measurable quantity with code " + measurableQuantityCode + " contain invalid SQL query: "
						+ e.getMessage());
			}
		}
	}
}
