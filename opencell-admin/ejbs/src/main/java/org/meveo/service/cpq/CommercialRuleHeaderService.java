package org.meveo.service.cpq;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.ProductContextDTO;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay.
 * @version 10.0
 * 
 * Commercial Rule Header type service implementation.
 */

@Stateless
public class CommercialRuleHeaderService extends BusinessService<CommercialRuleHeader> {
	
	@Inject
	TagService tagService;
	
	@Inject
	AttributeService attributeService;
	
	@Inject
	ProductService productService;
	
	@Inject
	ProductVersionService productVersionService;
	
	@Inject
	OfferTemplateService offerTemplateService;
	
	@Inject
	GroupedAttributeService groupedAttributeService;
	
	@SuppressWarnings("unchecked")
	public List<CommercialRuleHeader> getTagRules(String tagCode) throws BusinessException{
		Tag tag=tagService.findByCode(tagCode);
		if(tag == null) { 
			throw new EntityDoesNotExistsException(Tag.class,tagCode);
		}
		Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getTagRules")
				.setParameter("tagCode", tagCode);
		List<CommercialRuleHeader> commercialRules=(List<CommercialRuleHeader>)query.getResultList();
		return commercialRules;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<CommercialRuleHeader> getOfferRules(String offerCode) throws BusinessException{
		OfferTemplate offer=offerTemplateService.findByCode(offerCode);
		if(offer == null) { 
			throw new EntityDoesNotExistsException(OfferTemplate.class,offerCode);
		}
		Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getOfferRules")
				.setParameter("offerCode", offerCode);
		List<CommercialRuleHeader> commercialRules=(List<CommercialRuleHeader>)query.getResultList();
		return commercialRules;
	}
	
	@SuppressWarnings("unchecked")
	public List<CommercialRuleHeader> getProductAttributeRules(String attributeCode,String productCode) throws BusinessException{
		Attribute attribute=attributeService.findByCode(attributeCode);
		if(attribute == null) { 
			throw new EntityDoesNotExistsException(Attribute.class,attributeCode);
		}
		Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getAttributeRules")
				.setParameter("attributeCode", attributeCode).setParameter("productCode", productCode);
		List<CommercialRuleHeader> commercialRules=(List<CommercialRuleHeader>)query.getResultList();
		return commercialRules;
	}
	
	@SuppressWarnings("unchecked")
	public List<CommercialRuleHeader> getGroupedAttributesRules(String groupedAttributeCode,String productCode) throws BusinessException{
		GroupedAttributes groupedAttribute=groupedAttributeService.findByCode(groupedAttributeCode);
		if(groupedAttribute == null) { 
			throw new EntityDoesNotExistsException(GroupedAttributes.class,groupedAttributeCode);
		}
		Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getGroupedAttributeRules")
				.setParameter("groupedAttributeCode", groupedAttributeCode).setParameter("productCode", productCode);
		List<CommercialRuleHeader> commercialRules=(List<CommercialRuleHeader>)query.getResultList();
		return commercialRules;
	}
	
	@SuppressWarnings("unchecked")
	public List<CommercialRuleHeader> getOfferAttributeRules(String attributeCode,String offerCode) throws BusinessException{
		Attribute attribute=attributeService.findByCode(attributeCode);
		if(attribute == null) { 
			throw new EntityDoesNotExistsException(Attribute.class,attributeCode);
		}
		Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getOfferAttributeRules")
				.setParameter("attributeCode", attributeCode).setParameter("offerTemplateCode", offerCode);
		List<CommercialRuleHeader> commercialRules=(List<CommercialRuleHeader>)query.getResultList();
		return commercialRules;
	}
	
	@SuppressWarnings("unchecked")
	public List<CommercialRuleHeader> getProductRules(String offerCode,String productCode,int currentVersion) throws BusinessException{
		OfferTemplate offer=offerTemplateService.findByCode(offerCode);
		if(offer == null) { 
			throw new EntityDoesNotExistsException(OfferTemplate.class,offerCode);
		}
		Product product=productService.findByCode(productCode);
		if(product == null) { 
			throw new EntityDoesNotExistsException(Product.class,productCode);
		};
		ProductVersion productVersion=productVersionService.findByProductAndVersion(productCode, currentVersion);
		if(productVersion==null) {
			throw new EntityDoesNotExistsException(ProductVersion.class, productCode+" and version "+currentVersion);
		}
		Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getProductRules")
				.setParameter("offerCode", offerCode).setParameter("productCode", productCode).setParameter("currentVersion", currentVersion);
		List<CommercialRuleHeader> commercialRules=(List<CommercialRuleHeader>)query.getResultList();
		return commercialRules;
	}

	public boolean isElementSelectable(String offerCode, List<CommercialRuleHeader> commercialRules,
			List<ProductContextDTO> selectedProducts) {
		Boolean isSelectable = Boolean.TRUE;
		List<CommercialRuleItem> items = null;
		boolean continueProcess = false;
		for (CommercialRuleHeader commercialRule : commercialRules) {
			if (RuleTypeEnum.REPLACEMENT.equals(commercialRule.getRuleType())) {
				replacementProcess(commercialRule, selectedProducts);
			}
			boolean isPreRequisite = RuleTypeEnum.PRE_REQUISITE.equals(commercialRule.getRuleType());
			items = commercialRule.getCommercialRuleItems();
			for (CommercialRuleItem item : items) {
				Iterator<CommercialRuleLine> lineIterator = item.getCommercialRuleLines().iterator();
				while (lineIterator.hasNext()) {
					CommercialRuleLine line = lineIterator.next();
					continueProcess = checkOperator(item.getOperator(), !lineIterator.hasNext(), isPreRequisite,
							isSelectable);
					if ((isPreRequisite && line.getSourceOfferTemplate() != null
							&& !line.getSourceOfferTemplate().getCode().equals(offerCode))
							|| (!isPreRequisite && line.getSourceOfferTemplate() != null 
									&& line.getSourceOfferTemplate().getCode().equals(offerCode) && line.getSourceProduct()==null)) {
						if (continueProcess) {
							continue;
						} else {
							return false;
						}

					}
					if (line.getSourceProduct() != null) {
						String sourceProductCode = line.getSourceProduct().getCode();
						ProductContextDTO productContext = selectedProducts.stream()
								.filter(pdtCtx -> sourceProductCode.equals(pdtCtx.getProductCode())).findAny()
								.orElse(null);

						if ((isPreRequisite && productContext == null)
								|| (!isPreRequisite && productContext != null && line.getSourceAttribute()==null)) {
							if (continueProcess) {
								continue;
							} else {
								return false;
							}
						}
						if (line.getSourceAttribute() != null) {
							LinkedHashMap<String, Object> selectedAttributes = productContext.getSelectedAttributes();
							for (Entry<String, Object> entry : selectedAttributes.entrySet()) {
								String attributeCode = entry.getKey();
								Object attributeValue = entry.getValue();
								String convertedValue = String.valueOf(attributeValue); 
								if (attributeCode.equals(line.getSourceAttribute().getCode())) {
									switch (line.getSourceAttribute().getAttributeType()) {
									case LIST_MULTIPLE_TEXT:
									case LIST_MULTIPLE_NUMERIC:
										List<String> values = Arrays.asList(convertedValue.split(";"));
										if (!values.contains(line.getSourceAttributeValue())) {
											if (continueProcess) {
												continue;
											} else {
												return false;
											}
										}
									default:
										if ((isPreRequisite && !convertedValue.equals(line.getSourceAttributeValue()))
												|| !isPreRequisite && convertedValue.equals(line.getSourceAttributeValue())) {
											if (continueProcess) {
												continue;
											} else {
												return false;
											}
										}

									}
								}
							}
						}
						if (line.getSourceGroupedAttributes() != null) {
							LinkedHashMap<String, Object> selectedGroupedAttributes = productContext.getSelectedGroupedAttributes();
							for (Entry<String, Object> entry : selectedGroupedAttributes.entrySet()) {
								String groupedAttributeCode = entry.getKey();
								Object groupedAttributeValue = entry.getValue();
								String convertedValue = String.valueOf(groupedAttributeValue); 
								if (groupedAttributeCode.equals(line.getSourceGroupedAttributes().getCode())) {
									List<String> values = Arrays.asList(convertedValue.split(";"));
									if ((isPreRequisite && !values.contains(line.getSourceGroupedAttributeValue()))
											|| !isPreRequisite && values.contains(line.getSourceGroupedAttributeValue())) {
										if (continueProcess) {
											continue;
										} else {
											return false;
										}
									} 	
								}
							}
						}

					}
				}
			}

		}
		return true;
	}
	
	public void replacementProcess(CommercialRuleHeader commercialRule,List<ProductContextDTO> selectedProducts) {
		List<CommercialRuleItem> items = null;
		List<CommercialRuleLine> lines = null;
		CommercialRuleItem item = null;
		CommercialRuleLine line =null; 
		
	
			items = commercialRule.getCommercialRuleItems();
			if(!items.isEmpty() ) {
				if(items.size()>1) {
					log.warn("the replacement commercial rule "+commercialRule.getCode()+" has more than one item");
				}
				item=items.get(0);
				lines=item.getCommercialRuleLines();
				if(!lines.isEmpty() ) {
					if(lines.size()>1) {
						log.warn("the replacement commercial rule "+commercialRule.getCode()+" has more than one source line");
					}
					line=lines.get(0);
					if (line.getSourceProduct() != null) {
						String sourceProductCode = line.getSourceProduct().getCode();
						ProductContextDTO productContext = selectedProducts.stream()
								.filter(pdtCtx -> sourceProductCode.equals(pdtCtx.getProductCode())).findAny()
								.orElse(null);

						if (productContext!=null && line.getSourceAttribute() != null) {
							String productCode=productContext.getProductCode();
							LinkedHashMap<String, Object> selectedAttributes = productContext.getSelectedAttributes();
							for (Entry<String, Object> entry : selectedAttributes.entrySet()) {
								String attributeCode = entry.getKey();
								Object attributeValue = entry.getValue();
								if (attributeCode.equals(line.getSourceAttribute().getCode())) {
								
									String fieldName=null;
									switch (line.getSourceAttribute().getAttributeType()) {
									case TOTAL :
									case COUNT :
									case NUMERIC :
									case INTEGER:
										fieldName="doubleValue";
										break;
									case LIST_MULTIPLE_TEXT:
									case LIST_TEXT:
									case TEXT:	
										fieldName="stringValue";
										break;
									case DATE:
										fieldName="dateValue"; 
										break;
									}
									 Query attributeQuery = getEntityManager().createQuery("select a.id from " + QuoteAttribute.class.getName()+ " a where a.attribute.code=:attributeCode "
										 		+ " and quoteProduct.productVersion.product.code=:productCode");
									 attributeQuery.setParameter("attributeCode", attributeCode).setParameter("productCode", productCode);
									List<Long> resultList = (List<Long>)attributeQuery.getResultList(); 
									if(!resultList.isEmpty()) {
									 for(Long id :resultList) {
									 Query quoteQuery = getEntityManager().createQuery("update " + QuoteAttribute.class.getName() + " SET "+ fieldName +"=:attributeValue where id=:id");
										 quoteQuery.setParameter("attributeValue", attributeValue).setParameter("id", id);
										 quoteQuery.executeUpdate();
									 }
									}
									
								}
							}
						}

					
				}	
				
				}
			}
	}

	private boolean checkOperator(OperatorEnum operator, boolean isLastLine, boolean isPreRequisite,Boolean isSelectable) {
		if(isPreRequisite){
			if( OperatorEnum.AND.equals(operator)) {
				return false;
			}else {
				if(isLastLine && !isSelectable) {
					return false;
				}else {
					isSelectable=false;
					return true;
				}
				
			}
			//incompatibility
		}else {
			if( OperatorEnum.OR.equals(operator)) {
				return false;
			}else {
				if(isLastLine && !isSelectable) {
					return false;
				}else {
					isSelectable=false;
					return true;
				}
				
			}
		}
	
	}
	
	

}