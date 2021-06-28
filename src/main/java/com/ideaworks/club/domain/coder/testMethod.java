package com.ideaworks.club.domain.coder;

import java.io.IOException;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * 测试生成类与方法
 * 
 * @author 王庆港
 * @version 1.0.0
 */
public class testMethod {

	public static void main(String[] args) throws IOException {
		String className = "UserService";
		
		// 创建方法
		MethodSpec greetCustomer = MethodSpec
				.methodBuilder("greetCustomer") // 设置方法名
				.addModifiers(Modifier.PUBLIC)  // 设置修饰符
				.returns(String.class) // 设置返回值类型
				.addParameter(String.class, "name")   // 设置变量
				.addStatement("return $S+$N", "Welcome, ", "name")  // 添加语句   $S 为文本    $N  为 String 变量
				.build();
		
		//  创建类 ，类名为 User
		TypeSpec customerService = TypeSpec
				.classBuilder(className)
				.addModifiers(Modifier.PUBLIC)
				.addMethod(greetCustomer)  // 设置类的方法
				.build();
		
		// ========================================================================================
		
		MethodSpec doubleNumber = MethodSpec.methodBuilder("doubleNumber")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(long.class, "number")
				.returns(long.class)
				.addStatement("return $L*2", "number")   // $L 为 Long 型变量
				.build();
		
		MethodSpec printDoubleNumber = MethodSpec.methodBuilder("printDoubleNumber")
				.addModifiers(Modifier.PUBLIC)
				.addParameter(long.class, "number")
				.addStatement("$T.out.println(\"Your number doubled is: \"+$N($L))", System.class, doubleNumber, "number")
				// $T  代表 类型  如 String.class
				// $N() 为方法变量
				
				.build();
		
		TypeSpec numberUtil = TypeSpec.classBuilder("NumberUtil")
				.addMethod(doubleNumber)
				.addMethod(printDoubleNumber)
				.build();
		
		// ====================================================================================================
		
		AnnotationSpec path = AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", "/{id}").build();  // @GetMapping("/{id}")
		
		AnnotationSpec classParam = AnnotationSpec.builder(RequestMapping.class).addMember("value", "$S", "/book").build(); // @RequestMapping("/book")
		
		AnnotationSpec pathParam = AnnotationSpec.builder(PathVariable.class)
				.addMember("value", "$S", "id")
				.build(); // @PathVariable("id") final long id
		
		ParameterSpec id = ParameterSpec.builder(long.class, "id", Modifier.FINAL)
				.addAnnotation(pathParam)
				.build();
		
		MethodSpec findById = MethodSpec.methodBuilder("findById")
				.addAnnotation(path)
				.addAnnotation(Autowired.class)
				.returns(ResponseEntity.class).addParameter(id)
				.addStatement("return $T.ok().build()", ResponseEntity.class).build();
		
		 ClassName Userservice = ClassName.get("com.ideaworks", "UserService");
		 
		FieldSpec  fieldSpec = FieldSpec.builder(Userservice, "userService", Modifier.PRIVATE).addAnnotation(Autowired.class)
				.build();
		
		TypeSpec bookService = TypeSpec.classBuilder("BookService")
				.addMethod(findById)
				.addField(fieldSpec)
				.addAnnotation(classParam)
				.build();
		
		JavaFile javaFile = JavaFile.builder("com.hascode.tutorial", bookService).build();
		javaFile.writeTo(System.out);
		
//		JavaFile javaFile = JavaFile.builder("com.hascode.tutorial", numberUtil).build();
//		javaFile.writeTo(System.out);
		
		
//		JavaFile javaFile = JavaFile.builder("com.ideaworks.coder", customerService).build();
//		javaFile.writeTo(System.out);
	}

}
