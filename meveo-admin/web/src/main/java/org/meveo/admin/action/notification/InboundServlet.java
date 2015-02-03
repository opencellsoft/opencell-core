package org.meveo.admin.action.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.model.notification.InboundRequest;
import org.meveo.service.notification.InboundRequestService;
import org.slf4j.Logger;

@WebServlet("/inbound/*")
public class InboundServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1551787937225264581L;
	
	@Inject
	InboundRequestService inboundRequestService;
	
	@Inject
	Logger log;

	@Inject @InboundRequestReceived
	protected Event<InboundRequest> eventProducer;
	
	public static int readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis)
		     throws IOException  {
		     int bufferOffset = 0;
		     long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
		     while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < b.length) {
		         int readLength = java.lang.Math.min(is.available(),b.length-bufferOffset);
		         // can alternatively use bufferedReader, guarded by isReady():
		         int readResult = is.read(b, bufferOffset, readLength);
		         if (readResult == -1) break;
		         bufferOffset += readResult;
		     }
		     return bufferOffset;
		 }
	
	private void doService(HttpServletRequest req, HttpServletResponse res){
		log.debug("received request for method {} ",req.getMethod());
		InboundRequest inReq= new InboundRequest();
		inReq.setCode(req.getRemoteAddr()+":"+req.getRemotePort()+"_"+req.getMethod()+"_"+System.nanoTime());

		inReq.setContentLength(req.getContentLength());
		inReq.setContentType(req.getContentType());
		
		if(req.getParameterNames()!=null){
			Enumeration<String> parameterNames = req.getParameterNames();
			while(parameterNames.hasMoreElements()){
				String parameterName=parameterNames.nextElement();
				String[] paramValues = req.getParameterValues(parameterName);
				String parameterValue = null;
				String sep="";
				for(String paramValue:paramValues){
					parameterValue = sep+paramValue;
					sep="|";
				}
				inReq.getParameters().put(parameterName, parameterValue);
			}
		}
		inReq.setProtocol(req.getProtocol());
		inReq.setScheme(req.getScheme());
		inReq.setRemoteAddr(req.getRemoteAddr());
		inReq.setRemotePort(req.getRemotePort());
		byte[] charBuffer= new byte[8000];
		String body = ""; 
		try {
			readInputStreamWithTimeout(req.getInputStream(),charBuffer,2000);
			body = new String(charBuffer);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		inReq.setBody(body);
		
		inReq.setMethod(req.getMethod());
		inReq.setAuthType(req.getAuthType());
		if(req.getCookies()!=null){
			for(Cookie cookie:req.getCookies()){
				inReq.getCoockies().put(cookie.getName(), cookie.getValue());
			}
		}
		if(req.getHeaderNames()!=null){
			Enumeration<String> headerNames = req.getHeaderNames();

			while(headerNames.hasMoreElements()){
				String headerName=headerNames.nextElement();
				inReq.getHeaders().put(headerName, req.getHeader(headerName));
			}
		}
		inReq.setPathInfo(req.getPathInfo());
		inReq.setRequestURI(req.getRequestURI());
		
		//process the notifications
		eventProducer.fire(inReq);

		log.debug("triggered {} notification",inReq.getNotificationHistories().size());
		if(inReq.getNotificationHistories().size()==0){
			res.setStatus(404);
		} else {
			//produce the response
			res.setCharacterEncoding(inReq.getResponseEncoding()==null?req.getCharacterEncoding():inReq.getResponseEncoding());
			res.setContentType(inReq.getContentType());
			for(String cookieName:inReq.getResponseCoockies().keySet()){
				res.addCookie(new Cookie(cookieName, inReq.getResponseCoockies().get(cookieName)));
			}
			for(String headerName:inReq.getResponseHeaders().keySet()){
				res.addHeader(headerName, inReq.getResponseHeaders().get(headerName));
			}
			if(inReq.getResponseBody()!=null){
				try (ByteArrayOutputStream bout = new ByteArrayOutputStream(8192);
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout, res.getCharacterEncoding()))){	
					pw.write(inReq.getResponseBody());
					res.setContentLength(bout.size());
					bout.writeTo(res.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
					res.setStatus(500);
				}
			}
			res.setStatus(200);
		}

		try {
			inboundRequestService.create(inReq);
		} catch (BusinessException e1) {
			e1.printStackTrace();
		}
		log.debug("exit with status {}",res.getStatus());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			    throws IOException, ServletException    {
		doService(req,res);
	}
	
	public void doDelete(HttpServletRequest req, HttpServletResponse res)
		    throws IOException, ServletException    {
		doService(req,res); 
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
		    throws IOException, ServletException    {
		doService(req,res);	 
	}
	
	public void doHead(HttpServletRequest req, HttpServletResponse res)
		    throws IOException, ServletException    {
		doService(req,res);
	}
	
	public void doOption(HttpServletRequest req, HttpServletResponse res)
		    throws IOException, ServletException    {
		doService(req,res);
	}
	
	public void doPut(HttpServletRequest req, HttpServletResponse res)
		    throws IOException, ServletException    {
		doService(req,res); 
	}
	
	public void doTrace(HttpServletRequest req, HttpServletResponse res)
		    throws IOException, ServletException    {
		doService(req,res); 
	}
	
}
