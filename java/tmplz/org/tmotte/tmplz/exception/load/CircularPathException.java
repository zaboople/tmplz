package org.tmotte.tmplz.exception.load;
/**
 * This is always caught by the Preprocessor, and occurs when a series of <code>Include</code> tags
 * results in an infinite loop. The resulting exception is IncludeCircularException.
 * @see org.tmotte.tmplz.exception.parse.IncludeCircularException
 */
public class CircularPathException extends RuntimeException {
  public CircularPathException(){
    super();
  }
}