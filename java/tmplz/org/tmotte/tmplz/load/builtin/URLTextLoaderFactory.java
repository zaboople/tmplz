package org.tmotte.tmplz.load.builtin;
import java.net.Proxy;
import java.net.URL;
import java.net.MalformedURLException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.load.InvalidURLException;

/**
 * This acts as a base class for most of the other built-in TextLoaderFactory classes, and is somewhat useful on its own as well.
 */
public class URLTextLoaderFactory implements TextLoaderFactory {
  String prePath=null;
  Proxy proxy=null;
  boolean brokenZips=false;

  ////////////
  // SETUP: //
  ////////////
  
  /**
   * @param prePath Will be prepended to Path.getPathString() when Path.hasPathString() returns true. For example,
   *   if you want to load all templates from your filesystem, you could use "file://" or "file://c:/MyTemplateDirectory".
   * @param proxy This will be used to invoke URL.openConnection(Proxy), so that you can open HTTP connections
   *   (and perhaps other types) via a proxy server. This parameter should be null if no proxy is needed.
   */
  public URLTextLoaderFactory(String prePath, Proxy proxy){
    if (prePath==null)
      prePath="";
    this.prePath=prePath;
    this.proxy=proxy;
  }
  /**
   * Invokes <code>URLTextLoaderFactory(prePath, null)</code>.
   */
  public URLTextLoaderFactory(String prePath){
    this(prePath, null);
  }
  /**
   * Invokes <code>URLTextLoaderFactory("", null)</code>.
   */
  public URLTextLoaderFactory(){
    this("", null);
  }  
  /**
   * Used by ClassLoaderTextLoaderFactory to fix Java's problems with resolving relative URL's in zip
   * files.
   * @see ClassLoaderTextLoaderFactory#ClassLoaderTextLoaderFactory(java.lang.ClassLoader,java.lang.String,boolean)
   */
  public URLTextLoaderFactory setBrokenZipResolution(boolean enableBrokenZipResolution){
    brokenZips=enableBrokenZipResolution;
    return this;
  }


  ///////////////////////////////
  // INTERFACE IMPLEMENTATION: //
  ///////////////////////////////
  
  
  /**
   * @return If path.hasURL() returns true, a URLTextLoader is created with path.getURL(). If path.hasPathString() returns
   *         true, the "prePath" value passed to the constructor ("" by default) will be prepended to path.getPathString(),
   *         and new URL(String) will be invoked with it.
   */
  public TextLoader create(Path path) throws Exception{
    if (path.hasPathString()){
      String p=prePath+path.getPathString();
      if (p.contains(" "))
        p=p.replaceAll(" ", "%20");
      try {
        return new URLTextLoader(new URL(p), proxy, brokenZips);
      } catch (MalformedURLException e) {
        throw new InvalidURLException(p, e);
      }
    }  
    else    
    if (!path.hasURL())
      throw new InternalException("Does not have an internal URL: "+path.toString());
    else
      return new URLTextLoader(path.getURL(), proxy, brokenZips);  
  }
  
}
