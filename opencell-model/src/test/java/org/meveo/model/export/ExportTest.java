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

package org.meveo.model.export;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

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

                String converted = format(writer.toString());
                String expected = format(IOUtils.toString(this.getClass().getResourceAsStream("/export/data_" + version + "_expected.xml")));

                assertThat(expected).isEqualTo(converted);
                Assert.assertEquals(changesetFile, expected, converted);

            } catch (Exception e) {
                log.error("Failed to convert file {}", version, e);
                Assert.fail();
            }
        }
    }

    private String format(String value) {
        return value.replaceAll(" />", "/>").replaceAll("(\\s{2,}|\\t|\\n)", "");
    }

}