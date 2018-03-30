package com.hiekn.boot.autoconfigure.base.plugin;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class GenerateBaseServiceAndImplementPlugin extends PluginAdapter {
    private String serviceTargetDir;
    private String serviceTargetPackage;
    private String service;

    private ShellCallback shellCallback = null;

    public GenerateBaseServiceAndImplementPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }

    @Override
    public boolean validate(List<String> warnings) {
        serviceTargetDir = properties.getProperty("targetProject");
        serviceTargetPackage = properties.getProperty("targetPackage");
        service = properties.getProperty("service");
        return stringHasValue(serviceTargetDir) && stringHasValue(serviceTargetPackage) && stringHasValue(service);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> javaFiles = new ArrayList<>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            CompilationUnit unit = javaFile.getCompilationUnit();
            FullyQualifiedJavaType baseModelJavaType = unit.getType();
            String shortName = baseModelJavaType.getShortName();

            if (shortName.endsWith("Mapper")) {
                //create interface XxxService FullName
                String serviceInterfaceFullName = serviceTargetPackage + "." + shortName.replace("Mapper", "Service");
                Interface serviceInterface = new Interface(serviceInterfaceFullName);
                serviceInterface.setVisibility(JavaVisibility.PUBLIC);

                String pk = "Object";//default PK Object
                List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
                if(primaryKeyColumns != null && primaryKeyColumns.size() == 1){
                    for (IntrospectedColumn keyColumn : primaryKeyColumns) {
                        pk = keyColumn.getFullyQualifiedJavaType().getShortName();
                    }
                }

                String rootInterface = properties.getProperty("rootInterface");

                TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(serviceTargetPackage+".impl."+shortName.replace("Mapper", "ServiceImpl")));
                topLevelClass.setVisibility(JavaVisibility.PUBLIC);
                topLevelClass.addImportedType(service);
                topLevelClass.addImportedType(serviceInterfaceFullName);
                topLevelClass.addSuperInterface(new FullyQualifiedJavaType(serviceInterfaceFullName));
                topLevelClass.getAnnotations().add("@Service");

                if(rootInterface != null){
                    //create super interface BaseService
                    FullyQualifiedJavaType baseService = new FullyQualifiedJavaType("BaseService<"
                            + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ","
                            + pk+ ">");
                    serviceInterface.addImportedType(new FullyQualifiedJavaType(rootInterface));
                    serviceInterface.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
                    serviceInterface.addSuperInterface(baseService);

                    //create super class BaseServiceImpl
                    FullyQualifiedJavaType baseServiceImpl = new FullyQualifiedJavaType("BaseServiceImpl<"
                            + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ","
                            + pk+ ">");
                    topLevelClass.addImportedType(introspectedTable.getBaseRecordType());
                    topLevelClass.addImportedType(rootInterface.replace("BaseService","BaseServiceImpl"));
                    topLevelClass.setSuperClass(baseServiceImpl);
                }

                try {
                    JavaFormatter javaFormatter = context.getJavaFormatter();
                    //gen XxxService
                    checkAndAddJavaFile(new GeneratedJavaFile(serviceInterface, serviceTargetDir, javaFormatter),javaFiles);
                    //gen XxxServiceImpl
                    checkAndAddJavaFile(new GeneratedJavaFile(topLevelClass, serviceTargetDir, javaFormatter),javaFiles);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            }
        }
        return javaFiles;
    }

    private void checkAndAddJavaFile(GeneratedJavaFile javaFile,List<GeneratedJavaFile> javaFiles) throws ShellException {
        File dir = shellCallback.getDirectory(serviceTargetDir, serviceTargetPackage);
        File file = new File(dir, javaFile.getFileName());
        if (file.exists()) {
            file.delete();
        }
        javaFiles.add(javaFile);
    }

}