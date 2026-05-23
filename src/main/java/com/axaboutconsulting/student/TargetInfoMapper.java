package com.axaboutconsulting.student;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TargetInfoMapper {

	public void insertTarget(@Param("studentNo")int studentNo
			,@Param("list")List<TargetInfoVO.Register> list);

}
