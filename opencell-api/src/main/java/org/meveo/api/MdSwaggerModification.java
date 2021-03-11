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

package org.meveo.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MdSwaggerModification {
	
	private static final Logger log = LoggerFactory.getLogger(MdSwaggerModification.class);


    public static void main(String[] args) throws Exception {
        String parentpath = System.getProperty("user.dir");
        parentpath = parentpath + File.separator + "opencell-api";
        String filePath = parentpath + File.separator + "target" + File.separator + "doc" + File.separator + "swagger" + File.separator + "swagger.yaml";
        String fileTempPath = filePath.replace(".yaml", "Md.yaml"), str = "", tmp = "";
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileTempPath)));
             BufferedReader lnr = new BufferedReader(new FileReader(filePath))) {
            while ((str = lnr.readLine()) != null) {
                if (str.contains("@#")) {/*This is for the tag balise it have some a different type of identation compare to the introduction*/
                    str = str.replace("'", "");
                    String[] tmp2 = str.split("(@#)");
                    tmp = mdLineReturn(tmp2[1], parentpath);
                    str = str.substring(0, str.indexOf(":")) + ":" + " |\n" + tmp + "\n  " + "x-logo:\n" + "    url: https://opencellsoft.com/img/logo-opencell-red.png\n"
                            + "    href: https://opencellsoft.com/\n" + "    altText: \"Opencell Logo\"";
                }/*This is for the introduction balise it have some a different type of identation compare to the other one*/ else if (str.contains("@%")) {
                    str = str.replace("'", "");
                    String[] tmp2 = str.split("(@%)");
                    tmp = mdLineReturnTag(tmp2[1], parentpath);
                    str = str.substring(0, str.indexOf(":")) + ":" + " |\n" + tmp + "";

                }
                writer.println(str);
            }
        } catch (Exception e) {
            System.out.println("File not found");
            log.error("error = {}", e);
        }
    }

    public static String mdLineReturnTag(String nameFile, String parentpath) throws IOException {
        String filePath = parentpath + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "md" + File.separator + nameFile + ".md";
        String returnline = "", str = "";
        try (BufferedReader lnr = new BufferedReader(new FileReader(filePath))) {
            while ((str = lnr.readLine()) != null) {
                returnline = returnline + "      " + str + "  \n";
            }
        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println("File Not Found for " + nameFile);
            returnline = "    @%" + nameFile;

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return returnline;
    }

    public static String mdLineReturn(String nameFile, String parentpath) throws IOException {
        String filePath = parentpath + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "md" + File.separator + nameFile + ".md";
        String returnline = "", str = "";
        try (BufferedReader lnr = new BufferedReader(new FileReader(filePath))) {
            while ((str = lnr.readLine()) != null) {
                returnline = returnline + "    " + str + "  \n";
            }
        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println("File not Found for " + nameFile);
            returnline = "    @%" + nameFile;

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return returnline;
    }
}

