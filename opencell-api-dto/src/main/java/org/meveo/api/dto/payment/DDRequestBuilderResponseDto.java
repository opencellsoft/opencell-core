/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class DDRequestBuilderResponseDto.
 *
 * @author anasseh
 */

@XmlRootElement(name = "DDRequestBuilderResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestBuilderResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3151651854190686987L;

    /** The payment gateways. */
    @XmlElementWrapper(name = "ddRequestBuilders")
    @XmlElement(name = "ddRequestBuilder")
    private List<DDRequestBuilderDto> ddRequestBuilders = new ArrayList<DDRequestBuilderDto>();


    /**
     * Instantiates a new DD request builder response dto.
     */
    public DDRequestBuilderResponseDto() {

    }


    /**
     * Gets the dd request builders.
     *
     * @return the ddRequestBuilders
     */
    public List<DDRequestBuilderDto> getDdRequestBuilders() {
        return ddRequestBuilders;
    }


    /**
     * Sets the dd request builders.
     *
     * @param ddRequestBuilders the ddRequestBuilders to set
     */
    public void setDdRequestBuilders(List<DDRequestBuilderDto> ddRequestBuilders) {
        this.ddRequestBuilders = ddRequestBuilders;
    }

}
