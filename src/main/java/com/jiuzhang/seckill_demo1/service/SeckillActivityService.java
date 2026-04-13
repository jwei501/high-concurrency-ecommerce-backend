package com.jiuzhang.seckill_demo1.service;

import com.alibaba.fastjson.JSON;
import com.jiuzhang.seckill_demo1.db.dao.OrderDao;
import com.jiuzhang.seckill_demo1.db.dao.SeckillActivityDao;
import com.jiuzhang.seckill_demo1.db.dao.SeckillCommodityDao;
import com.jiuzhang.seckill_demo1.db.po.Order;
import com.jiuzhang.seckill_demo1.db.po.SeckillActivity;
import com.jiuzhang.seckill_demo1.db.po.SeckillCommodity;
import com.jiuzhang.seckill_demo1.mq.RocketMQService;
import com.jiuzhang.seckill_demo1.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class SeckillActivityService {

    @Resource
    private RedisService service;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private SeckillCommodityDao seckillCommodityDao;

    @Resource
    private RocketMQService rocketMQService;

    @Resource
    private OrderDao orderDao;

    private SnowFlake snowFlake  = new SnowFlake(1, 1);

    public boolean seckillStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return service.stockDeductValidation(key);
    }

    public Order createOrder(long seckillActivityId, long userId) throws Exception {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        Order order = new Order();
        order.setOrderNo((String.valueOf(snowFlake.nextId())));
        order.setSeckillActivityId(activity.getId());
        order.setUserId(userId);
        order.setOrderAmount(activity.getSeckillPrice().longValue());

        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));

        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 3);

        return order;
    }

    public void payOrderProcess(String orderNo) throws Exception {
        //log.info("完成支付订单 订单号:" + orderNo);
        Order order = orderDao.queryOrder(orderNo);
        if (order == null) {
            log.error("订单号对应订单不存在:" + orderNo);
            return;
        } else if(order.getOrderStatus() != 1 ) {
            log.error("订单状态无效:" + orderNo);
            return;
        }
        /*
         * 2.订单支付完成
         */
        order.setPayTime(new Date());
        //订单状态 0:没有可用库存,无效订单 1:已创建等待付款 ,2:支付完成
        order.setOrderStatus(2);
        orderDao.updateOrder(order);
        /*
         *3.发送订单付款成功消息
         */
        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));
    }

    public void pushSeckillInfoToRedis(long seckillActivityId) {
        SeckillActivity seckillActivity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        service.setValue("seckillActivity:" + seckillActivityId, JSON.toJSONString(seckillActivity));
        SeckillCommodity seckillCommodity = seckillCommodityDao.querySeckillCommodityById(seckillActivity.getCommodityId());
        service.setValue("seckillCommodity:" + seckillActivity.getCommodityId(), JSON.toJSONString(seckillCommodity));
    }

}
