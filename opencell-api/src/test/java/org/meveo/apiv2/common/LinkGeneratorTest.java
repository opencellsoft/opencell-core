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

package org.meveo.apiv2.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.GenericResource;
import org.meveo.apiv2.ordering.product.ProductResource;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class LinkGeneratorTest {
    
    @Test
    public void testCreateSelfLinkToResource() {
        LinkGenerator.SelfLinkGenerator linkGenerator = new LinkGenerator.SelfLinkGenerator(ProductResource.class);
        Link link = linkGenerator.withPostAction().withGetAction().withDeleteAction().withPatchAction().withId(123L).build();
        
        assertEquals(link.getRel(), "self");
        assertTrue(link.getParams().get("actions").contains("POST"));
        assertTrue(link.getParams().get("actions").contains("GET"));
        assertTrue(link.getParams().get("actions").contains("DELETE"));
        assertTrue(link.getParams().get("actions").contains("PATCH"));
        assertEquals(link.getUri(), UriBuilder.fromResource(ProductResource.class).path("123").build());
    }
    
    @Test
    public void should_create_link_for_path_parametrized_resources() {
        LinkGenerator.SelfLinkGenerator linkGenerator = new LinkGenerator.SelfLinkGenerator(GenericResource.class);
        Link link = linkGenerator.withPostAction().withGetAction().build("customer", "13");
        assertEquals(link.getUri().toString(), "/generic/customer/13");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void should_throw_IAE_when_params_are_null() {
        LinkGenerator.SelfLinkGenerator linkGenerator = new LinkGenerator.SelfLinkGenerator(GenericResource.class);
        Link link = linkGenerator.withPostAction().withGetAction().build(null);
        assertEquals(link.getUri().toString(), "/generic/customer/13");
    }
    
      @Test(expected = IllegalArgumentException.class)
    public void should_throw_IAE_when_params_are_empty() {
        LinkGenerator.SelfLinkGenerator linkGenerator = new LinkGenerator.SelfLinkGenerator(GenericResource.class);
        Link link = linkGenerator.withPostAction().withGetAction().build(new String[]{});
        assertEquals(link.getUri().toString(), "/generic/customer/13");
    }
    
    @Test
    public void testResolveNextAndPreviousLinkToResource() {
        LinkGenerator.PaginationLinkGenerator linkGenerator = new LinkGenerator.PaginationLinkGenerator(ProductResource.class);
        List<Link> links = linkGenerator.offset(5L).limit(10L).total(100L).build();
        
        assertEquals(links.size(), 2);
        assertEquals(links.get(0).getRel(), "previous");
        assertEquals(links.get(1).getRel(), "next");
        assertEquals(links.get(0).getUri(), UriBuilder.fromResource(ProductResource.class).queryParam("offset", 0).queryParam("limit", 10).build());
        assertEquals(links.get(1).getUri(), UriBuilder.fromResource(ProductResource.class).queryParam("offset", 15).queryParam("limit", 10).build());
        
    }
    
    @Test
    public void testResolveOnlyNextLinkToResource() {
        LinkGenerator.PaginationLinkGenerator linkGenerator = new LinkGenerator.PaginationLinkGenerator(ProductResource.class);
        List<Link> links = linkGenerator.offset(0L).limit(10L).total(100L).build();
        
        assertEquals(links.size(), 1);
        assertEquals(links.get(0).getRel(), "next");
        assertEquals(links.get(0).getUri(), UriBuilder.fromResource(ProductResource.class).queryParam("offset", 10).queryParam("limit", 10).build());
        
    }
    
    @Test
    public void testResolveOnlyPreviousLinkToResource() {
        LinkGenerator.PaginationLinkGenerator linkGenerator = new LinkGenerator.PaginationLinkGenerator(ProductResource.class);
        List<Link> links = linkGenerator.offset(90L).limit(10L).total(95L).build();
        
        assertEquals(links.size(), 1);
        assertEquals(links.get(0).getRel(), "previous");
        assertEquals(links.get(0).getUri(), UriBuilder.fromResource(ProductResource.class).queryParam("offset", 80).queryParam("limit", 10).build());
    }
}