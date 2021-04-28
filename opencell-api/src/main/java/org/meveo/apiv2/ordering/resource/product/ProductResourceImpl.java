/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.ordering.resource.product;

import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.ordering.services.ProductApiService;
import org.meveo.model.catalog.ProductTemplate;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

public class ProductResourceImpl implements ProductResource {
    @Inject
    private ProductApiService productService;
    private ProductMapper productMapper = new ProductMapper();

    @Override
    public Response getProducts(Long offset, Long limit, String sort, String orderBy, String filter, Request request) {
        List<ProductTemplate> productTemplatesEntity = productService.list(offset, limit, sort, orderBy, filter);
        EntityTag etag = new EntityTag(Integer.toString(productTemplatesEntity.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }
        ImmutableProduct[] productList = productTemplatesEntity
                .stream()
                .map(productTemplate -> toResourceProductWithLink(productMapper.toResource(productTemplate)))
                .toArray(ImmutableProduct[]::new);
        Long productCount = productService.getCount(filter);
        Products products = ImmutableProducts.builder().addData(productList).offset(offset).limit(limit).total(productCount)
                .build().withLinks(new LinkGenerator.PaginationLinkGenerator(ProductResource.class)
                        .offset(offset).limit(limit).total(productCount).build());
        return Response.ok().cacheControl(cc).tag(etag).entity(products).build();
    }

    @Override
    public Response getProduct(Long id, Request request) {
        return productService.findById(id)
                .map(productTemplate ->  {
                    EntityTag etag = new EntityTag(Integer.toString(productTemplate.hashCode()));
                    CacheControl cc = new CacheControl();
                    cc.setMaxAge(1000);
                    Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
                    if (builder != null) {
                        builder.cacheControl(cc);
                        return builder.build();
                    }
                    return Response.ok().cacheControl(cc).tag(etag)
                            .entity(toResourceProductWithLink(productMapper.toResource(productTemplate))).build();
                })
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response createProduct(Product product) {
        ProductTemplate productTemplate = productService.create(productMapper.toEntity(product));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ProductResource.class, productTemplate.getId()).build())
                .entity(toResourceProductWithLink(productMapper.toResource(productTemplate)))
                .build();
    }

    @Override
    public Response updateProduct(Long id, Product product) {
        return productService.update(id, productMapper.toEntity(product))
        .map(productTemplate -> {
            if(product.getImageUrl() != null){
                productService.saveImage(productTemplate, product.getImageUrl(),product.getImage64());
            }
            return Response.ok().entity(toResourceProductWithLink(productMapper.toResource(productTemplate))).build();
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response patchProduct(Long id, Product product) {
        return productService.patch(id, productMapper.toEntity(product))
                .map(productTemplate -> {
                    if(product.getImageUrl() != null){
                        productService.saveImage(productTemplate, product.getImageUrl(),product.getImage64());
                    }
                    return Response.ok().entity(toResourceProductWithLink(productMapper.toResource(productTemplate))).build();
                }).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteProduct(Long id) {
        return productService.delete(id).map(productTemplate ->
                Response.ok().entity(toResourceProductWithLink(productMapper.toResource(productTemplate))).build())
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteProducts(List<Long> ids) {
        ids.forEach(id -> productService.delete(id)
                        .orElseThrow(() -> new NotFoundException("product with id "+id+" does not exist !")));
        return Response.ok().entity("Successfully deleted all products").build();
    }

    // TODO : move to mapper
    private Product toResourceProductWithLink(Product product) {
        return ImmutableProduct.copyOf(product).withLinks(new LinkGenerator.SelfLinkGenerator(ProductResource.class)
                .withId(product.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
                .withDeleteAction().build());
    }

}
