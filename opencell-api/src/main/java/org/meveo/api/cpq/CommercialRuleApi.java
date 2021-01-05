package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialRuleDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialRulesResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.GroupedAttributeService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.primefaces.model.SortOrder;


/**
 * @author Mbarek-Ay
 * @version 11.0
 **/

@Stateless
public class CommercialRuleApi extends BaseCrudApi<CommercialRuleHeader, CommercialRuleHeaderDTO> { 
	
	@Inject
	private OfferTemplateService offerTemplateService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private AttributeService attributeService;
	
	@Inject
	private TagService tagService;
	
	@Inject
	private GroupedAttributeService groupedAttributeService; 
	
	@Inject
	private CommercialRuleHeaderService commercialRuleHeaderService;
	
	@Inject
	private ProductVersionService productVersionService;
	
	private static final String DEFAULT_SORT_ORDER_ID = "id";
	
	 
	 
	@Override
	public CommercialRuleHeader create(CommercialRuleHeaderDTO dto){ 

		if(StringUtils.isBlank(dto.getCode())) {
			missingParameters.add("code");
		}
		if(StringUtils.isBlank(dto.getRuleType())) {
			missingParameters.add("ruleType");
		} 
		handleMissingParameters();

		if (commercialRuleHeaderService.findByCode(dto.getCode()) != null) {
			throw new EntityAlreadyExistsException(CommercialRuleHeader.class, dto.getCode());
		} 
		CommercialRuleHeader commercialRuleHeader=new CommercialRuleHeader();
		commercialRuleHeader.setCode(dto.getCode());
		populateCommercialRuleHeader(dto, commercialRuleHeader); 
		commercialRuleHeaderService.create(commercialRuleHeader);
		return commercialRuleHeader;
	}
	
	@Override
	public CommercialRuleHeader update(CommercialRuleHeaderDTO dto){ 
		if(dto.getCode()== null) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		CommercialRuleHeader commercialRuleHeader = commercialRuleHeaderService.findByCode(dto.getCode());
		if(commercialRuleHeader==null) {
			throw new EntityDoesNotExistsException(CommercialRuleHeader.class, dto.getCode());
		} 
		populateCommercialRuleHeader(dto, commercialRuleHeader);
		commercialRuleHeaderService.update(commercialRuleHeader);
		return commercialRuleHeader;
	}
	
	@Override
	public void remove(String code) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		CommercialRuleHeader commercialRuleHeader = commercialRuleHeaderService.findByCode(code);
		if (commercialRuleHeader == null) {
			throw new EntityDoesNotExistsException(CommercialRuleHeader.class, code);
		}
		commercialRuleHeaderService.remove(commercialRuleHeader);
	}
	
	@Override
	public CommercialRuleHeader createOrUpdate(CommercialRuleHeaderDTO postData) throws MeveoApiException, BusinessException {
		CommercialRuleHeader commercialRuleHeader = commercialRuleHeaderService.findByCode(postData.getCode());
		if (commercialRuleHeader == null) {
			commercialRuleHeader = create(postData);
		} else {
			commercialRuleHeader = update(postData);
		}
		return commercialRuleHeader;
	}
	
	
	public GetCommercialRuleDtoResponse findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		CommercialRuleHeader commercialRuleHeader = commercialRuleHeaderService.findByCode(code);
		if (commercialRuleHeader == null) {
			throw new EntityDoesNotExistsException(CommercialRuleHeader.class, code);
		}  
		GetCommercialRuleDtoResponse result = new GetCommercialRuleDtoResponse(commercialRuleHeader); 
		return result;
	}
	
	
	
	
	public void populateCommercialRuleHeader(CommercialRuleHeaderDTO dto,CommercialRuleHeader commercialRuleHeader) {

		commercialRuleHeader.setDescription(dto.getDescription()); 
		commercialRuleHeader.setRuleType(dto.getRuleType());
		commercialRuleHeader.setRuleEl(dto.getRuleEl()); 

		if(!StringUtils.isBlank(dto.getOfferCode())) {
			OfferTemplate offerTemplate =offerTemplateService.findByCode(dto.getOfferCode());
			if(offerTemplate==null) {
				throw new EntityDoesNotExistsException(OfferTemplate.class, dto.getOfferCode());
			}
			commercialRuleHeader.setTargetOfferTemplate(offerTemplate);
		}
		if(!StringUtils.isBlank(dto.getProductCode())) {
			Product product =productService.findByCode(dto.getProductCode());
			if(product==null) {
				throw new EntityDoesNotExistsException(Product.class, dto.getProductCode());
			}
			commercialRuleHeader.setTargetProduct(product);
		}
		if(!StringUtils.isBlank(dto.getProductVersion()) && !StringUtils.isBlank(dto.getProductCode())) {
			ProductVersion productVersion =productVersionService.findByProductAndVersion(dto.getProductCode(), dto.getProductVersion());
			if(productVersion==null) {
				throw new EntityDoesNotExistsException(ProductVersion.class, dto.getProductCode()+" and version "+dto.getProductVersion());
			}
			commercialRuleHeader.setTargetProductVersion(productVersion);
		}

		if(!StringUtils.isBlank(dto.getAttributeCode())) {
			Attribute attribute =attributeService.findByCode(dto.getAttributeCode());
			if(attribute==null) {
				throw new EntityDoesNotExistsException(Attribute.class, dto.getAttributeCode());
			}
			commercialRuleHeader.setTargetAttribute(attribute);
		}
		if(!StringUtils.isBlank(dto.getTagCode())) {
			Tag tag =tagService.findByCode(dto.getTagCode());
			if(tag==null) {
				throw new EntityDoesNotExistsException(Tag.class, dto.getTagCode());
			}
			commercialRuleHeader.setTargetTag(tag);
		}

		if(!StringUtils.isBlank(dto.getGroupedAttributeCode())) {
			GroupedAttributes groupedAttributes =groupedAttributeService.findByCode(dto.getGroupedAttributeCode());
			if(groupedAttributes==null) {
				throw new EntityDoesNotExistsException(GroupedAttributes.class, dto.getGroupedAttributeCode());
			}
			commercialRuleHeader.setTargetGroupedAttributes(groupedAttributes);
		}
	}
	
	
	public GetListCommercialRulesResponseDto list (PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}
		String sortBy = DEFAULT_SORT_ORDER_ID;
		if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
			sortBy = pagingAndFiltering.getSortBy();
		}
		PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, null, pagingAndFiltering, CommercialRuleHeader.class);
		Long totalCount = commercialRuleHeaderService.count(paginationConfiguration);
		GetListCommercialRulesResponseDto result = new GetListCommercialRulesResponseDto();
		result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		if(totalCount > 0) {
			commercialRuleHeaderService.list(paginationConfiguration).stream().forEach(p -> {
				result.getCommercialRules().add(new CommercialRuleHeaderDTO(p));
			});
		}
		return result;
	}
	
 
		
		
 

}
