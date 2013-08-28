package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/** 
 * Thrown when the starting delimiter &amp; name of a valid tag is found, but the closing delimiter is not found. 
 */
public class NoTailOfTagException extends TemplateContentException {
  public final String tag;
  public final Delimiters delims;
  public NoTailOfTagException(Delimiters dt, String tag, StringChunker upTo){
    super("No \""+dt.endOfTag+"\" after \""+dt.startOfTag+tag+"\"", upTo);
    this.tag=tag;
    this.delims=dt;
  }
}