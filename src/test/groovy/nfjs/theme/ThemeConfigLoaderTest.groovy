package nfjs.theme


import javax.xml.parsers.DocumentBuilderFactory

import static org.junit.Assert.*
import org.junit.Test


import com.opensymphony.module.sitemesh.Decorator
import javax.xml.parsers.ParserConfigurationException
import javax.servlet.ServletException
import org.w3c.dom.Element

/**
 * User: ben
 * Date: Nov 6, 2008
 * Time: 12:09:10 PM
 */
public class ThemeConfigLoaderTest extends BaseThemeTest {

    @Test void testInitialization() {

        ThemeConfigLoader tcl = new ThemeConfigLoader(decoratorsFile)

        ThemeManager.setThemeId(1)
        AppTheme theme = ThemeManager.theme

        Decorator d = tcl.getDecoratorByName("home")

        assertTrue d != null

        assertTrue d.page == "/WEB-INF/theme/${theme.code}/decorators/home.jsp"

        ThemeManager.setThemeId(2)
        theme = ThemeManager.theme

        d = tcl.getDecoratorByName("home")

        assertTrue d != null

        assertTrue d.page == "/WEB-INF/theme/${theme.code}/decorators/home_bar.jsp"


    }


    @Test(expected=ServletException.class) void testHandleLoadErrorParserConfigurationException() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()
        tcl.handleLoadError("foo.bar",new ParserConfigurationException("test exception"))
    }

    @Test(expected=ServletException.class) void testHandleLoadErrorException() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()
        tcl.handleLoadError("foo.bar",new Exception("test exception")) 
    }

    @Test(expected=ServletException.class) void testNoConfigFileError() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()
        tcl.loadConfig() 
    }


    @Test void testLoad() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()
        tcl.configFile = decoratorsFile
        tcl.loadConfig()
    }

    @Test void testRefresh() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()

        tcl.refresh()

        tcl.configFile = decoratorsFile
        tcl.configLastModified = System.currentTimeMillis() - 20000

        decoratorsFile.setLastModified(System.currentTimeMillis())

        tcl.refresh()

        assertTrue tcl.decorators != null

    }

    @Test void testParseDefaultDir() {


        def document = stringToXmlDocument("<decorators defaultdir='/decorators'></decorators>")

        ThemeConfigLoader tcl = new ThemeConfigLoader()
        String dir = tcl.parseDefaultDir(document.documentElement)

        assertTrue dir == '/decorators'

        document = stringToXmlDocument("<decorators defaultDir='/decorators'></decorators>")
        dir = tcl.parseDefaultDir(document.documentElement)

        assertTrue dir == '/decorators' 

    }

/*
    @Test void testParseDecoratorElement() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()
        tcl.decorators = new HashMap[2]

        String docString = """<decorators defaultdir='/decorators'>
                                <decorator name="home" page="home.jsp">
                                    <pattern>/home.jsp</pattern>
                                </decorator>
                                <decorator name="home" page="home_bar.jsp" theme="bar">
                                    <pattern>/home.jsp</pattern>
                                </decorator>
                              </decorators>"""

        def document = stringToXmlDocument(docString)
        Element root = document.documentElement

        def decoratorNodes = root.getElementsByTagName("decorator");
        def decoratorElement = decoratorNodes.item(0);

        tcl.parseDecoratorElement(decoratorElement, "/decorators");

        decoratorElement = decoratorNodes.item(1);

        tcl.parseDecoratorElement(decoratorElement, "/decorators");

    }
*/    

    @Test void testFormatUriPath() {

        ThemeConfigLoader tcl = new ThemeConfigLoader()

        assertTrue tcl.formatUriPath(null) == null

        assertTrue tcl.formatUriPath("") == ""

        assertTrue tcl.formatUriPath("foo") == "/foo"

        assertTrue tcl.formatUriPath("/foo") == "/foo"
    }

    @Test void testFormatPageValue() {

         ThemeConfigLoader tcl = new ThemeConfigLoader()

        assertTrue tcl.formatPageValue("foo.jsp",null) == "foo.jsp"
        
        assertTrue tcl.formatPageValue(null,"/decorators") == null
        assertTrue tcl.formatPageValue("","/decorators") == ""


        assertTrue tcl.formatPageValue("foo.jsp","/decorators") == "/decorators/foo.jsp"

        log.warn(tcl.formatPageValue("/foo.jsp","/decorators"))

        assertTrue tcl.formatPageValue("/foo.jsp","/decorators") == "/decorators/foo.jsp"


    }

    @Test void testParseParams() {

        String docString = """<decorators defaultdir='/decorators'>
                                <decorator name="home" page="home.jsp">
                                    <pattern>/home.jsp</pattern>
                                    <init-param>
                                        <param-name>foo</param-name>
                                        <param-value>bar</param-value>
                                    </init-param>
                                </decorator>
                                <decorator name="home" page="home_bar.jsp" theme="bar">
                                    <pattern>/home.jsp</pattern>
                                </decorator>
                              </decorators>"""

        def document = stringToXmlDocument(docString)
        Element root = document.documentElement

        def decoratorNodes = root.getElementsByTagName("decorator");
        def decoratorElement = decoratorNodes.item(0);

         ThemeConfigLoader tcl = new ThemeConfigLoader()

         Map params = tcl.parseParams(decoratorElement)

        assertTrue params.foo == 'bar'

    }

    @Test void testGetAttribute() {

        String docString = "<foo value='bar '></foo>"

        def doc = stringToXmlDocument(docString)

        ThemeConfigLoader tcl = new ThemeConfigLoader()

        Element e = doc.documentElement

        assertTrue tcl.getAttribute(e,'value') == 'bar'

        assertTrue tcl.getAttribute(null,'value') == null
        assertTrue tcl.getAttribute(null,'invalid') == null
        assertTrue tcl.getAttribute(e,'') == null

    }

        @Test void testGetContainedText() {

        String docString = "<document><foo>bar</foo></document>"

        def doc = stringToXmlDocument(docString)

        ThemeConfigLoader tcl = new ThemeConfigLoader()

        Element e = doc.documentElement

        assertTrue tcl.getContainedText(e,'foo') == 'bar'
        assertTrue tcl.getContainedText(e,'bar') == null
        assertTrue tcl.getContainedText(null,'foo') == null

    }




    def stringToXmlDocument = { string ->

        def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def inputStream = new ByteArrayInputStream(string.bytes)
        return builder.parse(inputStream)
    }



}
