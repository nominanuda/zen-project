APPEND key value INTEGER

AUTH password STATUS

BGREWRITEAOF STATUS

BGSAVE STATUS

BLPOP (String key, int timeout) MULTIBULK
BLPOP (String... keys, int timeout) MULTIBULK

BRPOP (String key, int timeout)  MULTIBULK
BRPOP (String... keys, int timeout) MULTIBULK

BRPOPLPUSH source destination timeout BULK

#CONFIG GET parameter MULTIBULK

#CONFIG SET parameter value STATUS

#CONFIG RESETSTAT

DBSIZE INTEGER

#DEBUG OBJECT key

#DEBUG SEGFAULT STATUS

DECR key INTEGER

DECRBY key decrement INTEGER

DEL key INTEGER

DEL (String... keys)  INTEGER

DISCARD STATUS

#DUMP KEY

ECHO message BULK

#EVAL script numkeys key [key ...] arg [arg ...] MULTIBULK

EXEC MULTIBULK

EXISTS key INTEGER

EXPIRE key seconds INTEGER

EXPIREAT key timestamp INTEGER

FLUSHALL STATUS

FLUSHDB STATUS

GET key BULK

GETBIT key offset INTEGER

GETRANGE key start end BULK

GETSET key value BULK

HDEL key field INTEGER
HDEL (String key, String... fields) INTEGER

HEXISTS key field INTEGER

HGET key field BULK

HGETALL key MULTIBULK

HINCRBY key field increment INTEGER

HINCRBYFLOAT key field increment BULK

HKEYS key MULTIBULK

HLEN key INTEGER

HMGET key field MULTIBULK
HMGET (String key, String... fields) MULTIBULK

HMSET key field value STATUS
HMSET (String key, String... fieldValues) STATUS

HSET key field value INTEGER

HSETNX key field value INTEGER

HVALS key MULTIBULK

INCR key INTEGER

INCRBY key increment INTEGER

INCRBYFLOAT key increment BULK

INFO BULK

KEYS pattern MULTIBULK

LASTSAVE INTEGER

LINDEX key index MULTIBULK

#LINSERT key BEFORE|AFTER pivot value 

LLEN key INTEGER

LPOP key BULK

LPUSH key value INTEGER
LPUSH (String key, String... values) INTEGER

LPUSHX key value INTEGER

LRANGE key start stop MULTIBULK

LREM key count value INTEGER

LSET key index value STATUS

LTRIM key start stop STATUS

MGET (String... keys) MULTIBULK

MIGRATE host port key destination-db timeout STATUS

#MONITOR

MOVE key db INTEGER

MSET key value STATUS
#MSET (String... keyValues) STATUS

MSETNX key value STATUS
MSETNX (String... keyValues) STATUS

MULTI STATUS

#OBJECT subcommand [arguments [arguments ...]] 

PERSIST key INTEGER

PEXPIRE key milliseconds INTEGER

PEXPIREAT key millisecond-timestamp INTEGER

PING STATUS

PSETEX key milliseconds value STATUS

#PSUBSCRIBE pattern [pattern ...] 

PTTL key INTEGER

PUBLISH channel message INTEGER

#PUNSUBSCRIBE [pattern [pattern ...]] 

QUIT STATUS

RANDOMKEY BULK

RENAME key newkey INTEGER

RENAMENX key newkey STATUS

RESTORE key ttl serialized-value STATUS

RPOP key BULK

RPOPLPUSH source destination BULK

RPUSH key value INTEGER
RPUSH (String key, String... values) INTEGER

RPUSHX key value INTEGER

SADD key member INTEGER
SADD (String key, String... members) INTEGER

SAVE STATUS

SCARD key INTEGER

#SCRIPT EXISTS script [script ...] MULTIBULK

#SCRIPT FLUSH

#SCRIPT KILL

#SCRIPT LOAD

SDIFF key MULTIBULK
SDIFF (String key, String... values) MULTIBULK

SDIFFSTORE (String destination, String... keys) INTEGER

SELECT index STATUS

SET key value STATUS

SETBIT key offset value INTEGER

SETEX key seconds value STATUS

SETNX key value INTEGER

SETRANGE key offset value INTEGER

#SHUTDOWN [NOSAVE] [SAVE] 

SINTER key MULTIBULK
SINTER (String... keys) MULTIBULK

SINTERSTORE (String destination, String... keys) INTEGER

SISMEMBER key member INTEGER

SLAVEOF host port STATUS

#SLOWLOG subcommand [argument]

SMEMBERS key MULTIBULK

SMOVE source destination member INTEGER

SORT key MULTIBULK
#SORT key [BY pattern] [LIMIT offset count] [GET pattern [GET pattern ...]] [ASC|DESC] [ALPHA] [STORE destination] MULTIBULK

SPOP key BULK

SRANDMEMBER key BULK

SREM key member INTEGER
SREM (String key, String... members) INTEGER

STRLEN key INTEGER

#SUBSCRIBE channel [channel ...] 

SUNION key MULTIBULK
SUNION (String... keys) MULTIBULK

SUNIONSTORE (String destination, String... keys) MULTIBULK

TIME MULTIBULK

TTL key INTEGER

TYPE key BULK

#UNSUBSCRIBE channel [channel ...] 

UNWATCH STATUS

WATCH key STATUS
WATCH (String... keys) STATUS

ZADD key score member INTEGER
#ZADD key score member [score] [member] INTEGER

ZCARD key INTEGER

ZCOUNT key min max INTEGER

ZINCRBY key increment member BULK

#ZINTERSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX]  INTEGER

ZRANGE key start stop MULTIBULK
#ZRANGE key start stop [WITHSCORES] MULTIBULK

ZRANGEBYSCORE key min max MULTIBULK
#ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count] MULTIBULK

ZRANK key member INTEGER

ZREM key member INTEGER
ZREM (String key, String... members) INTEGER

ZREMRANGEBYRANK key start stop INTEGER

ZREMRANGEBYSCORE key min max INTEGER

ZREVRANGE key start stop MULTIBULK
#ZREVRANGE key start stop [WITHSCORES] MULTIBULK

ZREVRANGEBYSCORE key max min MULTIBULK
#ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count] MULTIBULK

ZREVRANK key member INTEGER

ZSCORE key member BULK

ZUNIONSTORE destination numkeys key INTEGER
ZUNIONSTORE (String destination, int numkeys, String... keys) INTEGER
#ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight [weight ...]] [AGGREGATE SUM|MIN|MAX] INTEGER
