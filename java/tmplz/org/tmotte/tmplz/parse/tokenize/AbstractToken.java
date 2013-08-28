package org.tmotte.tmplz.parse.tokenize;

public abstract class AbstractToken implements Token {
  OriginalTag oc;
  Token parentToken;//Sigh, for AttrTarget2, ugh.
  public void setOriginalTag(OriginalTag oc) {
    this.oc=oc;
  }
  public String getOriginalContent() {
    return oc.content;
  }
  public void getOriginalTag(Appendable a) throws java.io.IOException {
    a.append(oc.startOfTag);
    a.append(oc.content);
    a.append(oc.endOfTag);
  }
}