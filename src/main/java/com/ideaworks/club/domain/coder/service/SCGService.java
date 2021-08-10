package com.ideaworks.club.domain.coder.service;

import java.util.List;

import com.ideaworks.club.domain.coder.domain.Column;
import com.ideaworks.club.domain.coder.domain.EntityDTO;


public interface SCGService {
    /**
     * 根据 EntityDTO 生成 Domain
     *
     * @param entity 实体对象
     * @return String 生成代码
     * @throws ClassNotFoundException ClassNotFoundException
     */
    String generateDomain(EntityDTO entity) throws ClassNotFoundException;

    /**
     * 根据 EntityDTO 生成 DTO
     *
     * @param entity 实体对象
     * @return String 生成代码
     * @throws ClassNotFoundException ClassNotFoundException
     */
    String generateDTO(EntityDTO entity) throws ClassNotFoundException;

    /**
     * 根据 EntityDTO 生成 Service
     *
     * @param entityDTO 实体对象
     * @return 生成代码
     */
    String generateService(EntityDTO entityDTO);

    /**
     * 生成 repository
     *
     * @param entityDTO 实体对象
     * @return 生成代码
     */
    String generateRepository(EntityDTO entityDTO);

    /**
     * 生成 serviceImpl
     *
     * @param entityDTO 实体对象
     * @return 生成代码
     */
    String generateServiceImpl(EntityDTO entityDTO);

    /**
     * 生成 Controller
     *
     * @param entityDTO 实体对象
     * @return 生成代码
     */
    String generateController(EntityDTO entityDTO);


    // ===============================================================
    void generateModel(String tableName, List<Column> columns);

    void generateController(String tableName, Column column);

    void generateService(String tableName, Column column);

    void generateRepository(String tableName, Column column);

    void generateServiceImpl(String tableName, Column column);


}
