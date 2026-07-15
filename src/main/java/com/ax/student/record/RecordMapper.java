package com.ax.student.record;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper {

	public int insertAnalysisGroup(RecordVO.GroupStatus groupStatus);
	
	public int updateRecordStatus(RecordVO.GroupStatus groupStatus);
	
	public String selectRecordStatus(int groupNo);
	
	public int selectRecordStatusByStudentNo(RecordVO.GroupStatus groupStatus);
	
	public RecordVO.Detail selectRecord(int studentNo);

	public int insertRecord(RecordVO.Insert insert);
}
