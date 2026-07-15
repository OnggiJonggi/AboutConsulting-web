package com.ax.global.file.component;

/**
 * 파일 이름에 들어가면 쪼까 곤란한 특수문자 이스케이프
 */
public enum FileNameEscapeEnum {
    AMP("&",  "&amp;"),
    LT("<",  "&lt;"),
    GT(">",  "&gt;"),
    DQUOTE("\"", "&quot;"),
    SQUOTE("'",  "&#39;"),
    SEMICOL(";",  "&#59;");

    private FileNameEscapeEnum(String origin, String escaped) {
		this.origin = origin;
		this.escaped = escaped;
	}

	private final String origin;
    private final String escaped;

    /**
     * 문자열 이스케이프
     * @param value
     * @return 이스케이프 문자열
     */
    public static String escapeAll(String value) {
    	
        if (value == null) return null;
        
        String result = value;
        
        for (FileNameEscapeEnum c : values()) {
            result = result.replace(c.origin, c.escaped);
        }
        
        return result;
    }
}
