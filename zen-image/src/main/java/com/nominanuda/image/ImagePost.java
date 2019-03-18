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
import static com.nominanuda.zen.oio.OioUtils.IO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.entity.StringEntity;

import com.nominanuda.web.http.Http500Exception;
import com.nominanuda.web.http.HttpProtocol;
import com.nominanuda.web.mvc.CommandRequestHandler;
import com.nominanuda.zen.codec.Digester;
import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.common.Tuple2;
import static com.nominanuda.zen.io.Uris.URIS;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.Stru;

public class ImagePost implements CommandRequestHandler, HttpProtocol {
	
	/**
	 * use "{code}" in the UriTemplate
	 * ONLY IF the corresponding match
	 * has to be used as the file name
	 * instead of the computed MD5.
	 * 
	 * Example: POST /{code}.{ext jpg|jpeg|png}
	 * (otherwise just use something different, like: POST /{name}.{ext jpg|jpeg|png})
	 */
	private final static String CMD_CODE = "code";
	
	private final ImageAwtTransformer imageSizeFilter = new ImageAwtTransformer();
	private final Digester digester = new Digester();
	private boolean forceTextResponse = false; // for ie9- compatibility
	private String resourcePrefix = "";
	private ImageStore imageStore;

	public Object handle(Stru _cmd, HttpRequest request) throws Exception {
		try {
			Check.illegalargument.assertEquals(POST, request.getRequestLine().getMethod());
			HttpEntity entity = Check.illegalargument.assertNotNull(HTTP.getEntity(request));
			Obj cmd = _cmd.asObj();
			
			byte[] barr;
			if (HTTP.isMultipart(entity)) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				entity.writeTo(baos); // because getContent() can be unimplemented
				DataSource ds = new ByteArrayDataSource(baos.toByteArray(), "application/octet-stream");
				BodyPart bp = new MimeMultipart(ds).getBodyPart(0);
				barr = IO.readAndClose(bp.getInputStream());
			} else {
				barr = IO.readAndClose(entity.getContent());
			}
			
			Obj response = post(barr, cmd.getStr(CMD_CODE));
			if (forceTextResponse) {
				StringEntity resp = new StringEntity(response.toString());
				resp.setContentType(CT_TEXT_PLAIN);
				return resp;
			}
			return response;
			
		} catch(Exception e) {
			throw new Http500Exception(e);
		}
	}
	
	
	/* for usage in admin tools */
	
	public Obj post(byte[] barr, @Nullable String imageUriCode) throws Exception {
		if (imageUriCode == null) imageUriCode = digester.md5(barr).toBase62();
		Tuple2<BufferedImage, String> imgAndFmt = imageSizeFilter.readImageAndFormat(new ByteArrayInputStream(barr));
		String format = imgAndFmt.get1();
		imageStore.put(imageUriCode, format, barr);
		BufferedImage bi = imgAndFmt.get0();
		return Obj.make(
			"code", imageUriCode,
			"resource", URIS.pathJoin(resourcePrefix, imageUriCode),
			"width", bi.getWidth(),
			"height", bi.getHeight(),
			"size", barr.length,
			"format", format
		);
	}
	public Obj post(byte[] barr) throws Exception {
		return post(barr, null);
	}
	
	
	/* setters */

	public void setImageStore(ImageStore imageStore) {
		this.imageStore = imageStore;
	}
	
	public void setForceTextResponse(boolean flg) {
		forceTextResponse = flg;
	}
	
	public void setResourcePrefix(String prefix) {
		this.resourcePrefix = prefix;
	}
}
