package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.meveo.api.dto.FilterDto;

/**
 * Return DTO for FilteredList API that includes the FilterDto in the response.
 *
 * @author Tony Alejandro
 * @lastModifiedVersion 5.0
 **/
@XmlRootElement(name = "GetFilterResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetFilterResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The filter. */
    private FilterDto filter;

    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public FilterDto getFilter() {
        return filter;
    }

    /**
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public void setFilter(FilterDto filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GetFilterResponseDto{");
        sb.append("filter=").append(filter);
        sb.append('}');
        return sb.toString();
    }
}