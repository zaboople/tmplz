package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenRename; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/** 
 * Thrown when a Rename references a Slot or Section element that does not exist.
 */
public class RenameNoSuchTagException extends TemplateContentException {
  String includedPath;
  public RenameNoSuchTagException(TokenList tokens, TokenRename offender, String includedPath){
    super(
      new TemplateErrorInfo(
        getErrorMessage(tokens, offender, includedPath),
        tokens, 
        offender
      )
    );
    this.includedPath=includedPath;
  }
  public String getIncludedPath() {
    return includedPath;
  }
  private static String getErrorMessage(TokenList tokens, TokenRename offender, String includedPath) {
    DocumentTokenizer dt=tokens.getTokenizer();
    return dt.nameRename+" references non-existent elements(s) \""
      +offender.getOriginalToReplace()
      +"\""
      +" in included template "+includedPath;
  }
}