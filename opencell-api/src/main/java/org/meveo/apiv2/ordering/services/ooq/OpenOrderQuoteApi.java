package org.meveo.apiv2.ordering.services.ooq;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.ordering.resource.ooq.OpenOrderQuoteDto;
import org.meveo.apiv2.ordering.resource.order.ThresholdInput;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderQuote;
import org.meveo.model.ordering.OpenOrderQuoteStatusEnum;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.Threshold;
import org.meveo.model.ordering.ThresholdRecipientsEnum;
import org.meveo.model.settings.OpenOrderSetting;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.cpq.TagService;
import org.meveo.service.order.OpenOrderArticleService;
import org.meveo.service.order.OpenOrderProductService;
import org.meveo.service.order.OpenOrderQuoteService;
import org.meveo.service.order.OpenOrderService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.settings.impl.OpenOrderSettingService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.DRAFT;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.REJECTED;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.SENT;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.VALIDATED;
import static org.meveo.model.ordering.OpenOrderQuoteStatusEnum.WAITING_VALIDATION;
import static org.meveo.model.ordering.OpenOrderTypeEnum.ARTICLES;
import static org.meveo.model.ordering.OpenOrderTypeEnum.PRODUCTS;

@Stateless
public class OpenOrderQuoteApi {

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private OpenOrderQuoteService openOrderQuoteService;

    @Inject
    private OpenOrderSettingService openOrderSettingService;

    @Inject
    private OpenOrderTemplateService openOrderTemplateService;

    @Inject
    private OpenOrderProductService openOrderProductService;

    @Inject
    private OpenOrderArticleService openOrderArticleService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private TagService tagService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private OpenOrderService openOrderService;

    @Transactional
    public Long create(OpenOrderQuoteDto dto) {

        // init bags used in ooq builder
        List<OpenOrderProduct> products = new ArrayList<>();
        List<OpenOrderArticle> articles = new ArrayList<>();

        // build tags
        List<Tag> tags = new ArrayList<>();

        if (openOrderQuoteService.findByCode(dto.getCode()) != null) {
            throw new EntityAlreadyExistsException(OpenOrderQuote.class, dto.getCode());
        }

        // Load dependencies
        OpenOrderTemplate template = getOpenOrderTemplate(dto);
        BillingAccount billingAccount = getBillingAccount(dto);

        buildTags(dto, tags);
        validateThresholds(dto, template);
        validateAndBuildProductsAndArticles(dto, products, articles, template);
        validateReadOnlyFields(dto, template);

        // Build OpenOrderQuote object
        OpenOrderQuote ooq = new OpenOrderQuote();
        ooq.setStatus(DRAFT); // 3.2 status: default value in creation is DRAFT. should not be changed by update call.
        ooq.setCurrency(billingAccount.getTradingCurrency()); // 3.3 currency: billingAccount currency, set only in creation, and not updated
        buildOOQ(dto, products, articles, tags, template, billingAccount, ooq);

        openOrderQuoteService.create(ooq);

        return ooq.getId();
    }

    @Transactional
    public Long update(Long idOOQ, OpenOrderQuoteDto dto) {

        // init bags used in ooq builder
        List<OpenOrderProduct> products = new ArrayList<>();
        List<OpenOrderArticle> articles = new ArrayList<>();

        // build tags
        List<Tag> tags = new ArrayList<>();

        OpenOrderQuote ooq = openOrderQuoteService.findById(idOOQ);

        if (ooq == null) {
            throw new EntityDoesNotExistsException("No OpenOrderQuote found with id '" + idOOQ + "'");
        }

        OpenOrderQuote ooqWithSameCode = openOrderQuoteService.findByCode(dto.getCode());
        if (ooqWithSameCode != null && !ooqWithSameCode.getId().equals(idOOQ)) {
            throw new EntityAlreadyExistsException(OpenOrderQuote.class, dto.getCode());
        }

        // Load dependencies
        OpenOrderTemplate template = getOpenOrderTemplate(dto);
        BillingAccount billingAccount = getBillingAccount(dto);

        if (!template.getId().equals(ooq.getOpenOrderTemplate().getId())) {
            throw new BusinessApiException("Template cannot be updated");
        }

        buildTags(dto, tags);
        validateThresholds(dto, template);
        validateAndBuildProductsAndArticles(dto, products, articles, template);
        validateReadOnlyFields(dto, template);

        // Build OpenOrderQuote object
        buildOOQ(dto, products, articles, tags, template, billingAccount, ooq);

        openOrderQuoteService.update(ooq);

        return ooq.getId();
    }

    private BillingAccount getBillingAccount(OpenOrderQuoteDto dto) {
        BillingAccount billingAccount = billingAccountService.findByCode(dto.getBillingAccountCode());

        if (billingAccount == null) {
            throw new EntityDoesNotExistsException("No BillingAccount found with code '" + dto.getBillingAccountCode() + "'");
        }
        return billingAccount;
    }

    private OpenOrderTemplate getOpenOrderTemplate(OpenOrderQuoteDto dto) {
        OpenOrderTemplate template = openOrderTemplateService.findByCode(dto.getOpenOrderTemplate());

        if (template == null) {
            throw new EntityDoesNotExistsException("No OpenOrderTemplate found with code '" + dto.getOpenOrderTemplate() + "'");
        }

        // To activate in SPRINT 21
        /*if (OpenOrderTemplateStatusEnum.ACTIVE != template.getStatus()) {
            throw new BusinessApiException("Template shall be in ACTIVE status");
        }*/
        return template;
    }

    @Transactional
    public OpenOrderQuote changeStatus(String code, OpenOrderQuoteStatusEnum newStatus) {
        // Check existence
        OpenOrderQuote ooq = openOrderQuoteService.findByCode(code);

        if (ooq == null) {
            throw new EntityDoesNotExistsException("No Open Order Quote found with code '" + code + "'");
        }

        // Find General Settings
        List<OpenOrderSetting> settings = openOrderSettingService.list();

        if (CollectionUtils.isEmpty(settings) || settings.size() == 0) {
            throw new BusinessApiException("No Open Order setting found");
        }

        OpenOrderSetting setting = openOrderSettingService.list().get(0);

        // check by new status
        switch (newStatus) {
            case DRAFT:
                if (ooq.getStatus() != DRAFT) {
                    throw new BusinessApiException("Cannot change status '" + ooq.getStatus() + "' to DRAFT");
                }

                break;

            case WAITING_VALIDATION:
                // ASK VALIDATION feature activated and status in(DRAFT, REJECTED)
                // check that Product/Article list is not empty(depending on type)

                if (!setting.getUseManagmentValidationForOOQuotation()) {
                    throw new BusinessApiException("ASK VALIDATION feature is not activated");
                }

                if (!(ooq.getStatus() == DRAFT || ooq.getStatus() == REJECTED)) {
                    throw new BusinessApiException("Open Order Quote status must be DRAFT or REJECTED");
                }

                if (ooq.getOpenOrderType() == ARTICLES && CollectionUtils.isEmpty(ooq.getArticles())) {
                    throw new BusinessApiException("Cannot ask validation for Open Order Quote without Articles");
                }

                if (ooq.getOpenOrderType() == PRODUCTS && CollectionUtils.isEmpty(ooq.getProducts())) {
                    throw new BusinessApiException("Cannot ask validation for Open Order Quote without Products");
                }

                break;

            case ACCEPTED:
                if (ooq.getStatus() != SENT) {
                    throw new BusinessApiException("Open Order Quote status must be SENT");
                }

                break;

            case SENT:
                // ASK VALIDATION feature NOT activated and status in(DRAFT)
                // status in (VALIDATED, SENT)
                // check that Product/Article list is not empty(depending on type)

                if (setting.getUseManagmentValidationForOOQuotation()) {
                    throw new BusinessApiException("ASK VALIDATION feature shall not be activated");
                }

                if (ooq.getStatus() != VALIDATED) {
                    throw new BusinessApiException("Open Order Quote status must be VALIDATED");
                }

                break;
            case VALIDATED :
                openOrderService.create(ooq);
                break;
            case REJECTED:
                if (!setting.getUseManagmentValidationForOOQuotation()) {
                    throw new BusinessApiException("ASK VALIDATION feature is not activated");
                }

                if (ooq.getStatus() != WAITING_VALIDATION) {
                    throw new BusinessApiException("Open Order Quote status must be WAITING_VALIDATION");
                }

                break;

            case CANCELED:
                // Possible for all status : DRAFT, WAITING VALIDATION, REJECTED, VALIDATED, ACCEPTED, SENT
                break;

            default:
        }

        return openOrderQuoteService.changeStatus(ooq, newStatus);
    }

    private void buildOOQ(OpenOrderQuoteDto dto, List<OpenOrderProduct> products, List<OpenOrderArticle> articles, List<Tag> tags, OpenOrderTemplate template, BillingAccount billingAccount, OpenOrderQuote ooq) {
        ooq.setCode(dto.getCode());
        ooq.setDescription(dto.getDescription());
        ooq.setOpenOrderType(dto.getOpenOrderType());
        ooq.setMaxAmount(dto.getMaxAmount());
        ooq.setOpenOrderNumber(serviceSingleton.getNextOpenOrderSequence());
        ooq.setOpenOrderTemplate(template); // 3.4 template: set at creation, cannot be changed later.
        ooq.setExternalReference(dto.getExternalReference());
        ooq.setEndOfValidityDate(dto.getEndOfValidityDate());
        ooq.setBillingAccount(billingAccount);
        ooq.setActivationDate(dto.getActivationDate());
        ooq.setArticles(articles);
        ooq.setProducts(products);
        ooq.setTags(tags);

        List<Threshold> thresholds = new ArrayList<>();
        Optional.ofNullable(dto.getThresholds()).orElse(Collections.emptySet())
                .forEach(thresholdDto -> {
                    Threshold threshold = new Threshold();
                    threshold.setOpenOrderTemplate(template);
                    threshold.setExternalRecipient(thresholdDto.getExternalRecipient());
                    threshold.setRecipients(thresholdDto.getRecipients());
                    threshold.setSequence(thresholdDto.getSequence());
                    threshold.setPercentage(thresholdDto.getPercentage());
                    threshold.setOpenOrderQuote(ooq);

                    thresholds.add(threshold);
                });
        ooq.setThresholds(thresholds);
    }

    private void validateReadOnlyFields(OpenOrderQuoteDto dto, OpenOrderTemplate template) {
        // Check read only properties
        // type: read from template
        if (template.getOpenOrderType() != dto.getOpenOrderType()) {
            throw new BusinessApiException("OpenOrder type shall be the same as Template : given='" + dto.getOpenOrderType() + "' | template='" + template.getOpenOrderType() + "'");
        }
    }

    private void validateAndBuildProductsAndArticles(OpenOrderQuoteDto dto, List<OpenOrderProduct> products, List<OpenOrderArticle> articles, OpenOrderTemplate template) {
        // check products/articles
        if (dto.getOpenOrderType() == PRODUCTS) {
            // articles must be empty
            if (CollectionUtils.isNotEmpty(dto.getArticles())) {
                throw new BusinessApiException("OpenOrderQuote with type PRODUCTS shall not have ARTICLES");
            }

            // products  must not be empty
            if (CollectionUtils.isEmpty(dto.getProducts())) {
                throw new BusinessApiException("OpenOrderQuote with type PRODUCTS must have at least one PRODUCTS");
            }

            // all products must exist and be active in the openOrderTemplate products list.
            dto.getProducts().forEach(productCode -> {
                OpenOrderProduct templateOOP = openOrderProductService.findByProductCodeAndTemplate(productCode, template.getId());

                if (templateOOP == null) {
                    throw new EntityDoesNotExistsException("Open Order Product with code '" + productCode + "' is not exist in template");
                }

                OpenOrderProduct oop = new OpenOrderProduct();
                oop.setActive(true);
                oop.setProduct(templateOOP.getProduct());
                oop.setOpenOrderTemplate(template);
                oop.updateAudit(currentUser);

                products.add(oop);
            });
        } else if (dto.getOpenOrderType() == ARTICLES) {
            // products must be empty
            if (CollectionUtils.isNotEmpty(dto.getProducts())) {
                throw new BusinessApiException("OpenOrderQuote with type ARTICLES shall not have PRODUCTS");
            }

            // articles  must not be empty
            if (CollectionUtils.isEmpty(dto.getArticles())) {
                throw new BusinessApiException("OpenOrderQuote with type ARTICLES must have at least one ARTICLES");
            }

            // all articles must exist and be active in the openOrderTemplate articles list.
            dto.getArticles().forEach(articleCode -> {
                OpenOrderArticle article = openOrderArticleService.findByArticleCodeAndTemplate(articleCode, template.getId());
                if (article == null) {
                    throw new EntityDoesNotExistsException("Open Order Article with code '" + articleCode + "' is not exist in template");
                }

                OpenOrderArticle ooa = new OpenOrderArticle();
                ooa.setActive(true);
                ooa.setAccountingArticle(article.getAccountingArticle());
                ooa.setOpenOrderTemplate(template);
                ooa.updateAudit(currentUser);

                articles.add(ooa);

            });
        }
    }

    private void validateThresholds(OpenOrderQuoteDto dto, OpenOrderTemplate template) {
        // check thresholds list
        if (CollectionUtils.isNotEmpty(dto.getThresholds())) {
            dto.getThresholds().forEach(thresholdDto -> {
                // "percentage": distinct integers, from 1 to 100
                if (thresholdDto.getPercentage() != null && (thresholdDto.getPercentage() < 1 || thresholdDto.getPercentage() > 100)) {
                    throw new BusinessApiException("Invalid Threshold percentage '" + thresholdDto.getPercentage() + "'. Value must be between 1 and 100");
                }

                // recipients: not null, enum: CUSTOMER,SALES_AGENT,CONSUMER
                if (CollectionUtils.isEmpty(thresholdDto.getRecipients())) {
                    throw new BusinessApiException("Threshold Recipients must not be empty");
                }

                // externalRecipient: should be valid email format :
                // use @Email in dto field

            });

            // Sequence: distinct integers, from 1 to thresholds list size.
            List<Integer> dtoSequence = dto.getThresholds().stream()
                    .sorted(Comparator.comparing(ThresholdInput::getSequence))
                    .map(ThresholdInput::getSequence).collect(Collectors.toList());

            if (dtoSequence.get(0) != 1) {
                throw new BusinessApiException("Threshold sequence shall be start by '1'");
            }

            if (!isConsecutive(dtoSequence)) {
                throw new BusinessApiException("Threshold sequence are not consecutive " + dtoSequence);
            }

            // For the list of thresholds ordered by sequence, the next threshold percent must be greater than the previous one.
            List<ThresholdInput> sortedThresholds = dto.getThresholds().stream()
                    .sorted(Comparator.comparing(ThresholdInput::getSequence))
                    .collect(Collectors.toList());
            int percent = 0;
            for (ThresholdInput th : sortedThresholds) {
                if (percent == 0) {
                    percent = th.getPercentage();
                    continue;
                }

                if (th.getPercentage() <= percent) {
                    throw new BusinessApiException("Thresholds percent are not in correct sequence : current percente='" + th.getPercentage() + "', previous one='" + percent + "'");
                }
                percent = th.getPercentage();
            }

            // all existing thresholds on template must be present in the OpenOrder (percentage is the key).
            List<Integer> templateThresholdPercentages = Optional.ofNullable(template.getThresholds()).orElse(Collections.emptyList())
                    .stream()
                    .sorted(Comparator.comparing(Threshold::getPercentage))
                    .map(Threshold::getPercentage).collect(Collectors.toList());

            List<Integer> dtoThresholdPercentages = dto.getThresholds().stream()
                    .sorted(Comparator.comparing(ThresholdInput::getPercentage))
                    .map(ThresholdInput::getPercentage)
                    .collect(Collectors.toList());

            templateThresholdPercentages.removeAll(dtoThresholdPercentages);

            if (templateThresholdPercentages.size() > 0) {
                throw new BusinessApiException("All existing thresholds on template must be present in the OpenOrder. Missing percentages : " + templateThresholdPercentages);
            }

            // all existing recipients in a template line must be present in the OpenOrder existing line.
            Set<ThresholdRecipientsEnum> templateThresholdRecipients = Optional.ofNullable(template.getThresholds()).orElse(Collections.emptyList())
                    .stream()
                    .map(Threshold::getRecipients)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            Set<ThresholdRecipientsEnum> dtoThresholdRecipients = dto.getThresholds().stream()
                    .map(ThresholdInput::getRecipients)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            templateThresholdRecipients.removeAll(dtoThresholdRecipients);

            if (templateThresholdRecipients.size() > 0) {
                throw new BusinessApiException("All existing recipients in a template line must be present in the OpenOrder existing line. Missing recipients : " + templateThresholdRecipients);
            }
        }
    }

    private void buildTags(OpenOrderQuoteDto dto, List<Tag> tags) {
        Optional.ofNullable(dto.getTags()).orElse(Collections.emptySet())
                .forEach(tagCode -> {
                    Tag tag = tagService.findByCode(tagCode);
                    if (tag == null) {
                        throw new EntityDoesNotExistsException("Tag with code '" + tagCode + "' is not exist");
                    }

                    tags.add(tag);
                });
    }

    public boolean isConsecutive(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) != list.get(i + 1) - 1) {
                return false;
            }
        }
        return true;
    }

}