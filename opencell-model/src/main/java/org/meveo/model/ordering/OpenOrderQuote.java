package org.meveo.model.ordering;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;

@Entity
@Table(name = "open_order_quote")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_quote_seq"),})
public class OpenOrderQuote extends BusinessEntity {

    @Enumerated(EnumType.STRING)
	@Column(name = "open_order_type", length = 50)
    @NotNull
    private OpenOrderTypeEnum openOrderType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "open_order_template_tags", joinColumns = @JoinColumn(name = "open_order_template_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "open_order_template_id")
    private List<Threshold> thresholds;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "open_order_template_products", joinColumns = @JoinColumn(name = "open_order_template_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private List<Product> products;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "open_order_template_articles", joinColumns = @JoinColumn(name = "open_order_template_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"))
    private List<AccountingArticle> articles;

    @Column(name = "status")
	@Enumerated(EnumType.STRING)
    private OpenOrderQuoteStatusEnum status;

    public OpenOrderTypeEnum getOpenOrderType() {
        return openOrderType;
    }

    public void setOpenOrderType(OpenOrderTypeEnum openOrderType) {
        this.openOrderType = openOrderType;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<Threshold> thresholds) {
        this.thresholds = thresholds;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<AccountingArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<AccountingArticle> articles) {
        this.articles = articles;
    }
}
