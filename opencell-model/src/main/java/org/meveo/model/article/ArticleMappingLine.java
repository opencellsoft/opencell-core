package org.meveo.model.article;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "billing_article_mapping_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_article_mapping_line_seq"), })
public class ArticleMappingLine extends BusinessEntity {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "article_mapping_id")
    private ArticleMapping articleMapping;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "article_id")
    private AccountingArticle accountingArticle;

    @OneToMany(mappedBy = "articleMappingLine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeMapping> attributesMapping;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "offer_template_id")
    private OfferTemplate offerTemplate;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "product_template_id")
    private ProductTemplate productTemplate;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "charge_template_id")
    private ChargeTemplate chargeTemplate;

    @Column(name = "parameter_1")
    private String parameter1;

    @Column(name = "parameter_2")
    private String parameter2;

    @Column(name = "parameter_3")
    private String parameter3;

    @Column(name = "mapping_key_el")
    private String mappingKelEL;

    public ArticleMapping getArticleMapping() {
        return articleMapping;
    }

    public void setArticleMapping(ArticleMapping articleMapping) {
        this.articleMapping = articleMapping;
    }

    public AccountingArticle getAccountingArticle() {
        return accountingArticle;
    }

    public void setAccountingArticle(AccountingArticle accountingArticle) {
        this.accountingArticle = accountingArticle;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(ProductTemplate productTemplate) {
        this.productTemplate = productTemplate;
    }

    public ChargeTemplate getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(ChargeTemplate chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public List<AttributeMapping> getAttributesMapping() {
        return attributesMapping;
    }

    public void setAttributesMapping(List<AttributeMapping> attributesMapping) {
        this.attributesMapping = attributesMapping;
    }

    public String getMappingKelEL() {
        return mappingKelEL;
    }

    public void setMappingKelEL(String mappingKelEL) {
        this.mappingKelEL = mappingKelEL;
    }
}
