
package nfjs.theme;


import java.util.List;
import java.util.Map;

/**
 * User: benellingson@yahoo.com
 */



public interface AppTheme {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getDomainName();

    public void setDomainName(String domainName);

    public String getCode();

    public void setCode(String abreviation);

    public String getHomeUri();

    public void setHomeUri(String homeUri);

    public Boolean isActive();

    public void setActive(Boolean active);

    public Map getProperties();

    public void setProperties(Map properties);

    public List<String> getAlternateDomains();

    void setAlternateDomains(List<String> alternateDomains);


}
