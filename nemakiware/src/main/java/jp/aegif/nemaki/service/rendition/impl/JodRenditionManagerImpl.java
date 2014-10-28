package jp.aegif.nemaki.service.rendition.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DefaultDocumentFormatRegistry;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.codehaus.jackson.format.DataFormatDetector;

import jp.aegif.nemaki.service.rendition.RenditionManager;
import jp.aegif.nemaki.util.PropertyUtil;
import jp.aegif.nemaki.util.YamlManager;
import jp.aegif.nemaki.util.constant.PropertyKey;

public class JodRenditionManagerImpl implements RenditionManager {

	private PropertyUtil propertyUtil;
	private DefaultDocumentFormatRegistry registry;
	private static final Log log = LogFactory
			.getLog(JodRenditionManagerImpl.class);
	
	public JodRenditionManagerImpl(){
		
	}
	
	public JodRenditionManagerImpl(PropertyUtil propertyUtil){
		setPropertyUtil(propertyUtil);
		
		registry = new DefaultDocumentFormatRegistry();

		String definitionFile = "";
		try {
			definitionFile = propertyUtil.getPropertyManager()
					.readValue(PropertyKey.JODCONVERTER_REGISTRY_DATAFORMATS);
		} catch (Exception e) {
			log.error("Cannot read a permission definition file", e);
		}

		//Parse definition file
		YamlManager manager = new YamlManager(definitionFile);
		List<Map<String, Object>> yml = (List<Map<String, Object>>) manager
				.loadYml();

		if(CollectionUtils.isNotEmpty(yml)){
			for(Map<String, Object> format : yml){
				String name = (String)(format.get("name"));
				String extension = (String)(format.get("extension"));
				String mediaType = (String)(format.get("mediaType"));
				
				DocumentFormat df = new DocumentFormat(name, extension, mediaType);
				registry.addFormat(df);
			}
			
		}
	}

	public ContentStream convertToPdf(ContentStream contentStream,
			String documentName) {
		OutputStream outputStream = null;
		try {
			String prefix = getPrefix(documentName);
			String suffix = getSuffix(documentName);
			File inputFile = convertInputStreamToFile(prefix, "." + suffix,
					contentStream.getStream());
			inputFile.deleteOnExit();
			File outputFile = File.createTempFile("output", ".pdf");
			outputFile.deleteOnExit();
			
			String officehome = propertyUtil.getPropertyManager().readValue(
					PropertyKey.JODCONVERTER_OFFICEHOME);

			OfficeManager officeManager = new DefaultOfficeManagerConfiguration()
					.setPortNumber(8100).setOfficeHome(officehome)
					.buildOfficeManager();
			officeManager.start();

			OfficeDocumentConverter converter = new OfficeDocumentConverter(
					officeManager);
			converter.convert(inputFile, outputFile);

			officeManager.stop();

			// convert back
			FileInputStream fis = new FileInputStream(outputFile);
			ContentStreamImpl result = new ContentStreamImpl();
			result.setStream(fis);
			result.setFileName(contentStream.getFileName());
			result.setMimeType("application/pdf");
			result.setLength(BigInteger.valueOf(outputFile.length()));

			return result;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.debug(e);
		} finally {

			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		return null;
	}

	/**
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private File convertInputStreamToFile(String prefix, String suffix,
			InputStream inputStream) throws IOException {

		File file = File.createTempFile(prefix, suffix);
		try {
			OutputStream out = new FileOutputStream(file);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			inputStream.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return file;
	}

	private String getSuffix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(point + 1);
		}
		return fileName;
	}

	private String getPrefix(String fileName) {
		if (fileName == null)
			return null;
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			return fileName.substring(0, point);
		}
		return fileName;
	}
	
	public boolean checkConvertible(String mediatype){
		DocumentFormat df = registry.getFormatByMediaType(mediatype);
		return df != null;
	}
	

	public void setPropertyUtil(PropertyUtil propertyUtil) {
		this.propertyUtil = propertyUtil;
	}
}