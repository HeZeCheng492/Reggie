package com.hzc.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzc.reggie.common.CustomException;
import com.hzc.reggie.dto.SetmealDto;
import com.hzc.reggie.entity.Setmeal;
import com.hzc.reggie.entity.SetmealDish;
import com.hzc.reggie.mapper.SetmealMapper;
import com.hzc.reggie.service.SetmealDishService;
import com.hzc.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {

    @Autowired
    private  SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish表，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count=this.count(queryWrapper);
        if(count>0){
            //如果不能删除，抛出一个业务异常
            throw  new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        //删除关系表中的数据---setmeal_dish
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(setmealDishQueryWrapper);
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //先查询套餐的基本数据
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //再查询该套餐关联的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return (setmealDto);
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //更新sermeal表的基本数据
        this.updateById(setmealDto);

        //先查询套餐关联的菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        //删除当前套餐关联的菜品数据---setmeal_dish表的delete操作
        setmealDishService.remove(queryWrapper);

        //添加当前提交过来的菜品数据---setmeal_dish表的insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes().stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void updateStatus(int status, List<Long> ids) {
        //先根据id查询到套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);

        //查出的菜品用list存储，给每项菜品修改状态
        List<Setmeal> list = this.list(queryWrapper);
        list.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(list);
    }

}
