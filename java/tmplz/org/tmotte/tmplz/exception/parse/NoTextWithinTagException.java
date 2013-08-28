package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenStatic; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/** 
 * Thrown when an Include or Show element contains non-whitespace, non-tag text. These elements should only contain
 * Tmplz tags for additional elements.
 */
public class NoTextWithinTagException extends TemplateContentException {
  public Token parentToken;
  public NoTextWithinTagException(TokenList tokens, Token parentToken, TokenStatic offender){
    super(
      new TemplateErrorInfo(
        tokens.getTokenizer().getTokenName(parentToken)+" can only contain tags and \"whitespace\"",
        tokens, 
        offender
      )
    );
  }
}