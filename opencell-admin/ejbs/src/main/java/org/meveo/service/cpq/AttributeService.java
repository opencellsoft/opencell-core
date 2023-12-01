/**
 * 
 */
package org.meveo.service.cpq;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeValidationType;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.CpqQuote;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

/**
 * @author Rachid.AITYAAZZA
 *
 */

@Stateless
public class AttributeService extends BusinessService<Attribute>{

    private static final String PHONE_REGEX_VALIDATION = "^\\d{10,15}$";
    private static final String EMAIL_REGEX_VALIDATION = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    /**
     * Update parent attribute
     */
    public void updateParentAttribute(Long id) {
        Query q=getEntityManager().createNamedQuery("Attribute.updateParentAttribute").setParameter("id", id);
        q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public <T> T evaluateElExpressionAttribute(String expression, Product product, OfferTemplate offer, CpqQuote quote, WalletOperation walletOperation, Class<T> resultType) throws BusinessException {
        Map<Object, Object> params = new HashMap<>();
        if (Strings.isBlank(expression)) {
            return null;
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_PRODUCT) >= 0 && product != null) {
            params.put(ValueExpressionWrapper.VAR_PRODUCT, product);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_OFFER) >= 0 && offer != null) {
            params.put(ValueExpressionWrapper.VAR_OFFER, offer);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_CPQ_QUOTE) >= 0 && quote != null) {
            params.put(ValueExpressionWrapper.VAR_CPQ_QUOTE, quote);
        }
        if (expression.indexOf(ValueExpressionWrapper.VAR_WALLET_OPERATION) >= 0 && walletOperation != null) {
            params.put(ValueExpressionWrapper.VAR_WALLET_OPERATION, walletOperation);
        }
        if (resultType == null) {
            resultType = (Class<T>) String.class;
        }
        T res =  evaluateExpression(expression, params, resultType);
        return res;
    }

    public Attribute findByDescription(String description) {

        TypedQuery<Attribute> query = getEntityManager().createQuery("select a from Attribute a where lower(a.description)=:code", entityClass)
            .setParameter("code", description.toLowerCase()).setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of description {} found", entityClass.getSimpleName(), description);
            return null;
        }
    }

    public <T extends AttributeValue<?>> void validateAttributes(Set<ProductVersionAttribute> productVersionAttributes, List<T> attributeInstances) {
        if (CollectionUtils.isEmpty(productVersionAttributes)) {
            return;
        }

        Set<Long> checkedAttributs = new HashSet<>();

        attributeInstances.forEach(attributeInstance -> {
            Optional<ProductVersionAttribute> productVersionAttribute = productVersionAttributes.stream().filter(pva -> pva.getAttribute().getId().equals(attributeInstance.getAttribute().getId()))
                    .findFirst();
            if (productVersionAttribute.isEmpty()) {
                throw new BusinessApiException("No ProductVersionAttribute found for Attribute '" + attributeInstance.getAttribute().getCode() + "'");
            }
            validAttribute(productVersionAttribute.get(), attributeInstance);
            checkedAttributs.add(attributeInstance.getAttribute().getId());
        });

        // Check no given mandatory field
        Set<Long> mandatoryProductVersions = productVersionAttributes.stream().filter(ProductVersionAttribute::isMandatory).map(ProductVersionAttribute::getAttribute).map(BaseEntity::getId).collect(Collectors.toSet());
        Set<Long> delta = new HashSet<>(mandatoryProductVersions);
        delta.removeAll(checkedAttributs);
        if (CollectionUtils.isNotEmpty(delta)) {
            throw new BusinessApiException(delta.size() + " mandatories Product Attribute not filled");
        }

    }

    public void validAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        checkMandatoryAttribute(pvAttribute, attributeValue);
        checkReadOnlyAttribute(pvAttribute, attributeValue);
        checkRegExAttribute(pvAttribute, attributeValue);
        checkListAttribute(pvAttribute, attributeValue);
        checkNumericAttribute(pvAttribute, attributeValue);
        checkDateAttribute(pvAttribute, attributeValue);
        checkBooleanAttribute(pvAttribute, attributeValue);
        checkEmailAttribute(pvAttribute, attributeValue);
        checkPhoneAttribute(pvAttribute, attributeValue);
    }

    private void checkPhoneAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Phone values
        if (AttributeTypeEnum.PHONE == pvAttribute.getAttribute().getAttributeType()) {
            if (!isValidPhone(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid Phone value '" + attributeValue.getRealValue() + "'");
            }
        }
    }

    private void checkEmailAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Email values
        if (AttributeTypeEnum.EMAIL == pvAttribute.getAttribute().getAttributeType()) {
            if (!isValidEmail(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid Email value '" + attributeValue.getRealValue() + "'");
            }
        }
    }

    private void checkBooleanAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Boolean values
        if (AttributeTypeEnum.BOOLEAN == pvAttribute.getAttribute().getAttributeType()) {
            if (!isValidBoolean(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid Boolean value '" + attributeValue.getRealValue() + "'");
            }
        }
    }

    private void checkDateAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Dates values
        if (AttributeTypeEnum.DATE == pvAttribute.getAttribute().getAttributeType() ||
                AttributeTypeEnum.CALENDAR == pvAttribute.getAttribute().getAttributeType()) {
            if (!(attributeValue.getRealValue() instanceof Date) && !isValidDate(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid Date value '" + attributeValue.getRealValue() + "'");
            }
        }
    }

    private void checkNumericAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Number values
        if (AttributeTypeEnum.NUMERIC == pvAttribute.getAttribute().getAttributeType() ||
                AttributeTypeEnum.INTEGER == pvAttribute.getAttribute().getAttributeType()) {
            if (!isValidNumber(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid number '" + attributeValue.getRealValue() + "'");
            }

        }

        // Total values
        if (AttributeTypeEnum.TOTAL == pvAttribute.getAttribute().getAttributeType()) {
            if (CollectionUtils.isEmpty(attributeValue.getAssignedAttributeValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " with TOTAL type, does not have a linked assigned attributes");
            }

            attributeValue.getAssignedAttributeValue().forEach(assignedAttibute -> {
                if (!isValidNumber(assignedAttibute.getRealValue())) {
                    throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid number '" + attributeValue.getRealValue() + "'");
                }
            });

            if (!isValidNumber(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid number '" + attributeValue.getRealValue() + "'");
            }

        }

        // Count values
        if (AttributeTypeEnum.COUNT == pvAttribute.getAttribute().getAttributeType()) {
            if (CollectionUtils.isEmpty(attributeValue.getAssignedAttributeValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " with COUNT type, does not have a linked assigned attributes");
            }

            if (!isValidNumber(attributeValue.getRealValue())) {
                throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " has not a valid number '" + attributeValue.getRealValue() + "'");
            }

        }
    }

    private void checkListAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Check value content
        // List values
        if (attributeValue.getRealValue() == null) {
            return;
        }

        String seperator = ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");

        Set<String> givenValues = Set.of(attributeValue.getRealValue().toString().split(seperator));

        if ((AttributeTypeEnum.LIST_TEXT == pvAttribute.getAttribute().getAttributeType() ||
                AttributeTypeEnum.LIST_NUMERIC == pvAttribute.getAttribute().getAttributeType()) && CollectionUtils.isNotEmpty(pvAttribute.getAttribute().getAllowedValues())) {
            if (AttributeTypeEnum.LIST_TEXT == pvAttribute.getAttribute().getAttributeType() && !pvAttribute.getAttribute().getAllowedValues().contains(attributeValue.getRealValue())) {
                throw new BusinessApiException("The value '" + attributeValue.getRealValue() + "' is not part of allowed values " + pvAttribute.getAttribute().getAllowedValues());
            }else if(AttributeTypeEnum.LIST_NUMERIC == pvAttribute.getAttribute().getAttributeType()){
	           boolean valueExist = pvAttribute.getAttribute().getAllowedValues().stream().anyMatch(value -> new BigDecimal(value).compareTo(new BigDecimal(attributeValue.getRealValue().toString())) == 0);
			   if(!valueExist){
				   throw new BusinessApiException("The value '" + attributeValue.getRealValue() + "' is not part of allowed values " + pvAttribute.getAttribute().getAllowedValues());
			   }
            }

        }

        // List multiple values
        if (AttributeTypeEnum.LIST_MULTIPLE_TEXT == pvAttribute.getAttribute().getAttributeType() ||
                AttributeTypeEnum.LIST_MULTIPLE_NUMERIC == pvAttribute.getAttribute().getAttributeType()) {
            // Split value by separator
            if (!pvAttribute.getAttribute().getAllowedValues().containsAll(givenValues)) {
                throw new BusinessApiException("The values " + givenValues + " are not part of allowed values " + pvAttribute.getAttribute().getAllowedValues());
            }

        }
    }

    private void checkRegExAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        if (attributeValue.getRealValue() == null) {
            return;
        }
        // Check value type
        if (pvAttribute.getValidationType() == AttributeValidationType.REGEX && !attributeValue.getRealValue().toString().matches(pvAttribute.getValidationPattern())) {
            throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " does not meet the regEx " + pvAttribute.getValidationPattern());
        }
    }

    private void checkReadOnlyAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Read only attributes : Attributes having ready-only property must not be modified
        if (pvAttribute.getReadOnly()) {
            if (StringUtils.isBlank(attributeValue.getRealValue())) {
                throw new BusinessApiException("The read only attribute " + pvAttribute.getAttribute().getCode() + " should have a default value");
            }
            if (AttributeTypeEnum.DATE == pvAttribute.getAttribute().getAttributeType() && attributeValue.getRealValue() instanceof Date && !pvAttribute.getDefaultValue().equals(DateUtils.formatAsDate((Date) attributeValue.getRealValue()))) {
                throw new BusinessApiException("The read only attribute " + pvAttribute.getAttribute().getCode() + " cannot be updated");
            } else if (!(attributeValue.getRealValue() instanceof Date) && pvAttribute.getDefaultValue() != null && !String.valueOf(pvAttribute.getDefaultValue()).equals(String.valueOf(attributeValue.getRealValue()))) {
                throw new BusinessApiException("The read only attribute " + pvAttribute.getAttribute().getCode() + " cannot be updated :" +
                        " default value '" + pvAttribute.getDefaultValue() + "', given value '" + attributeValue.getRealValue() + "'");
            }

        }
    }

    private void checkMandatoryAttribute(ProductVersionAttribute pvAttribute, AttributeValue<?> attributeValue) {
        // Check requirement
        if (pvAttribute.isMandatory() && (attributeValue == null || attributeValue.getRealValue() == null)) {
            throw new BusinessApiException("The attribute " + pvAttribute.getAttribute().getCode() + " is mandatory");
        }
    }

    private boolean isValidNumber(Object value) {
        if (value == null) {
            return true;
        }
        try {
            Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    private boolean isValidDate(Object value) {
        if (value == null) {
            return true;
        }
        try {
            Date date = value.toString().matches("^\\d{4}-\\d{2}-\\d{2}$") ? new SimpleDateFormat("yyyy-MM-dd").parse(value.toString()) :
                    new SimpleDateFormat("dd/MM/yyyy").parse(value.toString());
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean isValidBoolean(Object value) {
        if (value == null) {
            return true;
        }
        return "true".equalsIgnoreCase(value.toString()) || "false".equalsIgnoreCase(value.toString());
    }

    private boolean isValidEmail(Object value) {
        if (value == null) {
            return true;
        }
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX_VALIDATION);

        return emailPattern.matcher(value.toString()).matches();
    }

    private boolean isValidPhone(Object value) {
        if (value == null) {
            return true;
        }

        Pattern phonePattern = Pattern.compile(PHONE_REGEX_VALIDATION);

        String givenPhone = value.toString();

        if (givenPhone.startsWith("+")) {
            givenPhone = givenPhone.replace("+", "00");
        }
        if (givenPhone.contains("(")) {
            givenPhone = givenPhone.replace("(", "");
        }
        if (givenPhone.contains(")")) {
            givenPhone = givenPhone.replace(")", "");
        }
        if (givenPhone.contains("-")) {
            givenPhone = givenPhone.replace("-", "");
        }

        return phonePattern.matcher(givenPhone).matches();

    }

}