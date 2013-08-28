package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Token; 
import org.tmotte.tmplz.parse.tokenize.TokenList; 

/**
 * This was created so that we have something that can isolate the line where the error occured
 * and adjust the Exception message to include it.
 */
public class TemplateErrorInfo {
  int lineNumber=-1;
  String lastLine;
  String message;
  String templateTextUpTo;
  public TemplateErrorInfo(String messageBase, TokenList tokens, Token badToken){  
    this(messageBase, getTextUpTo(tokens.getTokenizer(), tokens, badToken));
  }
  public TemplateErrorInfo(String messageBase, StringChunker templateTextUpTo){
    this(messageBase, templateTextUpTo.getEverythingSoFar());
  }
  public TemplateErrorInfo(String messageBase, String templateTextUpTo){
    this.templateTextUpTo=templateTextUpTo;
    message=messageBase;
    if (templateTextUpTo!=null){
      StringChunker chunker=new StringChunker(templateTextUpTo);        
      int lineIndex=1;
      String lastLine=null;
      while (chunker.find("\n")){
        lastLine=chunker.getUpTo();
        lineIndex++;
      }
      String ll=chunker.getRest();
      if (lastLine==null || !ll.trim().equals(""))
        lastLine=ll;
      message+=". Refer to line "+lineIndex+": "+lastLine;
    }
  }
  private static String getTextUpTo(DocumentTokenizer dt, TokenList tokens, Token offender) {
    StringBuilder sb=new StringBuilder();
    dt.produce(sb, tokens, offender);
    return sb.toString();
  }
}
