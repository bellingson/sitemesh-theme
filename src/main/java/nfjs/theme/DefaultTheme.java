package nfjs.theme;

import java.util.List;
import java.util.Map;

/**
 * Created by benellingson@yahoo.com
 * Date: Jan 5, 2010 4:17:22 PM
 */
public class DefaultTheme implements AppTheme {

    protected Long id;
    protected String name;
    protected String domainName;
    protected String code;
    protected String homeUri;
    protected Boolean active = true;

    protected List<String> alternateDomains;
    protected Map properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHomeUri() {
        return homeUri;
    }

    public void setHomeUri(String homeUri) {
        this.homeUri = homeUri;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Map getProperties() {
       return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public List<String> getAlternateDomains() {
        return alternateDomains;
    }

    public void setAlternateDomains(List<String> alternateDomains) {
        this.alternateDomains = alternateDomains;
    }

    public Boolean getShouldRedirectHomeUri() { return true; }

    public String getRedirectTo() { return null; }

}
