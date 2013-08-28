package org.tmotte.tmplz.exception;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/** 
  * Should only be thrown as a result of a bug in Tmplz. Report any instance of this exception to the author(s) 
  * so that the underlying problem can be fixed.
  */
public class InternalException extends TmplzException {
  public InternalException(String message, Exception e){
    super("Internal failure, please report: "+message, e);
  }
  public InternalException(String message){
    super("Internal failure, please report: "+message);
  }
}