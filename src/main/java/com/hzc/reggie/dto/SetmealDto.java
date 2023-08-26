package com.hzc.reggie.dto;


import com.hzc.reggie.entity.Setmeal;
import com.hzc.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
