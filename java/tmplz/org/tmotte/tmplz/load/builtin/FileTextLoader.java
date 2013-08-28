package org.tmotte.tmplz.load.builtin;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.load.FileDoesNotExistException;
import org.tmotte.tmplz.exception.load.CannotLoadFileException;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.util.Log;
import org.tmotte.common.io.Loader;

/**
 * Used exclusively by FileTextLoaderFactory.
 */
public class FileTextLoader implements TextLoader {
  File myFile;
  String myPath;
  String dir;
  long ifModifiedSince=-1;

  public FileTextLoader(String path) {
    myPath=path;
    myFile=new java.io.File(path);
    if (!myFile.exists())
      throw new FileDoesNotExistException(myFile);
    int lastSlash=myPath.lastIndexOf("/");
    if (lastSlash==-1)
      lastSlash=myPath.lastIndexOf("\\");
    dir=lastSlash!=-1
      ?myPath.substring(0, lastSlash+1)
      :"";
  }
  
  public String check(){
    if (!myFile.exists())
      throw new FileDoesNotExistException(myFile);
    long realIfMod=myFile.lastModified();
    if (realIfMod==ifModifiedSince)
      return null;
    try {
      return Loader.loadString(new FileInputStream(myFile), 512);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Path getAbsolutePath(String mayBeRelativePath){
    boolean relative=
      mayBeRelativePath.startsWith(".") ||
      (
        !mayBeRelativePath.contains(":\\")
        &&
        !mayBeRelativePath.startsWith("/")
      );
    try {
      return new Path(
        relative 
          ?new java.io.File(dir+mayBeRelativePath).getCanonicalPath()
          :mayBeRelativePath
      );
    } catch (Exception e) {
      throw new CannotLoadFileException(myFile, e);
    }
    
  }
  
} 