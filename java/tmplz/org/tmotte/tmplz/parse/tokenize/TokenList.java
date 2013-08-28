package org.tmotte.tmplz.parse.tokenize;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import org.tmotte.common.text.DelimitedString;

/** 
 * This acts as a list of Token objects. It isn't a Token in and of itself.
 */
public class TokenList {
  private List<Token> list=new LinkedList<Token>();
  DocumentTokenizer dt;
  public TokenList(DocumentTokenizer dt) {
    this.dt=dt;
  }
  public DocumentTokenizer getTokenizer() {
    return dt;
  }
  public void add(Token node){
    list.add(node);
  }
  public int size(){
    return list.size();
  }
  public Token get(int i){
    return list.get(i);
  }
  public void add(int i, Token t) {
    list.add(i, t);
  }
  public void set(int i, Token t) {
    list.set(i, t);
  }
  public void clear(){
    list.clear();
  }
  public void remove(int i){
    list.remove(i);
  }

  public String toString(){
    DelimitedString ds=new DelimitedString("\n[\n", "\n", "\n]");
    for (int i=0; i<size(); i++)
      ds.add(get(i));
    return ds.toString();
  }
}
