# CN1 XMLView Library

A codename one UI component for rendering XML.  This is similar to an HTML renderer except it is far simpler, performant,
and pluggable.  It is useful for displaying rich content (text, images, videos) within a Codename One form.

## License

Apache 2.0

## Features

* Light-weight component.
* Comes with support for minimal set of tags: `<p>`, `<img>`, `<carousel>`, and `<video>`.
* Pluggable.  Add your own views and support your own custom tags by implementing your own view factory.
* Easily Styleable.  All built-in components support the `uiid` attribute which specifies the UIID to use for styling the component.

## Installation

Copy the `xmlview.cn1lib` into your project's "lib" directory and refresh libs.

## Usage Example

~~~~
Form hi = new Form("Wiki News Feed");
hi.setLayout(new BorderLayout());
XMLView xmlView = new XMLView();
new DefaultXMLViewKit().install(xmlView);

// Loading XML from a string
xmlView.setXML(Util.readToString(Display.getInstance().getResourceAsStream(null, "/SampleNewsFeed.xml"), "UTF-8"));

// Alternate syntax loading XML from a URL.
//xmlView.load("http://example.com/myfile.xml", new Callback<Element>() {
//    public void onSuccess(Element root){ ... }
//    ....
//});
hi.addComponent(BorderLayout.CENTER, xmlView);
hi.show();

~~~~

[Sample XML File](https://github.com/shannah/cn1-xmlview/blob/master/src/SampleNewsFeed.xml)
[Sample App -- uses Cloudinary API for image resizing](https://github.com/shannah/cn1-xmlview/blob/master/src/com/codename1/demos/xmlview/XMLViewDemo.java)

What it looks like:

![image](https://cloud.githubusercontent.com/assets/2677562/11411088/940b6dba-9381-11e5-9662-d9a486e51cb7.png)

## Limitations

This is not meant to be a full HTML renderer.  If you look at most "feed" type apps, like Facebook, Twitter, and the like, they don't perform complex page rendering.  They usually just display paragraph text, images, and videos in a responsive format, where images are all square, videos are 16x9, and both images and videos span the width of the parent component (and often the full device width).  That is the kind of thing that this component excels at.

For rendering, this component doesn't traverse down the hierarchy of the XML document.  It merely takes the top-level children, and passes each to the appropriate view factory, and lays out the components returned from the view factory vertically (in a box layout).

## Documentation

* [Javadocs](http://shannah.github.io/cn1-xmlview/javadoc/index.html)
* Styling Views (Under construction)
* Creating Custom View Factories (Under construction)

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
