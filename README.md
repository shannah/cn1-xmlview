# CN1 XMLView Library

A codename one UI component for rendering XML.  This is similar to an HTML renderer except it is far simpler, performant,
and pluggable.  It is useful for displaying rich content (text, images, videos) within a Codename One form.

## License

Apache 2.0

## Features

* Light-weight component.
* Comes with support for minimal set of tags: `<p>`, `<img>`, `<carousel>`, and `<video>`.
* Pluggable.  Add your own views and support your own custom tags by implementing your own view factory.

## Installation

Copy the `xmlview.cn1lib` into your project's "lib" directory and refresh libs.

## Example

## Documentation

[Javadocs](http://shannah.github.io/cn1-xmlview/javadoc/index.html)

## Building Form Source

~~~~
$ git clone https://github.com/shannah/cn1-xmlview.git
$ cd cn1-xmlview
$ ant -f configure.xml
$ ant jar
~~~~

You'll find `xmlview.cn1lib` inside the `dist` directory.

NOTE:  The `ant -f configure.xml` is only necessary the first time you build the source.  It will download some dependencies
like JavaSE.jar, CodenameOne.jar, etc.. that were not included with this repository to save space.
