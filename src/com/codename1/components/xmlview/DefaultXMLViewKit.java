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

/**
 * A utility class that instantiates the basic view factories and registers them
 * with the XML view.  The default view factories are {@link ImageView}, {@link ParagraphView}, 
 * {@link VideoView}, and {@link CarouselView}.
 * 
 * @author shannah
 */
public class DefaultXMLViewKit {
    public void install(XMLView view) {
        view.registerViewFactory("img", new ImageView());
        view.registerViewFactory("p", new ParagraphView());
        view.registerViewFactory("video", new VideoView());
        view.registerViewFactory("carousel", new CarouselView());
    }
}
