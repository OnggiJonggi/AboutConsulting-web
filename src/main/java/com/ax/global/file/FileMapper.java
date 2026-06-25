package com.ax.global.file;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

	public int insertInfo(FileInfoVO.Insert insert);

	public int insertMapping(FileInfoVO.InsertMapping insertMapping);

	public int insertHistory(FileInfoVO.InsertHistory insertHistory);
	
}
