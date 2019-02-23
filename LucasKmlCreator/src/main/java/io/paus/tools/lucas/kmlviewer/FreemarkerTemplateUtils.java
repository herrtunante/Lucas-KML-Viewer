package io.paus.tools.lucas.kmlviewer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;


public class FreemarkerTemplateUtils {
	
	private FreemarkerTemplateUtils() {}

	public static boolean applyTemplate(File sourceTemplate, File destinationFile, Map<?, ?> data) throws IOException, TemplateException{

		boolean success = true;
		
		// Process the template file using the data in the "data" Map
		final Configuration cfg = new Configuration( new Version("2.3.23"));
		cfg.setDirectoryForTemplateLoading(sourceTemplate.getParentFile());

		// Load template from source folder
		final Template template = cfg.getTemplate(sourceTemplate.getName());

		// Console output
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFile), Charset.forName("UTF-8")));
			template.process(data, fw);
		}finally {
			if (fw != null) {
				fw.close();
			}
		}
		
		return success;
		
	}
	


}
