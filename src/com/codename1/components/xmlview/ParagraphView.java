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

import com.codename1.components.SpanLabel;
import com.codename1.ui.Component;
import com.codename1.xml.Element;
import java.util.Iterator;

/**
 * View factory to display a paragraph of text.
 * 
 * <h3>Attributes</h3>
 * <table summary="attributes"><tr><th>Name</th><th>Description</th><th>Required</th><th>Default</th></tr>
 * <tr><td>uiid</td><td>The UIID to use for the text.</td><td>No</td><td>ParagraphView</td></tr>
 * </table>
 * 
 * <p><b>Note:</b> A SpanLabel is used to render the paragraph.  The uiid attribute specifies the
 * SpanLabel's style.  The actual text within the spanlabel appends "Text" to the UIID.  E.g. if you 
 * specify the uiid to be ParagraphView, then the text's UIID will be ParagraphViewText.
 * 
 * <h3>Example</h3>
 * <p><code>&lt;p uiid="MyParagraphView"&gt;This is some text to be displayed in a paragraph.&lt;/p&gt;</code></p>
 * 
 * @author shannah
 */
public class ParagraphView implements XMLView.ViewFactory {

    
    /**
     * Generates paragraph view.
     * 
     * {@inheritDoc}
     */
    @Override
    public Component createView(Element el, XMLView context) {
        String text = getText(el);
        SpanLabel label = new SpanLabel(text);
        String uiid = el.getAttribute("uiid");
        if (uiid == null) {
            uiid = "ParagraphView";
        }
        label.setTextUIID(uiid+"Text");
        label.setUIID(uiid);
        
        return label;
    }
    
    private void getText(Element root, StringBuilder sb) {
        if (root.isTextElement()) {
            sb.append(root.getText());
        } else {
            Iterator<Element> it = root.iterator();
            while (it.hasNext()) {
                getText(it.next(), sb);
            }
        }
    }
    
    private String getText(Element root) {
        StringBuilder sb = new StringBuilder();
        getText(root, sb);
        return sb.toString();
    }
    
}
