package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.TokenShow; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 
import org.tmotte.common.text.DelimitedString; 

/**
 * Thrown when an Show references a nonexistent Section.
 */
public class ShowNoSuchSectionException extends TemplateContentException {
  private final String sectionName;
  private String path;
  public ShowNoSuchSectionException(TokenList tokens, TokenShow tokenShow, String path){
    super(
      new TemplateErrorInfo(
        "Show tag references nonexistent Section \""
          +new DelimitedString(".").addEach(tokenShow.getNames())
         +"\" expected in template \""+path+"\"", tokens, tokenShow
      )
    );
    this.sectionName=doTrim(tokenShow.getOriginalContent());
    this.path=path;
  }
  private static String doTrim(String s) {
    if (s==null)
      return "";
    return s.trim();
  }
  public String getSectionName() {
    return sectionName;
  }
  public String getPath(){
    return path;
  }
  
}