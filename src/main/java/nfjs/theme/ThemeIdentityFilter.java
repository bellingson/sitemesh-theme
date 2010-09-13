package nfjs.theme;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * The ThemeIdentityFilter inspects the host value in the HTTP header to identify the theme 
 * and call setDomain(host) in the ThemeManager.  Based on the host string, ThemeManager sets
 * a threadLocal theme variable to set the theme property to be accessible within the
 * HTTP request scope.
 *
 * User: benellingson@yahoo.com
 * Date: Jun 18, 2008
 * Time: 4:40:03 PM
 *
 */
public class ThemeIdentityFilter implements Filter {

    private final static String [] HOME_URI = { "/","/index.jsp","/index.html" };

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException, ServletException {

           HttpServletRequest req = (HttpServletRequest) request;
           HttpServletResponse resp = (HttpServletResponse) response;

           String domainName = req.getHeader("host");

           try {

               Theme theme = ThemeManager.setDomain(domainName);
               req.setAttribute("theme",theme);

               String uri = req.getRequestURI();

               // if uri matches home pattern continue in filter chain
               if(isHomeUri(uri)) {
                   resp.sendRedirect(ThemeManager.getTheme().getHomeUri());
                   return;
               }

               fc.doFilter(request,response);

           } finally {
               ThemeManager.clear();
           }
       }

        public Boolean isHomeUri(String uri) {

            for(String huri : HOME_URI) {  // if uri = '/'
                if(uri.equals(huri)) {
                    return true;
                }
            }

            return false;
        }


        public void init(FilterConfig filterConfig) { }

        public void destroy() { }


}
