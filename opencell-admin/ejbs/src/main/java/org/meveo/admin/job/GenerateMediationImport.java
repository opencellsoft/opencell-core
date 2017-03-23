package org.meveo.admin.job;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Inject;

import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
public class GenerateMediationImport {

	private final int START_ROW = 0;
	private final int MAX_ROW = 9999;
	private String outputFile = "c://temp//";
	private final String ACCESS_NAME = "IMSI";
	private final String PARAM1 = "DATA";
	private final String PARAM2 = "";
	private final String PARAM3 = "";
	private final String PARAM4 = "";
	private final int QUANTITY = 5;

	public static void main(String args[]) {
		new GenerateMediationImport();
	}
	
	@Inject
	private Logger log;

	public GenerateMediationImport() {
		System.out.println("start creating mediation file...");
		outputFile = outputFile + ACCESS_NAME + "_" + MAX_ROW + ".csv";
		PrintWriter out = null;
		int count = 0;

		try {
			out = new PrintWriter(new File(outputFile));
			Calendar c = Calendar.getInstance();
			c.setTime(new Date()); // Now use today date.
			c.add(Calendar.DATE, 2); // Adding 2 days

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			
			for (int i = START_ROW; i <= MAX_ROW; i++) {
				StringBuilder line = new StringBuilder(sdf.format(c.getTime()) + ";" + QUANTITY + ";" + ACCESS_NAME + i);
				if (!StringUtils.isBlank(PARAM1)) {
					line.append(";" + PARAM1);
				}
				if (!StringUtils.isBlank(PARAM2)) {
					line.append(";" + PARAM2);
				}
				if (!StringUtils.isBlank(PARAM3)) {
					line.append(";" + PARAM3);
				}
				if (!StringUtils.isBlank(PARAM4)) {
					line.append(";" + PARAM4);
				}
				out.println(line.toString());
				count++;
			}

			out.flush();
		} catch (Exception e) {
			log.error("Failed to generate mediation import",e);
		} finally {
			if (out != null) {
				out.close();
			}
		}

		System.out.println("DONE. " + count + " created.");
	}
}
