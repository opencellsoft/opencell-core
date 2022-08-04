package org.meveo.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportFileTypeDto {

    @XmlElement(required = true)
    @Schema(description = "Name of the uploaded file")
    private String fileName;


    @XmlElement(required = true)
    @Schema(description = "Type of the uploaded file")
    private ImportTypesEnum fileType;

    public ImportFileTypeDto() {
    }

    public ImportFileTypeDto(String fileName, ImportTypesEnum fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    /**
     * Gets the file name
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }


    /**
     * Sets the file name
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the file type
     *
     * @return the file type
     */
    public ImportTypesEnum getFileType() {
        return fileType;
    }

    /**
     * Sets the file type
     *
     * @param fileType the new file type
     */
    public void setFileType(ImportTypesEnum fileType) {
        this.fileType = fileType;
    }


}
