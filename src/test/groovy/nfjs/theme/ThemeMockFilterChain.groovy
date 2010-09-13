package nfjs.theme

import org.springframework.mock.web.MockFilterChain
import javax.servlet.ServletResponse
import javax.servlet.ServletRequest

/**
 * Created by benellingson@yahoo.com
 * Date: Sep 12, 2010 8:11:04 PM
 */
class ThemeMockFilterChain extends MockFilterChain {

    Boolean called = false

    Throwable error

    ThemeMockFilterChain() { }

    ThemeMockFilterChain(Throwable error) {
        this.error = error
    }


    void doFilter(ServletRequest request, ServletResponse response)  {
        if(error != null) throw error
        called = true
        super.doFilter(request,response)
    }



}
