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

package org.meveo.export;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

import org.junit.Test;
import org.meveo.export.EntityExportImportService.ReusingReferenceByIdMarshallingStrategy;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.slf4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppReader;

import junit.framework.Assert;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
public class ExportTest {
	@Inject
	private Logger log;

    private Map<ExportTemplate, XStream> xstreams = new HashMap<ExportTemplate, XStream>();

    private Customer customer1 = null;

    /**
     * Test equals and hashmap contains/get operations on RelatedEntityToExport class
     */
    @Test
    public void testExport1() {

        Map<RelatedEntityToExport, String> map = new HashMap<RelatedEntityToExport, String>();

        RelatedEntityToExport ree1 = new RelatedEntityToExport(null, null, "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree2 = new RelatedEntityToExport(null, null, "select * from CustmerAcount", null, CustomerAccount.class);
        RelatedEntityToExport ree3 = new RelatedEntityToExport(null, null, "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree4 = new RelatedEntityToExport(null, null, "select * from Customerssss", null, Customer.class);
        RelatedEntityToExport ree5 = new RelatedEntityToExport("address", null, "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree6 = new RelatedEntityToExport("address", null, "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree7 = new RelatedEntityToExport("address", "name==5", "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree8 = new RelatedEntityToExport("address", "name==5", "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree9 = new RelatedEntityToExport("addresses", null, "select * from Customer", null, Customer.class);
        RelatedEntityToExport ree10 = new RelatedEntityToExport("address", "name==6", "select * from Customer", null, Customer.class);
        map.put(ree1, "val1");
        map.put(ree2, "val2");
        map.put(ree5, "val5");
        map.put(ree7, "val7");

        Assert.assertEquals("val1", map.get(ree1));
        Assert.assertEquals("val2", map.get(ree2));
        Assert.assertEquals("val1", map.get(ree3));
        Assert.assertNull(map.get(ree4));
        Assert.assertEquals("val5", map.get(ree5));
        Assert.assertEquals("val5", map.get(ree6));
        Assert.assertEquals("val7", map.get(ree7));
        Assert.assertEquals("val7", map.get(ree8));
        Assert.assertNull(map.get(ree9));
        Assert.assertNull(map.get(ree10));
    }

    /**
     * Just test/debug on how export/import template is serialized
     * 
     * @throws ClassNotFoundException class is not found
     * @throws IOException error when reading / writing a file on disk
     */
    // @Test
    public void testExport() throws ClassNotFoundException, IOException {

        StringWriter buffer = new StringWriter();

        // serialize
        HierarchicalStreamWriter writer = new PrettyPrintWriter(buffer);
        writer.startNode("meveoExport");
        writer.addAttribute("version", "4.1");

        XStream xstream = new XStream();

        xstream.alias("exportTemplate", ExportTemplate.class);
        xstream.alias("relatedEntity", RelatedEntityToExport.class);
        xstream.useAttributeFor(ExportTemplate.class, "name");
        xstream.useAttributeFor(ExportTemplate.class, "entityToExport");
        xstream.useAttributeFor(ExportTemplate.class, "canDeleteAfterExport");

        ExportTemplate template = new ExportTemplate();
        template.setName("Template One");

        List<String> filterList = new ArrayList<>();
        filterList.addAll(Arrays.asList("OfferTemplate", "ServiceTemplate", "ChargeTemplate", "OfferTemplateCategory", "ProductTemplate", "BundleTemplate", "PricePlanMatrix"));

        template.setFilters(new HashMap<>());
        template.getFilters().put("disabled", false);
        template.getFilters().put("appliesTo", "OfferTemplate");
        template.getFilters().put("appliesTo2", filterList);

        template.setRelatedEntities(new ArrayList<RelatedEntityToExport>());
        RelatedEntityToExport relent = new RelatedEntityToExport();
        relent.setSelection("select a from b where b=:b");
        relent.setParameters(new HashMap<String, String>());
        relent.getParameters().put("param1", "#{entity.ku}");
        template.getRelatedEntities().add(relent);
        xstream.marshal(template, writer);
        Assert.fail(xstream.toXML(template));
        Assert.fail(buffer.toString());

        if (1 / 1 == 1) {
            return;
        }
        writer.startNode("data");
        exportEntities("1", writer, template);
        exportEntities("2", writer, template);
        writer.endNode();

        template = new ExportTemplate();
        template.setName("Template Two");
        xstream.marshal(template, writer);

        writer.startNode("data");
        exportEntities("21", writer, template);
        exportEntities("22", writer, template);
        writer.endNode();

        writer.endNode();

        // ObjectOutputStream oos = xstream.createObjectOutputStream(writer);
        // oos.writeObject(customer);
        // oos.writeObject(custAccount);
        // oos.writeObject(custAccount2);
        // oos.close();

        HierarchicalStreamReader reader = new XppReader(new StringReader(buffer.toString()));

        String rootNode = reader.getNodeName();
        if (rootNode.equals("meveoExport")) {
        	log.info("Version : " + reader.getAttribute("version"));
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals("exportTemplate")) {
                ExportTemplate importTemplate = (ExportTemplate) xstream.unmarshal(reader);
                reader.moveUp();
                reader.moveDown();
                nodeName = reader.getNodeName();
                if (nodeName.equals("data")) {
                    importEntities(importTemplate, reader);
                }
                reader.moveUp();
            }
        }
    }

    private void importEntities(ExportTemplate template, HierarchicalStreamReader reader) {

        XStream xstream = xstreams.get(template);

        if (xstream == null) {
            xstream = new XStream();
            xstream.setMarshallingStrategy(new ReusingReferenceByIdMarshallingStrategy());
            xstream.aliasSystemAttribute("xsId", "id");
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            Object obj = xstream.unmarshal(reader);
            if (obj instanceof CustomerAccount) {
            	log.info("Object is " + obj.toString() + " cust is " + ((CustomerAccount) obj).getCustomer());

            } else if (obj instanceof Customer) {
                if (((Customer) obj).getCode().equals("Customer_1")) {
                    ((Customer) obj).setCode("KUKU");
                }

            } else {
            	log.info("Object is " + obj.toString());
            }
            reader.moveUp();
        }
    }

    private void exportEntities(String prefix, HierarchicalStreamWriter writer, ExportTemplate template) {

        XStream xstream = xstreams.get(template);

        if (xstream == null) {
            xstream = new XStream();
            xstream.setMarshallingStrategy(new ReusingReferenceByIdMarshallingStrategy());
            xstream.aliasSystemAttribute("xsId", "id");

            customer1 = new Customer();
            customer1.setCode("Customer_" + prefix);

            xstream.marshal(customer1, writer);
            xstreams.put(template, xstream);
        }

        List objects = new ArrayList();
        for (int i = 0; i < 2; i++) {

            Customer customer = new Customer();
            customer.setCode("Customer" + "_" + prefix + "_" + i);
            xstream.marshal(customer, writer);

            CustomerAccount custAccount = new CustomerAccount();
            custAccount.setCode("CA1" + "_" + prefix + "_" + i);
            custAccount.setCustomer(customer);
            xstream.marshal(custAccount, writer);

            CustomerAccount custAccount2 = new CustomerAccount();
            custAccount2.setCode("CA2" + "_" + prefix + "_" + i);
            custAccount2.setCustomer(customer1);
            xstream.marshal(custAccount2, writer);
            objects.add(custAccount2);
        }

        xstream.marshal(objects, writer);
    }
}