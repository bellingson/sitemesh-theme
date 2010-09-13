package nfjs.theme

import org.junit.Test
import static org.junit.Assert.*

/**
 * Created by benellingson@yahoo.com
 * Date: Sep 11, 2010 8:01:11 PM
 */
class DefaultThemeTest {


    @Test void testTheme() {

         DefaultTheme theme = new DefaultTheme(id:1,name:'test',domainName:'localhost:8080',active:true)
         theme.properties = ['foo':'bar']

        assertTrue theme.id == 1
        assertTrue theme.name == 'test'
        assertTrue theme.domainName == 'localhost:8080'

        assertTrue theme.active

        assertTrue theme.properties.foo == 'bar'

        assertTrue theme.isActive()

    }

}
