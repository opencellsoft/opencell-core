package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ObservableEntity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_DISCOUNT_PLAN", uniqueConstraints = { @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DISCOUNT_PLAN_SEQ")
public class DiscountPlan extends BusinessEntity {

    private static final long serialVersionUID = -2762453947446654646L;

    @Column(name = "MIN_DURATION")
    private int minDuration = 0;

    @Column(name = "MAX_DURATION")
    private int maxDuration = 99999;

    @OneToMany(mappedBy = "discountPlan")
    private List<DiscountPlanItem> discountPlanItems;

    public int getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public List<DiscountPlanItem> getDiscountPlanItems() {
        return discountPlanItems;
    }

    public void setDiscountPlanItems(List<DiscountPlanItem> discountPlanItems) {
        this.discountPlanItems = discountPlanItems;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("DiscountPlan [%s, minDuration=%s, maxDuration=%s, discountPlanItems=%s]", super.toString(), minDuration, maxDuration,
            discountPlanItems != null ? discountPlanItems.subList(0, Math.min(discountPlanItems.size(), maxLen)) : null);
    }
}