package japp.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import au.com.bytecode.opencsv.CSVWriter;

public abstract class CsvHelper {
	
	private static final String CHARSET_UTF_8 = "UTF-8";
	
	protected CsvHelper() {
		
	}
	
	public static void convert(final String[] header, final String[] content, final OutputStream outputStream) throws IOException {
		final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, CHARSET_UTF_8));
		bufferedWriter.write('\ufeff');
		
		final CSVWriter csvWriter = new CSVWriter(bufferedWriter, ';');
		
		csvWriter.writeNext(header);
		
		for (int i = 0; i < content.length; i++) {
			final String[] columns = new String[header.length];
			
			for (int j = 0; j < columns.length; j++, i++) {
				columns[j] = content[i];
			}
			
			i--;
			
			csvWriter.writeNext(columns);
		}
		
		csvWriter.close();
	}
}
