package org.tmotte.tmplz.exception.load;
import org.tmotte.tmplz.load.Path;
import java.net.URI;
import java.net.URL;

/** 
 * This should be thrown by TextLoader.getAbsolutePath() when it cannot resolve a requested path against a given absolute path.
 */
public class CannotResolveRelativePathException extends AbstractLoadException  {
  Object inContextOfPath;
  String relativePath;
  
  private CannotResolveRelativePathException(String relativePath, String inContextOfPath){
    super("Could not resolve \""+relativePath+"\" in context of \""+inContextOfPath+"\"");
    this.inContextOfPath=inContextOfPath;
    this.relativePath=relativePath;
  }
  public CannotResolveRelativePathException(String relativePath, URI inContextOfPath){
    this(relativePath, inContextOfPath==null ?null :inContextOfPath.toString());
  }
  public CannotResolveRelativePathException(String relativePath, URI inContextOfPath, URI newPath, Exception e){
    super("Path \""+relativePath+"\" in context of \""+inContextOfPath+"\" resolved to an invalid absolute path: \""+newPath+"\"");
    this.inContextOfPath=inContextOfPath;
    this.relativePath=relativePath;
  }


  public String getInContextOfPath(){
    return inContextOfPath.toString();
  }
  public String getRelativePath(){
    return relativePath;
  }

}