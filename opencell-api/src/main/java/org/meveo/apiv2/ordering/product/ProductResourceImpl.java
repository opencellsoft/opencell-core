package org.meveo.apiv2.ordering.product;

import org.meveo.apiv2.common.LinkGenerator;
import org.meveo.apiv2.models.ImmutableProduct;
import org.meveo.apiv2.models.ImmutableProducts;
import org.meveo.apiv2.models.Product;
import org.meveo.apiv2.models.Products;
import org.meveo.apiv2.services.ProductService;
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
    private ProductService productService;

    @Override
    public Response getProducts(Long offset, Long limit, String sort, String orderBy, String filter, Request request) {
        List<ProductTemplate> productTemplates = productService.list(offset, limit, sort, orderBy, filter);

        EntityTag etag = new EntityTag(Integer.toString(productTemplates.hashCode()));
        CacheControl cc = new CacheControl();
        cc.setMaxAge(1000);
        Response.ResponseBuilder builder = request.evaluatePreconditions(etag);
        if (builder != null) {
            builder.cacheControl(cc);
            return builder.build();
        }

        ImmutableProduct[] productList = productTemplates
                .stream()
                .map(productTemplate -> toResourceProductWithLink(ProductMapper.toProduct(productTemplate)))
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
                            .entity(toResourceProductWithLink(ProductMapper.toProduct(productTemplate))).build();
                })
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Response createProduct(Product product) {
        ProductTemplate productTemplate = productService.create(ProductMapper.toProductTemplate(product));
        return Response
                .created(LinkGenerator.getUriBuilderFromResource(ProductResource.class, productTemplate.getId()).build())
                .entity(toResourceProductWithLink(ProductMapper.toProduct(productTemplate)))
                .build();
    }

    @Override
    public Response updateProduct(Long id, Product product) {
        return productService.update(id, ProductMapper.toProductTemplate(product))
        .map(productTemplate -> {
            if(product.getImageUrl() != null){
                productService.saveImage(productTemplate, product.getImageUrl(),product.getImage64());
            }
            return Response.ok().entity(toResourceProductWithLink(ProductMapper.toProduct(productTemplate))).build();
        }).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response patchProduct(Long id, Product product) {
        return productService.patch(id, ProductMapper.toProductTemplate(product))
                .map(productTemplate -> {
                    if(product.getImageUrl() != null){
                        productService.saveImage(productTemplate, product.getImageUrl(),product.getImage64());
                    }
                    return Response.ok().entity(toResourceProductWithLink(ProductMapper.toProduct(productTemplate))).build();
                }).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response deleteProduct(Long id) {
        return productService.delete(id).map(productTemplate ->
                Response.ok().entity(toResourceProductWithLink(ProductMapper.toProduct(productTemplate))).build())
                .orElseThrow(NotFoundException::new);
    }
    // TODO : move to mapper
    private ImmutableProduct toResourceProductWithLink(ImmutableProduct product) {
        return product.withLinks(new LinkGenerator.SelfLinkGenerator(ProductResource.class)
                .withId(product.getId()).withGetAction().withPostAction().withPutAction().withPatchAction()
                .withDeleteAction().build());
    }

}
