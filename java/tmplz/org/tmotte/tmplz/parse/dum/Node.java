package org.tmotte.tmplz.parse.dum;
public interface Node {
  public int getType();
  /** This is forced by NodeSection.show(). */
  public Node cloneSelf();
}