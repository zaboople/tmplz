package org.tmotte.common.io;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/** 
 * Routes a java.io.InputStream directly to a java.io.OutputStream.
 */
public class Passthru {

  /** 
   * Invokes pass() with approxlen parameter of 512, which is appropriate for small text files (like this one).
   */
  public static void pass(InputStream inStream, OutputStream outStream) throws IOException{
    pass(inStream, outStream, 512);
  }

  /** 
   * Writes everything from inStream to outStream
   */
  public static void pass(InputStream inStream, OutputStream outStream, int approxlen) throws IOException{
    byte[] readBuffer=new byte[approxlen];
    try {
     int bytesRead;
     while ((bytesRead=inStream.read(readBuffer, 0, readBuffer.length))>0)
       outStream.write(readBuffer, 0, bytesRead);
    } finally {
      inStream.close();
      outStream.close();
    }    
  }

}