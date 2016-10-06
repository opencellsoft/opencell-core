/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.Auditable;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.ProductTemplate;

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "PRODUCT")
@Table(name = "BILLING_PRODUCT_INSTANCE")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_PRODUCT_INSTANCE_SEQ")
public class ProductInstance extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount userAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_TEMPLATE_ID")
    private ProductTemplate productTemplate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "APPLICATION_DATE")
    private Date applicationDate;

    @OneToMany(mappedBy = "productInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<ProductChargeInstance> productChargeInstances = new ArrayList<ProductChargeInstance>();

    @Column(name = "QUANTITY", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal quantity = BigDecimal.ONE;

    public ProductInstance() {
        super();
    }

    public ProductInstance(UserAccount userAccount,Subscription subscription, ProductTemplate productTemplate, BigDecimal quantity, Date applicationDate, String code, String description, User user) {
        this.applicationDate = applicationDate;
        this.code = code;
        this.description = description;
        this.productChargeInstances = new ArrayList<>();
        this.productTemplate = productTemplate;
        this.quantity = quantity;
        this.subscription=subscription;
        if(subscription==null){
            this.userAccount = userAccount;
        } else {
        	this.userAccount=subscription.getUserAccount();
        }
        Auditable auditable = new Auditable();
        auditable.setCreated(new Date());
        auditable.setCreator(user);
        this.setAuditable(auditable);
    }

	public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
    }

    public List<ProductChargeInstance> getProductChargeInstances() {
        return productChargeInstances;
    }

    public void setProductChargeInstances(List<ProductChargeInstance> productChargeInstances) {
        this.productChargeInstances = productChargeInstances;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof ProductInstance)) {
            return false;
        }

        ProductInstance other = (ProductInstance) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        // FIXME
        // return new ICustomFieldEntity[]{productTemplate};
        return null;
    }

}