package nfjs.theme

import static org.junit.Assert.*
import org.junit.Test

import org.springframework.mock.web.MockFilterConfig
import org.springframework.mock.web.MockServletContext
import org.springframework.mock.web.MockHttpServletRequest
import com.opensymphony.module.sitemesh.Config
import com.opensymphony.module.sitemesh.Page
import com.opensymphony.module.sitemesh.Decorator
import com.opensymphony.module.sitemesh.parser.FastPage

import org.junit.Before
import javax.servlet.ServletException
import com.opensymphony.module.sitemesh.mapper.ConfigDecoratorMapper
import javax.servlet.http.HttpServletRequest

/**
 * User: ben
 * Date: Nov 6, 2008
 * Time: 12:38:18 PM
 */
public class ThemeDecoratorMapperTest extends BaseThemeTest {


    static ThemeDecoratorMapper decoratorMapper

    @Before void setUp()  {

        super.setUp()

        if(decoratorMapper == null) {

           decoratorMapper = setupThemeDecoratorMapper()

        }

    }

    ThemeDecoratorMapper setupThemeDecoratorMapper() {


        File decoratorsFile = getDecoratorsFile()

        MockResourceLoader rl = new MockResourceLoader()
        MockServletContext msc = new MockServletContext(rl)
        MockFilterConfig fc = new MockFilterConfig(msc)

        Config config = new Config(fc)

        ThemeDecoratorMapper tdm = new ThemeDecoratorMapper()

        Properties siteMeshProp = new Properties()
        siteMeshProp.put("config",decoratorsFile.getAbsolutePath())

        tdm.init(config, siteMeshProp,null)

        return tdm
    }

    @Test void testGetDecorator() {
        

        Page page = new FastPage(null,null,null,null,"foo","foo","foo",false)

        MockHttpServletRequest req = new MockHttpServletRequest()
        req.setRequestURI("/home.jsp")
        req.setServletPath(null)

        ThemeManager.setThemeId(1)

        Decorator d = decoratorMapper.getDecorator(req,page)

        assertTrue(d != null)

        log.debug("D: ${d.page}")

        assertTrue(d.page.endsWith('home.jsp'))

        ThemeManager.setThemeId(2)

        d = decoratorMapper.getDecorator(req,page)

        assertTrue(d != null)
        assertTrue(d.page.endsWith("home_bar.jsp"))        

        ThemeManager.setThemeId(1)

    }

    @Test void testGetDecorator2() {


        Page page = new FastPage(null,null,null,null,"foo","foo","foo",false)

        MockHttpServletRequest req = new MockHttpServletRequest()
        req.requestURI = null
        req.servletPath = null

        Decorator d = decoratorMapper.getNamedDecorator(req,"home")

        // hacky test coverage code ... not really needed ?
              decoratorMapper.parent = new ConfigDecoratorMapper() {
                  public Decorator getNamedDecorator(HttpServletRequest request, String name) {
                       return d
                  }
              }
        

        Decorator d2 = decoratorMapper.getDecorator(req,page)

        assertTrue d2 == d

    }



    @Test void testParsePath() {

         MockHttpServletRequest req = new MockHttpServletRequest()
         req.servletPath = "/home.jsp"
         req.pathInfo = "foo"
         //req.requestURI = "/home.jsp"

         ThemeDecoratorMapper tdm = new ThemeDecoratorMapper()
         tdm.configLoader = new ThemeConfigLoader()

         String path = tdm.parsePath(req)

         assertTrue path == "/home.jsp"


         req.servletPath = ""
         req.pathInfo = "foo"

         path = tdm.parsePath(req)

        // log.error("PATH: ${path}")

         assertTrue path == "foo"

         req.servletPath = null
         req.requestURI = "foobarfoo"
         req.pathInfo = "bar"

         path = tdm.parsePath(req)

         //log.error("PATH: ${path}")

         assertTrue path == "foo"



    }

    @Test void testGetMappedName() {

       ThemeDecoratorMapper tdm = new ThemeDecoratorMapper()

        tdm.configLoader = new ThemeConfigLoader() {
            String getMappedName(String name) {
                throw new ServletException("foo")
            }
            
        }
        assertTrue tdm.getMappedName('foo') == null

        loadConfig()

        assertTrue decoratorMapper.getMappedName('home.jsp') == null 

    }

    @Test void testGetNamedDecorator() {

          MockHttpServletRequest req = new MockHttpServletRequest()

          Decorator decorator = decoratorMapper.getNamedDecorator(req,"home")

          assertTrue decorator != null

          req.requestURI = "/home.jsp"
          req.pathInfo = "home"


           // hacky test coverage code ... not really needed ?
          decoratorMapper.parent = new ConfigDecoratorMapper() {
              public Decorator getNamedDecorator(HttpServletRequest request, String name) {
                   return decorator
              }
          }

          Decorator decorator2 = decoratorMapper.getNamedDecorator(req,"foo")

          assertTrue decorator == decorator2 
           
       }


       @Test void testGetNamedDecoratorServletException() {

           MockHttpServletRequest req  = new MockHttpServletRequest()

           Decorator decorator = decoratorMapper.getNamedDecorator(req,"home")

           ThemeDecoratorMapper tdm = new ThemeDecoratorMapper()
           tdm.parent = new ConfigDecoratorMapper() {
              public Decorator getNamedDecorator(HttpServletRequest request, String name) {
                   return decorator
              }
          }

           tdm.configLoader = new ThemeConfigLoader() {
                 public Decorator getDecoratorByName(String name) {
                    throw new ServletException("test exception")
                 }
           }

           tdm.getNamedDecorator(req,"foo")

       }


    
       @Test(expected=InstantiationException) void testInitError() {

            File decoratorsFile = new File("/WEB-INF/decorators_foo.xml")

            MockResourceLoader rl = new MockResourceLoader()
            MockServletContext msc = new MockServletContext(rl)
            MockFilterConfig fc = new MockFilterConfig(msc)

            Config config = new Config(fc)

            ThemeDecoratorMapper tdm = new ThemeDecoratorMapper()

            Properties siteMeshProp = new Properties()
            siteMeshProp.put("config",decoratorsFile.getAbsolutePath())

            tdm.init(config, siteMeshProp,null)

       }
}
