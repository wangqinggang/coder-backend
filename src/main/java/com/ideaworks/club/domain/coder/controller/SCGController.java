package com.ideaworks.club.domain.coder.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.lang.model.element.Modifier;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
@RequestMapping(value = "/api/code")
@Tag(name = "Coder-backend Java code generator api")
public class SCGController {
    @Autowired
    private SCGService scgService;

    @GetMapping("/hello")
    public String HelloWorld() {
        return "";
    }

    @PostMapping("/domainFile")
    public void generateDomainFile(@RequestBody EntityDTO entityDTO, HttpServletResponse response) throws ClassNotFoundException, IOException {
        String fileString = scgService.generateDomain(entityDTO);
        String fileName = entityDTO.getEntityName() + ".java";
        downloadFile(entityDTO, response, fileString,fileName);
    }
    @PostMapping("/dtoFile")
    public void generateDTOFile(@RequestBody EntityDTO entityDTO, HttpServletResponse response) throws ClassNotFoundException, IOException {
        String fileString = scgService.generateDTO(entityDTO);
        String fileName = entityDTO.getEntityName() + "DTO.java";
        downloadFile(entityDTO, response, fileString,fileName);
    }
    @PostMapping("/serviceFile")
    public void generateServiceFile(@RequestBody EntityDTO entityDTO, HttpServletResponse response) throws ClassNotFoundException, IOException {
        String fileString = scgService.generateService(entityDTO);
        String fileName = entityDTO.getEntityName() + "Service.java";
        downloadFile(entityDTO, response, fileString,fileName);
    }
    @PostMapping("/service-implFile")
    public void generateImplFile(@RequestBody EntityDTO entityDTO, HttpServletResponse response) throws ClassNotFoundException, IOException {
        String fileString = scgService.generateServiceImpl(entityDTO);
        String fileName = entityDTO.getEntityName() + "ServiceImpl.java";
        downloadFile(entityDTO, response, fileString,fileName);
    }
    @PostMapping("/repositoryFile")
    public void generateRepositoryFile(@RequestBody EntityDTO entityDTO, HttpServletResponse response) throws ClassNotFoundException, IOException {
        String fileString = scgService.generateRepository(entityDTO);
        String fileName = entityDTO.getEntityName() + "Repository.java";
        downloadFile(entityDTO, response, fileString,fileName);
    }
    @PostMapping("/controllerFile")
    public void generateControllerFile(@RequestBody EntityDTO entityDTO, HttpServletResponse response) throws ClassNotFoundException, IOException {
        String fileString = scgService.generateController(entityDTO);
        String fileName = entityDTO.getEntityName() + "Controller.java";
        downloadFile(entityDTO, response, fileString,fileName);
    }

    /**
     * 下载文件将 string 内容写入文件进行下载
     * @param entityDTO
     * @param response
     * @param fileString
     * @throws IOException
     */
    private void downloadFile(EntityDTO entityDTO, HttpServletResponse response, String fileString,String fileName) throws IOException {
        InputStream is = IOUtils.toInputStream(fileString, StandardCharsets.UTF_8);
        response.setContentLength(is.available());
        response.setContentType("application/octet-stream");
        response.setHeader("fileName",fileName);
        response.setHeader("Access-Control-Expose-Headers","fileName");
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName + "\""));
        FileCopyUtils.copy(is, response.getOutputStream());
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
    public ResponseEntity<Object> generateRepository(@RequestBody EntityDTO entityDTO) {
        String file = scgService.generateRepository(entityDTO);
        return ResponseEntity.ok(file);
    }

    @PostMapping("/service")
    public ResponseEntity<Object> generateService(@RequestBody EntityDTO entityDTO) {
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
        String file = scgService.generateController(entityDTO);
        return ResponseEntity.ok(file);
    }


    // ==========================================================================

    //	@PostMapping("/model")
    public ResponseEntity<Object> generateModel(@RequestParam("tableName") String tableName,
                                                @RequestBody List<Column> columns) {
        scgService.generateModel(tableName, columns);
        return ResponseEntity.ok().build();
    }

//-------------------------------------------------------------------CONTROLLER CLASS GENERATION-------------------------------

    //	@PostMapping("/controller1")
    public ResponseEntity<Object> generateController1(@RequestParam("tableName") String tableName,
                                                      @RequestBody Column column) {
        scgService.generateController(tableName, column);
        return ResponseEntity.ok().build();
    }

//-------------------------------------------------------------------SERVICE INTERFACE GENERATION-------------------------------

    //	@PostMapping("/service1")
    public ResponseEntity<Object> generateService1(@RequestParam("tableName") String tableName,
                                                   @RequestBody Column column) {
        scgService.generateService(tableName, column);
        return ResponseEntity.ok().build();
    }

//-------------------------------------------------------------------SERVICE CLASS IMPLEMENTATION---------------------------

    //	@PostMapping("/service-impl1")
    public ResponseEntity<Object> generateServiceImpl1(@RequestParam("tableName") String tableName,
                                                       @RequestBody Column column) {
        scgService.generateServiceImpl(tableName, column);
        return ResponseEntity.ok().build();
    }

//-------------------------------------------------------------------REPOSITORY INTERFACE GENERATION---------------------------

    //	@PostMapping("/repository1")
    public ResponseEntity<Object> generateRepository1(@RequestParam("tableName") String tableName,
                                                      @RequestBody Column column) {
        scgService.generateRepository(tableName, column);
        return ResponseEntity.ok().build();
    }

    //-------------------------------------------------------------------END-------------------------------------------------------
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
}
