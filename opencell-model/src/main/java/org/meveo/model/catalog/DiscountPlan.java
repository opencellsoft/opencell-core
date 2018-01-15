package org.meveo.model.catalog;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code"})
@Table(name = "cat_discount_plan", uniqueConstraints = { @UniqueConstraint(columnNames = { "code"}) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "cat_discount_plan_seq"), })
public class DiscountPlan extends BusinessEntity {

    private static final long serialVersionUID = -2762453947446654646L;

    @Column(name = "min_duration")
    private int minDuration = 0;

    @Column(name = "max_duration")
    private int maxDuration = 99999;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "discountPlan", fetch=FetchType.EAGER)
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