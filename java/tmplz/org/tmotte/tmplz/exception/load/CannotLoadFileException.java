package org.tmotte.tmplz.exception.load;
import java.io.File;

/** 
 * Thrown when an FileTextLoader cannot load a file for unknown reasons. 
 */
public class CannotLoadFileException extends AbstractLoadException {
  File file;
  public File getFile(){
    return file;
  }
  public CannotLoadFileException(File file, Exception e){
    super("Cannot load file: "+getName(file), e);
    this.file=file;
  }
  protected static String getName(File f) {
    if (f==null)
      return null;
    try {
      return f.getCanonicalPath();
    } catch (Exception e) {
      return f.toString();
    }
  }
}