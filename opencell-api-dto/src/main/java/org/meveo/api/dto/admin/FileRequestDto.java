package org.meveo.api.dto.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class FileRequestDto.
 *
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */
@XmlRootElement(name = "FileRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1150993171011072506L;

    /** The filepath. */
    @XmlElement(required = true)
    private String filepath;

    /** The Base 64 data */
    @XmlElement(required = true)
    private String content;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "FileUploadRequestDto [filepath=" + filepath + ", content=" + content + "]";
    }
}