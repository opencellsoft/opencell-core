package org.meveo.api.dto.response;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import org.meveo.api.dto.ActionStatusEnum;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(FIELD)
public class AggregatedDataResponseDto {

    @XmlAttribute
    private List<Map<String, Object>> queryResult;
    @XmlAttribute
    private ActionStatusEnum status;

    public List<Map<String, Object>> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(List<Map<String, Object>> queryResult) {
        this.queryResult = queryResult;
    }

    public ActionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ActionStatusEnum status) {
        this.status = status;
    }
}