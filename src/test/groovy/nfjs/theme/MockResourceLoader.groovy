package nfjs.theme

import org.springframework.core.io.FileSystemResourceLoader
import org.springframework.core.io.Resource
import org.springframework.core.io.FileSystemResource


/**
 * User: ben
 * Date: Nov 6, 2008
 * Time: 1:00:14 PM
 */
public class MockResourceLoader extends FileSystemResourceLoader {

    protected Resource getResourceByPath(String path) {

/*
        URL url = getClass().getResource(path)
        return new FileSystemResource(url.getFile())

*/
        return new FileSystemResource(new File(path))

    }

}
