package org.meveo.api.dto.billing;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;

public class ProcessCdrDto extends BaseEntityDto {

    /**
     * 
     */
    private static final long serialVersionUID = -1847398010531234326L;
    
    private final Long cdrId;
    private final CDRStatusEnum status;
    private final String rejectReason;
    private final Long edrId;


    public ProcessCdrDto(CDR cdr) {
        this.cdrId = cdr.getId();
        this.edrId = (cdr.getHeaderEDR()!=null)?cdr.getHeaderEDR().getId():null;
        this.status = cdr.getStatus();
        this.rejectReason = cdr.getRejectReason();
    }

    /**
     * @return the cdrId
     */
    public Long getCdrId() {
        return cdrId;
    }

    /**
     * @return the rejectReason
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * @return the status
     */
    public CDRStatusEnum getStatus() {
        return status;
    }

    /**
     * @return the edrId
     */
    public Long getEdrId() {
        return edrId;
    }

}
