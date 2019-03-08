package org.meveo.apiv2.services;

import org.meveo.model.catalog.ProductTemplate;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Optional<ProductTemplate> findById(Long id);

    Long getCount(String filter);

    List<ProductTemplate>  list(Long offset, Long limit, String sort, String orderBy, String filter);

    ProductTemplate create(ProductTemplate productTemplate);

    Optional<ProductTemplate> update(Long id, ProductTemplate productTemplate);

    Optional<ProductTemplate>  patch(Long id, ProductTemplate productTemplate);

    Optional<ProductTemplate> delete(Long id);

    void saveImage(ProductTemplate productTemplate, String imageUrl, String image64);
}
