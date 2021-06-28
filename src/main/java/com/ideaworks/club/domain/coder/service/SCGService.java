package com.ideaworks.club.domain.coder.service;

import java.util.List;

import com.ideaworks.club.domain.coder.domain.Column;
import com.ideaworks.club.domain.coder.domain.EntityDTO;


public interface SCGService 
{
	/**
	 * 根据 EntityDTO 生成 Domain
	 * @param entity
	 * @return String
	 * @throws ClassNotFoundException 
	 */
	public String generateDomain(EntityDTO entity) throws ClassNotFoundException;
	
	/**
	 * 根据 EntityDTO 生成 DTO
	 * @param entity
	 * @return String
	 * @throws ClassNotFoundException 
	 */
	public String generateDTO(EntityDTO entity) throws ClassNotFoundException;
	
	/**
	 * 根据 EntityDTO 生成 Service
	 * @param entityDTO
	 * @return
	 */
	public String generateService(EntityDTO entityDTO);
	
	/**
	 * 生成 repository
	 * @param entityDTO
	 * @return
	 */
	public String generateRepository(EntityDTO entityDTO);
	
	/**
	 * 生成 serviceImpl
	 * @param entityDTO
	 * @return
	 */
	public String generateServiceImpl(EntityDTO entityDTO);
	
	/**
	 * 生成 Controller
	 * @param entityDTO
	 * @return
	 */
	public String generateController(EntityDTO entityDTO);
	
	
	// ===============================================================
	public void generateModel(String tableName, List<Column> columns);

	public void generateController(String tableName, Column column);
	
	public void generateService(String tableName, Column column);

	public void generateRepository(String tableName, Column column);

	public void generateServiceImpl(String tableName, Column column);

	

	


	
}
