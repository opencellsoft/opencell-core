package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OfferProductTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.tmf.dsmapi.catalog.resource.AbstractCatalogEntity;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.ServiceLevelAgreement;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.resource.ResourceCandidate;
import org.tmf.dsmapi.catalog.resource.service.ServiceCandidate;
import org.tmf.dsmapi.commons.Utilities;
import org.tmf.dsmapi.commons.annotation.EntityReferenceProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author pierregauthier
 *
 *         { "id": "42", "version": "3.43", "href":
 *         "http://serverlocation:port/catalogManagement/productOffering/42",
 *         "name": "Virtual Storage Medium", "description":
 *         "Virtual Storage Medium", "lastUpdate": "2013-04-19T16:42:23-04:00",
 *         "lifecycleStatus": "Active", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime":
 *         "2013-06-19T00:00:00-04:00" }, "isBundle": "true", "category": [ {
 *         "id": "12", "version": "2.0", "href":
 *         "http://serverlocation:port/catalogManagement/category/12", "name":
 *         "Cloud offerings" } ], "channel": [ { "id": "13", "href":
 *         "http://serverlocation:port/marketSales/channel/13", "name":
 *         "Online Channel" } ], "place": [ { "id": "12", "href":
 *         "http://serverlocation:port/marketSales/place/12", "name": "France" }
 *         ], "bundledProductOffering": [ { "id": "15", "href":
 *         "http://serverlocation:port/catalogManagement/productOffering/15",
 *         "lifecycleStatus": "Active", "name": "Offering 15" }, { "id": "64",
 *         "href":
 *         "http://serverlocation:port/catalogManagement/productOffering/64",
 *         "lifecycleStatus": "Active", "name": "Offering 64" } ],
 *         "serviceLevelAgreement": { "id": "28", "href":
 *         "http://serverlocation:port/slaManagement/serviceLevelAgreement/28",
 *         "name": "Standard SLA" }, "productSpecification": { "id": "13",
 *         "href":
 *         "http://serverlocation:port/catalogManagement/productSpecification/13",
 *         "version": "2.0", "name": "specification product 1" },
 *         "serviceCandidate": [ { "id": "13", "href":
 *         "http://serverlocation:port/catalogManagement/serviceCandidate/13",
 *         "version": "2.0", "name": "specification service 1" } ],
 *         "resourceCandidate": [ { "id": "13", "href":
 *         "http://serverlocation:port/catalogManagement/resourceCandidate/13",
 *         "version": "2.0", "name": "specification resource 1" } ],
 *         "productOfferingTerm": [ { "name": "12 Month", "description":
 *         "12 month contract", "duration": "12", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23-04:00", "endDateTime":
 *         "2013-06-19T00:00:00-04:00" } } ], "productOfferingPrice": [ {
 *         "name": "Monthly Price", "description": "monthlyprice", "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00", "endDateTime":
 *         "2013-06-19T00:00:00-04:00" }, "priceType": "recurring",
 *         "unitOfMeasure": "", "price": { "taxIncludedAmount": "12.00",
 *         "dutyFreeAmount": "10.00", "taxRate": "20.00", "currencyCode": "EUR",
 *         "percentage": 0 }, "recurringChargePeriod": "monthly" }, { "name":
 *         "Usage Price", "description": "usageprice", "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00", "endDateTime":
 *         "2013-06-19T00:00:00-04:00" }, "priceType": "usage", "unitOfMeasure":
 *         "second", "price": { "taxIncludedAmount": "12.00", "dutyFreeAmount":
 *         "10.00", "taxRate": "20.00", "currencyCode": "EUR", "percentage": 0
 *         }, "recurringChargePeriod": "", "productOfferPriceAlteration": {
 *         "name": "Shipping Discount", "description":
 *         "One time shipping discount", "validFor": { "startDateTime":
 *         "2013-04-19T16:42:23.0Z" }, "priceType": "One Time discount",
 *         "unitOfMeasure": "", "price": { "percentage": 100 },
 *         "recurringChargePeriod": "", "priceCondition":
 *         "apply if total amount of the  order is greater than 300.00" } } ] }
 *
 */
@MappedSuperclass
@XmlRootElement
@JsonInclude(value=Include.NON_NULL)
public class ProductOffering extends AbstractCatalogEntity implements Serializable {
	private final static long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger(ProductOffering.class.getName());

	@Column(name = "IS_BUNDLE", nullable = true)
	private Boolean isBundle;

	@Embedded
	@ElementCollection
	@CollectionTable(name = "CRI_PRODUCT_OFFER_R_CATEGORY", joinColumns = { @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
			@JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"), @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
			@JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION") })
	@EntityReferenceProperty(classId = Category.class)
	private List<CatalogReference> category;

	@Embedded
	@ElementCollection
	@CollectionTable(name = "CRI_PRODUCT_OFFER_R_CHANNEL", joinColumns = { @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
			@JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"), @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
			@JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION") })
	private List<Channel> channel;

	@Embedded
	@ElementCollection
	@CollectionTable(name = "CRI_PRODUCT_OFFER_R_PLACE", joinColumns = { @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
			@JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"), @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
			@JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION") })
	private List<Place> place;

	@Embedded
	@ElementCollection
	@CollectionTable(name = "CRI_PRODUCT_OFFER_R_PRODUCT_OFFER", joinColumns = { @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
			@JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"), @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
			@JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION") })
	@EntityReferenceProperty(classId = ProductOffering.class)
	private List<BundledProductReference> bundledProductOffering;

	@Embedded
	private ServiceLevelAgreement serviceLevelAgreement;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "referencedId", column = @Column(name = "SPECIFICATION_ID")),
			@AttributeOverride(name = "referencedVersion", column = @Column(name = "SPECIFICATION_VERSION")),
			@AttributeOverride(name = "referencedHref", column = @Column(name = "SPECIFICATION_HREF")),
			@AttributeOverride(name = "referencedName", column = @Column(name = "SPECIFICATION_NAME")),
			@AttributeOverride(name = "referencedDescription", column = @Column(name = "SPECIFICATION_DESCRIPTION")) })
	@EntityReferenceProperty(classId = ProductSpecification.class)
	private CatalogReference productSpecification;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "referencedId", column = @Column(name = "SERVICE_CANDIDATE_ID")),
			@AttributeOverride(name = "referencedVersion", column = @Column(name = "SERVICE_CANDIDATE_VERSION")),
			@AttributeOverride(name = "referencedHref", column = @Column(name = "SERVICE_CANDIDATE_HREF")),
			@AttributeOverride(name = "referencedName", column = @Column(name = "SERVICE_CANDIDATE_NAME")),
			@AttributeOverride(name = "referencedDescription", column = @Column(name = "SERVICE_CANDIDATE_DESCRIPTION")) })
	@EntityReferenceProperty(classId = ServiceCandidate.class)
	private CatalogReference serviceCandidate;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "referencedId", column = @Column(name = "RESOURCE_CANDIDATE_ID")),
			@AttributeOverride(name = "referencedVersion", column = @Column(name = "RESOURCE_CANDIDATE_VERSION")),
			@AttributeOverride(name = "referencedHref", column = @Column(name = "RESOURCE_CANDIDATE_HREF")),
			@AttributeOverride(name = "referencedName", column = @Column(name = "RESOURCE_CANDIDATE_NAME")),
			@AttributeOverride(name = "referencedDescription", column = @Column(name = "RESOURCE_CANDIDATE_DESCRIPTION")) })
	@EntityReferenceProperty(classId = ResourceCandidate.class)
	private CatalogReference resourceCandidate;

	@ElementCollection
	@CollectionTable(name = "CRI_PRODUCT_OFFER_R_OFFERING_TERM", joinColumns = { @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
			@JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"), @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
			@JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION") })
	private List<ProductOfferingTerm> productOfferingTerm;

	@Embedded
	@ElementCollection
	@CollectionTable(name = "CRI_PRODUCT_OFFER_R_PRICE", joinColumns = { @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
			@JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"), @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
			@JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION") })
	private List<ProductOfferingPrice> productOfferingPrice;

	public ProductOffering() {
	}

	public ProductOffering(OfferTemplate offer, UriInfo uriInfo, Category category, List<ProductOfferingPrice> offerPrices) {
		this.setId(offer.getCode());
		this.setVersion(String.format("%d.0", offer.getVersion() == null ? 0 : offer.getVersion()));
		this.setHref(String.format("%scatalogManagement/productOffering/%s", uriInfo.getBaseUri().toString(), offer.getCode()));

		this.setName(offer.getCode());
		this.setDescription(offer.getDescription());
		this.setLastUpdate(offer.getAuditable() != null ? offer.getAuditable().getLastModified() : null);
		this.setLifecycleStatus(offer.isActive() ? LifecycleStatus.ACTIVE : LifecycleStatus.OBSOLETE);
		this.setValidFor(new TimeRange());
		this.getValidFor().setStartDateTime(offer.getAuditable() != null ? offer.getAuditable().getCreated() : null);
		if (!offer.isActive()) {
			this.getValidFor().setEndDateTime(offer.getAuditable() != null ? offer.getAuditable().getUpdated() : null);
		}

		this.setCategory(new ArrayList<CatalogReference>());
		this.getCategory().add(category.getCatalogReference());

		this.setChannel(new ArrayList<Channel>()); // leave empty

		this.setPlace(new ArrayList<Place>()); // leave empty

		List<OfferProductTemplate> offerProductTemplates = offer.getOfferProductTemplates();
		this.setBundledProductOffering(new ArrayList<BundledProductReference>());
		if (offerProductTemplates != null && !offerProductTemplates.isEmpty()) {
			this.setIsBundle(Boolean.TRUE);
			populateBundledProductOfferings(this, uriInfo, offerProductTemplates);
		} else {
			this.setIsBundle(Boolean.FALSE);
		}

		this.setServiceLevelAgreement(null);
		this.setProductSpecification(null);// Product
											// Specification
											// created from the
											// offer (see below)
		this.setServiceCandidate(null);
		this.setResourceCandidate(null);
		this.setProductOfferingTerm(new ArrayList<ProductOfferingTerm>()); // empty
		this.setProductOfferingPrice(offerPrices);// empty
	}

	private void populateBundledProductOfferings(ProductOffering productOffering, UriInfo uriInfo, List<OfferProductTemplate> offerProductTemplates) {
		BundledProductReference bundledProductReference = null;
		ProductTemplate productTemplate = null;
		for (OfferProductTemplate offerProductTemplate : offerProductTemplates) {
			productTemplate = offerProductTemplate.getProductTemplate();
			bundledProductReference = new BundledProductReference();

			bundledProductReference.setReferencedId(productTemplate.getCode());
			bundledProductReference.setReferencedHref(String.format("%scatalogManagement/productOffering/%s", uriInfo.getBaseUri().toString(), productTemplate.getCode()));
			bundledProductReference.setReferencedName(productTemplate.getCode());
			bundledProductReference.setReferencedLifecycleStatus(productTemplate.isActive() ? LifecycleStatus.ACTIVE : LifecycleStatus.OBSOLETE);

			productOffering.getBundledProductOffering().add(bundledProductReference);

		}
	}

	public Boolean getIsBundle() {
		return isBundle;
	}

	public void setIsBundle(Boolean isBundle) {
		this.isBundle = isBundle;
	}

	public List<CatalogReference> getCategory() {
		return category;
	}

	public void setCategory(List<CatalogReference> category) {
		this.category = category;
	}

	public List<Channel> getChannel() {
		return channel;
	}

	public void setChannel(List<Channel> channel) {
		this.channel = channel;
	}

	public List<Place> getPlace() {
		return place;
	}

	public void setPlace(List<Place> place) {
		this.place = place;
	}

	public List<BundledProductReference> getBundledProductOffering() {
		return bundledProductOffering;
	}

	public void setBundledProductOffering(List<BundledProductReference> bundledProductOffering) {
		this.bundledProductOffering = bundledProductOffering;
	}

	public ServiceLevelAgreement getServiceLevelAgreement() {
		return serviceLevelAgreement;
	}

	public void setServiceLevelAgreement(ServiceLevelAgreement serviceLevelAgreement) {
		this.serviceLevelAgreement = serviceLevelAgreement;
	}

	public CatalogReference getProductSpecification() {
		return productSpecification;
	}

	public void setProductSpecification(CatalogReference productSpecification) {
		this.productSpecification = productSpecification;
	}

	public CatalogReference getServiceCandidate() {
		return serviceCandidate;
	}

	public void setServiceCandidate(CatalogReference serviceCandidate) {
		this.serviceCandidate = serviceCandidate;
	}

	public CatalogReference getResourceCandidate() {
		return resourceCandidate;
	}

	public void setResourceCandidate(CatalogReference resourceCandidate) {
		this.resourceCandidate = resourceCandidate;
	}

	public List<ProductOfferingTerm> getProductOfferingTerm() {
		return productOfferingTerm;
	}

	public void setProductOfferingTerm(List<ProductOfferingTerm> productOfferingTerm) {
		this.productOfferingTerm = productOfferingTerm;
	}

	public List<ProductOfferingPrice> getProductOfferingPrice() {
		return productOfferingPrice;
	}

	public void setProductOfferingPrice(List<ProductOfferingPrice> productOfferingPrice) {
		this.productOfferingPrice = productOfferingPrice;
	}

	@JsonProperty(value = "category")
	public List<CatalogReference> categoryToJson() {
		return (category != null && category.size() > 0) ? category : null;
	}

	@JsonProperty(value = "channel")
	public List<Channel> channelToJson() {
		return (channel != null && channel.size() > 0) ? channel : null;
	}

	@JsonProperty(value = "place")
	public List<Place> placeToJson() {
		return (place != null && place.size() > 0) ? place : null;
	}

	@JsonProperty(value = "bundledProductOffering")
	public List<BundledProductReference> bundledProductOfferingToJson() {
		return (bundledProductOffering != null && bundledProductOffering.size() > 0) ? bundledProductOffering : null;
	}

	@JsonProperty(value = "productOfferingTerm")
	public List<ProductOfferingTerm> productOfferingTermToJson() {
		return (productOfferingTerm != null && productOfferingTerm.size() > 0) ? productOfferingTerm : null;
	}

	@JsonProperty(value = "productOfferingPrice")
	public List<ProductOfferingPrice> productOfferingPriceToJson() {
		return (productOfferingPrice != null && productOfferingPrice.size() > 0) ? productOfferingPrice : null;
	}

	@Override
	public int hashCode() {
		int hash = 5;

		hash = 73 * hash + super.hashCode();

		hash = 73 * hash + (this.isBundle != null ? this.isBundle.hashCode() : 0);
		hash = 73 * hash + (this.category != null ? this.category.hashCode() : 0);
		hash = 73 * hash + (this.channel != null ? this.channel.hashCode() : 0);
		hash = 73 * hash + (this.place != null ? this.place.hashCode() : 0);
		hash = 73 * hash + (this.bundledProductOffering != null ? this.bundledProductOffering.hashCode() : 0);
		hash = 73 * hash + (this.serviceLevelAgreement != null ? this.serviceLevelAgreement.hashCode() : 0);
		hash = 73 * hash + (this.productSpecification != null ? this.productSpecification.hashCode() : 0);
		hash = 73 * hash + (this.serviceCandidate != null ? this.serviceCandidate.hashCode() : 0);
		hash = 73 * hash + (this.resourceCandidate != null ? this.resourceCandidate.hashCode() : 0);
		hash = 73 * hash + (this.productOfferingTerm != null ? this.productOfferingTerm.hashCode() : 0);
		hash = 73 * hash + (this.productOfferingPrice != null ? this.productOfferingPrice.hashCode() : 0);

		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
			return false;
		}

		final ProductOffering other = (ProductOffering) object;
		if (Utilities.areEqual(this.isBundle, other.isBundle) == false) {
			return false;
		}

		if (Utilities.areEqual(this.category, other.category) == false) {
			return false;
		}

		if (Utilities.areEqual(this.channel, other.channel) == false) {
			return false;
		}

		if (Utilities.areEqual(this.place, other.place) == false) {
			return false;
		}

		if (Utilities.areEqual(this.bundledProductOffering, other.bundledProductOffering) == false) {
			return false;
		}

		if (Utilities.areEqual(this.serviceLevelAgreement, other.serviceLevelAgreement) == false) {
			return false;
		}

		if (Utilities.areEqual(this.productSpecification, other.productSpecification) == false) {
			return false;
		}

		if (Utilities.areEqual(this.serviceCandidate, other.serviceCandidate) == false) {
			return false;
		}

		if (Utilities.areEqual(this.resourceCandidate, other.resourceCandidate) == false) {
			return false;
		}

		if (Utilities.areEqual(this.productOfferingTerm, other.productOfferingTerm) == false) {
			return false;
		}

		if (Utilities.areEqual(this.productOfferingPrice, other.productOfferingPrice) == false) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "ProductOffering{<" + super.toString() + ">, isBundle=" + isBundle + ", category=" + category + ", channel=" + channel + ", place=" + place
				+ ", bundledProductOffering=" + bundledProductOffering + ", serviceLevelAgreement=" + serviceLevelAgreement + ", productSpecification=" + productSpecification
				+ ", serviceCandidate=" + serviceCandidate + ", resourceCandidate=" + resourceCandidate + ", productOfferingTerm=" + productOfferingTerm + ", productOfferingPrice="
				+ productOfferingPrice + '}';
	}

	@Override
	@JsonIgnore
	public Logger getLogger() {
		return logger;
	}

	@Override
	@JsonIgnore
	public void setCreateDefaults() {
		super.setCreateDefaults();

		if (isBundle == null) {
			isBundle = false;
		}
	}

	public void edit(ProductOffering input) {
		if (input == null || input == this) {
			return;
		}

		super.edit(input);

		if (this.isBundle == null) {
			this.isBundle = input.isBundle;
		}

		if (this.category == null) {
			this.category = input.category;
		}

		if (this.channel == null) {
			this.channel = input.channel;
		}

		if (this.place == null) {
			this.place = input.place;
		}

		if (this.bundledProductOffering == null) {
			this.bundledProductOffering = input.bundledProductOffering;
		}

		if (this.serviceLevelAgreement == null) {
			this.serviceLevelAgreement = input.serviceLevelAgreement;
		}

		if (this.productSpecification == null) {
			this.productSpecification = input.productSpecification;
		}

		if (this.serviceCandidate == null) {
			this.serviceCandidate = input.serviceCandidate;
		}

		if (this.resourceCandidate == null) {
			this.resourceCandidate = input.resourceCandidate;
		}

		if (this.productOfferingTerm == null) {
			this.productOfferingTerm = input.productOfferingTerm;
		}

		if (this.productOfferingPrice == null) {
			this.productOfferingPrice = input.productOfferingPrice;
		}
	}

	@Override
	@JsonIgnore
	public boolean isValid() {
		logger.log(Level.FINE, "ProductOffering:valid ()");

		if (super.isValid() == false) {
			return false;
		}

		if (this.isBundle == Boolean.TRUE) {
			if (Utilities.hasContents(this.bundledProductOffering) == false) {
				logger.log(Level.FINE, " invalid: bundledProductOffering must be specified when isBundle is true");
				return false;
			}
		} else {
			if (Utilities.hasContents(this.bundledProductOffering) == true) {
				logger.log(Level.FINE, " invalid: bundledProductOffering must not be specififed when isBundle is false");
				return false;
			}
		}

		return true;
	}

	public static ProductOffering createProto() {
		ProductOffering productOffering = new ProductOffering();

		productOffering.setId("id");
		productOffering.setVersion("3.43");
		productOffering.setHref("href");
		productOffering.setName("name");
		productOffering.setDescription("description");
		productOffering.setLastUpdate(new Date());
		productOffering.setLifecycleStatus(LifecycleStatus.ACTIVE);
		productOffering.setValidFor(TimeRange.createProto());

		productOffering.isBundle = true;

		productOffering.category = new ArrayList<CatalogReference>();
		productOffering.category.add(CatalogReference.createProto());

		productOffering.channel = new ArrayList<Channel>();
		productOffering.channel.add(Channel.createProto());

		productOffering.place = new ArrayList<Place>();
		productOffering.place.add(Place.createProto());

		productOffering.bundledProductOffering = new ArrayList<BundledProductReference>();
		productOffering.bundledProductOffering.add(BundledProductReference.createProto());

		productOffering.serviceLevelAgreement = ServiceLevelAgreement.createProto();
		productOffering.productSpecification = CatalogReference.createProto();
		productOffering.serviceCandidate = CatalogReference.createProto();
		productOffering.resourceCandidate = CatalogReference.createProto();

		productOffering.productOfferingTerm = new ArrayList<ProductOfferingTerm>();
		productOffering.productOfferingTerm.add(ProductOfferingTerm.createProto());

		productOffering.productOfferingPrice = new ArrayList<ProductOfferingPrice>();
		productOffering.productOfferingPrice.add(ProductOfferingPrice.createProto());

		return productOffering;
	}
}
