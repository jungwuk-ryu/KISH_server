package org.kish;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class Interceptor extends HandlerInterceptorAdapter {
    public ConcurrentHashMap<String, Long> clients = new ConcurrentHashMap<>();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);

        long current = System.currentTimeMillis();
        clients.put(request.getRemoteAddr(), current);

        StringBuilder sb = new StringBuilder();
        sb.append("url=[").append(request.getRequestURI())
                .append("]; client=[").append(request.getRemoteAddr())
                .append("];");
        MainLogger.info(sb.toString());
    }
}
