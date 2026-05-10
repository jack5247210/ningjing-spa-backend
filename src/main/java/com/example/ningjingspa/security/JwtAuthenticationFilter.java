package com.example.ningjingspa.security;

import com.example.ningjingspa.dao.UserDao;
import com.example.ningjingspa.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDao userDao;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        
        // 如果是 OAuth2 相關路徑，直接放行，不進行 JWT 檢查
        if (path.startsWith("/login/oauth2") || path.startsWith("/oauth2")) {
            chain.doFilter(request, response);
            return;
        }

        // 以下為原本的 JWT 驗證邏輯
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                User user = userDao.getByEmail(email);
                if (user != null) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (Boolean.TRUE.equals(user.getIsAdmin())) {
                        authorities.add(new SimpleGrantedAuthority("ADMIN"));
                    }
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        
        // 繼續執行過濾器鏈
        chain.doFilter(request, response);
    }
}