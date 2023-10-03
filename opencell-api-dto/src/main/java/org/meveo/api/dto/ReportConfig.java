package org.meveo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "ReportConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportConfig implements Serializable {

    /**
     * Decide if billing run report will be generated during billing run creation
     */
    @Schema(description = "Decide if billing run report will be generated during billing run creation", nullable = true)
    private Boolean preReportAutoOnCreate;

    /**
     * Decide if billing run report will be generated after invoice line job
     */
    @Schema(description = "Decide if billing run report will be generated after invoice line job", nullable = true)
    private Boolean preReportAutoOnInvoiceLinesJob;

    /**
     * Pilots computation and display of billing accounts block
     */
    @Schema(description = "Pilots computation and display of billing accounts block", nullable = true)
    private Boolean displayBillingAccounts;

    /**
     * Pilots computation and display of subscriptions block
     */
    @Schema(description = "Pilots computation and display of subscriptions block", nullable = true)
    private Boolean displaySubscriptions;

    /**
     * Pilots computation and display of offers block
     */
    @Schema(description = "Pilots computation and display of offers block", nullable = true)
    private Boolean displayOffers;

    /**
     * Pilots computation and display of products block
     */
    @Schema(description = "Pilots computation and display of products block", nullable = true)
    private Boolean displayProducts;

    /**
     * Pilots computation and display of articles block
     */
    @Schema(description = "Pilots computation and display of articles block", nullable = true)
    private Boolean displayArticles;

    /**
     * Report billing accounts block size between 1 and 100
     */
    @Schema(description = "Report billing accounts block size between 1 and 100", nullable = true)
    private Integer blockSizeBillingAccounts;

    /**
     * Report subscriptions block size between 1 and 100
     */
    @Schema(description = "Report subscriptions block size between 1 and 100", nullable = true)
    private Integer blockSizeSubscriptions;

    /**
     * Report offers block size between 1 and 100
     */
    @Schema(description = "Report offers block size between 1 and 100", nullable = true)
    private Integer blockSizeOffers;

    /**
     * Report products block size between 1 and 100
     */
    @Schema(description = "Report products block size between 1 and 100", nullable = true)
    private Integer blockSizeProducts;

    /**
     * Report articles block size between 1 and 100
     */
    @Schema(description = "Report articles block size between 1 and 100", nullable = true)
    private Integer blockSizeArticles;

    public ReportConfig() {}

    public ReportConfig(Boolean preReportAutoOnCreate, Boolean preReportAutoOnInvoiceLinesJob,
                        Boolean displayBillingAccounts, Boolean displaySubscriptions, Boolean displayOffers,
                        Boolean displayProducts, Boolean displayArticles, Integer blockSizeBillingAccounts,
                        Integer blockSizeSubscriptions, Integer blockSizeOffers, Integer blockSizeProducts, Integer blockSizeArticles) {
        this.preReportAutoOnCreate = preReportAutoOnCreate;
        this.preReportAutoOnInvoiceLinesJob = preReportAutoOnInvoiceLinesJob;
        this.displayBillingAccounts = displayBillingAccounts;
        this.displaySubscriptions = displaySubscriptions;
        this.displayOffers = displayOffers;
        this.displayProducts = displayProducts;
        this.displayArticles = displayArticles;
        this.blockSizeBillingAccounts = blockSizeBillingAccounts;
        this.blockSizeSubscriptions = blockSizeSubscriptions;
        this.blockSizeOffers = blockSizeOffers;
        this.blockSizeProducts = blockSizeProducts;
        this.blockSizeArticles = blockSizeArticles;
    }

    public Boolean getPreReportAutoOnCreate() {
        return preReportAutoOnCreate;
    }

    public void setPreReportAutoOnCreate(Boolean preReportAutoOnCreate) {
        this.preReportAutoOnCreate = preReportAutoOnCreate;
    }

    public Boolean getPreReportAutoOnInvoiceLinesJob() {
        return preReportAutoOnInvoiceLinesJob;
    }

    public void setPreReportAutoOnInvoiceLinesJob(Boolean preReportAutoOnInvoiceLinesJob) {
        this.preReportAutoOnInvoiceLinesJob = preReportAutoOnInvoiceLinesJob;
    }

    public Boolean getDisplayBillingAccounts() {
        return displayBillingAccounts;
    }

    public void setDisplayBillingAccounts(Boolean displayBillingAccounts) {
        this.displayBillingAccounts = displayBillingAccounts;
    }

    public Boolean getDisplaySubscriptions() {
        return displaySubscriptions;
    }

    public void setDisplaySubscriptions(Boolean displaySubscriptions) {
        this.displaySubscriptions = displaySubscriptions;
    }

    public Boolean getDisplayOffers() {
        return displayOffers;
    }

    public void setDisplayOffers(Boolean displayOffers) {
        this.displayOffers = displayOffers;
    }

    public Boolean getDisplayProducts() {
        return displayProducts;
    }

    public void setDisplayProducts(Boolean displayProducts) {
        this.displayProducts = displayProducts;
    }

    public Boolean getDisplayArticles() {
        return displayArticles;
    }

    public void setDisplayArticles(Boolean displayArticles) {
        this.displayArticles = displayArticles;
    }

    public Integer getBlockSizeBillingAccounts() {
        return blockSizeBillingAccounts;
    }

    public void setBlockSizeBillingAccounts(Integer blockSizeBillingAccounts) {
        this.blockSizeBillingAccounts = blockSizeBillingAccounts;
    }

    public Integer getBlockSizeSubscriptions() {
        return blockSizeSubscriptions;
    }

    public void setBlockSizeSubscriptions(Integer blockSizeSubscriptions) {
        this.blockSizeSubscriptions = blockSizeSubscriptions;
    }

    public Integer getBlockSizeOffers() {
        return blockSizeOffers;
    }

    public void setBlockSizeOffers(Integer blockSizeOffers) {
        this.blockSizeOffers = blockSizeOffers;
    }

    public Integer getBlockSizeProducts() {
        return blockSizeProducts;
    }

    public void setBlockSizeProducts(Integer blockSizeProducts) {
        this.blockSizeProducts = blockSizeProducts;
    }

    public Integer getBlockSizeArticles() {
        return blockSizeArticles;
    }

    public void setBlockSizeArticles(Integer blockSizeArticles) {
        this.blockSizeArticles = blockSizeArticles;
    }
}
