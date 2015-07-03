package it.sinergis.ep09.landslide.procedure;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import java.util.List;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.raster.RangeLookupProcess;
import org.geotools.process.vector.VectorToRasterProcess;
import org.opengis.geometry.BoundingBox;
import org.opengis.util.ProgressListener;


public class GeologyProcedure extends Procedure {
	
	private static final Logger LOGGER = Logger.getLogger(GeologyProcedure.class);
	
	public SimpleFeatureCollection geologyLayer;
	public GridCoverage2D dem;
	public String valueFieldForRasterize;
	public BoundingBox boundingBox;
	public List<String> geologyReclassify;
	
	public ProgressListener progressListener;
	
	public GeologyProcedure(SimpleFeatureCollection geologyLayer, String valueFieldForRasterize, List<String> geologyReclassify, GridCoverage2D dem, BoundingBox boundingBox,
			ProgressListener progressListener) {
		this.geologyLayer = geologyLayer;
		this.dem = dem;
		this.valueFieldForRasterize = valueFieldForRasterize;
		this.boundingBox = boundingBox;
		this.geologyReclassify = geologyReclassify;
	}
	
	public String executeProcedure() {
		
		String layerName = geologyLayer.getSchema().getTypeName();
		
		String reclassifyRasterFile = null;
		String translateOutputRaster = null;
		String divideOutputRaster = null;
		
		//check fields layer
		ProcessUtils.checkFieldLayer(geologyLayer, valueFieldForRasterize);
		
		try {
			//1) vector_to_raster
			VectorToRasterProcess vectorToRaster = new VectorToRasterProcess();
			GridCoverage2D rasterGeology = vectorToRaster.execute(geologyLayer, dem.getRenderedImage().getWidth(), dem
					.getRenderedImage().getHeight(), layerName, valueFieldForRasterize,
					boundingBox, progressListener);
			
			//2)reclassify
			if (rasterGeology != null) {
				LOGGER.debug("inizio reclassify raster " + rasterGeology.getName());
				RangeLookupProcess rangeLookupProcess = new RangeLookupProcess();
				
				readReclassify(geologyReclassify);
				
				GridCoverage2D rasterReclassify = rangeLookupProcess.execute(rasterGeology, 0, rangeList,
						outputRangeList, null, progressListener);
				reclassifyRasterFile = ProjectProperties.loadByName(Constants.TMP_PATH) + rasterGeology.getName() + "_" + Constants.RECLASSIFY+"_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				ProcessUtils.writeRasterSuFile(reclassifyRasterFile, rasterReclassify);
				LOGGER.debug("fine reclassify raster, scritto in " + reclassifyRasterFile);
			}
			
			//3) float geo
			if (reclassifyRasterFile != null) {
				LOGGER.debug("inizio translate raster " + reclassifyRasterFile);
				translateOutputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + rasterGeology.getName() + "_" + Constants.FLOAT+"_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				GdalOperation.translateToFloat(reclassifyRasterFile, translateOutputRaster);
				LOGGER.debug("fine translate raster " + translateOutputRaster);
			}
			
			//4)divide geo
			if (translateOutputRaster != null) {
				LOGGER.debug("inizio divide raster " + translateOutputRaster);
				divideOutputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + rasterGeology.getName() + "_" + Constants.DIVIDE+"_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				if (ProjectProperties.loadByName(Constants.IS_WIN) != null && ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
					GdalOperation.winDivide(translateOutputRaster, divideOutputRaster,
							ProjectProperties.loadByName(Constants.GEOLOGY_DIVISORE));
				} else {
					GdalOperation.divide(translateOutputRaster, divideOutputRaster,
							ProjectProperties.loadByName(Constants.GEOLOGY_DIVISORE));
				}
				
				LOGGER.debug("fine divide raster " + divideOutputRaster);
			}
		}
		catch (Exception e) {
			throw new WPSException(e.getMessage());
		}
		finally {
			if (reclassifyRasterFile != null) {
				ProcessUtils.deleteTmpFile(reclassifyRasterFile);
			}
			if (translateOutputRaster != null) {
				ProcessUtils.deleteTmpFile(translateOutputRaster);
			}
		}
		
		return divideOutputRaster;
		
	}
	
}
