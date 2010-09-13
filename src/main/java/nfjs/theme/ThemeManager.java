package nfjs.theme;


import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.LogFactory;


import java.util.*;

/**
 * User: benellingson@yahoo.com
 * Date: Jun 12, 2008
 * Time: 2:46:49 PM
 */
public class ThemeManager {

    private static List<Theme> themes;

    private static Map domainThemeMap;
    private static Map themeMap;

    private static ThreadLocal themeHolder = new ThreadLocal();


    public static Theme setDomain(String domainName) {

        Theme theme = (Theme) domainThemeMap.get(domainName);

        if(theme == null) {
            LogFactory.getLog(ThemeManager.class).warn("Unrecognized domain: " + domainName);
            theme = (Theme) themes.get(0);
        }

        themeHolder.set(theme);

        return theme;
    }

    public static Theme setThemeId(Long themeId) {

        if(themeId == null) return null;

        Theme theme = (Theme) themeMap.get(themeId.toString());
        if(theme == null) throw new IllegalStateException("Missing app for id: " + themeId);
        themeHolder.set(theme);

        return theme;
    }


    public static Theme getTheme() {
        Theme theme = (Theme) themeHolder.get();
        if(theme == null) {
            LogFactory.getLog(ThemeManager.class).error("ERROR: theme not set",new Exception("Theme not Set"));
            throw new IllegalStateException("Theme has not been set");
        }
        return theme;
    }

    public static Theme getThemeByCode(String code) {

        for(Theme theme : themes) {
            if(theme.getCode().equals(code)) { return theme; }
        }
        return null;

    }

    public static Theme getThemeById(Long id) {
        if(id == null) return null;
        return (Theme) themeMap.get(id.toString());
    }


    public static void clear() {
        themeHolder.set(null);
    }

    public void setThemes(List themes) {
        ThemeManager.themes = themes;

        init(themes);
    }

    public static List<Theme> getThemes() {
        return ThemeManager.themes;
    }

    public static String resolveResource(String name) {
        return "/WEB-INF/theme/" + getTheme().getCode() + name;
    }


    private void init(List<Theme> themes) {

        themeMap = new FastHashMap();
        domainThemeMap = new FastHashMap();

        for(Theme theme : themes) {

            addAlternativeDomains(theme);

            themeMap.put(theme.getId().toString(),theme);

            domainThemeMap.put(theme.getDomainName(),theme);

            for(String domainName : theme.getAlternateDomains()) {
                domainThemeMap.put(domainName,theme);
            }

        }

    }


    public void addAlternativeDomains(Theme theme) {

        List alternateDomains = theme.getAlternateDomains();

        if(alternateDomains == null) {
            alternateDomains = new ArrayList();
            theme.setAlternateDomains(alternateDomains);
        }

        String domainName = theme.getDomainName();

        if(domainName.startsWith("www.")) {
            alternateDomains.add(domainName.substring(4));
        } else {
            alternateDomains.add("www." + domainName);
        }

    }

    public static String delimitAbreviations(Collection<Theme> themes) {

        StringBuffer s = new StringBuffer();
        Iterator i = themes.iterator();
        while(i.hasNext()) {
            Theme theme = (Theme) i.next();
            s.append(theme.getCode());
            if(i.hasNext())
                s.append(",");
        }

        return s.toString();
    }


}