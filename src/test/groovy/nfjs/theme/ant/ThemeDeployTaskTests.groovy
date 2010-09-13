package nfjs.theme.ant

import org.apache.tools.ant.Project

import org.junit.Test
import static org.junit.Assert.*

import nfjs.theme.BaseThemeTest

/**
 * Created by benellingson@yahoo.com
 * Date: Feb 23, 2010 6:32:07 PM
 */
public class ThemeDeployTaskTests extends BaseThemeTest {

    String testSourceDir = "src/test/ant-webapp"
    String testTargetDir = "build/ant-webapp"
    String testThemeDir = "WEB-INF/theme"


    @Test public void testExecute() {


        // delete if it already exists
        deleteDir(testTargetDir)

        new File(testTargetDir).mkdirs()

        // validate source content

        String parentIndex = new File("${testSourceDir}/${testThemeDir}/parent/index.html").text
        String fooIndex = new File("${testSourceDir}/${testThemeDir}/foo/index.html").text

        assertTrue parentIndex.contains("<title>Parent</title>")
        assertTrue fooIndex.contains("<title>Foo</title>")

        Project p = new Project()
        p.setProperty("appTheme.names","foo,bar")
        p.setProperty("appTheme.foo.parent","parent")
        p.setProperty("appTheme.bar.parent","parent")

        ThemeDeployTask tdt = new ThemeDeployTask()
        tdt.project = p
        tdt.sourcePath = testSourceDir
        tdt.themePath = testThemeDir
        tdt.targetPath = testTargetDir

        tdt.init()
        tdt.execute()

        // validate result foo/index overrode parent index

        String deployIndex = new File("${testTargetDir}/${testThemeDir}/foo/index.html").text         
        assertTrue deployIndex.contains("<title>Foo</title>")

        // validate parent file copied where child does not exist
        assertTrue new File("${testTargetDir}/${testThemeDir}/foo/page1.html").exists()

        // validate child file copied where parent does not exist
        assertTrue new File("${testTargetDir}/${testThemeDir}/foo/page2.html").exists()

        // validate webapp (non themed) resources are copied

        assertTrue new File("${testTargetDir}/test.jsp").exists()
        assertTrue new File("${testTargetDir}/scripts").exists()
        assertTrue new File("${testTargetDir}/images").exists()

        // validate filtering @appTheme.code@

        String fooPage1 = new File("${testTargetDir}/${testThemeDir}/foo/page1.html").text         
        assertTrue fooPage1.contains("<h1>Page 1 foo</h1>")

        String barPage1 = new File("${testTargetDir}/${testThemeDir}/bar/page1.html").text
        assertTrue barPage1.contains("<h1>Page 1 bar</h1>")


    }

    @Test public void testSettingDevApp() {


        // setting the devApp property allows you to deploy a single or subset of themes
        // this is useful for development when working on a large project

        // delete if it already exists
        deleteDir(testTargetDir)

        new File(testTargetDir).mkdirs()

        Project p = new Project()
        p.setProperty("appTheme.names","foo,bar")
        p.setProperty("appTheme.foo.parent","parent")
        p.setProperty("appTheme.bar.parent","parent")

        ThemeDeployTask tdt = new ThemeDeployTask()
        tdt.project = p        
        tdt.sourcePath = testSourceDir
        tdt.themePath = testThemeDir
        tdt.targetPath = testTargetDir
        tdt.devApp = 'foo'

        tdt.init()
        tdt.execute()

        // validate parent file copied where child does not exist
        assertTrue new File("${testTargetDir}/${testThemeDir}/foo/page1.html").exists()

        // validate child file copied where parent does not exist
        assertTrue new File("${testTargetDir}/${testThemeDir}/bar/page1.html").exists() == false
        

    }

    @Test public void testSettingThemeName() {


        // setting the devApp property allows you to deploy a single or subset of themes
        // this is useful for development when working on a large project

        // delete if it already exists
        deleteDir(testTargetDir)

        new File(testTargetDir).mkdirs()

        Project p = new Project()
        p.setProperty("appTheme.foo.parent","parent")
        p.setProperty("appTheme.bar.parent","parent")

        ThemeDeployTask tdt = new ThemeDeployTask()
        tdt.project = p
        tdt.sourcePath = testSourceDir
        tdt.themePath = testThemeDir
        tdt.targetPath = testTargetDir
        tdt.themeNames = 'foo,bar'

        tdt.init()
        tdt.execute()

        // validate parent file copied where child does not exist
        assertTrue new File("${testTargetDir}/${testThemeDir}/foo/page1.html").exists()

    }

     @Test public void testThemeWithoutParentAndWithIncludes() {

            // baz theme has no parent
            // test that the includes filter works

            deleteDir(testTargetDir)

            new File(testTargetDir).mkdirs()

            Project p = new Project()
            p.setProperty("appTheme.foo.parent","parent")
            p.setProperty("appTheme.bar.parent","parent")

            ThemeDeployTask tdt = new ThemeDeployTask()
            tdt.project = p
            tdt.sourcePath = testSourceDir
            tdt.themePath = testThemeDir
            tdt.targetPath = testTargetDir
            tdt.themeNames = 'foo,bar,baz'
            tdt.includes = '*.html,**/*.png'

            tdt.init()
            tdt.execute()

            // test that single file exists and no parent files copied
            assertTrue new File("${testTargetDir}/${testThemeDir}/baz/index.html").exists()
            assertTrue new File("${testTargetDir}/${testThemeDir}/baz/page1.html").exists() == false
            assertTrue new File("${testTargetDir}/${testThemeDir}/baz/img/UberConf.png").exists()
            assertTrue new File("${testTargetDir}/${testThemeDir}/baz/img/UberConf.jpg").exists() == false

    }

    
    @Test public void testThemeWithOverwright() {

            deleteDir(testTargetDir)

            File f = new File("${testTargetDir}/${testThemeDir}/foo/index.html")

            f.parentFile.mkdirs()

            f << 'overwrite this'

            assertTrue f.exists()
            assertTrue f.text.contains('overwrite this')

            Project p = new Project()
            p.setProperty("appTheme.foo.parent","parent")

            ThemeDeployTask tdt = new ThemeDeployTask()
            tdt.project = p
            tdt.sourcePath = testSourceDir
            tdt.themePath = testThemeDir
            tdt.targetPath = testTargetDir
            tdt.themeNames = 'foo'
            tdt.overwrite = true

            tdt.init()
            tdt.execute()

            // test that single file exists and no parent files copied
            assertTrue f.exists()

            assertTrue f.text.contains('overwrite this') == false

    }

    @Test public void testThemeFilterIncludes() {

            deleteDir(testTargetDir)

            new File(testTargetDir).mkdirs()

            Project p = new Project()

            ThemeDeployTask tdt = new ThemeDeployTask()
            tdt.project = p
            tdt.sourcePath = testSourceDir
            tdt.themePath = testThemeDir
            tdt.targetPath = testTargetDir
            tdt.devApp = 'baz'
            tdt.filterIncludes = "**/*.html,**/*.jsp"

            tdt.init()
            tdt.execute()

            File f =  new File("${testTargetDir}/${testThemeDir}/baz/index.html")

            assertTrue f.exists()

            // apply filter to: <h1>Hello : @appTheme.code@</h1>
            assertTrue f.text.contains("<h1>Hello : baz</h1>")

            // don't filter image files
            assertTrue new File("${testTargetDir}/${testThemeDir}/baz/img/UberConf.png").exists()

    }

    @Test public void testResolvePath() {

            Project p = new Project()
            p.baseDir = new File('.')

            ThemeDeployTask tdt = new ThemeDeployTask()
            tdt.project = p
            tdt.init()

            assertTrue  tdt.resolvePath('/tmp') == '/tmp'

            File f = new File(testTargetDir)

            assertTrue tdt.resolvePath(testTargetDir) == f.absolutePath

    }




}
