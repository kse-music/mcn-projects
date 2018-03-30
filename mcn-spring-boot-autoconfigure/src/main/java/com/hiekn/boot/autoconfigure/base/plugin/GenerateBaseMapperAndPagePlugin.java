package com.hiekn.boot.autoconfigure.base.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

public class GenerateBaseMapperAndPagePlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.getSuperInterfaceTypes().clear();//清除原来的rootInterface
        interfaze.getImportedTypes().clear();

        String rootInterface = this.context.getJavaClientGeneratorConfiguration().getProperty("rootInterface");
        String repository = this.context.getJavaClientGeneratorConfiguration().getProperty("repository");

        if(rootInterface != null){
            String pk = "Object";//default PK Object
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
            if(primaryKeyColumns != null && primaryKeyColumns.size() == 1){
                for (IntrospectedColumn keyColumn : primaryKeyColumns) {
                    pk = keyColumn.getFullyQualifiedJavaType().getShortName();
                }
            }

            FullyQualifiedJavaType baseMapper = new FullyQualifiedJavaType("BaseMapper<"
                    + introspectedTable.getBaseRecordType() + ","
                    + pk+ ">");
            interfaze.addImportedType(new FullyQualifiedJavaType(rootInterface));
            interfaze.addSuperInterface(baseMapper);
        }

        if(repository != null){
            interfaze.addImportedType(new FullyQualifiedJavaType(repository));
            interfaze.getAnnotations().add("@Repository");
        }

        interfaze.getMethods().clear();
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();// 数据库表名
        XmlElement parentElement = document.getRootElement();

        // 添加sql——where
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", "sql_where"));
        XmlElement where = new XmlElement("where");
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" != null"); //$NON-NLS-1$
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            where.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(" and ");
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = "); //$NON-NLS-1$
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }
        sql.addElement(where);
        parentElement.addElement(sql);

        //添加pageSelect
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "pageSelect"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        select.addAttribute(new Attribute("parameterType", "java.util.Map"));
        select.addElement(new TextElement(" select * from " + tableName));
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid", "sql_where"));
        select.addElement(include);
        select.addElement(new TextElement(" LIMIT #{pageNo},#{pageSize}"));
        parentElement.addElement(select);

        //添加pageCount
        XmlElement pageCount = new XmlElement("select");
        pageCount.addAttribute(new Attribute("id", "pageCount"));
        pageCount.addAttribute(new Attribute("resultType", "java.lang.Integer"));
        pageCount.addAttribute(new Attribute("parameterType", "java.util.Map"));
        pageCount.addElement(new TextElement(" select COUNT(*) from " + tableName));
        XmlElement include2 = new XmlElement("include");
        include2.addAttribute(new Attribute("refid", "sql_where"));
        pageCount.addElement(include2);
        parentElement.addElement(pageCount);

        return true;
    }


}