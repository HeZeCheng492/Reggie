package com.hzc.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hzc.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
