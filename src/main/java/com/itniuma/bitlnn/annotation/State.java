package com.itniuma.bitlnn.annotation;

import com.itniuma.bitlnn.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * @author aceFelix
 */
// 表示该注解应该被包含在文档中
@Documented
// 表示该注解应该被应用到字段上
@Target(ElementType.FIELD)
// 表示该注解应该被保留到运行时
@Retention(RetentionPolicy.RUNTIME)
// 指定验证器
@Constraint(validatedBy = {StateValidation.class})
public @interface State {
    // message: 表示验证失败时返回的错误消息
    String message() default "state的值只能是已发布或者草稿";

    // groups: 表示验证时使用的分组
    Class<?>[] groups() default {};

    // payload: 表示验证时使用的负载，可以用于传递额外的验证信息
    Class<? extends Payload>[] payload() default {};
}
