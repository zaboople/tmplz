package org.tmotte.tmplz.load.builtin;
import java.net.URI;
import java.net.URL;

class DumbURIFix {
  public static URI fix(URI uri, String relativePath) throws java.net.URISyntaxException {
    String old=uri.toString();
    if (old.endsWith("/"))
      return new URI(old+relativePath).normalize();
    int idnex=old.lastIndexOf("/");
    if (idnex>-1) 
      return new URI(old.substring(0, idnex+1)+relativePath).normalize();
    throw new RuntimeException("Tried to fix broken zip URI resolution but there were no / characters");
  }
  public static void main(String[] args) throws Exception {
    String s1=args[0], s2=args[1];
    URL u=new URL(s1);
    System.out.println(fix(u.toURI(), s2));
  }
}