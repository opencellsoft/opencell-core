package org.meveo.model.index;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.*;
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
	
	public void update(BusinessEntity e,User updater) throws BusinessException {
		if(client!=null){
			try {
				XContentBuilder builder = jsonBuilder()
					.startObject()
					    .field("updated",e.getAuditable().getUpdated())
					    .field("updater",e.getAuditable().getUpdater().getUserName());
				if(e instanceof BusinessCFEntity){
						
				}
				String index=updater.getProvider().getCode().toLowerCase();
				String type =  e.getClass().getName().toLowerCase();
				IndexResponse response = client.prepareIndex(index,type,e.getCode())
			        .setSource(builder.endObject())
			        .get();
				if(response.isCreated()){
					log.warn("added new entity {} of type {} to index {}",e.getCode(),type,index);
				} else {
					log.debug("updated entity {} of type {} to index {}, version={}",e.getCode(),type,index,response.getVersion());
				}
			} catch (IOException e1) {
				throw new BusinessException(e1);
			}
		}
	}
	
	public void create(BusinessEntity e,User creator) throws BusinessException {
		if(client!=null){
			try {
			XContentBuilder builder = jsonBuilder()
				.startObject()
				    .field("code",e.getCode())
				    .field("description",e.getDescription())
				    .field("created",e.getAuditable().getCreated())
				    .field("creator",e.getAuditable().getCreator().getUserName());
				if(e instanceof BusinessCFEntity){
						
				}
				String index=creator.getProvider().getCode().toLowerCase();
				String type =  e.getClass().getName().toLowerCase();
				IndexResponse response = client.prepareIndex(index,type,e.getCode())
			        .setSource(builder.endObject())
			        .get();
				if(response.isCreated()){
					log.debug("added entity {} of type {} to index {}",e.getCode(),type,index);
				} else {
					log.warn("updated existing entity {} of type {} to index {}, version={}",e.getCode(),type,index,response.getVersion());
				}
			} catch (IOException e1) {
				throw new BusinessException(e1);
			}
		}
	}
	
	
	@PreDestroy
	private void shutdown(){
		client.close();
	}
}
