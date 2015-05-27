package org.meveo.export;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.meveo.export.EntityExportImportService.ReusingReferenceByIdMarshallingStrategy;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppReader;

public class ExportTest {

    private Map<ExportTemplate, XStream> xstreams = new HashMap<ExportTemplate, XStream>();

    private Customer customer1 = null;

    @Test
    public void testExport() throws ClassNotFoundException, IOException {

        StringWriter buffer = new StringWriter();

        // serialize
        HierarchicalStreamWriter writer = new PrettyPrintWriter(buffer);
        writer.startNode("meveoExport");
        writer.addAttribute("version", "4.1");

        XStream xstream = new XStream();

        xstream.alias("exportTemplate", ExportTemplate.class);
        xstream.useAttributeFor(ExportTemplate.class, "name");
        xstream.useAttributeFor(ExportTemplate.class, "entityToExport");
        xstream.useAttributeFor(ExportTemplate.class, "canDeleteAfterExport");

        ExportTemplate template = new ExportTemplate();
        template.setName("Template One");
        xstream.marshal(template, writer);

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

        System.out.println(buffer.toString());

        // ObjectOutputStream oos = xstream.createObjectOutputStream(writer);
        // oos.writeObject(customer);
        // oos.writeObject(custAccount);
        // oos.writeObject(custAccount2);
        // oos.close();
        //
        // System.out.println(buffer.toString());

        HierarchicalStreamReader reader = new XppReader(new StringReader(buffer.toString()));

        String rootNode = reader.getNodeName();
        if (rootNode.equals("meveoExport")) {
            System.out.println("Version " + reader.getAttribute("version"));
        }

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String nodeName = reader.getNodeName();
            if (nodeName.equals("exportTemplate")) {
                ExportTemplate importTemplate = (ExportTemplate) xstream.unmarshal(reader);
                reader.moveUp();
                reader.moveDown();
                nodeName = reader.getNodeName();
                if (nodeName.equals("data")){
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
                System.out.println("Object is " + obj.toString() + " cust is " + ((CustomerAccount) obj).getCustomer());
            
            } else if (obj instanceof Customer){
                if (((Customer)obj).getCode().equals("Customer_1")){
                    ((Customer)obj).setCode("KUKU");
                }
                
                
            } else {
                System.out.println("Object is " + obj.toString());
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