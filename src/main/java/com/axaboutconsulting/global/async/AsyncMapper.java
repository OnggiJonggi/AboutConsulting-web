package com.axaboutconsulting.global.async;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AsyncMapper {

	public void insertStatus(AsyncVO.Insert insert);

	public void updateStatus(AsyncVO.Update update);
	
	public int selectIsRecordProcessing(int studentNo);


}
