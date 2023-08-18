package dev.backlog.domain.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private static final int START = 0;

    private final RedisTemplate<String, String> redisTemplate;

    public void setValue(String key, String data) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data);
    }

    public void setValueList(String key, String data) {
        redisTemplate.opsForList().rightPushAll(key, data);
    }

    public String getValue(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public List<String> getValueList(String key) {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        Long size = listOperations.size(key);
        return Objects.isNull(size) ? new ArrayList<>() : listOperations.range(key, START, size - 1);
    }

}
