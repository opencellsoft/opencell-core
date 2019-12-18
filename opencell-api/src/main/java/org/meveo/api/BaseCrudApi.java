package org.meveo.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.module.ModulePropertyFlagLoader;
import org.meveo.api.dto.response.GenericSearchResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.BusinessEntity;
import org.meveo.service.base.BusinessService;
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
public abstract class BaseCrudApi<E extends BusinessEntity, T extends BusinessEntityDto> extends BaseApi implements ApiService<E, T> {

    /**
     * Persistence service corresponding to a entity that API implementation corresponds to
     */
    private BusinessService<E> ps;

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
            ps = (BusinessService<E>) getPersistenceService(entityClass, true);
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
    public E createOrUpdate(T dataDto) throws MeveoApiException, BusinessException {

        if (ps.findByCode(dataDto.getCode()) == null) {
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
    public void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        E entity = ps.findByCode(code);

        if (entity == null) {
            throw new EntityDoesNotExistsException(entityClass, code);
        }

        ps.remove(entity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#findIgnoreNotFound(java.lang.String)
     */
    @Override
    public T find(String code, ModulePropertyFlagLoader modulePropertyFlagLoader) throws MeveoApiException {
        throw new UnsupportedOperationException();
    }

    /**
     * Perform a paginated search returning a list of DTOs matched
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @param entityToDtoFunction A function definition to convert entity to dto object
     * @return Search results including repeated pagination and filtering criteria plus total record count
     * @throws MeveoApiException Api related exception
     */
    protected GenericSearchResponse<T> find(PagingAndFiltering pagingAndFiltering, Function<E, T> entityToDtoFunction) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, pagingAndFiltering, entityClass);

        Long totalCount = ps.count(paginationConfig);

        pagingAndFiltering.setTotalNumberOfRecords(totalCount.intValue());

        List<T> dtos = new ArrayList<T>();
        if (totalCount > 0) {
            List<E> walletOperations = ps.list(paginationConfig);
            for (E wo : walletOperations) {
                dtos.add(entityToDtoFunction.apply(wo));
            }
        }

        GenericSearchResponse<T> response = new GenericSearchResponse<T>(dtos, pagingAndFiltering);
        return response;
    }
}