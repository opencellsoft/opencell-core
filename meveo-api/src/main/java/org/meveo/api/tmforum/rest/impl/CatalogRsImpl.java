package org.meveo.api.tmforum.rest.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.tmforum.rest.CatalogRs;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmf.dsmapi.catalog.resource.Attachment;
import org.tmf.dsmapi.catalog.resource.CatalogReference;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.resource.RelatedParty;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.BundledProductReference;
import org.tmf.dsmapi.catalog.resource.product.Channel;
import org.tmf.dsmapi.catalog.resource.product.Place;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductOfferingPrice;
import org.tmf.dsmapi.catalog.resource.product.ProductOfferingTerm;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecCharacteristic;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;
import org.tmf.dsmapi.catalog.resource.specification.CharacteristicValueType;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationCharacteristicRelationship;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationCharacteristicValue;
import org.tmf.dsmapi.catalog.resource.specification.SpecificationRelationship;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class CatalogRsImpl implements CatalogRs {

	@Inject
	private Logger log = LoggerFactory.getLogger(CatalogRsImpl.class);

	private static Map<String, ProductOffering> productOfferings = new HashMap<String, ProductOffering>();
	private static Map<String, ProductSpecification> productSpecifications = new HashMap<String, ProductSpecification>();

	@Context
	private UriInfo uriInfo;
	private static Category category = new Category();

	static {
		category.setId("1");
		category.setName("Default");
		category.setDescription("Default category");
		Calendar c = Calendar.getInstance();
		c.set(1970, 1, 1, 0, 0, 0);
		category.setLastUpdate(c.getTime());
		category.setLifecycleStatus(LifecycleStatus.ACTIVE);
		TimeRange timeRange = new TimeRange();
		timeRange.setStartDateTime(c.getTime());
		category.setValidFor(timeRange);
		category.setIsRoot(Boolean.TRUE);
		category.setParentId(null);
		ProductOffering p = new ProductOffering();
		p.setId("offer");
		productOfferings.put(p.getId(), p);
	}

	@Override
	public List<Category> findCategories() {
		log.info("find all categories...");
		List<Category> categories = new ArrayList<Category>();
		String url = uriInfo.getAbsolutePath().toString();
		category.setHref(String.format("%s/%s", url, category.getId()));
		categories.add(category);
		return categories;
	}

	@Override
	public Response findCategoryById(String id) {
		log.info("find catetegory by id {}", id);
		if (!category.getId().equals(id)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok().entity(category).build();
	}

	@Override
	public Response createProductOffering(OfferTemplate offer) {
		log.info("create productOffering ... ");
		ProductOffering productOffering = new ProductOffering();
		try {
			productOffering.setId(offer.getCode());
			productOffering.setVersion(String.format("%d.0", offer.getVersion() == null ? 0 : offer.getVersion()));
			productOffering.setHref(String.format("%s%s%s", uriInfo.getAbsolutePath(),
					"/catalogManager/productOffering/", offer.getCode()));
			productOffering.setName(offer.getCode());
			productOffering.setDescription(offer.getDescription());
			productOffering.setLastUpdate(offer.getAuditable() != null ? offer.getAuditable().getLastModified() : null);
			productOffering.setLifecycleStatus(offer.isActive() ? LifecycleStatus.ACTIVE : LifecycleStatus.OBSOLETE);
			productOffering.setValidFor(new TimeRange());
			productOffering.getValidFor()
					.setStartDateTime(offer.getAuditable() != null ? offer.getAuditable().getCreated() : null);
			if (!offer.isActive()) {
				productOffering.getValidFor()
						.setEndDateTime(offer.getAuditable() != null ? offer.getAuditable().getUpdated() : null);
			}

			productOffering.setIsBundle(Boolean.FALSE);

			productOffering.setCategory(new ArrayList<CatalogReference>());
			productOffering.getCategory().add(category.getCatalogReference());

			productOffering.setChannel(new ArrayList<Channel>()); // leave empty

			productOffering.setPlace(new ArrayList<Place>()); // leave empty

			productOffering.setBundledProductOffering(new ArrayList<BundledProductReference>());// leave
																								// empty

			productOffering.setServiceLevelAgreement(null);
			productOffering.setProductSpecification(null);// Product
															// Specification
															// created from the
															// offer (see below)
			productOffering.setServiceCandidate(null);
			productOffering.setResourceCandidate(null);

			productOffering.setProductOfferingTerm(new ArrayList<ProductOfferingTerm>());// leave
																							// empty

			productOffering.setProductOfferingPrice(new ArrayList<ProductOfferingPrice>());// leave
																							// empty
			productOfferings.put(productOffering.getId(), productOffering);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error when save a productOffer {} : {}", offer, e.getMessage());
		}
		return Response.ok().entity(productOffering).build();
	}

	@Override
	public Response findProductOfferingById(String id) {
		ProductOffering productOffering = productOfferings.get(id);
		log.info("find productOffer by id {} : {}", id, productOffering);
		if (productOffering == null) {
			return Response.status(Status.NOT_FOUND).entity("not found").build();
		}
		return Response.ok().entity(productOffering).build();
	}

	@Override
	public Response createProductSpecification(OfferTemplate offer) {
		ProductSpecification productSpecification = new ProductSpecification();
		try {
			productSpecification.setId(offer.getCode());
			productSpecification.setVersion(String.format("%d.0", offer.getVersion() == null ? 0 : offer.getVersion()));
			productSpecification.setHref(String.format("%s%s%s", uriInfo.getAbsolutePath().toString(),
					"/catalogManager/productSpecification/", offer.getCode()));
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

			if (offer.getServiceTemplates() != null)
				for (ServiceTemplate service : offer.getServiceTemplates()) {
					SpecificationCharacteristicValue specCharacteristicValue = new SpecificationCharacteristicValue();
					specCharacteristicValue.setValueType(CharacteristicValueType.STRING);
					specCharacteristicValue.setDefaultValue(Boolean.FALSE);
					specCharacteristicValue.setValue(service.getCode());
					specCharacteristicValue.setUnitOfMeasure("unit");
					specCharacteristicValue
							.setValueFrom(offer.getAuditable() != null ? "" + offer.getAuditable().getCreated() : null);
					specCharacteristicValue.setValueTo("");
					specCharacteristicValue.setValidFor(new TimeRange());
					specCharacteristicValue.getValidFor()
							.setStartDateTime(offer.getAuditable() != null ? offer.getAuditable().getCreated() : null);
					productSpecCharacteristic.getProductSpecCharacteristicValue().add(specCharacteristicValue);
				}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error when save a productSpecification {} : {}", offer, e.getMessage());
		}
		productSpecifications.put(productSpecification.getId(), productSpecification);
		return Response.ok().entity(productSpecification).build();
	}

	@Override
	public Response findProductSpecificationById(String id) {
		ProductSpecification productSpecification = productSpecifications.get(id);
		if (productSpecification == null) {
			return Response.status(Status.NOT_FOUND).entity("no found!").build();
		}
		return Response.ok().entity(productSpecification).build();
	}
}
