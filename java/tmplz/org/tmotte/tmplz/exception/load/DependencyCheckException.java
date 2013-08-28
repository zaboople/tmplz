package org.tmotte.tmplz.exception.load;
import org.tmotte.tmplz.parse.TextSource;
import java.net.URL;

/** 
 * Thrown when an URLTextLoader cannot load a URL for unknown reasons. 
 */
public class DependencyCheckException extends AbstractLoadException {
  public final TextSource hadDependency;
  public DependencyCheckException(TextSource ts, Exception e){
    super("Included path in "+ts.getPath()+" failed", e);
    this.hadDependency=ts;
  }
}