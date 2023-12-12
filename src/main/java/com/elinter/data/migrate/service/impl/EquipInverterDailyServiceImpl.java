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
import java.util.concurrent.*;

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
    public CompletableFuture<Integer> migrate(Long start, Long end,Date date) {
        log.info(Thread.currentThread().getName() + ": 执行插入 start:{} end:{}",start,end);
        int res = 0;
        try {
            res = equipInverterDailyMapper.migrateData(start, end);
        } catch (Exception e) {
            log.error("回滚 start:{}",start,e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("插入错误日志 start:{}",start,e);
            insertFailedLog(start,end,date);
        }
        return CompletableFuture.completedFuture(res);
    }

    @Override
    public Long selectMaxId() {
        return equipInverterDailyMapper.selectMaxId();
    }


    private void insertFailedLog(Long start, Long end,Date date) {
        FailedRecord failedRecord = new FailedRecord();
        failedRecord.setStart(start);
        failedRecord.setEnd(end);
        failedRecord.setOperateTime(date);
        failedRecordMapper.insert(failedRecord);
    }
}
