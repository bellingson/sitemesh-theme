package nfjs.theme


import static org.junit.Assert.*
import org.junit.Test

import org.springframework.mock.web.*

/**
 * User: ben
 * Date: Jan 27, 2009
 * Time: 2:22:33 PM
 */
class ThemeIdentityFilterTest extends BaseThemeTest {

    @Test void testThemeIdentity() {

        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        MockHttpServletResponse resp = new MockHttpServletResponse()
        MockFilterChain fc = new MockFilterChain()

        req.addHeader("host",theme.domainName)

        assertTrue req.getAttribute("appTheme") == null

        ThemeIdentityFilter tif = new ThemeIdentityFilter()

        tif.init(null)

        tif.doFilter(req,resp,fc)

        assertTrue req.getAttribute("appTheme") == theme

        tif.destroy()

    }

    @Test void testError() {

        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        MockHttpServletResponse resp = new MockHttpServletResponse()

        ThemeMockFilterChain fc = new ThemeMockFilterChain()

        req.addHeader("host",theme.domainName)

        assertTrue req.getAttribute("appTheme") == null

        ThemeIdentityFilter tif = new ThemeIdentityFilter()
        tif.doFilter(req,resp,fc)

        assertTrue fc.called == true

    }

    @Test void testCatchError() {

        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        MockHttpServletResponse resp = new MockHttpServletResponse()

        ThemeMockFilterChain fc = new ThemeMockFilterChain(new Exception("test exception"))

        req.addHeader("host",theme.domainName)

        assertTrue req.getAttribute("appTheme") == null

        ThemeIdentityFilter tif = new ThemeIdentityFilter()
        tif.doFilter(req,resp,fc)

        assertTrue fc.called == false

    }



    @Test void testHomeUri() {

        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        MockHttpServletResponse resp = new MockHttpServletResponse()

        ThemeMockFilterChain fc = new ThemeMockFilterChain()

        req.addHeader("host",theme.domainName)

        req.requestURI = '/'

        assertTrue req.getAttribute("appTheme") == null

        ThemeIdentityFilter tif = new ThemeIdentityFilter()
        tif.doFilter(req,resp,fc)

        assertTrue resp.redirectedUrl != null
        assertTrue resp.redirectedUrl == theme.homeUri



    }

     @Test void testHomeUriWithContextPath() {

        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        MockHttpServletResponse resp = new MockHttpServletResponse()

        ThemeMockFilterChain fc = new ThemeMockFilterChain()

        req.addHeader("host",theme.domainName)

        req.requestURI = '/context/'
        req.contextPath = '/context'

        assertTrue req.getAttribute("appTheme") == null

        ThemeIdentityFilter tif = new ThemeIdentityFilter()
        tif.doFilter(req,resp,fc)

        assertTrue resp.redirectedUrl != null
        assertTrue resp.redirectedUrl == "/context${theme.homeUri}"

    }

    @Test void testFormatHomeUri() {

        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        req.contextPath = '/context'

        assertTrue req.getAttribute("appTheme") == null

        ThemeIdentityFilter tif = new ThemeIdentityFilter()

        String homeUri = tif.formatHomeURI(req,theme)

        assertTrue homeUri == "/context${theme.homeUri}"

        // test with empty contextPath

        req.contextPath = null

        homeUri = tif.formatHomeURI(req,theme)

        assertTrue homeUri == theme.homeUri

        req.contextPath = ''

        homeUri = tif.formatHomeURI(req,theme)

        assertTrue homeUri == theme.homeUri

    }

    @Test void testIsHomeURI() {


        AppTheme theme = ThemeManager.theme

        MockHttpServletRequest req = new MockHttpServletRequest()
        req.contextPath = '/context'
        req.requestURI = '/context/'

        ThemeIdentityFilter tif = new ThemeIdentityFilter()

        assertTrue tif.isHomeURI(req,req.requestURI)

        req.requestURI = '/context/about.jsp'

        assertTrue tif.isHomeURI(req,req.requestURI) == false

        // test null context path
        req.contextPath = null
        req.requestURI = '/'

        assertTrue tif.isHomeURI(req,req.requestURI)

        req.requestURI = '/about.jsp'

        assertTrue tif.isHomeURI(req,req.requestURI) == false

        req.contextPath = ''
        req.requestURI = '/'

        assertTrue tif.isHomeURI(req,req.requestURI)

        req.requestURI = '/about.jsp'

        assertTrue tif.isHomeURI(req,req.requestURI) == false



        
    }



}
