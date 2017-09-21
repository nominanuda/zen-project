package com.nominanuda.zen.common;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by azum on 03/03/17.
 */

public class Util {

    /* lang */

	public interface Function<T, R> {
		R apply(T t);
	}

	public interface Consumer<T> {
		void accept(T result);
	}

	public interface Supplier<T> {
		T get();
	}

	public static <T> T get(Supplier<T> s) {
		return s.get();
	}

	public static class AsyncLambdaTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
		private final Function<Params[], Result> mDoInBackgroundFnc;
		private final Consumer<Progress[]> mOnProgressUpdateFnc;
		private final Consumer<Result> mOnPostExecuteFnc;

		public AsyncLambdaTask(Function<Params[], Result> doInBackgroundFnc,
							   Consumer<Progress[]> onProgressUpdateFnc,
							   Consumer<Result> onPostExecuteFnc,
							   Params... params) {
			mDoInBackgroundFnc = doInBackgroundFnc;
			mOnProgressUpdateFnc = onProgressUpdateFnc;
			mOnPostExecuteFnc = onPostExecuteFnc;
			execute(params);
		}

		public AsyncLambdaTask(Function<Params[], Result> doInBackgroundFnc,
							   Consumer<Result> onPostExecuteFnc,
							   Params... params) {
			this(doInBackgroundFnc, values -> {}, onPostExecuteFnc, params);
		}

		@Override
		final protected Result doInBackground(Params... params) {
			return mDoInBackgroundFnc.apply(params);
		}

		@Override
		final protected void onProgressUpdate(Progress... values) {
			mOnProgressUpdateFnc.accept(values);
		}

		@Override
		final protected void onPostExecute(Result result) {
			mOnPostExecuteFnc.accept(result);
		}
	}



    /* objects */

	public static <K, T extends K> T cast(K o) {
		return (T) o;
	}

	public static boolean notNulls(Object... objs) {
		for (Object obj : objs) {
			if (obj == null) {
				return false;
			}
		}
		return true; // only if all objs are not null
	}



	/* strings */

	public static boolean isBlank(CharSequence s) {
		return s == null || isBlank(s.toString());
	}

	public static boolean isBlank(String s) {
		return s == null || TextUtils.isEmpty(s.trim());
	}

	public static boolean notBlank(CharSequence... seqs) {
		if (seqs != null && seqs.length > 0) {
			for (CharSequence seq : seqs) {
				if (isBlank(seq)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public static boolean isEmpty(CharSequence s) {
		return TextUtils.isEmpty(s);
	}

	public static boolean notEmpty(CharSequence... seqs) {
		if (seqs != null && seqs.length > 0) {
			for (CharSequence seq : seqs) {
				if (TextUtils.isEmpty(seq)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	public static boolean notEmpty(CharSequence s, int min) {
		return notEmpty(s) && (s.length() >= min);
	}

	public static String notEmptyElse(String... strs) {
		for (String str : strs) {
			if (notEmpty(str)) {
				return str;
			}
		}
		return null;
	}

	public static String trimOrNull(String s) {
		if (s != null) {
			s = s.trim();
			if (s.length() == 0) {
				return null;
			}
		}
		return s;
	}



	/* integers */

	public static boolean isEmpty(Integer i) {
		return i == null || i == 0;
	}

	public static boolean notEmpty(Integer i) {
		return !isEmpty(i);
	}

	public static Integer notEmptyElse(Integer... ints) {
		for (Integer i : ints) {
			if (notEmpty(i)) {
				return i;
			}
		}
		return null;
	}

	public static int toInt(String value, int defValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defValue;
		}
	}



	/* longs */

	public static long toLong(String value, long defValue) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return defValue;
		}
	}



    /* arrays*/

	public static boolean isEmpty(Object[] o) {
		return o == null || o.length == 0;
	}

	public static boolean notEmpty(Object[] o) {
		return !isEmpty(o);
	}



    /* collections */

	public static boolean notEmpty(Collection<?> c) {
		return c != null && !c.isEmpty();
	}



	/* sets */

	public static <T> Set<T> set() {
		return new HashSet<T>();
	}

	public static <T> Set<T> set(T... elms) {
		return new HashSet<T>(Arrays.asList(elms));
	}



	/* lists */

	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	public static <T> List<T> list(T... elms) {
		return new ArrayList<T>(Arrays.asList(elms));
	}



	/* maps */

	public static <T, K> Map<T, K> map() {
		return new HashMap<T, K>();
	}

	public static <T, K> Map<T, K> map(int capacity) {
		return new HashMap<T, K>(capacity);
	}

	public static <T, K> Map<T, K> map(T t, K k) {
		Map<T, K> m = map();
		m.put(t, k);
		return m;
	}

	public static boolean notEmpty(Map<?, ?> map) {
		return map != null && !map.isEmpty();
	}



	/* enums */

	public static <T extends Enum<T>> T enumSwitch(String switchValue, T defaultValue) {
		try {
			return Enum.valueOf(defaultValue.getDeclaringClass(), switchValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}



	/* iterations */

	public static <T> Iterable<T> toIterable(final Iterator<T> source) {
		return () -> source;
	}

	public interface ForEachConsumer<T> {
		void item(T item, int index);
	}

	public static <T> Iterable<T> forEach(final Iterable<T> iterable, ForEachConsumer<T> consumer) {
		int i = 0;
		for (T item : iterable) {
			consumer.item(item, i);
			i++;
		}
		return iterable;
	}

	public static <T> Iterable<T> forEach(final Iterator<T> source, ForEachConsumer<T> consumer) {
		return forEach(toIterable(source), consumer);
	}

	public static <T> T[] forEach(T[] items, ForEachConsumer<T> consumer) {
		int l = items.length;
		for (int i = 0; i < l; i++) {
			consumer.item(items[i], i);
		}
		return items;
	}



	/* bundles */

	public static class BB { // Bundle builder
		private Bundle mBundle = new Bundle();

		protected BB() {
			// just for subclasses
		}

		public BB(String key, boolean b) {
			put(key, b);
		}

		public BB(String key, int i) {
			put(key, i);
		}

		public BB(String key, double d) {
			put(key, d);
		}

		public BB(String key, float f) {
			put(key, f);
		}

		public BB(String key, String s) {
			put(key, s);
		}

		public BB(String key, Integer... i) {
			put(key, i);
		}

		public BB(String key, String... s) {
			put(key, s);
		}

		public BB(String key, Serializable s) {
			put(key, s);
		}

		public BB put(String key, boolean b) {
			mBundle.putBoolean(key, b);
			return this;
		}

		public BB put(String key, int i) {
			mBundle.putInt(key, i);
			return this;
		}

		public BB put(String key, double d) {
			mBundle.putDouble(key, d);
			return this;
		}

		public BB put(String key, float f) {
			mBundle.putFloat(key, f);
			return this;
		}

		public BB put(String key, String s) {
			mBundle.putString(key, s);
			return this;
		}

		public BB put(String key, Serializable s) {
			mBundle.putSerializable(key, s);
			return this;
		}

		public BB put(String key, Bundle b) {
			mBundle.putBundle(key, b);
			return this;
		}

		public BB put(String key, Integer... i) {
			mBundle.putIntegerArrayList(key, new ArrayList<>(Arrays.asList(i)));
			return this;
		}

		public BB put(String key, String... s) {
			mBundle.putStringArrayList(key, new ArrayList<>(Arrays.asList(s)));
			return this;
		}

		public BB put(Bundle b) {
			mBundle.putAll(b);
			return this;
		}

		/**
		 * After this call the BB can still be used
		 *
		 * @return the currently cumulated Bundle
		 */
		public Bundle build() {
			return mBundle;
		}

		/**
		 * After this call the BB cannot be used anymore
		 *
		 * @return the resulting Bundle
		 */
		public Bundle go() {
			Bundle b = mBundle;
			mBundle = null;
			return b;
		}
	}

	public static boolean notEmpty(Bundle b) {
		return b != null && !b.isEmpty();
	}
}
