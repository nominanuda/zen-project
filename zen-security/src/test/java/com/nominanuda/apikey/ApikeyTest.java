package com.nominanuda.apikey;

import static com.nominanuda.apikey.FieldConfig.PUBLIC;
import static com.nominanuda.zen.obj.wrap.Wrap.WF;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.SimpleJixParser;

public class ApikeyTest {
	HashSet<String> redis = new HashSet<String>();
	ApikeyManager beFac;
	ApikeyManager feFac;
	ApikeyManager cliFac;

	@Before
	public void init() throws RuntimeException, Exception {
		Obj secretKeySimm = json("{cipher:'aes',key:'aaasecret1aaa',digest:'crc32'}");
		Obj secret1KeySetRead = json("{cipher:'aes',key:'aaasupersecretaaa',digest:'secp128r1',verify:'MDYwEAYHKoZIzj0CAQYFK4EEABwDIgAElZVTGUk1GDaZ9rU8OUBP3mds7x1bNTysQn8Zp4N8fANc'}");
		Obj secret1KeySetReadWrite = secret1KeySetRead.copy().with("sign", "MF0CAQAwEAYHKoZIzj0CAQYFK4EEABwERjBEAgEBBBB0pf1I3FtesuNflHOQcRbUoAcGBSuBBAAcoSQDIgAElZVTGUk1GDaZ9rU8OUBP3mds7x1bNTysQn8Zp4N8fANc");
		Obj publicKeySetReadWrite = json("{cipher:'xor',digest:'crc32'}");

		redis = new HashSet<String>();

		ApikeyConfigurator c = new ApikeyConfigurator();
		c.setFingerprint(PUBLIC);
		c.setUser(PUBLIC);
		c.setTime(PUBLIC);
		c.setRoles("secret1");
		c.addField(new FieldConfig("test", "secret0", String.class, false));
		c.addField(new FieldConfig("cliAndStuff", "secret1", TestUserData.class, false));

		beFac = new ApikeyManager(c, new KeyRing(Obj.make()
				.with(PUBLIC, publicKeySetReadWrite)
				.with("secret1", secret1KeySetReadWrite)
			));
		feFac = new ApikeyManager(c, new KeyRing(Obj.make()
				.with(PUBLIC, publicKeySetReadWrite)
				.with("secret0", secretKeySimm)
				.with("secret1", secret1KeySetRead)
			));
		cliFac = new ApikeyManager(c, new KeyRing(Obj.make()
				.with(PUBLIC, publicKeySetReadWrite)
				.with("secret0", secretKeySimm)
			));
	}

	private Obj json(String json) {
		return SimpleJixParser.obj(json);
	}
	
	@Test
	public void test() {
		long tm = System.currentTimeMillis();
//		for(int i = 0; i < 100; i++) {
			String apikey = beLogin();
			System.err.println(apikey);
			String refreshedApikey = feInvokedByCli(apikey);
			String clientRefreshedKey = clientChecksRoleAndRefreshes(refreshedApikey);
			beChangesPrivateInfo(clientRefreshedKey);
//		}
		System.err.println(System.currentTimeMillis() -tm);
	}
	
	private String beChangesPrivateInfo(String clientRefreshedKey) {
		Apikey beApk = beFac.decode(clientRefreshedKey);
		TestUserData cliAndStuff = (TestUserData)beApk.getField("cliAndStuff");
		assertEquals("_auth", cliAndStuff.auth());
		cliAndStuff.auth("AUTH");
		beApk.setField("cliAndStuff", cliAndStuff);
		assertTrue(redis.contains(beApk.getFingerprint()));
		return beApk.serialize();
	}
	
	private String clientChecksRoleAndRefreshes(String refreshedApikey) {
		Apikey cliApk = cliFac.decode(refreshedApikey);
//		assertTrue(cliApk.isUserInRole("ROLE_EDITOR"));
		assertTrue(cliApk.getField("test").equals("test_value_by_fe"));
		cliApk.refresh();
		String clientRefreshedKey = cliApk.serialize();
		return clientRefreshedKey;
	}
	
	private String feInvokedByCli(String apikey) {
		Apikey feApk = feFac.decode(apikey);
		assertTrue(feApk.notExpired());
		try {
			Object segm = feApk.getField("cliAndStuff");
			feApk.setField("cliAndStuff", segm);
			feApk.serialize();
			fail();
		} catch(SecurityException e) {}
		feApk = feFac.decode(apikey);
		assertTrue(redis.contains(feApk.getFingerprint()));
		assertTrue(feApk.isUserInRole("ROLE_EDITOR"));
		feApk.setField("test", "test_value_by_fe");
		feApk.refresh();
		String apikey2 = feApk.serialize();
		return apikey2;
	}
	private String beLogin() {
		TestUserData data = WF.wrap(TestUserData.class)
			.appId("_appId")
			.auth("_auth")
			.cli("_cli")
			.deviceId("_deviceId")
			.pin("_pin")
			.sample(false)
			.subscription("_subscription");

		beFac.serializeNowNoRoles("pippo", TimeUnit.DAYS.toMillis(1));
		assertNull(beFac.decode(beFac.serializeNowNoRoles("pippo", TimeUnit.DAYS.toMillis(1))).getField("cliAndStuff"));
		Apikey apk = beFac.createFromNamesAndValues("pippo", System.currentTimeMillis(), TimeUnit.DAYS.toMillis(1), 
			Arr.make("ROLE_EDITOR"), 
			"cliAndStuff", data);
		String apikey = apk.serialize();
		redis.add(apk.getFingerprint());
		return apikey;
	}
}
