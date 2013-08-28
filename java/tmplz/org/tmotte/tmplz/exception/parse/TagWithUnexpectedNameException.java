package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 

/**
 * Thrown when a <code>TagWith</code> tag uses a shortcut name that is not in the standard list.
 */
public class TagWithUnexpectedNameException extends TemplateContentException {
  String unknownType;
  public TagWithUnexpectedNameException(String unknownType, StringChunker upTo){
    super(DocumentTokenizer.nameTagWith+": shortcut \""+unknownType+"\" not recognized", upTo);
    this.unknownType=unknownType;
  }
}