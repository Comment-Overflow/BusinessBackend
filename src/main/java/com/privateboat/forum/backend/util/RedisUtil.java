package com.privateboat.forum.backend.util;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    static private final String postString = "day-posts";
    static private final String commentString = "day-comments";
    static private final String userString = "day-users";
    static private final String activeUserString = "day-active-users";
    static private final String approvalString = "day-approvals";
    static private final String viewsString = "day-views";
    static private final String recordArrayKey = "record";
    static private final String viewRecordPrefix = "view-record-";


    private List<Long> recordList() {
        List<String> recordString = stringRedisTemplate.opsForList().range(recordArrayKey, 0, -1);
        assert recordString != null;
        return recordString.stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private void setRecordList(List<Long> recordList) {
        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();
        for (int index = 0; index < 24; index++) {
            listOperations.set(recordArrayKey, index, recordList.get(index).toString());
        }
    }

    private List<Long> dailyList() {
        List<Long> dailyList = new ArrayList<>();
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        dailyList.add(Long.parseLong(Objects.requireNonNull(valueOperations.get(postString))));
        dailyList.add(Long.parseLong(Objects.requireNonNull(valueOperations.get(commentString))));
        dailyList.add(Long.parseLong(Objects.requireNonNull(valueOperations.get(userString))));
        dailyList.add((long) BitSet.valueOf(Objects.requireNonNull(valueOperations.get(activeUserString)).getBytes()).cardinality());
        dailyList.add(Long.parseLong(Objects.requireNonNull(valueOperations.get(approvalString))));
        dailyList.add(Long.parseLong(Objects.requireNonNull(valueOperations.get(viewsString))));
        return dailyList;
    }

    private void clearDailyListInRedis() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(postString, "0", 2, TimeUnit.DAYS);
        valueOperations.set(commentString, "0", 2, TimeUnit.DAYS);
        valueOperations.set(userString, "0", 2, TimeUnit.DAYS);
        valueOperations.set(activeUserString, "", 2, TimeUnit.DAYS);
        valueOperations.set(viewsString, "0", 2, TimeUnit.DAYS);
        valueOperations.set(approvalString, "0", 2, TimeUnit.DAYS);
    }

    public void initializeRecord() {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        ListOperations<String, String> listOperations = stringRedisTemplate.opsForList();

        valueOperations.setIfAbsent(postString, "0", 2, TimeUnit.DAYS);
        valueOperations.setIfAbsent(commentString, "0", 2, TimeUnit.DAYS);
        valueOperations.setIfAbsent(userString, "0", 2, TimeUnit.DAYS);
        valueOperations.setIfAbsent(activeUserString, "", 2, TimeUnit.DAYS);
        valueOperations.setIfAbsent(viewsString, "0", 2, TimeUnit.DAYS);
        valueOperations.setIfAbsent(approvalString, "0", 2, TimeUnit.DAYS);

        Long size = listOperations.size(recordArrayKey);
        if (size == null || size != 24) {
            stringRedisTemplate.delete(recordArrayKey);
            List<String> initRecordList = new ArrayList<>(Collections.nCopies(24, "0"));
            listOperations.rightPushAll(recordArrayKey, initRecordList);
        }
    }

    public void dailyRecordUpdate() {
        List<Long> dailyList = dailyList();
        List<Long> record = recordList();

        // add daily record to total counter
        for (int timeUnit = 1; timeUnit < 4; ++timeUnit) {
            for (int index = 0; index < 6; ++index) {
                int recordInd = index + timeUnit * 6;
                record.set(recordInd, record.get(recordInd) + dailyList.get(index));
            }
        }

        // if begin of the week, clear weekly record
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            for (int recordInd = 6; recordInd < 12; ++recordInd) {
                record.set(recordInd, 0L);
            }
        }

        // if begin of the month, clear monthly record
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1) {
            for (int recordInd = 12; recordInd < 18; ++recordInd) {
                record.set(recordInd, 0L);
            }
        }

        setRecordList(record);
        clearDailyListInRedis();
    }

    public void addPostCounter() {
        stringRedisTemplate.opsForValue().increment(postString);
    }

    public void addCommentCounter() {
        stringRedisTemplate.opsForValue().increment(commentString);
    }

    public void addUserCounter() {
        stringRedisTemplate.opsForValue().increment(userString);
    }

    public void addActiveUserCounter(Long userId) {
        stringRedisTemplate.opsForValue().setBit(activeUserString, userId, true);
    }

    public void addViewCounter(Long userId, Long postId) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.increment(viewsString);
        valueOperations.setBit(viewRecordPrefix + userId.toString(), postId, true);
    }

    public void addApprovalCount() {
        stringRedisTemplate.opsForValue().increment(approvalString);
    }

    public List<Long> getCurrentRecord() {
        List<Long> dailyList = dailyList();
        List<Long> record = recordList();

        List<Long> result = new ArrayList<>(dailyList);
        for (int timeUnit = 1; timeUnit < 4; ++timeUnit) {
            for (int index = 0; index < 6; ++index) {
                int recordInd = index + timeUnit * 6;
                result.add(record.get(recordInd) + dailyList.get(index));
            }
        }
        return result;
    }

    public List<Long> filterReadPosts(Long userId, List<Long> postList) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        String key = viewRecordPrefix + userId.toString();
        return postList.stream().filter(
                id -> Boolean.FALSE.equals(valueOperations.getBit(key, id))
        ).collect(Collectors.toList());
    }
}
