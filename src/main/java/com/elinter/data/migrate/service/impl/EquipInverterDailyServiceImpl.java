package com.elinter.data.migrate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import com.elinter.data.migrate.dao.entity.FailedRecord;
import com.elinter.data.migrate.dao.mapper.EquipInverterDailyMapper;
import com.elinter.data.migrate.dao.mapper.FailedRecordMapper;
import com.elinter.data.migrate.service.EquipInverterDailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EquipInverterDailyServiceImpl extends ServiceImpl<EquipInverterDailyMapper, EquipInverterDaily> implements EquipInverterDailyService {


    @Resource
    private EquipInverterDailyMapper equipInverterDailyMapper;

    @Resource
    private FailedRecordMapper failedRecordMapper;

    @Override
    @Async("asyncDataMigrate")
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<Map<Boolean,FailedRecord>> migrate(Long start, Long end, Date date) {
        log.info(Thread.currentThread().getName() + ": 执行插入 start:{} end:{}",start,end);
        FailedRecord failedRecord = new FailedRecord();
        Map<Boolean,FailedRecord> resMap = new HashMap<>();
        resMap.put(true,failedRecord);
        try {
            equipInverterDailyMapper.migrateData(start, end);
        } catch (Exception e) {
            failedRecord.setStart(start);
            failedRecord.setEnd(end);
            failedRecord.setOperateTime(date);
            resMap.put(false, failedRecord);
            log.error("回滚 start:{} end:{}",start,end,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return CompletableFuture.completedFuture(resMap);
        }
        return CompletableFuture.completedFuture(resMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int simpleMigrate(Long start, Long end, Date date) {
        log.info( "执行插入 start:{} end:{}",start,end);
        return equipInverterDailyMapper.migrateData(start, end);

    }

    @Override
    public Long selectMaxId() {
        return equipInverterDailyMapper.selectMaxId();
    }

}
