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
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.slf4j.Logger;

@Stateless
public class DWHQueryBean {

	@Inject
	private MeasurableQuantityService mqService;

	@Inject
	private MeasuredValueService mvService;

	@PersistenceContext(unitName = "MeveoAdmin")
	private EntityManager em;

	@Inject
	private Logger log;

	// iso 8601 date and datetime format
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat tf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void executeQuery(JobExecutionResultImpl result, String parameter, User currentUser)
			throws BusinessException {
		Provider provider=currentUser.getProvider();
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
			mqList = mqService.listToBeExecuted(new Date(),provider);
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
				if(mq.getLastMeasureDate()==null){
					mq.setLastMeasureDate(mq.getPreviousDate(toDate));
				}
				while (mq.getNextMeasureDate().before(toDate)) {
					log.debug("resolve query:{}, nextMeasureDate={}, lastMeasureDate={}, provider={} ", mq.getSqlQuery(),mq.getNextMeasureDate(),mq.getLastMeasureDate(),provider.getId());
					String queryStr = mq.getSqlQuery().replaceAll("#\\{date\\}", df.format(mq.getLastMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{dateTime\\}", tf.format(mq.getLastMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{nextDate\\}", df.format(mq.getNextMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{nextDateTime\\}", tf.format(mq.getNextMeasureDate()));
					queryStr = queryStr.replaceAll("#\\{provider\\}", "" + provider.getId());
					log.debug("execute query:{}", queryStr);
					Query query = em.createNativeQuery(queryStr);
					@SuppressWarnings("unchecked")
					List<Object> results = query.getResultList();
					for (Object res : results) {
						MeasurementPeriodEnum mve = (mq.getMeasurementPeriod() != null) ? mq.getMeasurementPeriod()
								: MeasurementPeriodEnum.DAILY;
						BigDecimal value = BigDecimal.ZERO;
						Date date = mq.getLastMeasureDate();
						String dimension1 = mq.getDimension1();
						String dimension2 = mq.getDimension2();
						String dimension3 = mq.getDimension3();
						String dimension4 = mq.getDimension4();
						if (res instanceof Object[]) {
							Object[] resTab = (Object[]) res;
							value = new BigDecimal("" + resTab[0]);
							int i=1;
							if (resTab.length > i) {
								try{
									date = (Date) resTab[1];
									i++;
								} catch(Exception e){}
								if (resTab.length > i) {
									dimension1 = resTab[i]==null?"":resTab[i].toString();
									i++;
									if (resTab.length > i) {
										dimension2 = resTab[i]==null?"":resTab[i].toString();
										i++;
										if (resTab.length > i) {
											dimension3 = resTab[i]==null?"":resTab[i].toString();
											i++;
											if (resTab.length > i) {
												dimension4 = resTab[i]==null?"":resTab[i].toString();
											}
										}
									}
								}
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
						mv.setDimension1(dimension1);
						mv.setDimension2(dimension2);
						mv.setDimension3(dimension3);
						mv.setDimension4(dimension4);
						if (mv.getId() == null) {
							mvService.create(mv, currentUser);
						}
					}
					mq.increaseMeasureDate();
					result.registerSucces();
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.registerError("Measurable quantity with code " + measurableQuantityCode
						+ " contain invalid SQL query: " + e.getMessage());
				log.info("Measurable quantity with code " + measurableQuantityCode + " contain invalid SQL query: "
						+ e);
			}
		}
	}
}
