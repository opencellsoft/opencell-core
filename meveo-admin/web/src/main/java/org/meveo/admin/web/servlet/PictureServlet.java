package org.meveo.admin.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.meveo.admin.util.ModuleUtil;

/**
 * Show a picture from a rest URI like /meveo/picture/provider/module/tmp/filename.suffix
 * 
 * 3 provider code
 * 4 group like module or offer
 * 5 tmp, read pictures from tmp folder
 * 6 picture filename like entity's code with suffix png,gif,jpeg,jpg
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 */
@WebServlet(name = "pictureServlet", urlPatterns = "/picture/*",loadOnStartup=1000)
public class PictureServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Log log=LogFactory.getLog(PictureServlet.class);
	
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
		if(path==null||(path.length!=6&path.length!=7)){
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
		
		String destfile=rootPath+File.separator+filename;
		log.debug("read a picture file from "+ destfile);
		File file=new File(destfile);
		if(!file.exists()){
			log.debug("Picture file isn't existed "+destfile);
			return;
		}
		InputStream in=null;
		OutputStream out=null;
		try{
			in = new ByteArrayInputStream(ModuleUtil.readPicture(destfile));
	        out = resp.getOutputStream();
	        byte[] buffer = new byte[1024];
	        int len = 0;
	        while ((len = in.read(buffer)) != -1) {
	        	out.write(buffer, 0, len);
	        }
	        out.flush();
		}catch(Exception e){
			log.error("error when read picture file "+destfile+" , info "+(e.getMessage()==null?e.getClass().getSimpleName():e.getMessage()),e);
		}finally{
			IOUtils.closeQuietly(in);
	        IOUtils.closeQuietly(out);
		}
	}
}
