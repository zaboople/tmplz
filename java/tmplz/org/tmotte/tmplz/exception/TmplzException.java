package org.tmotte.tmplz.exception;

/** The base class for all of Tmplz's custom Exception classes. */
public abstract class TmplzException extends RuntimeException {
  public TmplzException(String message){
    super(message);
  }
  public TmplzException(String message, Throwable cause){
    super(message, cause);
  }
  public TmplzException(Throwable cause){
    super(cause);
  }

}