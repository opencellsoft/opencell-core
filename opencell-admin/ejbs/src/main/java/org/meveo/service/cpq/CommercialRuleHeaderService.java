package org.meveo.service.cpq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
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

import static java.util.Collections.singletonList;

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
        OfferTemplate offer = offerTemplateService.findByCode(offerCode);
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

    
    private boolean checkOperator(OperatorEnum operator, boolean isOnlyOneLine, boolean isLastLine, boolean isElementExists) {
        if (isOnlyOneLine) {
            return false;
        }
        if (!isElementExists && OperatorEnum.AND.equals(operator)) {
            return false;
        }
        if (OperatorEnum.OR.equals(operator)) {
            if(!isElementExists  && isLastLine ){
        		return false;
        	}
        	if(!isElementExists  && !isLastLine ){
        		return true;
        	}	
        } 
        return true;
    }
    
    public boolean isElementSelectable(String offerCode, List<CommercialRuleHeader> commercialRules,List<ProductContextDTO> selectedProducts,LinkedHashMap<String, Object> selectedOfferAttributes, Predicate<CommercialRuleHeader> commercialRuleHeaderFilter) {
        Boolean isSelectable = Boolean.FALSE;
        commercialRules = commercialRules.stream()
                .filter(rule -> !RuleTypeEnum.REPLACEMENT.equals(rule.getRuleType()))
                .filter(rule -> !rule.isDisabled())
                .filter(commercialRuleHeaderFilter)
                .collect(Collectors.toList());
        List<CommercialRuleItem> items = null;
        boolean continueProcess = false;
        for (CommercialRuleHeader commercialRule : commercialRules) {
            boolean isPreRequisite = RuleTypeEnum.PRE_REQUISITE.equals(commercialRule.getRuleType());
            items = commercialRule.getCommercialRuleItems();
            for (CommercialRuleItem item : items) {
                int linesCount = item.getCommercialRuleLines().size();
                Iterator<CommercialRuleLine> lineIterator = item.getCommercialRuleLines().iterator();
                while (lineIterator.hasNext()) {
                    CommercialRuleLine line = lineIterator.next();
                    continueProcess = checkOperator(item.getOperator(), linesCount == 1, !lineIterator.hasNext(), true);
                    if ((isPreRequisite && line.getSourceOfferTemplate() != null
                            && !line.getSourceOfferTemplate().getCode().equals(offerCode))
                            || (!isPreRequisite && line.getSourceOfferTemplate() != null
                            && line.getSourceOfferTemplate().getCode().equals(offerCode) && line.getSourceProduct() == null)) {
                        if (continueProcess) {
                            continue;
                        } else {
                            return false;
                        }

                    }
                    if(line.getSourceProduct() == null && line.getSourceOfferTemplate()!=null) {
                    return isSelectedAttribute(selectedOfferAttributes,line,continueProcess, isPreRequisite,offerCode);
                    }
                    if (line.getSourceProduct() != null) {
                        String sourceProductCode = line.getSourceProduct().getCode();
                        ProductContextDTO productContext = selectedProducts.stream()
                                .filter(pdtCtx -> sourceProductCode.equals(pdtCtx.getProductCode())).findAny()
                                .orElse(null);

                        if ((isPreRequisite && productContext == null && !isSelectable)
                                || (!isPreRequisite && productContext != null && line.getSourceAttribute() == null)) {
                            if (checkOperator(item.getOperator(), linesCount == 1, !lineIterator.hasNext(), productContext != null)) {
                                continue;
                            } else {
                                return false;
                            }
                        }
                        if (isPreRequisite && productContext != null && OperatorEnum.OR.equals(item.getOperator())){
                        	isSelectable=true;
                        	
                        }
                        if (line.getSourceAttribute() != null && productContext!=null){ 
                        		return isSelectedAttribute(productContext.getSelectedAttributes(),line,continueProcess, isPreRequisite,offerCode) ;
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
    
    
    private  boolean  isSelectedAttribute(LinkedHashMap<String, Object> selectedAttributes, CommercialRuleLine line, boolean continueProcess, boolean isPreRequisite,String offerCode) {
    	boolean isSelected=true;
    	if(line.getSourceAttribute()==null) {
    		return true;
    	}
    	if(selectedAttributes!=null) {
    		for (Entry<String, Object> entry : selectedAttributes.entrySet()) {
    			String attributeCode = entry.getKey();
    			Object attributeValue = entry.getValue();
    			String convertedValue = String.valueOf(attributeValue);
    			if (attributeCode.equals(line.getSourceAttribute().getCode())) {
    				isSelected=true;
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
    				case EXPRESSION_LANGUAGE:
    					OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode);
    					String result = attributeService.evaluateElExpressionAttribute(convertedValue, null, offerTemplate, null, String.class);
    					convertedValue = result;
    					if ((isPreRequisite && !result.equals(line.getSourceAttributeValue()))
    							|| !isPreRequisite && result.equals(line.getSourceAttributeValue())) {
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
                                        processReplacement(quoteOffers, attributeToReplace.get(), commercialRuleLines.get(0), commercialRuleHeader.getCode());
                                    } else if(commercialRuleHeader.getTargetAttributeValue() != null){
                                        overrideBySourceAttributeValue(attributeToReplace.get(), commercialRuleHeader.getTargetAttributeValue(), commercialRuleHeader.getCode());
                                    }

                                }else if(commercialRuleHeader.getTargetAttributeValue() != null){
                                    overrideBySourceAttributeValue(attributeToReplace.get(), commercialRuleHeader.getTargetAttributeValue(), commercialRuleHeader.getCode());
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

    private void processReplacement(List<QuoteOffer> quoteOffers, QuoteAttribute attributeToReplace, CommercialRuleLine commercialRuleLine, String commercialRuleHeaderCode) {
        if (commercialRuleLine.getSourceOfferTemplate() != null && commercialRuleLine.getSourceProduct() == null) {
            Optional<QuoteOffer> sourceOffer = quoteOffers.stream()
                    .filter(offer -> offer.getOfferTemplate().getId().equals(commercialRuleLine.getSourceOfferTemplate().getId()))
                    .findFirst();
            if (sourceOffer.isPresent()) {
                Optional<QuoteAttribute> sourceOfferAttribute = sourceOffer.get().getQuoteAttributes().stream()
                        .filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(commercialRuleLine.getSourceAttribute().getCode()))
                        .findFirst();
                if (sourceOfferAttribute.isPresent()) {
                    updateQuoteAttribute(attributeToReplace, sourceOfferAttribute);
                }else if(commercialRuleLine.getSourceAttributeValue() != null){
                    overrideBySourceAttributeValue(attributeToReplace, commercialRuleLine.getSourceAttributeValue(), commercialRuleHeaderCode);
                }
            }else if(commercialRuleLine.getSourceAttributeValue() != null){
                overrideBySourceAttributeValue(attributeToReplace, commercialRuleLine.getSourceAttributeValue(), commercialRuleHeaderCode);
            }
        } else if (commercialRuleLine.getSourceOfferTemplate() != null && commercialRuleLine.getSourceProduct() != null){
            Optional<QuoteOffer> sourceOffer = quoteOffers
                    .stream()
                    .filter(offer -> offer.getOfferTemplate().getId().equals(commercialRuleLine.getSourceOfferTemplate().getId()))
                    .findFirst();
            if(sourceOffer.isPresent()){
                sourceOffer.get().getQuoteProduct()
                        .stream()
                        .filter(product -> product.getProductVersion().getProduct().getId().equals(commercialRuleLine.getSourceProduct().getId()))
                        .forEach(
                                product -> {
                                    Optional<QuoteAttribute> sourceProductAttribute = product.getQuoteAttributes().stream()
                                            .filter(quoteAttribute -> quoteAttribute.getAttribute().getCode().equals(commercialRuleLine.getSourceAttribute().getCode()))
                                            .findFirst();
                                    if (sourceProductAttribute.isPresent()){
                                        updateQuoteAttribute(attributeToReplace, sourceProductAttribute);
                                    }else if(commercialRuleLine.getSourceAttributeValue() != null){
                                        overrideBySourceAttributeValue(attributeToReplace, commercialRuleLine.getSourceAttributeValue(), commercialRuleHeaderCode);
                                    }
                                }
                        );
            } else if(commercialRuleLine.getSourceAttributeValue() != null){
                overrideBySourceAttributeValue(attributeToReplace, commercialRuleLine.getSourceAttributeValue(), commercialRuleHeaderCode);
            }

        }
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