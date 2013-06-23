package ca.ott.al.starmap.core.map;

import java.io.Serializable;

public abstract class StarMapObject implements Comparable<StarMapObject>, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -2341712442880404003L;
    
    protected String name;

    public StarMapObject(){}
    
    public StarMapObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    //Makes all StarMap Objects comparable 
    public int compareTo(StarMapObject object){
        return this.getName().compareTo(object.getName());
    }

    public String toString(){
        return getName();
    }
}
