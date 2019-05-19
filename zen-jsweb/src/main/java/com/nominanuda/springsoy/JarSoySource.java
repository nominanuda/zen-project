package com.nominanuda.springsoy;

import static com.nominanuda.zen.oio.OioUtils.IO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.template.soy.SoyFileSet.Builder;
import com.nominanuda.zen.common.Tuple2;


public class JarSoySource extends SoySource {
	private final List<String> templatesLocations = new ArrayList<>();
	
	
	public JarSoySource(String... baseTemplatesLocations) {
		setTemplatesLocations(baseTemplatesLocations);
	}
	public JarSoySource() {
	}
	
	
	@Override
	protected void cumulate(Builder builder, List<String> jsTplNames) throws IOException {
		List<Tuple2<String, String>> templateFileUrls = new LinkedList<>();
		for (String templatesLocation : templatesLocations) {
			List<Tuple2<String, String>> entries = IO.getEntries(templatesLocation,
				param -> param.endsWith(".soy"),
				(name, is) -> {
					try {
						return IO.readAndCloseUtf8(is);
					} catch (IOException e) {
						// nothing to do
					}
					return null;
				});
			if (null != entries) {
				templateFileUrls.addAll(entries);
			}
		}
		for (Tuple2<String, String> templateFile : templateFileUrls) {
			builder.add(templateFile.get1(), templateFile.get0());
			jsTplNames.add(templateFile.get0()); //jsTplNames.add(IO.getLastPathSegment(templateFile.get0()));
		}
	}
	

	/* setters */
	
	public void setTemplatesLocations(String... templatesLocation) {
		this.templatesLocations.addAll(Arrays.asList(templatesLocation));
	}
}
