package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.InboundRequestService;

@Named
@ConversationScoped
public class InboundRequestBean extends BaseBean<InboundRequest> {

    private static final long serialVersionUID = -6762628879784107169L;

    @Inject
    InboundRequestService inboundRequestService;

    public InboundRequestBean() {
        super(InboundRequest.class);
    }

    @Override
    protected IPersistenceService<InboundRequest> getPersistenceService() {
        return inboundRequestService;
    }

    protected String getDefaultViewName() {
        return "inboundRequests";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("parameters", "coockies", "headers", "responseCoockies", "responseHeaders");
    }

    @Override
    public InboundRequest initEntity() {
        InboundRequest inboundRequest = super.initEntity();

        extractMapTypeFieldFromEntity(inboundRequest.getHeaders(), "headers");
        extractMapTypeFieldFromEntity(inboundRequest.getParameters(), "parameters");
        extractMapTypeFieldFromEntity(inboundRequest.getCoockies(), "coockies");
        extractMapTypeFieldFromEntity(inboundRequest.getResponseCoockies(), "responseCoockies");
        extractMapTypeFieldFromEntity(inboundRequest.getResponseHeaders(), "responseHeaders");

        return inboundRequest;
    }

    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        // Do not user a check against user.provider as it contains only one value, while user can be linked to various providers
        searchCriteria.put(PersistenceService.SEARCH_SKIP_PROVIDER_CONSTRAINT, true);

        return searchCriteria;
    }
    
    public void exportToFile() throws Exception {

        CsvBuilder csv = new CsvBuilder();
        csv.appendValue("Update date"); 
        csv.appendValue("From IP"); 
        csv.appendValue("Port"); 
        csv.appendValue("Protocol");
        csv.appendValue("Path info");
        csv.appendValue("Code");
        csv.appendValue("Active");
        csv.appendValue("Scheme");
        csv.appendValue("Content type");
        csv.appendValue("Content length");
        csv.appendValue("Method");
        csv.appendValue("Authentication type");
        csv.appendValue("Request URI");
        csv.appendValue("Cookies");
        csv.appendValue("Headers");
        csv.appendValue("Parameters");
        csv.appendValue("ContentType");
        csv.appendValue("Encoding");
        csv.appendValue("Cookies");
        csv.appendValue("Headers"); 
        csv.startNewLine();
        for(InboundRequest  inboundRequest:inboundRequestService.list()){ 
        	 csv.appendValue(DateUtils.formatDateWithPattern(inboundRequest.getAuditable().getUpdated(), "dd/MM/yyyy"));
        	 csv.appendValue(inboundRequest.getRemoteAddr());
        	 csv.appendValue(inboundRequest.getRemotePort()+"");
        	 csv.appendValue(inboundRequest.getProtocol());
        	 csv.appendValue(inboundRequest.getPathInfo());
        	 csv.appendValue(inboundRequest.getCode());
        	 csv.appendValue(inboundRequest.isDisabled()+"");
        	 csv.appendValue(inboundRequest.getScheme());
        	 csv.appendValue(inboundRequest.getContentType());
        	 csv.appendValue(inboundRequest.getContentLength()+"");
        	 csv.appendValue(inboundRequest.getMethod());
        	 csv.appendValue(inboundRequest.getAuthType());
        	 csv.appendValue(inboundRequest.getRequestURI());
        	 csv.appendValue(inboundRequest.getCoockies()+"");
        	 csv.appendValue(inboundRequest.getHeaders()+"");
        	 csv.appendValue(inboundRequest.getParameters()+"");
        	 csv.appendValue(inboundRequest.getResponseContentType()+"");
        	 csv.appendValue(inboundRequest.getResponseEncoding()+"");
        	 csv.appendValue(inboundRequest.getResponseCoockies()+"");
        	 csv.appendValue(inboundRequest.getResponseHeaders()+"");
        	 csv.startNewLine();
        }
        InputStream inputStream=new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "NotificationHistory.csv");
    }
    
    
    
    
}