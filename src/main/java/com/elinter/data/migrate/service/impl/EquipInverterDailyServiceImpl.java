package com.elinter.data.migrate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import com.elinter.data.migrate.dao.entity.FailedRecord;
import com.elinter.data.migrate.dao.mapper.EquipInverterDailyMapper;
import com.elinter.data.migrate.dao.mapper.FailedRecordMapper;
import com.elinter.data.migrate.service.EquipInverterDailyService;
import com.elinter.data.migrate.service.FailedRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EquipInverterDailyServiceImpl extends ServiceImpl<EquipInverterDailyMapper, EquipInverterDaily> implements EquipInverterDailyService {


    @Resource
    EquipInverterDailyMapper equipInverterDailyMapper;

    @Resource
    private FailedRecordMapper failedRecordMapper;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100,200,3, TimeUnit.MINUTES,new LinkedBlockingQueue<>(100));
    @Override
    @Async("asyncDataMigrate")
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<Integer> migrate(Integer start, Integer size,Date date) {
        int res;
        try {
            log.info(Thread.currentThread().getName() + ": 执行插入 start:{} ",start);
            res = equipInverterDailyMapper.migrateData(start, size);
            return CompletableFuture.completedFuture(res);
        } catch (Exception e) {
            FailedRecord failedRecord = new FailedRecord();
            failedRecord.setStart(start);
            failedRecord.setStep(size);
            failedRecord.setOperateTime(date);
            failedRecordMapper.insert(failedRecord);
            throw new RuntimeException(e);
        }
    }
}
