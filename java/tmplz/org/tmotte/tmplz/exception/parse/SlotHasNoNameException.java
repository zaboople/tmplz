package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.TokenSlot; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when a Slot tag does not specify a name.
 */
public class SlotHasNoNameException extends TemplateContentException {
  public SlotHasNoNameException(TokenList tokens, TokenSlot offender){
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