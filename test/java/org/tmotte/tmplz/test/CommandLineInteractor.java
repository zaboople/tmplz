package org.tmotte.tmplz.test;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
public class CommandLineInteractor {
  BufferedReader br=null;
  boolean yessir=true;
  public void stop(){
    yessir=false;
  }
  public boolean keepGoing() throws IOException {
    if (!yessir)
      return false;
    else
    if (br==null){
      br=new BufferedReader(new InputStreamReader(System.in));
      return true;
    }
    else {
      System.out.print("\n\nDo again? ");   
      String foo=br.readLine();
      return foo!=null && !foo.toLowerCase().startsWith("n");
    }
  }
}