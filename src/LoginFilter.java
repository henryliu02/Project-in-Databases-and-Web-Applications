import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if this URL is allowed to access without logging in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            System.out.println("URL allowed without login");
            // Keep default action: pass along the filter chain
            chain.doFilter(request, response);
            return;
        }

        // Redirect to login page if the "user" attribute doesn't exist in session
        if (httpRequest.getSession().getAttribute("user") == null) {
            System.out.println(httpRequest.getSession().getAttribute("user"));
            // Get the requested URI
            String requestURI = httpRequest.getRequestURI();

            // Check if the requested URI contains "_dashboard"
            if (requestURI.contains("_dashboard/")) {
                httpResponse.sendRedirect("_dashboard/login.html");
            } else if (requestURI.contains("_dashboard")){
                httpResponse.sendRedirect("_dashboard/login.html");
            }
            else {
                httpResponse.sendRedirect("login.html");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        /*
         Setup your own rules here to allow accessing some resources without logging in
         Always allow your own login related requests(html, js, servlet, etc..)
         You might also want to allow some CSS files, etc..
         */
        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
    }

    public void init(FilterConfig fConfig) {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("api/employeeLogin");
        allowedURIs.add("logo.png");
        allowedURIs.add("movie.png");
        allowedURIs.add("login.css");
        allowedURIs.add("lzh.png");
        allowedURIs.add("song.mp3");
        allowedURIs.add("_dashboard/login.html");
        allowedURIs.add("_dashboard/login.js");
        allowedURIs.add("form-recaptcha");
    }

    public void destroy() {
        // ignored.
    }

}
