package org.tmotte.tmplz.exception.load;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/** 
 * This is used to wrap a MalformedURLException or URISyntaxException. Currently
 * only used by ServletContextTextLoaderFactory.
 */
public class InvalidURLException extends AbstractLoadException {
  String url;
  public String getURL(){
    return url;
  }
  public InvalidURLException(String url, MalformedURLException e){
    super("Invalid URL: "+url, e);
    this.url=url;
  }
  public InvalidURLException(java.net.URL url, URISyntaxException e){
    super("Invalid URL: "+url, e);
    if (url!=null)
      this.url=url.toString();
  }

}