package org.meveo.admin.action.frontend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;

/**
 * @author Edward P. Legaspi
 */
@WebServlet("/files/*")
public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = -7865816094143438213L;

	private ParamBean paramBean = ParamBean.getInstance();

	private String basePath;

	/**
	 * Initialize the servlet.
	 * 
	 * @see HttpServlet#init().
	 */
	public void init() throws ServletException {

		// Get base path (path to get all resources from) as init parameter.
		this.basePath = paramBean.getProperty("providers.rootDir", "./opencelldata");

		// Validate base path.
		if (this.basePath == null) {
			throw new ServletException("FileServlet property 'providers.rootDir' is required.");
		} else {
			File path = new File(this.basePath);
			if (!path.exists()) {
				throw new ServletException("FileServlet property 'providers.rootDir' value '" + this.basePath
						+ "' does actually not exist in file system.");
			} else if (!path.isDirectory()) {
				throw new ServletException("FileServlet property 'providers.rootDir' value '" + this.basePath
						+ "' is actually not a directory in file system.");
			} else if (!path.canRead()) {
				throw new ServletException("FileServlet property 'providers.rootDir' value '" + this.basePath
						+ "' is actually not readable in file system.");
			}
		}
	}

	/**
	 * Process HEAD request. This returns the same headers as GET request, but
	 * without content.
	 * 
	 * @see HttpServlet#doHead(HttpServletRequest, HttpServletResponse).
	 */
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Process request without content.
		processRequest(request, response, false);
	}

	/**
	 * Process GET request.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse).
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Process request with content.
		processRequest(request, response, true);
	}

	/**
	 * Process the actual request.
	 * 
	 * @param request
	 *            The request to be processed.
	 * @param response
	 *            The response to be created.
	 * @param content
	 *            Whether the request body should be written (GET) or not (HEAD).
	 * @throws IOException
	 *             If something fails at I/O level.
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response, boolean content)
			throws IOException {
		// Validate the requested file
		// ------------------------------------------------------------

		// Get requested file by path info.
		String requestedFile = request.getPathInfo();

		// Check if file is actually supplied to the request URL.
		if (requestedFile == null) {
			// Do your thing if the file is not supplied to the request URL.
			// Throw an exception, or send 404, or show default/warning page, or just ignore
			// it.
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// URL-decode the file name (might contain spaces and on) and prepare file
		// object.
		File fileOrFolder = new File(basePath, URLDecoder.decode(requestedFile, "UTF-8"));

		// Check if file actually exists in filesystem.
		if (!fileOrFolder.exists()) {
			// Do your thing if the file appears to be non-existing.
			// Throw an exception, or send 404, or show default/warning page, or just ignore
			// it.
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// Prepare some variables. The ETag is an unique identifier of the file.
		String fileName = fileOrFolder.getName();

		if (fileOrFolder.isDirectory()) {
			// zipped it
			ByteArrayOutputStream zipout = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(zipout);
			FileUtils.addDirToArchive(basePath, fileOrFolder.getPath(), zos);
			zos.close();

			ServletOutputStream outStream = response.getOutputStream();
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".zip");
			outStream.write(zipout.toByteArray());
			outStream.flush();
			outStream.close();
			zipout.close();
		} else {
			// file
			FileInputStream fis = new FileInputStream(fileOrFolder);
			response.setContentType("application/force-download");
			response.setContentLength((int) fileOrFolder.length());
			response.addHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");
			IOUtils.copy(fis, response.getOutputStream());
			response.flushBuffer();
		}

	}

}
