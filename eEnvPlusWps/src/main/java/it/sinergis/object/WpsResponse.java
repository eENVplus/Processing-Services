package it.sinergis.object;

public class WpsResponse {
	
	public static String ERRORE_SQL = "ERRORE_SQL";
	public static String ERRORE_GENERICO = "ERRORE_GENERICO";
	public static String ERRORE_GEOSERVER = "ERRORE_GEOSERVER";
	public static String ERRORE_INPUT = "ERRORE_INPUT";
	public static String ERRORE_LAYER_NON_ESISTENTE = "ERRORE_LAYER_NON_ESISTENTE";
	
	private String errorMessage;
	private String pathUnionShapeFile;
	private String errorShapeFile;
	
	public WpsResponse() {
		
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getPathUnionShapeFile() {
		return pathUnionShapeFile;
	}

	public void setPathUnionShapeFile(String pathUnionShapeFile) {
		this.pathUnionShapeFile = pathUnionShapeFile;
	}

	public String getErrorShapeFile() {
		return errorShapeFile;
	}

	public void setErrorShapeFile(String errorShapeFile) {
		this.errorShapeFile = errorShapeFile;
	}
	
	public String toXml() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml += "<wpsResponse>";
		if (pathUnionShapeFile!=null) {
			xml += "<pathUnionShapeFile>" + pathUnionShapeFile + "</pathUnionShapeFile>";
		}
		if (errorShapeFile!=null) {
			xml += "<errorShapeFile>" + errorShapeFile + "</errorShapeFile>";
		}
		if (errorMessage!=null) {
			xml += "<errorMessage>" + errorMessage + "</errorMessage>";
		}
		xml += "</wpsResponse>";
		return xml;
	}
	
}
