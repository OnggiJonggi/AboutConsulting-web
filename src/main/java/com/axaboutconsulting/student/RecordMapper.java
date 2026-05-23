package com.axaboutconsulting.student;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper {

	public RecordVO.Detail selectReord(int studentNo);

	public void insertRecord(RecordVO.Insert insert);

}
