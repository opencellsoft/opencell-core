package org.meveo.api.rest.admin.impl;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * @author Edward P. Legaspi 7 Aug 2017
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.3.0
 */
public class FileUploadForm {

	@FormParam("uploadedFile")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	private byte[] data;

	@FormParam("filename")
	@PartType(MediaType.TEXT_PLAIN)
	private String filename;

	@FormParam("fileFormat")
	@PartType(MediaType.TEXT_PLAIN)
	private String fileFormat;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Gets the fileFormat
	 *
	 * @return the fileFormat
	 */
	public String getFileFormat() {
		return fileFormat;
	}

	/**
	 * Sets the fileFormat.
	 *
	 * @param fileFormat the new fileFormat
	 */
	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}
}
