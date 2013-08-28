package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.TokenFillin; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when an <code>Fillin</code> references a nonexistent <code>Slot</code>.
 */
public class FillinNoSuchSlotException extends TemplateContentException {
  public final String slotName;
  private String path;
  public FillinNoSuchSlotException(TokenList tokens, TokenFillin tokenFillin, String path){
    super(
      new TemplateErrorInfo(
        "Fillin tag references nonexistent Slot \""+doTrim(tokenFillin.getOriginalContent())+"\" in included template \""+path+"\"", tokens, tokenFillin
      )
    );
    slotName=doTrim(tokenFillin.getOriginalContent());
    this.path=path;
  }
  private static String doTrim(String s) {
    if (s==null)
      return "";
    return s.trim();
  }
  public String getSlotName() {
    return slotName;
  }
  public String getPath(){
    return path;
  }
  
}