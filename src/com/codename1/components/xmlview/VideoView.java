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

import com.codename1.components.MediaPlayer;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Display;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.xml.Element;

/**
 * A View factory for displaying videos.  This will render a screenshot
 * of the video, that should be specified by the <code>&lt;video&gt;</code> tag's 
 * <code>poster</code> attribute.  When a user clicks on the placeholder, it will
 * be replaced by the video, and it will start playing.
 * 
 * <h3>Attributes</h3>
 * <table summary="Attributes"><tr><th>Name</th><th>Description</th><th>Required</th><th>Default</th></tr>
 * <tr><td>src</td><td>The URL to the video.</td><td>Yes</td><td></td></tr>
 * <tr><td>poster</td><td>The URL to the poster frame/screen  placeholder image.</td><td>Yes</td><td></td></tr>
 * <tr><td>uiid</td><td>The UIID for the placeholder image.</td><td>No</td><td>VideoView</td></tr>
 * </table>
 * 
 * <h3>Example</h3>
 * <p><code>&lt;video src="http://example.com/myvideo.mp4" poster="http://example.com/mimage.jpg" uiid="MyVideoView"/&gt;</code></p>
 * 
 * @author shannah
 */
public class VideoView implements XMLView.ViewFactory {

    /**
     * Generates video view.
     * 
     * {@inheritDoc}
     */
    @Override
    public Component createView(Element el, XMLView context) {
        String posterUrl = el.getAttribute("poster");
        if (posterUrl != null) {
            posterUrl = context.toAbsoluteUrl(posterUrl);
        }
        final String src = context.toAbsoluteUrl(el.getAttribute("src"));
        Image posterImg = null;
        if (posterUrl != null) {
            String placeholder = el.getAttribute("placeholder");
            if (placeholder == null) {
                placeholder = "video";
            }
            

            posterImg = context.getImage(posterUrl, placeholder, XMLView.ASPECT_16X9);
        } else {
            posterImg = context.getDefault16x9PlaceholderImage();
        }
        final Image playIcon = context.getIcon("\uf144", posterImg.getWidth()/9, posterImg.getWidth()/9);
        final Button placeholderBtn = new Button(posterImg) {

            @Override
            public void paint(Graphics g) {
                int x = this.getX();
                int y = this.getY();
                int w = this.getWidth();
                int h = this.getHeight();
                super.paint(g);
                g.drawImage(playIcon, x + w/2 - playIcon.getWidth()/2, y + h/2 - playIcon.getHeight()/2);
                
            }
            
        };
        
        placeholderBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    
                    final MediaPlayer mp = new MediaPlayer() {
                        boolean replaced=false;
                        @Override
                        public void paint(Graphics g) {
                            if (!replaced) {
                                int absY = this.getAbsoluteY();
                                int h = this.getHeight();

                                if (absY < 0 || absY + h > Display.getInstance().getDisplayHeight()) {
                                    final MediaPlayer mp = this;
                                    replaced = true;
                                    Display.getInstance().callSerially(new Runnable() {

                                        public void run() {
                                            if (mp.getParent() != null) {

                                                mp.getParent().replace(mp, placeholderBtn, null);
                                                
                                            }
                                        }

                                    });
                                }
                            }
                            super.paint(g); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        protected Dimension calcPreferredSize() {
                            return new Dimension(placeholderBtn.getWidth(), placeholderBtn.getHeight());
                        }
                        
                        
                         
                    };
                    mp.setAutoplay(true);
                    
                    //mp.setWidth(placeholderBtn.getWidth());
                    //mp.setHeight(placeholderBtn.getHeight());
                    mp.setUIID("MediaPlayer");
                    //mp.setPreferredW(placeholderBtn.getWidth());
                    //mp.setPreferredH(placeholderBtn.getHeight());
                    mp.setDataSource(src);
                    placeholderBtn.getParent().replace(placeholderBtn, mp, null);
                    
                    
                    ScrollListener l = new ScrollListener() {

                        public void scrollChanged(int scrollX, int scrollY, int oldscrollX, int oldscrollY) {
                            mp.getParent().replace(mp, placeholderBtn, null);
                        }
                        
                    };
                    
                    
                    
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        String uiid = el.getAttribute("uiid");
        if (uiid == null) {
            uiid = "VideoView";
        }
        placeholderBtn.setUIID(uiid);
        
        
        return placeholderBtn;
        
    }
    
}
