package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.meveo.api.dto.FilterDto;

/**
 * @author Tony Alejandro
 **/
@XmlRootElement(name = "GetFilterResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetFilterResponseDto extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private FilterDto filter;

	public FilterDto getFilter() {
		return filter;
	}

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
