package it.sinergis.ep10.utils;

import java.io.Serializable;

public class Layer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String schema;
	String name;
	String geom;
	String epsg;
	
	public Layer(String schema, String name) {
		this.schema = schema;
		this.name = name;
	}
	
	public Layer(String schema, String name, String geo) {
		this.schema = schema;
		this.name = name;
		this.geom = geo;
	}
	
	public Layer(String schema, String name, String geo, String epsg) {
		this.schema = schema;
		this.name = name;
		this.geom = geo;
		this.epsg = epsg;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGeom() {
		return geom;
	}
	
	public void setGeom(String geom) {
		this.geom = geom;
	}
	
	public String getEpsg() {
		return epsg;
	}
	
	public void setEpsg(String epsg) {
		this.epsg = epsg;
	}
	
}
