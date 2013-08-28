package org.tmotte.tmplz.load.builtin;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.servlet.ServletContext;
import org.tmotte.tmplz.exception.load.InvalidURLException;
import org.tmotte.tmplz.exception.load.NoURLForPathException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.util.Log;

/**
 * <p>
 * Uses javax.servlet.ServletContext to locate resources within a web application's file hierarchy
 * via ServletContext.getResource(). Paths passed to 
 * TemplateManager.getTemplate() should should start with a "/" character (or pass such as <code>prePath</code>
 * to <code>ServletContextTextLoaderFactory(servletContext, prePath)</code>). Relative paths
 * in Include tags are allowed, since this class extends URLTextLoaderFactory.
 * </p>
 * <p>
 * Note: Much to our dissapointment, recent/certain versions of Tomcat do not properly handle URL's
 * which (correctly) use <code>%20</code> to represent space characters in the file name. 
 * </p>
 */
public class ServletContextTextLoaderFactory extends URLTextLoaderFactory {
  private ServletContext servletContext;
  
  /**
   * @param prePath This will be prepended to the Path passed to <code>create(Path)</code>
   *  when <code>Path.hasPathString()</code> is true.
   */ 
  public ServletContextTextLoaderFactory(ServletContext servletContext, String prePath){
    super(prePath);
    this.servletContext=servletContext;
  }
  public ServletContextTextLoaderFactory(ServletContext servletContext){
    this(servletContext, "");
  }
  /**
   * Returns a TextLoader when the <code>path.hasURL()</code> returns true, or when <code>path.getPathString()</code> has a leading "/"
   * (including the <code>prePath</code> passed to the constructor); otherwise returns <code>null</code>. Uses URLTextLoader internally.
   */
  public TextLoader create(Path path) throws Exception {
    if (path.hasURL())
      return super.create(path);  
    path=convertPath(path.getPathString(), prePath, servletContext);
    if (path==null)
      return null;
    return super.create(path);
  }
  

  public Path convertPath(String path, String prePath, ServletContext servletContext){
    boolean canLoad=
       prePath.startsWith("/")
       || (
         prePath.equals("") && path.startsWith("/")
       );
    path=prePath+path;
    path=path.replaceAll("%20", " ").replaceAll(" ", "%20");
    URL myURL;
    try {
      myURL=servletContext.getResource(path);
    } catch (MalformedURLException e) {
      throw new InvalidURLException(path, e);
    }
    if (myURL==null)
      throw new NoURLForPathException(path);
    return new Path(myURL);
  }

}
