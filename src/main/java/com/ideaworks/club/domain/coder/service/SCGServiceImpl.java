package com.ideaworks.club.domain.coder.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.lang.model.element.Modifier;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ideaworks.club.domain.coder.constrant.KeyConstant;
import com.ideaworks.club.domain.coder.domain.Column;
import com.ideaworks.club.domain.coder.domain.EntityDTO;
import com.ideaworks.club.domain.coder.domain.EntityField;
import com.ideaworks.club.domain.coder.factory.ClassBuilderFactory;
import com.ideaworks.club.domain.coder.factory.FieldBuilderFactory;
import com.ideaworks.club.domain.coder.factory.FileBuilderFactory;
import com.ideaworks.club.domain.coder.factory.InterfaceBuilderFactory;
import com.ideaworks.club.domain.coder.factory.MethodBuilderFactory;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.AnnotationSpec.Builder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SCGServiceImpl implements SCGService {
    /**
     * 生成 Domain 类
     */
    @Override
    public String generateDomain(EntityDTO entity) {
        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        List<FieldSpec> fields = new ArrayList<>();

        String entityName = entity.getEntityName(); // Entity 名称
        String tableName = entity.getTableName(); // Entity 对应的表
        String comment = entity.getComment(); // Entity 说明 ApiModel 注解用
//		String apiTag = entity.getApiTag(); // Controller 接口说明 API（tags） 注解用

        // 类上的注解
        classAnnotations.add(AnnotationSpec.builder(Entity.class).build());
        classAnnotations.add(AnnotationSpec.builder(Data.class).build());
        classAnnotations.add(AnnotationSpec.builder(Table.class).addMember("name", "$S", tableName).build());
        classAnnotations.add(AnnotationSpec.builder(ApiModel.class).addMember("value", "$S", comment).build());
        classAnnotations.add(AnnotationSpec.builder(Accessors.class).addMember("chain", "$L", true).build());

        // 实体的属性列表
        List<EntityField> columns = entity.getEntityFields();

        for (EntityField column : columns) {
            if (column.getIsKey()) // 主键
            {
                List<AnnotationSpec> fieldAnnotations = new ArrayList<>();

//				fieldAnnotations
//						.add(AnnotationSpec.builder(GeneratedValue.class).addMember("strategy", "auto").build());

                // 添加 @Id
                fieldAnnotations.add(AnnotationSpec.builder(Id.class).build());

                // 添加 @Column
                Builder annotationSpec = AnnotationSpec.builder(javax.persistence.Column.class);
                annotationSpec.addMember("name", "$S", column.getTableFieldName()).addMember("nullable", "$L",
                        !column.getIsRequired());

                if (column.getDataType().equals("String")) {
                    annotationSpec.addMember("length", "$L", column.getLength());
                }

                fieldAnnotations.add(annotationSpec.build());

                // 添加 @ApiModelProperty
                fieldAnnotations.add(AnnotationSpec.builder(ApiModelProperty.class)
                        .addMember("name", "$S", column.getFieldComment()).build());

                FieldSpec android = FieldSpec.builder(this.getFieldType(column.getDataType()), column.getFieldName())
                        .addModifiers(Modifier.PRIVATE).addAnnotations(fieldAnnotations).build();

                fields.add(android);
            } else {
                List<AnnotationSpec> fieldAnnotations = new ArrayList<>();

                // 添加 @Column
                Builder annotationSpec = AnnotationSpec.builder(javax.persistence.Column.class);
                annotationSpec.addMember("name", "$S", column.getTableFieldName()).addMember("nullable", "$L",
                        !column.getIsRequired());

                if (column.getDataType().equals("String")) {
                    annotationSpec.addMember("length", "$L", column.getLength());
                }

                fieldAnnotations.add(annotationSpec.build());

                // 添加 @DateTimeFormat(pattern = Constants.PATTERN_DATE)
//			    @JsonFormat(pattern = Constants.PATTERN_DATE, timezone = "GMT+8")

                if (column.getDataType().equals("Date")) {
                    fieldAnnotations.add(AnnotationSpec.builder(DateTimeFormat.class)
                            .addMember("pattern", "$S", "yyyy-MM-dd").build());

                    fieldAnnotations.add(AnnotationSpec.builder(JsonFormat.class)
                            .addMember("pattern", "$S", "yyyy-MM-dd").addMember("timezone", "$S", "GTM+8").build());
                }

                // 添加 @ApiModelProperty
                fieldAnnotations.add(AnnotationSpec.builder(ApiModelProperty.class)
                        .addMember("name", "$S", column.getFieldComment()).build());

                FieldSpec android = FieldSpec.builder(getFieldType(column.getDataType()), column.getFieldName())
                        .addModifiers(Modifier.PRIVATE).addAnnotations(fieldAnnotations).build();

                fields.add(android);
            }
        }

        TypeSpec classTypeSpec = TypeSpec.classBuilder(entityName).addFields(fields).addAnnotations(classAnnotations)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();

        JavaFile javaFile = JavaFile.builder("www.ideaworks.club.domain", classTypeSpec).build();

        String fileString;
        fileString = javaFile.toString();
//		javaFile.writeTo(System.out);
        return fileString;
    }

    /**
     * 生成 DTO 类
     */
    @Override
    public String generateDTO(EntityDTO entity) {
        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        List<FieldSpec> fields = new ArrayList<>();

        String entityName = entity.getEntityName(); // Entity 名称

        // 类上的注解
        classAnnotations.add(AnnotationSpec.builder(Data.class).build());

        // 实体的属性列表
        List<EntityField> columns = entity.getEntityFields();

        for (EntityField column : columns) {
            List<AnnotationSpec> fieldAnnotations = new ArrayList<>();

            // 添加 @ApiModelProperty
            fieldAnnotations.add(AnnotationSpec.builder(ApiModelProperty.class)
                    .addMember("name", "$S", column.getFieldComment()).build());

            // 添加 @DateTimeFormat(pattern = Constants.PATTERN_DATE)
//		    @JsonFormat(pattern = Constants.PATTERN_DATE, timezone = "GMT+8")

            if (column.getDataType().equals("Date")) {
                fieldAnnotations.add(
                        AnnotationSpec.builder(DateTimeFormat.class).addMember("pattern", "$S", "yyyy-MM-dd").build());

                fieldAnnotations.add(AnnotationSpec.builder(JsonFormat.class).addMember("pattern", "$S", "yyyy-MM-dd")
                        .addMember("timezone", "$S", "GTM+8").build());
            }

            FieldSpec android = FieldSpec.builder(getFieldType(column.getDataType()), column.getFieldName())
                    .addModifiers(Modifier.PRIVATE).addAnnotations(fieldAnnotations).build();

            fields.add(android);
        }

        TypeSpec classTypeSpec = TypeSpec.classBuilder(entityName + "DTO").addFields(fields)
                .addAnnotations(classAnnotations).addModifiers(Modifier.PUBLIC).build();

        JavaFile javaFile = JavaFile.builder("www.ideaworks.club.dto", classTypeSpec).build();

        String fileString;
        fileString = javaFile.toString();
//		javaFile.writeTo(System.out);
        return fileString;
    }

    /**
     * 生成 repository
     */
    @Override
    public String generateRepository(EntityDTO entityDTO) {
        List<AnnotationSpec> interfaceAnnotations = new ArrayList<>();

        String entityName = entityDTO.getEntityName(); // Entity 名称
//		String tableName = entityDTO.getTableName(); // Entity 对应的表
//		String comment = entityDTO.getComment(); // Entity 说明 ApiModel 注解用
//		String apiTag = entity.getApiTag(); // Controller 接口说明 API（tags） 注解用

        // 实体的属性列表
        List<EntityField> columns = entityDTO.getEntityFields();

        ClassName idClassName = null;
        for (EntityField column : columns)
            if (column.getIsKey()) {
                idClassName = ClassName.bestGuess(column.getDataType());
            }
        if (idClassName == null) {
            idClassName = ClassName.bestGuess("com.lang.String");
        }

        interfaceAnnotations.add(AnnotationSpec.builder(Repository.class).build());

        // 接口继承 jpa 接口
//		ClassName modelClass = ClassName.get(("www.ideaworks.club.domain").trim(), entityName);

//		ClassName primaryKey = idClassName;

//		TypeVariableName jpaRepository = TypeVariableName
//				.get(JpaRepository.class.getTypeName().concat("<" + modelClass + ", " + primaryKey + ">"));

        ClassName jpaClassName = ClassName.get("org.springframework.data.jpa.repository", "JpaRepository");
        ClassName PageingClassName = ClassName.get("org.springframework.data.repository", "PagingAndSortingRepository");
        ClassName JpaSpecificationExecutor = ClassName.get("org.springframework.data.repository", "JpaSpecificationExecutor");

        TypeSpec repositorySpec = TypeSpec.interfaceBuilder(entityName + "Repository").addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(jpaClassName,
                        ClassName.get("www.ideaworks.club.domain", entityName), ClassName.get("java.lang", "String")))
                .addSuperinterface(ParameterizedTypeName.get(PageingClassName,
                        ClassName.get("www.ideaworks.club.domain", entityName), ClassName.get("java.lang", "String")))
                .addSuperinterface(ParameterizedTypeName.get(JpaSpecificationExecutor,
                        ClassName.get("www.ideaworks.club.domain", entityName)))
                .build();
        JavaFile javaFile = JavaFile.builder("www.ideaworks.club.repository", repositorySpec).build();

        String fileString;
        fileString = javaFile.toString();
//		javaFile.writeTo(System.out);
        return fileString;

    }

    /**
     * 生成 Service
     */
    @Override
    public String generateService(EntityDTO entityDTO) {
        List<MethodSpec> methods = new ArrayList<>();

        String entityName = entityDTO.getEntityName(); // Entity 名称
//		String tableName = entityDTO.getTableName(); // Entity 对应的表
//		String comment = entityDTO.getComment(); // Entity 说明 ApiModel 注解用
//		String apiTag = entity.getApiTag(); // Controller 接口说明 API（tags） 注解用

        // 实体类的类名
        ClassName modelClass = ClassName.get(("www.ideaworks.club.domain").trim(), entityName);
        ClassName DTOClass = ClassName.get(("www.ideaworks.club.dto").trim(), entityName + "DTO");
        // 实体的属性列表
        List<EntityField> columns = entityDTO.getEntityFields();

        EntityField columnEntityField = null;
        for (EntityField column : columns) {
            if (column.getIsKey()) {
                columnEntityField = column;
            }
        }

        // service 方法的修饰符
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(Modifier.PUBLIC);
        modifiers.add(Modifier.ABSTRACT);

        // SAVE 保存单个实体方法 ========================================
        List<ParameterSpec> saveMethodParameters = new ArrayList<>();

        saveMethodParameters.add(ParameterSpec.builder(DTOClass, entityName.toLowerCase() + "DTO").build());

        MethodSpec saveMethodSpec = MethodSpec.methodBuilder("save" + entityName).addModifiers(modifiers)
                .returns(DTOClass).addParameters(saveMethodParameters).build();

        methods.add(saveMethodSpec);

        // GetALL 获取所有实体列表====================================
        ClassName list = ClassName.get("java.util", "List");

        // List<model>
        TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);
        TypeName modelDTOClasses = ParameterizedTypeName.get(list, DTOClass);

        MethodSpec GetAllMethodSpec = MethodSpec.methodBuilder("getAll" + entityName + "s").addModifiers(modifiers)
                .returns(modelDTOClasses).build();

        methods.add(GetAllMethodSpec);

        // GetById 通过 id 获取某个实体对象

        List<ParameterSpec> getMethodParameters = new ArrayList<>();

        assert columnEntityField != null;
        getMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).build());

        MethodSpec GetByIdMethodSpec = MethodSpec
                .methodBuilder(
                        "get" + entityName + "By" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1))
                .addModifiers(modifiers).returns(DTOClass).addParameters(getMethodParameters).build();

        methods.add(GetByIdMethodSpec);

        // UpdateById 根据id 更新实体对象 =========================================
        List<ParameterSpec> updateMethodParameters = new ArrayList<>();

        // 要更新的新实体
        updateMethodParameters.add(ParameterSpec.builder(DTOClass, entityName.toLowerCase() + "DTO").build());

        // 要更新的实体名
        updateMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).build());

        MethodSpec UpdateByIdMethodSpec = MethodSpec
                .methodBuilder(
                        "update" + entityName + "By" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1))
                .addModifiers(modifiers).returns(DTOClass).addParameters(updateMethodParameters).build();

        methods.add(UpdateByIdMethodSpec);

        // deleteById 根据id 删除实体
        List<ParameterSpec> deleteMethodParameters = new ArrayList<>();

        deleteMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).build());

        MethodSpec deleteByIdMethodSpec = MethodSpec
                .methodBuilder(
                        "delete" + entityName + "By" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1))
                .addModifiers(modifiers).returns(void.class).addParameters(deleteMethodParameters).build();

        methods.add(deleteByIdMethodSpec);

        // isExist 查看某个实体是否已经存在
        List<ParameterSpec> isExitMethodParameters = new ArrayList<>();

        isExitMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).build());

        MethodSpec isExitMethodSpec = MethodSpec.methodBuilder("is" + entityName + "Exist").addModifiers(modifiers)
                .returns(Boolean.class).addParameters(isExitMethodParameters).build();

        methods.add(isExitMethodSpec);

        // Page<model> 根据条件查询分页========================================
        // Page<CasesSdjg> getPage(CasesSdjgCondition condition, Pageable pageable);

        ClassName listPage = ClassName.get("org.springframework.data.domain", "Page");
        ClassName condition = ClassName.get("www.ideaworks.club.dto", entityName + "DTO");
        ClassName pageable = ClassName.get("org.springframework.data.domain", "Pageable");

        List<ParameterSpec> getPageMethodParameters = new ArrayList<>();

        TypeName PageClasses = ParameterizedTypeName.get(listPage, DTOClass);

        getPageMethodParameters.add(ParameterSpec.builder(condition, (entityName + "DTO").toLowerCase()).build());
        getPageMethodParameters.add(ParameterSpec.builder(pageable, "pageable").build());

        MethodSpec ConditionPageMethodSpec = MethodSpec.methodBuilder("getPageByCondition").addModifiers(modifiers)
                .addParameters(getPageMethodParameters).returns(PageClasses).build();

        methods.add(ConditionPageMethodSpec);

        // 生成文件并返回=================================================
        TypeSpec serviceTypeSpec = TypeSpec.interfaceBuilder(entityName + "Service").addModifiers(Modifier.PUBLIC)
                .addMethods(methods).build();

        JavaFile javaFile = JavaFile.builder("www.ideaworks.club.service", serviceTypeSpec).build();

        String fileString;
        fileString = javaFile.toString();
//		javaFile.writeTo(System.out);
        return fileString;

    }

    /**
     * 生成 ServiceImpl
     */
    @Override
    public String generateServiceImpl(EntityDTO entityDTO) {
        List<MethodSpec> methods = new ArrayList<>();

        String entityName = entityDTO.getEntityName(); // Entity 名称
//		String tableName = entityDTO.getTableName(); // Entity 对应的表
//		String comment = entityDTO.getComment(); // Entity 说明 ApiModel 注解用
//		String apiTag = entity.getApiTag(); // Controller 接口说明 API（tags） 注解用

        // 实体类的类名
        ClassName modelClass = ClassName.get(("www.ideaworks.club.domain").trim(), entityName);

        // 实体类的类名
        ClassName DTOClass = ClassName.get(("www.ideaworks.club.form").trim(), entityName + "DTO");

        ClassName serviceInterface = ClassName.get(("www.ideaworks.club.service").trim(),
                entityName + "Service");

        ClassName repositoryClass = ClassName.get(("www.ideaworks.club.service").trim(),
                entityName + "Repository");

        // 实体的属性列表
        List<EntityField> columns = entityDTO.getEntityFields();

        EntityField columnEntityField = null;
        for (EntityField column : columns) {
            if (column.getIsKey()) {
                columnEntityField = column;
            }
        }

        // service 方法的修饰符
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(Modifier.PUBLIC);
//		modifiers.add(Modifier.ABSTRACT);

        // SAVE 保存单个实体方法 ========================================
        List<ParameterSpec> saveMethodParameters = new ArrayList<>();

        saveMethodParameters.add(
                ParameterSpec.builder(DTOClass, entityName.toLowerCase() + "DTO").addModifiers(Modifier.FINAL).build());

        MethodSpec saveMethodSpec = MethodSpec.methodBuilder("save" + entityName).addModifiers(modifiers)
                .returns(DTOClass)
                .addStatement("final $T " + entityName.toLowerCase() + " = new $T()", modelClass, modelClass)
                .addStatement("mapToEntity(" + entityName.toLowerCase() + "DTO," + entityName.toLowerCase() + ")")
                .addStatement("return mapToDTO(" + entityName.toLowerCase() + "Repository" + ".save(" + entityName.toLowerCase() + ")", "new " + entityName + "DTO())")
                .addParameters(saveMethodParameters).build();

        methods.add(saveMethodSpec);

        // GetALL 获取所有实体列表====================================
        ClassName list = ClassName.get("java.util", "List");

        // List<model>
//		TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);

        TypeName DTOClasses = ParameterizedTypeName.get(list, DTOClass);

        MethodSpec GetAllMethodSpec = MethodSpec.methodBuilder("getAll" + entityName + "s").addModifiers(modifiers)
                .addStatement("return " + entityName.toLowerCase() + "Repository" + "\n.findAll()\n.stream()\n.map("
                        + entityName.toLowerCase() + "-> mapToDTO(" + entityName.toLowerCase() + "," + "new "
                        + entityName + "DTO()))\n.collect(Collectors.toList())")
                .returns(DTOClasses).build();

        methods.add(GetAllMethodSpec);

        // GetById 通过 id 获取某个实体对象

        List<ParameterSpec> getMethodParameters = new ArrayList<>();

        assert columnEntityField != null;
        getMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).addModifiers(Modifier.FINAL).build());

        MethodSpec GetByIdMethodSpec = MethodSpec
                .methodBuilder(
                        "get" + entityName + "By" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1))
                .addModifiers(modifiers)
//                .addStatement("$T " + entityName.toLowerCase() + "DTO = new $T()", DTOClass, DTOClass)
                .addStatement("return " + entityName.toLowerCase() + "Repository" + ".findById("
                        + columnEntityField.getFieldName().toLowerCase() + ")\n.map(" + entityName.toLowerCase()
                        + " -> " + "mapToDTO(" + entityName.toLowerCase() + ",new " + entityName + "DTO()))\n"
                        + ".orElse(new " + entityName + "DTO())")
                .returns(DTOClass).addParameters(getMethodParameters).build();

        methods.add(GetByIdMethodSpec);

        // UpdateById 根据id 更新实体对象 =========================================
        List<ParameterSpec> updateMethodParameters = new ArrayList<>();

        // 要更新的新实体
        updateMethodParameters.add(
                ParameterSpec.builder(DTOClass, entityName.toLowerCase() + "DTO").addModifiers(Modifier.FINAL).build());

        updateMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).addModifiers(Modifier.FINAL).build());

        MethodSpec UpdateByIdMethodSpec = MethodSpec
                .methodBuilder(
                        "update" + entityName + "By" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1))
                .addModifiers(modifiers)
                .addStatement("final $T " + entityName.toLowerCase() + " = " + entityName.toLowerCase()
                        + "Repository" + ".findById(" + columnEntityField.getFieldName().toLowerCase() + ").orElse(new " + entityName + "())", modelClass)
                .addStatement("mapToEntity(" + entityName.toLowerCase() + "DTO," + entityName.toLowerCase() + ")")
                .addStatement("return " + "mapToDTO(" + entityName.toLowerCase() + "Repository.save(" + entityName.toLowerCase() + "),new " + entityName + "DTO())")
                .returns(DTOClass).addParameters(updateMethodParameters).build();

        methods.add(UpdateByIdMethodSpec);

        // deleteById 根据id 删除实体
        List<ParameterSpec> deleteMethodParameters = new ArrayList<>();

        deleteMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).addModifiers(Modifier.FINAL).build());

        MethodSpec deleteByIdMethodSpec = MethodSpec
                .methodBuilder(
                        "delete" + entityName + "By" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1))
                .addModifiers(modifiers)
                .addStatement(entityName.toLowerCase() + "Repository" + ".deleteById("
                        + columnEntityField.getFieldName().toLowerCase() + ")")
                .returns(void.class).addParameters(deleteMethodParameters).build();

        methods.add(deleteByIdMethodSpec);

        // isExist 查看某个实体是否已经存在
        List<ParameterSpec> isExitMethodParameters = new ArrayList<>();

        isExitMethodParameters.add(ParameterSpec.builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                columnEntityField.getFieldName().toLowerCase()).addModifiers(Modifier.FINAL).build());

        MethodSpec isExitMethodSpec = MethodSpec.methodBuilder("is" + entityName + "Exist").addModifiers(modifiers)
                .addStatement("return " + entityName.toLowerCase() + "Repository" + ".existsById("
                        + columnEntityField.getFieldName().toLowerCase() + ")")
                .returns(Boolean.class).addParameters(isExitMethodParameters).build();

        methods.add(isExitMethodSpec);

        // Page<model> 根据条件查询分页 TODO
        // Page<CasesSdjg> getPage(CasesSdjgCondition condition, Pageable pageable);

        ClassName listPage = ClassName.get("org.springframework.data.domain", "Page");
        ClassName condition = ClassName.get("www.ideaworks.club.form", entityName + "DTO");
        ClassName pageable = ClassName.get("org.springframework.data.domain", "Pageable");

        List<ParameterSpec> getPageMethodParameters = new ArrayList<>();

        TypeName PageClassesDTO = ParameterizedTypeName.get(listPage, DTOClass);
//        TypeName PageClasses = ParameterizedTypeName.get(listPage, modelClass);

        getPageMethodParameters.add(ParameterSpec.builder(condition, (entityName.toLowerCase() + "DTO"))
                .addModifiers(Modifier.FINAL).build());
        getPageMethodParameters.add(ParameterSpec.builder(pageable, "pageable").addModifiers(Modifier.FINAL).build());

        MethodSpec ConditionPageMethodSpec = MethodSpec.methodBuilder("getPageByCondition").addModifiers(modifiers)
                .addStatement("return " + entityName.toLowerCase() + "Repository" + ".findAll( this.createSpecification("
                        + entityName.toLowerCase() + "DTO" + "),pageable)\n.map(" + entityName.toLowerCase() + " -> { \n"
                        + entityName + "DTO " + entityName.toLowerCase() + "DTO1 = new " + entityName  + "DTO();\n"
                        + "return mapToDTO(" + entityName.toLowerCase() + "," + entityName.toLowerCase() + "DTO1 );\n"
                        + "})")
                .addParameters(getPageMethodParameters).returns(PageClassesDTO).build();

        methods.add(ConditionPageMethodSpec);

        // ====================this.createSpecification========
        ClassName specificatioName = ClassName.get("org.springframework.data.jpa.domain", "Specification");
        ClassName predicateName = ClassName.get("javax.persistence.criteria", "Predicate");

        TypeName specificatioCondition = ParameterizedTypeName.get(specificatioName, modelClass);

        StringBuilder codeBlockString = new StringBuilder();
        for (EntityField column : columns) {
            String typeString = column.getDataType();
            if (typeString.equals("String")) {
                codeBlockString.append("if(!StringUtils.isEmpty(")
                        .append(entityName.toLowerCase() + "DTO.get" + column.getFieldName().substring(0, 1).toUpperCase() + column.getFieldName().substring(1) + "()")
                        .append(")){\n").append("\tPredicate predicate = criteriaBuilder.equal(root.get(\"")
                        .append(column.getFieldName())
                        .append("\"),")
                        .append(entityName.toLowerCase() + "DTO.get" + column.getFieldName().substring(0, 1).toUpperCase() + column.getFieldName().substring(1) + "()")
                        .append("); \n").append("\tpredicates.add(predicate);\n}\n");
            } else {
                codeBlockString.append("if(" + entityName.toLowerCase() + "DTO.get" + column.getFieldName().substring(0, 1).toUpperCase() + column.getFieldName().substring(1) + "()" + "!=null){\n" + "\tPredicate predicate = criteriaBuilder.equal(root.get(\"")
                        .append(column.getFieldName()).append("\"),")
                        .append(entityName.toLowerCase() + "DTO.get" + column.getFieldName().substring(0, 1).toUpperCase() + column.getFieldName().substring(1) + "()")
                        .append("); \n")
                        .append("\tpredicates.add(predicate);\n}\n");
            }
        }

        MethodSpec specificationMethodSpec = MethodSpec.methodBuilder("createSpecification").addModifiers(modifiers)
                .addStatement(
                        "return (root,query,criteriaBuilder) -> {\n" + "List<$T> predicates = new ArrayList<$T>();\n"
                                + "if(!StringUtils.isEmpty(" + entityName.toLowerCase() + "DTO.get"
                                + columnEntityField.getFieldName().substring(0, 1).toUpperCase() + columnEntityField.getFieldName().substring(1) + "())){ \n"
                                + "\t$T predicate = criteriaBuilder.equal(root.get(\"" + columnEntityField.getFieldName() + "\"),"
                                + entityName.toLowerCase() + "DTO.get" + columnEntityField.getFieldName().substring(0, 1).toUpperCase()
                                + columnEntityField.getFieldName().substring(1) + "()"
                                + "); \n" + "\tpredicates.add(predicate);\n" + "}\n"
                                + codeBlockString
                                + "return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();"
                                + "}",
                        predicateName, predicateName, predicateName)
                .addParameter(ParameterSpec.builder(condition, (entityName.toLowerCase() + "DTO"))
                        .addModifiers(Modifier.FINAL).build())
                .returns(specificatioCondition).build();

        methods.add(specificationMethodSpec);

        // ===============================================================

//		ClassName specificatioName = ClassName.get("org.springframework.data.jpa.domain", "Specification");
        ClassName BeanUtilsName = ClassName.get("org.springframework.beans", "BeanUtils");

//		TypeName specificatioCondition = ParameterizedTypeName.get(specificatioName, DTOClass);

//		public String create(final CbjgWsSdjgDTO cbjgWsSdjgDTO) {
//			final CbjgWsSdjg cbjgWsSdjg = new CbjgWsSdjg();
//			mapToEntity(cbjgWsSdjgDTO, cbjgWsSdjg);
//			return cbjgWsSdjgRepository.save(cbjgWsSdjg).getId();
//		}

        MethodSpec mapToEntityMethodSpec = MethodSpec.methodBuilder("mapToEntity").addModifiers(modifiers)
                .addStatement(
                        "$T.copyProperties(" + entityName.toLowerCase() + "DTO" + "," + entityName.toLowerCase() + ")",
                        BeanUtilsName)
                .addStatement("return " + entityName.toLowerCase())
                .returns(modelClass)
                .addParameter(ParameterSpec.builder(DTOClass, entityName.toLowerCase() + "DTO")
                        .addModifiers(Modifier.FINAL).build())
                .addParameter(ParameterSpec.builder(modelClass, entityName.toLowerCase()).addModifiers(Modifier.FINAL)
                        .build())
                .build();

        methods.add(mapToEntityMethodSpec);

        MethodSpec mapToDTOMethodSpec = MethodSpec.methodBuilder("mapToDTO").addModifiers(modifiers)
                .addStatement("$T.copyProperties(" + entityName.toLowerCase() + "," + entityName.toLowerCase() + "DTO)",
                        BeanUtilsName)
                .addStatement("return " + entityName.toLowerCase() + "DTO")
                .returns(DTOClass)
                .addParameter(
                        ParameterSpec.builder(modelClass, entityName.toLowerCase()).addModifiers(Modifier.FINAL).build())
                .addParameter(ParameterSpec.builder(DTOClass, entityName.toLowerCase() + "DTO")
                        .addModifiers(Modifier.FINAL).build())
                .build();

        methods.add(mapToDTOMethodSpec);

        // 生成文件并返回=================================================

        List<FieldSpec> fields = new ArrayList<>();
        List<AnnotationSpec> fieldAnnotations = new ArrayList<>();

        fieldAnnotations.add(AnnotationSpec.builder(Autowired.class).build());
        fields.add(FieldBuilderFactory.build(((entityName.toLowerCase()) + "Repository").trim(), repositoryClass, null,
                fieldAnnotations));

        List<AnnotationSpec> classAnnotationSpecs = new ArrayList<>();
        classAnnotationSpecs.add(AnnotationSpec.builder(Service.class).build());

        TypeSpec serviceImplTypeSpec = TypeSpec.classBuilder(entityName + "ServiceImpl").addModifiers(Modifier.PUBLIC)
                .addMethods(methods).addAnnotations(classAnnotationSpecs).addFields(fields)
                .addSuperinterface(serviceInterface).build();

        JavaFile javaFile = JavaFile.builder("www.ideaworks.club.serviceImpl", serviceImplTypeSpec).build();

//		File file = new File("/target/directory");
//		javaFile.writeTo(file); // this will make a new File with all the data you added

        String fileString;
        fileString = javaFile.toString();

//		javaFile.writeTo();
//		javaFile.writeTo(System.out);
        return fileString;
    }

    /**
     * 生成 Controller
     */
    @Override
    public String generateController(EntityDTO entityDTO) {
        List<MethodSpec> methods = new ArrayList<>();

        String entityName = entityDTO.getEntityName(); // Entity 名称
//		String tableName = entityDTO.getTableName(); // Entity 对应的表
        String comment = entityDTO.getComment(); // Entity 说明 ApiModel 注解用
        String apiTag = entityDTO.getApiTag(); // Controller 接口说明 API（tags） 注解用

        // 实体类的类名
        ClassName modelClass = ClassName.get(("www.ideaworks.club.domain").trim(), entityName);

        // 实体类的类名
        ClassName DTOClass = ClassName.get(("www.ideaworks.club.form").trim(), entityName + "DTO");

        ClassName serviceInterface = ClassName.get(("www.ideaworks.club.service").trim(),
                entityName + "Service");


        // 实体的属性列表
        List<EntityField> columns = entityDTO.getEntityFields();

        EntityField columnEntityField = null;
        for (EntityField column : columns) {
            if (column.getIsKey()) {
                columnEntityField = column;
            }
        }

        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(Modifier.PUBLIC);

        // ------------------SAVE-----------------

        List<AnnotationSpec> saveMethodAnnotationSpecs = new ArrayList<>();
        saveMethodAnnotationSpecs.add(AnnotationSpec.builder(PostMapping.class)
                .addMember("value", "\"/" + "save" + "\"").build());
        AnnotationSpec saveapiAnnotationSpec = AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", "添加" + comment).build();
        saveMethodAnnotationSpecs.add(saveapiAnnotationSpec);

        List<ParameterSpec> saveMethodParameters = new ArrayList<>();
        saveMethodParameters.add(ParameterSpec.builder(modelClass, entityName.toLowerCase())
                .addAnnotation(AnnotationSpec.builder(RequestBody.class).build()).build());

        MethodSpec saveMethodSpec = MethodBuilderFactory.build(saveMethodAnnotationSpecs, modifiers, modelClass,
                ("save" + entityName).trim(), saveMethodParameters);

        methods.add(
                saveMethodSpec.toBuilder()
                        .addStatement(("return " + (entityName.toLowerCase())
                                + "Service" + "." + "save" + entityName
                                + "(" + entityName.toLowerCase()
                                + ")").trim())
                        .build());

        // -----------------------GET-----------------------

        List<AnnotationSpec> getAllMethodAnnotationSpecs = new ArrayList<>();
        getAllMethodAnnotationSpecs.add(AnnotationSpec.builder(GetMapping.class)
                .addMember("value", "\"/" + "all" + "\"").build());
        AnnotationSpec getapiAnnotationSpec = AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", "获取 " + comment + "列表").build();
        saveMethodAnnotationSpecs.add(getapiAnnotationSpec);


        ClassName list = ClassName.get("java.util", "List");
        TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);

        MethodSpec getMethodSpec = MethodBuilderFactory.build(getAllMethodAnnotationSpecs, modifiers, modelClasses,
                ("get" + entityName + "s").trim(), null);

        methods.add(getMethodSpec.toBuilder().addStatement(
                ("return " + (entityName.toLowerCase()) + "Service"
                        + ("." + "get" + entityName + "s" + "(")
                        + ")").trim())
                .build());

        // ----------------------GET BY ID----------------------

        List<AnnotationSpec> getMethodAnnotationSpecs = new ArrayList<>();
        getMethodAnnotationSpecs.add(AnnotationSpec.builder(GetMapping.class)
                .addMember("value", "\"/" + "{id}" + "\"").build());
        AnnotationSpec getbyidApiAnnotationSpec = AnnotationSpec.builder(ApiOperation.class).addMember("value", "$S", "根据主键获取一条 " + comment).build();
        saveMethodAnnotationSpecs.add(getbyidApiAnnotationSpec);

        List<ParameterSpec> getMethodParameters = new ArrayList<>();
        assert columnEntityField != null;
        getMethodParameters
                .add(ParameterSpec
                        .builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                                columnEntityField.getFieldName().toLowerCase())
                        .addAnnotation(
                                AnnotationSpec.builder(PathVariable.class).addMember("value",
                                        "\"" + columnEntityField.getFieldName() + "\"").build())
                        .build());

        MethodSpec getByIdMethodSpec = MethodBuilderFactory.build(getMethodAnnotationSpecs, modifiers, modelClass,
                ("get" + entityName).trim(), getMethodParameters);

        methods.add(
                getByIdMethodSpec.toBuilder()
                        .addStatement(("return " + (entityName.toLowerCase())
                                + "Service" + "." + "get" + entityName
                                + "(" + columnEntityField.getFieldName()
                                + ")").trim())
                        .build());

        // ----------------------UPDATE BY ID---------------------

        List<AnnotationSpec> updateMethodAnnotationSpecs = new ArrayList<>();
        updateMethodAnnotationSpecs.add(AnnotationSpec.builder(PutMapping.class)
                .addMember("value", "\"/" + "{id}" + "\"").build());

        List<ParameterSpec> updateMethodParameters = new ArrayList<>();

        updateMethodParameters.add(ParameterSpec.builder(modelClass, entityName.toLowerCase())
                .addAnnotation(AnnotationSpec.builder(RequestBody.class).build()).build());

        updateMethodParameters
                .add(ParameterSpec
                        .builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                                columnEntityField.getFieldName().toLowerCase())
                        .addAnnotation(
                                AnnotationSpec.builder(PathVariable.class).addMember("value",
                                        "\"" + columnEntityField.getFieldName() + "\"").build())
                        .build());

        MethodSpec updateMethodSpec = MethodBuilderFactory.build(updateMethodAnnotationSpecs, modifiers, modelClass,
                ("update" + entityName).trim(), updateMethodParameters);

        methods.add(updateMethodSpec.toBuilder()
                .addStatement(("return " + (entityName.toLowerCase())
                        + "Service" + "." + "update" + entityName
                        + "(" + entityName.toLowerCase() + ", "
                        + columnEntityField.getFieldName() + ")").trim())
                .build());

        // ---------------------DELETE BY ID------------------------

        List<AnnotationSpec> deleteMethodAnnotationSpecs = new ArrayList<>();
        deleteMethodAnnotationSpecs.add(AnnotationSpec.builder(DeleteMapping.class)
                .addMember("value", "\"/" + "{id}" + "\"").build());

        List<ParameterSpec> deleteMethodParameters = new ArrayList<>();

        deleteMethodParameters
                .add(ParameterSpec
                        .builder(SCGServiceImpl.getFieldType(columnEntityField.getDataType()),
                                columnEntityField.getFieldName().toLowerCase())
                        .addAnnotation(
                                AnnotationSpec.builder(PathVariable.class).addMember("value",
                                        "\"" + columnEntityField.getFieldName() + "\"").build())
                        .build());

        MethodSpec deleteMethodSpec = MethodBuilderFactory.build(deleteMethodAnnotationSpecs, modifiers, void.class,
                ("delete" + entityName).trim(), deleteMethodParameters);

        methods.add(deleteMethodSpec.toBuilder()
                .addStatement(((entityName.toLowerCase()) + "Service" + "."
                        + "delete" + entityName + "("
                        + columnEntityField.getFieldName() + ")").trim())
                .build());

        // ----------------CONTROLLER CLASS CREATION----------------------

        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        classAnnotations.add(AnnotationSpec.builder(RestController.class).build());
        classAnnotations.add(AnnotationSpec.builder(Api.class).addMember("tags", "\"" + apiTag + "\"").build());
        classAnnotations.add(AnnotationSpec.builder(RequestMapping.class)
                .addMember("value", "\"/" + entityName.toLowerCase() + "s\"").build());

        List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
        fieldAnnotations.add(AnnotationSpec.builder(Autowired.class).build());

        List<FieldSpec> fields = new ArrayList<>();
        fields.add(FieldBuilderFactory.build(((entityName.toLowerCase()) + "Service").trim(),
                serviceInterface, null, fieldAnnotations));

        TypeSpec classTypeSpec = ClassBuilderFactory.build((entityName + "Controller").trim(),
                fields, methods, classAnnotations);

//        FileBuilderFactory.build(
//                ("www.ideaworks.club.controller").trim(),
//                classTypeSpec);

        TypeSpec serviceImplTypeSpec = TypeSpec.classBuilder(entityName + "ServiceImpl").addModifiers(Modifier.PUBLIC)
                .addMethods(methods).addAnnotations(classAnnotations).addFields(fields)
                .addSuperinterface(serviceInterface).build();

        JavaFile javaFile = JavaFile.builder("www.ideaworks.club.serviceImpl", serviceImplTypeSpec).build();
        return javaFile.toString();
    }

    // ================================================================================
//--------------------------------------------------------------------------------GENERATE MODEL CLASS-----------------------------------------
    @Override
    public void generateModel(String tableName, List<Column> columns) {
        List<AnnotationSpec> classAnnotations = new ArrayList<>();
        List<FieldSpec> fields = new ArrayList<>();

        try {
            for (Column column : columns) {
                if (column.isPrimaryKey()) {
                    List<AnnotationSpec> fieldAnnotations = new ArrayList<>();

                    fieldAnnotations.add(AnnotationSpec.builder(GeneratedValue.class)
                            .addMember(KeyConstant.MSG_STRATEGY, KeyConstant.MSG_PERSISTENCE_IDENTITY).build());

                    fieldAnnotations.add(AnnotationSpec.builder(Id.class).build());
                    fields.add(FieldBuilderFactory.build(column.getName(), getFieldType(column.getDataType()), null,
                            fieldAnnotations));
                } else
                    fields.add(FieldBuilderFactory.build(column.getName(), getFieldType(column.getDataType()), null,
                            null));
            }

            classAnnotations.add(AnnotationSpec.builder(Entity.class).build());
            classAnnotations.add(AnnotationSpec.builder(Table.class)
                    .addMember(KeyConstant.MSG_STATEMENT_NAME, "\"" + tableName + "\"").build());

            classAnnotations.add(AnnotationSpec.builder(Data.class).build());
            classAnnotations.add(AnnotationSpec.builder(AllArgsConstructor.class).build());
            classAnnotations.add(AnnotationSpec.builder(NoArgsConstructor.class).build());
            classAnnotations.add(AnnotationSpec.builder(ToString.class).build());

            TypeSpec classTypeSpec = ClassBuilderFactory.build(tableName, fields, null, classAnnotations);

//			FileBuilderFactory.build((KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(),
//										classTypeSpec);
            JavaFile javaFile = JavaFile.builder("com.hascode.tutorial", classTypeSpec).build();
            javaFile.writeTo(System.out);
        } catch (Exception e) {
            log.error("Error : {}, {}", e.getMessage(), e.getCause());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getFieldType(String dataType) {
        Class<T> type = null;
        switch (dataType) {
            case "String":
                type = (Class<T>) String.class;
                break;
            case "Integer":
                type = (Class<T>) Integer.class;
                break;
            case "Double":
                type = (Class<T>) Double.class;
                break;
            case "Long":
                type = (Class<T>) Long.class;
                break;
            case "Byte":
                type = (Class<T>) Byte.class;
                break;
            case "Byte[]":
                type = (Class<T>) Byte.class;
                break;
            case "Float":
                type = (Class<T>) Float.class;
                break;
            case "Date":
                type = (Class<T>) Date.class;
                break;
            case "Boolean":
                type = (Class<T>) Boolean.class;
                break;
        }
        return type;
    }

//--------------------------------------------------------------------------------GENERATE REPOSITORY CLASS------------------------------------

    @Override
    public void generateRepository(String tableName, Column column) {
        List<AnnotationSpec> interfaceAnnotations = new ArrayList<>();

        try {
            interfaceAnnotations.add(AnnotationSpec.builder(Repository.class).build());

            TypeSpec interfaceTypeSpec = InterfaceBuilderFactory.buildRepository(tableName, null, null,
                    interfaceAnnotations, column);

            FileBuilderFactory.build(
                    (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_REPOSITORY).trim(),
                    interfaceTypeSpec);
        } catch (Exception e) {
            log.error("Error : {}, {}", e.getMessage(), e.getCause());
        }
    }

//--------------------------------------------------------------------------------GENERATE SERVICE CLASS------------------------------------

    @Override
    public void generateService(String tableName, Column column) {
        List<MethodSpec> methods = new ArrayList<>();
        ClassName modelClass = ClassName.get(
                (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(),
                tableName);

        List<Modifier> modifiers = new ArrayList<>();
        try {
            modifiers.add(Modifier.PUBLIC);
            modifiers.add(Modifier.ABSTRACT);
            // --------------------------------------------------SAVE
            // METHOD--------------------------------------------------------------------

            List<ParameterSpec> saveMethodParameters = new ArrayList<>();
            saveMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());
            methods.add(MethodBuilderFactory.build(null, modifiers, modelClass,
                    ("save" + tableName).trim(), saveMethodParameters));

            // ---------------------------------------------------GET
            // METHOD----------------------------------------------------------------------

            ClassName list = ClassName.get(KeyConstant.MSG_PKG_JAVA_UTIL, KeyConstant.MSG_JAVA_LIST);
            TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);

            methods.add(MethodBuilderFactory.build(null, modifiers, modelClasses,
                    ("get" + tableName + "s").trim(), null)); // Class.forName(modelName)

            // ----------------------------------------------------GET BY
            // ID---------------------------------------------------------------------

            List<ParameterSpec> getMethodParameters = new ArrayList<>();

            getMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            methods.add(MethodBuilderFactory.build(null, modifiers, modelClass,
                    ("get" + tableName).trim(), getMethodParameters));

            // ----------------------------------------------------UPDATE BY
            // ID-------------------------------------------------------------------

            List<ParameterSpec> updateMethodParameters = new ArrayList<>();

            updateMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());
            updateMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            methods.add(MethodBuilderFactory.build(null, modifiers, modelClass,
                    ("update" + tableName).trim(), updateMethodParameters));

            // ----------------------------------------------------DELETE BY
            // ID-------------------------------------------------------------------

            List<ParameterSpec> deleteMethodParameters = new ArrayList<>();

            deleteMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            methods.add(MethodBuilderFactory.build(null, modifiers, void.class,
                    ("delete" + tableName).trim(), deleteMethodParameters));

            // ----------------------------------------------------IS EXIST
            // ID--------------------------------------------------------------------

            List<ParameterSpec> isExitMethodParameters = new ArrayList<>();

            isExitMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            methods.add(MethodBuilderFactory.build(null, modifiers, boolean.class,
                    (KeyConstant.MSG_METHOD_IS_EXIST + tableName).trim(), isExitMethodParameters));

            // ---------------------------------------------------SERVICE INTERFACE
            // GENERATION---------------------------------------------------

            TypeSpec interfaceTypeSpec = InterfaceBuilderFactory
                    .buildService((tableName + "Service").trim(), null, methods, null, null);

            FileBuilderFactory.build(
                    (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_SERVICE).trim(),
                    interfaceTypeSpec);
        } catch (Exception e) {
            log.error("Error : {}, {}", e.getMessage(), e.getCause());
        }
    }

//--------------------------------------------------------------------------------GENERATE SERVICE IMPLEMENTATION--------------------------------

    @Override
    public void generateServiceImpl(String tableName, Column column) {
        List<MethodSpec> methods = new ArrayList<>();

        ClassName modelClass = ClassName.get(
                (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(),
                tableName);

        ClassName serviceInterface = ClassName.get(
                (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_SERVICE).trim(),
                tableName + "Service");

        ClassName repositoryClass = ClassName.get(
                (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_REPOSITORY).trim(),
                tableName + KeyConstant.MSG_NAME_REPOSITORY);
        try {
            List<Modifier> modifiers = new ArrayList<>();
            modifiers.add(Modifier.PUBLIC);

            List<AnnotationSpec> methodAnnotationSpecs = new ArrayList<>();
            methodAnnotationSpecs.add(AnnotationSpec.builder(Override.class).build());

            // ----------------------SAVE METHOD--------------------

            List<ParameterSpec> saveMethodParameters = new ArrayList<>();
            saveMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());

            MethodSpec saveMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClass,
                    ("save" + tableName).trim(), saveMethodParameters);

            methods.add(saveMethodSpec.toBuilder()
                    .addStatement(("return " + (tableName.toLowerCase())
                            + KeyConstant.MSG_NAME_REPOSITORY + KeyConstant.MSG_REPO_METHOD_DOT_SAVE
                            + tableName.toLowerCase() + ")").trim())
                    .build());

            // ---------------------------------------------------GET
            // METHOD----------------------------------------------------------------------

            ClassName list = ClassName.get(KeyConstant.MSG_PKG_JAVA_UTIL, KeyConstant.MSG_JAVA_LIST);
            TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);

            MethodSpec getMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClasses,
                    ("get" + tableName + "s").trim(), null);

            methods.add(getMethodSpec.toBuilder()
                    .addStatement(("return " + (tableName.toLowerCase())
                            + KeyConstant.MSG_NAME_REPOSITORY + KeyConstant.MSG_REPO_METHOD_DOT_FINDALL).trim())
                    .build());

            // ----------------------------------------------------GET BY
            // ID---------------------------------------------------------------------

            List<ParameterSpec> getMethodParameters = new ArrayList<>();

            getMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            MethodSpec getByIdMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClass,
                    ("get" + tableName).trim(), getMethodParameters);

            methods.add(getByIdMethodSpec.toBuilder()
                    .addStatement(("return " + (tableName.toLowerCase())
                            + KeyConstant.MSG_NAME_REPOSITORY + KeyConstant.MSG_REPO_METHOD_DOT_FIND_BY_ID
                            + column.getName() + ")"
                            + KeyConstant.MSG_REPO_METHOD_DOT_GET).trim())
                    .build());

            // ----------------------------------------------------UPDATE BY
            // ID-------------------------------[NEED IMPROVEMENT HERE]---------------------

            List<ParameterSpec> updateMethodParameters = new ArrayList<>();

            updateMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase()).build());
            updateMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            MethodSpec updateMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, modelClass,
                    ("update" + tableName).trim(), updateMethodParameters);

            methods.add(updateMethodSpec.toBuilder()
                    .addStatement(("return " + (tableName.toLowerCase())
                            + KeyConstant.MSG_NAME_REPOSITORY + KeyConstant.MSG_REPO_METHOD_DOT_SAVE
                            + tableName.toLowerCase() + ")").trim())
                    .build());

            // ----------------------------------------------------DELETE BY
            // ID---------------------------------------------------------------------------

            List<ParameterSpec> deleteMethodParameters = new ArrayList<>();

            deleteMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            MethodSpec deleteMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, void.class,
                    ("delete" + tableName).trim(), deleteMethodParameters);

            methods.add(deleteMethodSpec.toBuilder()
                    .addStatement(((tableName.toLowerCase()) + KeyConstant.MSG_NAME_REPOSITORY
                            + KeyConstant.MSG_REPO_METHOD_DOT_DELETE_BY_ID + column.getName()
                            + ")").trim())
                    .build());

            // ----------------------------------------------------IS EXIST
            // ID--------------------------------------------------------------------

            List<ParameterSpec> isExitMethodParameters = new ArrayList<>();

            isExitMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .build());

            MethodSpec isExistMethodSpec = MethodBuilderFactory.build(methodAnnotationSpecs, modifiers, boolean.class,
                    (KeyConstant.MSG_METHOD_IS_EXIST + tableName).trim(), isExitMethodParameters);

            methods.add(isExistMethodSpec.toBuilder()
                    .addStatement((("return" + tableName.toLowerCase())
                            + KeyConstant.MSG_NAME_REPOSITORY + KeyConstant.MSG_REPO_METHOD_DOT_IS_EXIST_BY_ID
                            + column.getName() + ")").trim())
                    .build());

            // ---------------------------------------------------SERVICE CLASS
            // IMPLEMENTATION-------------------------------------------------

            List<FieldSpec> fields = new ArrayList<>();
            List<AnnotationSpec> fieldAnnotations = new ArrayList<>();

            fieldAnnotations.add(AnnotationSpec.builder(Autowired.class).build());
            fields.add(FieldBuilderFactory.build(((tableName.toLowerCase()) + KeyConstant.MSG_NAME_REPOSITORY).trim(),
                    repositoryClass, null, fieldAnnotations));

            List<AnnotationSpec> classAnnotationSpecs = new ArrayList<>();
            classAnnotationSpecs.add(AnnotationSpec.builder(Service.class).build());

            TypeSpec interfaceTypeSpec = ClassBuilderFactory.build(
                    (tableName + "Service" + "Impl").trim(), fields, methods, classAnnotationSpecs,
                    serviceInterface);

            FileBuilderFactory.build(
                    (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_SERVICE).trim(),
                    interfaceTypeSpec);
        } catch (Exception e) {
            log.error("Error : {}, {}", e.getMessage(), e.getCause());
        }
    }

//--------------------------------------------------------------------------------GENERATE CONTROLLER CLASS-----------------------------------------

    @Override
    public void generateController(String tableName, Column column) {
        List<MethodSpec> methods = new ArrayList<>();

        ClassName modelClass = ClassName.get(
                (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_MODEL).trim(),
                tableName);
        ClassName serviceInterface = ClassName.get(
                (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_SERVICE).trim(),
                tableName + "Service");
        List<Modifier> modifiers = new ArrayList<>();
        try {
            modifiers.add(Modifier.PUBLIC);

            // --------------------------------------------------SAVE
            // METHOD--------------------------------------------------------------------

            List<AnnotationSpec> saveMethodAnnotationSpecs = new ArrayList<>();
            saveMethodAnnotationSpecs.add(AnnotationSpec.builder(PostMapping.class)
                    .addMember("value", "\"/" + KeyConstant.MSG_PATH_SAVE + "\"").build());

            List<ParameterSpec> saveMethodParameters = new ArrayList<>();
            saveMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase())
                    .addAnnotation(AnnotationSpec.builder(RequestBody.class).build()).build());

            MethodSpec saveMethodSpec = MethodBuilderFactory.build(saveMethodAnnotationSpecs, modifiers, modelClass,
                    ("save" + tableName).trim(), saveMethodParameters);

            methods.add(
                    saveMethodSpec.toBuilder()
                            .addStatement(("return " + (tableName.toLowerCase())
                                    + "Service" + "." + "save" + tableName
                                    + "(" + tableName.toLowerCase()
                                    + ")").trim())
                            .build());

            // ---------------------------------------------------GET
            // METHOD----------------------------------------------------------------------

            List<AnnotationSpec> getAllMethodAnnotationSpecs = new ArrayList<>();
            getAllMethodAnnotationSpecs.add(AnnotationSpec.builder(GetMapping.class)
                    .addMember("value", "\"/" + KeyConstant.MSG_PATH_GET_ALL + "\"").build());

            ClassName list = ClassName.get(KeyConstant.MSG_PKG_JAVA_UTIL, KeyConstant.MSG_JAVA_LIST);
            TypeName modelClasses = ParameterizedTypeName.get(list, modelClass);

            MethodSpec getMethodSpec = MethodBuilderFactory.build(getAllMethodAnnotationSpecs, modifiers, modelClasses,
                    ("get" + tableName + "s").trim(), null);

            methods.add(getMethodSpec.toBuilder().addStatement(
                    ("return" + (tableName.toLowerCase()) + "Service"
                            + ("." + "get" + tableName + "s" + "(")
                            + ")").trim())
                    .build());

            // ----------------------------------------------------GET BY
            // ID---------------------------------------------------------------------

            List<AnnotationSpec> getMethodAnnotationSpecs = new ArrayList<>();
            getMethodAnnotationSpecs.add(AnnotationSpec.builder(GetMapping.class)
                    .addMember("value", "\"/" + "{id}" + "\"").build());

            List<ParameterSpec> getMethodParameters = new ArrayList<>();
            getMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .addAnnotation(AnnotationSpec.builder(PathVariable.class)
                            .addMember("value", "\"" + column.getName() + "\"").build())
                    .build());

            MethodSpec getByIdMethodSpec = MethodBuilderFactory.build(getMethodAnnotationSpecs, modifiers, modelClass,
                    ("get" + tableName).trim(), getMethodParameters);

            methods.add(
                    getByIdMethodSpec.toBuilder()
                            .addStatement(("return " + (tableName.toLowerCase())
                                    + "Service" + "." + "get" + tableName
                                    + "(" + column.getName()
                                    + ")").trim())
                            .build());

            // ----------------------------------------------------UPDATE BY
            // ID------------------------------------------------------------------------

            List<AnnotationSpec> updateMethodAnnotationSpecs = new ArrayList<>();
            updateMethodAnnotationSpecs.add(AnnotationSpec.builder(PutMapping.class)
                    .addMember("value", "\"/" + "{id}" + "\"").build());

            List<ParameterSpec> updateMethodParameters = new ArrayList<>();

            updateMethodParameters.add(ParameterSpec.builder(modelClass, tableName.toLowerCase())
                    .addAnnotation(AnnotationSpec.builder(RequestBody.class).build()).build());

            updateMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .addAnnotation(AnnotationSpec.builder(PathVariable.class)
                            .addMember("value", "\"" + column.getName() + "\"").build())
                    .build());

            MethodSpec updateMethodSpec = MethodBuilderFactory.build(updateMethodAnnotationSpecs, modifiers, modelClass,
                    ("update" + tableName).trim(), updateMethodParameters);

            methods.add(updateMethodSpec.toBuilder()
                    .addStatement(("return " + (tableName.toLowerCase())
                            + "Service" + "." + "update" + tableName
                            + "(" + tableName.toLowerCase() + ", " + column.getName()
                            + ")").trim())
                    .build());

            // ----------------------------------------------------DELETE BY
            // ID---------------------------------------------------------------------------

            List<AnnotationSpec> deleteMethodAnnotationSpecs = new ArrayList<>();
            deleteMethodAnnotationSpecs.add(AnnotationSpec.builder(DeleteMapping.class)
                    .addMember("value", "\"/" + "{id}" + "\"").build());

            List<ParameterSpec> deleteMethodParameters = new ArrayList<>();

            deleteMethodParameters.add(ParameterSpec
                    .builder(SCGServiceImpl.getFieldType(column.getDataType()), column.getName().toLowerCase())
                    .addAnnotation(AnnotationSpec.builder(PathVariable.class)
                            .addMember("value", "\"" + column.getName() + "\"").build())
                    .build());

            MethodSpec deleteMethodSpec = MethodBuilderFactory.build(deleteMethodAnnotationSpecs, modifiers, void.class,
                    ("delete" + tableName).trim(), deleteMethodParameters);

            methods.add(deleteMethodSpec.toBuilder()
                    .addStatement(((tableName.toLowerCase()) + "Service" + "."
                            + "delete" + tableName + "("
                            + column.getName() + ")").trim())
                    .build());

            // -----------------------------------------------------------------------CONTROLLER
            // CLASS CREATION-----------------------------------------

            List<AnnotationSpec> classAnnotations = new ArrayList<>();
            classAnnotations.add(AnnotationSpec.builder(RestController.class).build());
            classAnnotations.add(AnnotationSpec.builder(RequestMapping.class)
                    .addMember("value", "\"/" + tableName.toLowerCase() + "s\"").build());

            List<AnnotationSpec> fieldAnnotations = new ArrayList<>();
            fieldAnnotations.add(AnnotationSpec.builder(Autowired.class).build());

            List<FieldSpec> fields = new ArrayList<>();
            fields.add(FieldBuilderFactory.build(((tableName.toLowerCase()) + "Service").trim(),
                    serviceInterface, null, fieldAnnotations));

            TypeSpec classTypeSpec = ClassBuilderFactory.build((tableName + KeyConstant.MSG_NAME_CONTROLLER).trim(),
                    fields, methods, classAnnotations);

            FileBuilderFactory.build(
                    (KeyConstant.MSG_PKG_COM_DOT + tableName.toLowerCase() + KeyConstant.MSG_PKG_DOT_CONTROLLER).trim(),
                    classTypeSpec);
        } catch (Exception e) {
            log.error("Error : {}, {}", e.getMessage(), e.getCause());
        }
    }

}
