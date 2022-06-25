package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "open_order_template")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_template_seq"),})
public class OpenOrderTemplate extends BusinessEntity {

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
    @JoinTable(name = "open_order_template_products", joinColumns = @JoinColumn(name = "open_order_template_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "open_product_id", referencedColumnName = "id"))
    private List<OpenOrderProduct> products;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "open_order_template_articles", joinColumns = @JoinColumn(name = "open_order_template_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "open_article_id", referencedColumnName = "id"))
    private List<OpenOrderArticle> articles;

    @Column(name = "status")
	@Enumerated(EnumType.STRING)
    private OpenOrderTemplateStatusEnum status;

     @Column(name = "template_name", nullable = false)
    private String templateName;

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

    public List<OpenOrderProduct> getProducts() {
		return products;
	}

	public void setProducts(List<OpenOrderProduct> products) {
		this.products = products;
	}

	public List<OpenOrderArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<OpenOrderArticle> articles) {
		this.articles = articles;
	}

	public OpenOrderTemplateStatusEnum getStatus() {
        return status;
    }

    public void setStatus(OpenOrderTemplateStatusEnum status) {
        this.status = status;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
