package org.meveo.service.notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.model.notification.WebHook;
import org.meveo.model.notification.WebHookMethodEnum;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class WebHookNotifier {

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;
	
	@Inject
	Logger log;
	
	@Inject 
	NotificationHistoryService notificationHistoryService;

	private String evaluate(String expression,IEntity e) throws BusinessException{
		HashMap<Object,Object> userMap = new HashMap<Object, Object>();
		userMap.put("event", e);
		return (String)RatingService.evaluateExpression(expression, userMap, String.class);
	}
	
	private Map<String,String> evaluateMap(Map<String,String> map,IEntity e) throws BusinessException{
		Map<String,String> result = new HashMap<String,String>();
		HashMap<Object,Object> userMap = new HashMap<Object, Object>();
		userMap.put("event", e);
		for(String key:map.keySet()){
			result.put(key,(String)RatingService.evaluateExpression(map.get(key), userMap, String.class));
		}
		return result;
	}
	
	@Asynchronous
	public void sendRequest(WebHook webHook, IEntity e){
		log.debug("webhook sendRequest");
		String result="";
		try {
			String url  = webHook.getHost().startsWith("http")?webHook.getHost():"http://"+webHook.getHost();
			if(webHook.getPort()>0){
				url+=":"+webHook.getPort();
			}
			if(!StringUtils.isBlank(webHook.getPage())){
				url+="/"+evaluate(webHook.getPage(),e);
			}

			log.debug("webhook url: {}",url);
			Connection connection=HttpConnection.connect(url);
			connection.data(evaluateMap(webHook.getParams(),e));
			Map<String,String> headers=evaluateMap(webHook.getHeaders(),e);
			for(String key:headers.keySet()){
				connection.header(key, webHook.getHeaders().get(key));
			}
			if(WebHookMethodEnum.HTTP_GET==webHook.getHttpMethod()){
				result=connection.get().toString();
			} else {
				result = connection.post().toString();
			}
			//TODO: handle correctly non temporary errors like 404...
			notificationHistoryService.create(webHook, e, result,NotificationHistoryStatusEnum.SENT);
			log.debug("webhook answer : "+result);
		} catch (BusinessException e1) {
			try {
				log.debug("webhook business error : "+e1.getMessage());
				notificationHistoryService.create(webHook, e, e1.getMessage(),NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.debug("webhook history error : "+e2.getMessage());
				e2.printStackTrace();
			}
		} catch (IOException e1) {
			try {
				log.debug("webhook io error : "+e1.getMessage());
				notificationHistoryService.create(webHook, e, e1.getMessage(),NotificationHistoryStatusEnum.TO_RETRY);
			} catch (BusinessException e2) {
				log.debug("webhook history error : "+e2.getMessage());
				e2.printStackTrace();
			}
		}
	}
}
