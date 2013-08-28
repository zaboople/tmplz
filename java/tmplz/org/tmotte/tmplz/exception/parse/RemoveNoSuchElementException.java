package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.TokenRemove; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when an Remove references a nonexistent Section/Slot.
 */
public class RemoveNoSuchElementException extends TemplateContentException {
  private final String elementName;
  private String path;
  public RemoveNoSuchElementException(TokenList tokens, TokenRemove tokenRemove, String path){
    super(
      new TemplateErrorInfo(
        tokens.getTokenizer().nameRemove+" tag references nonexistent element \""
          +doTrim(tokenRemove.getOriginalContent())+"\" expected in template \""+path+"\"", 
        tokens, 
        tokenRemove
      )
    );
    this.elementName=doTrim(tokenRemove.getOriginalContent());
    this.path=path;
  }
  private static String doTrim(String s) {
    if (s==null)
      return "";
    return s.trim();
  }
  public String getElementName() {
    return elementName;
  }
  public String getPath(){
    return path;
  }
  
}