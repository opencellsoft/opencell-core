package org.meveo.service.cpq;

import static java.util.Collections.singletonList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
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
import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.enums.ScopeTypeEnum;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
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

    @Inject
    QuoteAttributeService quoteAttributeService;

    @SuppressWarnings("unchecked")
    public List<CommercialRuleHeader> getTagRules(String tagCode) throws BusinessException {
        Tag tag = tagService.findByCode(tagCode);
        if (tag == null) {
            throw new EntityDoesNotExistsException(Tag.class, tagCode);
        }
        Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getTagRules")
                .setParameter("tagCode", tagCode);
        List<CommercialRuleHeader> commercialRules = (List<CommercialRuleHeader>) query.getResultList();
        return commercialRules;
    }


    @SuppressWarnings("unchecked")
    public List<CommercialRuleHeader> getOfferRules(String offerCode) throws BusinessException {
        OfferTemplate offer = offerTemplateService.findByCodeIgnoringValidityDate(offerCode);
        if (offer == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, offerCode);
        }
        Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getOfferRules")
                .setParameter("offerCode", offerCode);
        List<CommercialRuleHeader> commercialRules = (List<CommercialRuleHeader>) query.getResultList();
        return commercialRules;
    }

    @SuppressWarnings("unchecked")
    public List<CommercialRuleHeader> getProductAttributeRules(String attributeCode, String productCode) throws BusinessException {
        Attribute attribute = attributeService.findByCode(attributeCode);
        String queryName = "CommercialRuleHeader.getAttributeRules";
        if (attribute == null) {
            throw new EntityDoesNotExistsException(Attribute.class, attributeCode);
        }
        if (!StringUtils.isEmpty(productCode)) {
            queryName = "CommercialRuleHeader.getProductAttributeRules";
        }
        Query query = getEntityManager().createNamedQuery(queryName)
                .setParameter("attributeCode", attributeCode);
        if (!StringUtils.isEmpty(productCode)) {
            query.setParameter("productCode", productCode);
        }

        List<CommercialRuleHeader> commercialRules = (List<CommercialRuleHeader>) query.getResultList();
        return commercialRules;
    }

    @SuppressWarnings("unchecked")
    public List<CommercialRuleHeader> getGroupedAttributesRules(String groupedAttributeCode, String productCode) throws BusinessException {
        GroupedAttributes groupedAttribute = groupedAttributeService.findByCode(groupedAttributeCode);
        if (groupedAttribute == null) {
            throw new EntityDoesNotExistsException(GroupedAttributes.class, groupedAttributeCode);
        }
        Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getGroupedAttributeRules")
                .setParameter("groupedAttributeCode", groupedAttributeCode).setParameter("productCode", productCode);
        List<CommercialRuleHeader> commercialRules = (List<CommercialRuleHeader>) query.getResultList();
        return commercialRules;
    }

    @SuppressWarnings("unchecked")
    public List<CommercialRuleHeader> getOfferAttributeRules(String attributeCode, String offerCode) throws BusinessException {
        Attribute attribute = attributeService.findByCode(attributeCode);
        if (attribute == null) {
            throw new EntityDoesNotExistsException(Attribute.class, attributeCode);
        }
        Query query = getEntityManager().createNamedQuery("CommercialRuleHeader.getOfferAttributeRules")
                .setParameter("attributeCode", attributeCode).setParameter("offerTemplateCode", offerCode);
        List<CommercialRuleHeader> commercialRules = (List<CommercialRuleHeader>) query.getResultList();
        return commercialRules;
    }

    @SuppressWarnings("unchecked")
    public List<CommercialRuleHeader> getProductRules(String offerCode, String productCode, Integer currentVersion) throws BusinessException {
        String queryName = "CommercialRuleHeader.getProductRules";
        if (!StringUtils.isEmpty(offerCode)) {
            OfferTemplate offer = offerTemplateService.findByCode(offerCode);
            if (offer == null) {
                throw new EntityDoesNotExistsException(OfferTemplate.class, offerCode);
            }
            queryName = "CommercialRuleHeader.getProductRulesWithOffer";
        }

        Product product = productService.findByCode(productCode);
        if (product == null) {
            throw new EntityDoesNotExistsException(Product.class, productCode);
        }
        ;
        if (currentVersion != null) {
            ProductVersion productVersion = productVersionService.findByProductAndVersion(productCode, currentVersion);
            if (productVersion == null) {
                throw new EntityDoesNotExistsException(ProductVersion.class, productCode + " and version " + currentVersion);
            }
        }

        Query query = getEntityManager().createNamedQuery(queryName)
                .setParameter("productCode", productCode);
        if (!StringUtils.isEmpty(offerCode)) {
            query.setParameter("offerCode", offerCode);
        }
        List<CommercialRuleHeader> commercialRules = (List<CommercialRuleHeader>) query.getResultList();
        return commercialRules;
    }

    
    private Boolean checkOperator(OperatorEnum operator, boolean isLastLine, boolean isElementExists) {
        if (isLastLine) {
            return Boolean.FALSE;
        }
        if ((OperatorEnum.OR.equals(operator) && isElementExists) || (OperatorEnum.AND.equals(operator) && !isElementExists)) { 
        		return Boolean.FALSE;
        	} 
        
        return Boolean.TRUE;
    }
    
    public boolean isElementSelectable(String offerCode, List<CommercialRuleHeader> commercialRules,List<ProductContextDTO> selectedProducts,LinkedHashMap<String, Object> selectedOfferAttributes, Predicate<CommercialRuleHeader> commercialRuleHeaderFilter) {
        Boolean isSelectable = Boolean.FALSE;
        commercialRules = commercialRules.stream()
                .filter(rule -> !RuleTypeEnum.REPLACEMENT.equals(rule.getRuleType()))
                .filter(rule -> !rule.isDisabled())
                .filter(commercialRuleHeaderFilter)
                .collect(Collectors.toList());
        List<CommercialRuleItem> items = null; 
        boolean isSelectedAttribute=true;
        MutableBoolean continueProcess=new MutableBoolean(false);
        boolean isLastLine=false;
        CommercialRuleLine line=null;
        for (CommercialRuleHeader commercialRule : commercialRules) {
        	if(!isSelectedAttribute) {
        		return false;
        	}
            boolean isPreRequisite = RuleTypeEnum.PRE_REQUISITE.equals(commercialRule.getRuleType());
            items = commercialRule.getCommercialRuleItems();
            for (CommercialRuleItem item : items) { 
                Iterator<CommercialRuleLine> lineIterator = item.getCommercialRuleLines().iterator();
                while (lineIterator.hasNext()) {
                    line = lineIterator.next();
                    isLastLine=!lineIterator.hasNext();
                    continueProcess.setValue(checkOperator(item.getOperator(), isLastLine, true));
                    if ((line.getSourceOfferTemplate() != null
                            && !line.getSourceOfferTemplate().getCode().equals(offerCode))
                            || (line.getSourceOfferTemplate() != null
                            && line.getSourceOfferTemplate().getCode().equals(offerCode) && line.getSourceProduct() == null && line.getSourceAttribute()==null)) {
                        if (continueProcess.isTrue()) {
                            continue;
                        } else {
                        	isSelectedAttribute=true;
                               break;
                        }

                    }
                    if(line.getSourceProduct() == null && line.getSourceOfferTemplate()!=null) {
                        isSelectedAttribute=isSelectedAttribute(selectedOfferAttributes,line,continueProcess, isPreRequisite,offerCode,isLastLine);
                    	if (continueProcess.isTrue()) {
                    		continue;
                    	} else {
                    		break;
                    	}
                    }
                    if (line.getSourceProduct() != null) {
                        String sourceProductCode = line.getSourceProduct().getCode();
                        ProductContextDTO productContext = selectedProducts.stream()
                                .filter(pdtCtx -> sourceProductCode.equals(pdtCtx.getProductCode())).findAny()
                                .orElse(null);

                        if ((isPreRequisite && productContext == null && !isSelectable)
                                || (!isPreRequisite && productContext != null && line.getSourceAttribute() == null)) {
                            if (checkOperator(item.getOperator(), !lineIterator.hasNext(), productContext != null)==Boolean.TRUE) {
                                continue;
                            } else {
                                return false;
                            }
                        }
                        if (isPreRequisite && productContext != null && OperatorEnum.OR.equals(item.getOperator())){
                        	isSelectable=true;
                        	
                        }
                        if (line.getSourceAttribute() != null && productContext!=null){ 
                        		 isSelectedAttribute= isSelectedAttribute(productContext.getSelectedAttributes(),line,continueProcess, isPreRequisite,offerCode,isLastLine) ; 
                        		 if (continueProcess.isTrue()) {
                                     continue;
                                 } else {
                                     break;
                                 }
                        }   
                        
                        if (line.getSourceGroupedAttributes() != null && productContext != null && productContext.getSelectedGroupedAttributes() != null) {
                            LinkedHashMap<String, Object> selectedGroupedAttributes = productContext.getSelectedGroupedAttributes();
                            for (Entry<String, Object> entry : selectedGroupedAttributes.entrySet()) {
                                String groupedAttributeCode = entry.getKey();
                                Object groupedAttributeValue = entry.getValue();
                                String convertedValue = String.valueOf(groupedAttributeValue);
                                if (groupedAttributeCode.equals(line.getSourceGroupedAttributes().getCode())) {
                                    List<String> values = Arrays.asList(convertedValue.split(";"));
                                    if ((isPreRequisite && !values.contains(line.getSourceGroupedAttributeValue()))
                                            || !isPreRequisite && values.contains(line.getSourceGroupedAttributeValue())) {
                                        if (continueProcess.isFalse()) {
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
        return isSelectedAttribute;
    }
    
    
    
    private boolean valueCompare(RuleOperatorEnum operator,String sourceAttributeValue,Object convertedValue) {
    	if(convertedValue==null && StringUtils.isBlank(sourceAttributeValue)) {
    		return true;
    	}
    	if(sourceAttributeValue!=null &&  operator!=null) {
    		String convertedValueStr=convertedValue !=null?String.valueOf(convertedValue):null;
    		switch(operator) {
    		case EQUAL:
    			if(StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) &&  NumberUtils.isCreatable(sourceAttributeValue.trim())) {
    				 if(Double.valueOf(convertedValueStr).compareTo(Double.valueOf(sourceAttributeValue))==0) {
    					 return true;
    				 }
    			}
    			if (sourceAttributeValue.equals(convertedValueStr))
    				return true;
    			break;
    		case NOT_EQUAL:
    			if(StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
   				 if(Double.valueOf(convertedValueStr).compareTo(Double.valueOf(sourceAttributeValue))!=0) {
   					 return true;
   				 }
   				 break;
   			    }
    			if (!sourceAttributeValue.equals(convertedValueStr))
    				return true;
    			break;
    		case LESS_THAN:
    			if(StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
    			if (Double.valueOf(convertedValueStr)<Double.valueOf(sourceAttributeValue))
    				return true;
    			}
    			break;
    		case LESS_THAN_OR_EQUAL:
    			if(StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
    			if (Double.valueOf(convertedValueStr)<=Double.valueOf(sourceAttributeValue))
    				return true;
    			}
    			break;
    		case GREATER_THAN:
    			if(StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
    			if (Double.valueOf(convertedValueStr)>Double.valueOf(sourceAttributeValue))
    				return true;	
    			}
    			break;

    		case GREATER_THAN_OR_EQUAL:
    			if(StringUtils.isNotBlank(convertedValueStr) && NumberUtils.isCreatable(convertedValueStr.trim()) && NumberUtils.isCreatable(sourceAttributeValue.trim())) {
    			if (Double.valueOf(convertedValueStr)>=Double.valueOf(sourceAttributeValue))
    				return true;	
    			}
    		}
    	}
    	return false;
    }
    
    private  boolean  isSelectedAttribute(LinkedHashMap<String, Object> selectedAttributes, CommercialRuleLine line, MutableBoolean continueProcess, boolean isPreRequisite,String offerCode,boolean isLastLine) {
    	boolean isSelected=!isPreRequisite;
    	if(line.getSourceAttribute()==null) {
    		return true;
    	}
    	if(selectedAttributes!=null) {
    		for (Entry<String, Object> entry : selectedAttributes.entrySet()) {
    			String attributeCode = entry.getKey();
    			Object attributeValue = entry.getValue();
    			if (attributeCode.equals(line.getSourceAttribute().getCode())) {
    				switch (line.getSourceAttribute().getAttributeType()) {
    				case LIST_MULTIPLE_TEXT:
    				case LIST_MULTIPLE_NUMERIC:
    					List<String> values = attributeValue!=null?Arrays.asList(String.valueOf(attributeValue).split(";")):new ArrayList<String>();
    					if ((isPreRequisite && !values.contains(line.getSourceAttributeValue()))
    							|| !isPreRequisite && values.contains(line.getSourceAttributeValue())) {
    						continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, values.contains(line.getSourceAttributeValue())));
    							return false;
    						}else if (isPreRequisite && values.contains(line.getSourceAttributeValue())){
    							continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, true));
    							return true;
    						}
    					break;
    				case EXPRESSION_LANGUAGE:
    					OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode);
    					String result =attributeValue!=null? attributeService.evaluateElExpressionAttribute(String.valueOf(attributeValue), null, offerTemplate, null, String.class):null;
    					if(result!=null) {
    					boolean resultCompareEl=valueCompare(line.getOperator(), line.getSourceAttributeValue(), result);
    					if (isPreRequisite && !resultCompareEl || !isPreRequisite && resultCompareEl) {
    						continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, resultCompareEl));
    							return false;
    						}else if (isPreRequisite && resultCompareEl){
    							continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, true));
    							return true;
    						}
    					}
    					break;
    				default:
    					boolean resultCompare=valueCompare(line.getOperator(), line.getSourceAttributeValue(), attributeValue);
    					if (isPreRequisite && !resultCompare || !isPreRequisite && resultCompare) {
    						continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, resultCompare));
    						return false;
    					}else if (isPreRequisite && resultCompare){
    						continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, true));
    						return true;
    					}
    					

    				}
    			}else {
    				continueProcess.setValue(checkOperator(line.getCommercialRuleItem().getOperator(), isLastLine, false));
    			}
    		}
    	}else if(isPreRequisite && line.getSourceAttribute()!=null) {
    		return false;
    	}
    	return isSelected;
    }


    public void processProductReplacementRule(QuoteProduct quoteProduct) {
        QuoteVersion quoteVersion = quoteProduct.getQuoteVersion();
        List<CommercialRuleHeader> productRules = quoteProduct.getProductVersion().getProduct().getCommercialRuleHeader()
                .stream()
                .filter(commercialRuleHeader -> !commercialRuleHeader.isDisabled())
                .filter(commercialRuleHeader -> RuleTypeEnum.REPLACEMENT.equals(commercialRuleHeader.getRuleType()))
                .collect(Collectors.toList());
        productRules.stream()
                .forEach(
                        commercialRuleHeader -> {
                            log.info("about to apply replacement rule: " + commercialRuleHeader.getCode());
                            Optional<QuoteAttribute> attributeToReplace = getQuoteAttributeToReplace(quoteProduct, commercialRuleHeader.getTargetAttribute().getCode());

                            if (attributeToReplace.isPresent()) {
                                List<CommercialRuleItem> commercialRuleItems = commercialRuleHeader.getCommercialRuleItems();
                                if (!commercialRuleItems.isEmpty()) {
                                    if (commercialRuleItems.size() > 1) {
                                        log.warn("the replacement commercial rule " + commercialRuleHeader.getCode() + " has more than one item");
                                    }
                                    CommercialRuleItem commercialRuleItem = commercialRuleItems.get(0);

                                    List<CommercialRuleLine> commercialRuleLines = commercialRuleItem.getCommercialRuleLines();
                                    if (!commercialRuleLines.isEmpty()) {
                                        if (commercialRuleLines.size() > 1) {
                                            log.warn("the replacement commercial rule " + commercialRuleHeader.getCode() + " has more than one source line");
                                        }

                                        List<QuoteOffer> quoteOffers = isOfferScope(commercialRuleHeader.getScopeType()) ? singletonList(quoteProduct.getQuoteOffer()) : quoteVersion.getQuoteOffers();
                                    
                                        Optional<QuoteAttribute> quoteAttributes=Optional.empty();
                                        for (CommercialRuleLine commercialLine : commercialRuleLines) {
                                        	quoteAttributes=processReplacement(quoteOffers, attributeToReplace.get(), commercialLine, commercialRuleHeader.getCode());
                                        	if(quoteAttributes.isPresent() && OperatorEnum.OR.equals(commercialRuleItem.getOperator())) {
                                        		break;
                                        	}else if(quoteAttributes.isEmpty() && OperatorEnum.AND.equals(commercialRuleItem.getOperator())) {
                                        		break;
                                        	}
                                        }
                                        if(quoteAttributes.isPresent()) {
                                        	if(!StringUtils.isBlank(commercialRuleHeader.getTargetAttributeValue())) {
                                        		overrideBySourceAttributeValue(attributeToReplace.get(), commercialRuleHeader.getTargetAttributeValue(), commercialRuleItem.getCommercialRuleHeader().getCode());
                                        	}
                                        	else {
                                        		updateQuoteAttribute(attributeToReplace.get(), quoteAttributes);
                                        	}
                                        }
                                    }

                                }
                            }
                        }
                );
    }
    
    private boolean isOfferScope(ScopeTypeEnum scopeType) {
        return scopeType == null || scopeType == ScopeTypeEnum.QUOTE_OFFER;
    }

    private Optional<QuoteAttribute> getQuoteAttributeToReplace(QuoteProduct quoteProduct, String attributeToReplaceCode) {
        return quoteProduct.getProductVersion().getAttributes()
                                        .stream()
                                        .filter(attribute -> attribute.getAttribute().getCode().equals(attributeToReplaceCode))
                                        .map(attribute -> {
                                            Optional<QuoteAttribute> matchedAttribute = quoteProduct.getQuoteAttributes()
                                                    .stream()
                                                    .filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(attribute.getAttribute().getCode()))
                                                    .findFirst();

                                            if (matchedAttribute.isPresent())
                                                return matchedAttribute.get();
                                            else {
                                                QuoteAttribute quoteAttribute = new QuoteAttribute();
                                                quoteAttribute.setAttribute(attribute.getAttribute());
                                                quoteAttribute.setQuoteProduct(quoteProduct);
                                                quoteAttributeService.create(quoteAttribute);
                                                quoteProduct.getQuoteAttributes().add(quoteAttribute);
                                                return quoteAttribute;
                                            }

                                        })
                                        .findAny();
    }

    private Optional<QuoteAttribute> processReplacement(List<QuoteOffer> quoteOffers, QuoteAttribute attributeToReplace, CommercialRuleLine commercialRuleLine, String commercialRuleHeaderCode) {
    	CommercialRuleHeader commercialRuleHeader = commercialRuleLine.getCommercialRuleItem().getCommercialRuleHeader();
    	Optional<QuoteAttribute> quoteAttributes=Optional.empty();
    	if (commercialRuleLine.getSourceOfferTemplate() != null && commercialRuleLine.getSourceProduct() == null) {
    		Optional<QuoteOffer> sourceOffer = quoteOffers.stream()
    				.filter(offer -> offer.getOfferTemplate().getId().equals(commercialRuleLine.getSourceOfferTemplate().getId()))
    				.findFirst();
    		if (sourceOffer.isPresent()) {
    			quoteAttributes = sourceOffer.get().getQuoteAttributes().stream()
    					.filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(commercialRuleLine.getSourceAttribute().getCode()) 
    							&& valueCompare(commercialRuleLine.getOperator(), commercialRuleLine.getSourceAttributeValue(),
    									quoteAttribute.getAttribute().getAttributeType().getValue(quoteAttribute)))
    					.findFirst();
    		
    		}
    	} else if (commercialRuleLine.getSourceOfferTemplate() != null && commercialRuleLine.getSourceProduct() != null){
    		Optional<QuoteOffer> sourceOffer = quoteOffers
    				.stream()
    				.filter(offer -> offer.getOfferTemplate().getId().equals(commercialRuleLine.getSourceOfferTemplate().getId()))
    				.findFirst();
    		if(sourceOffer.isPresent()){
    			Optional<QuoteProduct> quoteProduct=
    					sourceOffer.get().getQuoteProduct()
    					.stream()
    					.filter(product -> product.getProductVersion().getProduct().getId().equals(commercialRuleLine.getSourceProduct().getId()))
    					.findFirst();
    			if(quoteProduct.isPresent()) {
    				if(commercialRuleLine.getSourceAttribute()==null) {
    					if(!StringUtils.isBlank(commercialRuleHeader.getTargetAttributeValue())) {
    						overrideBySourceAttributeValue(attributeToReplace, commercialRuleHeader.getTargetAttributeValue(), commercialRuleHeaderCode);
    					} 
    				}else {
    					quoteAttributes= quoteProduct.get().getQuoteAttributes().stream()
    							.filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(commercialRuleLine.getSourceAttribute().getCode())&&
    									valueCompare(commercialRuleLine.getOperator(), commercialRuleLine.getSourceAttributeValue(),
    											quoteAttribute.getAttribute().getAttributeType().getValue(quoteAttribute)))
    							.findFirst();
    					  
    				}
    			}

    		} 

    	}
    	return quoteAttributes;
    }

    private void overrideBySourceAttributeValue(QuoteAttribute quoteAttributeToUpdate, String sourceAttributeValue, String commercialRuleCode) {
        switch (quoteAttributeToUpdate.getAttribute().getAttributeType()){
            case TOTAL:
            case COUNT:
            case NUMERIC:
            case INTEGER:
            case LIST_MULTIPLE_NUMERIC:
                try {
                    quoteAttributeToUpdate.setDoubleValue(Double.parseDouble(sourceAttributeValue));
                }catch (Exception exp){
                    log.error("can not parse attribute value to double: " + sourceAttributeValue);
                }
                quoteAttributeToUpdate.setStringValue(sourceAttributeValue);
                break;
            case LIST_MULTIPLE_TEXT:
            case LIST_TEXT:
            case EXPRESSION_LANGUAGE:
            case TEXT:
                quoteAttributeToUpdate.setStringValue(sourceAttributeValue);
                break;
            case DATE:
                try {
                    quoteAttributeToUpdate.setDateValue(new SimpleDateFormat("yyyy-MM-dd").parse(sourceAttributeValue));
                } catch (ParseException e) {
                    log.error("can not override quote value of type date, date parsing error: commercial rule: " + commercialRuleCode, e);
                }
                break;
        }
    }

    private void updateQuoteAttribute(QuoteAttribute attributeToReplace, Optional<QuoteAttribute> sourceOfferAttribute) {
        attributeToReplace.setStringValue(sourceOfferAttribute.get().getStringValue());
        attributeToReplace.setDoubleValue(sourceOfferAttribute.get().getDoubleValue());
        attributeToReplace.setDateValue(sourceOfferAttribute.get().getDateValue());
        quoteAttributeService.update(attributeToReplace);
    }

    public Map<String, Object> replacementProcess(CommercialRuleHeader commercialRule, List<ProductContextDTO> selectedProducts) {
        List<CommercialRuleItem> items = null;
        List<CommercialRuleLine> lines = null;
        CommercialRuleItem item = null;
        CommercialRuleLine line = null;


        items = commercialRule.getCommercialRuleItems();
        if (!items.isEmpty()) {
            if (items.size() > 1) {
                log.warn("the replacement commercial rule " + commercialRule.getCode() + " has more than one item");
            }
            item = items.get(0);
            lines = item.getCommercialRuleLines();
            if (!lines.isEmpty()) {
                if (lines.size() > 1) {
                    log.warn("the replacement commercial rule " + commercialRule.getCode() + " has more than one source line");
                }
                line = lines.get(0);
                OfferTemplate sourceOfferTemplate = line.getSourceOfferTemplate();
                Product sourceProduct = line.getSourceProduct();
                Attribute sourceAttribute = line.getSourceAttribute();
                return replaceProductAttribute(selectedProducts, sourceAttribute, line.getSourceAttributeValue(), sourceProduct.getCode());

            }
        } else {
            return replaceProductAttribute(selectedProducts, commercialRule.getTargetAttribute(), commercialRule.getTargetAttributeValue(), commercialRule.getTargetProduct().getCode());
        }
        return null;
    }

    private Map<String, Object> replaceProductAttribute(List<ProductContextDTO> selectedProducts, Attribute sourceAttribute, String sourceAttributeValue, String sourceCode) {
        Map<String, Object> overriddenAttributes = new HashMap<>();
        String sourceProductCode = sourceCode;
        ProductContextDTO productContext = selectedProducts.stream()
                .filter(pdtCtx -> sourceProductCode.equals(pdtCtx.getProductCode())).findAny()
                .orElse(null);

        if (productContext != null && productContext.getSelectedAttributes() != null && sourceAttribute != null) {
            String productCode = productContext.getProductCode();
            LinkedHashMap<String, Object> selectedAttributes = productContext.getSelectedAttributes();
            for (Entry<String, Object> entry : selectedAttributes.entrySet()) {
                String attributeCode = entry.getKey();
                if (attributeCode.equals(sourceAttribute.getCode())) {

                    String stringFieldName = null;
                    String doubleFieldName = null;
                    switch (sourceAttribute.getAttributeType()) {
                        case TOTAL:
                        case COUNT:
                        case NUMERIC:
                        case INTEGER:
                            doubleFieldName = "doubleValue";
                            stringFieldName = "stringValue";
                            break;
                        case LIST_MULTIPLE_TEXT:
                        case LIST_TEXT:
                        case EXPRESSION_LANGUAGE:
                        case TEXT:
                            stringFieldName = "stringValue";
                            break;
                        case DATE:
                            stringFieldName = "dateValue";
                            break;
                    }
                    Query attributeQuery = getEntityManager().createQuery("select a.id from " + QuoteAttribute.class.getName() + " a where a.attribute.code=:attributeCode "
                            + " and quoteProduct.productVersion.product.code=:productCode");
                    attributeQuery.setParameter("attributeCode", attributeCode).setParameter("productCode", productCode);
                    List<Long> resultList = (List<Long>) attributeQuery.getResultList();
                    if (!resultList.isEmpty()) {
                        for (Long id : resultList) {
                            if (doubleFieldName != null)
                                updateField(doubleFieldName, Double.parseDouble(sourceAttributeValue), id);
                            if (stringFieldName != null)
                                updateField(stringFieldName, sourceAttributeValue, id);
                        }
                    }
                    overriddenAttributes.put(attributeCode, sourceAttributeValue);

                }
            }


        }
        return overriddenAttributes;
    }

    private void updateField(String fieldName, Object sourceAttributeValue, Long id) {
        Query quoteQuery = getEntityManager().createQuery("update " + QuoteAttribute.class.getName() + " SET " + fieldName + "=:attributeValue where id=:id");
        quoteQuery.setParameter("attributeValue", sourceAttributeValue).setParameter("id", id);
        quoteQuery.executeUpdate();
    }

    


}