package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * Thrown when a Remove element does not specify a Section or Slot to remove.
 */
public class UnmatchedQuoteException extends TemplateContentException {
  public UnmatchedQuoteException(StringChunker upTo){
    super("Tag parameter has an opening quote (\") character without a closing quote character", upTo);
  }
}