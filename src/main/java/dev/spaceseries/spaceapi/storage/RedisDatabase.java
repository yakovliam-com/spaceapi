package dev.spaceseries.spaceapi.storage;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDatabase implements Database<Jedis> {

    private final JedisPool jedisPool;
    private final String auth;

    private RedisDatabase(String host, int port, String password) {
        jedisPool = new JedisPool(host, port);
        auth = password;
    }

    @Override
    public Jedis getConnection() {
        Jedis jedis = jedisPool.getResource();
        if (auth != null)
            jedis.auth(auth);

        return jedis;
    }
}
