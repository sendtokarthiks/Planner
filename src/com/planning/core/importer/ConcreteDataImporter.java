package com.planning.core.importer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Concrete implementation for importing data. Can be extended to perform specialized importing tasks.
 * @author Karthik
 *
 * @param Entity
 */
public class ConcreteDataImporter<T> implements DataImporter<T> {

	@Override
	public T importData(String jsonName, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		byte[] jsonData = new byte[100];
		try {
			jsonData = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(jsonName).toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (jsonData.length != 0) {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(jsonData, clazz);
		} else {
			return null;
		}
	}
}
