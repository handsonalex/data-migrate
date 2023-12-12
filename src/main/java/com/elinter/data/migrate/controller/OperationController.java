package com.elinter.data.migrate.controller;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

//    private Long count = 10000000L;
    private Long count;


    @Resource
    private FailedRecordService failedRecordService;

    @GetMapping("/migrate")
    public String migrate(@RequestParam Integer step){

        long end = count;
        long start = end - step;
        List<CompletableFuture<Integer>> futures = new ArrayList<>();

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
            CompletableFuture<Integer> res = null;
            try {
                res = dailyService.migrate(start, end,date);
            } catch (Exception e) {
                FailedRecord failedRecord = new FailedRecord();
                failedRecord.setStart(start);
                failedRecord.setEnd(end);
                failedRecord.setOperateTime(date);
                failedRecordService.save(failedRecord);
                log.error("插入错误日志 start:{}",start,e);
            }

            futures.add(res);
            end = start - 1;
            start -= step;
        }

        // 等待所有异步任务完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 阻塞直到所有任务完成
        allOf.join();
        long endTime = System.currentTimeMillis();
        int sum = 0;
        try {
            for (CompletableFuture<Integer> future : futures) {
                sum += future.get();
            }
        } catch (Exception e) {
            log.error("获取结果失败", e);
        }
        log.info("Task took " + (endTime - startTime) / (60 * 1000) + " minutes.");
        return "已插入" + sum + "条数据";
    }

    @GetMapping("/manual")
    public String manual(@RequestParam Long start,@RequestParam Integer step,@RequestParam Long end){

        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        long threadNum = end / step;
        if (threadNum % step != 0){
            threadNum++;
        }
        Date date = new Date();
        for (;threadNum > 0;threadNum--){
            CompletableFuture<Integer> res = null;
            try {
                res = dailyService.migrate(start, end,date);
            } catch (Exception e) {
                FailedRecord failedRecord = new FailedRecord();
                failedRecord.setStart(start);
                failedRecord.setEnd(end);
                failedRecord.setOperateTime(date);
                failedRecordService.save(failedRecord);
                log.error("插入错误日志 start:{}",start,e);
            }

            futures.add(res);
            start =  start + step;
        }

        // 等待所有异步任务完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        // 阻塞直到所有任务完成
        allOf.join();
        int sum = 0;
        try {
            for (CompletableFuture<Integer> future : futures) {
                sum += future.get();

            }
        } catch (Exception e) {
            log.error("获取结果失败", e);
        }
        return "已插入" + sum + "条数据";
    }

    @PostConstruct
    private void getCount(){
        log.info("预获取原表最大id");
        count = dailyService.selectMaxId();
        log.info("maxId = {}",count);
    }


}
