package com.ax.global.common;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

@Component
public class PdfComponent {

	private static final int PAGE_LIMIT = 10;

	/**
	 * clova ocr은 pdf파일을 최대 10장까지만 분석하기에 동강동강 해야지요
	 */
	public List<byte[]> splitPdf(byte[] pdfBytes) throws Exception {
		List<byte[]> chunkBytesList = new ArrayList<>();

		try (PDDocument document = Loader.loadPDF(pdfBytes)) {
			
			// 10페이지 이하이면 바로 반환
			int totalPages = document.getNumberOfPages();
	        if (totalPages <= PAGE_LIMIT) {
	            return List.of(pdfBytes);
	        }
			
			Splitter splitter = new Splitter();
			splitter.setSplitAtPage(PAGE_LIMIT);
			List<PDDocument> chunks = splitter.split(document);

			for (PDDocument chunk : chunks) {
				try (chunk; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					chunk.save(baos);
					chunkBytesList.add(baos.toByteArray());
				}
			}
		}
		return chunkBytesList;
	}
}
