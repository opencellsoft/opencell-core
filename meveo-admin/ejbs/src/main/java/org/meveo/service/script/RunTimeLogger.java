package org.meveo.service.script;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

public class RunTimeLogger implements org.slf4j.Logger {
	 
	private Class<?> clazz;
	private String scriptCode;
	private String providerCode;
	
    private String SEP= "  ";
    private String DEBUG= "DEBUG";
    private String INFO= "INFO";
    private String TRACE= "TRACE";
    private String ERROR= "ERROR";
    private String WARN= "WARN";
    ScriptInstanceService scriptInstanceService;
   	
	/**
	 * 
	 * @param clazz
	 * @param providerCode
	 * @param scriptCode
	 */
	public RunTimeLogger( Class<?> clazz,String providerCode,String scriptCode){
		this.clazz = clazz;
		this.scriptCode=scriptCode;
		this.providerCode=providerCode;
		scriptInstanceService = (ScriptInstanceService) EjbUtils.getServiceInterface("ScriptInstanceService");
	}
	
	/**
	 * 
	 * @param level
	 * @param message
	 * @param throwable
	 */
	public void log(String level,String message,Throwable throwable){
		  log( level, message,throwable.getMessage());
		  StringWriter errors = new StringWriter();
		  throwable.printStackTrace(new PrintWriter(errors));
		  log( level, message,errors.toString());
	}
	
	/**
	 * 
	 * @param level
	 * @param message
	 * @param args
	 */
	public void log(String level,String message,Object...args){
		StringBuffer sb = new StringBuffer();
		sb.append(DateUtils.formatDateWithPattern(new Date(), "HH:mm:ss,SSS"));
		sb.append(SEP);
		sb.append(level);
		sb.append(SEP);		
		sb.append("["+clazz.getCanonicalName()+"]");
		sb.append(SEP);			
		sb.append(MessageFormatter.arrayFormat(message, args).getMessage() );
		sb.append("\n");
		scriptInstanceService.addLog(sb.toString(),providerCode,scriptCode);
	}
	
	@Override
	public void debug(String arg0) {
		log(DEBUG, arg0);		
	}

	@Override
	public void debug(String arg0, Object arg1) {
		log(DEBUG, arg0,arg1);	
		
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		log(DEBUG, arg0,arg1);
		
	}

	@Override
	public void debug(String arg0, Object arg1, Object arg2) {
		log(DEBUG, arg0,arg1,arg2);
		
	}


	@Override
	public void error(String arg0) {
		log(ERROR, arg0);
		
	}

	@Override
	public void error(String arg0, Object arg1) {
		log(ERROR, arg0,arg1);
		
	}

	@Override
	public void error(String arg0, Object... arg1) {
		log(ERROR, arg0,arg1);
		
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		log(ERROR, arg0,arg1);
		
	}

	@Override
	public void error(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String arg0, Object arg1, Object arg2) {
		log(ERROR, arg0,arg1,arg2);
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void info(String arg0) {
		log(INFO, arg0);
		
	}

	@Override
	public void info(String arg0, Object arg1) {
		log(INFO, arg0,arg1);
		
	}

	@Override
	public void info(String arg0, Object... arg1) {
		log(INFO, arg0,arg1);
		
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		log(INFO, arg0,arg1);
		
	}

	@Override
	public void info(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String arg0, Object arg1, Object arg2) {
		log(INFO, arg0,arg1,arg2);
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDebugEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isErrorEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInfoEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTraceEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWarnEnabled(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(String arg0) {
		log(TRACE, arg0);
		
	}

	@Override
	public void trace(String arg0, Object arg1) {
		log(TRACE, arg0,arg1);
		
	}

	@Override
	public void trace(String arg0, Object... arg1) {
		log(TRACE, arg0,arg1);
		
	}

	@Override
	public void trace(String arg0, Throwable arg1) {
		log(TRACE, arg0,arg1);
		
	}

	@Override
	public void trace(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String arg0, Object arg1, Object arg2) {
		log(TRACE, arg0,arg1,arg2);
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0) {
		log(WARN, arg0);
		
	}

	@Override
	public void warn(String arg0, Object arg1) {
		log(WARN, arg0,arg1);
		
	}

	@Override
	public void warn(String arg0, Object... arg1) {
		log(WARN, arg0,arg1);
		
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		log(WARN, arg0,arg1);
		
	}

	@Override
	public void warn(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String arg0, Object arg1, Object arg2) {
		log(WARN, arg0,arg1,arg2);
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String arg0, Object... arg1) {
		log(WARN, arg0,arg1);
		
	}

	@Override
	public void debug(Marker arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object... arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(Marker arg0, String arg1, Object arg2, Object arg3) {
		// TODO Auto-generated method stub
		
	}

	
}
