package com.ideaworks.club.domain.coder.controller;

import java.util.Date;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ideaworks.club.domain.coder.domain.Column;
import com.ideaworks.club.domain.coder.domain.EntityDTO;
import com.ideaworks.club.domain.coder.service.SCGService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/code")
@Tag(name = "SCGController")
public class SCGController {
	@Autowired
	private SCGService scgService;

	@GetMapping("/hello")
	public String HelloWorld() {
//		MethodSpec main = MethodSpec.methodBuilder("main")
//			    .addCode(""
//			        + "int total = 0;\n"
//			        + "for (int i = 0; i < 10; i++) {\n"
//			        + "  total += i;\n"
//			        + "}\n")
//			    .build();

//		MethodSpec main = computeRange("add", 0,100,"+");
//		returnDate();
		return "";
	}

	private MethodSpec computeRange(String name, int from, int to, String op) {
		return MethodSpec.methodBuilder(name).returns(int.class).addStatement("int result = 0")
				.beginControlFlow("for (int i = $L; i < $L; i++)", from, to).addStatement("result = result $L i", op)
				.endControlFlow().addStatement("return result").build();
	}

	private void returnDate() {
		MethodSpec today = MethodSpec.methodBuilder("getDate").returns(Date.class)
				.addStatement("return new $T()", Date.class).build();

		TypeSpec typeSpec = TypeSpec.classBuilder("helloWorld").addModifiers(Modifier.PUBLIC, Modifier.FINAL)
				.addMethod(today).build();

		JavaFile javaFile = JavaFile.builder("com.code.helloworld", typeSpec).build();
//		File file = new File("D:/code");
//		try {
//			javaFile.writeTo(file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

//-------------------------------------------------------------------MODEL CLASS GENERATION------------------------------------	

	@PostMapping("/domain")
	public ResponseEntity<Object> generateDomain(@RequestBody EntityDTO entityDTO) throws ClassNotFoundException {
		String file = scgService.generateDomain(entityDTO);
		return ResponseEntity.ok(file);
	}
	
	@PostMapping("/dto")
	public ResponseEntity<Object> generateDTO(@RequestBody EntityDTO entityDTO) throws ClassNotFoundException {
		String file = scgService.generateDTO(entityDTO);
		return ResponseEntity.ok(file);
	}
	
	@PostMapping("/repository")
	public ResponseEntity<Object> generateRepository( @RequestBody EntityDTO entityDTO) {
		String file = scgService.generateRepository(entityDTO);
		return ResponseEntity.ok(file);
	}

	@PostMapping("/service")
	public ResponseEntity<Object> generateService( @RequestBody EntityDTO entityDTO) {
		String file = scgService.generateService(entityDTO);
		return ResponseEntity.ok(file);
	}
	
	@PostMapping("/service-impl")
	public ResponseEntity<Object> generateServiceImpl(@RequestBody EntityDTO entityDTO) {
		String file = scgService.generateServiceImpl(entityDTO);
		return ResponseEntity.ok(file);
	}
	
	@PostMapping("/controller")
	public ResponseEntity<Object> generateController(@RequestBody EntityDTO entityDTO) {
		String file =scgService.generateController(entityDTO);
		return ResponseEntity.ok(file);
	}
	
	
	// ==========================================================================
	
	@PostMapping("/model")
	public ResponseEntity<Object> generateModel(@RequestParam("tableName") String tableName,
			@RequestBody List<Column> columns) {
		scgService.generateModel(tableName, columns);
		return ResponseEntity.ok().build();
	}

//-------------------------------------------------------------------CONTROLLER CLASS GENERATION-------------------------------

	@PostMapping("/controller1")
	public ResponseEntity<Object> generateController1(@RequestParam("tableName") String tableName,
			@RequestBody Column column) {
		scgService.generateController(tableName, column);
		return ResponseEntity.ok().build();
	}

//-------------------------------------------------------------------SERVICE INTERFACE GENERATION-------------------------------

	@PostMapping("/service1")
	public ResponseEntity<Object> generateService1(@RequestParam("tableName") String tableName,
			@RequestBody Column column) {
		scgService.generateService(tableName, column);
		return ResponseEntity.ok().build();
	}

//-------------------------------------------------------------------SERVICE CLASS IMPLEMENTATION---------------------------

	@PostMapping("/service-impl1")
	public ResponseEntity<Object> generateServiceImpl1(@RequestParam("tableName") String tableName,
			@RequestBody Column column) {
		scgService.generateServiceImpl(tableName, column);
		return ResponseEntity.ok().build();
	}

//-------------------------------------------------------------------REPOSITORY INTERFACE GENERATION---------------------------

	@PostMapping("/repository1")
	public ResponseEntity<Object> generateRepository1(@RequestParam("tableName") String tableName,
			@RequestBody Column column) {
		scgService.generateRepository(tableName, column);
		return ResponseEntity.ok().build();
	}

//-------------------------------------------------------------------END-------------------------------------------------------

}
