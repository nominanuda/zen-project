package com.nominanuda.apikey;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.SimpleJixParser;

public class ApikeyTestTypeConversionTest {
	public static final String AES_SECRET = "sytm7Xksrkf9Z6EcLzG3ze6NGcbQAkVZNmuxCMcGX84VbKf5SujAaJzSEpeT4b6Z";
	private static ApikeyManager apkManager;
	static {
		Map<String, KeySet> keySetDef = new LinkedHashMap<String, KeySet>();
		KeySet ks = new KeySet();
		ks.setCipher("aes");
		ks.setKey(AES_SECRET);
		ks.setDigest("crc32");
		keySetDef.put("authority0", ks);
		KeyRing kr;
		try {
			kr = new KeyRing(keySetDef);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		ApikeyConfigurator c = new ApikeyConfigurator();
		c.setAll("authority0");
		c.addField(new FieldConfig("s", "authority0", String.class));
		c.addField(new FieldConfig("i", "authority0", Integer.class));
		c.addField(new FieldConfig("l", "authority0", Long.class));
		c.addField(new FieldConfig("d", "authority0", Double.class));
		c.addField(new FieldConfig("b", "authority0", Boolean.class));
		c.addField(new FieldConfig("o", "authority0", Obj.class));

		apkManager = new ApikeyManager(c, kr);
	}

	@Test
	public void test() {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("s", "1");
		m.put("i", 2);
		m.put("l", 3L);
		m.put("d", 4.0D);
		m.put("b", false);
		m.put("o", SimpleJixParser.obj("{foo:'bar'}"));
		Apikey apk1 = apkManager.createFromMap("user", 0, 10000, Arr.make(), m);
		String s1 = apk1.serialize();
		Apikey apk2 = apkManager.decode(s1);
		assertEquals("1", apk2.getField("s"));
		assertEquals(2, apk2.getField("i"));
		assertEquals(3L, apk2.getField("l"));
		assertEquals(4.0D, apk2.getField("d"));
		assertEquals(false, apk2.getField("b"));
		assertEquals(SimpleJixParser.obj("{foo:'bar'}"), apk2.getField("o"));
		
		String s2 = apk2.serialize();
		assertEquals(s1,  s2);
	}
}
