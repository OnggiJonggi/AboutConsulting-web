package com.ax.student.record;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ax.student.AnalysisComponent;
import com.ax.student.AnalysisTypeEnum;
import com.ax.student.AnalysisVO;
import com.ax.student.StudentMapper;
import com.ax.student.StudentVO;
import com.ax.student.TargetInfoVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecordAsyncComponent {
	private final StudentMapper studentMapper;
	private final RecordMapper recordMapper;
	private final AnalysisComponent analysisComponent;
	private final ObjectMapper objectMapper;
	
	/**
	 * 생기부 api요청 및 DB저장
	 * 
	 * 비동기 방식은 File객체를 못 받아서 byte[]로 다뤄야 해요
	 * 
	 * 1. 학생 정보 조회
	 * 2. 학생 정보 프롬프트 작성
	 * 3. LLM에 분석 요청
	 * 4. LLM결과 문자열 역직렬화
	 * 5. toString()을 사용해 DB저장용 객체로 직렬화
	 * 6. DB저장 및 상태값 수정
	 */
	@Async
	public void analysisRecord(int studentNo, int groupNo, byte[] filebytes){
		try {
			// 학생 정보 조회
			StudentVO.Detail student = studentMapper.selectStudentForRecordApi(studentNo);
			if(student == null) throw new Exception();
			
			// 학생 프롬프트
			StringBuilder sb = new StringBuilder();
			sb.append("[USER REQUEST]\n");
			sb.append("[학생 기본 정보 및 목표 대학/학과]\n");
			sb.append("- 이름: ").append(student.getName()).append("\n");
			sb.append("- 학년: ").append(student.getGrade()).append("학년 ").append(student.getSemester()).append("학기\n");
			sb.append("- 계열: ").append(student.getTrack()).append("\n");
			sb.append("- 출신고교: ").append(student.getSchoolName()).append("\n");
			sb.append("- 목표:\n");
			for (TargetInfoVO.Pair target : student.getTarget()) {
				sb.append("  - ").append(target.getUniv()).append(" ").append(target.getMajor()).append(" (희망순위 ")
						.append(target.getRanking()).append(")\n");
			}
			String studentPrompt = sb.toString();
			
			log.info("학생 프롬프트 : {}", studentPrompt);
			
			// LLM 요청
			AnalysisVO rawGsw  = analysisComponent.run(studentPrompt, AnalysisTypeEnum.GRADE_STRENGTH_WEAKNESS, filebytes, null);
			log.info("GSW : {}", rawGsw.toString());
			AnalysisVO rawGida = analysisComponent.run(studentPrompt, AnalysisTypeEnum.GRADE_IN_DEPTH_ANALYSIS, null, rawGsw.getInteractionId());
			AnalysisVO rawLrsw = analysisComponent.run(studentPrompt, AnalysisTypeEnum.LIFE_RECORD_STRENGTH_WEAKNESS, null, rawGida.getInteractionId());
			AnalysisVO rawLrdo = analysisComponent.run(studentPrompt, AnalysisTypeEnum.LIFE_RECORD_DIAGNOSIS_OVERVIEW, null, rawLrsw.getInteractionId());
			AnalysisVO rawLrra = analysisComponent.run(studentPrompt, AnalysisTypeEnum.LIFE_RECORD_ROADMAP_ACADEMIC, null, rawLrdo.getInteractionId());
			AnalysisVO rawPr   = analysisComponent.run(studentPrompt, AnalysisTypeEnum.PROJECT_RECOMMENDATION, null, rawLrra.getInteractionId());
			AnalysisVO rawBr   = analysisComponent.run(studentPrompt, AnalysisTypeEnum.BOOK_RECOMMENDATION, null, rawPr.getInteractionId());
			AnalysisVO rawPsm  = analysisComponent.run(studentPrompt, AnalysisTypeEnum.PARENT_STUDENT_MESSAGE, null, rawBr.getInteractionId());

			// 문자열 -> 객체 역직렬화
			ApiRecordVO.Gsw  gsw  = objectMapper.readValue(rawGsw.getMessage(),  ApiRecordVO.Gsw.class);
			ApiRecordVO.Gida gida = objectMapper.readValue(rawGida.getMessage(), ApiRecordVO.Gida.class);
			ApiRecordVO.Lrsw lrsw = objectMapper.readValue(rawLrsw.getMessage(), ApiRecordVO.Lrsw.class);
			ApiRecordVO.Lrdo lrdo = objectMapper.readValue(rawLrdo.getMessage(), ApiRecordVO.Lrdo.class);
			ApiRecordVO.Lrra lrra = objectMapper.readValue(rawLrra.getMessage(), ApiRecordVO.Lrra.class);
			ApiRecordVO.Pr   pr   = objectMapper.readValue(rawPr.getMessage(),   ApiRecordVO.Pr.class);
			ApiRecordVO.Br   br   = objectMapper.readValue(rawBr.getMessage(),   ApiRecordVO.Br.class);
			ApiRecordVO.Psm  psm  = objectMapper.readValue(rawPsm.getMessage(),  ApiRecordVO.Psm.class);
			
	        // 객체 -> 저장용 문자열 직렬화
	        String GSW  = gsw.toStorageString();
	        String GIDA = gida.toStorageString();
	        String LRSW = lrsw.toStorageString();
	        String LRDO = lrdo.toStorageString();
	        String LRRA = lrra.toStorageString();
	        String PR   = pr.toStorageString();
	        String BR   = br.toStorageString();
	        String PSM  = psm.toStorageString();

			// 생기부 DB저장
	        RecordVO.Insert recordInsert = RecordVO.Insert.builder()
		    		.groupNo(groupNo)
		    		.gsw(GSW).gida(GIDA).lrsw(LRSW).lrdo(LRDO).lrra(LRRA).pr(PR).br(BR).psm(PSM).build();
	        log.info(recordInsert.toString());
		    int result2 = recordMapper.insertRecord(recordInsert);
		    if(result2==0) throw new Exception();
		    
		    // 비동기 요청 작업 상태값 수정
		    int result3 = recordMapper.updateRecordStatus(RecordVO.GroupStatus.builder()
		    		.groupNo(groupNo)
		    		.status(RecordStatusEnum.ACTIVE.name()).build());
		    if(result3==0) throw new Exception();
		    
		} catch (Exception e) {
			// 비동기 요청 작업 상태값 실패로 수정
			e.printStackTrace();
			log.info("비동기 요청 실 패! 이놈 때문임-> studentNo: {}, groupNo: {}", studentNo, groupNo);
			
			int result = recordMapper.updateRecordStatus(RecordVO.GroupStatus.builder()
					.groupNo(groupNo)
					.status(RecordStatusEnum.FAILED.name()).build());
			
			if(result==0) log.warn("비동기 요청 작업 상태값 수정 대 실 패! 이놈 때문임-> studentNo: {}, groupNo: {}", studentNo, groupNo);
		}
	}
}
