package org.tmotte.tmplz.exception.load;
import org.tmotte.tmplz.exception.TmplzException;

/**
 * This acts as the base class for all exceptions in this package.
 */
public class AbstractLoadException extends TmplzException {
  public AbstractLoadException(String message){
    super(message);
  }
  public AbstractLoadException(String message, Throwable cause){
    super(message, cause);
  }
}