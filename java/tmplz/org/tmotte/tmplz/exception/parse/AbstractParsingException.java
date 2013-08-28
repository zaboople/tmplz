package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.exception.TmplzException;

/**
 * Acts as the base class for all exceptions in this package. All of its descendants
 * are thrown as the result of syntax errors, except for InternalTemplateException.
 */
public class AbstractParsingException extends TmplzException {
  public AbstractParsingException(String message){
    super(message);
  }
  public AbstractParsingException(String message, Throwable cause){
    super(message, cause);
  }
}