package sample

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent

import nfjs.theme.ThemeManager
import nfjs.theme.DefaultTheme

import org.apache.log4j.Logger


/**
 * Created by benellingson@yahoo.com
 *
 * This is a simple ServletContextListener that will initialize a
 * ThemeManager instance with simple groovy code.  In a production
 * app with many themes and test vs production settings, you probably
 * want to configure the AppThemes using Spring or another object factory
 * mechanism.
 *
 * Date: Sep 17, 2010 3:36:33 PM
 */
class SimpleAppThemeListener implements ServletContextListener {

    protected Logger log = Logger.getLogger(this.class);

    void contextInitialized(ServletContextEvent servletContextEvent) {

          log.debug("initializing sample app")

          ThemeManager themeManager = new ThemeManager()
          def app1 = new DefaultTheme(id:1,code:'app1',name:'App Theme 1',domainName:'app1.localhost:8080',homeUri:'/index.jsp',active:true)
          def app2 = new DefaultTheme(id:2,code:'app2',name:'App Theme 2',domainName:'app2.localhost:8080',homeUri:'/index.jsp',active:true)
          themeManager.themes = [ app1, app2 ]

          log.debug("INITIALIZED SAMPLE THEME APP\n BE SURE TO ADD: ${app1.domainName} and ${app2.domainName} to /etc/hosts file")

    }

    void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
