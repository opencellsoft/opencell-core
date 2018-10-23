package org.meveo.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.hibernate.annotations.Type;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class DbModelDocs {

    private static FieldVisitor fieldVisitor;

    public static void main(String[] args) {

        String modelDir = args[0];
        String outputDir = args[1];

        System.out.println("Will parse " + modelDir + " and write db documentation to " + outputDir);

        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(modelDir));
        javaParserTypeSolver.setParent(reflectionTypeSolver);
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        combinedSolver.add(javaParserTypeSolver);
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolSolver);

        new File(outputDir).mkdirs();

        File sources = new File(modelDir);

        List<File> files = FileUtils.listFiles(sources, new String[] { "java" }, true).stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());

        File dbDoc = new File(outputDir + File.separator + "dbModel.html");
        BufferedWriter writer = null;
        try {

            VoidVisitor<DBTable> classVisitor = new ClassVisitor();
            fieldVisitor = new FieldVisitor();

            List<DBTable> dbTables = new ArrayList<>();

            // Parse sources to determine DB tables and their fields
            for (File file : files) {

                CompilationUnit cu = JavaParser.parse(file);

                DBTable dbTable = new DBTable();
                classVisitor.visit(cu, dbTable);
                if (dbTable.classname != null) {
                    fieldVisitor.visit(cu, dbTable);
                    dbTables.add(dbTable);
                }
            }

            // Append embedded entity data in case of superclass, or nested embedded fields
            for (DBTable dbTable : dbTables) {
                if (dbTable.failedEmbededTypes == null) {
                    continue;
                }
                for (String embededType : dbTable.failedEmbededTypes) {
                    for (DBTable dbTableEmb : dbTables) {
                        if (dbTableEmb.isEmbedded && dbTableEmb.classname.equals(embededType)) {
                            dbTable.fields.addAll(dbTableEmb.fields);
                        }
                    }
                }
            }

            writer = new BufferedWriter(new FileWriter(dbDoc, false));

            // Write documentation to a file
            for (DBTable dbTable : dbTables) {
                if (dbTable.tablename != null) {
                    writer.write(dbTable.toHtml());
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static class ClassVisitor extends VoidVisitorAdapter<DBTable> {

        @Override
        public void visit(ClassOrInterfaceDeclaration cd, DBTable dbTable) {
            super.visit(cd, dbTable);

            // Interested only in table type entities
            if (cd.isAnnotationPresent("Table")) {

                AnnotationExpr tableAnnotation = cd.getAnnotationByClass(Table.class).get();
                dbTable.tablename = getStringParameter(tableAnnotation, "name");
                dbTable.classname = cd.getNameAsString();

                for (ClassOrInterfaceType extendsFromType : cd.getExtendedTypes()) {
                    System.out.println(dbTable.classname + " extends " + extendsFromType.getNameAsString());
                    ResolvedReferenceType resolvedSuperClass = extendsFromType.resolve();
                    try {

                        try {
                            for (ResolvedFieldDeclaration parentClassField : resolvedSuperClass.getAllFieldsVisibleToInheritors()) {
                                FieldDeclaration fd = ((JavaParserFieldDeclaration) parentClassField).getWrappedNode();
                                fieldVisitor.visit(fd, dbTable);
                            }
                        } catch (Exception e) {
                            System.out.println("Failed to resolve parent class fields for " + extendsFromType);
                            e.printStackTrace(System.out);
                        }

                    } catch (Exception e) {
                        System.out.println("Failed to resolve super class " + extendsFromType);
                    }
                }

                if (cd.getComment().isPresent()) {
                    dbTable.comment = cleanComments(cd.getComment().get().toString());
                }

                System.out.println("-----------------------------------------------------------");
                System.out.println(dbTable);

                // Suplements a parent table
            } else if (cd.isAnnotationPresent("DiscriminatorValue")) {

                System.out.println("AKK discriminator table " + cd.getNameAsString());

                // Embedded entities
            } else if (cd.isAnnotationPresent("Embeddable")) {

                dbTable.classname = cd.getNameAsString();
                dbTable.isEmbedded = true;
            }

        }
    }

    private static class FieldVisitor extends VoidVisitorAdapter<DBTable> {
        @Override
        public void visit(FieldDeclaration fd, DBTable dbTable) {
            super.visit(fd, dbTable);

            DBField dbField = new DBField();

            VariableDeclarator vd = fd.getVariable(0);

            if (fd.isAnnotationPresent(Transient.class) || fd.isAnnotationPresent(OneToMany.class) || fd.isStatic() || fd.isFinal()) {
                return;

            } else if (fd.isAnnotationPresent(CollectionTable.class)) {

                DBTable extraTable = new DBTable();
                extraTable.tablename = getStringParameter(fd.getAnnotationByClass(CollectionTable.class).get(), "name");
                if (fd.getComment().isPresent()) {
                    extraTable.comment = cleanComments(fd.getComment().get().toString());
                }

                extraTable.fields.add(new DBField(dbTable.classname.toLowerCase() + "_id", "Long"));
                extraTable.fields.add(new DBField(vd.getNameAsString(), "String"));
                extraTable.fields.add(new DBField(vd.getNameAsString() + "_key", "String"));
                return;

            } else if (fd.isAnnotationPresent(Column.class)) {
                dbField.dbFieldname = getStringParameter(fd.getAnnotationByClass(Column.class).get(), "name");
                dbField.nullable = getBooleanParameter(fd.getAnnotationByClass(Column.class).get(), "nullable", true);

            } else if ((fd.isAnnotationPresent(ManyToOne.class) || fd.isAnnotationPresent(OneToOne.class)) && fd.isAnnotationPresent(JoinColumn.class)) {
                dbField.dbFieldname = getStringParameter(fd.getAnnotationByClass(JoinColumn.class).get(), "name");
                dbField.dbFieldType = "Long";

            } else if (fd.isAnnotationPresent(Embedded.class)) {

                try {
                    Set<ResolvedFieldDeclaration> resolvedFields = vd.getType().resolve().asReferenceType().getDeclaredFields();

                    for (ResolvedFieldDeclaration field : resolvedFields) {
                        fieldVisitor.visit(((JavaParserFieldDeclaration) field).getWrappedNode(), dbTable);
                    }
                } catch (Exception e) {
                    // System.out.println("Failed to resolve fields for " + vd.getType() + " " + vd.getType().getClass());
                    // e.printStackTrace(System.out);
                    if (dbTable.failedEmbededTypes == null) {
                        dbTable.failedEmbededTypes = new ArrayList<>();
                    }
                    dbTable.failedEmbededTypes.add(vd.getType().toString());
                }

                return;
            }

            if (fd.isAnnotationPresent(Enumerated.class)) {
                dbField.dbFieldType = "String";

            } else if (fd.isAnnotationPresent(Type.class)) {
                if (getStringParameter(fd.getAnnotationByClass(Type.class).get(), "type").contains("json")) {
                    dbField.dbFieldType = "Json";
                }
            }
            if (fd.isAnnotationPresent(NotNull.class)) {
                dbField.nullable = false;
            }

            // System.out.println("Field: " + fd);

            dbField.fieldname = vd.getNameAsString();
            if (dbField.dbFieldType == null) {
                dbField.dbFieldType = vd.getTypeAsString();
            }
            if (vd.getInitializer().isPresent()) {
                dbField.dbFieldDefaultValue = vd.getInitializer().get().toString();
            }
            if (fd.getComment().isPresent()) {
                dbField.comment = cleanComments(fd.getComment().get().toString());
            }

            dbTable.fields.add(dbField);

            System.out.println(dbField);
        }

    }

    private static Expression getParameter(AnnotationExpr annotationExpr, String parameterName) {
        List<MemberValuePair> children = annotationExpr.getChildNodesByType(MemberValuePair.class);
        for (MemberValuePair memberValuePair : children) {
            if (parameterName.equals(memberValuePair.getNameAsString())) {
                return memberValuePair.getValue();
            }
        }
        return null;
    }

    private static String getStringParameter(AnnotationExpr annotationExpr, String parameterName) {

        StringLiteralExpr exp = (StringLiteralExpr) getParameter(annotationExpr, parameterName);
        if (exp != null) {
            return exp.getValue();
        }
        return null;
    }

    private static boolean getBooleanParameter(AnnotationExpr annotationExpr, String parameterName, boolean defaultValue) {

        BooleanLiteralExpr exp = (BooleanLiteralExpr) getParameter(annotationExpr, parameterName);
        if (exp != null) {
            return exp.getValue();
        }
        return defaultValue;
    }

    private static class DBTable {

        public String classname;
        public String tablename;
        public String comment;

        public List<DBField> fields = new ArrayList<>();
        public List<DBTable> extraTables = null;

        public List<String> failedEmbededTypes = null;
        public boolean isEmbedded = false;

        @Override
        public String toString() {
            return classname + "/" + tablename + (comment != null ? "\n" + comment : "");
        }

        public String toHtml() {
            String html = "<H1>" + classname + "</H1><br/><b>" + tablename + "</b>" + (comment != null ? "<br/><p>" + comment + "</p>" : "");
            html += "<table><tr><th>Field name</th><th>Db column name</th><th>Type</th><th>Default value</th><th>Required</th><th>Description</th></tr>";
            for (DBField field : fields) {
                html += field.toHtml();
            }

            html += "</table>";

            if (extraTables != null) {
                for (DBTable dbTable : extraTables) {
                    html += dbTable.toHtml();
                }
            }
            return html;
        }

    }

    private static class DBField {

        public String fieldname;
        public String dbFieldname;
        public String dbFieldType;
        public String dbFieldDefaultValue;
        public String comment;
        public boolean nullable = true;

        public DBField() {
        }

        public DBField(String dbFieldname, String dbFieldType) {
            super();
            this.dbFieldname = dbFieldname;
            this.dbFieldType = dbFieldType;
        }

        @Override
        public String toString() {
            return "-- " + fieldname + "/" + dbFieldname + "/" + dbFieldType + "/" + dbFieldDefaultValue + "/" + nullable + (comment != null ? "\n" + comment : "");
        }

        public String toHtml() {
            return "<tr><td>" + fieldname + "</td><td>" + dbFieldname + "</td><td>" + dbFieldType + "</td><td>" + (dbFieldDefaultValue != null ? dbFieldDefaultValue : "")
                    + "</td><td>" + (!nullable ? "yes" : "") + "</td><td>" + (comment != null ? comment : "") + "</td></tr>";
        }
    }

    private static String cleanComments(String comment) {
        if (comment == null) {
            return null;
        }

        // Remove @author and other tags
        int index = comment.indexOf("@");
        while (index > 0) {
            int end = comment.indexOf("*", index);
            if (end > index) {
                String newComment = comment.substring(0, index);
                newComment = newComment + comment.substring(end);
                comment = newComment;
            } else {
                break;
            }
            index = comment.indexOf("@");
        }

        comment = comment.replaceAll("//", "");

        comment = comment.replaceAll("/\\*\\*", "");
        comment = comment.replaceAll("/\\*", "");
        comment = comment.replaceAll("\\*/", "");
        comment = comment.replaceAll("\\*", "");

        return comment;
    }
}