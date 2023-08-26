package com.hzc.reggie.dto;

import com.hzc.reggie.entity.OrderDetail;
import com.hzc.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author: 鵺白
 * @date: 2023/8/25
 * @description:
 */
@Data
public class OrderDto extends Orders {

    private List<OrderDetail> orderDetails;
}
