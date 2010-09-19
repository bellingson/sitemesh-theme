package nfjs.theme;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;


import org.apache.commons.logging.*;

import java.io.IOException;
import java.io.File;


/**
 * User: ben
 * Date: Jun 12, 2008
 * Time: 3:01:18 PM
 */
public class ThemeDispatchFilter implements Filter {

    private final Log log = LogFactory.getLog(ThemeDispatchFilter.class);

    private ServletContext servletContext;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {

            String resourcePath = parseResourcePath(req);

            String targetUri = ThemeManager.resolveResource(resourcePath);

             // if resource does not exist return 404
            if(doesResourceExist(targetUri) == false) {
                log.warn("RESOURCE: " + targetUri + " DOES NOT EXIST \n REFERER: " + req.getHeader("Referer") + "\nUser-Agent: " + req.getHeader("User-Agent"));
                 resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Could not find: " + resourcePath);
                 return;
            }

            RequestDispatcher rd = req.getRequestDispatcher(targetUri);
            rd.include(req,resp);

        } catch(Throwable t) {
            handleError(t,req,resp);
        }
    }

    public String parseResourcePath(HttpServletRequest req) {

        String context = req.getContextPath();
        String uri = req.getRequestURI();

        if(context == null || context.trim().equals("")) {
            return uri;
        }

        return uri.substring(context.length());
    }


    public void handleError(Throwable e, HttpServletRequest req, HttpServletResponse resp) throws IOException {

         log.error("ERROR: " + req.getRequestURI(),e);
         e.printStackTrace(resp.getWriter());
    }

    public Boolean doesResourceExist(String targetUri) {
        String path = servletContext.getRealPath(targetUri);
        return new File(path).exists();
    }

    public void init(FilterConfig filterConfig) {
        this.servletContext = filterConfig.getServletContext();

    }

    public void destroy() { }

}