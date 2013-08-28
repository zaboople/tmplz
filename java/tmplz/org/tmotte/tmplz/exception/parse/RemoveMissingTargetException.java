package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * Thrown when a Remove element does not specify a Section or Slot to remove.
 */
public class RemoveMissingTargetException extends TemplateContentException {
  Delimiters dt;
  public RemoveMissingTargetException(Delimiters dt, StringChunker upTo){
    super(DocumentTokenizer.nameRemove+" tag does not specify a Slot or Section to remove", upTo);
    this.dt=dt;
  }
}