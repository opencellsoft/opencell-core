package org.meveo.model.article;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.OperatorEnum;

@Entity
@Table(name = "billing_article_mapping_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_article_mapping_line_seq") })
@NamedQueries({
        @NamedQuery(name = "ArticleMappingLine.findAll", query = "SELECT a FROM ArticleMappingLine a")})
@Cacheable
public class ArticleMappingLine extends BusinessEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 6999650126224128377L;

    public ArticleMappingLine() {
		super();
	}

	@OneToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "article_mapping_id")
    private ArticleMapping articleMapping;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "article_id")
    private AccountingArticle accountingArticle;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "attribute_operator", nullable = false)
    private OperatorEnum attributeOperator = OperatorEnum.AND;

    @OneToMany(mappedBy = "articleMappingLine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<AttributeMapping> attributesMapping = new ArrayList<AttributeMapping>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "offer_template_id")
    private OfferTemplate offerTemplate;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

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
    private String mappingKeyEL;

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public String getMappingKeyEL() {
        return mappingKeyEL;
    }

    public void setMappingKeyEL(String mappingKelEL) {
        this.mappingKeyEL = mappingKelEL;
    }

    public OperatorEnum getAttributeOperator() {
        return attributeOperator;
    }

    public void setAttributeOperator(OperatorEnum attributeOperator) {
        this.attributeOperator = attributeOperator;
    }
}
