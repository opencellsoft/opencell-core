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
        parentpath=parentpath+File.separator+"opencell-api";
        FileReader fr = null, md = null;
        BufferedReader lnr = null;
        String filePath = parentpath+File.separator+"target"+File.separator+"doc"+File.separator+"swagger"+File.separator+"swagger.yaml";
        String fileTempPath=filePath.replace(".yaml","Md.yaml"), str = "", tmp = "";
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileTempPath)));
        try {
            fr = new FileReader(filePath);
            lnr = new BufferedReader(fr);
            while ((str = lnr.readLine()) != null) {
                if (str.contains("@#")) {/*This is for the tag balise it have some a different type of identation compare to the introduction*/
                    str = str.replaceAll("'", "");
                    String[] tmp2 = str.split("(@#)");
                    tmp = mdLineReturn(tmp2[1],parentpath);
                    str=str.substring(0,str.indexOf(":"))+":"+ " |\n"+tmp+"\n  " +
                            "x-logo:\n" +
                            "    url: https://opencellsoft.com/img/logo-opencell-red.png\n" +
                            "    href: https://opencellsoft.com/\n" +
                            "    altText: \"Opencell Logo\"";
                }/*This is for the introduction balise it have some a different type of identation compare to the other one*/
                else if(str.contains("@%")){
                    str = str.replaceAll("'", "");
                    String[] tmp2 = str.split("(@%)");
                    tmp = mdLineReturnTag(tmp2[1],parentpath);
                    str=str.substring(0,str.indexOf(":"))+":"+ " |\n"+tmp+"";

                }
                writer.println(str);
            }
            writer.close();
            lnr.close();
        } catch (Exception e) {
            System.out.println("File not found");
            log.error("error = {}", e);
        }
    }

    public static String mdLineReturnTag(String nameFile,String parentpath) throws IOException {
        FileReader md = null;
        BufferedReader lnr = null;
        String filePath = parentpath+File.separator+"src"+File.separator+"test"+File.separator+"resources" +File.separator+"md" +File.separator+ nameFile + ".md";
        String returnline = "", str = "";
        try {
            md = new FileReader(filePath);
            lnr = new BufferedReader(md);
            while ((str = lnr.readLine()) != null) {
                    returnline = returnline + "      " + str + "  \n";
            }
            lnr.close();

        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println("File Not Found for "+nameFile);
            returnline="    @%"+nameFile;

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return returnline;
    }
    public static String mdLineReturn(String nameFile,String parentpath) throws IOException {
        FileReader md = null;
        BufferedReader lnr = null;
        String filePath = parentpath+File.separator+"src"+File.separator+"test"+File.separator+"resources" +File.separator+"md" +File.separator+ nameFile + ".md";
        String returnline = "", str = "";
        try {
            md = new FileReader(filePath);
            lnr = new BufferedReader(md);
            while ((str = lnr.readLine()) != null) {
                        returnline = returnline + "    " + str + "  \n";
            }
            lnr.close();

        } catch (FileNotFoundException e1) {
            //e1.printStackTrace();
            System.out.println("File not Found for "+nameFile);
            returnline="    @%"+nameFile;

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return returnline;
    }
}

