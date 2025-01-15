package com.itniuma.bigevent.validation;

import com.itniuma.bigevent.annotation.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// 验证器
public class StateValidation implements ConstraintValidator<State,String> {
    // 泛型一：给哪个注解提供校验规则，泛型二：验证的数据类型

    /**
     *
     * @param s ： 需要校验的数据
     * @param constraintValidatorContext
     * @return 校验结果：true：校验通过，false：校验失败
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        // 提供校验规则
        if (s == null) {
            return false;
        }
        if (s.equals("已发布")|| s.equals("草稿")){
            return true;
        }
        return false;
    }


}
