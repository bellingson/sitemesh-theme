package nfjs.theme



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
        
        File homeJsp = new File("${webAppDir}/WEB-INF/theme/${theme.code}/home.jsp" )

        if(homeJsp.exists() == false) homeJsp << "<html><body>foo</body></html>"

        MockServletContext msc = new MockServletContext(new File(webAppDir).toURI().toString())
        MockFilterConfig mfc = new MockFilterConfig(msc)

        MockHttpServletRequest req = new MockHttpServletRequest(msc)

        MockHttpServletResponse resp = new MockHttpServletResponse()
        MockFilterChain fc = new MockFilterChain()

        String css = "/styles/theme.css"

        req.requestURI = css

        ThemeDispatchFilter tdf = new ThemeDispatchFilter()
        tdf.init(mfc)

        tdf.doFilter(req,resp,fc)

        log.debug("R: ${resp.includedUrl}")

        assertTrue resp.includedUrl == "/WEB-INF/theme/${theme.code}${css}"

        // test the method
        tdf.destroy()


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
