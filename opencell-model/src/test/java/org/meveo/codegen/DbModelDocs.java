package org.meveo.codegen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
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
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserClassDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

/**
 * Produces data/DB model documentation by parsing source files
 */
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

        BufferedWriter writer = null;

        VoidVisitor<DBTable> classVisitor = new ClassVisitor();
        fieldVisitor = new FieldVisitor();

        try {
            List<File> files = FileUtils.listFiles(sources, new String[] { "java" }, true).stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());

            File dbDoc = new File(outputDir + File.separator + "dbModel.html");

            List<DBTable> dbTables = new ArrayList<>();

            // Parse sources to determine DB tables and their fields
            for (File file : files) {

                CompilationUnit cu = JavaParser.parse(file);

                // Parse class. If class is of no interest - does not have a table- do not set a classname value and it will be disregarded
                DBTable dbTable = new DBTable();
                classVisitor.visit(cu, dbTable);

                // Add table to a list of tables and parse fields
                if (dbTable.classname != null) {
                    dbTables.add(dbTable);

                    fieldVisitor.visit(cu, dbTable);
                }
            }

            // Append embedded entity data in case of superclass with nested embedded fields
            // Append fields to a parent entity in case of Inheritance.SINGLE inheritance strategy
            for (DBTable dbTable : dbTables) {
                if (dbTable.parentClassname != null) {
                    for (DBTable dbTableParent : dbTables) {
                        if (dbTableParent.classname.equals(dbTable.parentClassname)) {
                            dbTableParent.addUniqueFields(dbTable.fields);
                            if (dbTable.extraTables != null) {
                                dbTableParent.addExtraTables(dbTable.extraTables);
                            }
                        }
                    }
                }
                if (dbTable.failedEmbeddedTypes != null) {
                    for (String embeddedType : dbTable.failedEmbeddedTypes) {
                        for (DBTable dbTableEmb : dbTables) {
                            if (dbTableEmb.isEmbedded && dbTableEmb.classname.equals(embeddedType)) {
                                dbTable.fields.addAll(dbTableEmb.fields);
                            }
                        }
                    }
                }
            }

            writer = new BufferedWriter(new FileWriter(dbDoc, false));

            // Write documentation to a file
            for (DBTable dbTable : dbTables) {
                if (dbTable.tablename != null) {
                    dbTable.fields.sort(Comparator.comparing(DBField::getFieldname));
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

    /**
     * Class inspector. Retrieves table information.
     */
    private static class ClassVisitor extends VoidVisitorAdapter<DBTable> {

        @Override
        public void visit(ClassOrInterfaceDeclaration cd, DBTable dbTable) {
            super.visit(cd, dbTable);

            // Interested only in table type entities
            if (cd.isAnnotationPresent("Table")) {

                AnnotationExpr tableAnnotation = cd.getAnnotationByClass(Table.class).get();
                Optional<AnnotationExpr> discriminatorValueAnnotation = cd.getAnnotationByClass(DiscriminatorValue.class);

                dbTable.tablename = getStringParameter(tableAnnotation, "name");
                dbTable.classname = cd.getNameAsString();

                // Inspect parent classes and add fields if applicable
                for (ClassOrInterfaceType extendsFromType : cd.getExtendedTypes()) {
                    // System.out.println(dbTable.classname + " extends " + extendsFromType.getNameAsString());
                    ResolvedReferenceType resolvedSuperClass = extendsFromType.resolve();

                    // Need to check if parent entity has fields in a separate or same table
                    // Covers Inheritance.JOINED inheritance strategy
                    if (resolvedSuperClass.getTypeDeclaration() instanceof JavaParserClassDeclaration) {

                        JavaParserClassDeclaration parentClassDeclaration = (JavaParserClassDeclaration) resolvedSuperClass.getTypeDeclaration();
                        Optional<AnnotationExpr> inheritanceAnnotation = parentClassDeclaration.getWrappedNode().getAnnotationByClass(Inheritance.class);

                        if (inheritanceAnnotation.isPresent()) {
                            String inheritanceStrategy = getStringParameter(inheritanceAnnotation.get(), "strategy");

                            // In Joined type strategy parent entity stores fields in its own table. An ID field is added
                            if (inheritanceStrategy.contains("JOINED")) {
                                dbTable.fields.add(new DBField("id", "id", "Long", null, "Identifier", false));
                                continue;
                            }
                        }
                    }

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
                    dbTable.setComment(cd.getComment().get().toString());
                }

                // System.out.println("-----------------------------------------------------------");
                // System.out.println(dbTable);

                // Supplements a parent table with inheritance.SINGLE type of inheritance. Fields will be added to parent table in later iteration.
            } else if (cd.isAnnotationPresent("DiscriminatorValue")) {

                // Inspect parent classes and add fields if applicable
                for (ClassOrInterfaceType extendsFromType : cd.getExtendedTypes()) {
                    // System.out.println(dbTable.classname + " extends " + extendsFromType.getNameAsString());
                    ResolvedReferenceType resolvedSuperClass = extendsFromType.resolve();

                    // Find a parent class that has fields in the same table
                    if (resolvedSuperClass.getTypeDeclaration() instanceof JavaParserClassDeclaration) {

                        JavaParserClassDeclaration parentClassDeclaration = (JavaParserClassDeclaration) resolvedSuperClass.getTypeDeclaration();
                        Optional<AnnotationExpr> inheritanceAnnotation = parentClassDeclaration.getWrappedNode().getAnnotationByClass(Inheritance.class);

                        if (inheritanceAnnotation.isPresent()) {
                            String inheritanceStrategy = getStringParameter(inheritanceAnnotation.get(), "strategy");

                            // In Joined type strategy parent entity stores fields in its own table. An ID field is added
                            if (inheritanceStrategy.contains("SINGLE")) {
                                dbTable.classname = cd.getNameAsString();
                                dbTable.parentClassname = extendsFromType.getNameAsString();
                            }
                        }
                    }
                }

                // Embedded entities. Fields will be added to entity holding embedded entity in later interaction.
            } else if (cd.isAnnotationPresent("Embeddable")) {

                dbTable.classname = cd.getNameAsString();
                dbTable.isEmbedded = true;
            }
        }
    }

    /**
     * Class inspector. Retrieves field information.
     */
    private static class FieldVisitor extends VoidVisitorAdapter<DBTable> {

        /**
         * Prefix to add to comments
         */
        public String commentPrefix;

        @Override
        public void visit(FieldDeclaration fd, DBTable dbTable) {
            super.visit(fd, dbTable);

            try {
                DBField dbField = new DBField();

                VariableDeclarator vd = fd.getVariable(0);
                String variableName = vd.getNameAsString();

                // Static, transient, final and OneToMany fields are of no interest
                if (fd.isTransient() || fd.isAnnotationPresent(Transient.class) || fd.isAnnotationPresent(OneToMany.class) || fd.isStatic() || fd.isFinal()) {
                    return;

                    // Extra table to store collection
                } else if (fd.isAnnotationPresent(CollectionTable.class)) {

                    AnnotationExpr collectionTableAnnotation = fd.getAnnotationByClass(CollectionTable.class).get();

                    String joinColumnName = null;
                    NormalAnnotationExpr joinColumnAnnotation = null;
                    Expression joinColumnsExpression = getParameter(collectionTableAnnotation, "joinColumns");
                    if (joinColumnsExpression != null) {
                        if (joinColumnsExpression instanceof NormalAnnotationExpr) {
                            joinColumnAnnotation = (NormalAnnotationExpr) joinColumnsExpression;
                        } else if (joinColumnsExpression instanceof ArrayInitializerExpr) {
                            joinColumnAnnotation = (NormalAnnotationExpr) ((ArrayInitializerExpr) joinColumnsExpression).getValues().get(0);
                        }

                        joinColumnName = getStringParameter(joinColumnAnnotation, "name");

                    } else {
                        joinColumnName = dbTable.classname.toLowerCase() + "_id";
                    }

                    DBTable extraTable = new DBTable();
                    extraTable.tablename = getStringParameter(collectionTableAnnotation, "name");
                    if (fd.getComment().isPresent()) {
                        extraTable.setComment(fd.getComment().get().toString());
                    }

                    ClassOrInterfaceType variableType = (ClassOrInterfaceType) vd.getType();

                    extraTable.fields.add(new DBField("", joinColumnName, "Long", null, dbTable.classname + " identifier", false));

                    if (variableType.getNameAsString().equals("Map")) {

                        String typeKey = ((ClassOrInterfaceType) (variableType.getTypeArguments()).get().get(0)).getNameAsString();
                        String typeValue = ((ClassOrInterfaceType) (variableType.getTypeArguments()).get().get(1)).getNameAsString();

                        extraTable.fields.add(new DBField("", variableName.toLowerCase(), typeValue, null, "Value", false));
                        extraTable.fields.add(new DBField("", variableName.toLowerCase() + "_key", typeKey, null, "Key", false));

                    } else if (variableType.getNameAsString().equals("Set") || variableType.getNameAsString().equals("List")) {

                        if (fd.isAnnotationPresent(AttributeOverrides.class)) {

                            ClassOrInterfaceType referencedType = (ClassOrInterfaceType) variableType.getTypeArguments().get().get(0);

                            AnnotationExpr attributesOverriteAnnotation = fd.getAnnotationByClass(AttributeOverrides.class).get();

                            ArrayInitializerExpr overridesAnnotations = (ArrayInitializerExpr) getParameter(attributesOverriteAnnotation, "value");
                            Map<String, DBField> overridenFields = new HashMap<>();
                            for (Expression overridenAnnotation : overridesAnnotations.getValues()) {
                                String fieldname = getStringParameter((AnnotationExpr) overridenAnnotation, "name");
                                AnnotationExpr columnDefinitionAnnotation = (AnnotationExpr) getParameter((AnnotationExpr) overridenAnnotation, "column");
                                String dbFieldname = getStringParameter(columnDefinitionAnnotation, "name");
                                boolean isNullable = getBooleanParameter(columnDefinitionAnnotation, "nullable", false);

                                overridenFields.put(fieldname, new DBField(fieldname, dbFieldname, null, null, null, isNullable));
                            }

                            ResolvedReferenceType resolvedReferencedClass = referencedType.resolve();

                            // Extract fields from referenced class and add comments, data types and default values to overriden column definitions
                            if (resolvedReferencedClass.getTypeDeclaration() instanceof JavaParserClassDeclaration) {
                                ClassOrInterfaceDeclaration referencedClassDeclaration = ((JavaParserClassDeclaration) resolvedReferencedClass.getTypeDeclaration())
                                    .getWrappedNode();

                                DBTable referencedTable = new DBTable();
                                fieldVisitor.visit(referencedClassDeclaration, referencedTable);

                                for (DBField fieldInReferencedEntity : referencedTable.fields) {
                                    DBField overridenField = overridenFields.get(fieldInReferencedEntity.fieldname);
                                    if (overridenField != null) {
                                        overridenField.setComment(fieldInReferencedEntity.comment);
                                        overridenField.setDbFieldType(fieldInReferencedEntity.dbFieldType);
                                        overridenField.setDbFieldDefaultValue(fieldInReferencedEntity.dbFieldDefaultValue);
                                    }
                                }
                            }
                            extraTable.fields.addAll(overridenFields.values());

                        } else {

                            String valueColumnName = variableName.toLowerCase();
                            if (fd.isAnnotationPresent(Column.class)) {
                                valueColumnName = getStringParameter(fd.getAnnotationByClass(Column.class).get(), "name");
                            }

                            String type = ((ClassOrInterfaceType) variableType.getTypeArguments().get().get(0)).getNameAsString();
                            if (fd.isAnnotationPresent(Enumerated.class)) {
                                if ("ORDINAL".equals(getStringParameter(fd.getAnnotationByClass(Enumerated.class).get(), "value"))) {
                                    type = "Integer";
                                } else {
                                    type = "String";
                                }
                            }

                            if (type.equals("String") || type.equals("Long") || type.equals("Integer")) {
                                extraTable.fields.add(new DBField("", valueColumnName, type, null, "Value", false));
                            } else {
                                extraTable.fields.add(new DBField("", valueColumnName + "_id", "Long", null, type + " identifier", false));
                            }
                        }
                    }
                    dbTable.addExtraTable(extraTable);
                    return;

                    // Extra table to store ManyToMany relationship
                } else if (fd.isAnnotationPresent(ManyToMany.class)) {

                    // Relation is defined in another class, so skip the field
                    if (getStringParameter(fd.getAnnotationByClass(ManyToMany.class).get(), "mappedBy") != null) {
                        return;
                    }

                    String relatedEntityClassname = ((ClassOrInterfaceType) (((ClassOrInterfaceType) vd.getType()).getTypeArguments()).get().get(0)).getNameAsString();

                    AnnotationExpr joinAnnotation = fd.getAnnotationByClass(JoinTable.class).get();

                    DBTable extraTable = new DBTable();
                    extraTable.tablename = getStringParameter(joinAnnotation, "name");
                    if (fd.getComment().isPresent()) {
                        extraTable.setComment(fd.getComment().get().toString());
                    }

                    NormalAnnotationExpr joinColumnAnnotation = null;

                    Expression joinColumnsExpression = getParameter(joinAnnotation, "joinColumns");
                    if (joinColumnsExpression instanceof NormalAnnotationExpr) {
                        joinColumnAnnotation = (NormalAnnotationExpr) joinColumnsExpression;
                    } else if (joinColumnsExpression instanceof ArrayInitializerExpr) {
                        joinColumnAnnotation = (NormalAnnotationExpr) ((ArrayInitializerExpr) joinColumnsExpression).getValues().get(0);
                    }
                    NormalAnnotationExpr inverseJoinColumnAnnotation = null;
                    Expression inverseJoinColumnsExpression = getParameter(joinAnnotation, "inverseJoinColumns");
                    if (inverseJoinColumnsExpression instanceof NormalAnnotationExpr) {
                        inverseJoinColumnAnnotation = (NormalAnnotationExpr) inverseJoinColumnsExpression;
                    } else if (joinColumnsExpression instanceof ArrayInitializerExpr) {
                        inverseJoinColumnAnnotation = (NormalAnnotationExpr) ((ArrayInitializerExpr) inverseJoinColumnsExpression).getValues().get(0);
                    }

                    String joinColumnName = getStringParameter(joinColumnAnnotation, "name");
                    String inverseJoinColumnName = getStringParameter(inverseJoinColumnAnnotation, "name");
                    extraTable.fields.add(new DBField("", joinColumnName, "Long", null, dbTable.classname + " identifier", false));
                    extraTable.fields.add(new DBField("", inverseJoinColumnName, "Long", null, relatedEntityClassname + " identifier", false));

                    if (fd.isAnnotationPresent(OrderColumn.class)) {
                        extraTable.fields
                            .add(new DBField("", getStringParameter(fd.getAnnotationByClass(OrderColumn.class).get(), "name"), "Integer", null, "Ordering number sequence", false));
                    }
                    return;

                    // Regular DB column
                } else if (fd.isAnnotationPresent(Column.class)) {
                    dbField.dbFieldname = getStringParameter(fd.getAnnotationByClass(Column.class).get(), "name");
                    dbField.nullable = getBooleanParameter(fd.getAnnotationByClass(Column.class).get(), "nullable", true);

                    // Many to one relation
                } else if (fd.isAnnotationPresent(ManyToOne.class)) {
                    if (fd.isAnnotationPresent(JoinColumn.class)) {

                        dbField.dbFieldname = getStringParameter(fd.getAnnotationByClass(JoinColumn.class).get(), "name");
                        dbField.setDbFieldType("Long");

                    } else {
                        dbField.dbFieldname = variableName;
                        dbField.setDbFieldType("Long");
                    }

                    // One to one relation
                } else if (fd.isAnnotationPresent(OneToOne.class)) {
                    if (fd.isAnnotationPresent(JoinColumn.class)) {
                        dbField.dbFieldname = getStringParameter(fd.getAnnotationByClass(JoinColumn.class).get(), "name");
                        dbField.setDbFieldType("Long");
                    } else {
                        return;
                    }

                    // Embedded entity
                } else if (fd.isAnnotationPresent(Embedded.class)) {

                    try {
                        Set<ResolvedFieldDeclaration> resolvedFields = vd.getType().resolve().asReferenceType().getDeclaredFields();

                        DBTable embeddedTable = new DBTable();

                        // Prefix internal field comments with a embedded field's comment
                        if (fd.getComment().isPresent()) {
                            FieldVisitor fieldVisitorEmbedded = new FieldVisitor();
                            fieldVisitorEmbedded.commentPrefix = cleanComments(fd.getComment().get().toString());

                            for (ResolvedFieldDeclaration field : resolvedFields) {
                                fieldVisitorEmbedded.visit(((JavaParserFieldDeclaration) field).getWrappedNode(), embeddedTable);
                            }
                            fieldVisitorEmbedded = null;

                        } else {
                            for (ResolvedFieldDeclaration field : resolvedFields) {
                                fieldVisitor.visit(((JavaParserFieldDeclaration) field).getWrappedNode(), embeddedTable);
                            }
                        }

                        // Handle overridden DB fields
                        Map<String, DBField> overridenFields = new HashMap<>();
                        if (fd.isAnnotationPresent(AttributeOverrides.class)) {

                            AnnotationExpr attributesOverriteAnnotation = fd.getAnnotationByClass(AttributeOverrides.class).get();

                            ArrayInitializerExpr overridesAnnotations = (ArrayInitializerExpr) getParameter(attributesOverriteAnnotation, "value");

                            for (Expression overridenAnnotation : overridesAnnotations.getValues()) {
                                String fieldname = getStringParameter((AnnotationExpr) overridenAnnotation, "name");
                                AnnotationExpr columnDefinitionAnnotation = (AnnotationExpr) getParameter((AnnotationExpr) overridenAnnotation, "column");
                                String dbFieldname = getStringParameter(columnDefinitionAnnotation, "name");
                                boolean isNullable = getBooleanParameter(columnDefinitionAnnotation, "nullable", true);

                                overridenFields.put(fieldname, new DBField(fieldname, dbFieldname, null, null, null, isNullable));
                            }
                        }
                        for (DBField field : embeddedTable.fields) {
                            DBField overridenField = overridenFields.get(field.fieldname);
                            if (overridenField != null) {
                                field.dbFieldname = overridenField.dbFieldname;
                                field.nullable = overridenField.nullable;
                            }
                            field.fieldname = variableName + "." + field.fieldname;
                        }

                        dbTable.addUniqueFields(embeddedTable.fields);

                    } catch (Exception e) {
                        // System.out.println("Failed to resolve fields for " + vd.getType() + " " + vd.getType().getClass());
                        // e.printStackTrace(System.out);
                        if (dbTable.failedEmbeddedTypes == null) {
                            dbTable.failedEmbeddedTypes = new ArrayList<>();
                        }
                        dbTable.failedEmbeddedTypes.add(vd.getType().toString());
                    }

                    return;

                    // Not a DB persisted column
                } else {
                    return;
                }

                if (fd.isAnnotationPresent(Enumerated.class)) {
                    if ("ORDINAL".equals(getStringParameter(fd.getAnnotationByClass(Enumerated.class).get(), "value"))) {
                        dbField.setDbFieldType("Integer");
                    } else {
                        dbField.setDbFieldType("String");
                    }

                } else if (fd.isAnnotationPresent(Type.class)) {
                    if (getStringParameter(fd.getAnnotationByClass(Type.class).get(), "type").contains("json")) {
                        dbField.setDbFieldType("Json");
                    }
                }
                if (fd.isAnnotationPresent(NotNull.class)) {
                    dbField.nullable = false;
                }

                // System.out.println("Field: " + fd);

                dbField.setFieldname(variableName);
                if (dbField.dbFieldType == null) {
                    dbField.setDbFieldType(vd.getTypeAsString());
                }
                if (vd.getInitializer().isPresent()) {
                    Expression initializer = vd.getInitializer().get();

                    if (initializer instanceof StringLiteralExpr) {
                        dbField.setDbFieldDefaultValue(((StringLiteralExpr) initializer).getValue());
                    } else if (initializer instanceof FieldAccessExpr) {
                        dbField.setDbFieldDefaultValue(((FieldAccessExpr) initializer).getNameAsString());
                    } else if (initializer instanceof LongLiteralExpr) {
                        String longValue = ((LongLiteralExpr) initializer).getValue();
                        if (longValue.endsWith("L")) {
                            longValue = longValue.substring(0, longValue.length() - 1);
                        }
                        dbField.setDbFieldDefaultValue(longValue);
                    } else {
                        dbField.setDbFieldDefaultValue(vd.getInitializer().get().toString());
                    }
                }
                if (fd.getComment().isPresent()) {
                    dbField.setComment((commentPrefix != null ? commentPrefix + " - " : "") + fd.getComment().get().toString());
                }

                dbTable.fields.add(dbField);

                // System.out.println(dbField);

            } catch (

            Exception e) {
                System.out.println("Failed to process " + dbTable.classname + " field " + fd.toString());
                e.printStackTrace(System.out);
                throw e;
            }
        }

    }

    /**
     * Retrieve annotation parameter value as Expression
     * 
     * @param annotationExpr Annotation
     * @param parameterName Parameter name to retrieve
     * @return Parameter value as Expresion
     */
    @SuppressWarnings("deprecation")
    private static Expression getParameter(AnnotationExpr annotationExpr, String parameterName) {
        List<MemberValuePair> children = annotationExpr.getChildNodesByType(MemberValuePair.class);
        for (MemberValuePair memberValuePair : children) {
            if (parameterName.equals(memberValuePair.getNameAsString())) {
                return memberValuePair.getValue();
            }
        }
        return null;
    }

    /**
     * Retrieve annotation parameter value as String
     * 
     * @param annotationExpr Annotation
     * @param parameterName Parameter name to retrieve
     * @return Parameter value as String
     */
    private static String getStringParameter(AnnotationExpr annotationExpr, String parameterName) {

        Expression expression = getParameter(annotationExpr, parameterName);
        if (expression != null && expression instanceof StringLiteralExpr) {
            return ((StringLiteralExpr) expression).getValue();

        } else if (expression != null && expression instanceof FieldAccessExpr) {
            return ((FieldAccessExpr) expression).getNameAsString();
        }
        return null;

    }

    /**
     * Retrieve annotation parameter value as boolean
     * 
     * @param annotationExpr Annotation
     * @param parameterName Parameter name to retrieve
     * @param defaultValue Default value
     * @return Parameter value as boolean
     */
    private static boolean getBooleanParameter(AnnotationExpr annotationExpr, String parameterName, boolean defaultValue) {

        BooleanLiteralExpr exp = (BooleanLiteralExpr) getParameter(annotationExpr, parameterName);
        if (exp != null) {
            return exp.getValue();
        }
        return defaultValue;
    }

    /**
     * Represents a database table
     */
    private static class DBTable {

        /**
         * Entity class
         */
        public String classname;

        /**
         * DB table name
         */
        public String tablename;

        /**
         * Comment/Description
         */
        public String comment;

        /**
         * A list of DB table fields
         */
        public List<DBField> fields = new ArrayList<>();

        /**
         * Additional collection type tables with field information
         */
        public List<DBTable> extraTables = null;

        /**
         * Unresolved nested embedded types. To be resolved in a later iteration.
         */
        public List<String> failedEmbeddedTypes = null;

        /**
         * Is this an embedded entity definition. In this case tablename will be null. Fields will be appended to the entities where failedEmbeddedTypes match the classname value
         * of embedded entity.
         */
        public boolean isEmbedded = false;

        /**
         * Parent classname in case of Inheritance.SINGLE type inheritance. Fields will be appended to the entity which classname match parentClassname value
         */
        public String parentClassname;

        public void setComment(String comment) {
            this.comment = cleanComments(comment);
        }

        /**
         * Add extra table definitions
         * 
         * @param extraTablesToAdd Extra table definitions
         */
        public void addExtraTables(List<DBTable> extraTablesToAdd) {

            if (this.extraTables == null) {
                this.extraTables = new ArrayList<>();
            }
            this.extraTables.addAll(extraTablesToAdd);
        }

        /**
         * Add extra table definition
         * 
         * @param extraTableToAdd Extra table definition
         */
        public void addExtraTable(DBTable extraTableToAdd) {

            if (extraTables == null) {
                extraTables = new ArrayList<>();
            }
            extraTables.add(extraTableToAdd);
        }

        /**
         * Add fields that are not present yet
         * 
         * @param fieldsToAdd Fields to add
         * @throws Exception 
         */
        public void addUniqueFields(List<DBField> fieldsToAdd) throws Exception {

            if (fields == null || fields.isEmpty()) {
                fields = fieldsToAdd;
                return;
            }

            mainloop: for (DBField dbFieldToAdd : fieldsToAdd) {

                for (DBField dbField : fields) {

                    if (dbField.dbFieldname == null) {
                        System.out.println("DbTable/field is null " + this.classname + "/" + this.tablename + "\n" + dbField);
                        throw new Exception("DbTable/field is null " + this.classname + "/" + this.tablename + " " + dbField);
                    }
                    if (dbField.dbFieldname.equals(dbFieldToAdd.dbFieldname)) {
                        continue mainloop;
                    }
                }
                fields.add(dbFieldToAdd);
            }
        }

        @Override
        public String toString() {
            return classname + "/" + tablename + (comment != null ? "\n" + comment : "");
        }

        /**
         * Provide a HTML text for document representing a table with fields and any extra tables with their fields
         * 
         * @return HTML text
         */
        public String toHtml() {
            String html = "<H1>" + (classname != null ? classname : "") + "</H1><br/><b>" + tablename + "</b>" + (comment != null ? "<br/><p>" + comment + "</p>" : "");
            html += "<table><tr><th>Field name</th><th>Db column name</th><th>Type</th><th>Default value</th><th>Required</th><th>Description</th></tr>";
            for (DBField field : fields) {
                html += field.toHtml();
            }

            html += "</table>";

            if (extraTables != null) {
                for (DBTable dbTable : extraTables) {
                    dbTable.fields.sort(Comparator.comparing(DBField::getFieldname));
                    html += dbTable.toHtml();
                }
            }
            return html;
        }

    }

    /**
     * Represents a database table field
     */
    private static class DBField {

        /**
         * Entity's field name
         */
        public String fieldname;

        /**
         * Database table field name
         */
        public String dbFieldname;

        /**
         * Database field type
         */
        public String dbFieldType;

        /**
         * Default value
         */
        public String dbFieldDefaultValue;

        /**
         * Comment
         */
        public String comment;

        /**
         * Is field optional
         */
        public boolean nullable = true;

        public DBField() {
        }

        public DBField(String fieldname, String dbFieldname, String dbFieldType, String dbFieldDefaultValue, String comment, boolean nullable) {
            super();
            this.fieldname = fieldname;
            this.dbFieldname = dbFieldname;
            this.setDbFieldType(dbFieldType);
            this.setDbFieldDefaultValue(dbFieldDefaultValue);
            this.comment = comment;
            this.nullable = nullable;
        }

        public void setFieldname(String fieldname) {
            this.fieldname = fieldname;
            if (fieldname.equalsIgnoreCase("id") || fieldname.equalsIgnoreCase("version")) {
                this.nullable = false;
            }
        }

        public String getFieldname() {
            return fieldname;
        }

        public String getDbFieldname() {
            return dbFieldname;
        }

        /**
         * Set default value. Value will be cleaned up
         * 
         * @param defaultValue Default value to set
         */
        public void setDbFieldDefaultValue(String defaultValue) {

            if (defaultValue != null) {

                defaultValue = defaultValue.replace("UUID.randomUUID().toString()", "Generated UUID");
                defaultValue = defaultValue.replace("ZERO", "0");
                if (defaultValue.equalsIgnoreCase("false")) {
                    defaultValue = "false/0";
                } else if (defaultValue.equalsIgnoreCase("true")) {
                    defaultValue = "true/1";
                } else if (defaultValue.equalsIgnoreCase("new Date()")) {
                    defaultValue = "Current timestamp";
                }

            }
            this.dbFieldDefaultValue = defaultValue;
        }

        /**
         * Set comment. Comment will be cleaned up.
         * 
         * @param comment Comment
         */
        public void setComment(String comment) {
            this.comment = cleanComments(comment);
        }

        /**
         * Set database field type. Type be cleaned up.
         * 
         * @param dbFieldType DB field type
         */
        public void setDbFieldType(String dbFieldType) {

            if (dbFieldType != null) {
                if (dbFieldType.equals("boolean") && dbFieldDefaultValue == null) {
                    setDbFieldDefaultValue("false");
                } else if (dbFieldType.equals("int") && dbFieldDefaultValue == null) {
                    setDbFieldDefaultValue("0");
                }

                dbFieldType = dbFieldType.replace("BigDecimal", "Decimal");
                if (dbFieldType.equalsIgnoreCase("boolean")) {
                    dbFieldType = "Boolean/Integer";
                } else if (dbFieldType.equalsIgnoreCase("int")) {
                    dbFieldType = "Integer";
                }
            }

            this.dbFieldType = dbFieldType;
        }

        @Override
        public String toString() {
            return "-- " + fieldname + "/" + dbFieldname + "/" + dbFieldType + "/" + dbFieldDefaultValue + "/" + nullable + (comment != null ? "\n" + comment : "");
        }

        /**
         * Provide a HTML text for document representing a single field
         * 
         * @return HTML table row
         */
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