package org.tmotte.tmplz.load.builtin;
import java.net.URL;
import java.net.MalformedURLException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.TemplateManager;
import java.io.File;

/**
 * Loads from the filesystem. For web applications, this is less flexible than ServletContextTextLoaderFactory and 
 * ClassLoaderTextLoaderFactory, but it does give better performance (as per the package docs) under extremely high load. 
 */
public class FileTextLoaderFactory implements TextLoaderFactory {
  
  String prePath;
  
  /**
   * @param prePath <code>create(Path)</code> will prepend this to <code>Path.getPathString()</code>.
   */  
  public FileTextLoaderFactory(String prePath){
    this.prePath=prePath;
  }

  public FileTextLoaderFactory(){
    this(null);
  }
  
  /**
   * Tries to create a TextLoader, according to the following rules:<ul>
   * <li>If path.hasURL() returns true, returns null; 
   * <li>Otherwise attempts to use path.getPathString() as a reference to a file.
   * </ul>
   */
  public TextLoader create(Path path) throws Exception{
    return path.hasURL()
      ?null
      :new FileTextLoader(convertPath(path.getPathString()));
  }
  private String convertPath(String path) throws Exception {
    if (path.startsWith("file://"))
      return path.substring(7);
    if (prePath==null)
      return path;
    File f=new File(path);
    if (f.exists() && f.getCanonicalPath().equals(path))
      return path;
    return prePath+path;
  }
}
