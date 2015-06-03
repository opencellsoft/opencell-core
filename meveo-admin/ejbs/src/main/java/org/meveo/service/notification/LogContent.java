package org.meveo.service.notification;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.meveo.model.shared.DateUtils;

public class LogContent {
	
	public static String  getLogs(Date fromDate,Date toDate){
		
		String tmp="";
		
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(System.getProperty("logging.configuration").substring(5)));
			String logFile = props.getProperty("handler.FILE.fileName");
			String dateFormat = props.getProperty("formatter.FILE.pattern");
			String from=DateUtils.formatDateWithPattern(fromDate, "hh:mm:ss"), to=DateUtils.formatDateWithPattern(toDate, "hh:mm:ss");
			List<String>  lines = Files.readAllLines(Paths.get(logFile), StandardCharsets.UTF_8);
			int fromIndex=0,toIndex=0,i=0,linesSize = lines.size(),length=0;
			boolean isFromFounded =false,isToFounded=false;
			while(!isToFounded ||  !isFromFounded){
				String s = lines.get(i);
				if(!isFromFounded ){
					if(s.startsWith(from)){
						isFromFounded=true;
						fromIndex=i;
					}
				}
				if(!isToFounded ){
					if(s.startsWith(to)){
						isToFounded=true;
						toIndex=i;
					}
				}
				if((i== (linesSize-1)) ){					
					if(!isFromFounded && !isToFounded){
						fromIndex=0;
						toIndex=0;
						isFromFounded =true;
						isToFounded=true;
					}else if(!isFromFounded && isToFounded){
						fromIndex=toIndex/2;
						isFromFounded =true;
						isToFounded=true;
					}else if (isFromFounded && !isToFounded){
						toIndex=i;
						isFromFounded =true;
						isToFounded=true;
					}
				}else{
					i++;
				}
			}		 
			List<String> logs = lines.subList(fromIndex, toIndex==0?toIndex:(toIndex+1));
			for(String s : logs){
				length += s.length();
				tmp+=s +"\n";
				if(length > 100000){
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmp;
	}
	

}
