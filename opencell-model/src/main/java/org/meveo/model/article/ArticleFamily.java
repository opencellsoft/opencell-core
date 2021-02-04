package org.meveo.model.article;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.crm.custom.CustomFieldValues;

@Entity
@Table(name = "billing_article_family")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_article_family_seq"), })
public class ArticleFamily extends BusinessEntity implements ICustomFieldEntity {

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @ManyToOne
    @JoinColumn(name = "article_family_ref_id")
    private ArticleFamily articleFamily;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    public ArticleFamily() {
    }

    public ArticleFamily(Long id){
        this.id = id;
    }

    public ArticleFamily(String code, String description, AccountingCode accountingCode) {
        this.code = code;
        this.description = description;
        this.accountingCode = accountingCode;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public ArticleFamily getArticleFamily() {
        return articleFamily;
    }

    public void setArticleFamily(ArticleFamily articleFamily) {
        this.articleFamily = articleFamily;
    }

    @Override
    public String getUuid() {
        setUUIDIfNull();
        return uuid;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @PrePersist
    public void setUUIDIfNull() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[0];
    }

    @Override
    public CustomFieldValues getCfValues() {
        return null;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return this.cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfValues) {
        this.cfAccumulatedValues = cfValues;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }
}
