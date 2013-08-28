package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 
import org.tmotte.common.text.DelimitedString;

/** 
 * Thrown when a template tag can only be valid within the context of certain enclosing tags 
 * and there is no such enclosing tag present, and when a tag is enclosed within a tag that should not contain it. 
 * Examples include Show or Fillin tags not inside an Include tag.
 */
public class IllegalTagPlacementException extends TemplateContentException {
  public String[] legalParents;
  public IllegalTagPlacementException(TokenList tokens, Token parentToken, Token offender){
    this(tokens, parentToken, offender, (String[])null);
  }
  public IllegalTagPlacementException(TokenList tokens, Token parentToken, Token offender, String... legalParents){
    super(
      new TemplateErrorInfo(
        getErrorMessage(tokens, parentToken, offender, legalParents),
        tokens, 
        offender
      )
    );
  }
  private static String getErrorMessage(TokenList tokens, Token parentToken, Token offender, String[] legalParents) {
    String tokenName=tokens.getTokenizer().getTokenName(offender);
    if (parentToken==null){
      StringBuilder sb=new StringBuilder();
      sb.append(tokenName);
      sb.append(" tag should be nested in one of the following: ");
      if (legalParents==null)
        sb.append(" [INTERNAL ERROR, please report: legal parents not noted]");
      else 
        sb.append(new DelimitedString(", ").addEach(legalParents));
      return sb.toString();
    }
    else
      return tokenName
          +" tag cannot appear inside of "
          +tokens.getTokenizer().getTokenName(parentToken)+" tag";
  }
}