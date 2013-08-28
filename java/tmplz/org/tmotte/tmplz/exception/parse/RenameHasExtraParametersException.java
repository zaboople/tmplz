package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * Thrown when a Rename element has parameters beyond the optional Section/Slot, the old name, and the new name. 
 */
public class RenameHasExtraParametersException extends TemplateContentException {
  Delimiters dt;
  public RenameHasExtraParametersException(Delimiters dt, StringChunker upTo){
    super(
      DocumentTokenizer.nameRename+" tag has extra parameters that are not needed",
      upTo
    );
    this.dt=dt;
  }
}