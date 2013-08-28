package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 

/**
 * Thrown when a <code>TagWith</code> element does not specify a shortcut or delimiters, i.e. it's just blank.
 */
public class TagWithNoParametersException extends TemplateContentException {
  public TagWithNoParametersException(StringChunker upTo){
    super(DocumentTokenizer.nameTagWith+" has no parameters", upTo);
  }
}