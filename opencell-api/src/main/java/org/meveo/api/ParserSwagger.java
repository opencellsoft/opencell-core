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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ParserSwagger {

    public static final String DEFAULT = "default";
    public static final String REGEX_SPACE = "[ ]{3,}";
    public static final String RS_JAVA = "Rs.java";
    public static final String CLASS = ".class";
    public static final String REGEX_ASTERISK = "[\\*]";
    public static final String PARAM_KEY = "param";
    public static final String PARAMETER_DESCRIPTION = "@Parameter(description=\"";
    private static ParserSwagger parsing = new ParserSwagger();
    private static final Logger log = LoggerFactory.getLogger(ParserSwagger.class);

 
    public static void main(String[] args) throws Exception {
        String parentpath = System.getProperty("user.dir");
        //Be careful of the parentpath the next line mus be present if you work on jenkins. Otherwise if you are working locally the next line should be in comment 
        parentpath=parentpath+File.separator+"opencell-api";
        log.info("Adding annotations to file:"+parentpath+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"org"+File.separator+"meveo"+File.separator+"api"+File.separator+"rest");
        String[] allPathFiles = parsing.pathRetriever(parentpath+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"org"+File.separator+"meveo"+File.separator+"api"+File.separator+"rest",
                RS_JAVA);
        parsing.processCreation(allPathFiles);
    }

    //Search All files that we have to add for annotation
    private String[] pathRetriever(String folderPath, String code) {
        List<String> returnListFilesPath = new ArrayList<>();
        List<String> orderFilesPath;
        File folder = new File(folderPath);
        parsing.listAllFiles(folder, returnListFilesPath);
        orderFilesPath = parsing.orderFilesPath(returnListFilesPath, code);
        String[] finalPathFiles = orderFilesPath.toArray(new String[orderFilesPath.size()]);
        return finalPathFiles;

    }
    //Will search all sub directory files and will add it to our list
    private void listAllFiles(File folder, List<String> returnListFilesPath) {
        File[] fileNames = folder.listFiles();
        for (File file : fileNames) {
            // if directory call the same method again
            if (file.isDirectory()) {
                listAllFiles(file, returnListFilesPath);
            } else {
                try {
                    returnListFilesPath.add(file.getCanonicalPath());
                } catch (IOException e) {
                    log.error("error = {}", e);
                }
            }
        }
    }
    //Keep the Files with a given String sequence
    private List<String> orderFilesPath(List<String> listFilesPath, String code) {
        List<String> returnListFilesClean = new ArrayList<>();
        String[] allPathFiles = listFilesPath.toArray(new String[listFilesPath.size()]);
        for (int i = 0; i < allPathFiles.length; i++) {
            String path = allPathFiles[i];
            if (path.contains(code)) {
                returnListFilesClean.add(path);
            }
        }
        return returnListFilesClean;
    }
    //Get the info of javadoc.In all files
    private void processCreation(String[] allPathFiles) throws Exception {
        boolean missingComment;
        List<String> missingCommentList = new ArrayList<String>();
        for (String path : allPathFiles) {
            File FILE_PATH = new File(path);
            missingComment = checkLength(FILE_PATH);//This is the process that determine if the ressource are missing element
            if (!missingComment) {
                fileReader(FILE_PATH, path, FILE_PATH.getName());
            } else if (missingComment) {
                String fileIndex = FILE_PATH.toString();
                missingCommentList.add(fileIndex);
            }
        }
        System.out.println("List of files with missing Comments:");
        missingCommentList.forEach(System.out::println);
    }
    //Function to check if missing comment by comparing the length of  the list of the declaration and the number of return type
    private boolean checkLength(File FILE_PATH) throws FileNotFoundException {
        String[] firtsList = javaDocCollector(FILE_PATH);
        String[] secondList = returnTypeDoc(FILE_PATH);
        if (firtsList.length == secondList.length) {
            return false;
        }
        return true;
    }
    //Use of javaparser for retrieving the JavaDocCollector
    private String[] javaDocCollector(File FILE_PATH) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
        List<String> javaDocNames = new ArrayList<>();
        VoidVisitor<List<String>> javaDocCollector = new JavaDocCollector();
        javaDocCollector.visit(cu, javaDocNames);
        String[] javadoc = javaDocNames.toArray(new String[javaDocNames.size()]);
        return javadoc;
    }
    //Use of javaparser for retrieving the  ReturnTypeCollector
    private String[] returnTypeDoc(File FILE_PATH) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
        List<String> returnNames = new ArrayList<>();
        VoidVisitor<List<String>> returnTypeCollector = new ReturnTypeCollector();
        returnTypeCollector.visit(cu, returnNames);
        String[] returnInfo = returnNames.toArray(new String[returnNames.size()]);
        return returnInfo;
    }
    //Use of javaparser for retrieving the DeclarationTypeCollector
    private String[] declarationDoc(File FILE_PATH) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
        List<String> declarationDoc = new ArrayList<>();
        VoidVisitor<List<String>> declarationCollector = new DeclarationTypeCollector();
        declarationCollector.visit(cu, declarationDoc);
        String[] declarationInfo = declarationDoc.toArray(new String[declarationDoc.size()]);
        return declarationInfo;
    }
    //Get the javadoc for the collector
    private static class JavaDocCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(JavadocComment md, List<String> collector) {
            super.visit(md, collector);
            String element = md.getContent();
            element = element.replaceAll("[ ]{5,}", "");
            if (!element.contains("@author") && !element.contains("Web service for managing")) {
                collector.add(element);
            }
        }
    }
    //Get the return type
    private static class ReturnTypeCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            String typeReturn = "* @type " + md.getType().asString() + CLASS;
            String fusion = typeReturn + "\n";
            collector.add(fusion);
        }
    }
    //Get the return type and the declaration
    private static class DeclarationTypeCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            String name = (md.getDeclarationAsString());
            collector.add(name);
        }
    }
    //Activate the process of swagger implementation
    private static String swaggerGeneration(String info, String urlEnd, boolean deprecatedtag, boolean typeProcessFlag) {
        String[] infoData = info.split("\\*{1}");
        for (String data : infoData) {
            if (data.length() > 8) {
                info = info + data + "";
            }
        }
        infoData = info.split("[@]{1}");
        info = operationGeneration(infoData, urlEnd, deprecatedtag, typeProcessFlag);
        return info;
    }
    //Will retrieve information for their specific location
    private static String operationGeneration(String[] info, String urlEnd, boolean deprecatedtag, boolean typeProcessFlag) {
        String operation;
        String summary = info[0];//Resume of the Api
        String description = info[0];//Description of the API
        String returnValue = info[info.length - 2].replace("return", "");//Description of the return
        String typeValue = info[info.length - 1].replace("type", "");//Return type
        if (info[0].length() > 150) {
            if (info[0].contains(".")) {
                summary = info[0].substring(0, info[0].indexOf('.'));
            } else {
                summary = info[0].substring(0, 150);
            }
        }
        operation = operationString(description, summary, returnValue, typeValue, urlEnd, deprecatedtag, typeProcessFlag);
        return operation;
    }
    //Will create the String for operation for the specific method with a given entry
    private static String operationString(String description, String summary, String returnValue, String typeValue, String urlEnd, boolean deprecatedtag, boolean typeProcessFlag) {
        description = description.replace("\n", "").replace("\r", "").replaceAll("(\")", "").replaceAll(REGEX_ASTERISK, "");
        summary = summary.replace("\n", "").replace("\r", "").replaceAll("(\")", "").replaceAll(REGEX_ASTERISK, "");
        returnValue = returnValue.replace("\n", "").replace("\r", "").replaceAll("(\")", "");
        String deprecated;
        if (typeValue.contains("<String>")) {//In one of the file <String> is inside the typeValue which will cause an error in the swagger
            typeValue = typeValue.replaceAll("(<String>)", "");
        }
        if (deprecatedtag) {
            deprecated = "\n\t\t\tdeprecated=true,";
        } else {
            deprecated = "";
        }
        if(typeProcessFlag){
            urlEnd="\n\t\t\toperationId=\""+urlEnd+"\",";
        }
        else{urlEnd="";
            log.error("ERROR");}
        String operationString = "\t@Operation(\n\t\t\tsummary=\"" +
                summary +
                "\",\n\t\t\tdescription=\"" +
                description +
                "\"," + deprecated+urlEnd + "\n\t\t\tresponses= {\n" +
                "\t\t\t\t@ApiResponse(description=\"" +
                returnValue +
                "\",\n" +
                "\t\t\t\t\t\tcontent=@Content(\n" +
                "\t\t\t\t\t\t\t\t\tschema=@Schema(\n" +
                "\t\t\t\t\t\t\t\t\t\t\timplementation=" +
                typeValue +
                "\t\t\t\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t\t\t\t\t)\n" +
                "\t\t\t\t)}\n" +
                "\t)";
        return operationString;
    }
    //Separate data of the comment block and return it.
    private static String[] separationData(String info) {
        String[] infoData = info.split("\\*{1}");
        for (String data : infoData) {
            if (data.length() > 8) {
                data = data.replace("*", "");
                info = info + data + "";
            }
        }
        infoData = info.split("[@]{1}");
        return infoData;
    }
    //Parse the file line by line and will add annotations depending of the line. With considering the file having bloc of comment.
    private void fileReader(File FILE_PATH, String filePath, String className) throws IOException {
        String filePathTemp = filePath.replaceAll(RS_JAVA, "Rs.txt");
        String[] infoOfMethod = javaDocCollector(FILE_PATH);
        String[] returnTypeInfo = returnTypeDoc(FILE_PATH);
        String[] declarationDoc = declarationDoc(FILE_PATH);
        BufferedReader lnr = null;
        String str, combinaison = "", urlEndString = "", typeProcess = "";
        boolean annotationHere = false, typeProcessFlag = false, swaggerAnnotationImport = false, deletedextraline = false, declarationflag = true, deprecatedtag = false, tagflag = false, importApparition = false, javadocstart = false, javadocend = false, publicinterfaceflag = false;
        int occuration = 0, indexDeclaration = 0;
        className = className.replaceAll(RS_JAVA, "");
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePathTemp)))) {
            lnr = new BufferedReader(new FileReader(filePath));
            log.info("[INFO] Adding annotation to " + className);
            //The case if the annotation are not present in the file
            while ((str = lnr.readLine()) != null && !annotationHere) {
                //This line for retrieving the return value for the next return and threfore find it when it arrive
                String tmp2 =
                        " " + returnTypeInfo[indexDeclaration].replace("\\*", "").replace(CLASS, "").replace("@type", "").replace(" ", "").replace("\n", "").replace("\r", "");
                if (str.contains("/*")) {
                    javadocstart = true;
                    typeProcess = "";
                    typeProcessFlag = false;
                } else if (str.contains("import io.swagger.v3.oas.annotations")) {
                    annotationHere = true;
                } else if (str.contains("*/")) {
                    javadocend = true;
                } else if (str.contains("@Deprecated") && javadocend && javadocstart) {//@Deprecated Flag
                    deprecatedtag = true;
                } else if (str.contains("public interface ")) {
                    publicinterfaceflag = true;
                } else if (str.contains("@GET") || str.contains("@PATCH") || str.contains("@PUT") || str.contains("@DELETE") || str.contains("@POST")) {
                    typeProcess = str;
                    typeProcessFlag = true;
                } else if (str.contains("@Path(")) {//Generation for the Swagger @operation

                    String tmp = str.replaceAll(REGEX_SPACE, "");

                    if (!tagflag && !publicinterfaceflag) {// This means is the first apparition of Path() Which for the tag
                        tagflag = true;
                        str = str + "\n@Tag(name = \"" + className + "\", description = \"@%" + className + "\")";
                    } else if (javadocstart && javadocend) {
                        urlEndString = tmp;
                        urlEndString = autoCompleteUrl(urlEndString, typeProcess, className);
                        combinaison = infoOfMethod[occuration] + returnTypeInfo[occuration];
                        if (tmp.split(" ").length > 1) {//this Block is here in case of @Path ActionStatus XXXXX() are on the same line
                            String[] tmpArray = tmp.split(" ");
                            str = pathIssueSolver(tmpArray, swaggerGeneration(combinaison, urlEndString, deprecatedtag, typeProcessFlag));
                        } else {
                            str = str.substring(0, str.indexOf(')')) + ")\n" + swaggerGeneration(combinaison, urlEndString, deprecatedtag, typeProcessFlag);
                        }
                        occuration++;
                    }
                    javadocend = false;
                    javadocstart = false;
                    deprecatedtag = false;
                } else if (str.contains("import ") && !swaggerAnnotationImport) {//Import libraries
                    swaggerAnnotationImport = true;
                    str = "import io.swagger.v3.oas.annotations.Operation;\n" + "import io.swagger.v3.oas.annotations.Parameter;\n"
                            + "import io.swagger.v3.oas.annotations.media.Content;\n" + "import io.swagger.v3.oas.annotations.media.Schema;\n"
                            + "import io.swagger.v3.oas.annotations.parameters.RequestBody;\n" + "import io.swagger.v3.oas.annotations.responses.ApiResponse;\n"
                            + "import io.swagger.v3.oas.annotations.tags.Tag;\n" + "import io.swagger.v3.oas.annotations.Hidden;\n\n" + str;
                } else if ((str.contains(tmp2) && declarationflag)
                        || deletedextraline) {//@Parameter generation in function (WE check either the tmp2 contains the good returntype or if we are already in the process of parameter generation)
                    if (str.contains(");") && deletedextraline) {
                        str = parameterGeneration(declarationDoc[occuration - 1], infoOfMethod[occuration - 1]);
                        if (indexDeclaration == declarationDoc.length - 1) {
                            declarationflag = false;
                        } else {
                            indexDeclaration++;
                        }
                        deletedextraline = false;
                    } else if (str.contains(");")) {
                        //the line already have
                        str = parameterGeneration(declarationDoc[occuration - 1], infoOfMethod[occuration - 1]);
                        if (indexDeclaration == declarationDoc.length - 1) {
                            declarationflag = false;
                        } else {
                            indexDeclaration++;
                        }
                        deletedextraline = false;
                    } else {//the function is on two line or more so we just delete line until we reach a ");"
                        str = "";
                        deletedextraline = true;
                    }
                }
                writer.println(str);
            }
            //The case if the annotation are already present in the file
            if (annotationHere) {
                writer.println(str);//We still have the last line in the buffer
                String summary = DEFAULT, description = DEFAULT, returnValue = DEFAULT, typeValue = DEFAULT;
                while ((str = lnr.readLine()) != null) {
                    String tmp2 = " " + returnTypeInfo[indexDeclaration].replace("\\*", "").replace(CLASS, "").replace("@type", "").replace(" ", "").replace("\n", "")
                            .replace("\r", "");
                    if (occuration < returnTypeInfo.length && str.contains("@Operation")) {//When meating the @Operation will save the data for the next line
                        combinaison = infoOfMethod[occuration] + returnTypeInfo[occuration];
                        String[] info = separationData(combinaison);
                        description = info[0];
                        returnValue = info[info.length - 2].replace("return", "");
                        typeValue = info[info.length - 1].replace("type", "").replace("\n", "");
                        description = description.replace("\n", "").replace("\r", "").replaceAll("(\")", "").replaceAll(REGEX_ASTERISK, "");
                        summary = description;
                        returnValue = returnValue.replace("\n", "").replace("\r", "").replaceAll("(\")", "");
                        if (summary.length() > 150) {
                            if (summary.contains(".")) {
                                summary = summary.substring(0, summary.indexOf('.'));
                            } else {
                                summary = summary.substring(0, 150);
                            }
                        }
                        if (typeValue.contains("<String>")) {
                            typeValue = typeValue.replaceAll("(<String>)", "");
                        }
                        occuration++;
                    }

                    if (str.contains("summary=")) {
                        str = "\t\t\tsummary=\"" + summary + "\",";
                    } else if (str.contains("@ApiResponse(description=")) {
                        str = "\t\t\t\t@ApiResponse(description=\"" + returnValue + "\",";
                    } else if (str.contains("implementation=")) {
                        str = "\t\t\t\t\t\t\t\t\t\t\timplementation=" + typeValue;
                    } else if ((str.contains(tmp2) && declarationflag) || deletedextraline) {
                        if (str.contains(");")) {
                            if (declarationDoc.length == 1 && occuration == 0) {
                                str = paramRewriting(declarationDoc[occuration], infoOfMethod[occuration]);
                            } else {
                                str = paramRewriting(declarationDoc[occuration - 1], infoOfMethod[occuration - 1]);
                            }
                            if (indexDeclaration == declarationDoc.length - 1) {
                                declarationflag = false;
                            } else {
                                indexDeclaration++;
                            }
                            deletedextraline = false;
                        } else {
                            str = "";
                            deletedextraline = true;
                        }
                    } else if (str.contains("description=")) {
                        str = "\t\t\tdescription=\"" + description + "\",";
                    } else if (str.contains("@Tag(name =")) {
                        str = "@Tag(name = \"" + className + "\", description = \"@%" + className + "\")";
                    } else if (str.contains("operationId=")) {
                        str = "\t\t\toperationId=\"" + urlEndString + "\",";
                    } else if (str.contains("@GET") || str.contains("@PATCH") || str.contains("@PUT") || str.contains("@DELETE") || str.contains("@POST")) {
                        typeProcess = str;
                        typeProcessFlag = true;
                    } else if (str.contains("@Path(") && typeProcessFlag) {
                        urlEndString = str.replaceAll(REGEX_SPACE, "");
                        urlEndString = autoCompleteUrl(urlEndString, typeProcess, className);
                    }
                    writer.println(str);
                }
            }
            writer.close();
            lnr.close();
        } catch (Exception e) {
            log.error("error = {}", e);
        } finally {
            Objects.requireNonNull(lnr).close();
        }
        File realName = new File(filePath);
        Files.delete(Paths.get(filePath));
        new File(filePathTemp).renameTo(realName);
    }
    //Will create a unique operation ID for the annotation
    private String autoCompleteUrl(String urlEndString, String typeProcess, String className) {
        urlEndString = urlEndString.replace("@Path(\"", "").replaceAll("\"", "").replace(")", "")
                .replace("/", "_").replace("[}]", "").replace("[{]", "")
                .replace(REGEX_SPACE, "").replace("\t", "");
        typeProcess = typeProcess.replace(REGEX_SPACE, "").replace("@", "").replace("\t", "");
        if (urlEndString.length() == 1) {
            if (typeProcess.contains("GET")) {
                urlEndString = urlEndString + "search";
            } else if (typeProcess.contains("POST")) {
                urlEndString = urlEndString + "create";
            } else if (typeProcess.contains("PUT")) {
                urlEndString = urlEndString + "update";
            } else if (typeProcess.contains("DELETE")) {
                urlEndString = urlEndString + "delete";
            } else if (typeProcess.contains("PATCH")) {
                urlEndString = urlEndString + "patch";
            } else {
                urlEndString = urlEndString + DEFAULT;
            }
        }
        urlEndString = typeProcess + "_" + className + urlEndString;
        return urlEndString;
    }
    //resolve the case of Path and function on the same line
    private static String pathIssueSolver(String[] dataArray, String dataOperation) {
        String goodpath = new String();
        for (int i = 0; i < dataArray.length; i++) {
            if (i == 1) {
                goodpath = goodpath + "\n" + dataOperation + "\n";
            } else if (i == 0) {
                goodpath = "   " + goodpath;
            }
            goodpath = goodpath + " " + dataArray[i];
        }
        return goodpath;
    }
    //Generate the description in the line of param method
    private String parameterGeneration(String change, String data) {
        String param = "";
        boolean noParam = true;
        //Retrieve of the param information
        data = data.replace("[*]", "").replace("\n", "").replace("\r", "").replace("[\"]", "")
                .replace(",", "");//We delete the *,\n,\r for cleaning purpose.We clean the data from ther [, and "] because it will cause issue and also [,] is a character that will act as balise.
        String[] arrayData = data.split("@");
        List<String> database = new ArrayList<>();
        for (String tmp : arrayData) {
            if (tmp.contains(PARAM_KEY)) {
                database.add(tmp);
                noParam = false;
            }
        }
        //Will add annotation Parameter depending of the case.
        if (!noParam) {//Case for at least one parameter in the method
            param = "    " + change.substring(0, change.indexOf("(")) + "(";
            int i = change.indexOf("(") + 1;
            change = change.substring(i, change.length());
            if (change.contains(",")) {//Multiple parameter for the method. We will split
                String[] methodArray = change.split(",");
                for (int j = 0; j < methodArray.length; j++) {
                        String tmp = database.get(j).replace(PARAM_KEY, "");
                    if (j < methodArray.length - 1) {
                        param = param + PARAMETER_DESCRIPTION + tmp + "\")" + methodArray[j] + " , ";
                    } else {
                        param = param + PARAMETER_DESCRIPTION + tmp + "\")" + methodArray[j];
                    }
                }
            } else {//Only one parameter in the method
                String tmp = database.get(0).replace("\n", "").replace("\r", "").replace(PARAM_KEY, "");
                param = param + PARAMETER_DESCRIPTION + tmp + "\")" + change;
            }
            param = param + ";";
        } else if (noParam) {//Case for no parameter method
            param = "    " + change + ";";
        }
        return param;
    }
    //This is the same function as before but just for the case if @parameter are already present in the annotation
    private String paramRewriting(String change, String data) {
        String param = "";
        boolean noParam = true;
        //Retrieve of the param information
        data = data.replaceAll("[*]", "").replace("\n", "").replace("\r", "")
                .replaceAll("[\"]", "").replace(",", "");
        String[] arrayData = data.split("@");
        List<String> database = new ArrayList<>();
        for (String tmp : arrayData) {
            if (tmp.contains(PARAM_KEY)) {
                database.add(tmp);
                noParam = false;
            }
        }
        if (!noParam) {//Case for at least one parameter in the method
            param = "    " + change.substring(0, change.indexOf("(")) + "(";
            int i = change.indexOf("(") + 1;
            change = change.substring(i, change.length());
            if (change.contains(",")) {//Multiple parameter for the method
                String[] methodArray = change.split(",");
                for (i = 0; i < methodArray.length; i++) {//This will clean the old @Parameter annotation
                    String tmp = methodArray[i].replaceAll(".+[\"].+[\"]", "");
                    methodArray[i] = tmp;
                }
                for (int j = 0; j < methodArray.length; j++) {
                    String tmp = database.get(j).replace(PARAM_KEY, "");//If an error occur because of a out of bound exception. Check the number of param in the comment and in the function. Because is probably due to a missing @param
                    if (j < methodArray.length - 1) {
                        param = param + PARAMETER_DESCRIPTION + tmp + "\"" + methodArray[j] + " , ";
                    } else {
                        param = param + PARAMETER_DESCRIPTION + tmp + "\"" + methodArray[j];
                    }
                }
            } else {//Only one parameter in the method
                String tmp = database.get(0).replace("\n", "").replace("\r", "").replace(PARAM_KEY, "");
                change = change.replaceAll(".+[\"].+[\"]", "");
                param = param + PARAMETER_DESCRIPTION + tmp + "\"" + change;
            }
            param = param + ";";
        } else if (noParam) {//Case for no parameter method
            param = "    " + change + ";";
        }
        return param;
    }
}