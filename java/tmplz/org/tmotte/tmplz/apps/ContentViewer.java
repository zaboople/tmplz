package org.tmotte.tmplz.apps;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.tmotte.tmplz.exception.TmplzException;
import org.tmotte.tmplz.exception.InternalException;
import org.tmotte.tmplz.exception.load.NoURLForPathException;
import org.tmotte.tmplz.load.builtin.ServletContextTextLoaderFactory;
import org.tmotte.tmplz.load.builtin.URLTextLoaderFactory;
import org.tmotte.tmplz.load.Path;
import org.tmotte.tmplz.load.TextLoader;
import org.tmotte.tmplz.load.TextLoaderFactory;
import org.tmotte.tmplz.load.TextLoadMgr;
import org.tmotte.tmplz.Section;
import org.tmotte.tmplz.TemplateManager;
import org.tmotte.tmplz.util.Log;
import org.tmotte.tmplz.util.TemplateInterceptor;
import org.tmotte.common.text.StackTracer;
import org.tmotte.common.text.DateFormatPool;

/**
 *  This is a simple Servlet-based Content Mgmt application. Mainly this exists for demonstration purposes, although
 *  it should be dependable enough for production use within its limited scope of capabilities. Its methods
 *  are designed to be overridden to allow for slightly enhanced functionality. <br>
 *  Logging: All logging is done via java.util.Logging, assuming
 *  the necessary Logger is mapped to org.tmotte.tmplz. <br>
 *  Templates: By default this loads templates
 *  out of the web application directory via ServletContext.getResource(), mapping the request
 *  URI to its matching template.
 */
public class ContentViewer extends HttpServlet{

  private GregorianCalendar calendar=new GregorianCalendar();
  private Map<Path, Content> contentMap=new HashMap();
  private TemplateManager templateMgr=new TemplateManager();
  static class Content {
    String text, eTag;
  }

  /////////////////////
  //                 //
  // INITIALIZATION: //
  //                 //
  /////////////////////

  /** Runs initialization as per the Servlet spec.*/
  public void init(ServletConfig config) throws ServletException {

    //Source text loaders:
    TextLoadMgr sourceLoader=templateMgr.getTextLoadMgr();
    List<TextLoaderFactory> factories=getSourceTextLoaderFactories(config);
    if (factories==null)
      throw new RuntimeException("No TextLoaderFactories supplied during initialization");
    for (TextLoaderFactory t: factories)
      sourceLoader.register(t);

    //Create template mgr and the TextLoadMgr that wraps it:
    templateMgr.setTemplateInterceptor(getTemplateInterceptor(config));
  }

  /**
   * <p>
   * Should provide a List of TextLoaderFactory instances that can load template text content.
   * That text will be parsed by the Tmplz template engine.
   * </p>
   * <p>
   * By default this registers a ServletContextTextLoaderFactory as the source for template text,
   * so all templates will be loaded from the web application directory. Additionally it looks
   * for a value named "TemplateBasePath" in ServletContext and ServletConfig; if one is found,
   * that will be prepended to requested paths before attempting to load them.
   * </p>
   * @see org.tmotte.tmplz.load.builtin.ServletContextTextLoaderFactory
   */
  public List<TextLoaderFactory> getSourceTextLoaderFactories(ServletConfig config){
    String basePath=config.getInitParameter("TemplateBasePath");
    if (basePath==null)
      basePath=config.getServletContext().getInitParameter("TemplateBasePath");
    if (basePath==null)
      basePath="";
    List<TextLoaderFactory> list=new ArrayList<TextLoaderFactory>();
    list.add(new ServletContextTextLoaderFactory(config.getServletContext(), basePath));
    return list;
  }


  /**
   * Allows templates to be intercepted on creation and manipulated for any sort of
   * desired behavior. By default, this first looks for a parameter in the ServletConfig or
   * ServletContext ("init-param" in web.xml) named "Context",  and if such is found, creates
   * a TemplateInterceptor that looks for Slots named "Context" in every template and fills them in.
   */
  public TemplateInterceptor getTemplateInterceptor(ServletConfig config){
    String s=config.getInitParameter("Context");
    if (s==null)
      s=config.getServletContext().getInitParameter("Context");
    if (s==null)
      return null;
    final String urlContext=s;
    return new TemplateInterceptor() {
      public void postLoad(Section template){
        if (urlContext!=null)
          template.replace("Context", urlContext, true);
      }
      public void preRender(Section template){}
    };
  }

  ///////////////////////
  //                   //
  // REQUEST HANDLING: //
  //                   //
  ///////////////////////

  /**
   * Everything is handled via this standard Servlet method.
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, java.io.IOException{
    java.io.PrintWriter pw=resp.getWriter();
    try {

      //Get path:
      Path path=getContentPath(req);
      if (path==null){
        resp.sendError(resp.SC_NOT_FOUND, req.getRequestURI());
        return;
      }

      //Get caching headers on source & destination:
      String eTag=req.getHeader("If-None-Match"),
             newTag=Long.toHexString(templateMgr.getETag(path));

      //Return 304 on no change:
      if (eTag!=null && newTag.equals(eTag)){
        Log.info("Returning 304 for "+path);
        resp.setStatus(resp.SC_NOT_MODIFIED);
        return;//RETURN;
      }

      //Get updated content as necessary:
      Content content=contentMap.get(path);
      if (content==null || !newTag.equals(content.eTag)) {
        content=new Content();
        content.text=templateMgr.getTemplate(path, false).toString();
        content.eTag=newTag;
        contentMap.put(path, content);
      }

      //Send back to suer
      Log.info("ContentViewer.doGet(): Sending content, and eTag "+newTag);
      resp.setHeader("ETag", newTag);
      pw.write(content.text);
      pw.flush();
    } catch (Exception e) {
      StringBuilder sb=new StringBuilder();
      getExceptionText(sb, e);
      Log.error("ContentViewer.doGet() ", sb.toString());
      displayError(pw, e);
    }
  }

  /**
   * Obtains a Path object based on the HttpServletRequest. That Path will then be passed to the TextLoaderFactory
   * instance(s) created by getSourceTextLoaderFactories().
   * <p>
   * The default behavior, in order, is:
   * <ol>
   *   <li>The path is assumed to be the part of request.getURI() that comes after req.getContextPath(); this is
   *    everything that comes after the part of the URI that is mapped to the
   *    servlet, i.e. http://myserver/myservlet/foo/index.html will map to /foo/index.html.
   *   <li> The path will be logged at level java.util.logging.Level.INFO.
   *   <li>The path may not contain ".." and it must start with a "/". These are security checks that
   *    among other things prevent someone from asking for content like "http://evil.com/evil.html" or
   *    "file:///etc/password". If these constraints are not met, null is returned, which will turn
   *    into an HTTP 404-not-found later.
   *    <li>If the path ends with "/", it is converted to end with "/index.html". Note that this does
   *    not generate a redirect to /index.html, which would probably be better.
   *  </ol>
   *  @return A Path instance; or if the path cannot be served, null, resulting in an eventual HTTP 404 Not Found.
   */
  public Path getContentPath(HttpServletRequest req) {
    String uri=req.getRequestURI(),
           contentPath=null;
    try {
      contentPath=uri.substring(req.getContextPath().length());
    } catch (Exception e) {
      throw new InternalException(
        "Something went wrong with getRequestURI()="+uri+" getContextPath()="+req.getContextPath(),
        e
      );
    }
    Log.fine("***********");
    Log.info("ContentViewer", "getContentPath()", contentPath);
    if (contentPath.indexOf("..")>-1 || !contentPath.startsWith("/")) {
      Log.info("ContentViewer", "getContentPath()", contentPath, "ILLEGAL CONTENT PATH");
      return null;
    }
    if (contentPath.endsWith("/"))
      contentPath+="index.html";
    return new Path(contentPath);
  }

  /**
   * When an error occurs, this displays it inside of a nested html-body-pre set of elements.
   */
  public void displayError(PrintWriter pw, Exception e) {
    pw.append("<html><body><pre>");
    getExceptionHTML(pw, e);
    pw.append("</pre></body></html>");
  }
  /**
   * This is invoked by displayError, and recurses through the nested Exceptions to generate the actual error message.
   */
  protected void getExceptionHTML(PrintWriter a, Throwable e) {
    if (e==null)
      return;
    else
    if (e instanceof TmplzException) {
      a.append(e.getMessage().replaceAll("<", "&lt;").replaceAll(">", "&gt;")+"\n");
      getExceptionHTML(a, e.getCause());
    }
    else
      StackTracer.recurseStackTrace(e, a);
  }
  protected void getExceptionText(Appendable a, Throwable e) throws java.io.IOException {
    if (e==null)
      return;
    else
    if (e instanceof TmplzException) {
      a.append(e.getClass().getSimpleName());
      a.append(": ");
      a.append(e.getMessage()+"\n");
      getExceptionText(a, e.getCause());
    }
    else
      StackTracer.recurseStackTrace(e, a);
  }


  ///////////////////////////////
  //                           //
  // LAST MODIFIED DATE STUFF: //
  //                           //
  ///////////////////////////////


  protected String formatLastModifiedDate(long time) {
    return datePool.format(new java.util.Date(time));
  }
  protected long parseIfModifiedSinceDate(String time) {
    try {
      java.util.Date date=datePool.parse(time);
      //Log.finest("ContentViewer", "parseIfModifiedSinceDate()", "Parsing if-modified-since of "+time+" to "+date);
      return date.getTime();
    } catch (Exception e) {
      Log.finest("ContentViewer", "parseIfModifiedSinceDate()", "WARNING: failed to parse If-Modified-Since : "+time+" error="+e.getMessage());
      return -1;
    }
  }
  private DateFormatPool datePool=new DateFormatPool(){
    public DateFormat create() {
      SimpleDateFormat sdf=new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'z", Locale.US);
      sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
      return sdf;
    }
  };

}