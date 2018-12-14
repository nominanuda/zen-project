/*
 * Copyright 2008-2018 the original author or authors.
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
package com.nominanuda.image;

import static com.nominanuda.web.http.HttpCoreHelper.HTTP;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.CommandRequestHandler;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;
import com.nominanuda.zen.io.Uris;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;
import com.twelvemonkeys.image.ResampleOp;

public class ImageGet implements CommandRequestHandler, HttpProtocol, HttpStatus {
	private final static Pattern WxH_RE = Pattern.compile("^(\\d+)?x(\\d+)?$");
	
	private ImageStore imageStore;
	private JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);

	private class ImageTransformer extends ImageAwtTransformer {
		long resize(BufferedImage srcBI, OutputStream os, String outputFormat, Tuple2<Integer, Integer> wh) throws IOException {
			BufferedImage dstBI = null;
			int w1 = wh.get0(), h1 = wh.get1();
			int w0 = srcBI.getWidth(), h0 = srcBI.getHeight();
			if (w1 == w0 && h1 == h0) { // no resizing
				dstBI = srcBI;
			} else {
				int[] coords = clipCalc(w0, h0, w1, h1, false);
				BufferedImage subBI = srcBI.getSubimage(coords[0], coords[1], coords[2] - coords[0], coords[3] - coords[1]);
				try {
					dstBI = new ResampleOp(coords[4], coords[5], ResampleOp.FILTER_LANCZOS).filter(subBI, null);
				} catch (Exception e) { // trick because lanczos sometimes throws an ArrayIndexOutOfBoundsException (why?)
					dstBI = new ResampleOp(coords[4], coords[5], ResampleOp.FILTER_BLACKMAN_SINC).filter(subBI, null);
				}
			}
			try (ImageOutputStream memOs = new MemoryCacheImageOutputStream(os)) {
				Check.illegalargument.assertTrue(write(dstBI, memOs, outputFormat), "could not perform conversion");
				return memOs.length();
			}
		}
		
		private boolean write(BufferedImage bi, ImageOutputStream os, String format) throws IOException {
			switch (format) {
				case "jpg":
				case "jpeg":
					// note related to thread-safety: the .next() call internally creates a new instance of ImageWriter
					final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next(); // we assume to find it
					writer.setOutput(os);
					writer.write(null, new IIOImage(dealphize(bi), null, null), jpegParams);
					return true;
			}
			return ImageIO.write(bi, format, os);
		}
		
		private BufferedImage dealphize(BufferedImage bi) {
			BufferedImage newBI = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
			newBI.createGraphics().drawImage(bi, 0, 0, Color.WHITE, null);
			return newBI;
		}
	};
	private ImageTransformer imageSizeFilter = new ImageTransformer();
	

	// (path/)name.ext | (path/)name.xH.ext | (path/)name.Wx.ext | (path/)name.WxH.ext
	public Object handle(Stru _cmd, HttpRequest request) throws Exception {
		try {
			Check.illegalargument.assertEquals(GET, request.getRequestLine().getMethod());
			Obj cmd = _cmd.asObj();
			String path = cmd.getStr("path");
			String nameAndTx = cmd.getStrictStr("name");
			String ext = cmd.getStrictStr("ext");
			int dotPos = nameAndTx.indexOf('.');
			String name = dotPos > 0 ? nameAndTx.substring(0, dotPos) : nameAndTx;
			String tx = dotPos > 0 ? nameAndTx.substring(dotPos + 1) : null;
			if (path != null) name = Uris.URIS.pathJoin(path, name);
			
			Tuple2<String, byte[]> foundImage = imageStore.get(name, ext);
			if (foundImage == null) {
				return HTTP.createBasicResponse(
					SC_NOT_FOUND,
					Obj.make("reason", statusToReason.get(SC_NOT_FOUND)).toString(),
					CT_APPLICATION_JSON_CS_UTF8);
			} else {
				ByteArrayEntity res;
				byte[] image = foundImage.get1();
				if (tx == null && ext.equals(foundImage.get0())) {
					res = new ByteArrayEntity(image);
				} else {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					Tuple2<BufferedImage, String> biAndFmt = imageSizeFilter.readImageAndFormat(new ByteArrayInputStream(image));
					BufferedImage src = biAndFmt.get0();
					imageSizeFilter.resize(src, baos, ext, getWidthAndHeight(src.getWidth(), src.getHeight(), tx));
					res = new ByteArrayEntity(baos.toByteArray());
				}
				res.setContentType(fmt2contentType(ext));
				return res;
			}
		} catch (Exception e) {
			throw new Http500Exception(e);
		}
	}
	

	private Tuple2<Integer, Integer> getWidthAndHeight(int realW, int realH, String tx) {
		if (tx != null) {
			Matcher m = WxH_RE.matcher(tx);
			if (m.find()) {
				String w = Check.ifNullOrEmpty(m.group(1), null);
				String h = Check.ifNullOrEmpty(m.group(2), null);
				Check.illegalargument.assertFalse(w == null && h == null, "unrecognized transformation " + tx);
				Integer wi = w == null ? null : Integer.valueOf(w);
				Integer hi = h == null ? null : Integer.valueOf(h);
				if (wi == null) {
					wi = new Double((double) realW * hi / realH).intValue();
				} else if (hi == null) {
					hi = new Double((double) realH * wi / realW).intValue();
				}
				return new Tuple2<Integer, Integer>(wi, hi);
			} else {
				throw new IllegalArgumentException("unrecognized transformation " + tx);
			}
		}
		return new Tuple2<Integer, Integer>(realW, realH);
	}

	private String fmt2contentType(String ext) {
		if ("jpeg".equals(ext) || "jpg".equals(ext)) {
			return CT_IMAGE_JPEG;
		} else if ("png".equals(ext)) {
			return CT_IMAGE_PNG;
		} else {
			throw new IllegalArgumentException("unrecognized format " + ext);
		}
	}
	
	
	/* setters */

	public void setImageStore(ImageStore byteArrayStore) {
		this.imageStore = byteArrayStore;
	}
	
	public void setJpegCompressionQuality(float quality) {
		jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		jpegParams.setCompressionQuality(quality);
	}
}