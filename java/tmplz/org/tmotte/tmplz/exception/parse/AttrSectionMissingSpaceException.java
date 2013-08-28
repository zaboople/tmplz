package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * DEPRECATED
 */
public class AttrSectionMissingSpaceException extends TemplateContentException {
  Delimiters dt;
  public AttrSectionMissingSpaceException(String upTo, Delimiters dt){
    super("No blank space between \""+dt.startOfTag+DocumentTokenizer.nameAttrSection+"\" and \""+dt.endOfTag+"\"", upTo);
    this.dt=dt;
  }
}