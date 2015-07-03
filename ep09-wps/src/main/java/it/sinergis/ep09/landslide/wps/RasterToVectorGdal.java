package it.sinergis.ep09.landslide.wps;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.GeoserverUtils;
import it.sinergis.ep09.utils.ProcessUtils;
import it.sinergis.ep09.utils.ProjectProperties;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.geoserver.catalog.Catalog;
import org.geoserver.wps.WPSException;
import org.geoserver.wps.jts.SpringBeanProcessFactory;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.opengis.util.ProgressListener;

@DescribeProcess(title = "rasterToVector", description = "Service to convert raster layer into a vector layer using gdal_polygonize")
public class RasterToVectorGdal extends SpringBeanProcessFactory {
	
	private Catalog catalog;
	private Properties properties = null;
	
	private static String GDAL_POLIGONIZE = "gdal_polygonize.py";
	
	private static final Logger LOGGER = Logger.getLogger(RasterToVectorGdal.class);
	
	public RasterToVectorGdal(Catalog catalog) {
		super(Constants.EENV_TITLE, Constants.EENV_NAMESPACE, RasterToVectorGdal.class);
		this.catalog = catalog;
	}
	
	@DescribeResult(name = "result", description = "output result")
	public String execute(
			@DescribeParameter(name = "inputRaster", description = "raster layer") GridCoverage2D inputRaster,
			ProgressListener progressListener) {
		
		String command = Constants.PYTHON;
		String result = "";
		try {
			String vectorName = inputRaster.getName().toString() + "_SHP";
			//cerco o se non lo trovo riscrivo il raster su fs
			String inputRasterMapFile = GeoserverUtils.getObjPath(catalog, inputRaster);
			System.out.println("INPUT: " + inputRasterMapFile);
			String srs = inputRaster.getCoordinateReferenceSystem().getIdentifiers().iterator().next().toString();
			result = GdalOperation.gdalPolygonize(inputRasterMapFile, vectorName);
			
			GeoServerRESTPublisher publisher = ProcessUtils.getRestPublisher();
			
			File fileZip = new File(result);
			
			publisher.publishShp(ProjectProperties.loadByName(Constants.GEOSERVER_WS_STORE_MAP), vectorName,
					vectorName, fileZip, srs, "");
			
		}
		catch (Exception e) {
			LOGGER.error("errore nella conversione del raster in vettoriale, comando eseguito " + command, e);
			e.printStackTrace();
			throw new WPSException("errore during raster conversion", e);
		}
		return result;
	}
	
}
