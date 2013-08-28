package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.Delimiters; 
import org.tmotte.common.text.DelimitedString;

/** 
 * Thrown when the starting delimiter &amp; name of a valid tag is found, but the closing delimiter is not found. 
 */
public class TagHasTooManyParametersException extends TemplateContentException {
  public final int maxWas;
  public final String[] paramBuffer;
  public TagHasTooManyParametersException(String[] params, int maxWas, StringChunker upTo){
    super("Tag has more than the maximum of "+maxWas+" parameter(s): "+getList(params), upTo);
    this.maxWas=maxWas;
    paramBuffer=params;
  }
  private static String getList(String[] params){
    DelimitedString d=new DelimitedString("", ", ", "", "\"", "\"");
    int i=0;
    while (i<params.length && params[i]!=null)
      d.add(params[i++]);
    return d.toString();
  }
}