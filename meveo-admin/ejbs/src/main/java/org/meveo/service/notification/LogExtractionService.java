package org.meveo.service.notification;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.shared.DateUtils;

public class LogExtractionService {
	
	public static String  getLogs(Date fromDate,Date toDate){
		String result="";
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(System.getProperty("logging.configuration").substring(5)));
			String logFile = props.getProperty("handler.FILE.fileName");
			String dateFormat = props.getProperty("formatter.FILE.pattern").substring(props.getProperty("formatter.FILE.pattern").indexOf("{")+1, props.getProperty("formatter.FILE.pattern").indexOf("}"));
			List<String>  allLines = Files.readAllLines(Paths.get(logFile), StandardCharsets.UTF_8);
			List<String>  foundedLines = new ArrayList<String>();
			int length=0;
			for(String line : allLines){
				if(DateUtils.isDateTimeWithinPeriod(DateUtils.parseDateWithPattern(line.substring(0, dateFormat.length()), dateFormat), fromDate, toDate)){
					foundedLines.add(line);
					length += line.length();
				}
				if(length > Integer.parseInt(ParamBean.getInstance().getProperty("meveo.notifier.log.lenght", "100000"))){
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	

}
