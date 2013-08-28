package org.tmotte.tmplz.load;
import java.net.URL;

/**
 * Represents the address of a document to be loaded by a TextLoader. A Path contains either a java.net.URL, or a String (never both). In the latter case,
 * the String may actually represent a URL; it is up to the TextLoaderFactory to convert it to such if it wants to.
 */
public class Path {
  final String pathString;
  final URL url;
  
  //////////////////
  //Constructors: //
  //////////////////
    
  public Path(String pathString){
    this.pathString=pathString;
    this.url=null;
  }
  public Path(URL url){
    this.pathString=null;
    this.url=url;
  }
  
  //////////////////
  // Get() stuff: //
  //////////////////  
    
  /**
   * @return true If this Path has a java.net.URL instead of a path String.
   */
  public boolean hasURL(){
    return url!=null;
  }
  public URL getURL(){
    return url;
  }
  /**
   * @return true If this Path contains a String instead of a java.net.URL.
   */
  public boolean hasPathString(){
    return pathString!=null;
  }
  public String getPathString(){
    return pathString;
  }
    
  /////////////////////////////////////
  // Gnarly equals() and hashCode(): //
  /////////////////////////////////////

  /**
   * This only returns true when the Path is compared to another Path, and both have the same internal URL
   * or path String.
   */
  public boolean equals(Object o) {
      
    //Simple cases:
    if (o==null)
      return false;
    if (!(o instanceof Path))
      return false;      
    
    Path otherPath=(Path)o;
    
    //This handles one half of nulls, next section handles other:  
    if (this.pathString==null && otherPath.pathString!=null)
      return false;            
    if (this.url==null && otherPath.url!=null)
      return false;
    
    //Voila:  
    return 
      (this.pathString!=null && this.pathString.equals(otherPath.pathString))
      ||
      (this.url!=null && this.url.equals(otherPath.url));
  }
  
  /**
   * Returns the hashCode for the internal path string, or the internal URL.
   */
  public int hashCode(){
    if (pathString!=null)
      return pathString.hashCode();
    if (url!=null)
      return url.hashCode();
    throw new IllegalStateException("pathString and URL are both null");
  }
 
  /**
   * Returns the internal path string, or the internal URL's <code>URL.toString()</code>.
   */
  public String toString(){
    if (pathString!=null)
      return pathString;
    else
      return url.toString();
  }
}