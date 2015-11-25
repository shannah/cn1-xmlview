/**
 * Copyright 2015 Codename One
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.codename1.components.xmlview;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.URLImage;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.util.Callback;
import com.codename1.xml.Element;
import com.codename1.xml.XMLParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


/**
 * A component that renders XML content as a visual component.  Works like
 * an HTML viewer, except that it is far more minimal, performant, and 
 * extensible.
 * @author shannah
 */
public class XMLView extends Container {
    
    /**
     * 1:1 aspect ratio
     */
    public static final int ASPECT_SQUARE=1;
    
    /**
     * 16:9 aspect ratio
     */
    public static final int ASPECT_16X9=2;
    
    /**
     * Maps names to view factories so that a view factory can be loaded by name.
     * The name corresponds with the XML tag name that is used as a basis for the view.
     */
    private final HashMap<String,ViewFactory> factoryMap = new HashMap<String,ViewFactory>();
    
    /**
     * Reference to the theme file.  Used to load resources.
     */
    //private Resources theme;
    
    /**
     * The base URL for the currently rendered page.
     */
    private String baseURL;
    
    /**
     * Square image placeholder.
     */
    private EncodedImage defaultSquarePlaceholderImage;
    
    /**
     * 16x9 image placeholder
     */
    private EncodedImage default16x9PlaceholderImage;
    
    /**
     * The component uses some standard icons.  This map stores them by
     * name so that they can be reused.
     */
    private final HashMap<String,Image> icons = new HashMap<String, Image>();
    
    /**
     * The font used to generate icons.  Usually fontawesome.
     */
    private final Font iconFont;
    
    /**
     * Default icon font color.
     */
    private int defaultIconColor = 0xCCCCCC;
    
    
    private static Resources defaultTheme;
    
    
    
    private static void installTheme() {
        if (defaultTheme == null) {
            try {
                defaultTheme = Resources.openLayered("/com.codename1.components.xmlview");
                com.codename1.ui.plaf.UIManager.getInstance().addThemeProps(defaultTheme.getTheme(defaultTheme.getThemeResourceNames()[0]));
            } catch (IOException ex) {
                Log.e(ex);
            }
        }
    }
    
    /**
     * Retrieves a placeholder image for the standard square image size.  
     * @return Default placeholder image.
     */
    public EncodedImage getDefaultSquarePlaceholderImage() {
        if (defaultSquarePlaceholderImage == null || defaultSquarePlaceholderImage.getWidth() != this.getWidth()) {
            if (this.getWidth() == 0) {
                this.setWidth(Display.getInstance().getDisplayWidth());
            }
            defaultSquarePlaceholderImage = EncodedImage.createFromImage(Image.createImage(this.getWidth(), this.getWidth()), false);
        }
        return defaultSquarePlaceholderImage;
    }
    
    
    /**
     * Retrieves the placeholder for 16x9 images.
     * @return Default 16x9 placeholder image.
     */
    public EncodedImage getDefault16x9PlaceholderImage() {
        if (default16x9PlaceholderImage == null || default16x9PlaceholderImage.getWidth() != this.getWidth()) {
            double height = ((double)this.getWidth()) * 9.0 / 16.0;
            default16x9PlaceholderImage = EncodedImage.createFromImage(Image.createImage(this.getWidth(), (int)Math.round(height)), false);
        }
        return default16x9PlaceholderImage;
    }
    
    /**
     * Caches placeholder images by name.
     */
    private final Map<String,EncodedImage> placeholders = new HashMap<String,EncodedImage>();
    
    /**
     * Sets a placeholder image.
     * @param name The name of the image.
     * @param img The placeholder image.
     */
    public void setPlaceholderImage(String name, EncodedImage img) {
        placeholders.put(name, img);
    }
    
    /**
     * Gets a placeholder image.
     * @param name The name of the placeholder image.
     * @param aspectPreference The aspect ratio preference.  One of {@link #ASPECT_16X9} and {@link #ASPECT_SQUARE}.
     * @return Placeholder image with specified name and aspect ration preference.
     */
    public EncodedImage getPlaceholderImage(String name, int aspectPreference) {
        EncodedImage img = placeholders.get(name);
        if (img == null) {
            //UIManager.getIcon(img)
            img = (EncodedImage)UIManager.getInstance().getThemeImageConstant(name);
            if (img != null) {
                placeholders.put(name, img);
            }
        }
        if (img == null) {
            switch (aspectPreference) {
                case ASPECT_16X9:
                    img = getDefault16x9PlaceholderImage();
                    break;
                default:
                    img = getDefaultSquarePlaceholderImage();
                    
            }
            if (img != null) {
                placeholders.put(name, img);
            }
        }
        return img;
    }
    
    /**
     * Gets an image from a particular URL and stretches it to the given aspect ratio.
     * @param url The URL of the image.
     * @param placeholder The placeholder for the image.
     * @param aspectPreference Preferred aspect ratio for the image.
     * @return The image at the given URL.
     */
    public Image getImage(String url, String placeholder, int aspectPreference) {
        url = toAbsoluteUrl(url);
        return URLImage.createToStorage(getPlaceholderImage(placeholder, aspectPreference), placeholder + "@"+this.getWidth()+";" + url, url, URLImage.RESIZE_SCALE_TO_FILL);
    }
    
    /**
     * Gets a font icon for a particular character code in a specified width, 
     * height, and color
     * @param charCode The char code to display.
     * @param width The width of the rendered icon.
     * @param height The height of the rendered icon.
     * @param color THe color of the rendered icon.
     * @return Prerendered image of the icon.
     */
    public Image getIcon(String charCode, int width, int height, int color) {
        String key = charCode+"@"+width+"x"+height+"#"+color;
        Image icn = icons.get(key);
        if (icn == null) {
            icn = FontImage.createFixed(charCode, iconFont, color, width, height);
            icons.put(key, icn);
        }
        return icn;
        
    }
    
    /**
     * Gets image icon using the icon font.  Default icon font is fontawesome.
     * @param charCode The char code to render.  This is font-dependent.
     * @param width The width to render the image in px.
     * @param height The height to render the image in px.
     * @return The Image with the icon rendered with the given character code. From FontAwesome.
     */
    public Image getIcon(String charCode, int width, int height) {
        return getIcon(charCode, width, height, defaultIconColor);
    }
    
    /**
     * Interface to be implemented by all view factories. A view factory 
     * creates a view given an input XML element.
     */
    public static  interface ViewFactory {
        
        /**
         * Creates a view from a model element.
         * @param el The XML element from which to create the view.
         * @param context The XMLView context in which the view is created.
         * @return A Component that can be added to the XMLView.
         */
        Component createView(Element el, XMLView context);
    }
    
    /**
     * Default constructor.
     */
    //public XMLView() {
    //    this(null);
    //    setUIID("XMLView");
    //}
    
    /**
     * Creates an XML view.
     */
    public XMLView(/*Resources theme*/) {
        installTheme();
        setUIID("XMLView");
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        //this.theme = theme;
        
        Label l = new Label();
        l.setUIID("XMLViewIcon");
        iconFont = Font.createTrueTypeFont("FontAwesome", "fontawesome-webfont.ttf");
    }
    
    /**
     * Sets the XML contents of the view.
     * @param xml The XML to render.
     * @param base The base URL of the document.  This is used to retrieve relative
     * resources like images that are referenced in the document.
     */
    public void setXML(String xml, String base) {
        baseURL = base;
        setXML(xml);
    }
    
    /**
     * Sets the resource file used by this view.
     * @param theme The resource file.
     */
    //public void setTheme(Resources theme) {
    //    this.theme = theme;
    //}
    
    /**
     * Sets the XML contents of the view.  The base URL will be taken from the "base" attribute
     * of either the root element, the head element, or the body element.
     * @param xml The XML to load.
     */
    public void setXML(String xml) {
        try {
            XMLParser parser = new XMLParser();
            Element root = parser.parse(new InputStreamReader(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
            setXML(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Sets the XML contents to view.
     * @param root The root XML element to display.
     * @param base The base URL.  Used for retrieving dependent resources like images
     * that are referenced using a relative path.
     */
    public void setXML(Element root, String base) {
        baseURL = base;
        setXML(root);
    }
    
    /**
     * Sets the XML contents of the view.  The base URL will be extracted from the "base" attribute
     * of the root element, the head element, or the body element.
     * @param root The root element to display.
     */
    public void setXML(Element root) {
        //XMLParser parser = new XMLParser();
        //System.out.println("Setting XML");
        if (root.getAttribute("base") != null) {
            baseURL = root.getAttribute("base");
        }
        if ("doc".equals(root.getTagName())) {
            //System.out.println("Root is doc");
            Iterator <Element> it = root.iterator();
            while (it.hasNext()) {
                Element nex = it.next();
                if ("head".equals(nex.getTagName())) {
                    Vector baseTags = nex.getDescendantsByTagName("base");
                    if (!baseTags.isEmpty()) {
                        Element baseTag = (Element)baseTags.get(0);
                        baseURL = baseTag.getAttribute("href");
                    }
                } else if ("body".equals(nex.getTagName())) {
                    root = nex;
                    if (root.getAttribute("base") != null) {
                        baseURL = root.getAttribute("base");
                    }
                }
            }
            
        }
        
        //Element root = parser.parse(new StringReader(xmlstr));
        this.removeAll();
        Iterator<Element> it = root.iterator();
        while (it.hasNext()) {
            //System.out.println("Adding element");
            Component c = createView(it.next());
            if (c != null) {
                addComponent(c);
            }
        }
        revalidate();
    }
    
    /**
     * Creates a view for the given XML element.  This will check each registered
     * view factory to see if it supports the element, and will return the component
     * generated by the first factory that supports it.  If none can be found, then
     * this will return null.
     * @param el The XML element to convert to a view.
     * @return The generated component or null if no factories are registered for 
     * this element.
     */
    public Component createView(Element el) {
        String tagName = el.getTagName();
        ViewFactory factory = factoryMap.get(tagName);
        if (factory != null) {
            return factory.createView(el, this);
        }
        return null;
    }
    
    /**
     * Gets the theme file.
     * @return 
     */
    //public Resources getTheme() {
    //    return theme;
    //}
    
    /**
     * Converts a url to an absolute URL.  This is idempotent.
     * @param url The URL to convert.
     * @return The resulting absolute URL.
     */
    public String toAbsoluteUrl(String url) {
        if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
            return url;
        } else if (url.indexOf("/") == 0) {
            int slashPos = baseURL.indexOf('/', 8);
            if (slashPos >= 0) {
                return baseURL.substring(0, slashPos) + url;
            } else {
                return baseURL + url;
            }
            
        } else {
            return baseURL + url;
        }
        
    }
    
    /**
     * Registers a view factory with the view.
     * @param tagName The tag name that the view factory processes.
     * @param factory The view factory.
     */
    public void registerViewFactory(String tagName, ViewFactory factory) {
        factoryMap.put(tagName, factory);
    }
    
    /**
     * Loads an XML page from a url.  This will load asynchronously.
     * @param url The URL to load.
     * @param callback A callback that is called when loading is complete.
     */
    public void load(String url, final Callback<Element> callback) {
        ConnectionRequest req = new ConnectionRequest() {

            @Override
            protected void readResponse(InputStream input) throws IOException {
                XMLParser parser = new XMLParser();
                final Element el = parser.parse(new InputStreamReader(input));
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {
                        setXML(el);
                        callback.onSucess(el);
                    }
                });
            }
            
        };
        req.setUrl(url);
        req.setPost(false);
        req.setHttpMethod("GET");
        NetworkManager.getInstance().addToQueue(req);
        
    }
    
    
}
