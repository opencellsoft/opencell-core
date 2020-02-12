package org.meveo.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.IEntityDto;
import org.meveo.api.dto.response.GenericSearchResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.primefaces.model.SortOrder;

/**
 * Base API service for CRUD operations on entity
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 *
 * @param <E> Entity class
 * @param <T> Dto class
 */
public abstract class BaseCrudApi<E extends BaseEntity, T extends BaseEntityDto> extends BaseApi implements ApiService<E, T> {

    /**
     * Persistence service corresponding to a entity that API implementation corresponds to
     */
    private PersistenceService<E> ps;

    /**
     * Entity class that API implementation corresponds to
     */
    private Class<E> entityClass;

    /**
     * Constructor
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BaseCrudApi() {

        Class clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }
        Object o = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

        if (o instanceof TypeVariable) {
            entityClass = (Class<E>) ((TypeVariable) o).getBounds()[0];
        } else {
            entityClass = (Class<E>) o;
        }

        try {
            ps = (PersistenceService<E>) getPersistenceService(entityClass, true);
        } catch (BusinessException e) {
            log.error("Failed to obtain a persistenceService for {}", getClass());
        }
    }

    @Override
    public T findIgnoreNotFound(String code) throws MeveoApiException {
        try {
            return find(code);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    @Override
    public T findIgnoreNotFound(Long id) throws MeveoApiException {
        try {
            return find(id);
        } catch (EntityDoesNotExistsException e) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public E createOrUpdate(T dataDto) throws MeveoApiException, BusinessException {

        BaseEntity entity = null;

        if (dataDto instanceof BusinessEntityDto && ((BusinessEntityDto) dataDto).getCode() != null) {
            entity = ((BusinessService) ps).findByCode(((BusinessEntityDto) dataDto).getCode());
        } else if (dataDto instanceof IEntityDto && ((IEntityDto)dataDto).getId() != null) {
            entity = ps.findById(((IEntityDto)dataDto).getId());
        }

        if (entity == null) {
            return create(dataDto);
        } else {
            return update(dataDto);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        BusinessEntity entity = ((BusinessService) ps).findByCode(code);
        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, code);
        }
        if (enable) {
            ps.enable((E) entity);
        } else {
            ps.disable((E) entity);
        }
    }

    @Override
    public void enableOrDisable(Long id, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (id == null) {
            missingParameters.add("id");
        }

        handleMissingParameters();

        E entity = ps.findById(id);
        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, id);
        }
        if (enable) {
            ps.enable((E) entity);
        } else {
            ps.disable((E) entity);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        E entity = (E) ((BusinessService) ps).findByCode(code);

        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, code);
        }

        ps.remove(entity);
    }

    @Override
    public void remove(Long id) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (id == null) {
            missingParameters.add("id");
        }

        handleMissingParameters();

        E entity = ps.findById(id);

        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, id);
        }

        ps.remove(entity);
    }

    /**
     * Returns Entity DTO based on its code.
     * 
     * @param code Entity code
     * @return DTO object
     * @throws MeveoApiException Meveo api exception.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public T find(String code) throws MeveoApiException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        E entity = (E) ((BusinessService) ps).findByCode(code);
        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, code);
        }

        return getEntityToDtoFunction().apply(entity);
    }

    /**
     * Returns Entity DTO based on its ID.
     * 
     * @param id Entity ID
     * @return DTO object
     * @throws MeveoApiException API related exception
     */
    public T find(Long id) throws MeveoApiException {
        if (id == null) {
            missingParameters.add("id");
        }
        handleMissingParameters();

        E entity = (E) ps.findById(id);
        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, id);
        }

        return getEntityToDtoFunction().apply(entity);
    }

    /**
     * Perform a paginated search returning a list of entity DTOs matched
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return Search results including repeated pagination and filtering criteria plus total record count
     * @throws MeveoApiException API related exception
     */
    public GenericSearchResponse<T> search(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return search(pagingAndFiltering, getEntityToDtoFunction());
    }

    /**
     * Perform a paginated search returning a list of DTOs matched
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @param entityToDtoFunction A function definition to convert entity to dto object
     * @return Search results including repeated pagination and filtering criteria plus total record count
     * @throws MeveoApiException Api related exception
     */
    protected GenericSearchResponse<T> search(PagingAndFiltering pagingAndFiltering, Function<E, T> entityToDtoFunction) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, pagingAndFiltering, entityClass);

        Long totalCount = ps.count(paginationConfig);

        pagingAndFiltering.setTotalNumberOfRecords(totalCount.intValue());

        List<T> dtos = new ArrayList<T>();
        if (totalCount > 0) {
            List<E> entityList = ps.list(paginationConfig);
            for (E wo : entityList) {
                dtos.add(entityToDtoFunction.apply(wo));
            }
        }

        GenericSearchResponse<T> response = new GenericSearchResponse<T>(dtos, pagingAndFiltering);
        return response;
    }

    /**
     * @return A function to convert entity object to DTO object
     */
    protected Function<E, T> getEntityToDtoFunction() {

        return null;
    }
}