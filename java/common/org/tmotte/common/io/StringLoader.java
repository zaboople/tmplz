package org.tmotte.common.io;

public class StringLoader {
  private java.io.InputStreamReader br;
  private java.io.InputStream instr;
  private String last=null;
  public StringLoader(java.io.InputStream instr) {
    this.instr=instr;
    this.br=new java.io.InputStreamReader(instr);
  }
  public boolean load(char[] readBuffer) throws java.io.IOException {
    final int charsRead=br.read(readBuffer, 0, readBuffer.length);
    final boolean result=charsRead>0;
    last=result
      ?new String(readBuffer, 0, charsRead)
      :null;
    if (!result) {
      instr.close();
      br.close();
    }
    return result;
  }
  public StringLoader load(char[] readBuffer, StringBuilder sb) throws java.io.IOException {
    while (true) {
      final int charsRead=br.read(readBuffer, 0, readBuffer.length);
      if (charsRead>0) 
        sb.append(readBuffer, 0, charsRead);
      else{
        instr.close();
        br.close();
        return this;
      }
    }
  }
  public StringLoader load(int bufferSize, StringBuilder sb) throws java.io.IOException {
    return load(new char[bufferSize], sb);
  }
  public StringLoader load(StringBuilder sb) throws java.io.IOException {
    return load(4096, sb);
  }
  public String load(int bufSize) throws java.io.IOException {
    StringBuilder sb=new StringBuilder();
    load(bufSize, sb);
    return sb.toString();
  }
  public String load() throws java.io.IOException {
    StringBuilder sb=new StringBuilder();
    load(4096, sb);
    return sb.toString();
  }
  public String get() {
    return last;
  }
  
  public static void main(String[] args) throws java.io.IOException {
    StringLoader sl=new StringLoader(System.in);
    StringBuilder sb=new StringBuilder();
    System.out.println("\n\nStarting...");
    if (args.length>0 && args[0].equals("-sb")) {
      sl.load(120, sb);
      System.out.println(sb.toString());
    }
    else {
      char[] buf=new char[16];
      while (sl.load(buf))
        System.out.print(sl.get());
      System.out.println();
    }
    System.out.println("...Finished\n\n");
  }
}
