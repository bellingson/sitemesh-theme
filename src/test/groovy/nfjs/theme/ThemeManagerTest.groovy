package nfjs.theme

import org.junit.Test
import static org.junit.Assert.*

/**
 * Created by benellingson@yahoo.com
 * Date: Sep 13, 2010 1:24:30 AM
 */
class ThemeManagerTest extends BaseThemeTest {



    @Test void testSetDomain() {


        Theme theme = ThemeManager.theme

        Theme theme2 = ThemeManager.setDomain(theme.domainName)

        assertTrue theme == theme2

        ThemeManager.setThemeId(2)

        Theme theme3 = ThemeManager.setDomain("notreal.com")

        assertTrue theme3 == theme
        

    }

    @Test(expected=IllegalStateException) void testSetThemeId() {


        assertTrue ThemeManager.setThemeId(null) == null


        ThemeManager.setThemeId(4)
    }


    @Test(expected=IllegalStateException) void testGetTheme() {

         ThemeManager.clear()

         ThemeManager tm = new ThemeManager()
         tm.getTheme()
        
    }


    @Test void testGetThemeByCode() {

        Theme theme = ThemeManager.theme

        Theme theme2 = ThemeManager.getThemeByCode(theme.code)

        assertTrue theme == theme2

         Theme theme3 = ThemeManager.getThemeByCode("fake")

        assertTrue theme3 == null

    }

    @Test void testGetThemeById() {

        Theme theme = ThemeManager.theme

        assertTrue ThemeManager.getThemeById(null) == null 

        Theme theme2 = ThemeManager.getThemeById(theme.id)

        assertTrue theme == theme2

    }

    @Test void addAlternativeDomains() {


          Theme theme = ThemeManager.theme

          assertTrue theme.alternateDomains.empty == false

          themeManager.addAlternativeDomains(theme)

          //log.error("FOO: ${theme.alternateDomains}")

          assertTrue theme.alternateDomains.contains("www.test.foo.com:8080")

    }

    @Test void testDelimitAbreviations() {

        List themes = ThemeManager.themes
        String str = ThemeManager.delimitAbreviations(themes)

        assertTrue str == 'foo,bar'

    }

}
