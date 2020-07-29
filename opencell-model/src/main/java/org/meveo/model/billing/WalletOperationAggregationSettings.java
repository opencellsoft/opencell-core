package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.filter.Filter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregation Settings.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 10.0
 */
@Entity
@Cacheable
@Table(name = "wo_aggregation_settings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wo_aggregation_matrix_seq"), })
public class WalletOperationAggregationSettings extends BusinessEntity {
    /**
     * Global aggregation rather than by job.
     */
    @Column(name = "global_aggregation")
    @Type(type = "numeric_boolean")
    private boolean globalAggregation;

    /**
     * Aggregate by continuous periods.
     */
    @Column(name = "period_aggregation")
    @Type(type = "numeric_boolean")
    private boolean periodAggregation;

    /**
     * If periodEndDateIncluded=true, the rule used to aggregate period is wo1.endDate = wo2.startDate. If not wo1.endDate + 1 day = wo2.startDate
     */
    @Column(name = "period_end_date_included")
    @Type(type = "numeric_boolean")
    private boolean periodEndDateIncluded;

    /**
     * The aggregation amount rounding.
     */
    @Column(name = "aggregation_rounding", columnDefinition = "int DEFAULT 2", nullable = false)
    private int aggregationRounding = 2;

    /**
     * The aggregation amount rounding mode.
     */
    @Column(name = "aggregation_rounding_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoundingModeEnum aggregationRoundingMode;
    /**
     * Additional order by fields seperated by comma that can be used to create the billing_wallet_operation_period view.
     */
    @Column(name = "additional_order_by")
    private String additionalOrderBy;

    /**
     * Apply an additional filter to WO.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_operation_filter_id")
    private Filter walletOperationFilter;

    @OneToMany(mappedBy = "aggregationSettings", fetch = FetchType.EAGER)
    private List<WalletOperationAggregationLine> aggregationLines = new ArrayList<>();

    public List<WalletOperationAggregationLine> getAggregationLines() {
        return aggregationLines;
    }

    public void setAggregationLines(List<WalletOperationAggregationLine> aggregationLines) {
        this.aggregationLines = aggregationLines;
    }

    public boolean isGlobalAggregation() {
        return globalAggregation;
    }

    public void setGlobalAggregation(boolean globalAggregation) {
        this.globalAggregation = globalAggregation;
    }

    public boolean isPeriodAggregation() {
        return periodAggregation;
    }

    public void setPeriodAggregation(boolean periodAggregation) {
        this.periodAggregation = periodAggregation;
    }

    public boolean isPeriodEndDateIncluded() {
        return periodEndDateIncluded;
    }

    public void setPeriodEndDateIncluded(boolean periodEndDateIncluded) {
        this.periodEndDateIncluded = periodEndDateIncluded;
    }

    public Filter getWalletOperationFilter() {
        return walletOperationFilter;
    }

    public void setWalletOperationFilter(Filter walletOperationFilter) {
        this.walletOperationFilter = walletOperationFilter;
    }

    public int getAggregationRounding() {
        return aggregationRounding;
    }

    public void setAggregationRounding(int aggregationRounding) {
        this.aggregationRounding = aggregationRounding;
    }

    public RoundingModeEnum getAggregationRoundingMode() {
        return aggregationRoundingMode;
    }

    public void setAggregationRoundingMode(RoundingModeEnum aggregationRoundingMode) {
        this.aggregationRoundingMode = aggregationRoundingMode;
    }

    public String getAdditionalOrderBy() {
        return additionalOrderBy;
    }

    public void setAdditionalOrderBy(String additionalOrderBy) {
        this.additionalOrderBy = additionalOrderBy;
    }
}
