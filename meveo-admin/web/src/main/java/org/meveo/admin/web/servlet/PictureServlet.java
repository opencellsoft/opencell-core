package org.meveo.admin.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.ProductOfferingService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * Show a picture from a rest URI like /meveo/picture/provider/module/tmp/filename.suffix
 * or  /meveo/picture/provider/offer/offerCode
 * or  /meveo/picture/provider/service/serviceCode
 * 
 * 3 provider code
 * 4 group : module or offer or service
 * 5 tmp, read pictures from tmp folder
 * 6 picture filename like entity's code with suffix png,gif,jpeg,jpg
 * 
 *
 */
@WebServlet(name = "pictureServlet", urlPatterns = "/picture/*",loadOnStartup=1000)
public class PictureServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Log log=LogFactory.getLog(PictureServlet.class);
	
	@Inject
	ProviderService providerService; 
	
	@Inject
	ProductOfferingService productOfferingService;
	
	@Inject
	ServiceTemplateService serviceTemplateService;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		showPicture(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		showPicture(req, resp);
	}

	private void showPicture(HttpServletRequest req, HttpServletResponse resp) {
		String url=req.getRequestURI();
		String[] path=url.split("/");
		if(path==null||(path.length<5)){
			return;
		}
		String rootPath=null;
		String filename=null;
		String provider=path[3];
		String groupname=path[4];
		try{
			if(path.length==7&&path[5].equals("tmp")){
				rootPath=ModuleUtil.getTmpRootPath(provider);
				filename=path[6];
			}else if(path.length==6){
				rootPath=ModuleUtil.getPicturePath(provider,groupname);
				filename=path[5];
			}else{
				log.error("error context path "+url);
				return;
			}
		}catch(Exception e){
			log.error("error when read picture path. Reason "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()));
			return;
		}

		byte[] data = null;
		if(filename.indexOf(".")>0 || (!groupname.equals("offer") && !groupname.equals("service")) ){
			String destfile=rootPath+File.separator+filename;
			log.debug("read a picture file from "+ destfile);
			File file=new File(destfile);
			if(!file.exists()){
				
				log.debug("Picture file isn't existed "+destfile);
				return;
			}
			try {
				data=ModuleUtil.readPicture(destfile);
			} catch (IOException e) {
				log.error("error when read picture from "+destfile+" , info "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()),e);
			}
		}
		else {
			Provider providerEntity = providerService.findByCode(provider);
			if(providerEntity==null){
				log.error("Provider "+provider+" does not exist");
				return;
			}
			if("offer".equals(groupname)){
				ProductOffering offering = productOfferingService.findByCode(filename, providerEntity);
				if(offering==null){
					log.error("Product, bundle or offer with code "+offering+" does not exist");
					return;
				}
				data=offering.getImageAsByteArr();
			} else if ("offer".equals(groupname)){
				ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(filename, providerEntity);
				if(serviceTemplate==null){
					log.error("Service with code "+serviceTemplate+" does not exist");
					return;
				}
				//FIXME uncomment this
				//data=serviceTemplate.getImageAsByteArr();
			}
		}
		if(data!=null){
			InputStream in=null;
			OutputStream out=null;
			try{
				in = new ByteArrayInputStream(data);
		        out = resp.getOutputStream();
		        byte[] buffer = new byte[1024];
		        int len = 0;
		        while ((len = in.read(buffer)) != -1) {
		        	out.write(buffer, 0, len);
		        }
		        out.flush();
			}catch(Exception e){
				log.error("error when read picture , info "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()),e);
			}finally{
				IOUtils.closeQuietly(in);
		        IOUtils.closeQuietly(out);
			}
		}
	}
}
