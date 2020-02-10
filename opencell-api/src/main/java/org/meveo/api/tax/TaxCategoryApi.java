package org.meveo.api.tax;

import java.util.function.Function;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.tax.TaxCategoryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.tax.TaxCategoryService;

/**
 * CRUD API for {@link TaxCategory} - Tax category
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class TaxCategoryApi extends BaseCrudApi<TaxCategory, TaxCategoryDto> {

    @Inject
    private TaxCategoryService entityService;

    /**
     * Creates a new TaxCategory entity
     * 
     * @param dto Posted Tax category data to API
     * 
     * @throws MeveoApiException Api exception
     * @throws BusinessException General business exception.
     */
    @Override
    public TaxCategory create(TaxCategoryDto dto) throws MeveoApiException, BusinessException {

        String code = dto.getCode();

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(dto);

        TaxCategory entity = entityService.findByCode(code);

        if (entity != null) {
            throw new EntityAlreadyExistsException(TaxCategory.class, code);
        }

        entity = new TaxCategory();

        dtoToEntity(entity, dto);
        entityService.create(entity);

        return entity;
    }

    /**
     * Updates a Tax category based on code)
     * 
     * @param dto Posted Tax category data to API
     * 
     * @throws MeveoApiException API exception
     * @throws BusinessException business exception.
     */
    @Override
    public TaxCategory update(TaxCategoryDto dto) throws MeveoApiException, BusinessException {

        String code = dto.getCode();
        Long id = dto.getId();

        if (StringUtils.isBlank(code) && id == null) {
            missingParameters.add("code");
        }

        handleMissingParametersAndValidate(dto);

        TaxCategory entity = null;
        if (!StringUtils.isBlank(code)) {
            entity = entityService.findByCode(code);
        } else {
            entity = entityService.findById(id);
        }
        if (entity == null) {
            throw new EntityDoesNotExistsException(TaxCategory.class, !StringUtils.isBlank(code) ? code : id.toString());
        }

        if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            if (entityService.findByCode(dto.getUpdatedCode()) != null) {
                throw new EntityAlreadyExistsException(TaxCategory.class, dto.getUpdatedCode());
            }
        }

        dtoToEntity(entity, dto);

        entity = entityService.update(entity);
        return entity;
    }

    @Override
    protected Function<TaxCategory, TaxCategoryDto> getEntityToDtoFunction() {
        return TaxCategoryDto::new;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param entity Entity to populate
     * @param dto DTO entity object to populate from
     **/
    private void dtoToEntity(TaxCategory entity, TaxCategoryDto dto) {

        boolean isNew = entity.getId() == null;
        if (isNew) {
            entity.setCode(dto.getCode());
        } else if (!StringUtils.isBlank(dto.getUpdatedCode())) {
            entity.setCode(dto.getUpdatedCode());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(StringUtils.isEmpty(dto.getDescription()) ? null : dto.getDescription());
        }
        if (dto.getDescriptionI18n() != null) {
            entity.setDescriptionI18n(convertMultiLanguageToMapOfValues(dto.getDescriptionI18n(), entity.getDescriptionI18n()));
        }

    }
}