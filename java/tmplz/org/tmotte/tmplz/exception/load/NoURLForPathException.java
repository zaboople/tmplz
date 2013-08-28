package org.tmotte.tmplz.exception.load;
import org.tmotte.tmplz.load.Path;

/** 
 * Thrown when Path.getPathString() cannot be resolved to a URL &amp; loaded by a TextLoader.
 */
public class NoURLForPathException extends AbstractLoadException {
  String path;
  public String getPath(){
    return path;
  }
  public NoURLForPathException(String path){
    super("Could not resolve \""+path+"\" to a loadable system URL.");
    this.path=path;
  }
}