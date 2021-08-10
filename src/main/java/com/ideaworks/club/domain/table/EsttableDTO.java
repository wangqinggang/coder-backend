package com.ideaworks.club.domain.table;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.lang.Integer;
import java.lang.String;
import java.util.Date;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class EsttableDTO {
    @ApiModelProperty(
            name = "编号主键"
    )
    private String bh;

    @ApiModelProperty(
            name = "用户名"
    )
    private String userid;

    @ApiModelProperty(
            name = "表名"
    )
    private Integer tid;

    @ApiModelProperty(
            name = "表说明"
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GTM+8"
    )
    private Date tname;
}
