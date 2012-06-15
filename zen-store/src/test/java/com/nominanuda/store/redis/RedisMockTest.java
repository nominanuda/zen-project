package com.nominanuda.store.redis;

import com.nominanuda.store.redis.*;

public class RedisMockTest extends RedisTest {
	@Override
	protected Redis getObject() {
		return new RedisMock();
	}
}
