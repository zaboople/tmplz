package org.tmotte.tmplz.exception.parse;
import org.tmotte.tmplz.load.Path; 

/** 
 * Always thrown with a nested exception. Its purpose is to tell us what template
 * had a problem. The contained Exception will tell us what the problem was, and may nest further Exceptions.
 */
public class TemplateNestedException extends AbstractParsingException {
  private Path path;
  public TemplateNestedException(Path path, Exception e) {
    super("Error occurred processing template: "+path, e);
    this.path=path;
  }
  public Path getPath(){
    return path;
  }
}
