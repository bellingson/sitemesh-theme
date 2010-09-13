package nfjs.theme


import static org.junit.Assert.*

import org.apache.log4j.Logger
import org.apache.log4j.BasicConfigurator

import org.junit.Before
import org.apache.tools.ant.taskdefs.Delete

/**
 * User: ben
 * Date: Nov 6, 2008
 * Time: 12:38:46 PM
 */
class BaseThemeTest {

    protected Logger log = Logger.getLogger(this.class);

    static {

        System.out.println("initializing log4j")

        BasicConfigurator.configure()

    }

    private static ThemeManager themeManager

    @Before void setUp() {

        themeManager = getThemeManager()
        themeManager.setThemeId(1)

    }

    void deleteDir(String path) {

        File targetDir = new File(path)

        if(targetDir.exists()) {

            Delete deleteTask = new Delete()
            deleteTask.dir = new File(path)
            deleteTask.execute()

        }

        assertTrue targetDir.exists() == false

    }


    ThemeManager getThemeManager() {

         if(themeManager == null) {

             themeManager = new ThemeManager();

             List themes = []
             themes << new DefaultTheme(id:1,code:'foo',domainName:'test.foo.com:8080',homeUri:'/index.html',active:true)
             themes << new DefaultTheme(id:2,code:'bar',domainName:'www.test.bar.com:8080',homeUri:'/index.html',active:true)

             AppTheme appTheme = themes[0]
             appTheme.alternateDomains = [ "www.test.foo.com:8080" ]

             themeManager.setThemes(themes)
        }


        return themeManager
    }


    String getWebAppDir() {
         "build/webapp"
    }

    File getConfigFile() {

        //String configFileName = getClass().getResource("sitemesh.xml").file
        
        String configFileName = "${webAppDir}/WEB-INF/sitemesh.xml"
        File file = new File(configFileName)

        assertTrue(file.exists())
        
        return file
    }

    File getDecoratorsFile() {

        //String filePath = this.class.getResource("decorators.xml").file
        String filePath = "${webAppDir}/WEB-INF/decorators.xml"
        File file = new File(filePath)

        assertTrue(file.exists())

        return file
    }


    ThemeConfigLoader loadConfig() {
        return new ThemeConfigLoader(decoratorsFile)

    }



}
