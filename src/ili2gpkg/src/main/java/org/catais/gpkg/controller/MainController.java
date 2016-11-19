package org.catais.gpkg.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.catais.gpkg.service.Ili2gpkgService;

@Controller
public class MainController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment env;
	
	@Autowired
	Ili2gpkgService ili2gpkg;


	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index.html";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> uploadFile(
			@RequestParam(name="referenceFrame", defaultValue="2056", required=true) String referenceFrame, 
			@RequestParam(name="strokeArcs", required=false) String strokeArcs,
			@RequestParam(name="skipPolygonBuilding", required=false) String skipPolygonBuilding,
			@RequestParam(name="nameByTopic", required=false) String nameByTopic,
			@RequestParam(name="noSmartMapping", required=false) String noSmartMapping,
			@RequestParam(name="file", required=true) MultipartFile uploadfile) {
		
		log.debug(referenceFrame);
		log.debug(strokeArcs);
		log.debug(skipPolygonBuilding);
		log.debug(nameByTopic);
		log.debug(noSmartMapping);
		log.debug(uploadfile.getOriginalFilename());
		log.debug(Boolean.toString(uploadfile.isEmpty()));
		
		
		// Very simple validation.
		// TODO: Consider org.springframework.validation.Validator
		
		// 1. file size
		if (uploadfile.isEmpty()) {
	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)                 
	                .body("error: empty file");
		}
		
		// 2. file extension
		String uploadFileExt = FilenameUtils.getExtension(uploadfile.getOriginalFilename());
		if (!uploadFileExt.equalsIgnoreCase("itf") && !uploadFileExt.equalsIgnoreCase("xtf")) {
	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)                 
	                .body("error: no interlis file");
		}
		
		try {
			// Save uploaded file in temporary directory.
			String filename = uploadfile.getOriginalFilename();
			String directory = env.getProperty("org.catais.gpkg.uploadedFiles");

			String tmpDirectoryPrefix = "ili2gpkg_";
			Path tmpDirectory = Files.createTempDirectory(Paths.get(directory), tmpDirectoryPrefix);

			String filepath = Paths.get(tmpDirectory.toString(), filename).toString();

			BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(uploadfile.getBytes());
			stream.close();
			
			// Translate interlis transfer file to geopackage.
			String resultFileName = ili2gpkg.translate(filepath, referenceFrame, strokeArcs, skipPolygonBuilding, 
					nameByTopic, noSmartMapping);

			log.debug(resultFileName);
			
			// Sent result file to client.
			// TODO: consider FileSystemResource

			File resultFile = new File(resultFileName);
			InputStream is = new FileInputStream(resultFile);
			
			return ResponseEntity
					.ok().header("content-disposition", "attachment; filename=" + resultFile.getName())
					.contentLength(resultFile.length())
//					.contentType(MediaType.parseMediaType("text/plain"))
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(new InputStreamResource(is));	      

		} catch (Exception e) {
			log.error(e.getMessage());
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)                 
	                .body(e.getMessage());
		}		
	}
}
