package com.ax.global.file.component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.global.file.FileDataVO;
import com.ax.global.file.FileInfoVO;
import com.ax.global.file.FileMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileComponent {
	private final FileMapper fileMapper;
	
	@Value("${file.upload.address}")
	private String uploadAddress;

	
	/**
	 * 파일 저장, FILE_INFO테이블 저장
	 * 
	 * @param file
	 * @param groupNo
	 * @param memberNo
	 * @return FileInfoVO.Registor
	 */
	@Transactional
	public void saveFile(FileDataVO file, int groupNo, TargetEnum target, int memberNo) throws Exception{
		
		// 지금 몇 시에요?
		LocalDateTime now = LocalDateTime.now();
		
		// 저장 경로 만들기
		String path = uploadAddress + target.getSaveFolder() + now.format(DateTimeFormatter.ofPattern("/yyyy/MM/dd"));
		
		// 본 이름 유효성 확인 및 이스케이프
		String originalName = isValid(file);
		
		// 이름 그냥 바꿔부러
		String changedName = UUID.randomUUID().toString();
		
		// 저장할 경로 생성
		Path targetDir = Paths.get(path);
		Files.createDirectories(targetDir);
		
		// 파일 저장
	    Path targetFile = targetDir.resolve(changedName);
	    Files.write(targetFile, file.getBytes());
		
		// FileInfoRegistor 객체 생성
		FileInfoVO.Registor registor = FileInfoVO.Registor.builder()
				.originalName(originalName)
				.changedName(changedName)
				.mime(file.getMime())
				.fileSize(file.getSize())
				.savePath(path)
				.build();
		
		// 파일 메타데이터 저장
		int result1 = fileMapper.insertInfo(registor);
		if(result1==0) throw new Exception();
		
		
		// FileInfoVO.InsertMapping 객체 생성
		FileInfoVO.InsertMapping insertMapping = FileInfoVO.InsertMapping.builder()
				.target(target)
				.groupNo(groupNo)
				.fileNo(registor.getFileNo()).build();
		
		// 파일-맵핑 테이블 저장
		int result2 = fileMapper.insertMapping(insertMapping);
		if(result2==0) throw new Exception();
		
		// FileInfoInsertHistory 객체 생성
		FileInfoVO.InsertHistory insertHistory = FileInfoVO.InsertHistory.builder()
				.fileNo(registor.getFileNo())
				.originalName(originalName)
				.changedName(changedName)
				.savePath(path)
				.actionAt(now)
				.action(FileStatusEnum.ACTIVE)
				.actionBy(memberNo).build();
		
		// 기록 생성
		int result3= fileMapper.insertHistory(insertHistory);
		if(result3==0) throw new Exception();
	}
	
	
	/**
	 * 파일 유효성 검사
	 * 파일 이름, docType검사
	 * 
	 * @param file
	 * @return 불값
	 */
	public String isValid(FileDataVO file) throws Exception{
		// 파일 이름 내놔
		String originalName = file.getOriginalName();

		// 파일 이름 이상하면 가세요라
		if (originalName == null 
				|| originalName.isBlank()
				|| !Pattern.matches(FileRegexp.ORIGINAL_NAME_NO_REGEXP, originalName)
				) throw new CustomException(ErrorCodeEnum.DOC_NAME_FORBIDDEN);
		
		// 파일명 이스케이프
		return FileNameEscapeEnum.escapeAll(originalName);
	}
	
	/**
	 * 폴더 디렉토리와 파일 이름으로 진짜 경로 만들기
	 * 
	 * @param savePath
	 * @param changedName
	 * @return 진짜 파일 경로
	 */
	public String createPath(String savePath, String changedName) {
		return savePath + File.separator + changedName;
	}
	
}
