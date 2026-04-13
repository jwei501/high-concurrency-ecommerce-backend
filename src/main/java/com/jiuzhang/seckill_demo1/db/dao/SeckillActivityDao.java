package com.jiuzhang.seckill_demo1.db.dao;

import com.jiuzhang.seckill_demo1.db.po.SeckillActivity;

import java.util.List;

public interface SeckillActivityDao {

    public List<SeckillActivity> querySeckillActivitysByStatus(int activityStatus);

    public void insertSeckillActivity(SeckillActivity seckillActivity);

    public SeckillActivity querySeckillActivityById(long activityId);

    public void updateSeckillActivity(SeckillActivity seckillActivity);

    boolean lockStock(Long SeckillActivityId);

    boolean deductStock(Long seckillActivityId);

    void revertStock(Long seckillActivityId);

}
