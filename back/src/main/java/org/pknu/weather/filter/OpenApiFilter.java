package org.pknu.weather.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class OpenApiFilter extends OncePerRequestFilter {
    private static final List<String> OPEN_API_PATHS = Arrays.asList("/api/public/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isOpenApiPath(path)) {
            // Open API 경로인 경우 다음 필터들을 모두 건너뛰고 직접 DispatcherServlet으로 요청을 전달
            request.getRequestDispatcher(path).forward(request, response);
        } else {
            // Open API 경로가 아닌 경우 정상적으로 다음 필터로 진행
            filterChain.doFilter(request, response);
        }
    }

    private boolean isOpenApiPath(String path) {
        return OPEN_API_PATHS.stream().anyMatch(path::startsWith);
    }
}
