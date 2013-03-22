/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.crm;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.meveo.model.BusinessEntity; 
import org.meveo.model.admin.User;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Currency;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry; 
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.InterBankTitle;
import org.meveo.model.shared.Title;

@Entity
@Table(name = "CRM_PROVIDER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_PROVIDER_SEQ")
public class Provider extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    private Currency currency ;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    private Country country; 
    

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "LANGUAGE_ID")
   private Language language;
    
    @Column(name = "MULTICOUNTRY_FLAG")
    private boolean multicountryFlag;
    
    
    @Column(name = "MULTICURRENCY_FLAG")
    private boolean multicurrencyFlag;
    
    
    @Column(name = "MULTILANGUAGE_FLAG")
    private boolean multilanguageFlag;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ACCOUNT_ID")
    private CustomerAccount customerAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_ACCOUNT_ID")
    private BillingAccount billingAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount userAccount;

    
    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private List<TradingCountry> tradingCountries;
    
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ADM_USER_PROVIDER", joinColumns = @JoinColumn(name = "PROVIDER_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
    private List<User> users = new ArrayList<User>();

    private static final String PM_SEP = ",";

    @Column(name = "PAYMENT_METHODS")
    private String serializedPaymentMethods;

    @Transient
    private List<PaymentMethodEnum> paymentMethods;

    @ManyToMany()
    @JoinTable(name = "PROVIDER_TITLES")
    private List<Title> titles;

    @Column(name = "LOGO")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private Blob logo;

    @Column(name = "INVOICE_PREFIX")
    private String invoicePrefix;

    @Column(name = "CURRENT_INVOICE_NB")
    private Long currentInvoiceNb;

    @Column(name = "RATING_ROUNDING")
    private Integer rounding;

    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();

    @Column(name = "ENTREPRISE")
    private boolean entreprise = false;

    @Column(name = "AUTOMATIC_INVOICING")
    private boolean automaticInvoicing = false;

    @Embedded
    private InterBankTitle interBankTitle;

    @Column(name = "AMOUNT_VALIDATION")
    private boolean amountValidation = false;

    @Column(name = "LEVEL_DUPLICATION")
    private boolean levelDuplication = false;

    @Column(name = "EMAIL", length = 100)
    @Email
    @Length(max = 100)
    protected String email;

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private List<TradingLanguage> tradingLanguage;
    
	public String getSerializedPaymentMethods() {
        return serializedPaymentMethods;
    }

    public void setSerializedPaymentMethods(String serializedPaymentMethods) {
        this.serializedPaymentMethods = serializedPaymentMethods;
    }
 
	 
	 

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public boolean getMulticountryFlag() {
		return multicountryFlag;
	}

	public void setMulticountryFlag(boolean multicountryFlag) {
		this.multicountryFlag = multicountryFlag;
	}

	public boolean getMulticurrencyFlag() {
		return multicurrencyFlag;
	}

	public void setMulticurrencyFlag(boolean multicurrencyFlag) {
		this.multicurrencyFlag = multicurrencyFlag;
	}

	public boolean getMultilanguageFlag() {
		return multilanguageFlag;
	}

	public void setMultilanguageFlag(boolean multilanguageFlag) {
		this.multilanguageFlag = multilanguageFlag;
	}

	
	

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<PaymentMethodEnum> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<PaymentMethodEnum>();
            if (serializedPaymentMethods != null) {
                int index = -1;
                while ((index = serializedPaymentMethods.indexOf(PM_SEP)) > -1) {
                    String paymentMethod = serializedPaymentMethods.substring(0, index);
                    paymentMethods.add(PaymentMethodEnum.valueOf(paymentMethod));
                    serializedPaymentMethods = serializedPaymentMethods.substring(index);
                }
            }
        }
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethodEnum> paymentMethods) {
        if (paymentMethods == null) {
            serializedPaymentMethods = null;
        } else {
            serializedPaymentMethods = "";
            String sep = "";
            for (PaymentMethodEnum paymentMethod : paymentMethods) {
                serializedPaymentMethods = sep + paymentMethod.name();
                sep = PM_SEP;
            }
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

    public Blob getLogo() {
        return logo;
    }

    public void setLogo(Blob logo) {
        this.logo = logo;
    }

    public String getInvoicePrefix() {
        return invoicePrefix;
    }

    public void setInvoicePrefix(String invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
    }

    public void setBankCoordinates(BankCoordinates bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    }

    public boolean isEntreprise() {
        return entreprise;
    }

    public void setEntreprise(boolean entreprise) {
        this.entreprise = entreprise;
    }

    public Long getCurrentInvoiceNb() {
        return currentInvoiceNb;
    }

    public void setCurrentInvoiceNb(Long currentInvoiceNb) {
        this.currentInvoiceNb = currentInvoiceNb;
    }

    public InterBankTitle getInterBankTitle() {
        return interBankTitle;
    }

    public void setInterBankTitle(InterBankTitle interBankTitle) {
        this.interBankTitle = interBankTitle;
    }

    public Integer getRounding() {
        return rounding;
    }

    public void setRounding(Integer rounding) {
        this.rounding = rounding;
    }

    public boolean isAutomaticInvoicing() {
        return automaticInvoicing;
    }

    public void setAutomaticInvoicing(boolean automaticInvoicing) {
        this.automaticInvoicing = automaticInvoicing;
    }

    public boolean isAmountValidation() {
        return amountValidation;
    }

    public void setAmountValidation(boolean amountValidation) {
        this.amountValidation = amountValidation;
    }

    public boolean isLevelDuplication() {
        return levelDuplication;
    }

    public void setLevelDuplication(boolean levelDuplication) {
        this.levelDuplication = levelDuplication;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public List<TradingCountry> getTradingCountries() {
		return tradingCountries;
	}

	public void setTradingCountries(List<TradingCountry> tradingCountries) {
		this.tradingCountries = tradingCountries;
	}

	public List<TradingLanguage> getTradingLanguage() {
		return tradingLanguage;
	}

	public void setTradingLanguage(List<TradingLanguage> tradingLanguage) {
		this.tradingLanguage = tradingLanguage;
	}



    

}
