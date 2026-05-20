package com.axaboutconsulting.student;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TargetInfoMapper {

	public void insertTargetUniv(TargetInfoVO.Update targetUpdate);

}
