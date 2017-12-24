/** This highlights a sub-heading on a page when you click a link
    that takes you to that specific sub-heading. */
function tmplzHilite() {
  var tpoundy=new RegExp(".*\#");
  var tloc=window.location.toString();
  if (tpoundy.test(tloc)){
    var taname=tloc.replace(tpoundy, "");
    var elem=document.getElementById(taname+"Heading");
    if (elem!=null){
      elem.style.backgroundColor="#E5E5FF";
      //elem.style.fontSize="16px";
      //elem.style.textDecoration="underline";
      //elem.innerHTML+=" <span style=\"color:red;\">*</span>";
    }
  }
}
