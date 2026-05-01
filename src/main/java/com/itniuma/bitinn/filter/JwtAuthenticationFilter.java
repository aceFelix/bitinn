package com.itniuma.bitinn.filter;

import com.itniuma.bitinn.utils.JwtUtil;
import com.itniuma.bitinn.utils.RedisCacheHelper;
import com.itniuma.bitinn.utils.ThreadLocalUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * JWT 认证过滤器
 * 优化策略：先本地解析 JWT（零网络开销），再检查 Redis 黑名单（仅在 token 被主动失效时才命中）
 * 正常请求只需 1 次 JWT 解析，无需每次都查 Redis
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    private final RedisCacheHelper redisCache;

    public JwtAuthenticationFilter(RedisCacheHelper redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 第一步：本地解析 JWT（零网络开销）
            Map<String, Object> claims = JwtUtil.parseToken(token);

            // 第二步：检查黑名单（仅在 token 被主动失效时才命中，如修改密码、登出）
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            String blacklisted = redisCache.get(blacklistKey);
            if (blacklisted != null) {
                log.debug("Token 已被加入黑名单: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            ThreadLocalUtil.set(claims);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claims, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.debug("JWT 解析失败: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            ThreadLocalUtil.remove();
        }
    }
}
