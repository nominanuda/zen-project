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
package com.nominanuda.jai;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.nominanuda.code.Nullable;
import com.nominanuda.lang.Check;
import com.nominanuda.lang.Tuple2;

public class ImageAwtTransformer {

	public Tuple2<BufferedImage, String>  readImageAndFormat(InputStream is) throws IOException, IllegalArgumentException {
		ImageInputStream iis = ImageIO.createImageInputStream(is);
		Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
		if (!readers.hasNext()) {
			iis.close();
			throw new IllegalArgumentException("unsupported image type");
		}
		ImageReader reader = readers.next();
		String inputFormat = reader.getFormatName();
		reader.setInput(iis, true, true);
		BufferedImage src = reader.read(0);
		reader.dispose();
		iis.close();
		return new Tuple2<BufferedImage, String>(src, inputFormat);
	}

	public long transform(InputStream is, OutputStream os, @Nullable Integer targetWidth,
			@Nullable Integer targetHeigth, @Nullable String outputFormat,
			boolean allowDistort) throws IOException, IllegalArgumentException {
		Tuple2<BufferedImage, String> biAndFmt = readImageAndFormat(is);
		BufferedImage src = biAndFmt.get0();
		String inputFormat = biAndFmt.get1();
		if (outputFormat == null) {
			outputFormat = inputFormat;
		}
		return transform(src, os, targetWidth, targetHeigth, outputFormat, allowDistort);
	}

	public long transform(InputStream is, OutputStream os, @Nullable String outputFormat, int[] g/*see clipCalc*/) throws IOException, IllegalArgumentException {
		Tuple2<BufferedImage, String> biAndFmt = readImageAndFormat(is);
		BufferedImage src = biAndFmt.get0();
		String inputFormat = biAndFmt.get1();
		if (outputFormat == null) {
			outputFormat = inputFormat;
		}
		return transform(src, os, outputFormat, g);
	}
	public long transform(BufferedImage src, OutputStream os, @Nullable Integer _targetWidth,
			@Nullable Integer targetHeigth, String outputFormat,
			boolean allowDistort) throws IOException, IllegalArgumentException {
		Integer targetWidth = Check.ifNull(_targetWidth, src.getWidth());
		int[] g = clipCalc(src.getWidth(), src.getHeight(), targetWidth, targetHeigth, allowDistort);
		return transform(src, os, outputFormat, g);
	}

	public long transform(BufferedImage src, OutputStream os,
			String outputFormat, int[] g/*see clipCalc*/) throws IOException {
		BufferedImage bi = new BufferedImage(g[4], g[5], BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		g2d.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(
			src.getSubimage(g[0], g[1], g[2] - g[0], g[3] - g[1]), 
			0, 0, g[4], g[5], null);
		g2d.dispose();
		bi.flush();
		MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(os);
		try {
			Check.illegalargument.assertTrue(
				ImageIO.write(bi, outputFormat, ios), "could not perform conversion");
			long len = ios.length();
			return len;
		} finally {
			ios.close();
		}
	}
	/**
	 * 
	 * @param sw
	 *            source canvas width
	 * @param sh
	 *            source canvas height
	 * @param dw
	 *            dest canvas width
	 * @param dh
	 *            dest canvas height (nullable)
	 * @param allowDistort
	 * @return six integers that represent P1(0,1) P2(2,3) W(4) H(5) where P1
	 *         and P2 are the src clipping points and W,H are the actual dest
	 *         width/height
	 */
	protected int[] clipCalc(int sw, int sh, int dw, @Nullable Integer dh,
			boolean allowDistort/*, boolean allowExpand */) {
		Check.notNull(dw);
		boolean allowExpand = allowDistort;
		if (dh == null) {// full
			return sw < dw ? new int[] { 0, 0, sw, sh, sw, sh } : new int[] {
					0, 0, sw, sh, dw, scale(sh, dw, sw) };
		} else if (allowDistort && allowExpand) {
			return new int[] { 0, 0, sw, sh, dw, dh };
		} else {// clever scaling
			if (dw > sw && dh > sh) {
				return new int[] { 0, 0, sw, sh, sw, sh };
			} else if (dh > sh) {
				int margin = approx((double) (sw - dw) / 2);
				return new int[] { 0 + margin, 0, sw - margin, sh, dw, sh };
			} else if (dw > sw) {
				int margin = approx((double) (sh - dh) / 2);
				return new int[] { 0, 0 + margin, sw, sh - margin, sw, dh };
			} else {
				double xRatio = (double) sw / (double) dw;
				double yRatio = (double) sh / (double) dh;
				if (xRatio > yRatio) {
					int margin = approx((double) (sw - scale(dw, sh, dh)) / 2);
					return new int[] { 0 + margin, 0, sw - margin, sh, dw, dh };
				} else {
					int margin = approx((double) (sh - scale(dh, sw, dw)) / 2);
					return new int[] { 0, 0 + margin, sw, sh - margin, dw, dh };
				}
			}
		}
	}

	private int scale(int ori, int x, int y) {
		double a = ori, b = x, c = y;
		return approx(a * b / c);
	}

	private int approx(double d) {
		return new Double(d).intValue();
	}
}
