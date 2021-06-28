package com.ideaworks.club.domain.coder.domain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 文件。
 * @author Ben
 * @since 2.0.0
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
public class File implements Serializable {
	
	private static final long serialVersionUID = -3196248117443290051L;
	
	/** json 简单视图 */
    public interface SimpleView {}

    /** json 详情视图 */
    public interface DetailView extends SimpleView {}
	
	@NonNull
	
	private String filename;
	
	@NonNull
	private byte[] bytes;
	
	
	private String mime;
	
	
	private String name;
	
	
	private String id;
	
	
	private String relativePath;
	
	
	private String ywsj;
	
	
	public long getSize() {
		return bytes == null ? 0 : bytes.length;
	}
	
	
	public String getSuffix() {
		String suffix = StringUtils.substringAfterLast(filename, ".").toLowerCase();
		return suffix;
	}

}
