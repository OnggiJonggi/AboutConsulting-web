package com.ax.global.file;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;

@Component
public class FileValidateComponent {
	
	/**
	 * 파일 유효성 검사
	 * 파일 이름, docType검사
	 * 
	 * @param file
	 * @return 불값
	 */
	public String isValidNameAndDocType(MultipartFile file,
			String docType) throws Exception{
		
		// 파일 비었으면 가세요라
		if (file == null || file.isEmpty())
			throw new CustomException(ErrorCodeEnum.DOC_NAME_FORBIDDEN);

		// 파일 이름 내놔
		String originalName = file.getOriginalFilename();

		// 파일 이름 이상하면 가세요라
		if (originalName == null 
				|| originalName.isBlank()
				|| !Pattern.matches(FileRegexp.ORIGINAL_NAME_NO_REGEXP, originalName)
				) throw new CustomException(ErrorCodeEnum.DOC_NAME_FORBIDDEN);
		
		// 서류 타입 이상하면 가세요라
		if(docType == null
				|| !Pattern.matches(FileRegexp.DOC_TYPE_REGEXP, docType))
			throw new CustomException(ErrorCodeEnum.DOC_TYPE_FORBIDDEN);
		
		// 파일명 이스케이프
		return FileNameEscapeEnum.escapeAll(originalName);
	}
}
