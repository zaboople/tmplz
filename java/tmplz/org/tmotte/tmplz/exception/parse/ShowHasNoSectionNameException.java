package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when a <code>Show</code> element does not specify the name of a Section. The closing tag for a Show (which is always required)
 * does not have to specify the name, however, just the opening tag.
 */
public class ShowHasNoSectionNameException extends TemplateContentException {
  public ShowHasNoSectionNameException(TokenList tokens, Token offender){
    super(
      new TemplateErrorInfo(
        tokens.getTokenizer().getTokenName(offender)
          +" does not specify a section name", 
        tokens, 
        offender
      )
    );
  }
}