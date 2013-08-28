package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 
import org.tmotte.common.text.StringChunker;

/**
 * Thrown in various cases when the parser fails because of a bug. Occurences of this Exception should be
 * reported to the author(s) so that they can fix the problem. Ideally, this Exception should never be thrown.
 */
public class InternalTemplateException extends TemplateContentException {
  public InternalTemplateException(String message, TokenList tokens, Token token){
    super(
      new TemplateErrorInfo(
        "Internal error: "+message+"; please report this to us", tokens, token
      )
    );
  }
  public InternalTemplateException(String message, StringChunker upTo){
    super(
      new TemplateErrorInfo(
        "Internal error: "+message+"; please report this to us", upTo.getEverythingSoFar()
      )
    );
  }
}