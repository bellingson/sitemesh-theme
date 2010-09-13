package nfjs.theme.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSet;

import java.io.File;


/**
 * Created by benellingson@yahoo.com
 * Date: Feb 12, 2010 2:42:50 PM
 */
public class ThemeDeployTask extends org.apache.tools.ant.Task {

    Boolean overwrite = false;

    String sourcePath;
    String themePath = "WEB-INF/theme";
    String targetPath;

    //String includes = "**/*.jsp,**/*.css,**/*.html";
    String includes;
    String excludes;

    String filterIncludes;

    String themeNames;
    String devApp;

    Project p;

    public void execute() {

        FilterSet filterSet = p.getGlobalFilterSet();

        String [] themes = themeNames.split(",");

        for(String theme : themes) {

             filterSet.addFilter("theme.code",theme);

             String parent = p.getProperty("theme." + theme + ".parent");
             deployTheme(theme,parent);
        }

        copyWebSourceFiles();

        
    }

    public void init() {

        super.init();

        p = getProject();

        if(devApp != null)
            themeNames = devApp;
        else if(themeNames == null)
            themeNames = p.getProperty("theme.names");

    }

    protected void deployTheme(String theme, String parent) {

        log("DEPLOY: " + parent + "->" + theme);

        copyThemeSourceFiles(theme,parent);

    }


    protected void copyWebSourceFiles() {

        String source = resolvePath(sourcePath);
        String target = resolvePath(targetPath);
        String themeBase = "**/" + themePath + "/**";

        log(source + "\n" + target + "\n" + themeBase);

        doCopy(source,target,null,themeBase,false);

    }

    protected void copyThemeSourceFiles(String theme, String parent) {

        String themeTarget = resolvePath(targetPath + "/" + themePath  + "/" + theme);

        if(parent != null) {
            String parentSource = resolvePath(sourcePath + "/" + themePath + "/" + parent);
            copyThemeSourceDir(parentSource,themeTarget,false);
        }

        String themeSource = resolvePath(sourcePath + "/" + themePath + "/" + theme);
        copyThemeSourceDir(themeSource,themeTarget,true);


    }

    protected void copyThemeSourceDir(String source, String target, Boolean overwrite) {

        log("copy: " + source + " to " + target);

        if(filterIncludes != null) {

           log("filtering files: " + filterIncludes);

           doCopy(source,target, filterIncludes,null,overwrite);
           doCopy(source,target,null, filterIncludes,overwrite);

        } else {
           doCopy(source,target,includes,null,overwrite);
        }


    }


    String resolvePath(String path) {
        if(path.startsWith("/")) return path;
        return p.getBaseDir() + "/" + path;
    }


    void doCopy(String srcdir, String todir, String includes, String excludes, Boolean overwrite) {

        File todirF = new File(todir);
        File srcdirF = new File(srcdir);

        log(todirF.getAbsolutePath());

        todirF.mkdirs();

        Copy copy = new Copy();
        copy.setProject(getProject());
        copy.setFiltering(true);
        copy.setTodir(todirF);
        copy.setOverwrite(overwrite);
                                                
        FileSet fileSet = new FileSet();
        fileSet.setDir(srcdirF);
        if(includes != null)
            fileSet.setIncludes(includes);
        if(excludes != null)
            fileSet.setExcludes(excludes);

        copy.addFileset(fileSet);
        copy.execute();
        
    }

    



    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setThemePath(String themePath) {
        this.themePath = themePath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setThemeNames(String themeNames) {
        this.themeNames = themeNames;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public void setFilterIncludes(String filterIncludes) {
        this.filterIncludes = filterIncludes;
    }

    public void setDevApp(String devApp) {
        this.devApp = devApp;
    }

}
