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

             filterSet.addFilter("appTheme.code",theme);
             filterSet.addFilter("theme.code",theme);

			 String [] parents = null;
			 String parentsString = p.getProperty("appTheme." + theme + ".parent");
			
			 if(parentsString != null)
   			    parents = parentsString.split(",");

			 if(parents != null) {
				 for(String parent : parents)
	             	deployTheme(theme,parent);
				
  			  } else {
 					deployTheme(theme,null);
			 }

        }

        copyWebSourceFiles();

        
    }

    public void init() {

        super.init();

        p = getProject();

        if(devApp != null)
            themeNames = devApp;
        else if(themeNames == null)
            themeNames = p.getProperty("appTheme.names");

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
            copyThemeSourceDir(parentSource,themeTarget,true);
        }

        String themeSource = resolvePath(sourcePath + "/" + themePath + "/" + theme);
        copyThemeSourceDir(themeSource,themeTarget,true);


    }

    protected void copyThemeSourceDir(String source, String target, Boolean overwrite) {

        log("copy: " + source + " to " + target);

        if(filterIncludes != null) {

           log("filtering files: " + filterIncludes);

           doCopyWithFiltering(source,target, filterIncludes,null,overwrite);
           doCopyWithFiltering(source,target,null, filterIncludes,overwrite);

        } else {
           doCopyWithFiltering(source,target,includes,null,overwrite);
        }


    }


    String resolvePath(String path) {
        if(path.startsWith("/")) return path;
        return p.getBaseDir() + "/" + path;
    }

	Copy createCopy(String srcdir, String todir, String includes, String excludes, Boolean overwrite) {
		
		File todirF = new File(todir);
        File srcdirF = new File(srcdir);

        log(todirF.getAbsolutePath());

        todirF.mkdirs();

        Copy copy = new Copy();
        copy.setProject(getProject());
        copy.setTodir(todirF);
        copy.setOverwrite(overwrite);
		copy.setPreserveLastModified(true);
                                                
        FileSet fileSet = new FileSet();
        fileSet.setDir(srcdirF);
        if(includes != null)
            fileSet.setIncludes(includes);
        if(excludes != null)
            fileSet.setExcludes(excludes);

        copy.addFileset(fileSet);
		return copy;
		
	}

    void doCopy(String srcdir, String todir, String includes, String excludes, Boolean overwrite) {

        Copy copy = createCopy(srcdir,todir,includes,excludes,overwrite);
        copy.execute();
        
    }

    void doCopyWithFiltering(String srcdir, String todir, String includes, String excludes, Boolean overwrite) {

        Copy copy = createCopy(srcdir,todir,includes,excludes,overwrite);
		copy.setFiltering(true);
		copy.setEncoding("UTF-8");
		copy.setOutputEncoding("UTF-8");		
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
