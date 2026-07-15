package legacy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ax.global.common.PdfComponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

/**
 * (미사용) 네이버 클로바 OCR
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OcrComponent {
	private final PdfComponent pdfComponent;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	@Value("${ncloud-ocr.invoke-url}")
	private String invokeUrl;
	
	@Value("${ncloud-ocr.key}")
	private String keyStr;
	
	private static final String MESSAGE_TEMPLATE = """
		    {"version":"V2","requestId":"%s","timestamp":1720600000000,"lang":"ko","images":[{"format":"pdf","name":"abcdefg","enableTableDetection":true}]}
		    """;
	
	private String bodyMessage() {
	    return MESSAGE_TEMPLATE.formatted(UUID.randomUUID().toString());
	}

	/**
	 * 네이버 클로바 ocr api
	 * https://console.ncloud.com/ocr/subscription
	 * 
	 * 생기부 전용. 생기부를 통짜로 llm에 넣으면 글자 인식 잘 못하나봄.
	 * api 1회 호출에 pdf파일은 최대 10장 까지만 가능해서
	 * 10장 단위로 잘라서 따로 요청
	 * llm에 넣기 좋게(토큰 아끼기 좋게) 응답 json에서 단어만 추출해
	 * <기본 정보 표>
	 * 1. 인적사항
	 * <인적사항 표>
	 * 2. 출결사항
	 * ...
	 * 꼴로 된 생기부를 마크다운 형식 문자열로 변경
	 * 
	 * TODO: ocr이 테이블을 인식 못 하는데 어쩌죠
	 */
	public String pdfToText(byte[] filebytes) throws Exception {
		// 반환할 문자열
		StringBuilder fullText = new StringBuilder();
		
		// 파일 10장 이상이면 쪼개기
		List<byte[]> files = pdfComponent.splitPdf(filebytes);
		
		for(byte[] item : files) {
			
			// 헤더
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("X-OCR-SECRET", keyStr);
			
			// 바디
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.set("message", bodyMessage());
		    ByteArrayResource fileResource = new ByteArrayResource(item) {
		        @Override
		        public String getFilename() {
		            return "chunk.pdf";
		        }
		    };
		    body.set("file", fileResource);
			
			// 요청
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(
					invokeUrl,
					requestEntity,
					String.class
					);
			
			log.info("CLOVA 원본 응답: \n{}", response.getBody());
			
			// 응답 상태값
			HttpStatusCode statusCode = response.getStatusCode();
			if (!statusCode.is2xxSuccessful())
				throw new RuntimeException("CLOVA OCR 호출 실패: HTTP " + statusCode.value());
			
			// 응답 데이터 맵핑
			OcrApiVO.Wrapper1 result = objectMapper.readValue(response.getBody(), OcrApiVO.Wrapper1.class);
			for (OcrApiVO.Wrapper2 image : result.getImages()) {
				if (!"SUCCESS".equals(image.getInferResult()))
					throw new RuntimeException(
							"OCR인식에 실패했어요!\n상태값 : " + image.getInferResult() + "\nUUID :" + image.getUid());

				fullText.append(renderPage(image));
				fullText.append("\n");
			}
		}
		return fullText.toString().trim();
	}

	// 한 페이지(이미지)의 텍스트+표를 y좌표 순으로 병합해 렌더링
	private String renderPage(OcrApiVO.Wrapper2 image) {
		List<Integer> knownYs = new ArrayList<>();
		List<OcrPageVO> elements = new ArrayList<>();

		if (image.getFields() != null) {
			elements.addAll(buildTextElements(image.getFields(), knownYs));
		}
		if (image.getTables() != null) {
			for (OcrApiVO.OcrTable table : image.getTables()) {
				int topY = getTableTopY(table, knownYs);
				List<List<String>> matrix = cellsToMatrix(table);
				elements.add(OcrPageVO.ofTable(topY, matrix));
			}
		}

		elements.sort(Comparator.comparingInt(OcrPageVO::getTopY));

		StringBuilder sb = new StringBuilder();
		for (OcrPageVO el : elements) {
			if (el.getType() == OcrPageVO.Type.TEXT) {
				sb.append(el.getText());
			} else {
				sb.append(renderMarkdownTable(el.getTableMatrix()));
			}
		}
		return sb.toString();
	}

	// fields를 줄바꿈 기준으로 문단 텍스트 요소로 변환
	private List<OcrPageVO> buildTextElements(List<OcrApiVO.Block> fields, List<Integer> knownYs) {
		List<OcrPageVO> elements = new ArrayList<>();
		StringBuilder line = new StringBuilder();
		int lineTopY = 0;

		for (OcrApiVO.Block field : fields) {
			if (line.length() == 0) {
				lineTopY = snapY(getBlockY(field), knownYs);
			}
			line.append(field.getInferText());
			line.append(field.isLineBreak() ? "\n" : " ");

			if (field.isLineBreak()) {
				elements.add(OcrPageVO.ofText(lineTopY, line.toString()));
				line.setLength(0);
			}
		}
		if (line.length() > 0) {
			elements.add(OcrPageVO.ofText(lineTopY, line.toString()));
		}
		return elements;
	}

	private int getBlockY(OcrApiVO.Block block) {
		if (block == null || block.getBoundingPoly() == null || block.getBoundingPoly().getVertices().isEmpty()) {
			return 0;
		}
		return toIntY(block.getBoundingPoly().getVertices().get(0).getY());
	}
	
	private int toIntY(double y) {
	    return (int) Math.round(y);
	}

	private int getTableTopY(OcrApiVO.OcrTable table, List<Integer> knownYs) {
		int minY = Integer.MAX_VALUE;
		for (OcrApiVO.OcrCell cell : table.getCells()) {
			if (cell.getBoundingPoly() == null)
				continue;
			for (OcrApiVO.Vertex v : cell.getBoundingPoly().getVertices()) {
				minY = Math.min(minY, toIntY(v.getY()));
			}
		}
		if (minY == Integer.MAX_VALUE)
			minY = 0;
		return snapY(minY, knownYs);
	}
	
	// 셀 리스트를 rowIndex/columnIndex 기준으로 2차원 문자열 행렬로 변환
	private List<List<String>> cellsToMatrix(OcrApiVO.OcrTable table) {
		Map<Integer, Map<Integer, String>> rows = new TreeMap<>();
		int maxCol = 0;

		for (OcrApiVO.OcrCell cell : table.getCells()) {
			String text = extractCellText(cell);
			rows.computeIfAbsent(cell.getRowIndex(), k -> new TreeMap<>()).put(cell.getColumnIndex(), text);
			maxCol = Math.max(maxCol, cell.getColumnIndex());
		}

		List<List<String>> matrix = new ArrayList<>();
		for (Map<Integer, String> rowMap : rows.values()) {
			List<String> row = new ArrayList<>();
			for (int c = 0; c <= maxCol; c++) {
				row.add(rowMap.getOrDefault(c, ""));
			}
			matrix.add(row);
		}
		return matrix;
	}

	private String extractCellText(OcrApiVO.OcrCell cell) {
		StringBuilder sb = new StringBuilder();
		if (cell.getCellTextLines() == null)
			return "";
		for (OcrApiVO.CellTextLine line : cell.getCellTextLines()) {
			if (line.getCellWords() == null)
				continue;
			for (OcrApiVO.CellWord word : line.getCellWords()) {
				String t = word.getInferText();
				if (t != null && !t.isBlank())
					sb.append(t.trim());
			}
		}
		return sb.toString();
	}

	// 행렬을 마크다운 표 문자열로 변환 (첫 행을 헤더로 사용)
	private String renderMarkdownTable(List<List<String>> matrix) {
		if (matrix.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		List<String> header = matrix.get(0);

		sb.append("| ").append(String.join(" | ", header)).append(" |\n");
		sb.append("|").append(" --- |".repeat(header.size())).append("\n");

		for (int i = 1; i < matrix.size(); i++) {
			List<String> row = matrix.get(i);
			List<String> padded = new ArrayList<>(row);
			while (padded.size() < header.size())
				padded.add("");
			sb.append("| ").append(String.join(" | ", padded)).append(" |\n");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	private static final int Y_THRESHOLD = 10; // 10픽셀 이내 오차 허용

	// 이미 등록된 y값들 중 10픽셀 이내로 가까운 값이 있으면 그 값으로 통일
	private int snapY(int y, List<Integer> knownYs) {
		for (int known : knownYs) {
			if (Math.abs(y - known) <= Y_THRESHOLD) {
				return known;
			}
		}
		knownYs.add(y);
		return y;
	}
}
