package org.tmotte.tmplz.exception.load;
import org.tmotte.tmplz.load.Path;

/** 
 * Thrown when TextLoadMgr cannot obtain a TextLoader to provide content for a given Path.
 */
public class NoTextLoaderException extends AbstractLoadException {
  Path path;
  /** 
   * @return The Path that could not be resolved to a TextLoader.
   */
  public Path getPath(){
    return path;
  }
  public NoTextLoaderException(Path path){
    super("No TextLoader was found to fulfill the request for:"+path);
    this.path=path;
  }
}