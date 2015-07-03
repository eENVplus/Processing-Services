package it.sinergis.ep09.landslide.procedure;

import it.sinergis.ep09.utils.Constants;
import it.sinergis.ep09.utils.GdalOperation;
import it.sinergis.ep09.utils.ProjectProperties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.media.jai.PlanarImage;

import org.apache.log4j.Logger;
import org.geoserver.wps.WPSException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.jaitools.numeric.Range;

public class Procedure {
	
	private static final Logger LOGGER = Logger.getLogger(Procedure.class);
	
	protected List<Range> rangeList;
	protected int[] outputRangeList;
	
	/*
	 * raster calculator
	 */
	public String rasterCalculator(String dem, String geology, String landCover) {
		String outputRaster = ProjectProperties.loadByName(Constants.TMP_PATH) + Constants.LANDSLIDE_RASTER + "_"
				+ System.currentTimeMillis() + Constants.TIF_EXTENSION;
		if (ProjectProperties.loadByName(Constants.IS_WIN) != null
				&& ProjectProperties.loadByName(Constants.IS_WIN).equals("true")) {
			GdalOperation.winRasterCalculator(geology, dem, landCover, outputRaster);
		}
		else {
			GdalOperation.rasterCalculator(geology, dem, landCover, outputRaster);
		}
		
		return outputRaster;
	}
	
	
	/*
	 * restituisce la band del raster
	 */
	protected int getRasterBand(GridCoverage2D raster) {
		PlanarImage planarImage = (PlanarImage) raster.getRenderedImage();
		if (planarImage != null) {
			return planarImage.getNumBands();
		}
		return 0;
	}
	
	/*
	 * metodo che legge il file xml di reclassificiazione (creato da il wps createReclassificationTable)
	 */
	protected void readReclassify(List<String> geologyReclassify) {
		rangeList = new ArrayList<Range>();
		outputRangeList = new int[geologyReclassify.size()];
		
		try {
			
			Iterator<String> iterator = geologyReclassify.iterator();
			int i = 0;
			while (iterator.hasNext()) {
				String reclassify = iterator.next();
				StringTokenizer token = new StringTokenizer(reclassify, "|");
				if (token.hasMoreTokens()) {
					String id = (String) token.nextElement();
					Range<Integer> r = new Range<Integer>(Integer.parseInt(id), true, Integer.parseInt(id), true);
					rangeList.add(r);
					
					String susceptibililty = (String) token.nextElement();
					outputRangeList[i] = Integer.parseInt(susceptibililty);
					i++;
				}
			}
			
		}
		catch (Exception e) {
			LOGGER.error("errore nella lettura dei parametri di reclassificazione");
			e.printStackTrace();
			throw new WPSException("error during reading reclassify parameter");
		}
	}
	
}
