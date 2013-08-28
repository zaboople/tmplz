package org.tmotte.tmplz.exception.parse;
import java.net.URL;
import org.tmotte.common.text.StringChunker;

/**
 * Acts as the base class for most exceptions in this package.
 */
public abstract class TemplateContentException extends AbstractParsingException {

  TemplateErrorInfo myMess;
  protected TemplateContentException(TemplateErrorInfo tei){
    super(tei.message);
    myMess=tei;
  }
  protected TemplateContentException(TemplateErrorInfo tei, Exception e){
    super(tei.message, e);
    myMess=tei;    
  }
  protected TemplateContentException(String message, String upTo){
    this(new TemplateErrorInfo(message, upTo));
  }
  protected TemplateContentException(String message, StringChunker upTo){
    this(new TemplateErrorInfo(message, upTo));  
  }

  public int getLineNumber() {
    return myMess.lineNumber;
  }
  public String getLastLine() {
    return myMess.lastLine;
  }

}
