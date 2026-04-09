package com.itniuma.bitinn.validation;

import com.itniuma.bitinn.annotation.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 状态校验器
 * @author aceFelix
 */
public class StateValidation implements ConstraintValidator<State, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }
        return "已发布".equals(s) || "草稿".equals(s);
    }
}
