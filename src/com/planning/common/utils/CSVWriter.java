package com.planning.common.utils;

import java.io.FileWriter;
import java.util.List;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
/**
 * CSV Writer Utility.
 * @author Karthik
 *
 */
public class CSVWriter {
	public static void writeToCsv(List<?> entities, String csvName) {

		try {

			FileWriter writer = new FileWriter(csvName);

			StatefulBeanToCsvBuilder builder = new StatefulBeanToCsvBuilder(writer);
			
			StatefulBeanToCsv beanWriter = builder.withQuotechar('\0').build();

			beanWriter.write(entities);

			// closing the writer object
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
