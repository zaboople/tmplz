package org.tmotte.tmplz.load.builtin;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.load.CannotLoadURLException;
import org.tmotte.tmplz.exception.load.CannotResolveRelativePathException;
import org.tmotte.tmplz.exception.load.InvalidURLException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.util.Log;
import org.tmotte.common.io.Loader;

/**
 * Loads text from a URL. Since caching behavior is important, several things should be understood:
 * This last-modified timestamp returned by URLTextLoader.get() is the value of System.currentTimeMillis()
 * when the last change was detected. It is NOT the value of any timestamp reported by the URL's source.
 * Nonetheless URLTextLoader will negotiate with the URL source using last-modified timestamps
 * as a "validator" to detect changes.
 */
public class URLTextLoader implements TextLoader {
  private boolean brokenZip=false;
  protected URL url;
  protected URI uri;
  protected long sourceLastModified=-1;
  protected Proxy proxy;//FIXLATER use of proxy not tested

  public URLTextLoader(URL url){
    this(url, null, false);
  }
  public URLTextLoader(URL url, Proxy proxy) {
    this(url, proxy, false);
  }
  /**
   * @param url The URL to load from.
   * @param proxy Used when we a proxy server is needed; will be passed to <code>URL.openConnection(Proxy)</code>.
   * @param enableBrokenZipResolution Enables a "temporary" hack/fix for java's inability (as of version 7) to resolve
   *    relative URL's using URL's in zip/jar/war files via URI.resolve(). Required by ClassLoaderTextLoaderFactory.
   */
  public URLTextLoader(URL url, Proxy proxy, boolean enableBrokenZipResolution) {
    if (url==null)
      throw new IllegalArgumentException("URL was null");
    this.url=url;
    brokenZip=enableBrokenZipResolution;
    try {
      this.uri=url.toURI();
    } catch (java.net.URISyntaxException u) {
      throw new InvalidURLException(url, u);//Naughty
    }
    this.proxy=proxy;
  }



  /**
   * Because it uses URL/URI's, this class can ask its URL/URI
   * to resolve a path via URI.resolve(). Notes:
   * <ol>
   *  <li>Paths containing a ":" are assumed to be absolute, in which case null is returned.
   *  <li>Paths containing a " " character are converted such that " " is replaced with "%20".
   * </ol>
   * @see java.net.URI#resolve(String)
   */
  public Path getAbsolutePath(String relativePath){
    //Log.finest("URLTextLoader.getAbsolutePath(): relativePath="+relativePath);
    if (!relativePath.contains(":")){
      if (relativePath.contains(" "))
        relativePath=relativePath.replaceAll(" ", "%20");
      URI newURI=uri.resolve(relativePath);
      if (newURI==null)
        throw new CannotResolveRelativePathException(relativePath, uri);
      try {
        if (brokenZip && newURI.toString().indexOf(":")==-1)
          newURI=DumbURIFix.fix(uri, relativePath);
        return new Path(newURI.toURL());
      } catch (Exception e) {
        throw new CannotResolveRelativePathException(relativePath, uri, newURI, e);
      }
    }
    return null;
  }


  ////////////////////////
  // INTERFACE METHODS: //
  ////////////////////////

  /**
   * Checks its source for changes. Fulfills <code>TextLoader.check()</code> interface method.
   */
  public synchronized String check(){

    try {
      if (uri==null)
        throw new InternalException("URI is null");
      if (url==null)
        throw new InternalException("URL is null");
      URLConnection connection=proxy==null
        ?url.openConnection()
        :url.openConnection(proxy);
      connection.setUseCaches(false);

      //If our client is up-to-date with us, we'll ask the source
      //to only return content if we don't have it.
      if (sourceLastModified>0)
        connection.setIfModifiedSince(sourceLastModified);

      //Maybe ifModifiedSince() worked and we got 304:
      if (connection instanceof HttpURLConnection){
        HttpURLConnection hconn=(HttpURLConnection)connection;
        if (304==hconn.getResponseCode()){
          //Log.finest("URLTextLoader.check(): "+url+" no change, 304 ");
          connection.getInputStream().close();
          return null;
        }
      }

      //There is a java bug where Last Modified comes out zero if there
      //was a 304, but at this point we know there wasn't. We will get a 0
      //if last modified wasn't supported, or perhaps a -1.
      long newSourceLastModified=connection.getLastModified();
      //Log.finest("URLTextLoader.check(): "+url+" Last modified: "+newSourceLastModified);
      if (newSourceLastModified>0 && newSourceLastModified==sourceLastModified){
        //Log.finest("URLTextLoader.check(): "+url+" no change ");
        connection.getInputStream().close();
        return null;
      }
      sourceLastModified=newSourceLastModified;
      Log.finest("URLTextLoader.check(): detected change to "+url+" last modified: "+newSourceLastModified);


      //Now get the content. Note that we do one more check to make
      //sure it really changed:
      int len=connection.getContentLength();
      String temp=Loader.loadString(connection.getInputStream(), Math.max(len, 512));
      return changed(temp)
        ?temp
        :null;
    } catch (TmplzException e) {
      throw e;
    } catch (Exception e) {
      throw new CannotLoadURLException(url, e);
    }

  }

  /**
   * This will be invoked by <code>check()</code> when new text is found. Override this method to do your
   * own check to see if the content really changed
   * enough to warrant returning text. If changed() returns false, <code>check()</code> will return null.
   * @return The default behavior is to always return true.
   */
  protected boolean changed(String newData){
    return true;
  }
}