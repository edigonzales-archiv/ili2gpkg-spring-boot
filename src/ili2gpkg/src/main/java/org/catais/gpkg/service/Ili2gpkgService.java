package org.catais.gpkg.service;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

@Service
public class Ili2gpkgService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public String translate(String fileName, String referenceFrame, String strokeArcs,
			String skipPolygonBuilding, String nameByTopic, String noSmartMapping) {
	
		Config config = new Config();

		config.setModeldir(ch.interlis.ili2c.Main.ILI_REPOSITORY);		
		config.setModels(Ili2db.XTF);

		config.setSqlNull("enable");
		config.setDefaultSrsAuthority("EPSG");
		config.setDefaultSrsCode("2056");
		config.setMaxSqlNameLength(Integer.toString(NameMapping.DEFAULT_NAME_LENGTH));
		config.setIdGenerator(ch.ehi.ili2db.base.TableBasedIdGen.class.getName());
		config.setInheritanceTrafo(Config.INHERITANCE_TRAFO_SMART1);
		config.setCatalogueRefTrafo(Config.CATALOGUE_REF_TRAFO_COALESCE);
		config.setMultiSurfaceTrafo(Config.MULTISURFACE_TRAFO_COALESCE);
		config.setMultilingualTrafo(Config.MULTILINGUAL_TRAFO_EXPAND);
		config.setGeometryConverter(ch.ehi.ili2gpkg.GpkgColumnConverter.class.getName());
		config.setDdlGenerator(ch.ehi.sqlgen.generator_impl.jdbc.GeneratorGeoPackage.class.getName());
		config.setJdbcDriver("org.sqlite.JDBC");
		config.setIdGenerator(ch.ehi.ili2db.base.TableBasedIdGen.class.getName());
		config.setIli2dbCustomStrategy(ch.ehi.ili2gpkg.GpkgMapping.class.getName());
		config.setOneGeomPerTable(true);

		if (referenceFrame.equalsIgnoreCase("21781")) {
			config.setDefaultSrsCode("21781");
		}

		if (strokeArcs != null) {
			config.setStrokeArcs(config.STROKE_ARCS_ENABLE);
		}

		if (skipPolygonBuilding != null) {
			config.setDoItfLineTables(true);
			config.setAreaRef(config.AREA_REF_KEEP);
		}
		
		if (nameByTopic != null) {
			config.setNameOptimization(config.NAME_OPTIMIZATION_TOPIC);
		}
		
		if (noSmartMapping != null) {
			config.setInheritanceTrafo(null);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
		}

		// if it is a ili1 transfer file
	    String fileExtension = FilenameUtils.getExtension(fileName);
	    if (fileExtension.equalsIgnoreCase("itf")) {
	    	config.setItfTransferfile(true);
	    }  
	    
	    String logFileName =  FilenameUtils.removeExtension(fileName) + ".log";
	    config.setLogfile(logFileName);

		String gpkgFileName = FilenameUtils.removeExtension(fileName) + ".gpkg";
		config.setDbfile(gpkgFileName);
		config.setDburl("jdbc:sqlite:" + config.getDbfile());

	    config.setXtffile(fileName);

	    try {
			Ili2db.runImport(config, "");
		} catch (Ili2dbException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return logFileName;
		}

		return gpkgFileName;
	}
}
