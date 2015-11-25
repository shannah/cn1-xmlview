package com.codename1.demos.xmlview;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.codename1.components.xmlview.DefaultXMLViewKit;
import com.codename1.components.xmlview.XMLView;
import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;

public class XMLViewDemo {

    private Form current;
    private Resources theme;
    private Cloudinary cloudinary;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
        cloudinary = new Cloudinary("cloudinary://527557556819548:nbBxRe0KE79TYow2cIAl8PFxoe4@codename-one");
        cloudinary.config.privateCdn = false;
        // Pro users - uncomment this code to get crash reports sent to you automatically
        /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                Log.p("OS " + Display.getInstance().getPlatformName());
                Log.p("Error " + evt.getSource());
                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
                Log.e((Throwable)evt.getSource());
                Log.sendLog();
            }
        });*/
    }
    
    public void start() {
        try {
            if(current != null){
                current.show();
                return;
            }
            Form hi = new Form("Wiki News Feed");
            hi.setLayout(new BorderLayout());
            XMLView xmlView = new XMLView() {

                
                @Override
                public Image getImage(String url, String placeholder, int aspectPreference) {
                    return cloudinary.url().type("fetch").format("jpg").transformation(new Transformation().crop("fill").height(getWidth()).width(getWidth())).image(getPlaceholderImage(placeholder, aspectPreference), url);
                    //return super.getImage(url, placeholder, aspectPreference); //To change body of generated methods, choose Tools | Templates.
                }
                
                
            };
            xmlView.setScrollableY(true);
            new DefaultXMLViewKit().install(xmlView);
            
            xmlView.setXML(Util.readToString(Display.getInstance().getResourceAsStream(null, "/SampleNewsFeed.xml"), "UTF-8"));
            hi.addComponent(BorderLayout.CENTER, xmlView);
            hi.show();
        } catch (IOException ex) {
            Log.e(ex);
        }
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }

}
