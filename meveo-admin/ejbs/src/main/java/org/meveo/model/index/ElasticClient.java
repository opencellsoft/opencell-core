package org.meveo.model.index;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

@Startup
@Singleton
/**
 * Connect to an elastic search cluster
 * Not that for the nodes, the index name is set to provider code and type to entity type
 *
 * @author smichea
 *
 */
public class ElasticClient {
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	@Inject
	private Logger log;
	
	private ParamBean paramBean=ParamBean.getInstance();

	
	TransportClient client = null;

	@PostConstruct
	private void init() throws UnknownHostException {
		try{
		String clusterName = paramBean.getProperty("elasticsearch.cluster.name", "elasticsearch");
		String[] hosts=paramBean.getProperty("elasticsearch.hosts", "localhost").split(";");
		String portStr = paramBean.getProperty("elasticsearch.port", "9300");
		String sniffingStr = paramBean.getProperty("elasticsearch.client.transport.sniff", "false").toLowerCase();
		if(!StringUtils.isBlank(portStr) && StringUtils.isNumeric(portStr)
				&& (sniffingStr.equals("true")||sniffingStr.equals("false"))
				&& !StringUtils.isBlank(clusterName) && hosts.length>0){
			log.debug("Connecting to elasticsearch cluster {} and hosts {}",clusterName,StringUtils.join(hosts,";"));
			boolean sniffing = Boolean.parseBoolean(sniffingStr);
			Settings settings = Settings.settingsBuilder()
					.put("client.transport.sniff", sniffing)
			        .put("cluster.name",clusterName).build();
			client = TransportClient.builder().settings(settings).build();
			int port = Integer.parseInt(portStr);
			for(String host:hosts){
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));
			}
			log.debug("connected elasticsearch nodes : {}",client.connectedNodes());
		}
		} catch (Error e){
			log.error("Error while initializing elastic search");
			e.printStackTrace();
		}
	}
		
	/**
	 * 
	 * @param esDoc
	 * @param type
	 * @param index
	 */
	public void createOrUpdate(ElasticDocument esDoc,String type,String index){
		if(client!=null){
			try {
				log.debug("added elasticSearch doc {} ",esDoc.toJson());
				IndexResponse response = client.prepareIndex(index.toLowerCase(),type.toLowerCase(),esDoc.getCode()).setSource(esDoc.toJson()).get();
				if(response.isCreated()){
					log.debug("added entity {} of type {} to index {}",esDoc.getCode(),type,index);
				} else {
					log.warn("updated existing entity {} of type {} to index {}, version={}",esDoc.getCode(),type,index,response.getVersion());
				}
			} catch (Exception e1) {
				log.error("cant create ES Document",e1);
			}
		}else{
			log.warn("Elastic client down");
		}
		
	}
    
	/**
	 * 
	 * @param e
	 * @param creator
	 */
	public void createOrUpdate(BusinessEntity e,User creator){		
		ElasticDocument esDoc = new ElasticDocument(e);		
		createOrUpdate(esDoc, e.getClass().getName(), e.getProvider().getCode());		
	}
	
	
	@PreDestroy
	private void shutdown(){
		client.close();
	}
}
