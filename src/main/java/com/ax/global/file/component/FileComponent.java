package com.ax.global.file.component;

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
import org.springframework.web.multipart.MultipartFile;

import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
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
	 * 파일 저장
	 * @param file
	 * @param groupNo 
	 * @param memberNo 
	 * @return FileInfoVO.Registor
	 */
	@Transactional
	public void saveFile(MultipartFile file, int groupNo, TargetEnum target, int memberNo) throws Exception{
		// 지금 몇 시에요?
		LocalDateTime now = LocalDateTime.now();
		
		// 저장 경로 만들기
		String path = uploadAddress + target.name() + now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		
		// 본 이름 유효성 확인 및 이스케이프
		String originalName = isValid(file);
		
		// 이름 그냥 바꿔부러
		String changedName = UUID.randomUUID().toString();
		
		// 저장할 경로 생성
		Path targetDir = Paths.get(path);
		Files.createDirectories(targetDir);
		
		try {
			// 파일 저장
			Path targetFile = targetDir.resolve(changedName);
			file.transferTo(targetFile.toFile());
			
			// FileInfoRegistor 객체 생성
			FileInfoVO.Registor registor = FileInfoVO.Registor.builder()
					.originalName(originalName)
					.changedName(changedName)
					.mime(file.getContentType())
					.fileSize(file.getSize())
					.savePath(path)
					.savedAt(now)
					.build();
			
			// 파일 메타데이터 저장
			fileMapper.insertInfo(registor);
			
			// FileInfoVO.InsertMapping 객체 생성
			FileInfoVO.InsertMapping insertMapping = FileInfoVO.InsertMapping.builder()
					.target(target)
					.groupNo(groupNo)
					.fileNo(registor.getFileNo()).build();
			
			// 파일-맵핑 테이블 저장
			fileMapper.insertMapping(insertMapping);
			
			// FileInfoInsertHistory 객체 생성
			FileInfoVO.InsertHistory insertHistory = FileInfoVO.InsertHistory.builder()
					.fileNo(registor.getFileNo())
					.originalName(originalName)
					.savePath(path)
					.actionAt(now)
					.action(FileStatusEnum.ACTIVE)
					.actionBy(memberNo).build();
			
			// 기록 생성
			fileMapper.insertHistory(insertHistory);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("파일이 저장될 경로"+path);
		}
	}
	
	
	/**
	 * 파일 유효성 검사
	 * 파일 이름, docType검사
	 * 
	 * @param file
	 * @return 불값
	 */
	public String isValid(MultipartFile file) throws Exception{
		// 파일 이름 내놔
		String originalName = file.getOriginalFilename();

		// 파일 이름 이상하면 가세요라
		if (originalName == null 
				|| originalName.isBlank()
				|| !Pattern.matches(FileRegexp.ORIGINAL_NAME_NO_REGEXP, originalName)
				) throw new CustomException(ErrorCodeEnum.DOC_NAME_FORBIDDEN);
		
		// 파일명 이스케이프
		return FileNameEscapeEnum.escapeAll(originalName);
	}
	
}
