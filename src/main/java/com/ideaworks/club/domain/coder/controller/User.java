package com.ideaworks.club.domain.coder.controller;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Table(name = "USER")
@ApiModel("用户表")
@Accessors(chain = true)
public final class User {
	@Id
	@Column(name = "AGE", nullable = true)
	@ApiModelProperty(name = "年龄")
	private Integer age;

	@Column(name = "NAME", nullable = true, length = 100)
	@ApiModelProperty(name = "名称")
	private String name;

	@Column(name = "KSSJ", nullable = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GTM+8")
	@ApiModelProperty(name = "开始时间")
	private Date kssj;
}