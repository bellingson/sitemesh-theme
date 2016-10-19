package nfjs.theme

import javax.servlet.ServletException
import javax.servlet.http.HttpUpgradeHandler

import static org.junit.Assert.*
import org.junit.Test

import org.springframework.mock.web.*

import javax.servlet.http.HttpServletResponse

import javax.servlet.RequestDispatcher

/**
 * User: ben
 * Date: Jan 26, 2009
 * Time: 1:26:56 PM
 */
class ThemeDispatchFilterTest extends BaseThemeTest {

    @Test void testSuccess() {

        AppTheme theme = ThemeManager.theme
        
        File homeJsp = new File("${webAppDir}/WEB-INF/jsp/theme/${theme.code}/home.jsp" )

        if(homeJsp.exists() == false) homeJsp << "<html><body>foo</body></html>"

        MockServletContext msc = new MockServletContext(webAppDirURI)
        MockFilterConfig mfc = new MockFilterConfig(msc)

        MockHttpServletRequest req = new MockHttpServletRequest(msc)

        MockHttpServletResponse resp = new MockHttpServletResponse()
        MockFilterChain fc = new MockFilterChain()

        String css = "/styles/theme.css"

        req.requestURI = css

        log.warn("R: ${req.requestURI} : ${req.servletPath} : ${req.contextPath}")

        ThemeDispatchFilter tdf = new ThemeDispatchFilter()
        tdf.init(mfc)

        tdf.doFilter(req,resp,fc)

        log.debug("R: ${resp.includedUrl}")

        assertTrue resp.includedUrl == "/WEB-INF/jsp/theme/${theme.code}${css}"

        // test the method
        tdf.destroy()

    }

    @Test void testParseResourcePath() {


        MockServletContext msc = new MockServletContext(webAppDirURI)
        MockFilterConfig mfc = new MockFilterConfig(msc)

        MockHttpServletRequest req = new MockHttpServletRequest(msc)

        req.requestURI = "/app1/index.jsp"
        req.contextPath = "/app1"

        ThemeDispatchFilter tdf = new ThemeDispatchFilter()

        String resourcePath = tdf.parseResourcePath(req)

        assertTrue resourcePath == "/index.jsp"
        
        req.contextPath = null

        resourcePath = tdf.parseResourcePath(req)

        assertTrue resourcePath == "/app1/index.jsp"

        req.contextPath = ""

        resourcePath = tdf.parseResourcePath(req)

        assertTrue resourcePath == "/app1/index.jsp"
        

    }



    @Test void test404() {

        AppTheme theme = ThemeManager.theme

        MockServletContext msc = new MockServletContext(new File(webAppDir).toURI().toString())
        MockFilterConfig mfc = new MockFilterConfig(msc)

        MockHttpServletRequest req = new MockHttpServletRequest(msc)

        MockHttpServletResponse resp = new MockHttpServletResponse()
        MockFilterChain fc = new MockFilterChain()

        req.requestURI = "/home${System.currentTimeMillis()}.jsp"

        ThemeDispatchFilter tdf = new ThemeDispatchFilter()
        tdf.init(mfc)

        tdf.doFilter(req,resp,fc)

        assertTrue resp.getStatus() == HttpServletResponse.SC_NOT_FOUND



    }

    @Test void testError() {

        File contextBase = new File(webAppDir)

        MockServletContext msc = new MockServletContext(contextBase.toURI().toString())
        MockFilterConfig mfc = new MockFilterConfig(msc)

        MockHttpServletResponse resp = new MockHttpServletResponse()
        MockHttpServletRequest req = new MockHttpServletRequest(msc) {

            public RequestDispatcher getRequestDispatcher(String path) {
                throw new Exception("test exception handling")
                return null
            }

            @Override
            def <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
                return null
            }
        }

        StringWriter out = new StringWriter()
        resp.writer = new PrintWriter(out)

        ThemeMockFilterChain fc = new ThemeMockFilterChain()

        req.requestURI = "/index.html"

        ThemeDispatchFilter tdf = new ThemeDispatchFilter()
        tdf.init(mfc)

        tdf.doFilter(req,resp,fc)

//        log.debug("FOO: " + out.toString())


    }




}
