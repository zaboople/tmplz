package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.TokenInclude; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * Thrown when an <code>Include</code> is "circular", i.e. would result in an infinite loop of includes.
 */
public class IncludeCircularException extends TemplateContentException {
  private final String path;
  public IncludeCircularException(TokenList tokens, TokenInclude originalToken, String path){
    super(
      new TemplateErrorInfo(
        "Include tag refers to a path that will cause an infinite loop of includes: \""+path+"\"", tokens, originalToken
      )
    );
    this.path=path;
  }
  public String getPath(){
    return path;
  }
  
}