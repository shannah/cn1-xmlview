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

import com.codename1.ui.Component;
import com.codename1.ui.Label;
import com.codename1.xml.Element;

/**
 * A factory to render an image in an XMLView.
 * 
 * <h3>Attributes</h3>
 * <table summary="attributes"><tr><th>Name</th><th>Description</th><th>Required</th><th>Default</th></tr>
 * <tr><td>placeholder</td><td>The name of the placeholder image to be displayed while the image is loading.</td><td>No</td><td>image</td></tr>
 * <tr><td>src</td><td>The URL to the image to display.</td><td>Yes</td><td></td></tr>
 * <tr><td>uiid</td><td>The UIID to use for the image component's style.</td><td>No</td><td>ImageView</td></tr>
 * </table>
 * 
 * <h3>Example</h3>
 * <p><code>&lt;img uiid="MyParagraphView" src="http://example.com/myimage.png" placeholder="myplaceholder" /&gt;</code></p>
 * 
 * @see XMLView#setPlaceholderImage(java.lang.String, com.codename1.ui.EncodedImage) 
 * @see XMLView#getPlaceholderImage(java.lang.String, int) 
 * @author shannah
 */
public class ImageView implements XMLView.ViewFactory {

    /**
     * Generates Image view
     * 
     * {@inheritDoc}
     */
    @Override
    public Component createView(Element el, XMLView context) {
        String placeholder = el.getAttribute("placeholder");
        if (placeholder == null) {
            placeholder = "image";
        }
        String src = context.toAbsoluteUrl(el.getAttribute("src"));
        
        Label l = new Label(context.getImage(src, placeholder, XMLView.ASPECT_SQUARE));
        
        String uiid = el.getAttribute("uiid");
        if (uiid == null) {
            uiid = "ImageView";
        }
        l.setUIID(uiid);
        return l;
    }
    
}
