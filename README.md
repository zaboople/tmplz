## Tmplz

Tmplz (thought of as a derivation of template-ease) is a library for manipulating text files using a simple markup language. Although it is HTTP/HTML/web-agnostic, Tmplz resulted from a typical need to stuff gobs of HTML with dynamic data, like any old web site; yes, it's a template language.

It's different from most, however, in that for all its niftiness, Tmplz is not a dumbed-down programming language or even a dumbed-up programming language. The Tmplz language just defines the structure of templates and forces all the programming back into standard, plain old Java.

Tmplz is written in Java and has a very simple API. The markup language is fully documented within, as HTML and written using Tmplz itself.

## Building

To build Tmplz you need a reasonable Java installation (1.6 or so) and Apache Ant. Run `ant dist` to obtain a full distribution; documentation will be in dist/bin/docs.