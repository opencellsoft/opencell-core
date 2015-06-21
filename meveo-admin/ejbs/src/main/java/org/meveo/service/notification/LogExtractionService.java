package org.meveo.service.notification;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.joda.time.DateTimeComparator;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lauch a jobInstance and create a notificationHistory
 * 
 * @author anasseh
 * @created 19.06.2015
 * 
 */

public class LogExtractionService {
	
	private static Logger log = LoggerFactory.getLogger(LogExtractionService.class);

	public static String  getLogs(Date fromDate,Date toDate){
		String result="";
		BufferedReader logReader = null;
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(System.getProperty("logging.configuration").substring(5)));
			String logFile = props.getProperty("handler.FILE.fileName");			
			String dateFormat = props.getProperty("formatter.FILE.pattern").substring(props.getProperty("formatter.FILE.pattern").indexOf("{")+1, props.getProperty("formatter.FILE.pattern").indexOf("}"));			
			int length=0,maxLength=Integer.parseInt(ParamBean.getInstance().getProperty("meveo.notifier.log.lenght", "100000"));
			DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
			boolean mustBeInToo=false;
			Date dateCurrentLine =null;
			String line=null;
			boolean isAfterToDate = false;
			logReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
			while((line = logReader.readLine()) != null && length < maxLength && !isAfterToDate){				
				dateCurrentLine = getDateTime(line,dateFormat);			
				if(( dateCurrentLine ==null && mustBeInToo) || //include the line that not start by a date but it is in the period
						(dateCurrentLine !=null && comparator.compare(dateCurrentLine, fromDate) >= 0 &&  comparator.compare(dateCurrentLine, toDate) <= 0)){
					result+=line+"\n";
					length += line.length();
					mustBeInToo=true;
				}else{
					mustBeInToo=false;
				}
				if(dateCurrentLine != null){
					if(dateCurrentLine.after(toDate)){
						isAfterToDate = true;
					}
				}				
			}
		} catch (IOException e) {
			log.warn("",e);			
		}finally{			
			if(logReader != null){
				try {
					logReader.close();
				} catch (IOException e) {
					log.warn("",e);
				}
			}
		}
		return result;
	}

	private static Date getDateTime(String line, String dateFormat){
		Date result = null;
		if(line == null){
			return null;
		}
		if(line.length() < dateFormat.length()){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		try {
			result = sdf.parse(line.substring(0, dateFormat.length()));
		} catch (Exception e) {
		}
		return result;
	}
}
