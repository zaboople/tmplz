package org.tmotte.tmplz.exception.load;
import java.net.URL;

/** 
 * Thrown when an URLTextLoader cannot load a URL for unknown reasons. 
 */
public class CannotLoadURLException extends AbstractLoadException {
  URL url;
  public URL getURL(){
    return url;
  }
  public CannotLoadURLException(URL url, Exception e){
    super("Cannot load URL: "+url, e);
    this.url=url;
  }
}