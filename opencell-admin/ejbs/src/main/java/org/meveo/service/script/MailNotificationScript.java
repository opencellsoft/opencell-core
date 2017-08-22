package org.meveo.service.script;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.communication.impl.EmailSender;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MailNotificationScript extends Script {

	private EmailSender emailSender = (EmailSender) getServiceInterface("EmailSender");
	private EdrService edrService = (EdrService) getServiceInterface("EdrService");

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {

		String jobNativeQuery = "SELECT meveo_job_instance.code, start_date, (("
				+ "DATE_PART('day', end_date\\:\\:timestamp - start_date\\:\\:timestamp) * 24 + "
				+ "DATE_PART('hour', end_date\\:\\:timestamp - start_date\\:\\:timestamp)) * 60 + "
				+ "DATE_PART('minute', end_date\\:\\:timestamp - start_date\\:\\:timestamp)) * 60 + "
				+ "DATE_PART('second', end_date\\:\\:timestamp - start_date\\:\\:timestamp) as time_in_seconds FROM job_execution, meveo_job_instance "
				+ "WHERE job_execution.job_instance_id=meveo_job_instance.id order by start_date";
		

		String nbBASQL = "SELECT count  (*) FROM billing_billing_account";
		EntityManager entityManager = edrService.getEntityManager();

		Query query = entityManager.createNativeQuery(jobNativeQuery);
		List<Object[]> rows = query.getResultList();
		Map<String, Date> jobDateMap = new HashMap<>();
		Map<String, Double> jobDuration = new HashMap<>();

		Query nbBAQuery = entityManager.createNativeQuery(nbBASQL);

		BigInteger nbBA = (BigInteger) nbBAQuery.getSingleResult();

		for (Object[] row : rows) {
			try {
				jobDateMap.put((String) row[0], (Date) row[1]);
				jobDuration.put((String) row[0], (Double) row[2]);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
		

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<title> Test Perfs Result</title>");
		stringBuilder.append("</head>");
		stringBuilder.append("<body>");
		stringBuilder.append("<table border=\"1\">");
		stringBuilder.append("<thead>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<th>JOB</th>");
		stringBuilder.append("<th>STAT DATE</th>");
		stringBuilder.append("<th>DURATION</th>");
		stringBuilder.append("</tr>");
		stringBuilder.append("</thead>");
		stringBuilder.append("<tbody>");
		Set<Entry<String, Date>> entrySet = jobDateMap.entrySet();
		for (Entry<String, Date> entry : entrySet) {
			stringBuilder.append("<tr>");
			String key = entry.getKey();
			stringBuilder.append("<td>" + key + "</td>");
			stringBuilder.append("<td>" + entry.getValue() + "</td>");
			stringBuilder.append("<td>" + jobDuration.get(key) + "</td>");
			stringBuilder.append("<tr>");
		}

		stringBuilder.append("</tbody>");
		stringBuilder.append("</table>");

		stringBuilder.append("<br>");

		stringBuilder.append("For :" + nbBA + " billing accounts");

		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
		
		System.out.println(stringBuilder.toString());

		Map<String, Object> emailParams = new HashMap<String, Object>();
		emailParams.put("JobStartDate", jobDateMap);
		emailParams.put("JobDuration", jobDateMap);
		emailParams.put("nbBA", nbBA);

		String from = "tien_lan.phung@opencellsoft.com";
		String replyTo = "tien_lan.phung@opencellsoft.com";
		String to = "antoine.michea@opencellsoft.com";
		String cc = "antoine.michea@opencellsoft.com";

		try {
			emailSender.sent(from, Arrays.asList(replyTo), Arrays.asList(to), cc == null ? null : Arrays.asList(cc),
					null, "Test Perfs Result", null, stringBuilder.toString(), null, new Date());
			methodContext.put(Script.RESULT_VALUE, "Email sent");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
