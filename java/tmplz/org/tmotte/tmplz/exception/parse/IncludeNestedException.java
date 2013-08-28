package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenInclude; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/** 
 * Thrown when an included file fails to parse. It will always contain a nested exception.
 * This outer exception helps us identify where the Include was in the parent template, and 
 * what file was included.
 */
public class IncludeNestedException extends TemplateContentException {
  TokenInclude tokenInclude;
  public IncludeNestedException(TokenList tokens, TokenInclude token, Exception e) {
    super(new TemplateErrorInfo("Template include failed", tokens, token), e);
    this.tokenInclude=token;
  }
  /**
   * This will contain the path to the included template that failed parsing.
   */
  public TokenInclude getTokenInclude() {
    return tokenInclude;
  }
}
