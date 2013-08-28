package org.tmotte.tmplz.exception.load;
import java.io.File;

/** 
 * Thrown when an FileTextLoader cannot load a file because it doesn't exist. 
 */
public class FileDoesNotExistException extends AbstractLoadException {
  File file;
  public File getFile(){
    return file;
  }
  public FileDoesNotExistException(File file){
    super("File does not exist: "+CannotLoadFileException.getName(file));
    this.file=file;
  }
}