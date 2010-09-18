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


}
