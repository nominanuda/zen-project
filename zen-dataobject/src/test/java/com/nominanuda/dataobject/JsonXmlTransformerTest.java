package com.nominanuda.dataobject;

import java.io.CharArrayWriter;
import java.io.StringReader;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TransformerHandler;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import com.nominanuda.lang.InstanceFactory;
import com.nominanuda.xml.SAXPipeline;
import com.nominanuda.xml.XmlSerializer;

public class JsonXmlTransformerTest {
	@Test
	public void test() {
		String json = "{a:[1,2],b:{c:{},d:true}}";
		DataObject o = new JsonLooseParser().parseObject(json);

		SAXPipeline pipe = new SAXPipeline().add(
				new InstanceFactory<TransformerHandler>(
						new SimpleJsonXmlTransformer("root"))).complete();
		SAXPipeline nspipe = new SAXPipeline().add(
				new InstanceFactory<TransformerHandler>(
						new SimpleJsonXmlTransformer("urn:isbn:1", "i", "root",
								"_T_"))).complete();
		Assert.assertEquals(
				"<root><a>1</a><a>2</a><b><c/><d>true</d></b></root>",
				toXml(o, pipe));
		Assert.assertEquals(
				"<i:root xmlns:i=\"urn:isbn:1\"><i:a _T_=\"number\">1</i:a><i:a _T_=\"number\">2</i:a><i:b><i:c/><i:d _T_=\"bool\">true</i:d></i:b></i:root>",
				toXml(o, nspipe));
	}

	private String toXml(DataObject o, SAXPipeline pipe) {
		CharArrayWriter w = new CharArrayWriter();
		pipe.build(
				new SAXSource(new JsonXmlReader(), new InputSource(
						new StringReader(o.toString()))),
				new SAXResult(new XmlSerializer(w))).run();
		return new String(w.toCharArray());
	}
}
