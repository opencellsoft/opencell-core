package org.meveo.model.export;

import java.io.StringWriter;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportTest {

    @Test
    public void testExportTransformation() {

        Logger log = LoggerFactory.getLogger(this.getClass());

        Set<String> changesets = new Reflections("exportVersions", new ResourcesScanner()).getResources(Pattern.compile("changeSet_.*\\.xslt"));

        for (String changesetFile : changesets) {
            String version = changesetFile.substring(changesetFile.indexOf("_") + 1, changesetFile.indexOf(".xslt"));

            try {
                StringWriter writer = new StringWriter();
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(this.getClass().getResourceAsStream("/" + changesetFile)));
                transformer.setParameter("version", version);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(new StreamSource(this.getClass().getResourceAsStream("/export/data_" + version + ".xml")), new StreamResult(writer));

                String converted = writer.toString().replaceAll(" />", "/>");
                String expected = IOUtils.toString(this.getClass().getResourceAsStream("/export/data_" + version + "_expected.xml")).replaceAll(" />", "/>");

               // Assert.assertEquals(expected, converted);

            } catch (Exception e) {
                log.error("Failed to convert file {}", version, e);
                Assert.fail();
            }
        }
    }
}