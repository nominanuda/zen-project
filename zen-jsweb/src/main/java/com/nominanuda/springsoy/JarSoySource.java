package com.nominanuda.springsoy;

import static com.nominanuda.io.IOHelper.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.template.soy.SoyFileSet.Builder;
import com.nominanuda.lang.Strings;
import com.nominanuda.lang.Tuple2;


public class JarSoySource extends SoySource {
	private List<String> templatesLocations = initTemplatesLocations();
	
	protected List<String> initTemplatesLocations() {
		return Collections.emptyList();
	}
	
	@Override
	protected void cumulate(Builder builder, List<String> jsTplNames) throws IOException {
		List<Tuple2<String, String>> templateFileUrls = new LinkedList<Tuple2<String,String>>();
		for (String templatesLocation : templatesLocations) {
			List<Tuple2<String, String>> entries = getEntries(templatesLocation, new Function<String, Boolean>() {
				public Boolean apply(String param) {
					return param.endsWith(".soy");
				}
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

	private List<Tuple2<String, String>> getEntries(String jarOrFileSrcDir, Function<String,Boolean> namePredicate) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL r = cl.getResource(jarOrFileSrcDir);
		if (null != r) {
			String dirUrl = r.getFile();
			if (dirUrl.contains("!")) {
				return getJarEntries(dirUrl, namePredicate);
			} else {
				dirUrl = new File(dirUrl).getAbsolutePath(); // to compute the right length on win systems
				int prefixLength = dirUrl.length() - jarOrFileSrcDir.length();
				return getDirEntries(prefixLength, dirUrl, namePredicate);
			}
		} else {
			log.warn("no url for this src: " + jarOrFileSrcDir);
			return null;
		}
	}

	private List<Tuple2<String, String>> getDirEntries(int prefixLength, String srcDir, final Function<String,Boolean> namePredicate) throws IOException {
		File dir = new File(srcDir);
		String[] files = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return namePredicate.apply(name);
			}
		});
		String[] dirs= dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});
		LinkedList<Tuple2<String, String>> l = new LinkedList<Tuple2<String,String>>();
		for (String f : files) {
			String filename = Strings.pathConcat(dir.getAbsolutePath(), f).replace("\\", "/"); // for win systems
			l.add(new Tuple2<String, String>(filename.substring(prefixLength), IO.readAndCloseUtf8(new FileInputStream(new File(filename)))));
		}
		for (String d : dirs) {
			l.addAll(getDirEntries(prefixLength, Strings.pathConcat(srcDir, d), namePredicate));
		}
		return l;
	}
	
	private List<Tuple2<String, String>> getJarEntries(String jarSrcDir, Function<String,Boolean> namePredicate) throws IOException {
		List<Tuple2<String, String>> result = new LinkedList<Tuple2<String, String>>();
		String[] arr = jarSrcDir.substring("file:".length()).split("!");
		String jarPath = arr[0];
		String dirPathInJar = arr[1].substring(1) + "/";
		//int dirPathInJarLen = dirPathInJar.length();
		File f = new File(jarPath);
		JarFile jf = new JarFile(f);
		Enumeration<JarEntry> entries = jf.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (entryName.startsWith(dirPathInJar) && !dirPathInJar.equals(entryName)) {
				//String subResource = entryName.substring(dirPathInJarLen);
				if(namePredicate.apply(entryName)) {
					result.add(new Tuple2<String, String>(entryName, IO.readAndCloseUtf8(jf.getInputStream(entry))));
				}
			}
		}
		jf.close();
		return result;
	}
	
	
	/* setters */
	
	public void setTemplatesLocations(String... templatesLocation) {
		templatesLocations = Arrays.asList(templatesLocation);
	}
}
