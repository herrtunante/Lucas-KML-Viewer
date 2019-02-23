package io.paus.tools.lucas.kmlviewer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class GenerateKmlLucasViewer {

	private static final Logger logger = LoggerFactory.getLogger( GenerateKmlLucasViewer.class );

	public static void main(String[] args) {
		File csvFile;
		try {
			ClassLoader classLoader = GenerateKmlLucasViewer.class.getClassLoader();
			csvFile = new File(  classLoader.getResource("EU28_2015_20180724.csv").toURI() );


			GenerateKmlLucasViewer generateKmlLucasViewer = new GenerateKmlLucasViewer();
			Map<String, HashMap<String, List<LucasPoint>>> data = generateKmlLucasViewer.getDataFromCsv( csvFile );

			// Process the template file using the data in the "data" Map
			final File templateFile = new File( classLoader.getResource("kmlTemplate.fmt").toURI());

			File outputFolder = new File("LUCAS_2015_KMLs");
			if( !outputFolder.isDirectory() ) {
				outputFolder.mkdir();
			}

			try {
				Iterator it = data.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry)it.next();
					String country = (String) pair.getKey();
					HashMap<String, List<LucasPoint>> dataPerCountry = (HashMap<String, List<LucasPoint>>) pair.getValue();

					File destinationFile = new File( outputFolder, country + "_LUCAS_2015.kml");

					FreemarkerTemplateUtils.applyTemplate(templateFile, destinationFile, dataPerCountry);
					System.out.println("KML file generated " + destinationFile.getAbsolutePath() );
				}


			} catch (Exception e) {
				logger.error("Error producing kml",e);
			}
		} catch (URISyntaxException e1) {
			logger.error("Could not load file", e1);
		}
	}

	public Map<String, HashMap<String, List<LucasPoint>>> getDataFromCsv( File csvFile ){

		final Map<String, HashMap<String, List<LucasPoint>>> countriesData = new HashMap<String, HashMap<String, List<LucasPoint>>>();

		String[] csvRow;
		String[] headerRow =null;
		int rowNumber = 0 ;

		// POINT_ID	NUTS0	NUTS1	NUTS2	TH_LAT	TH_LONG	DATE	OBS_TYPE	PI_EXTENSION	GPS_PROJ	GPS_PREC	GPS_LAT	GPS_LONG	GPS_ALTITUDE	OBS_DIST	OBS_DIR	LC1	LC1_SPECIES	LC1_PCT	LC2	LC2_SPECIES	LC2_PCT	OBS_RADIUS	LU1	LU1_TYPE	LU1_PCT	LU2	LU2_TYPE	LU2_PCT	AREA_SIZE	TREES_HEIGHT_MATURITY	TREES_HEIGHT_SURVEY	FEATURE_WIDTH	LAND_MNGT	LC_LU_SPECIAL_REMARK	PROTECTED_AREA	WM	WM_SOURCE	WM_TYPE	WM_DELIVERY	SOIL_TAKEN	SOIL_PLOUGH	SOIL_CROP	SOIL_STONES	PHOTO_P	PHOTO_N	PHOTO_E	PHOTO_S	PHOTO_W	INSPIRE_PLCC1	INSPIRE_PLCC2	INSPIRE_PLCC3	INSPIRE_PLCC4	INSPIRE_PLCC5	INSPIRE_PLCC6	INSPIRE_PLCC7	INSPIRE_PLCC8	TRANSECT						
		// 26381958	PT	PT1	PT17	38.75777092	-9.4761107	13/08/2015	2	NA	1	2	38.75697	-9.47547	145	105	1	D20	8	4	8	8	8	2	U420	8	7	8	8	8	4	8	8	8	2	7	4	8	8	8	8	4	8	8	8	1	1	1	1	1	0	0	40	10	0	0	50	0	D20	61	51	61	C22	21	61



		CSVReader reader = null;
		String country = "";

		try {
			reader = CsvReaderUtils.getCsvReader( csvFile );

			while ((csvRow = reader.readNext()) != null) {
				try {
					country = csvRow[1];
					if( headerRow == null ){
						headerRow = csvRow;
					}

					// Check that the row is not just an empty row with no data
					if( CsvReaderUtils.onlyEmptyCells(csvRow)){
						// If the row is empty ( e.g. : ",,,,," ) jump to next row
						continue;
					}

					LucasPoint lucasPoint = new LucasPoint( csvRow[0],  Float.parseFloat( csvRow[4] ),  Float.parseFloat( csvRow[5] ), country, csvRow[12]) ;

					rowNumber++;

					HashMap<String, List<LucasPoint>> countryData = countriesData.get( country );
					if( countryData == null ) {
						List<LucasPoint> points = new ArrayList<LucasPoint>();
						HashMap<String, List<LucasPoint>> data = new HashMap<String, List<LucasPoint>>();
						points.add( lucasPoint );
						data.put("points", points);
						countriesData.put( country, data);
					}else {
						countryData.get("points").add( lucasPoint );
					}

				} catch (final Exception e) {
					if(rowNumber > 0 ){
						logger.error( "Error on the CSV file ", e );
						throw new IllegalArgumentException("Error in the CSV " + csvFile + " \r\n for row " + rowNumber + " = " + Arrays.toString( csvRow ), e);
					}else{
						logger.info("Error while reading the first line of the CSV fle, probably cause by the column header names");
					}
				}finally{
					rowNumber++;
				}
			}
		} catch (final IOException e) {
			throw new IllegalArgumentException("Error reading CSV " + csvFile , e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("error closing the CSV reader", e);
				}
			}
		}


		return countriesData;
	}

}
