package org.tmotte.tmplz.exception.parse;
import org.tmotte.common.text.StringChunker;
import org.tmotte.tmplz.parse.tokenize.DocumentTokenizer; 
import org.tmotte.tmplz.parse.tokenize.Delimiters; 

/**
 * Thrown when a Rename element has no parameters. Normally the element should specify an element to rename, and a 
 * a new name for it.
 */
public class RenameHasNoParametersException extends TemplateContentException {
  Delimiters dt;
  public RenameHasNoParametersException(Delimiters dt, StringChunker upTo){
    super(dt.startOfTag+DocumentTokenizer.nameRename+" needs two parameters, a tag to be replaced and a replacement name", upTo);
    this.dt=dt;
  }
}