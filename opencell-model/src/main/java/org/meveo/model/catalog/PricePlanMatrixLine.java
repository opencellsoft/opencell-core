package org.meveo.model.catalog;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.cpq.QuoteAttribute;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@ExportIdentifier({"code"})
@Table(name = "cpq_price_plan_matrix_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_line_sq"),})
@NamedQuery(name = "PricePlanMatrixLine.findByPricePlanMatrixVersion", query = "select p from PricePlanMatrixLine p where p.pricePlanMatrixVersion=:pricePlanMatrixVersion")
public class PricePlanMatrixLine extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "ppm_version_id")
    private PricePlanMatrixVersion pricePlanMatrixVersion;

    @Column(name = "description")
    private String description;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal pricetWithoutTax;

    @OneToMany(mappedBy = "pricePlanMatrixLine", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PricePlanMatrixValue> pricePlanMatrixValues;

    @Type(type = "numeric_boolean")
    @Column(name = "is_default")
    @NotNull
    private boolean isDefault = false;

    @Transient
    private MatchingTypeEnum matchingTypeEnum;

    public PricePlanMatrixVersion getPricePlanMatrixVersion() {
        return pricePlanMatrixVersion;
    }

    public void setPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        this.pricePlanMatrixVersion = pricePlanMatrixVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricetWithoutTax() {
        return pricetWithoutTax;
    }

    public void setPricetWithoutTax(BigDecimal pricetWithoutTax) {
        this.pricetWithoutTax = pricetWithoutTax;
    }

    public Set<PricePlanMatrixValue> getPricePlanMatrixValues() {
        return pricePlanMatrixValues == null ? new HashSet<>() : pricePlanMatrixValues;
    }

    public void setPricePlanMatrixValues(Set<PricePlanMatrixValue> pricePlanMatrixValues) {
        this.pricePlanMatrixValues = pricePlanMatrixValues;
    }

    public boolean match(Set<QuoteAttribute> quoteAttributes) {
        boolean isMatch = pricePlanMatrixValues.stream()
                .allMatch(v -> v.match(quoteAttributes));
        if (isMatch) {
            computeMatchType(quoteAttributes);
        }

        return isMatch;
    }

    private void computeMatchType(Set<QuoteAttribute> quoteAttributes) {
        List<QuoteAttribute.MatchingTypeEnum> matchingTypes = quoteAttributes.stream()
                .map(QuoteAttribute::getMatchingTypeEnum)
                .collect(Collectors.toList());
        boolean containsRegEx = false;
        boolean containsExactValue = false;
        boolean containsRangeValue = false;

        for (QuoteAttribute.MatchingTypeEnum matchingType : matchingTypes) {
            if (matchingType == QuoteAttribute.MatchingTypeEnum.REG_MATCHING)
                containsRegEx = true;
            else if (matchingType == QuoteAttribute.MatchingTypeEnum.EXACT_VALUE)
                containsExactValue = true;
            else if (matchingType == QuoteAttribute.MatchingTypeEnum.RANGE_VALUE)
                containsRangeValue = true;
        }
        setLineMatchingType(containsExactValue, containsRangeValue, containsRegEx);
    }

    private void setLineMatchingType(boolean containsExactValue, boolean containsRangeValue, boolean containsRegEx) {
        if (containsExactValue && !containsRangeValue && !containsRegEx)
            this.matchingTypeEnum = MatchingTypeEnum.ALL_EXACT_VALUES;
        else if (!containsExactValue && containsRangeValue && !containsRegEx)
            this.matchingTypeEnum = MatchingTypeEnum.ALL_RANGE_VALUE;
        else if (!containsExactValue && !containsRangeValue && containsRegEx)
            this.matchingTypeEnum = MatchingTypeEnum.ALL_REG_EX;
        else if (containsExactValue && containsRangeValue && !containsRegEx)
            this.matchingTypeEnum = MatchingTypeEnum.RANGE_AND_EXACT_VALUES;
        else if (containsExactValue && !containsRangeValue && containsRegEx)
            this.matchingTypeEnum = MatchingTypeEnum.REG_EX_AND_EXACT_VALUE;
        else if (!containsExactValue && containsRangeValue && containsRegEx)
            this.matchingTypeEnum = MatchingTypeEnum.RANGE_AND_REG_EX;

    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public MatchingTypeEnum getMatchingTypeEnum() {
        return matchingTypeEnum;
    }

    public void setMatchingTypeEnum(MatchingTypeEnum matchingTypeEnum) {
        this.matchingTypeEnum = matchingTypeEnum;
    }

    public enum MatchingTypeEnum {
        ALL_EXACT_VALUES(1),
        RANGE_AND_EXACT_VALUES(2),
        ALL_RANGE_VALUE(3),
        REG_EX_AND_EXACT_VALUE(4),
        RANGE_AND_REG_EX(5),
        ALL_REG_EX(6);

        private Integer priority;

        MatchingTypeEnum(Integer priority) {
            this.priority = priority;
        }

        public Integer getPriority() {
            return priority;
        }
    }
}
