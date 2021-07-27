package org.meveo.api.dto.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseEntityDto;

@XmlRootElement(name = "DownloadReportQueryResponseDto")
@XmlType(name = "DownloadReportQueryResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class DownloadReportQueryResponseDto extends BaseEntityDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String FileName;
	private byte[] reportContent;
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return FileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		FileName = fileName;
	}
	/**
	 * @return the reportContent
	 */
	public byte[] getReportContent() {
		return reportContent;
	}
	/**
	 * @param reportContent the reportContent to set
	 */
	public void setReportContent(byte[] reportContent) {
		this.reportContent = reportContent;
	}
	
	
}
