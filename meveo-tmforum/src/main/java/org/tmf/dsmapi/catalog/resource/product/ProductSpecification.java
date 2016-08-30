package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.tmf.dsmapi.catalog.resource.AbstractCatalogEntity;
import org.tmf.dsmapi.catalog.resource.Attachment;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.RelatedParty;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.resource.ResourceSpecification;
import org.tmf.dsmapi.catalog.resource.service.ServiceSpecification;
import org.tmf.dsmapi.catalog.resource.specification.CharacteristicValueType;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationCharacteristicRelationship;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationCharacteristicValue;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationRelationship;
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
 * {
 *     "id": "22",
 *     "version": "1.72",
 *     "href": "http://serverlocation:port/catalogManagement/productSpecification/22",
 *     "name": "iPhone 42",
 *     "description": "Siri works on this iPhone",
 *     "lastUpdate": "2013-04-19T16:42:23-04:00",
 *     "lifecycleStatus": "Active",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00"
 *     },
 *     "productNumber", "I42-340-DX",
 *     "isBundle": "true",
 *     "brand": "Apple",
 *     "attachment": [
 *         {
 *             "id": "22",
 *             "href": "http://serverlocation:port/documentManagement/attachment/22",
 *             "type": "Picture",
 *             "url": "http://xxxxx"
 *         }
 *     ],
 *     "relatedParty": [
 *         {
 *             "role": "Owner",
 *             "id": "1234",
 *             "href": "http ://serverLocation:port/partyManagement/partyRole/1234"
 *         }
 *     ],
 *     "bundledProductSpecification": [
 *         {
 *             "id": "15",
 *             "href": "http://serverlocation:port/catalogManagement/productSpecification/15",
 *             "lifecycleStatus": "Active",
 *             "name": "Product specification 15"
 *         },
 *         {
 *             "id": "64",
 *             "href": "http://serverlocation:port/catalogManagement/productSpecification/64",
 *             "lifecycleStatus": "Active",
 *             "name": "Product specification 64"
 *         }
 *     ],
 *     "productSpecificationRelationship": [
 *         {
 *             "type": "dependency",
 *             "id": "23",
 *             "href": " http://serverlocation:port/catalogManagement/productSpecification/23",
 *             "validFor": {
 *                 "startDateTime": "2013-04-19T16:42:23-04:00",
 *                 "endDateTime": ""
 *             }
 *         }
 *     ],
 *     "serviceSpecification": [
 *         {
 *             "id": "13",
 *             "href": "http://serverlocation:port/catalogManagement/serviceSpecification/13",
 *             "name": "specification 1",
 *             "version": "1.1"
 *         }
 *     ],
 *     "resourceSpecification": [
 *         {
 *             "id": "13",
 *             "href": "http://serverlocation:port/catalogManagement/resourceSpecification/13",
 *             "name": "specification 1",
 *             "version": "1.1"
 *         }
 *     ],
 *     "productSpecCharacteristic": [
 *         {
 *             "id": "42",
 *             "name": "Screen Size",
 *             "description": "Screen size",
 *             "valueType": "number",
 *             "configurable": "false",
 *             "validFor": {
 *                 "startDateTime": "2013-04-19T16:42:23-04:00",
 *                 "endDateTime": ""
 *             },
 *             "productSpecCharRelationship": [
 *                 {
 *                     "type": "dependency",
 *                     "id": "43",
 *                     "validFor": {
 *                         "startDateTime": "2013-04-19T16:42:23-04:00",
 *                         "endDateTime": ""
 *                     }
 *                 }
 *             ],
 *             "productSpecCharacteristicValue": [
 *                 {
 *                     "valueType": "number",
 *                     "default": "true",
 *                     "value": "4.2",
 *                     "unitOfMeasure": "inches",
 *                     "valueFrom": "",
 *                     "valueTo": "",
 *                     "validFor": {
 *                         "startDateTime": "2013-04-19T16:42:23-04:00",
 *                         "endDateTime": ""
 *                     }
 *                 }
 *             ]
 *         },
 *         {
 *             "id": "34",
 *             "name": "Colour",
 *             "description": "Colour",
 *             "valueType": "string",
 *             "configurable": "true",
 *             "validFor": {
 *                 "startDateTime": "2013-04-19T16:42:23-04:00",
 *                 "endDateTime": ""
 *             },
 *             "productSpecCharacteristicValue": [
 *                 {
 *                     "valueType": "string",
 *                     "default": "true",
 *                     "value": "Black",
 *                     "unitOfMeasure": "",
 *                     "valueFrom": "",
 *                     "valueTo": "",
 *                     "validFor": {
 *                         "startDateTime": "2013-04-19T16:42:23-04:00",
 *                         "endDateTime": ""
 *                     }
 *                 },
 *                 {
 *                     "valueType": "string",
 *                     "default": "false",
 *                     "value": "White",
 *                     "unitOfMeasure": "",
 *                     "valueFrom": "",
 *                     "valueTo": "",
 *                     "validFor": {
 *                         "startDateTime": "2013-04-19T16:42:23-04:00",
 *                         "endDateTime": ""
 *                     }
 *                 }
 *             ]
 *         }
 *     ]
 * }
 *
 */
@MappedSuperclass
@XmlRootElement
@JsonInclude(value=Include.NON_NULL)
public class ProductSpecification extends AbstractCatalogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ProductSpecification.class.getName());

    @Column(name = "PRODUCT_NUMBER", nullable = true)
    private String productNumber;
    
    @Column(name = "IS_BUNDLE", nullable = true)
    private Boolean isBundle;

    @Column(name = "BRAND", nullable = true)
    private String brand;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_ATTACHMENT", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    private List<Attachment> attachment;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_PARTY", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    private List<RelatedParty> relatedParty;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_PRODUCT_SPEC", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    @EntityReferenceProperty(classId=ProductSpecification.class)
    private List<BundledProductReference> bundledProductSpecification;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_RELATIONSHIP", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    private List<SpecificationRelationship> productSpecificationRelationship;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_SERVICE_SPEC", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    @EntityReferenceProperty(classId=ServiceSpecification.class)
    private List<CatalogReference> serviceSpecification;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_RESOURCE_SPEC", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    @EntityReferenceProperty(classId=ResourceSpecification.class)
    private List<CatalogReference> resourceSpecification;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "CRI_PRODUCT_SPEC_R_CHARACTERISTIC", joinColumns = {
        @JoinColumn(name = "CATALOG_ID", referencedColumnName = "CATALOG_ID"),
        @JoinColumn(name = "CATALOG_VERSION", referencedColumnName = "CATALOG_VERSION"),
        @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ID"),
        @JoinColumn(name = "ENTITY_VERSION", referencedColumnName = "VERSION")
    })
    private List<ProductSpecCharacteristic> productSpecCharacteristic;

    public ProductSpecification() {
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public Boolean getIsBundle() {
        return isBundle;
    }

    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public List<BundledProductReference> getBundledProductSpecification() {
        return bundledProductSpecification;
    }

    public void setBundledProductSpecification(List<BundledProductReference> bundledProductSpecification) {
        this.bundledProductSpecification = bundledProductSpecification;
    }

    public List<SpecificationRelationship> getProductSpecificationRelationship() {
        return productSpecificationRelationship;
    }

    public void setProductSpecificationRelationship(List<SpecificationRelationship> productSpecificationRelationship) {
        this.productSpecificationRelationship = productSpecificationRelationship;
    }

    public List<CatalogReference> getServiceSpecification() {
        return serviceSpecification;
    }

    public void setServiceSpecification(List<CatalogReference> serviceSpecification) {
        this.serviceSpecification = serviceSpecification;
    }

    public List<CatalogReference> getResourceSpecification() {
        return resourceSpecification;
    }

    public void setResourceSpecification(List<CatalogReference> resourceSpecification) {
        this.resourceSpecification = resourceSpecification;
    }

    public List<ProductSpecCharacteristic> getProductSpecCharacteristic() {
        return productSpecCharacteristic;
    }

    public void setProductSpecCharacteristic(List<ProductSpecCharacteristic> productSpecCharacteristic) {
        this.productSpecCharacteristic = productSpecCharacteristic;
    }

    @JsonProperty(value = "attachment")
    public List<Attachment> attachmentToJson() {
        return (attachment != null && attachment.size() > 0) ? attachment : null;
    }

    @JsonProperty(value = "relatedParty")
    public List<RelatedParty> relatedPartyToJson() {
        return (relatedParty != null && relatedParty.size() > 0) ? relatedParty : null;
    }

    @JsonProperty(value = "bundledProductSpecification")
    public List<BundledProductReference> bundledProductSpecificationToJson() {
        return (bundledProductSpecification != null && bundledProductSpecification.size() > 0) ? bundledProductSpecification : null;
    }

    @JsonProperty(value = "productSpecificationRelationship")
    public List<SpecificationRelationship> productSpecificationRelationshipToJson() {
        return (productSpecificationRelationship != null && productSpecificationRelationship.size() > 0) ? productSpecificationRelationship : null;
    }

    @JsonProperty(value = "serviceSpecification")
    public List<CatalogReference> serviceSpecificationToJson() {
        return (serviceSpecification != null && serviceSpecification.size() > 0) ? serviceSpecification : null;
    }

    @JsonProperty(value = "resourceSpecification")
    public List<CatalogReference> resourceSpecificationToJson() {
        return (resourceSpecification != null && resourceSpecification.size() > 0) ? resourceSpecification : null;
    }

    @JsonProperty(value = "productSpecCharacteristic")
    public List<ProductSpecCharacteristic> productSpecCharacteristicToJson() {
        return (productSpecCharacteristic != null && productSpecCharacteristic.size() > 0) ? productSpecCharacteristic : null;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 29 * hash + super.hashCode();

        hash = 29 * hash + (this.productNumber != null ? this.productNumber.hashCode() : 0);
        hash = 29 * hash + (this.isBundle != null ? this.isBundle.hashCode() : 0);
        hash = 29 * hash + (this.brand != null ? this.brand.hashCode() : 0);
        hash = 29 * hash + (this.attachment != null ? this.attachment.hashCode() : 0);
        hash = 29 * hash + (this.relatedParty != null ? this.relatedParty.hashCode() : 0);
        hash = 29 * hash + (this.bundledProductSpecification != null ? this.bundledProductSpecification.hashCode() : 0);
        hash = 29 * hash + (this.productSpecificationRelationship != null ? this.productSpecificationRelationship.hashCode() : 0);
        hash = 29 * hash + (this.serviceSpecification != null ? this.serviceSpecification.hashCode() : 0);
        hash = 29 * hash + (this.resourceSpecification != null ? this.resourceSpecification.hashCode() : 0);
        hash = 29 * hash + (this.productSpecCharacteristic != null ? this.productSpecCharacteristic.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass() || super.equals(object) == false) {
            return false;
        }

        final ProductSpecification other = (ProductSpecification) object;
        if (Utilities.areEqual(this.productNumber, other.productNumber) == false) {
            return false;
        }
        
        if (Utilities.areEqual(this.isBundle, other.isBundle) == false) {
            return false;
        }

        if (Utilities.areEqual(this.brand, other.brand) == false) {
            return false;
        }

        if (Utilities.areEqual(this.attachment, other.attachment) == false) {
            return false;
        }

        if (Utilities.areEqual(this.relatedParty, other.relatedParty) == false) {
            return false;
        }

        if (Utilities.areEqual(this.bundledProductSpecification, other.bundledProductSpecification) == false) {
            return false;
        }

        if (Utilities.areEqual(this.productSpecificationRelationship, other.productSpecificationRelationship) == false) {
            return false;
        }

        if (Utilities.areEqual(this.serviceSpecification, other.serviceSpecification) == false) {
            return false;
        }

        if (Utilities.areEqual(this.resourceSpecification, other.resourceSpecification) == false) {
            return false;
        }

        if (Utilities.areEqual(this.productSpecCharacteristic, other.productSpecCharacteristic) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ProductSpecification{<" + super.toString() + ">, productNumber=" + productNumber + ", isBundle=" + isBundle + ", brand=" + brand + ", attachment=" + attachment + ", relatedParty=" + relatedParty + ", bundledProductSpecification=" + bundledProductSpecification + ", productSpecificationRelationship=" + productSpecificationRelationship + ", serviceSpecification=" + serviceSpecification + ", resourceSpecification=" + resourceSpecification + ", productSpecCharacteristic=" + productSpecCharacteristic + '}';
    }

    @Override
    @JsonIgnore
    public Logger fetchLogger() {
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

    public void edit(ProductSpecification input) {
        if (input == null || input == this) {
            return;
        }

        super.edit(input);

        if (this.productNumber == null) {
            this.productNumber = input.productNumber;
        }
        
        if (this.isBundle == null) {
            this.isBundle = input.isBundle;
        }

        if (this.brand == null) {
            this.brand = input.brand;
        }

        if (this.attachment == null) {
            this.attachment = input.attachment;
        }

        if (this.relatedParty == null) {
            this.relatedParty = input.relatedParty;
        }

        if (this.bundledProductSpecification == null) {
            this.bundledProductSpecification = input.bundledProductSpecification;
        }

        if (this.productSpecificationRelationship == null) {
            this.productSpecificationRelationship = input.productSpecificationRelationship;
        }

        if (this.serviceSpecification == null) {
            this.serviceSpecification = input.serviceSpecification;
        }

        if (this.resourceSpecification == null) {
            this.resourceSpecification = input.resourceSpecification;
        }

        if (this.productSpecCharacteristic == null) {
            this.productSpecCharacteristic = input.productSpecCharacteristic;
        }
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        logger.log(Level.FINE, "ProductSpecification:valid ()");

        if (super.isValid() == false) {
            return false;
        }

        if (validateIsBundle() == false) {
            return false;
        }

        if (validateCharacteristics() == false) {
            return false;
        }

        return true;
    }

    private boolean validateIsBundle() {
        if (this.isBundle == Boolean.TRUE) {
            if (Utilities.hasContents(this.bundledProductSpecification) == false) {
                logger.log(Level.FINE, " invalid: bundledProductSpecification must be specified when isBundle is true");
                return false;
            }
        }
        else {
            if (Utilities.hasContents(this.bundledProductSpecification) == true) {
                logger.log(Level.FINE, " invalid: bundledProductSpecification must not be specififed when isBundle is false");
                return false;
            }
        }

        return true;
    }

    public boolean validateCharacteristics() {
        if (Utilities.hasContents(this.productSpecCharacteristic) == false) {
            return true;
        }

        for (ProductSpecCharacteristic characteristic : this.productSpecCharacteristic) {
            if (characteristic.isValid() == false) {
                return false;
            }
        }

        return true;
    }

    public static ProductSpecification createProto() {
        ProductSpecification productSpecification = new ProductSpecification();

        productSpecification.setId("id");
        productSpecification.setVersion("1.72");
        productSpecification.setHref("href");
        productSpecification.setName("name");
        productSpecification.setDescription("description");
        productSpecification.setLastUpdate(new Date ());
        productSpecification.setLifecycleStatus(LifecycleStatus.ACTIVE);
        productSpecification.setValidFor(TimeRange.createProto ());

        productSpecification.productNumber = "I42-340-DX";
        productSpecification.isBundle = true;
        productSpecification.brand = "brand";

        productSpecification.attachment = new ArrayList<Attachment>();
        productSpecification.attachment.add(Attachment.createProto());

        productSpecification.relatedParty = new ArrayList<RelatedParty>();
        productSpecification.relatedParty.add(RelatedParty.createProto());

        productSpecification.bundledProductSpecification = new ArrayList<BundledProductReference>();
        productSpecification.bundledProductSpecification.add(BundledProductReference.createProto());

        productSpecification.productSpecificationRelationship = new ArrayList<SpecificationRelationship>();
        productSpecification.productSpecificationRelationship.add(SpecificationRelationship.createProto());

        productSpecification.serviceSpecification = new ArrayList<CatalogReference>();
        productSpecification.serviceSpecification.add(CatalogReference.createProto());

        productSpecification.resourceSpecification = new ArrayList<CatalogReference>();
        productSpecification.resourceSpecification.add(CatalogReference.createProto());

        productSpecification.productSpecCharacteristic = new ArrayList<ProductSpecCharacteristic>();
        productSpecification.productSpecCharacteristic.add(ProductSpecCharacteristic.createProto());

        return productSpecification;
    }
    
    public static ProductSpecification parseFromOfferTemplate(OfferTemplate offer,UriInfo uriInfo){
    	ProductSpecification productSpecification=new ProductSpecification();
    	try{
    	productSpecification.setId(offer.getCode());
		productSpecification.setVersion(String.format("%d.0", offer.getVersion() == null ? 0 : offer.getVersion()));
		productSpecification.setHref(String.format("%scatalogManagement/productSpecification/%s",
				uriInfo.getBaseUri().toString(), offer.getCode()));
		
		productSpecification.setName(offer.getCode());
		productSpecification.setDescription(offer.getDescription());
		productSpecification
				.setLastUpdate(offer.getAuditable() != null ? offer.getAuditable().getLastModified() : null);
		productSpecification
				.setLifecycleStatus(offer.isActive() ? LifecycleStatus.ACTIVE : LifecycleStatus.OBSOLETE);
		productSpecification.setValidFor(new TimeRange());
		productSpecification.getValidFor()
				.setStartDateTime(offer.getAuditable() != null ? offer.getAuditable().getCreated() : null);
		if (!offer.isActive()) {
			productSpecification.getValidFor()
					.setEndDateTime(offer.getAuditable() != null ? offer.getAuditable().getUpdated() : null);
		}

		productSpecification.setProductNumber(offer.getCode());
		productSpecification.setIsBundle(false);
		productSpecification.setBrand("");

		productSpecification.setAttachment(new ArrayList<Attachment>());// leave
																		// empty

		productSpecification.setRelatedParty(new ArrayList<RelatedParty>());// leave
																			// empty

		productSpecification.setBundledProductSpecification(new ArrayList<BundledProductReference>());// leave
																										// empty

		productSpecification.setProductSpecificationRelationship(new ArrayList<SpecificationRelationship>());// leave
																												// empty
		productSpecification.setServiceSpecification(new ArrayList<CatalogReference>());// leave
																						// empty

		productSpecification.setResourceSpecification(new ArrayList<CatalogReference>());// leave
																							// empty

		productSpecification.setProductSpecCharacteristic(new ArrayList<ProductSpecCharacteristic>());

		ProductSpecCharacteristic productSpecCharacteristic = new ProductSpecCharacteristic();

		productSpecCharacteristic.setId(offer.getCode());
		productSpecCharacteristic.setName("service");
		productSpecCharacteristic.setDescription("offer's service");
		productSpecCharacteristic.setValueType(CharacteristicValueType.STRING);
		productSpecCharacteristic.setConfigurable(Boolean.TRUE);
		productSpecCharacteristic.setValidFor(new TimeRange());
		productSpecCharacteristic.getValidFor()
				.setStartDateTime(offer.getAuditable() != null ? offer.getAuditable().getCreated() : null);
		productSpecCharacteristic
				.setProductSpecCharRelationship(new ArrayList<SpecificationCharacteristicRelationship>());// leav
		productSpecCharacteristic
				.setProductSpecCharacteristicValue(new ArrayList<SpecificationCharacteristicValue>());
		          
		if (offer.getOfferServiceTemplates() != null){
			for (OfferServiceTemplate service : offer.getOfferServiceTemplates()) {
				SpecificationCharacteristicValue specCharacteristicValue = new SpecificationCharacteristicValue();
				specCharacteristicValue.setValueType(CharacteristicValueType.STRING);
				specCharacteristicValue.setDefaultValue(Boolean.FALSE);
				specCharacteristicValue.setValue(service.getServiceTemplate() == null? null : service.getServiceTemplate().getCode());
				specCharacteristicValue.setUnitOfMeasure("unit");
				specCharacteristicValue
						.setValueFrom(offer.getAuditable() != null ? "" + offer.getAuditable().getCreated() : null);
				specCharacteristicValue.setValueTo("");
				specCharacteristicValue.setValidFor(new TimeRange());
				specCharacteristicValue.getValidFor()
						.setStartDateTime(offer.getAuditable() != null ? offer.getAuditable().getCreated() : null);
				productSpecCharacteristic.getProductSpecCharacteristicValue().add(specCharacteristicValue);
			}
		}
		productSpecification.getProductSpecCharacteristic().add(productSpecCharacteristic);
    	}catch(Exception e){}
		
		return productSpecification;
    }
    public static List<ProductSpecification> parseFromOfferTemplates(List<OfferTemplate> offers,UriInfo uriInfo){
    	if(offers!=null){
    		List<ProductSpecification> productSpecifications=new ArrayList<ProductSpecification>();
    		for(OfferTemplate offer:offers){
    			productSpecifications.add(parseFromOfferTemplate(offer,uriInfo));
    		}
    		return productSpecifications;
    	}
    	return null;
    }

}
