package legacy;

import java.util.List;

import lombok.Getter;

public class OcrApiVO {

	@Getter
	public static class Wrapper1 {
		private String requestId;
		private String timestamp;
		private List<Wrapper2> images;
	}

	@Getter
	public static class Wrapper2 {
		private String uid;
		private String inferResult;
		private List<Block> fields;
		private List<OcrTable> tables;
	}

	@Getter
	public static class Block {
		private String inferText;
		private boolean lineBreak;
		private BoundingPoly boundingPoly;
	}

	@Getter
	public static class OcrTable {
		private List<OcrCell> cells;
	}

	@Getter
	public static class OcrCell {
		private int rowIndex;
		private int columnIndex;
		private List<CellTextLine> cellTextLines;
		private BoundingPoly boundingPoly;
	}

	@Getter
	public static class CellTextLine {
		private List<CellWord> cellWords;
	}

	@Getter
	public static class CellWord {
		private String inferText;
	}

	@Getter
	public static class BoundingPoly {
		private List<Vertex> vertices;
	}

	@Getter
	public static class Vertex {
		private double x;
		private double y;
	}
}
