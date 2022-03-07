package org.meveo.model.cpq.commercial;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;

/** 
 *
 */
@Entity
@Table(name = "cpq_order_amendement", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_amendement_seq")})
public class OrderAmendement extends BusinessCFEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1362016936635761055L;

	@OneToOne
    @JoinColumn(name = "subscription_id")
	@NotNull
    private Subscription subscription;
	
	@OneToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount consumer;

	@OneToMany(mappedBy = "orderAmendement", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> productsToSuspend = new ArrayList<OrderProduct>();
	
	@OneToMany(mappedBy = "orderAmendement", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> productsToReactivate = new ArrayList<OrderProduct>();
	
	@OneToMany(mappedBy = "orderAmendement", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> productsToTerminate = new ArrayList<OrderProduct>();
	
	@OneToMany(mappedBy = "orderAmendement", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> productsToActivate = new ArrayList<OrderProduct>();
	
	@OneToMany(mappedBy = "orderAmendement", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderProduct> productsToRestart = new ArrayList<OrderProduct>();

	public OrderAmendement(@NotNull Subscription subscription, UserAccount userAccount,
			List<OrderProduct> productsToSuspend, List<OrderProduct> productsToReactivate,
			List<OrderProduct> productsToTerminate, List<OrderProduct> productsToActivate,
			List<OrderProduct> productsToRestart) {
		super();
		this.subscription = subscription;
		this.consumer = userAccount;
		this.productsToSuspend = productsToSuspend;
		this.productsToReactivate = productsToReactivate;
		this.productsToTerminate = productsToTerminate;
		this.productsToActivate = productsToActivate;
		this.productsToRestart = productsToRestart;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public UserAccount getUserAccount() {
		return consumer;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.consumer = userAccount;
	}

	public List<OrderProduct> getProductsToSuspend() {
		return productsToSuspend;
	}

	public void setProductsToSuspend(List<OrderProduct> productsToSuspend) {
		this.productsToSuspend = productsToSuspend;
	}

	public List<OrderProduct> getProductsToReactivate() {
		return productsToReactivate;
	}

	public void setProductsToReactivate(List<OrderProduct> productsToReactivate) {
		this.productsToReactivate = productsToReactivate;
	}

	public List<OrderProduct> getProductsToTerminate() {
		return productsToTerminate;
	}

	public void setProductsToTerminate(List<OrderProduct> productsToTerminate) {
		this.productsToTerminate = productsToTerminate;
	}

	public List<OrderProduct> getProductsToActivate() {
		return productsToActivate;
	}

	public void setProductsToActivate(List<OrderProduct> productsToActivate) {
		this.productsToActivate = productsToActivate;
	}

	public List<OrderProduct> getProductsToRestart() {
		return productsToRestart;
	}

	public void setProductsToRestart(List<OrderProduct> productsToRestart) {
		this.productsToRestart = productsToRestart;
	}
	
	

	
}
