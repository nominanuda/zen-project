package com.nominanuda.zen.common;

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
    public static interface Consumer<T> {
        void accept(T result);
    }



    /* objects */

    public static <K,T extends K> T cast(K o) {
        return (T) o;
    }

    public static boolean notNulls(Object... objs) {
        for (Object obj : objs) {
            if (obj ==  null) {
                return false;
            }
        }
        return true; // only if all objs are not null
    }



	/* strings */

    public static boolean isBlank(CharSequence s) {
        return s == null ? true : isBlank(s.toString());
    }
    public static boolean isBlank(String s) {
        return s == null ? true : TextUtils.isEmpty(s.trim());
    }
    public static boolean notBlank(CharSequence s) {
        return !isBlank(s);
    }

    public static boolean isEmpty(CharSequence s) {
        return TextUtils.isEmpty(s);
    }
    public static boolean notEmpty(CharSequence... seqs) {
        for (CharSequence seq : seqs) {
            if (TextUtils.isEmpty(seq)) {
                return false;
            }
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
        return i == null ? true : (i == 0);
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

    public static <T,K> Map<T,K> map() {
        return new HashMap<T,K>();
    }
    public static <T,K> Map<T,K> map(int capacity) {
        return new HashMap<T,K>(capacity);
    }
    public static <T,K> Map<T,K> map(T t, K k) {
        Map<T,K> m = map();
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



	/* stuff */

    public static <T> Iterable<T> toIterable(final Iterator<T> source) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return source;
            }
        };
    }



	/* bundles */

    public static class BB { // Bundle builder
        private Bundle mBundle = new Bundle();

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
        public BB put(Bundle b) {
            mBundle.putAll(b);
            return this;
        }

        public Bundle go() {
            return mBundle;
        }
    }

    public static boolean notEmpty(Bundle b) {
        return b != null && !b.isEmpty();
    }
}
