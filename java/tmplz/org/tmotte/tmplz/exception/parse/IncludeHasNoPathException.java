package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/** 
 * Thrown when an Include element has no reference to an external template to load. This may happen when 
 * a closing Include tag is accidentally created with no opening tag for it to close.
 */
public class IncludeHasNoPathException extends TemplateContentException {
  public IncludeHasNoPathException(TokenList tokens, Token offender){
    super(
      new TemplateErrorInfo(
        tokens.getTokenizer().getTokenName(offender)
          +" does not have a reference to an external template", 
        tokens, 
        offender
      )
    );
  }
}