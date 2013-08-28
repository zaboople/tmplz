package org.tmotte.tmplz.exception.load;
import org.tmotte.tmplz.load.Path;

/**
 * This is thrown by TextLoadMgr when a TextLoaderFactory.create() throws an Exception that
 * is not a TmplzException.
 */
public class TextLoaderCreateException extends AbstractLoadException {
  Path path;
  public Path getPath(){
    return path;
  }
  public TextLoaderCreateException(Path path, Exception e){
    super("Cannot handle path: "+path, e);
    this.path=path;
  }
}
