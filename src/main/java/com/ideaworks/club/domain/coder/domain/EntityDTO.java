package com.ideaworks.club.domain.coder.domain;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 前端传递到后端的值
 * @author 王庆港
 * @version 1.0.0
*/
@Data
@Accessors(chain = true)
@ApiModel("创建领域对象实体信息")
public class EntityDTO {

	@ApiModelProperty("Entity 名称")
	private String entityName;
	
	@ApiModelProperty("Entity 对应的表")
	private String tableName; 
	
	@ApiModelProperty("Entity 说明 ApiModel 注解用")
	private String comment;
	
	@ApiModelProperty("// Controller 接口说明 API（tags） 注解用")
	private String apiTag; 
	
	@ApiModelProperty("实体包含的属性列表")
	private List<EntityField> entityFields;
}

