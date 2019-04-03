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
package com.nominanuda.apikey;

import static com.nominanuda.zen.codec.Base62.B62;
import static com.nominanuda.zen.seq.Seq.SEQ;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.nominanuda.zen.common.Check;
import com.nominanuda.zen.obj.Arr;
import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.obj.wrap.Wrap;


public class ApikeyManager {
	private final static String DEFAULT_AUTHORITY = "authority";
	
	private final KeyRing keyRing;
	private final ApikeyConfigurator conf;
	private final SecureRandom secureRandom = new SecureRandom();
	private long nowStartToleranceMs = 10000; // anticipate "now" creation by this amount, to prevent "future-starting" apikeys (if servers are not perfectly time-aligned)
	private int fingerprintSizeBytes = 8;

	public ApikeyManager(ApikeyConfigurator conf, KeyRing keyRing) {
		this.conf = conf;
		this.keyRing = keyRing;
	}
	
	
	/* single keyset manager with optional fields, for simple cases */
	public ApikeyManager(KeySet keySet, Map<String, Class<?>> fields) throws Exception {
		this(new ApikeyConfigurator(DEFAULT_AUTHORITY, fields), new KeyRing(SEQ.buildMap(HashMap.class, DEFAULT_AUTHORITY, keySet)));
	}
	@SuppressWarnings("serial")
	public ApikeyManager(KeySet keySet, final String jsonField) throws Exception {
		this(keySet, new HashMap<String, Class<?>>() {{
			put(jsonField, Obj.class);
		}});
	}
	public ApikeyManager(KeySet keySet) throws Exception {
		this(keySet, new HashMap<String, Class<?>>());
	}
	

	/* create */
	
	final protected Apikey createFromMapWithFingerprint(@Nullable String user, long start, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap, String fingerprint) {
		FieldMap fm = conf.getFieldMap(keyRing);
		Field[] apiOrderedFields = fm.createBlankApiFields();
		update(apiOrderedFields, user, start, duration, roles, nameAndValMap, fingerprint);
		Segment[] _segments = fm.apiOrderedFieldsToSegments(apiOrderedFields, keyRing);
		return new Apikey(new Segments(_segments, fm));
	}
	// override this one to change fingerprint strategy
	protected Apikey createFromMapInternal(@Nullable String user, long start, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap) {
		return createFromMapWithFingerprint(user, start, duration, roles, nameAndValMap, randomFingerprint());
	}
	
	public Apikey createFromMap(@Nullable String user, long start, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap) {
		return createFromMapInternal(user, start, duration, roles, nameAndValMap);
	}
	public Apikey createFromNamesAndValues(@Nullable String user, long start, long duration, @Nullable Arr roles, Object... nameAndObjs) {
		return createFromMap(user, start, duration, roles, toMap(nameAndObjs));
	}
	public Apikey createFromValues(@Nullable String user, long start, long duration, @Nullable Arr roles, Object... objs) {
		return createFromMap(user, start, duration, roles, toMap(conf.getCustomFields(), objs));
	}
	public Apikey createFromObj(@Nullable String user, long start, long duration, @Nullable Arr roles, Obj nameAndValMap) {
		return createFromMap(user, start, duration, roles, nameAndValMap);
	}
	public Apikey createNoRoles(@Nullable String user, long start, long duration) {
		return createFromValues(user, start, duration, Arr.make());
	}
	
	// with set for roles (protected to be hidden in js ctrls)
	protected Apikey createFromMap(@Nullable String user, long start, long duration, @Nullable List<String> roles, Map<String, Object> nameAndValMap) {
		return createFromMap(user, start, duration, toArr(roles), nameAndValMap);
	}
	protected Apikey createFromNamesAndValues(@Nullable String user, long start, long duration, @Nullable List<String> roles, Object... nameAndObjs) {
		return createFromNamesAndValues(user, start, duration, toArr(roles), nameAndObjs);
	}
	protected Apikey createFromValues(@Nullable String user, long start, long duration, @Nullable List<String> roles, Object... objs) {
		return createFromValues(user, start, duration, toArr(roles), objs);
	}
	protected Apikey createFromObj(@Nullable String user, long start, long duration, @Nullable List<String> roles, Obj nameAndValMap) {
		return createFromObj(user, start, duration, toArr(roles), nameAndValMap);
	}
	
	
	/* create now */
	
	public Apikey createNowFromMap(@Nullable String user, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap) {
		return createFromMap(user, nowStartMs(), duration, roles, nameAndValMap);
	}
	public Apikey createNowFromNamesAndValues(@Nullable String user, long duration, @Nullable Arr roles, Object... nameAndObjs) {
		return createFromNamesAndValues(user, nowStartMs(), duration, roles, nameAndObjs);
	}
	public Apikey createNowFromValues(@Nullable String user, long duration, @Nullable Arr roles, Object... objs) {
		return createFromValues(user, nowStartMs(), duration, roles, objs);
	}
	public Apikey createNowFromObj(@Nullable String user, long duration, @Nullable Arr roles, Obj nameAndValMap) {
		return createFromObj(user, nowStartMs(), duration, roles, nameAndValMap);
	}
	public Apikey createNowNoRoles(@Nullable String user, long duration) {
		return createNoRoles(user, nowStartMs(), duration);
	}
	
	private long nowStartMs() {
		return System.currentTimeMillis() - nowStartToleranceMs;
	}
	
	// with set for roles (protected to be hidden in js ctrls)
	protected Apikey createNowFromMap(@Nullable String user, long duration, @Nullable List<String> roles, Map<String, Object> nameAndValMap) {
		return createNowFromMap(user, duration, toArr(roles), nameAndValMap);
	}
	protected Apikey createNowFromNamesAndValues(@Nullable String user, long duration, @Nullable List<String> roles, Object... nameAndObjs) {
		return createNowFromNamesAndValues(user, duration, toArr(roles), nameAndObjs);
	}
	protected Apikey createNowFromValues(@Nullable String user, long duration, @Nullable List<String> roles, Object... objs) {
		return createNowFromValues(user, duration, toArr(roles), objs);
	}
	protected Apikey createNowFromObj(@Nullable String user, long duration, @Nullable List<String> roles, Obj nameAndValMap) {
		return createNowFromObj(user, duration, toArr(roles), nameAndValMap);
	}
	
	
	/* serialize */
	
	public String serializeFromMap(@Nullable String user, long start, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap) {
		return createFromMap(user, start, duration, roles, nameAndValMap).serialize();
	}
	public String serializeFromNamesAndValues(@Nullable String user, long start, long duration, @Nullable Arr roles, Object... nameAndObjs) {
		return createFromNamesAndValues(user, start, duration, roles, nameAndObjs).serialize();
	}
	public String serializeFromValues(@Nullable String user, long start, long duration, @Nullable Arr roles, Object... objs) {
		return createFromValues(user, start, duration, roles, objs).serialize();
	}
	public String serializeFromObj(@Nullable String user, long start, long duration, @Nullable Arr roles, Obj nameAndValMap) {
		return createFromObj(user, start, duration, roles, nameAndValMap).serialize();
	}
	public String serializeNoRoles(@Nullable String user, long start, long duration) {
		return createNoRoles(user, start, duration).serialize();
	}
	
	// with set for roles (protected to be hidden in js ctrls)
	protected String serializeFromMap(@Nullable String user, long start, long duration, @Nullable List<String> roles, Map<String, Object> nameAndValMap) {
		return serializeFromMap(user, start, duration, toArr(roles), nameAndValMap);
	}
	protected String serializeFromNamesAndValues(@Nullable String user, long start, long duration, @Nullable List<String> roles, Object... nameAndObjs) {
		return serializeFromNamesAndValues(user, start, duration, toArr(roles), nameAndObjs);
	}
	protected String serializeFromValues(@Nullable String user, long start, long duration, @Nullable List<String> roles, Object... objs) {
		return serializeFromValues(user, start, duration, toArr(roles), objs);
	}
	protected String serializeFromObj(@Nullable String user, long start, long duration, @Nullable List<String> roles, Obj nameAndValMap) {
		return serializeFromObj(user, start, duration, toArr(roles), nameAndValMap);
	}
	
	
	/* serialize now */
	
	public String serializeNowFromMap(@Nullable String user, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap) {
		return createNowFromMap(user, duration, roles, nameAndValMap).serialize();
	}
	public String serializeNowFromNamesAndValues(@Nullable String user, long duration, @Nullable Arr roles, Object... nameAndObjs) {
		return createNowFromNamesAndValues(user, duration, roles, nameAndObjs).serialize();
	}
	public String serializeNowFromValues(@Nullable String user, long duration, @Nullable Arr roles, Object... objs) {
		return createNowFromValues(user, duration, roles, objs).serialize();
	}
	public String serializeNowFromObj(@Nullable String user, long duration, @Nullable Arr roles, Obj nameAndValMap) {
		return createNowFromObj(user, duration, roles, nameAndValMap).serialize();
	}
	public String serializeNowNoRoles(@Nullable String user, long duration) {
		return createNowNoRoles(user, duration).serialize();
	}
	
	// with set for roles (protected to be hidden in js ctrls)
	protected String serializeNowFromMap(@Nullable String user, long duration, @Nullable List<String> roles, Map<String, Object> nameAndValMap) {
		return serializeNowFromMap(user, duration, toArr(roles), nameAndValMap);
	}
	protected String serializeNowFromNamesAndValues(@Nullable String user, long duration, @Nullable List<String> roles, Object... nameAndObjs) {
		return serializeNowFromNamesAndValues(user, duration, toArr(roles), nameAndObjs);
	}
	protected String serializeNowFromValues(@Nullable String user, long duration, @Nullable List<String> roles, Object... objs) {
		return serializeNowFromValues(user, duration, toArr(roles), objs);
	}
	protected String serializeNowFromObj(@Nullable String user, long duration, @Nullable List<String> roles, Obj nameAndValMap) {
		return serializeNowFromObj(user, duration, toArr(roles), nameAndValMap);
	}
	
	
	
	/* decode */

	public Apikey decode(String apikey) {
		FieldMap fm = conf.getFieldMap(keyRing);
		Field[] apiOrderedFields = fm.createBlankApiFields();
		List<byte[]> physicalSegments = Segments.splitApikeyToSegments(apikey);
		Segment[] _segments = fm.apiOrderedFieldsToSegments(apiOrderedFields, keyRing, physicalSegments);
		return new Apikey(new Segments(_segments, fm));
	}
	
	
	private void update(Field[] fields, @Nullable String user, long start, long duration, @Nullable Arr roles, Map<String, Object> nameAndValMap, String fingerprint) {
		Iterator<StandardField> itr = Arrays.asList(StandardField.values()).iterator();
		StandardField sf = itr.next();
		Check.illegalstate.assertEquals(StandardField.fingerprint, sf);
		fields[sf.ordinal()].update(fingerprint);
		sf = itr.next();
		Check.illegalstate.assertEquals(StandardField.user, sf);
		fields[sf.ordinal()].update(user);
		sf = itr.next();
		Check.illegalstate.assertEquals(StandardField.time, sf);
		Time t  = Wrap.WF.wrap(Obj.make("start", start, "duration", duration), Time.class);
		fields[sf.ordinal()].update(t);
		sf = itr.next();
		Check.illegalstate.assertEquals(StandardField.roles, sf);
		fields[sf.ordinal()].update(roles);
		
		List<FieldConfig> custom = conf.getCustomFields();
		int nCustomFields = custom.size();
		if (nCustomFields > 0) {
			int i = StandardField.values().length;
			for (FieldConfig fc : custom) {
				fields[i].update(nameAndValMap.get(fc.name));
				i++;
			}
		}
	}

	private Map<String, Object> toMap(@Nullable Object[] nameAndObjs) {
		Map<String, Object> m = new HashMap<String, Object>();
		if (nameAndObjs != null) {
			for(int i = 0; i < nameAndObjs.length / 2; i++) {
				m.put((String)nameAndObjs[2*i], nameAndObjs[2*i + 1]);
			}
		}
		return m;
	}

	private Map<String, Object> toMap(List<FieldConfig> ldc, @Nullable Object[] objs) {
		Map<String, Object> m = new HashMap<String, Object>();
		if (objs != null) {
			for(int i = 0; i < objs.length; i++) {
				m.put(ldc.get(i).name, objs[i]);
			}
		}
		return m;
	}
	
	private Arr toArr(List<String> roles) {
		return (roles != null ? Arr.fromList(roles) : null);
	}

	private String randomFingerprint() {
		byte[] bytes = new byte[fingerprintSizeBytes];
		secureRandom.nextBytes(bytes);
		return B62.encode(bytes);
	}
	

	public void setFingerprintSizeBytes(int fingerprintSizeBytes) {
		this.fingerprintSizeBytes = fingerprintSizeBytes;
	}
	
	public void setNowStartToleranceMs(long nowStartToleranceMs) {
		this.nowStartToleranceMs = nowStartToleranceMs;
	}
	
	
	/* utils */
	
	public @Nullable Apikey getApikeyOrNull(@Nullable String apikey) {
		if (apikey != null) {
			try {
				return decode(apikey);
			} catch (Exception e) { // any exception, also parsing ones
				// nothing to do
			}
		}
		return null;
	}
	
	public @Nullable Apikey isValidApikeyOrNull(@Nullable Apikey apk) {
		return (apk != null && apk.notExpired() ? apk : null);
	}
	public @Nullable Apikey getValidApikeyOrNull(@Nullable String apikey) {
		return isValidApikeyOrNull(getApikeyOrNull(apikey));
	}
	
	public boolean isValidApikey(@Nullable String apikey) {
		return (getValidApikeyOrNull(apikey) != null);
	}
	
	public @Nullable String getUserOrNull(@Nullable String apikey) {
		try {
			Apikey apk = getValidApikeyOrNull(apikey);
			if (apk != null) {
				return apk.getUser();
			}
		} catch (Exception e) { // any exception, also parsing ones
			// nothing to do
		}
		return null;
	}
	
	public boolean isUserInRole(@Nullable String apikey, @Nullable String role) {
		try {
			Apikey apk = getValidApikeyOrNull(apikey);
			if (apk != null) {
				return apk.isUserInRole(role);
			}
		} catch (Exception e) { // any exception, also parsing ones
			// nothing to do
		}
		return false;
	}
	
	
//	public void assertApikeyIsValid(String apikey, IApiError msg) throws Http400Exception {
//		HttpAppException.badParamExAssertTrue(isValidApikey(apikey), msg);
//	}
//	public void assertApikeyIsValid(String apikey) throws Http400Exception {
//		assertApikeyIsValid(apikey, null);
//	}
//	
//	public void assertApikeyBelongsToUser(String apikey, String username, IApiError msg) throws Http401Exception {
//		String user = getUserOrNull(apikey);
//		HttpAppException.authExAssertTrue(user != null && user.equals(username), msg);
//	}
//	public void assertApikeyBelongsToUser(String apikey, String username) throws Http401Exception {
//		assertApikeyBelongsToUser(apikey, username, null);
//	}
//	
//	public void assertUserInRole(String apikey, @Nullable String role, IApiError msg) throws Http401Exception {
//		HttpAppException.authExAssertTrue(isUserInRole(apikey, role), msg);
//	}
//	public void assertUserInRole(String apikey, @Nullable String role) throws Http401Exception {
//		assertUserInRole(apikey, role, null);
//	}
}
