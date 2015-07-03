package it.sinergis.utils;

import java.io.Serializable;

public class Layer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String schema;
	String name ;
	String geom ;
	
	public Layer(String schema, String name) {
		this.schema = schema;
		this.name = name;
	}
	
	public Layer(String schema, String name, String geo) {
		this.schema = schema;
		this.name = name;
		this.geom = geo;
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
	
}
