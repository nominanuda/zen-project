package com.nominanuda.xml;

import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XmlSerializerTest {

	@Test
	public void test() throws Exception {
		String s = "<z:a xmlns:z=\"http://foo.com\"/><a xmlns:unused=\"ftp:unused\"/><c xmlns=\"http://foo2.com\" xmlns:z=\"http://foo.com\"><z:D z:attr=\"1 or 2\"/></c>";
		StringWriter sw = new StringWriter();
		XmlSerializer ser = new XmlSerializer();
		ser.setWriter(sw);
		XMLReader r = new FragmentSaxParser();
		r.setContentHandler(ser);
		r.parse(new InputSource(new StringReader(s)));
		Assert.assertEquals(s, sw.toString());
	}

}
