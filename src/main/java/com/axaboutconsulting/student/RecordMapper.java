package com.axaboutconsulting.student;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper {

	public void insertAnalysisGroup(RecordVO.GroupStatus groupStatus);
	
	public void updateRecordStatus(RecordVO.GroupStatus groupStatus);
	
	public int selectRecordStatus(RecordVO.GroupStatus groupStatus);
	
	public RecordVO.Detail selectReord(int studentNo);

	public void insertRecord(RecordVO.Insert insert);



}
