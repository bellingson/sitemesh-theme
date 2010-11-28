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

    private static List<AppTheme> themes;

    private static Map domainThemeMap;
    private static Map themeMap;

    private static ThreadLocal themeHolder = new ThreadLocal();


    public static AppTheme setDomain(String domainName) {

        AppTheme appTheme = (AppTheme) domainThemeMap.get(domainName);

        if(appTheme == null) {
            LogFactory.getLog(ThemeManager.class).warn("Unrecognized domain: " + domainName);
            appTheme = (AppTheme) themes.get(0);
        }

        themeHolder.set(appTheme);

        return appTheme;
    }

    public static AppTheme setThemeId(Long themeId) {

        if(themeId == null) return null;

        AppTheme appTheme = (AppTheme) themeMap.get(themeId.toString());
        if(appTheme == null) throw new IllegalStateException("Missing app for id: " + themeId);
        themeHolder.set(appTheme);

        return appTheme;
    }


    public static AppTheme getTheme() {
        AppTheme appTheme = (AppTheme) themeHolder.get();
        if(appTheme == null) {
            LogFactory.getLog(ThemeManager.class).error("ERROR: theme not set",new Exception("Theme not Set"));
            throw new IllegalStateException("Theme has not been set");
        }
        return appTheme;
    }

    public static AppTheme getThemeByCode(String code) {

        for(AppTheme appTheme : themes) {
            if(appTheme.getCode().equals(code)) { return appTheme; }
        }
        return null;

    }

    public static AppTheme getThemeById(Long id) {
        if(id == null) return null;
        return (AppTheme) themeMap.get(id.toString());
    }


    public static void clear() {
        themeHolder.set(null);
    }

    public void setThemes(List themes) {
        ThemeManager.themes = themes;

        init(themes);
    }

    public static List<AppTheme> getThemes() {
        return ThemeManager.themes;
    }

    public static String resolveResource(String name) {
        return "/WEB-INF/theme/" + getTheme().getCode() + name;
    }


    private void init(List<AppTheme> themes) {

        themeMap = new FastHashMap();
        domainThemeMap = new FastHashMap();

        for(AppTheme theme : themes) {

            addAlternativeDomains(theme);

            themeMap.put(theme.getId().toString(),theme);

            domainThemeMap.put(theme.getDomainName(),theme);

            for(String domainName : theme.getAlternateDomains()) {
                domainThemeMap.put(domainName,theme);
            }

        }

    }


    public void addAlternativeDomains(AppTheme theme) {

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

		// if domain has a port add alt domain without port
		int x = domainName.indexOf(":");
		if(x != -1) {
			alternateDomains.add(domainName.substring(0,x));
		}

    }

    public static String delimitAbreviations(Collection<AppTheme> themes) {

        StringBuffer s = new StringBuffer();
        Iterator i = themes.iterator();
        while(i.hasNext()) {
            AppTheme theme = (AppTheme) i.next();
            s.append(theme.getCode());
            if(i.hasNext())
                s.append(",");
        }

        return s.toString();
    }


}