package org.meveo.api;

import java.io.*;
import java.util.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class ParserSwagger {

    private static ParserSwagger parsing = new ParserSwagger();

    public static void main(String[] args) throws Exception {
        String parentpath=System.getProperty("user.dir");
        String[] allPathFiles = parsing.pathRetriever(parentpath+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"org"+File.separator+"meveo"+File.separator+"api"+File.separator+"rest", "Rs.java");
        System.out.println("Adding annotations to file: src/main/java/org/meveo/api/rest");
        parsing.getAllInfo(allPathFiles);
    }

    //Get the javadoc for the collector
    private static class JavaDocCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(JavadocComment md, List<String> collector) {
            super.visit(md, collector);
            String element = md.getContent();
            element = element.replaceAll("[ ]{5,}", "");
            if (element.contains("@author") || element.contains("Web service for managing")) {
            } else {
                collector.add(element);
            }
        }
    }

    //Get the return type
    private static class ReturnTypeCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            String typeReturn = "* @type " + md.getType().asString() + ".class";
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

    //Search All file that we need
    private String[] pathRetriever(String folderPath, String code) {
        List<String> returnListFilesPath = new ArrayList<>();
        List<String> cleanFilesPath;
        File folder = new File(folderPath);
        parsing.listAllFiles(folder, returnListFilesPath);
        cleanFilesPath = parsing.cleanFilesPath(returnListFilesPath, code);
        String[] finalPathFiles = cleanFilesPath.toArray(new String[cleanFilesPath.size()]);
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
                    readFileNames(file, returnListFilesPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void readFileNames(File file, List<String> returnListFilesPath) throws IOException {
        returnListFilesPath.add(file.getCanonicalPath());
    }

    //Retrieve the Files with a given String sequence
    private List<String> cleanFilesPath(List<String> listFilesPath, String code) {
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
    private void getAllInfo(String[] allPathFiles) throws Exception {
        boolean missingComment;
        List<String> missingCommentList = new ArrayList<String>();
        for (String path : allPathFiles) {
            //System.out.println(path);
            File FILE_PATH = new File(path);
            missingComment = checkLength(FILE_PATH);
            if (!missingComment) {
                fileReader(FILE_PATH,path, FILE_PATH.getName());
            } else if (missingComment) {
                defaultReaderGeneration(path, FILE_PATH.getName());
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

    private String[] javaDocCollector(File FILE_PATH) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
        List<String> javaDocNames = new ArrayList<>();
        VoidVisitor<List<String>> javaDocCollector = new JavaDocCollector();
        javaDocCollector.visit(cu, javaDocNames);
        String[] javadoc = javaDocNames.toArray(new String[javaDocNames.size()]);
        return javadoc;
    }

    private String[] returnTypeDoc(File FILE_PATH) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
        List<String> returnNames = new ArrayList<>();
        VoidVisitor<List<String>> returnTypeCollector = new ReturnTypeCollector();
        returnTypeCollector.visit(cu, returnNames);
        String[] returnInfo = returnNames.toArray(new String[returnNames.size()]);
        return returnInfo;
    }

    private String[] declarationDoc(File FILE_PATH) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(FILE_PATH);
        List<String> declarationDoc = new ArrayList<>();
        VoidVisitor<List<String>> declarationCollector = new DeclarationTypeCollector();
        declarationCollector.visit(cu, declarationDoc);
        String[] declarationInfo = declarationDoc.toArray(new String[declarationDoc.size()]);
        return declarationInfo;
    }

    private static String swaggerGeneration(String info, boolean deprecatedtag) {
        String[] infoData = info.split("\\*{1}");
        for (String data : infoData) {
            if (data.length() > 8) {
                info = info + data + "";
            }
        }
        infoData = info.split("[@]{1}");
        info = operationGeneration(infoData, deprecatedtag);
        return info;
    }

    //Will retrieve information for their specific location
    private static String operationGeneration(String[] info, boolean deprecatedtag) {
        String operation;
        String summary = info[0];
        String description = info[0];
        String returnValue = info[info.length - 2].replaceAll("return", "");
        String typeValue = info[info.length - 1].replaceAll("type", "");
        if (info[0].length() > 150) {
            if (info[0].contains(".")) {
                summary = info[0].substring(0, info[0].indexOf("."));
            } else {
                summary = info[0].substring(0, 150);
            }
        }
        operation = operationString(description, summary, returnValue, typeValue, deprecatedtag);
        return operation;
    }

    //Will create the String for operation for the specific method with a given entry
    private static String operationString(String description, String summary, String returnValue, String typeValue, boolean deprecatedtag) {
        description = description.replace("\n", "").replace("\r", "").replaceAll("(\")", "").replaceAll("[\\*]","");
        summary = summary.replace("\n", "").replace("\r", "").replaceAll("(\")", "").replaceAll("[\\*]","");
        returnValue = returnValue.replace("\n", "").replace("\r", "").replaceAll("(\")", "");
        String deprecated;
        if (typeValue.contains("<String>")) {
            typeValue = typeValue.replaceAll("(<String>)", "");
        }

        if (deprecatedtag) {
            deprecated = "\n\t\t\tdeprecated=true,";
        } else {
            deprecated = "";
        }
        String operationString = "\t@Operation(\n\t\t\tsummary=\"" +
                summary +
                "\",\n\t\t\tdescription=\"" +
                description +
                "\"," + deprecated + "\n\t\t\tresponses= {\n" +
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
    //Parse the file and will do stuff depending of the line. With considering the file having bloc of comment
    private void fileReader(File FILE_PATH,String filePath, String className) throws IOException {
        String filePathTemp = filePath.replaceAll("Rs.java", "Rs.txt");
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePathTemp)));
        String[] infoOfMethod = javaDocCollector(FILE_PATH);
        String[] returnTypeInfo = returnTypeDoc(FILE_PATH);
        String[] declarationDoc = declarationDoc(FILE_PATH);
        FileReader fr = null;
        BufferedReader lnr = null;
        String str, combinaison;
        boolean swaggerAnnotationImport = false, deletedextraline = false, declarationflag = true, deprecatedtag = false, tagflag = false, importApparition = false, javadocstart = false, javadocend = false, publicinterfaceflag = false;
        int occuration = 0, indexDeclaration = 0;
        className = className.replaceAll("Rs.java", "");
        try {
            fr = new FileReader(filePath);
            lnr = new BufferedReader(fr);

            while ((str = lnr.readLine()) != null) {
                String tmp2 = "    " + returnTypeInfo[indexDeclaration].replaceAll("\\*", "").replaceAll(".class", "").replaceAll("@type", "").replaceAll(" ", "").replace("\n", "").replace("\r", "");
                if (str.contains("/*")) {
                    javadocstart = true;
                } else if (str.contains("*/")) {
                    javadocend = true;
                } else if (str.contains("@Deprecated") && javadocend && javadocstart) {
                    deprecatedtag = true;
                } else if (str.contains("public interface ")) {
                    publicinterfaceflag = true;
                } else if (str.contains("@Path(")) {
                    String tmp = str.replaceAll("[ ]{3,}", "");
                    if (tmp.split(" ").length > 1) {
                        String[] tmpArray = tmp.split(" ");
                        if (javadocstart && javadocend) {
                            combinaison = infoOfMethod[occuration] + returnTypeInfo[occuration];
                            str = pathIssueSolver(tmpArray, swaggerGeneration(combinaison, deprecatedtag));
                            occuration++;
                        }
                    } else {
                        if (!tagflag && !publicinterfaceflag) {
                            tagflag = true;
                            str = str + "\n@Tag(name = \"" + className + "\", description = \"All command api for " + className + " :\")";
                        } else if (javadocstart && javadocend) {
                            combinaison = infoOfMethod[occuration] + returnTypeInfo[occuration];
                            str = str.substring(0, str.indexOf(")")) + ")\n" + swaggerGeneration(combinaison, deprecatedtag);
                            occuration++;
                        }
                        javadocend = false;
                        javadocstart = false;
                        deprecatedtag = false;
                    }
                } else if (str.contains("import ") && !swaggerAnnotationImport) {
                    importApparition = true;
                } else if (importApparition && !swaggerAnnotationImport) {
                    swaggerAnnotationImport = true;
                    str = "\n" +
                            "import io.swagger.v3.oas.annotations.Operation;\n" +
                            "import io.swagger.v3.oas.annotations.Parameter;\n" +
                            "import io.swagger.v3.oas.annotations.media.Content;\n" +
                            "import io.swagger.v3.oas.annotations.media.Schema;\n" +
                            "import io.swagger.v3.oas.annotations.parameters.RequestBody;\n" +
                            "import io.swagger.v3.oas.annotations.responses.ApiResponse;\n" +
                            "import io.swagger.v3.oas.annotations.tags.Tag;\n"+
                            "import io.swagger.v3.oas.annotations.Hidden;\n";
                }
                writer.println(str);
            }
            writer.close();
            lnr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File realName = new File(filePath);
        if (realName.delete()) {
            //System.out.println("");
        } 
        new File(filePathTemp).renameTo(realName);

    }
    //Parse and replace the line by other information. This is for the case of missing comment in file
    private void defaultReaderGeneration(String filePath, String classNameTag) throws IOException {
        String filePathTemp = filePath.replaceAll("Rs.java", "Rs.txt");
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePathTemp)));
        File FILE_PATH = new File(filePath);
        String[] infoOfMethod = javaDocCollector(FILE_PATH);
        String[] returnTypeInfo = returnTypeDoc(FILE_PATH);
        String[] declarationDoc = declarationDoc(FILE_PATH);
        FileReader fr = null;
        BufferedReader lnr = null;
        String str, combinaison;
        int indexjavadoc = 0, indexreturntype = 0, indexDeclaration = 0;
        boolean swaggerAnnotationImport = false, deletedextraline = false, declarationflag = true, deprecatedtag = false, tagflag = false, importApparition = false, javadocstart = false, javadocend = false, publicinterfaceflag = false;
        classNameTag = classNameTag.replaceAll("Rs.java", "");
        try {
            // create new reader
            fr = new FileReader(filePath);
            lnr = new BufferedReader(fr);
            // read lines till the end of the stream
            while ((str = lnr.readLine()) != null) {

                if (str.contains("/*")) {
                    javadocstart = true;
                } else if (str.contains("*/")) {
                    javadocend = true;
                } else if (str.length() < 4 && javadocend && javadocstart) {
                    javadocend = false;
                    javadocstart = false;
                } else if (str.contains("@Deprecated") && javadocend && javadocstart) {
                    deprecatedtag = true;
                } else if (str.contains("public interface ")) {
                    publicinterfaceflag = true;
                } else if (str.contains(declarationDoc[indexDeclaration]) && declarationflag) {
                    indexDeclaration++;
                    if (indexDeclaration == declarationDoc.length - 1) {
                        declarationflag = false;
                    }
                }
                else if(str.contains("@Path(\"/user\")")){
                    str=str+"\n@Hidden";
                }
                else if (str.contains("@Path(")) {

                    String tmp = str.replaceAll("[ ]{3,}", "");
                    if (tmp.split(" ").length > 1) {
                        String[] tmpArray = tmp.split(" ");
                        if (javadocstart && javadocend) {
                            combinaison = infoOfMethod[indexjavadoc] + returnTypeInfo[indexreturntype];
                            str = pathIssueSolver(tmpArray, swaggerGeneration(combinaison, deprecatedtag));
                            indexjavadoc++;
                            indexreturntype++;
                        } else {
                            str = pathIssueSolver(tmpArray, operationString(str, str, returnTypeInfo[indexreturntype], returnTypeInfo[indexreturntype].replaceAll("\\* @type ", ""), deprecatedtag));
                            indexreturntype++;
                        }
                        javadocend = false;
                        javadocstart = false;
                        deprecatedtag = false;
                    } else {
                        if (!tagflag && !publicinterfaceflag) {
                            tagflag = true;
                            str = str + "\n@Tag(name = \"" + classNameTag + "\", description = \"All command api for " + classNameTag + " :\")";
                        } else if (javadocstart && javadocend) {
                            combinaison = infoOfMethod[indexjavadoc] + returnTypeInfo[indexreturntype];
                            str = str.substring(0, str.indexOf(")")) + ")\n" + swaggerGeneration(combinaison, deprecatedtag);
                            indexjavadoc++;
                            indexreturntype++;
                        } else {
                            str = str + "\n" + operationString(str, str, returnTypeInfo[indexreturntype].replaceAll(".class", " data."), returnTypeInfo[indexreturntype].replaceAll("\\* @type ", ""), deprecatedtag);
                            indexreturntype++;
                        }
                        javadocend = false;
                        javadocstart = false;
                        deprecatedtag = false;
                    }
                } else if (str.contains("import ") && !swaggerAnnotationImport) {
                    importApparition = true;
                } else if (importApparition && !swaggerAnnotationImport) {
                    swaggerAnnotationImport = true;
                    str = "\n" +
                            "import io.swagger.v3.oas.annotations.Operation;\n" +
                            "import io.swagger.v3.oas.annotations.Parameter;\n" +
                            "import io.swagger.v3.oas.annotations.media.Content;\n" +
                            "import io.swagger.v3.oas.annotations.media.Schema;\n" +
                            "import io.swagger.v3.oas.annotations.parameters.RequestBody;\n" +
                            "import io.swagger.v3.oas.annotations.responses.ApiResponse;\n" +
                            "import io.swagger.v3.oas.annotations.tags.Tag;\n"+
                            "import io.swagger.v3.oas.annotations.Hidden;\n";
                }
                writer.println(str);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        writer.close();
        lnr.close();
        File realName = new File(filePath);
        if (realName.delete()) {
            //System.out.print("");
        } 
        new File(filePathTemp).renameTo(realName);
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
}

