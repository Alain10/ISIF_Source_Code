package ca.ott.al.starmap.ui;

import javax.swing.ImageIcon;

public class IconUtility {
    
    private static IconUtility util;
    
    private IconUtility(){
        
    }
    
    public static IconUtility getUtilities(){
        if(util != null){
            return util;
        }
        else {
            util = new IconUtility();
            return util;
        }
    }
    
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    public ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
