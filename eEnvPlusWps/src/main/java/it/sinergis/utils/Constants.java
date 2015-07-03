package it.sinergis.utils;

public class Constants {
	public final static String GEOSERVER_WORKSPACE = "geoserver_workspace";
	public final static String GEOSERVER_DATASTORE = "geoserver_datastore";
	
	public final static String TOLERANCE = "tolerance";
	
	public final static String UPPER_LOWER_VALIDATION = "1";
	public final static String HOLES_VALIDATION = "2";
	public final static String LOWER_VALIDATION = "3";
	
	public final static String SHP_PATH_TOPOLOGICAL_VALIDATION = "shp_path_topologicalValidation";
	public final static String SHP_PATH_URBAN_RURAL_GROWTH = "shp_path_urbanRuralGrowth";
	
	public final static String SHP_PATH_TEMP_DIR = "shp_path_temp_dir";
	public final static String WRITE_SHP = "write_shape";
	public final static String URBAN_RURAL_GROWTH_SHAPE_CREATE = "urban_rural_growth_shape_create";
	public final static String URBAN_RURAL_GROWTH_STYLE_LAYER = "urban_rural_growth_style_layer";
	public final static String URBAN_RURAL_GROWTH_WIDTH_WMS_LAYER = "urban_rural_growth_width_wms_layer";
	public final static String URBAN_RURAL_GROWTH_HEIGHT_WMS_LAYER = "urban_rural_growth_height_wms_layer";
	public final static String URBAN_RURAL_GROWTH_FORMAT_WMS_LAYER = "urban_rural_growth_format_wms_layer";
	public final static String URBAN_RURAL_GROWTH_WFS_MAX_FEATURES = "urban_rural_growth_wfs_max_features";
	public final static String URBAN_RURAL_GROWTH_WFS_OUTPUT_FORMAT = "urban_rural_growth_wfs_output_format";
	public final static String URBAN_RURAL_GROWTH_ROUNDING = "urban_rural_growth_rounding";
	public final static String URBAN_RURAL_GROWTH_UNIT_MEASURE = "urban_rural_growth_unit_measure";
	public final static String URBAN_RURAL_GROWTH_PATH_XML = "urban_rural_growth_path_xml";
	
	//parametro che restituisce ST_DIMENSION (2 per i poligoni, 1 per le linee e 0 per i punti) 
	public final static  String GEOMETRY_TYPE_POLYGON = "2";
	
	public final static String UNZIP_PATH="unzip_path";
	public final static String DEFAULT_FORCED_EPSG = "defaultForcedEpsg";
	
	public final static String REGISTER_SINGLE="REGISTER_SINGLE";
	public final static String USERSID="USERSID";
	public final static String REGISTER_MULTI="REGISTER_MULTI";
	public final static String DO_NOTIFICATION="DO_NOTIFICATION";
	public final static String UNREGISTER="UNREGISTER";
	
	public final static String PH_EMAIL="@EMAIL";
	public final static String PH_USERID="@USERID";
	public final static String PH_USERSID="@USERSID";
	
	public final static String URL_52NWNS="URL_52NWNS";
	public final static String TEMP_DIR_52NWNS="TEMP_DIR_52NWNS";
	
	public final static String ENCODING_UTF8="UTF-8";
	
	public final static String SUCCESS_52NWNS="success";
	public final static String USERID_START_TAG_52NWNS="<UserID>";
	public final static String USERID_END_TAG_52NWNS="</UserID>";
	
	
	public final static String NR_TOPOLOGICAL_VALIDATION="155";
	public final static String NR_URBAN_RURAL_GROWTH="164";
	
	public final static String RESTURL  = "RESTURL";//"http://localhost:8080/geoserver";
	public final static String RESTUSER = "RESTUSER";//"admin";
	public final static String RESTPW   = "RESTPW";// "geoserver";
	
	/*
	 * EP09
	 */
	public final static String TMP_PATH = "tmp_path";
	public final static String RANGE_DEM = "range_dem";
	public final static String OUPUT_DEM_RANGE = "output_dem_range";
}
