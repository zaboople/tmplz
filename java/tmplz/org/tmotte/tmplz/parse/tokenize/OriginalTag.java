package org.tmotte.tmplz.parse.tokenize;
public class OriginalTag {
  String startOfTag, content, endOfTag;
  public OriginalTag(String startOfTag, String content, String endOfTag) {
    this.startOfTag=startOfTag;
    this.content=content;
    this.endOfTag=endOfTag;
  }
}