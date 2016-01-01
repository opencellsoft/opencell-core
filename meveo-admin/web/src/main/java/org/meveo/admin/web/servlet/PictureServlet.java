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

@WebServlet(name = "pictureServlet", urlPatterns = "/picture/*")
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
		log.debug("pictureServlet request URL "+url);
		String[] path=url.split("/");
		if(path==null||path.length!=5){
			return;
		}
		String filename=ModuleUtil.getPicturePath(path[3])+File.separator+path[4];
		File pictureFile=new File(filename);
		if(!pictureFile.exists()){
			log.debug(filename+ " module picture is not existed");
			return;
		}
		InputStream in=null;
		OutputStream out=null;
		try{
			in = new ByteArrayInputStream(ModuleUtil.readModulePicture(path[3], path[4]));
	        out = resp.getOutputStream();
	        byte[] buffer = new byte[1024];
	        int len = 0;
	        while ((len = in.read(buffer)) != -1) {
	        	out.write(buffer, 0, len);
	        }
	        out.flush();
		}catch(Exception e){
			log.error("error when read module file "+filename+" , info "+e.getMessage());
		}finally{
			IOUtils.closeQuietly(in);
	        IOUtils.closeQuietly(out);
		}
	}
}
