package org.catais.gpkg.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment env;

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index.html";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public void uploadFile(
			@RequestParam(name="referenceFrame", defaultValue="2056", required=true) String referenceFrame, 
			@RequestParam(name="strokeArcs", required=false) String strokeArcs,
			@RequestParam(name="skipPolygonBuilding", required=false) String skipPolygonBuilding,
			@RequestParam(name="nameByTopic", required=false) String nameByTopic,
			@RequestParam(name="noSmartMapping", required=false) String noSmartMapping,
			@RequestParam(name="file", required=true) MultipartFile uploadfile) {
		
		log.info(referenceFrame);
		log.info(strokeArcs);
		log.info(skipPolygonBuilding);
		log.info(nameByTopic);
		log.info(noSmartMapping);
		log.info(uploadfile.getOriginalFilename());
		
	}

	
//	@RequestMapping(value = "/", method = RequestMethod.POST)
//	@ResponseBody
//	public ResponseEntity<InputStreamResource> uploadFile(
//			@RequestParam("file") MultipartFile uploadfile) {
//
//		try {
//			// Get the filename and build the local file path
//			String filename = uploadfile.getOriginalFilename();
//			String directory = env.getProperty("ch.so.agi.interlis.paths.uploadedFiles");
//
//			String tmpDirectoryPrefix = "ilivalidator_";
//			Path tmpDirectory = Files.createTempDirectory(Paths.get(directory), tmpDirectoryPrefix);
//
//			String filepath = Paths.get(tmpDirectory.toString(), filename).toString();
//
//			// Save the file locally
//			BufferedOutputStream stream =
//					new BufferedOutputStream(new FileOutputStream(new File(filepath)));
//			stream.write(uploadfile.getBytes());
//			stream.close();
//
//			// Validate transfer file 
//			String logFileName = ilivalidator.validate(filepath);
//
//			File logFile = new File(logFileName);
//			InputStream is = new FileInputStream(logFile);
//
//			return ResponseEntity
//					.ok()
//					.contentLength(logFile.length())
//					.contentType(MediaType.parseMediaType("text/plain"))
//					//.contentType(MediaType.parseMediaType("application/octet-stream"))
//					.body(new InputStreamResource(is));	      
//		}
//		catch (Exception e) {
//			System.out.println(e.getMessage());
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}
//
//	} 

}
