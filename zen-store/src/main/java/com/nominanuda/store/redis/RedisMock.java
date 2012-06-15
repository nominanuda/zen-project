package com.nominanuda.store.redis;

import static java.util.Arrays.*;

import java.util.*;

public class RedisMock extends Redis {

	public RedisMock() {
		super("localhost", 6379);
	}

	private Map<String, Object> keys = new HashMap<String, Object>();

	@Override
	public Integer append(String key, String value)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, "");
		}
		String currentValue = (String) keys.get(key);
		String newValue = currentValue.concat(value);
		keys.put(key, newValue);
		return newValue.length();
	}

	@Override
	public String auth(String password)
	{
		return null;
	}

	@Override
	public String bgrewriteaof()
	{
		return null;
	}

	@Override
	public String bgsave()
	{
		return null;
	}

	@Override
	public List<String> blpop(String key, int timeout)
	{
		return asList(key, lpop(key));
	}

	@Override
	public List<String> blpop(String[] keys, int timeout)
	{
		for (String key : keys) {
			String value = lpop(key);
			if (null != value) return asList(key, value);
		}
		return null;
	}

	@Override
	public List<String> brpop(String key, int timeout)
	{
		return asList(key, rpop(key));
	}

	@Override
	public List<String> brpop(String[] keys, int timeout)
	{
		for (String key : keys) {
			String value = rpop(key);
			if (null != value) return asList(key, value);
		}
		return null;
	}

	@Override
	public String brpoplpush(String source, String destination, String timeout)
	{
		return rpoplpush(source, destination);
	}

	@Override
	public Integer dbsize()
	{
		return null;
	}

	@Override
	public Integer decr(String key)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, "0");
		}
		String value = (String) keys.get(key);
		int newValue = Integer.valueOf(value);
		newValue--;
		keys.put(key, newValue);
		return newValue;
	}

	@Override
	public Integer decrby(String key, String decrement)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, "0");
		}
		String value = (String) keys.get(key);
		int newValue = Integer.valueOf(value);
		newValue -= Integer.valueOf(decrement);
		keys.put(key, newValue);
		return newValue;
	}

	@Override
	public Integer del(String key)
	{
		if (!keys.containsKey(key)) return 0;
		keys.remove(key);
		return 1;
	}

	@Override
	public Integer del(String[] keys)
	{
		int result = 0;
		for (String key : keys) {
			result += del(key);
		}
		return result;
	}

	@Override
	public String discard()
	{
		return null;
	}

	@Override
	public String echo(String message)
	{
		return null;
	}

	@Override
	public List<String> exec()
	{
		return null;
	}

	@Override
	public Integer exists(String key)
	{
		return keys.containsKey(key) ? 1 : 0;
	}

	@Override
	public Integer expire(String key, String seconds)
	{
		return null;
	}

	@Override
	public Integer expireat(String key, String timestamp)
	{
		return null;
	}

	@Override
	public String flushall()
	{
		return null;
	}

	@Override
	public String flushdb()
	{
		return null;
	}

	@Override
	public String get(String key)
	{
		if (keys.containsKey(key)) return (String) keys.get(key);
		return null;
	}

	@Override
	public Integer getbit(String key, String offset)
	{
		return null;
	}

	@Override
	public String getrange(String key, String start, String end)
	{
		return null;
	}

	@Override
	public String getset(String key, String value)
	{
		return null;
	}

	@Override
	public Integer hdel(String key, String field)
	{
		int result = 0;
		if (!keys.containsKey(key)) return result;
		Map map = (Map) keys.get(key);
		result = map.containsKey(field) ? 1 : 0;
		map.remove(field);
		return result;
	}

	@Override
	public Integer hdel(String key, String[] fields)
	{
		int result = 0;
		if (!keys.containsKey(key)) return result;
		for (String field : fields) {
			result += hdel(key, field);
		}
		return result;
	}

	@Override
	public Integer hexists(String key, String field)
	{
		return null;
	}

	@Override
	public String hget(String key, String field)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new HashMap<String, String>());
		}
		Map map = (Map) keys.get(key);
		if (!map.containsKey(field)) return null;
		return (String) map.get(field);
	}

	@Override
	public List<String> hgetall(String key)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) return result;
		Map map = (Map) keys.get(key);
		for (String field : (Set<String>) map.keySet()) {
			result.add(field);
			result.add((String) map.get(field));
		}
		return result;
	}

	@Override
	public Integer hincrby(String key, String field, String increment)
	{
		return null;
	}

	@Override
	public String hincrbyfloat(String key, String field, String increment)
	{
		return null;
	}

	@Override
	public List<String> hkeys(String key)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) return result;
		Map map = (Map) keys.get(key);
		result.addAll(map.keySet());
		return result;
	}

	@Override
	public Integer hlen(String key)
	{
		return null;
	}

	@Override
	public List<String> hmget(String key, String field)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) return result;
		Map map = (Map) keys.get(key);
		if (map.containsKey(field)) {
			result.add((String) map.get(field));
		}
		return result;
	}

	@Override
	public List<String> hmget(String key, String[] fields)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) return result;
		Map map = (Map) keys.get(key);
		for (String field : fields) {
			if (map.containsKey(field)) {
				result.add((String) map.get(field));
			}
		}
		return result;
	}

	@Override
	public String hmset(String key, String field, String value)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new HashMap<String, String>());
		}
		Map map = (Map) keys.get(key);
		map.put(field, value);
		return "OK";
	}

	@Override
	public String hmset(String key, String[] fieldValues)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new HashMap<String, String>());
		}
		Map map = (Map) keys.get(key);
		for (int i = 0; i < fieldValues.length; i += 2) {
			map.put(fieldValues[i], fieldValues[i + 1]);
		}
		return "OK";
	}

	@Override
	public Integer hset(String key, String field, String value)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new HashMap<String, String>());
		}
		Map map = (Map) keys.get(key);
		int result = !map.containsKey(field) ? 1 : 0;
		map.put(field, value);
		return result;
	}

	@Override
	public Integer hsetnx(String key, String field, String value)
	{
		return null;
	}

	@Override
	public List<String> hvals(String key)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) {
			return result;
		}
		Map map = (Map) keys.get(key);
		result.addAll(map.values());
		return result;
	}

	@Override
	public Integer incr(String key)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, "0");
		}
		String value = (String) keys.get(key);
		int newValue = Integer.valueOf(value);
		newValue++;
		keys.put(key, newValue);
		return newValue;
	}

	@Override
	public Integer incrby(String key, String increment)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, "0");
		}
		String value = (String) keys.get(key);
		int newValue = Integer.valueOf(value);
		newValue += Integer.valueOf(increment);
		keys.put(key, newValue);
		return newValue;
	}

	@Override
	public String incrbyfloat(String key, String increment)
	{
		return null;
	}

	@Override
	public String info()
	{
		return null;
	}

	@Override
	public List<String> keys(String pattern)
	{
		List<String> result = new LinkedList<String>();
		for (String key : keys.keySet()) {
			String regex = pattern.replaceAll("\\*", ".*");
			if (key.matches(regex)) result.add(key);
		}
		return result;
	}

	@Override
	public Integer lastsave()
	{
		return null;
	}

	@Override
	public List<String> lindex(String key, String index)
	{
		return null;
	}

	@Override
	public Integer llen(String key)
	{
		return null;
	}

	@Override
	public String lpop(String key)
	{
		if (!keys.containsKey(key)) return null;
		List<String> list = (List<String>) keys.get(key);
		if (list.isEmpty()) return null;
		return list.remove(0);
	}

	@Override
	public Integer lpush(String key, String value)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new LinkedList<String>());
		}
		List<String> list = (List<String>) keys.get(key);
		list.add(0, value);
		return list.size();
	}

	@Override
	public Integer lpush(String key, String[] values)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new LinkedList<String>());
		}
		List<String> list = (List<String>) keys.get(key);
		for (String value : values) {
			list.add(0, value);
		}
		return list.size();
	}

	@Override
	public Integer lpushx(String key, String value)
	{
		if (keys.containsKey(key)) return 0;
		List<String> list = (List<String>) keys.get(key);
		list.add(0, value);
		return list.size();
	}

	@Override
	public List<String> lrange(String key, String start, String stop)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) return result;
		List list = (List) keys.get(key);
		int b = Integer.valueOf(start).intValue();
		int e = Integer.valueOf(stop).intValue();
		if (e >= list.size()) e = list.size() - 1;
		for (int i = b; i <= e; i++) {
			result.add((String) list.get(i));
		}
		return result;
	}

	@Override
	public Integer lrem(String key, String count, String value)
	{
		if (!keys.containsKey(key)) return 0;
		int c = Integer.valueOf(count);
		List<String> list = (List) keys.get(key);
		if (c < 0) {
			List<String> toRemove = new LinkedList<String>();
			for (int i = list.size() - 1; c > 0 && i >= 0; i--, c--) {
				String aValue = list.get(0);
				if (value.equals(aValue)) toRemove.add(aValue);
			}
			list.removeAll(toRemove);
			return toRemove.size();
		} else if (c == 0) {
			List<String> toRemove = new LinkedList<String>();
			for (int i = 0; i < list.size(); i++) {
				String aValue = list.get(0);
				if (value.equals(aValue)) toRemove.add(aValue);
			}
			list.removeAll(toRemove);
			return toRemove.size();
		} else if (c > 0) {
			List<String> toRemove = new LinkedList<String>();
			for (int i = 0; c > 0 && i < list.size(); i++, c--) {
				String aValue = list.get(0);
				if (value.equals(aValue)) toRemove.add(aValue);
			}
			list.removeAll(toRemove);
			return toRemove.size();
		}
		return null;
	}

	@Override
	public String lset(String key, String index, String value)
	{
		return null;
	}

	@Override
	public String ltrim(String key, String start, String stop)
	{
		return null;
	}

	@Override
	public List<String> mget(String[] keys)
	{
		List<String> result = new LinkedList<String>();
		for (String key : keys) {
			result.add(get(key));
		}
		return result;
	}

	@Override
	public String migrate(String host, String port, String key, String destinationdb, String timeout)
	{
		return null;
	}

	@Override
	public Integer move(String key, String db)
	{
		return null;
	}

	@Override
	public String mset(String key, String value)
	{
		return null;
	}

	@Override
	public String msetnx(String key, String value)
	{
		return null;
	}

	@Override
	public String msetnx(String[] keyValues)
	{
		return null;
	}

	@Override
	public String multi()
	{
		return null;
	}

	@Override
	public Integer persist(String key)
	{
		return null;
	}

	@Override
	public Integer pexpire(String key, String milliseconds)
	{
		return null;
	}

	@Override
	public Integer pexpireat(String key, String millisecondtimestamp)
	{
		return null;
	}

	@Override
	public String ping()
	{
		return "PONG";
	}

	@Override
	public String psetex(String key, String milliseconds, String value)
	{
		return null;
	}

	@Override
	public Integer pttl(String key)
	{
		return null;
	}

	@Override
	public Integer publish(String channel, String message)
	{
		return null;
	}

	@Override
	public String quit()
	{
		return null;
	}

	@Override
	public String randomkey()
	{
		return null;
	}

	@Override
	public Integer rename(String key, String newkey)
	{
		return null;
	}

	@Override
	public String renamenx(String key, String newkey)
	{
		return null;
	}

	@Override
	public String restore(String key, String ttl, String serializedvalue)
	{
		return null;
	}

	@Override
	public String rpop(String key)
	{
		if (!keys.containsKey(key)) return null;
		List<String> list = (List<String>) keys.get(key);
		if (list.isEmpty()) return null;
		String value = list.remove(list.size() - 1);
		return value;
	}

	@Override
	public String rpoplpush(String source, String destination)
	{
		String value = rpop(source);
		if (value == null) return null;
		lpush(destination, value);
		return value;
	}

	@Override
	public Integer rpush(String key, String value)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new LinkedList<String>());
		}
		List<String> list = (List<String>) keys.get(key);
		list.add(value);
		return list.size();
	}

	@Override
	public Integer rpush(String key, String[] values)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new LinkedList<String>());
		}
		List<String> list = (List<String>) keys.get(key);
		for (String value : values) {
			list.add(value);
		}
		return list.size();
	}

	@Override
	public Integer rpushx(String key, String value)
	{
		return null;
	}

	@Override
	public Integer sadd(String key, String member)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new HashSet());
		}
		Set set = (Set<String>) keys.get(key);
		set.add(member);
		return set.size();
	}

	@Override
	public Integer sadd(String key, String[] members)
	{
		if (!keys.containsKey(key)) {
			keys.put(key, new HashSet());
		}
		Set set = (Set<String>) keys.get(key);
		for (String member : members) {
			set.add(member);
		}
		return set.size();
	}

	@Override
	public String save()
	{
		return null;
	}

	@Override
	public Integer scard(String key)
	{
		if (!keys.containsKey(key)) return 0;
		Set set = (Set) keys.get(key);
		return set.size();
	}

	@Override
	public List<String> sdiff(String key)
	{
		return null;
	}

	@Override
	public List<String> sdiff(String key, String[] values)
	{
		return null;
	}

	@Override
	public Integer sdiffstore(String destination, String[] keys)
	{
		return null;
	}

	@Override
	public String select(String index)
	{
		return null;
	}

	@Override
	public String set(String key, String value)
	{
		keys.put(key, value);
		return "OK";
	}

	@Override
	public Integer setbit(String key, String offset, String value)
	{
		return null;
	}

	@Override
	public String setex(String key, String seconds, String value)
	{
		return null;
	}

	@Override
	public Integer setnx(String key, String value)
	{
		return null;
	}

	@Override
	public Integer setrange(String key, String offset, String value)
	{
		return null;
	}

	@Override
	public List<String> sinter(String key)
	{
		return null;
	}

	@Override
	public List<String> sinter(String[] keys)
	{
		return null;
	}

	@Override
	public Integer sinterstore(String destination, String[] keys)
	{
		return null;
	}

	@Override
	public Integer sismember(String key, String member)
	{
		if (!keys.containsKey(key)) return 0;
		Set set = (Set) keys.get(key);
		return set.contains(member) ? 1 : 0;
	}

	@Override
	public String slaveof(String host, String port)
	{
		return null;
	}

	@Override
	public List<String> smembers(String key)
	{
		List<String> result = new LinkedList<String>();
		if (!keys.containsKey(key)) return result;
		Set set = (Set) keys.get(key);
		result.addAll(set);
		return result;
	}

	@Override
	public Integer smove(String source, String destination, String member)
	{
		return null;
	}

	@Override
	public List<String> sort(String key)
	{
		List<String> result = new LinkedList<String>();
		if (keys.containsKey(key)) {
			result.addAll((Set<String>) keys.get(key));
		}
		return result;
	}

	@Override
	public String spop(String key)
	{
		return null;
	}

	@Override
	public String srandmember(String key)
	{
		return null;
	}

	@Override
	public Integer srem(String key, String member)
	{
		if (!keys.containsKey(key)) return 0;
		Set set = (Set) keys.get(key);
		if (!set.contains(member)) return 0;
		set.remove(member);
		return 1;
	}

	@Override
	public Integer srem(String key, String[] members)
	{
		int result = 0;
		for (String member : members) {
			result += srem(key, member);
		}
		return result;
	}

	@Override
	public Integer strlen(String key)
	{
		return null;
	}

	@Override
	public List<String> sunion(String key)
	{
		return null;
	}

	@Override
	public List<String> sunion(String[] keys)
	{
		return null;
	}

	@Override
	public List<String> sunionstore(String destination, String[] keys)
	{
		return null;
	}

	@Override
	public List<String> time()
	{
		return null;
	}

	@Override
	public Integer ttl(String key)
	{
		return null;
	}

	@Override
	public String type(String key)
	{
		return null;
	}

	@Override
	public String unwatch()
	{
		return null;
	}

	@Override
	public String watch(String key)
	{
		return null;
	}

	@Override
	public String watch(String[] keys)
	{
		return null;
	}

	@Override
	public Integer zadd(String key, String score, String member)
	{
		return null;
	}

	@Override
	public Integer zcard(String key)
	{
		return null;
	}

	@Override
	public Integer zcount(String key, String min, String max)
	{
		return null;
	}

	@Override
	public String zincrby(String key, String increment, String member)
	{
		return null;
	}

	@Override
	public List<String> zrange(String key, String start, String stop)
	{
		return null;
	}

	@Override
	public List<String> zrangebyscore(String key, String min, String max)
	{
		return null;
	}

	@Override
	public Integer zrank(String key, String member)
	{
		return null;
	}

	@Override
	public Integer zrem(String key, String member)
	{
		return null;
	}

	@Override
	public Integer zrem(String key, String[] members)
	{
		return null;
	}

	@Override
	public Integer zremrangebyrank(String key, String start, String stop)
	{
		return null;
	}

	@Override
	public Integer zremrangebyscore(String key, String min, String max)
	{
		return null;
	}

	@Override
	public List<String> zrevrange(String key, String start, String stop)
	{
		return null;
	}

	@Override
	public List<String> zrevrangebyscore(String key, String max, String min)
	{
		return null;
	}

	@Override
	public Integer zrevrank(String key, String member)
	{
		return null;
	}

	@Override
	public String zscore(String key, String member)
	{
		return null;
	}

	@Override
	public Integer zunionstore(String destination, String numkeys, String key)
	{
		return null;
	}

	@Override
	public Integer zunionstore(String destination, int numkeys, String[] keys)
	{
		return null;
	}

}
