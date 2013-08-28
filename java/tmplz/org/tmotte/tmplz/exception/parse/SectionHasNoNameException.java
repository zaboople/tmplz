package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.TokenSection; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 


/**
 * Thrown when a Section element does not specify a name for the Section.
 */
public class SectionHasNoNameException extends TemplateContentException {
  public SectionHasNoNameException(TokenList tokens, TokenSection offender){
    super(
      new TemplateErrorInfo(
        tokens.getTokenizer().getTokenName(offender)
          +" does not have a name specified", 
        tokens, 
        offender
      )
    );
  }
}