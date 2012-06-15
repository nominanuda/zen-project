package com.nominanuda.store.redis;

import static java.util.Arrays.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import java.util.*;

import org.junit.*;

import com.nominanuda.store.redis.*;

public class RedisTest {

	private Redis r;

	@Before
	public void setUp()
	{
		r = getObject();
	}

	protected Redis getObject()
	{
		return new Redis("localhost", 6379);
	}

	@Test
	public void ping()
	{
		assertEquals("PONG", r.ping());
		assertEquals("PONG", r.ping());
	}

	@Test
	public void set()
	{
		assertEquals("OK", r.set("a", "1"));
		assertEquals("OK", r.set("b", "{\"a\":1}"));
	}

	@Test
	public void get()
	{
		r.set("a", "0");
		assertEquals("0", r.get("a"));

		assertEquals(null, r.get("nonexisting"));
	}

	@Test
	public void mget()
	{
		r.set("a", "0");
		r.set("b", "1");
		assertEquals(asList("0", "1", null), r.mget(new String[] { "a", "b", "nonexisting" }));
	}

	@Test
	public void del()
	{
		r.set("a", "0");
		r.set("b", "0");
		assertEquals(2, r.del(new String[] { "a", "b" }).intValue());
		assertEquals(0, r.exists("a").intValue());
		assertEquals(0, r.exists("b").intValue());
	}

	@Test
	public void exists()
	{
		r.set("a", "0");
		assertEquals(1, r.exists("a").intValue());

		assertEquals(0, r.exists("nonexistent").intValue());
	}

	@Test
	public void lpush()
	{
		r.del("a");
		assertEquals(1, r.lpush("a", "0").intValue());
	}

	@Test
	public void rpush()
	{
		r.del("a");
		assertEquals(1, r.rpush("a", "0").intValue());
		assertEquals(2, r.rpush("a", "1").intValue());
		assertEquals("0", r.lpop("a"));
		assertEquals("1", r.lpop("a"));
	}

	@Test
	public void sadd()
	{
		r.del("a");
		assertEquals(1, r.sadd("a", "0").intValue());
	}

	@Test
	public void set_json()
	{
		r.del("a");
		assertEquals("OK", r.set("a", "{\"attr\":\"value\"}"));
	}

	@Test
	public void scard()
	{
		r.del("a");
		r.sadd("a", "0");
		assertEquals(1, r.scard("a").intValue());
		assertEquals(0, r.scard("nonexistent").intValue());
	}

	@Test
	public void sismember()
	{
		r.del("a");
		r.sadd("a", "0");
		assertEquals(1, r.sismember("a", "0").intValue());
		assertEquals(0, r.sismember("a", "nonexistent").intValue());
	}

	@Test
	public void srem()
	{
		r.del("a");
		r.sadd("a", "0");
		assertEquals(1, r.srem("a", "0").intValue());
		assertEquals(0, r.sismember("a", "0").intValue());
		assertEquals(0, r.srem("a", "nonexistent").intValue());
	}

	@Test
	public void smembers()
	{
		r.del("a");
		r.sadd("a", "0");
		assertEquals(asList("0"), r.smembers("a"));
		assertEquals(Collections.EMPTY_LIST, r.smembers("nonexistent"));
	}

	private static final Set<String> set(String... strings)
	{
		Set<String> result = new LinkedHashSet<String>();
		for (int i = 0; i < strings.length; i++) {
			result.add(strings[i]);
		}
		return result;
	}

	@Test
	public void sort()
	{
		r.del("a");
		r.sadd("a", "0");
		assertEquals(asList("0"), r.sort("a"));
		assertEquals(asList(), r.sort("nonexistent"));
	}

	@Test
	public void lpop()
	{
		r.del("a");
		r.lpush("a", "0");
		assertEquals("0", r.lpop("a"));
		assertEquals(null, r.lpop("nonexistent"));
	}

	@Test
	public void lrange()
	{
		r.del("a");
		r.lpush("a", "0");
		r.lpush("a", "0");
		assertEquals(asList("0", "0"), r.lrange("a", "0", "1"));
		assertEquals(asList(), r.lrange("a", "2", "3"));
	}

	@Test
	public void hset()
	{
		r.del("a");
		assertEquals(1, r.hset("a", "b", "0").intValue());
		assertEquals(0, r.hset("a", "b", "0").intValue());
	}

	@Test
	public void hget()
	{
		r.del("a");
		r.hset("a", "b", "0");
		assertEquals("0", r.hget("a", "b"));
	}

	@Test
	public void hgetall()
	{
		r.del("a");
		r.hset("a", "b", "0");
		r.hset("a", "c", "1");
		assertEquals(asList("b", "0", "c", "1"), r.hgetall("a"));
	}

	@Test
	public void hmget()
	{
		r.del("a");
		r.hset("a", "b", "0");
		r.hset("a", "c", "0");
		assertEquals(asList("0", "0"), r.hmget("a", new String[] { "b", "c" }));
	}

	@Test
	public void hdel()
	{
		r.del("a");
		r.hset("a", "b", "0");
		assertEquals(1, r.hdel("a", "b").intValue());
		assertEquals(null, r.hget("a", "b"));

		assertEquals(0, r.hdel("a", "nonexistent").intValue());
	}

	@Test
	public void hkeys()
	{
		r.del("a");
		r.hset("a", "b", "0");
		r.hset("a", "c", "0");
		assertEquals(asList("b", "c"), r.hkeys("a"));
		assertEquals(Collections.EMPTY_LIST, r.hkeys("nonexistent"));
	}

	@Test
	public void keys()
	{
		r.del("*");
		r.set("aa", "0");
		r.set("ab", "0");
		assertThat(r.keys("a*"), hasItems("aa", "ab"));
		assertEquals(asList(), r.keys("nonexistent"));
	}

	@Test
	public void append()
	{
		r.del("a");
		assertEquals(4, r.append("a", "tag,").intValue());
		assertEquals("tag,", r.get("a"));
	}

	@Test
	public void blpop()
	{
		r.del(new String[] { "a", "b" });
		r.rpush("a", "0");
		r.rpush("a", "1");
		r.rpush("a", "2");
		assertEquals(asList("a", "0"), r.blpop(new String[] { "a", "b" }, 0));
	}

	@Test
	public void brpop()
	{
		r.del(new String[] { "a", "b" });
		r.rpush("a", "0");
		r.rpush("a", "0");
		r.rpush("a", "1");
		r.rpush("a", "2");
		assertEquals(asList("a", "2"), r.brpop(new String[] { "a", "b" }, 0));
	}

	@Test
	public void brpoplpush()
	{
		r.del(new String[] { "a", "b" });
		r.rpush("a", "0");
		assertEquals("0", r.brpoplpush("a", "b", "0"));
		assertEquals("0", r.lpop("b"));
	}

	@Test
	public void incr()
	{
		r.del("a");
		r.set("a", "10");
		assertEquals(11, r.incr("a").intValue());
	}

	@Test
	public void incrby()
	{
		r.del("a");
		r.set("a", "10");
		assertEquals(11, r.incrby("a", "1").intValue());
	}

	@Test
	public void decr()
	{
		r.del("a");
		r.set("a", "10");
		assertEquals(9, r.decr("a").intValue());
	}

	@Test
	public void decrby()
	{
		r.del("a");
		r.set("a", "10");
		assertEquals(9, r.decrby("a", "1").intValue());
	}

	@Test
	public void hmset()
	{
		r.del("a");
		assertEquals("OK", r.hmset("a", "b", "1"));
		assertEquals("OK", r.hmset("a", "c", "2"));

		assertEquals("1", r.hget("a", "b"));
		assertEquals("2", r.hget("a", "c"));
	}

	@Test
	public void hvals()
	{
		r.del("a");
		r.hset("a", "b", "1");
		r.hset("a", "c", "2");

		assertEquals(asList("1", "2"), r.hvals("a"));
	}

	@Test
	public void lrem()
	{
		r.del("a");
		r.lpush("a", "0");
		assertEquals(1, r.lrem("a", "1", "0").intValue());
		assertEquals(null, r.lpop("a"));
	}

}
