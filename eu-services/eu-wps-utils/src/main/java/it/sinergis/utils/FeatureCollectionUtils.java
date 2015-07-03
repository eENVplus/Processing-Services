package it.sinergis.utils;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class FeatureCollectionUtils {
	
	private static final Logger LOGGER = Logger.getLogger(FeatureCollectionUtils.class);
	
	/*
	 * metodo che restituisce l'epsg dalla feature collection
	 */
	public static String getFirstEpsgCode(SimpleFeatureCollection features, String defaultEpsg) {
		if (features.getBounds() != null
				&& features.getBounds().getCoordinateReferenceSystem() != null
				&& features.getBounds().getCoordinateReferenceSystem().getIdentifiers() != null
				|| (features.getSchema() != null && features.getSchema().getCoordinateReferenceSystem() != null && features
						.getSchema().getCoordinateReferenceSystem().getIdentifiers() != null)) {
			Set<ReferenceIdentifier> identifiers = features.getBounds().getCoordinateReferenceSystem().getIdentifiers();
			if (identifiers == null) {
				identifiers = features.getSchema().getCoordinateReferenceSystem().getIdentifiers();
			}
			Iterator<ReferenceIdentifier> iter = identifiers.iterator();
			if (iter.hasNext()) {
				ReferenceIdentifier identifier = iter.next();
				LOGGER.debug("restituito primo epsg code "+identifier.getCode());
				return identifier.getCode();
			}
		}
		return defaultEpsg;
	}
	
	/*
	 * metodo che restituisce il primo sistema di riferimento presente
	 */
	public static String getEpsgFromCoordinateReferenceSystem(CoordinateReferenceSystem coord) {
		Set<ReferenceIdentifier> identifiers = coord.getIdentifiers();
		if (identifiers != null) {
			Iterator<ReferenceIdentifier> iterator = identifiers.iterator();
			if (iterator.hasNext()) {
				ReferenceIdentifier identifier = iterator.next();
				if (identifier.getCodeSpace() != null && identifier.getCode() != null) {
					return identifier.getCodeSpace() + ":" + identifier.getCode();
				}
			}
		}
		return null;
	}
	
	/*
	 * metodo che controlla che nello schema ci sia l'attributo indicato
	 */
	public static boolean checkAttribute(SimpleFeatureType schema, String attributeName) {
		for (int i = 0; i < schema.getAttributeCount(); i++) {
			if (schema.getAttributeDescriptors().get(i).getLocalName().equalsIgnoreCase(attributeName)) {
				return true;
			}
		}
		return false;
	}
	
	//Restituisco la lista delle feature che sovrappongono la geometria del layer
	public static SimpleFeatureCollection checkOverlapsFeatureCollection(SimpleFeatureCollection list,
			Geometry restrictedArea) {
		
		DefaultFeatureCollection result = new DefaultFeatureCollection();
		
		SimpleFeatureIterator iter = list.features();
		
		while (iter.hasNext()) {
			SimpleFeature next = iter.next();
			Geometry geom = (Geometry) next.getDefaultGeometry();
			
			if (restrictedArea.overlaps(geom) || restrictedArea.contains(geom)) {
				result.add(next);
			}
		}
		
		iter.close();
		
		SimpleFeatureCollection collectionResult = result;
		
		return collectionResult;
		
	}
}
