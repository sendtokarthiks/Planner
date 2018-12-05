package com.planning.core.importer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Super type of data importers.
 * @author Karthik
 *
 * @param <T>
 */
public interface DataImporter<T> {
	T importData(String jsonName, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException;
}
