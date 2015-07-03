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


public class LandCoverProcedure extends Procedure {
	
	private static final Logger LOGGER = Logger.getLogger(LandCoverProcedure.class);
	
	public SimpleFeatureCollection landCoverLayer;
	public GridCoverage2D dem;
	public String valueFieldForRasterizeLandcover;
	public BoundingBox boundingBox;
	public List<String> landcoverReclassify;
	
	public ProgressListener progressListener;
	
	public LandCoverProcedure(SimpleFeatureCollection landCoverLayer, String valueFieldForRasterizeLandcover,  List<String> landcoverReclassify,
			GridCoverage2D dem,
			BoundingBox boundingBox,  ProgressListener progressListener) {
		this.landCoverLayer = landCoverLayer;
		this.valueFieldForRasterizeLandcover = valueFieldForRasterizeLandcover;
		this.dem = dem;
		this.boundingBox = boundingBox;
		this.landcoverReclassify = landcoverReclassify;
	}

	public String executeProcedure() {
		
		String layerName = landCoverLayer.getSchema().getTypeName();
		
		String reclassifyRasterFile = null;
		String translateOutputRaster = null;
		String divideOutputRaster = null;
		
		try {
			//1) vector_to_raster
			VectorToRasterProcess vectorToRaster = new VectorToRasterProcess();
			GridCoverage2D rasterLandCover = vectorToRaster.execute(landCoverLayer, dem.getRenderedImage().getWidth(), dem
					.getRenderedImage().getHeight(), layerName, valueFieldForRasterizeLandcover,
					boundingBox, progressListener);
			
			//2)reclassify
			if (rasterLandCover != null) {
				LOGGER.debug("inizio reclassify raster " + rasterLandCover.getName());
				RangeLookupProcess rangeLookupProcess = new RangeLookupProcess();
				
				readReclassify(landcoverReclassify);
				
				GridCoverage2D rasterReclassify = rangeLookupProcess.execute(rasterLandCover, 0, rangeList,
						outputRangeList, null, progressListener);
				reclassifyRasterFile = ProjectProperties.loadByName(Constants.TMP_PATH) + rasterLandCover.getName() + "_"+ Constants.RECLASSIFY+"_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				ProcessUtils.writeRasterSuFile(reclassifyRasterFile, rasterReclassify);
				LOGGER.debug("fine reclassify raster, scritto in " + reclassifyRasterFile);
			}
			
			//3) float
			if (reclassifyRasterFile != null) {
				LOGGER.debug("inizio translate raster " + reclassifyRasterFile);
				translateOutputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + rasterLandCover.getName() + "_"+ Constants.FLOAT+"_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				GdalOperation.translateToFloat(reclassifyRasterFile, translateOutputRaster);
				LOGGER.debug("fine translate raster " + translateOutputRaster);
			}
			
			//4)divide geo
			if (translateOutputRaster != null) {
				LOGGER.debug("inizio divide raster " + translateOutputRaster);
				divideOutputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + rasterLandCover.getName() + "_"+ Constants.DIVIDE+"_"
						+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
				if (ProjectProperties.loadByName(Constants.IS_WIN) != null && ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
					GdalOperation.winDivide(translateOutputRaster, divideOutputRaster,ProjectProperties.loadByName(Constants.LANDCOVER_DIVISORE));
				} else {
					GdalOperation.divide(translateOutputRaster, divideOutputRaster,ProjectProperties.loadByName(Constants.LANDCOVER_DIVISORE));
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
