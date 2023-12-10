package com.elinter.data.migrate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import com.elinter.data.migrate.dao.entity.FailedRecord;
import com.elinter.data.migrate.dao.mapper.EquipInverterDailyMapper;
import com.elinter.data.migrate.dao.mapper.FailedRecordMapper;
import com.elinter.data.migrate.service.EquipInverterDailyService;
import com.elinter.data.migrate.service.FailedRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class EquipInverterDailyServiceImpl extends ServiceImpl<EquipInverterDailyMapper, EquipInverterDaily> implements EquipInverterDailyService {


    @Resource
    EquipInverterDailyMapper equipInverterDailyMapper;

    @Resource
    private FailedRecordMapper failedRecordMapper;

    private static List<Integer> list = Collections.synchronizedList(new ArrayList<>());

    private  Integer  start = 0;
    private  Integer  size = 1000;

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 3, TimeUnit.MINUTES, new LinkedBlockingQueue<>(100), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("daily-service-thread");
            return thread;
        }
    });
    @Override
    @Async("asyncDataMigrate")
    @Transactional
    public CompletableFuture<Integer> migrate(Integer start, Integer size, Date date, Semaphore semaphore) {
        int res;
        if (semaphore.tryAcquire()){
            try {
                log.info(Thread.currentThread().getName() + ": 执行插入 start:{} ",start);
                res = equipInverterDailyMapper.migrateData(start, size);
                return CompletableFuture.completedFuture(res);
            } catch (Exception e) {
                log.error("回滚 start:{}",start,e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                FailedRecord failedRecord = new FailedRecord();
                failedRecord.setStart(start);
                failedRecord.setStep(size);
                failedRecord.setOperateTime(date);
                failedRecordMapper.insert(failedRecord);
                log.error("插入错误日志 start:{}",start,e);
                throw new RuntimeException(e);
            }finally {
                semaphore.release();
            }
        }
        return CompletableFuture.completedFuture(0);
    }

    @Override
    public int preCalCount(Long count) {
        long threadNum = count / 1000;

        if (threadNum % 1000 != 0){
            threadNum++;
        }
        Semaphore semaphore = new Semaphore(4);
        for (;threadNum > 0;threadNum--){
            int res = equipInverterDailyMapper.getCountByGroup(start, size);
            list.add(res);
//            if (semaphore.tryAcquire()){
//                executor.execute(() -> {
//                    try {
//
//                    }catch (Exception e){
//                        log.error("计算错误日志 start:{}",start,e);
//                    }finally {
//                        semaphore.release();
//                    }
//                });
//            }
            start = start + size;
        }
        return list.stream().reduce(0,Integer::sum);
    }
}
