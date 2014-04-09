/*
 * Copyright 2008-2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nominanuda.io;

import java.util.HashMap;
import java.util.Map;

import com.nominanuda.lang.Collections;

public class ContentTypeHelper {

	/**
	 * if the given suffix contains dots it is iterally matched from the more specific to the less
	 * eg first is tried tar.gz and if not found just gz
	 * @param suffix with no trailing dots eg xml html txt jpeg jpg mov (also containing dots as in xmap.xml, tar.gz etc)
	 * @return the mime-type found or null
	 * @throws NPE on null input
	 */
	//TODO path split by slash + match filenames such as a.b.c.tar.gz
	public static String getMimeTypeByPath(String suffix) {
		while(suffix.startsWith(".") && suffix.length() > 0) {
			suffix = suffix.substring(1);
		}
		String mimeType = null;
		while((mimeType = findInMap(suffix)) == null) {
			int pos = suffix.indexOf('.');
			if(pos == -1) {
				break;
			}
			suffix = suffix.substring(pos+1);
		}
		return mimeType;
	}
	private static String findInMap(String suffix) {
		return suffixes.get(suffix);
	}
	@SuppressWarnings("unchecked")
	private static final Map<String, String> suffixes = Collections.buildMap(HashMap.class, 
		"aif", "audio/x-aiff",
		"aifc", "audio/x-aiff",
		"aiff", "audio/x-aiff",
		"amr", "audio/amr",
		"au", "audio/basic",
		"avi", "video/x-msvideo",
		"bmp", "image/bmp",
		"css", "text/css",
		"doc", "application/msword",
		"dtd", "application/xml-dtd",
		"dvi", "application/x-dvi",
		"exe", "application/octet-stream",
		"flv", "video/x-flv",
		"gif", "image/gif",
		"gz", "application/x-gzip",
		"html", "text/html",
		"htm", "text/html",
		"ico", "image/x-icon",
		"ics", "text/calendar",
		"ifb", "text/calendar",
		"jpeg", "image/jpeg",
		"jpe", "image/jpeg",
		"jpg", "image/jpeg",
		"js", "application/x-javascript",
		"kar", "audio/midi",
		"m3u", "audio/x-mpegurl",
		"m4a", "audio/mp4",
		"mathml", "application/mathml+xml",
		"mid", "audio/midi",
		"midi", "audio/midi",
		"mov", "video/quicktime",
		"mp2", "audio/mpeg",
		"mp3", "audio/mpeg",
		"mpeg", "video/mpeg",
		"mpe", "video/mpeg",
		"mpga", "audio/mpeg",
		"mpg", "video/mpeg",
		"ogg", "application/ogg",
		"pdf", "application/pdf",
		"png", "image/png",
		"ppt", "application/vnd.ms-powerpoint",
		"qt", "video/quicktime",
		"ra", "audio/x-realaudio",
		"ram", "audio/x-pn-realaudio",
		"rdf", "application/rdf+xml",
		"rm", "audio/x-pn-realaudio",
		"rtf", "text/rtf",
		"shtml", "text/html",
		"snd", "audio/basic",
		"svg", "image/svg+xml",
		"swf", "application/x-shockwave-flash",
		"tar", "application/x-tar",
		"tar.gz", "application/x-tar",
		"tgz", "application/x-tar",
		"tiff", "image/tiff",
		"tif", "image/tiff",
		"txt", "text/plain",
		"vxml", "application/voicexml+xml",
		"wav", "audio/x-wav",
		"xht", "application/xhtml+xml",
		"xhtml", "application/xhtml+xml",
		"xls", "application/vnd.ms-excel",
		"xml", "application/xml",
		"xsl", "application/xslt+xml",
		"xslt", "application/xslt+xml",
		"zip", "application/zip"
	);
}
