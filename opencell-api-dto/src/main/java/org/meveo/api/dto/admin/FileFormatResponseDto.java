package org.meveo.api.dto.admin;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * API response containing the File format Dto
 * 
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "FileFormatResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileFormatResponseDto extends BaseResponse {

    private static final long serialVersionUID = -854600872809452516L;

    /**
     * The file format dto
     */
    private FileFormatDto dto;

    /**
     * @return The File format dto
     */
    public FileFormatDto getDto() {
        return dto;
    }

    /**
     * @param dto The File format dto
     */
    public void setDto(FileFormatDto dto) {
        this.dto = dto;
    }

    @Override
    public String toString() {
        return "FileFormatResponse [" + dto + ", " + super.toString() + "]";
    }
}