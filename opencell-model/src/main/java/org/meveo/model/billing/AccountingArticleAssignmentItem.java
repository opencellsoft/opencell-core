package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class AccountingArticleAssignmentItem {
    private Long chargeTemplateId;
    private Long offerTemplateId;
    private Long serviceInstanceId;
    private List<Long> chargeInstancesIDs = new ArrayList<>();

    public AccountingArticleAssignmentItem(Long chargeTemplateId, Long offerTemplateId, Long serviceInstanceId, String chargeInstances) {
        this.chargeTemplateId = chargeTemplateId;
        this.offerTemplateId = offerTemplateId;
        this.serviceInstanceId = serviceInstanceId;
        this.chargeInstancesIDs = StringUtils.isBlank(chargeInstances) ? null : Pattern.compile(",").splitAsStream(chargeInstances).mapToLong(Long::parseLong).boxed().distinct().collect(Collectors.toList());
    }

    public AccountingArticleAssignmentItem(Object[] fields) {
        int i = 0;
        this.chargeTemplateId = (Long) fields[i++];
        this.offerTemplateId = (Long) fields[i++];
        this.serviceInstanceId = (Long) fields[i++];
        this.chargeInstancesIDs = Pattern.compile(",").splitAsStream((String) fields[i++]).mapToLong(Long::parseLong).boxed().distinct().collect(Collectors.toList());
    }

    public Long getChargeTemplateId() {
        return chargeTemplateId;
    }

    public void setChargeTemplateId(Long chargeTemplateId) {
        this.chargeTemplateId = chargeTemplateId;
    }

    public Long getOfferTemplateId() {
        return offerTemplateId;
    }

    public void setOfferTemplateId(Long offerTemplateId) {
        this.offerTemplateId = offerTemplateId;
    }

    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public List<Long> getChargeInstancesIDs() {
        return chargeInstancesIDs;
    }

    public void setChargeInstancesIDs(List<Long> chargeInstancesIDs) {
        this.chargeInstancesIDs = chargeInstancesIDs;
    }
}