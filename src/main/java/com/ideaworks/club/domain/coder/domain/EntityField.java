package com.ideaworks.club.domain.coder.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 实体属性列表
 * @author william
 * @version 1.0.0
*/
@Data
@Accessors(chain = true)
public class EntityField {

	@ApiModelProperty("属性数据类型")
	private String dataType;
	
	@ApiModelProperty("属性名称")
	private String fieldName; 
	
	@ApiModelProperty("属性对应数据库字段名称")
	private String tableFieldName; 
	
	@ApiModelProperty("属性长度（字符串时使用）")
	private Integer length; 
	
	@ApiModelProperty("属性注释 ApiModelProperty")
	private String fieldComment;
	
	@ApiModelProperty("是否为主键")
	private Boolean isKey;
	
	@ApiModelProperty("是否为必填字段")
	private Boolean isRequired;
	
}
