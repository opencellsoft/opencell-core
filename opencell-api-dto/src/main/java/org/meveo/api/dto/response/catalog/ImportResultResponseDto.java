package org.meveo.api.dto.response.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsItem;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;

public class ImportResultResponseDto extends BaseResponse {

    private static final long serialVersionUID = 1298040533424386975L;

    private List<ImportResultDto> ImportResultDtos = new ArrayList<>();

    public static class ImportResultDto {
        private ActionStatusEnum status = ActionStatusEnum.SUCCESS;
        private String message = "Imported successfully!";
        private String fileName;
        private String chargeCode;
        private VersionStatusEnum uploadAs;
        private Date startDate;
        private Date endDate;

        public ImportResultDto(ImportPricePlanVersionsItem importItem) {
            this.fileName = importItem.getFileName();
            this.chargeCode = importItem.getChargeCode();
            this.uploadAs = importItem.getStatus();
            this.startDate = DateUtils.truncateTime(importItem.getStartDate());
            this.endDate = DateUtils.truncateTime(importItem.getEndDate());
        }

        public ActionStatusEnum getStatus() {
            return status;
        }

        public void setStatus(ActionStatusEnum status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getChargeCode() {
            return chargeCode;
        }

        public void setChargeCode(String chargeCode) {
            this.chargeCode = chargeCode;
        }

        public VersionStatusEnum getUploadAs() {
            return uploadAs;
        }

        public void setUploadAs(VersionStatusEnum uploadAs) {
            this.uploadAs = uploadAs;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }
    }

    /**
     * @return the importResultDtos
     */
    public List<ImportResultDto> getImportResultDtos() {
        return ImportResultDtos;
    }

    /**
     * @param importResultDtos the importResultDtos to set
     */
    public void setImportResultDtos(List<ImportResultDto> importResultDtos) {
        ImportResultDtos = importResultDtos;
    }
}
