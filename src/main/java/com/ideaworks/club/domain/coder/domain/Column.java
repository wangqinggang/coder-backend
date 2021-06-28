package com.ideaworks.club.domain.coder.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Column implements Serializable
{
	private static final long serialVersionUID = -3099525840580580263L;

	private String name;
	
	private String dataType;
	
	private boolean isPrimaryKey;
	
}
