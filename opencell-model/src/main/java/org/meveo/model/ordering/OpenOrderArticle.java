package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.article.AccountingArticle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "open_order_article")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_article_seq"),})
@NamedQueries({
        @NamedQuery(name = "OpenOrderArticle.findByCodeAndTemplate",
                query = "SELECT ooa FROM OpenOrderArticle ooa WHERE ooa.openOrderTemplate.id=:TEMPLATE_ID AND ooa.accountingArticle.code=:ARTICLE_CODE AND ooa.active=TRUE") })
public class OpenOrderArticle extends AuditableEntity {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "accounting_article_id")
    private AccountingArticle accountingArticle;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "open_order_template_id")
    private OpenOrderTemplate openOrderTemplate;

    @Type(type = "numeric_boolean")
    @Column(name = "active")
    private Boolean active;

    public AccountingArticle getAccountingArticle() {
        return accountingArticle;
    }

    public void setAccountingArticle(AccountingArticle accountingArticle) {
        this.accountingArticle = accountingArticle;
    }

    public OpenOrderTemplate getOpenOrderTemplate() {
        return openOrderTemplate;
    }

    public void setOpenOrderTemplate(OpenOrderTemplate openOrderTemplate) {
        this.openOrderTemplate = openOrderTemplate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
