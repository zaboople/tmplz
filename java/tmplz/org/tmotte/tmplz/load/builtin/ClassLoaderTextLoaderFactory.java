package org.tmotte.tmplz.load.builtin;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.servlet.ServletContext;
import org.tmotte.tmplz.exception.load.NoURLForPathException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.util.Log;

/**
 * Loads content from the Java "class path", specifically any ClassLoader. 
 * Since this class extends URLTextLoaderFactory, it resolves relative URL's just like its parent.
 */
public class ClassLoaderTextLoaderFactory extends URLTextLoaderFactory {
  private ClassLoader classLoader;
  
  /**
   * @param classLoader A classLoader that has template files in its classpath. 
   *   In many cases this can simply be this.getClassLoader() for the object invoking
   *   the constructor, or even new Object().getClassLoader().
   * @param prePath This will prepended to any Path passed to <code>create()</code>
   *   if Path.hasPathString() returns true. If not needed, use "" or null.
   * @param enableBrokenZipFix Enables a "temporary" hack/fix for java's inability (as of version 7) to resolve
   *    relative URL's using URL's in zip/jar/war files via URI.resolve(). Required by ClassLoaderTextLoaderFactory.
   */ 
  public ClassLoaderTextLoaderFactory(ClassLoader classLoader, String prePath, boolean enableBrokenZipFix){
    super(prePath);
    this.classLoader=classLoader;
    this.setBrokenZipResolution(enableBrokenZipFix);
  }
  /**
   * Invokes <code>ClassLoaderTextLoaderFactory(classLoader, "", false)</code>.
   */
  public ClassLoaderTextLoaderFactory(ClassLoader classLoader, String prePath){
    this(classLoader, prePath, false);
  }
  /**
   * Invokes <code>ClassLoaderTextLoaderFactory(classLoader, "", false)</code>.
   */
  public ClassLoaderTextLoaderFactory(ClassLoader classLoader){
    this(classLoader, "");
  }
  /**
   * Invokes <code>ClassLoaderTextLoaderFactory(c.getClassLoader(), "", false)</code>.
   */
  public ClassLoaderTextLoaderFactory(Class c){
    this(c.getClassLoader(), "");
  }
  /**
   * If <code>path.hasURL()</code> returns true, this method returns a URLTextLoader.
   * Otherwise, this creates a URL using ClassLoader.getResource() and returns a URLTextLoader
   * that loads from that URL.
   */
  public TextLoader create(Path path) throws Exception {
    return path.hasURL()
      ?super.create(path)
      :super.create(
        new Path(getURL(path.getPathString(), prePath, classLoader))
      );  
  }
  
  /**
   * Can be overridden for extra functionality. 
   */
  public URL getURL(String path, String prePath, ClassLoader classLoader){
    URL myURL=classLoader.getResource(prePath+path);
    if (myURL==null)
      throw new NoURLForPathException(path);
    return myURL;
  }

}


