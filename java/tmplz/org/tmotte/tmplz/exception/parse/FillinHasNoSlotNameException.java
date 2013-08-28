package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when a Fillin element does not specify a Slot to fill in. May also happen
 * when a closing Fillin tag is declared without an opening tag.
 */
public class FillinHasNoSlotNameException extends TemplateContentException {
  public FillinHasNoSlotNameException(TokenList tokens, Token offender){
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