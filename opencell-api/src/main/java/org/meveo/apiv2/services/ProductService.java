package org.meveo.apiv2.services;

import org.apache.commons.codec.binary.Base64;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ImageUploadEventHandler;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.Channel;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.ChannelService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.meveo.service.catalog.impl.ProductTemplateService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

public class ProductService implements ApiService<ProductTemplate> {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    @Inject
    protected SellerService sellerService;
    @Inject
    protected ChannelService channelService;
    @Inject
    private OfferTemplateCategoryService offerTemplateCategoryService;
    @Inject
    private ProductTemplateService productTemplateService;
    private List<String> fetchFields;

    @PostConstruct
    public void initService(){
        fetchFields = Arrays.asList("offerTemplateCategories", "channels", "productChargeTemplates" ,"businessProductModel", "invoicingCalendar", "walletTemplates","attachments", "customerCategories");
    }

    @Override
    public Optional<ProductTemplate> findById(Long id) {
        return Optional.ofNullable(productTemplateService.findById(id, fetchFields));
    }

    @Override
    public List<ProductTemplate> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, fetchFields, null, null);
        return productTemplateService.list(paginationConfiguration);
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, fetchFields, null, null);
        return productTemplateService.count(paginationConfiguration);
    }

    @Override
    public ProductTemplate create(ProductTemplate productTemplate) {
        try {
            populateProductTemplateFields(productTemplate);

            productTemplateService.create(productTemplate);
        }catch (BusinessException e){
            throw new BadRequestException(e.getCause());
        }
        return productTemplate;
    }

    @Override
    public Optional<ProductTemplate> update(Long id, ProductTemplate productTemplate) {
        Optional<ProductTemplate> productTemplateOptional = findById(id);
        if(productTemplateOptional.isPresent()){
            if (StringUtils.isBlank(productTemplate.getCode()) || StringUtils.isBlank(productTemplate.getName())) {
                throw new BadRequestException("code and name filed are mandatory");
            }
            try {
                populateProductTemplateFields(productTemplate);
                ProductTemplate productTemplateToUpdate = productTemplateOptional.get();
                productTemplateToUpdate.setCode(productTemplate.getCode());
                productTemplateToUpdate.setName(productTemplate.getName());
                productTemplateToUpdate.setDescription(productTemplate.getDescription());
                productTemplateToUpdate.setLongDescription(productTemplate.getLongDescription());
                productTemplateToUpdate.setValidity(productTemplate.getValidity());
                productTemplateToUpdate.setLifeCycleStatus(productTemplate.getLifeCycleStatus());
                productTemplateToUpdate.setChannels(productTemplate.getChannels());
                productTemplateToUpdate.setOfferTemplateCategories(productTemplate.getOfferTemplateCategories());
                productTemplateService.update(productTemplateToUpdate);
            } catch (BusinessException e) {
                throw new BadRequestException(e.getCause());
            }
        }
        return productTemplateOptional;
    }

    @Override
    public Optional<ProductTemplate> patch(Long id, ProductTemplate productTemplate) {
        Optional<ProductTemplate> productTemplateOptional = findById(id);
        if(productTemplateOptional.isPresent()){
            try {
                populateProductTemplateFields(productTemplate);
                ProductTemplate productTemplateToUpdate = productTemplateOptional.get();
                if(!StringUtils.isBlank(productTemplate.getCode())){
                    productTemplateToUpdate.setCode(productTemplate.getCode());
                }
                if(!StringUtils.isBlank(productTemplate.getName())){
                    productTemplateToUpdate.setName(productTemplate.getName());
                }
                if(!StringUtils.isBlank(productTemplate.getDescription()) ) {
                    productTemplateToUpdate.setDescription(productTemplate.getDescription());
                }
                if(!StringUtils.isBlank(productTemplate.getLongDescription()) ) {
                    productTemplateToUpdate.setLongDescription(productTemplate.getLongDescription());
                }
                if(productTemplate.getValidity() != null ) {
                    productTemplateToUpdate.setValidity(productTemplate.getValidity());
                }
                if(productTemplate.getLifeCycleStatus() != null ) {
                    productTemplateToUpdate.setLifeCycleStatus(productTemplate.getLifeCycleStatus());
                }
                if(productTemplate.getChannels() != null ) {
                    productTemplateToUpdate.setChannels(productTemplate.getChannels());
                }
                if(productTemplate.getOfferTemplateCategories() != null ) {
                    productTemplateToUpdate.setOfferTemplateCategories(productTemplate.getOfferTemplateCategories());
                }
                productTemplateService.update(productTemplateToUpdate);
            } catch (BusinessException e) {
                throw new BadRequestException(e.getCause());
            }
        }
        return productTemplateOptional;
    }

    @Override
    public Optional<ProductTemplate> delete(Long id) {
        Optional<ProductTemplate> productTemplateOptional = findById(id);
        if(productTemplateOptional.isPresent()){
            try {
                productTemplateService.remove(id);
            } catch (BusinessException e) {
                throw new BadRequestException(e.getCause());
            }
        }
        return productTemplateOptional;
    }

    private void populateProductTemplateFields(ProductTemplate productTemplate) {
        List<Channel> channels = new ArrayList<>(productTemplate.getChannels() != null ? productTemplate.getChannels() : Collections.emptyList());
        for(Channel channelWithId : channels) {
            Channel channel = channelService.findById(channelWithId.getId());
            if (channel == null) {
                throw new BadRequestException("no channel found with id " + channelWithId.getId());
            }
            productTemplate.getChannels().remove(channelWithId);
            productTemplate.addChannel(channel);
        }

        List<OfferTemplateCategory> offerTemplateCategoryTemps = new ArrayList<>(
                productTemplate.getOfferTemplateCategories() != null ? productTemplate.getOfferTemplateCategories() : Collections.emptyList());
        for(OfferTemplateCategory offerTemplateCategoryWithId :offerTemplateCategoryTemps) {
            OfferTemplateCategory offerTemplateCategory = offerTemplateCategoryService.findById(offerTemplateCategoryWithId.getId());
            if (offerTemplateCategory == null) {
                throw new BadRequestException("no offerTemplateCategory found with id " + offerTemplateCategoryWithId.getId());
            }
            productTemplate.getOfferTemplateCategories().remove(offerTemplateCategoryWithId);
            productTemplate.addOfferTemplateCategory(offerTemplateCategory);
        }
    }
    // TODO : goes out of here
    public void saveImage(ProductTemplate productTemplate, String imageUrl, String image64) {
        try {
            ImageUploadEventHandler<IEntity> imageUploadEventHandler = new ImageUploadEventHandler<>(currentUser.getProviderCode());
            String filename = imageUploadEventHandler.saveImage(productTemplate, imageUrl, Base64.decodeBase64(image64));
            if (filename != null) {
                ((IImageUpload) productTemplate).setImagePath(filename);
            }
        } catch (AccessDeniedException e1) {
            throw new NotAuthorizedException("Failed saving image. Access is denied: " + e1.getMessage());
        } catch (IOException e) {
            throw new BadRequestException("Failed saving image. " + e.getMessage());
        }
    }
}
