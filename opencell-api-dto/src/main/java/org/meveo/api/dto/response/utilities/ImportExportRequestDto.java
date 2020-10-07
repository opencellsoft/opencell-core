package org.meveo.api.dto.response.utilities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ImportExportRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportExportRequestDto {

    private String exportType;

    private String instanceCode;

    private String fileName;

    private String entityToExport;

    public ImportExportRequestDto() {
    }

    public ImportExportRequestDto(String exportType, String instanceCode, String fileName, String entityToExport) {
        this.exportType = exportType;
        this.instanceCode = instanceCode;
        this.fileName = fileName;
        this.entityToExport = entityToExport;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

    public String getInstanceCode() {
        return instanceCode;
    }

    public void setInstanceCode(String instanceCode) {
        this.instanceCode = instanceCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getEntityToExport() {
        return entityToExport;
    }

    public void setEntityToExport(String entityToExport) {
        this.entityToExport = entityToExport;
    }
}
