package org.meveo.api.dto.admin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.GenericSearchResponse;
import org.meveo.api.dto.response.SearchResponse;

/**
 * API response containing a list of File format Dto
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "FileFormatListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileFormatListResponseDto extends SearchResponse {

    private static final long serialVersionUID = 7755312964281879099L;

    /**
     * A list of File format dto
     */
    @XmlElementWrapper(name = "dtos")
    @XmlElement(name = "dto")
    private List<FileFormatDto> dtos;

    /**
     * Constructor
     */
    public FileFormatListResponseDto() {
        super();
    }

    /**
     * Constructor
     */
    public FileFormatListResponseDto(GenericSearchResponse<FileFormatDto> searchResponse) {
        super(searchResponse.getPaging());
        this.dtos = searchResponse.getSearchResults();
    }

    /**
     * @return A list of File format dto
     */
    public List<FileFormatDto> getDtos() {
        if (dtos == null) {
            dtos = new ArrayList<>();
        }
        return dtos;
    }

    /**
     * @param dtos A list of FileFormat Dto
     */
    public void setDtos(List<FileFormatDto> dtos) {
        this.dtos = dtos;
    }

}
