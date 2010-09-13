package nfjs.theme;

import com.opensymphony.module.sitemesh.mapper.PathMapper;
import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Decorator;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * User: ben
 * Date: Aug 21, 2008
 * Time: 4:28:14 PM
 */
public class ThemeConfigLoader {

    private final Log log = LogFactory.getLog(ThemeConfigLoader.class);

    private Map [] decorators = null;
    private long configLastModified;

    private File configFile = null;
    private String configFileName = null;
    private PathMapper [] pathMapper = null;

    private Config config = null;

    // empty constructor
    public ThemeConfigLoader() { }

    /** Create new ConfigLoader using supplied File. */
    public ThemeConfigLoader(File configFile) throws ServletException {
        this.configFile = configFile;
        this.configFileName = configFile.getName();
        loadConfig();
    }

    /** Create new ConfigLoader using supplied filename and config. */
    public ThemeConfigLoader(String configFileName, Config config) throws ServletException {
        this.config = config;
        this.configFileName = configFileName;
        if (config.getServletContext().getRealPath(configFileName) != null) {
            this.configFile = new File(config.getServletContext().getRealPath(configFileName));
        }
        loadConfig();
    }

    /** Retrieve Decorator based on name specified in configuration file. */
    public Decorator getDecoratorByName(String name) throws ServletException {
        refresh();
        Theme theme = ThemeManager.getTheme();
        int mapOrdinal =  (int) (theme.getId() - 1);

        return (Decorator) ((Map)decorators[mapOrdinal]).get(name);
    }

    /** Get name of Decorator mapped to given path. */
    public String getMappedName(String path) throws ServletException {
        refresh();

        Theme theme = ThemeManager.getTheme();
        int mapOrdinal =  (int) (theme.getId() - 1);

        return pathMapper[mapOrdinal].get(path);
    }

    /** Load configuration from file. */
    private synchronized void loadConfig() throws ServletException {
        try {
            // Build a document from the file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = null;
            if (configFile != null && configFile.canRead()) {
                // Keep time we read the file to check if the file was modified
                configLastModified = configFile.lastModified();
                document = builder.parse(configFile);
            }
            else {
                document = builder.parse(config.getServletContext().getResourceAsStream(configFileName));
            }

            // Parse the configuration document
            parseConfig(document);
            
        } catch(Throwable e) {
            handleLoadError(configFileName,e);
        }


    }

    public void handleLoadError(String configFileName, Throwable e) throws ServletException {

        if(e instanceof ParserConfigurationException) {
            throw new ServletException("Could not get XML parser", e);
        }

        throw new ServletException("Could not read the config file: " + configFileName, e);
    }

    /** Parse configuration from XML document. */
    private synchronized void parseConfig(Document document) {

        initializeMaps();

        Element root = document.getDocumentElement();

        String defaultDir = parseDefaultDir(root);

        mapDecorators(root,defaultDir);

    }

    public String parseDefaultDir(Element root) {

        // get the default directory for the decorators
        String defaultDir = getAttribute(root, "defaultdir");
        if (defaultDir == null)
            defaultDir = getAttribute(root, "defaultDir");

        return defaultDir;
    }


    public void mapDecorators(Element root, String defaultDir) {

        // Get decorators
        NodeList decoratorNodes = root.getElementsByTagName("decorator");
        Element decoratorElement = null;

        for (int i = 0; i < decoratorNodes.getLength(); i++) {

            // get the current decorator element
            decoratorElement = (Element) decoratorNodes.item(i);
            parseDecoratorElement(decoratorElement, defaultDir);

        }


    }

    public void initializeMaps() {

        // Clear previous config
        List<Theme> themes = ThemeManager.getThemes();

        Theme lastTheme = (Theme) themes.get(themes.size() - 1);
        Integer mapSize = lastTheme.getId().intValue();

        decorators = new HashMap[mapSize];
        pathMapper = new PathMapper[mapSize];

        for(int i=0;i<mapSize;i++) {
            decorators[i] = new HashMap();
            pathMapper[i] = new PathMapper();
        }

    }


    private void parseDecoratorElement(Element decoratorElement, String defaultDir) {

        // The new format is used

        String page = getAttribute(decoratorElement, "page");
        String uriPath = getAttribute(decoratorElement, "webapp");

        page = formatPageValue(page,defaultDir);

        uriPath = formatUriPath(uriPath);

        createThemedDecorator(decoratorElement,page,uriPath);

    }

    public void createThemedDecorator(Element decoratorElement, String page, String uriPath) {

        // customize for the selected theme
        String code = getAttribute(decoratorElement,"theme");

        if(code == null) {          // apply to all themes

            List<Theme> themes = ThemeManager.getThemes();
            for(Theme theme: themes) {
               createDecorator(decoratorElement, theme,page,uriPath,false);
            }

        } else {   // apply to a single web app

            Theme theme = ThemeManager.getThemeByCode(code);
            createDecorator(decoratorElement, theme,page,uriPath,true);
        }


    }

    public String formatUriPath(String uriPath) {

        // The uriPath must begin with a slash
        if (uriPath != null && uriPath.length() > 0) {
            if (uriPath.charAt(0) != '/') {
                uriPath = '/' + uriPath;
            }
        }
        return uriPath;
    }

    public String formatPageValue(String page, String defaultDir) {

        // Append the defaultDir
        if (defaultDir != null && page != null && page.length() > 0) {
            if (page.charAt(0) == '/')
                page = defaultDir + page;
            else
                page = defaultDir + '/' + page;
        }
        return page;
    }



    private void createDecorator(Element decoratorElement, Theme theme, String page, String uriPath, boolean overRide ) {

        int mapOrdinal = (int) (theme.getId() - 1);
        Map themeDecorators = decorators[mapOrdinal];

        String name = getAttribute(decoratorElement, "name");
        
        Decorator v = (Decorator) themeDecorators.get(name);
        if(v != null && overRide == false) return; // do not override preset values

        String role = getAttribute(decoratorElement, "role");
        Map params = parseParams(decoratorElement);

        ThemeDecorator td = new ThemeDecorator(theme,name, page, uriPath, role, params);

        themeDecorators.put(td.getName(),td);

        PathMapper pm = pathMapper[mapOrdinal];
        populatePathMapper(pm, decoratorElement.getElementsByTagName("pattern"), role, name);


    }


    private Map parseParams(Element decoratorElement) {
        Map params = new HashMap();

        NodeList paramNodes = decoratorElement.getElementsByTagName("init-param");
        for (int ii = 0; ii < paramNodes.getLength(); ii++) {
            String paramName = getContainedText(paramNodes.item(ii), "param-name");
            String paramValue = getContainedText(paramNodes.item(ii), "param-value");
            params.put(paramName, paramValue);
        }

        return params;
    }


   /**
    * Extracts each URL pattern and adds it to the pathMapper map.
    */
   private void populatePathMapper(PathMapper pm, NodeList patternNodes, String role, String name) {
      for (int j = 0; j < patternNodes.getLength(); j++) {
          Element p = (Element)patternNodes.item(j);
          Text patternText = (Text) p.getFirstChild();
          if (patternText != null) {
             String pattern = patternText.getData().trim();
             if (pattern != null) {

                    if (role != null) {
                         // concatenate name and role to allow more
                         // than one decorator per role
                         pm.put(name + role, pattern);
                     }
                     else {
                         pm.put(name, pattern);
                     }

                 }

         }
      }
   }

   /** Override default behavior of element.getAttribute (returns the empty string) to return null. */
    private static String getAttribute(Element element, String name) {
        if (element != null && element.getAttribute(name) != null && element.getAttribute(name).trim() != "") {
            return element.getAttribute(name).trim();
        }
        else {
            return null;
        }
    }

    /**
     * With a given parent XML Element, find the text contents of the child element with
     * supplied name.
     */
    private static String getContainedText(Node parent, String childTagName) {
        try {
            Node tag = ((Element)parent).getElementsByTagName(childTagName).item(0);
            String text = ((Text)tag.getFirstChild()).getData();
            return text;
        }
        catch (Exception e) {
            return null;
        }
    }


    /** Check if configuration file has been updated, and if so, reload. */
    private synchronized void refresh() throws ServletException {
        if (configFile != null && configLastModified != configFile.lastModified()) loadConfig();
    }
}
