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

import com.codename1.components.ImageViewer;
import com.codename1.ui.Component;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.URLImage;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.xml.Element;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A factory to render an image carousel in an XMLView.
 * 
 * <h3>Attributes</h3>
 * <table summary="attributes"><tr><th>Name</th><th>Description</th><th>Required</th><th>Default</th></tr>
 * <tr><td>placeholder</td><td>The name of the placeholder image to be displayed while the images are loading.</td><td>No</td><td>image</td></tr>
 * <tr><td>uiid</td><td>The UIID to use for the image component's style.</td><td>No</td><td>CarouselView</td></tr>
 * </table>
 * 
 * <h3>Nested Tags</h3>
 * 
 * <p>For each image in the carousel, you would nest an <code>&lt;img&gt;</code> tag.:</p>
 * 
 * <h3>Example</h3>
 * 
 * <pre><code>
 * &lt;carousel uiid="MyCarouselView"&gt;
 *      &lt;img src="http://example.com/img1.png"/&gt;
 *      &lt;img src="http://example.com/img2.png"/&gt;
 *      &lt;img src="http://example.com/img3.png"/&gt;
 * &lt;/carousel&gt;
 * </code></pre>
 * 
 * @author shannah
 */
public class CarouselView implements XMLView.ViewFactory {

    @Override
    public Component createView(Element el, XMLView context) {
        ImageViewer iv = new ImageViewer();
        List<Image> images = new ArrayList<Image>();
        Iterator<Element> it = el.iterator();
        String placeholderName = el.getAttribute("placeholder");
        if (placeholderName == null) {
            placeholderName = "image";
        }
        EncodedImage placeholder =  context.getPlaceholderImage(placeholderName, XMLView.ASPECT_SQUARE);
        iv.setSwipePlaceholder(placeholder);
        while (it.hasNext()) {
            Element nex = it.next();
            if ("img".equals(nex.getTagName())) {
                System.out.println("Getting image from "+nex.getAttribute("src"));
                String src = context.toAbsoluteUrl(nex.getAttribute("src"));
                images.add(context.getImage(src, placeholderName, XMLView.ASPECT_SQUARE));
            }
        }
        for (Image img : images) {
            if (img instanceof URLImage) {
                ((URLImage)img).fetch();
            }
        }
        iv.setImageList(new DefaultListModel<Image>(images));
        
        String uiid = el.getAttribute("uiid");
        if (uiid == null) {
            uiid = "CarouselView";
        }
        iv.setUIID(uiid);
        
        
        return iv;
    }
    
}
