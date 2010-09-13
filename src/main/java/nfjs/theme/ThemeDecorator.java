package nfjs.theme;

import com.opensymphony.module.sitemesh.mapper.DefaultDecorator;

import java.util.Map;


/**
 * User: ben
 * Date: Oct 31, 2008
 * Time: 4:56:32 PM
 */
public class ThemeDecorator extends DefaultDecorator {

    public ThemeDecorator(AppTheme appTheme, String name, String page, String uriPath, String role, Map parameter) {
        super(name,page,uriPath,role,parameter);
        this.page =  "/WEB-INF/theme/" + appTheme.getCode() + "/decorators/" + page;
    }

    
}

