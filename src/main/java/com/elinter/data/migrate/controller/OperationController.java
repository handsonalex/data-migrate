package com.elinter.data.migrate.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.elinter.data.migrate.config.DataMigrateThreadPool;
import com.elinter.data.migrate.dao.entity.FailedRecord;
import com.elinter.data.migrate.service.EquipInverterDailyService;
import com.elinter.data.migrate.service.FailedRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author :何嘉骏
 * Description: 操作api
 * Date: 10:14 2023/12/8
 */

@RestController
@RequestMapping("/operate")
@Slf4j
public class OperationController {

    @Resource
    private EquipInverterDailyService dailyService;

    private Long count;


    @Resource
    private FailedRecordService failedRecordService;

    @Resource
    DataMigrateThreadPool threadPool;

    @GetMapping("/migrate")
    public String migrate(@RequestParam Integer step){

        long end = count;
        long start = end - step;
        List<CompletableFuture<Map<Boolean,FailedRecord>>> futures = new ArrayList<>();

        long threadNum = count / step;
        if (threadNum % step != 0){
            threadNum++;
        }
        Date date = new Date();
        long startTime = System.currentTimeMillis();

        for (;threadNum > 0;threadNum--){
            if (start < 0){
                start = 0;
            }
            // 避免启动时候所有线程同一时间提交事务
            setThreadInterval();
            CompletableFuture<Map<Boolean,FailedRecord>> res;
            res = dailyService.migrate(start, end,date);
            futures.add(res);
            end = start - 1;
            start -= step;
        }

        // 等待所有异步任务完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 阻塞直到所有任务完成
        allOf.join();
        long endTime = System.currentTimeMillis();
        Map<Boolean,FailedRecord> resMap = new HashMap<>();
        int sum = 0;
        for (CompletableFuture<Map<Boolean,FailedRecord>> future : futures) {
            try {
                resMap = future.get();
            } catch (Exception e) {
                log.error("获取结果失败", e);
            }
            if (resMap.get(false) != null){
                failedRecordService.save(resMap.get(false));
            }else if (resMap.get(true) != null){
                sum += step;
            }
        }

        log.info("Task took " + (endTime - startTime) / (60 * 1000) + " minutes.");
        return "已插入" + sum + "条数据";
    }

    private void setThreadInterval() {
        int interval = threadPool.getCorePoolSize();
        if (interval != 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            interval--;
        }
    }

    @GetMapping("/manual")
    public String manual(@RequestParam String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        List<FailedRecord> failedRecords = failedRecordService.list(new LambdaQueryWrapper<FailedRecord>().eq(FailedRecord::getOperateTime, date));
        List<CompletableFuture<Map<Boolean,FailedRecord>>> futures = new ArrayList<>();


        Date newDate = new Date();
        for (FailedRecord failedRecord : failedRecords){
            CompletableFuture<Map<Boolean,FailedRecord>> res = null;
            setThreadInterval();
            res = dailyService.migrate(failedRecord.getStart(), failedRecord.getEnd(),newDate);
            futures.add(res);
        }

        // 等待所有异步任务完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 阻塞直到所有任务完成
        allOf.join();
        Map<Boolean,FailedRecord> resMap;
        try {
            for (CompletableFuture<Map<Boolean,FailedRecord>> future : futures) {
                resMap = future.get();
                if (resMap.get(false) != null){
                    failedRecordService.save(resMap.get(false));
                }else if (resMap.get(true) != null){
                    failedRecordService.remove(new LambdaQueryWrapper<FailedRecord>()
                            .eq(FailedRecord::getStart, resMap.get(true) .getStart())
                            .eq(FailedRecord::getEnd, resMap.get(true) .getEnd())
                            .eq(FailedRecord::getOperateTime, date));
                }
            }
        } catch (Exception e) {
            log.error("获取结果失败", e);
        }
        return "插入完成";
    }

    @GetMapping("/simpleMigrate")
    public String simpleMigrate(@RequestParam Integer step){
        long end = count;
        long start = end - step;

        long times = count / step;
        if (times % step != 0){
            times++;
        }
        Date date = new Date();
        long startTime = System.currentTimeMillis();
        List<Integer> list = new ArrayList<>();
        for (;times > 0;times--){
            if (start < 0){
                start = 0;
            }
            int res = 0 ;
            try {
                res = dailyService.simpleMigrate(start, end,date);
            } catch (Exception e) {
                log.error("插入失败日志 start:{}  end:{}",start,end);
                FailedRecord failedRecord = new FailedRecord();
                failedRecord.setStart(start);
                failedRecord.setEnd(end);
                failedRecord.setOperateTime(date);
                failedRecordService.save(failedRecord);
            }
            list.add(res);
            end = start - 1;
            start -= step;
        }
        long endTime = System.currentTimeMillis();
        log.info("Task took " + (endTime - startTime) / (60 * 1000) + " minutes.");
        int sum = list.stream().reduce(0,Integer::sum);
        return "已插入" + sum + "条数据";
    }

    @PostConstruct
    private void getCount(){
        log.info("预获取原表最大id");
        count = dailyService.selectMaxId();
        log.info("maxId = {}",count);
    }


}
