package legacy;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OcrPageVO {
	enum Type {
		TEXT, TABLE
	}

	private final Type type;
	private final int topY;
	private final String text;
	private final List<List<String>> tableMatrix;
	
    static OcrPageVO ofText(int topY, String text) {
        return new OcrPageVO(Type.TEXT, topY, text, null);
    }

    static OcrPageVO ofTable(int topY, List<List<String>> matrix) {
        return new OcrPageVO(Type.TABLE, topY, null, matrix);
    }
}
