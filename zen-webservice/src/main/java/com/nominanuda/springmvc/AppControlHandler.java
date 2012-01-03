package com.nominanuda.springmvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import com.nominanuda.codec.Digester;
import com.nominanuda.lang.Check;
import com.nominanuda.web.http.HttpProtocol;
//echo -n `date -u +%Y-%m-%dT%H:%M:%SZ` > /tmp/__dt;echo -n refresh_appctx`cat /tmp/__dt` > /tmp/__seed ; openssl dgst -sha256 -hmac "uwq76jksad09cckjlass" -binary /tmp/__seed  |base64|sed s/\+/%2B/g > /tmp/__digest ;curl -i "http://localhost:8080/dixero-music-catalog/appcontrol?cmd=refresh_appctx&ts=`cat /tmp/__dt`&digest=`cat /tmp/__digest`"
public class AppControlHandler implements HttpRequestHandler, ApplicationContextAware {
	private static final DateTimeFormatter isoFmt = ISODateTimeFormat
		.dateTimeNoMillis().withZone(DateTimeZone.UTC);
	private static final char[] badReqMsg = "bad request".toCharArray();
	private static final char[] okMsg = "ok".toCharArray();
	private Digester digester;
	private long timestampValidityMillis;
	private ApplicationContext applicationContext;

	public synchronized void handleRequest(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		try {
			Thread.sleep(1000);
			String appCommand = req.getParameter("cmd");
			String tsStr = req.getParameter("ts");
			String digest = req.getParameter("digest");
			Check.illegalargument.assertTrue(digest.equals(
					digester.hmacSHA256(appCommand+tsStr).toBase64Classic()));
			DateTime ts = isoFmt.parseDateTime(tsStr);
			Check.illegalargument.assertTrue(
				ts.plus(timestampValidityMillis).isAfter(new DateTime()));
			if("refresh_appctx".equals(appCommand)) {
				((AbstractApplicationContext) applicationContext).refresh();
				res.setContentType(HttpProtocol.CT_TEXT_PLAIN_CS_UTF8);
				res.setContentLength(okMsg.length);
				res.getWriter().write(okMsg);
			} else {
				Check.illegalargument.fail();
			}
		} catch(Exception e) {
			res.setStatus(400);
			res.setContentType(HttpProtocol.CT_TEXT_PLAIN_CS_UTF8);
			res.setContentLength(badReqMsg.length);
			res.getWriter().write(badReqMsg);
		}
	}

	public void setTimestampValidityMillis(long timestampValidityMillis) {
		this.timestampValidityMillis = timestampValidityMillis;
	}

	public void setSecretKey(String secretKey) {
		digester = new Digester().withSecretKeySpec(secretKey);
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
