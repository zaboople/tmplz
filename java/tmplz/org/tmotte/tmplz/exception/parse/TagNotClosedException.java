package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 
import org.tmotte.common.text.StringChunker;

/** 
 * Thrown when a tag such as <code>Section</code> or <code>Fillin</code> does not have a closing tag. Some tags are 
 * allowed to be self-closing, but the rest will throw this Exception if there is not both a starting and ending tag.
 */
public class TagNotClosedException extends TemplateContentException {
  public TagNotClosedException(TokenList tokens, Token offender){
    super(
      new TemplateErrorInfo(
        tokens.getTokenizer().getTokenName(offender)+" has no closing tag",
        tokens, 
        offender
      )
    );
  }
}