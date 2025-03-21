package com.itniuma.bigevent.interceptors;

import com.itniuma.bigevent.pojo.Result;
import com.itniuma.bigevent.utils.JwtUtil;
import com.itniuma.bigevent.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        // 验证token
        try {
            // 从redis中获取相同的token
            String tokenValue = stringRedisTemplate.opsForValue().get(token);
            // 如果token不存在或者token与redis中的token不一致，则抛出异常
            if (tokenValue == null || !tokenValue.equals(token)) {
                throw new RuntimeException();
            }
            Map<String, Object> calims = JwtUtil.parseToken(token);
            //将业务数据存储到ThreadLocal中
            ThreadLocalUtil.set(calims);
            return true; // 放行
        } catch (Exception e) {
            // 非法令牌
            // 设置http相应状态码为401
            response.setStatus(401);
            return false; // 拦截
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        // 清除ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }
}
