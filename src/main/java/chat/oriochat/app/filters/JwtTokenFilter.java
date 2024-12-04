package chat.oriochat.app.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(AUTHORIZATION_HEADER);

        // if (token == null || !token.startsWith(BEARER_PREFIX)) {
        //     sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized.", "Access token isn't available.");
        //     return;
        // }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message, String errorDetail) throws IOException {
        response.setStatus(status);
        Map<String, Object> responseJSON = new HashMap<>();
        responseJSON.put("status", false);
        responseJSON.put("message", message);

        Map<String, List<String>> errors = new HashMap<>();
        errors.computeIfAbsent("error", key -> new java.util.ArrayList<>()).add(errorDetail);
        responseJSON.put("errors", errors);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseJSON));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
