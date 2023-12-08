package com.elinter.data.migrate.controller;

import com.elinter.data.migrate.service.EquipInverterDailyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author :何嘉骏
 * Description: 操作api
 * Date: 10:14 2023/12/8
 */

@RestController
@RequestMapping("/operate")
public class OperationController {

    @Resource
    private EquipInverterDailyService dailyService;

    private Long count = 10420009L;

    private static List<Integer> resList = Collections.synchronizedList(new ArrayList<>());

    private static ThreadLocal<Long>  threadLocal = new ThreadLocal<>();

    @GetMapping("/start")
    public String migrate(@RequestParam Integer start,@RequestParam Integer step) throws ExecutionException, InterruptedException {
        long threadNum = count / step;
        if (threadNum % step != 0){
            threadNum++;
        }
        Date date = new Date();
        for (;threadNum > 0;threadNum--){
            CompletableFuture<Integer> res = dailyService.migrate(start, step, date);
            start =  start + step + 1;
//            resList.add(res.get() == null ? 0 : res.get());
        }
        int sum = resList.stream().reduce(0, Integer::sum);
        return "已插入" + sum + "条数据";
    }

//    @PostConstruct
    private void getCount(){
        count = dailyService.count();
    }


}
