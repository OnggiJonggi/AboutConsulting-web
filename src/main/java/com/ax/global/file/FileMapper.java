package com.ax.global.file;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

	public void insertInfo(FileInfoVO.Registor registor);

	public void insertMapping(FileInfoVO.InsertMapping insertMapping);

	public void insertHistory(FileInfoVO.InsertHistory insertHistory);
	
}
