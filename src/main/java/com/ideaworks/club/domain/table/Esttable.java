package com.ideaworks.club.domain.table;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.lang.Integer;
import java.lang.String;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
//@Table(
//        name = "ESTTABLE"
//)
@ApiModel("表设计表")
@Accessors(
        chain = true
)
public final class Esttable {
    @Id
    @Column(
            name = "BH",
            nullable = false,
            length = 4
    )
    @ApiModelProperty(
            name = "编号主键"
    )
    private String bh;

    @Column(
            name = "USERID",
            nullable = false,
            length = 20
    )
    @ApiModelProperty(
            name = "用户名"
    )
    private String userid;

    @Column(
            name = "TID",
            nullable = true
    )
    @ApiModelProperty(
            name = "表名"
    )
    private Integer tid;

    @Column(
            name = "TNAME",
            nullable = true
    )
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd",
            timezone = "GTM+8"
    )
    @ApiModelProperty(
            name = "表说明"
    )
    private Date tname;
}