package org.meveo.api.account;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.TitlesDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;

/**
 * CRUD API for {@link Title}.
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class TitleApi extends BaseApi {

    @Inject
    private TitleService titleService;

    /**
     * Creates a new Title entity.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void create(TitleDto postData) throws MeveoApiException, BusinessException {

        String titleCode = postData.getCode();

        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }

        handleMissingParametersAndValidate(postData);

        Title title = titleService.findByCode(titleCode);

        if (title != null) {
            throw new EntityAlreadyExistsException(Title.class, titleCode);
        }

        title = new Title();
        title.setCode(titleCode);
        title.setDescription(postData.getDescription());
        title.setIsCompany(postData.getIsCompany());
        title.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));

        titleService.create(title);
    }

    /**
     * Returns TitleDto based on title code.
     * 
     * @param titleCode title code
     * @return title dto object
     * @throws MeveoApiException meveo api exception.
     */
    public TitleDto find(String titleCode) throws MeveoApiException {
        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }
        handleMissingParameters();

        Title title = titleService.findByCode(titleCode);
        if (title == null) {
            throw new EntityDoesNotExistsException(Title.class, titleCode);
        }

        TitleDto titleDto = new TitleDto();
        titleDto.setCode(title.getCode());
        titleDto.setDescription(title.getDescription());
        titleDto.setIsCompany(title.getIsCompany());
        titleDto.setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(title.getDescriptionI18n()));

        EntityManager em = titleService.getEntityManager();

        TypedQuery<Seller> query = em.createQuery("Select e from Seller e order by id", Seller.class);
        List<Seller> sellers = query.getResultList();

        int i = 0;
        for (Seller seller : sellers) {
            seller.setDescription("me_" + i);
            i++;
            em.flush();
        }

        TypedQuery<CustomerAccount> query2 = em.createQuery("Select e from CustomerAccount e order by id", CustomerAccount.class);
        List<CustomerAccount> cas = query2.getResultList();

        i = 0;
        for (CustomerAccount ca : cas) {
            ca.setDescription("me_" + i);
            i++;
            em.flush();
        }

        for (Seller seller : sellers) {
            seller.setSellerCFField("K", "me_" + i);
            i++;
            em.flush();
        }

        i = 0;
        for (CustomerAccount ca : cas) {
            ca.setCfValue("K", "ke_" + i);
            i++;
            em.flush();
        }

        return titleDto;
    }

    /**
     * Updates a Title Entity based on title code.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void update(TitleDto postData) throws MeveoApiException, BusinessException {
        String titleCode = postData.getCode();
        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }

        handleMissingParametersAndValidate(postData);

        Title title = titleService.findByCode(titleCode);
        if (title == null) {
            throw new EntityDoesNotExistsException(Title.class, titleCode);
        }

        title.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        title.setDescription(postData.getDescription());
        title.setIsCompany(postData.getIsCompany());
        if (postData.getLanguageDescriptions() != null) {
            title.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), title.getDescriptionI18n()));
        }

        titleService.update(title);
    }

    /**
     * Removes a title based on title code.
     * 
     * @param titleCode title's code
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void remove(String titleCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(titleCode)) {
            missingParameters.add("titleCode");
        }

        handleMissingParameters();

        Title title = titleService.findByCode(titleCode);
        if (title != null) {
            titleService.remove(title);
        } else {
            throw new EntityDoesNotExistsException(Title.class, titleCode);
        }
    }

    public void createOrUpdate(TitleDto postData) throws MeveoApiException, BusinessException {
        Title title = titleService.findByCode(postData.getCode());

        if (title == null) {
            // create
            create(postData);
        } else {
            // update
            update(postData);
        }
    }

    public TitlesDto list() throws MeveoApiException {
        TitlesDto titlesDto = new TitlesDto();
        List<Title> titles = titleService.list(true);

        if (titles != null) {
            for (Title title : titles) {
                TitleDto titleDto = new TitleDto();
                titleDto.setCode(title.getCode());
                titleDto.setDescription(title.getDescription());
                titleDto.setIsCompany(title.getIsCompany());
                titlesDto.getTitle().add(titleDto);
                titleDto.setLanguageDescriptions(LanguageDescriptionDto.convertMultiLanguageFromMapOfValues(title.getDescriptionI18n()));
            }
        }

        return titlesDto;
    }
}
