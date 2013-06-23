package ca.ott.al.starmap.core.unit;

import java.io.Serializable;

public class JumpshipClass implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -6338588425282684552L;
    
    protected int mass;
    protected int dropshipCapacity;
    protected int driveIntegrity;
    
    public int getDropshipCapacity() {
        return dropshipCapacity;
    }
    
    public int getMass() {
        return mass;
    }

    public int getDriveIntegrity() {
        return driveIntegrity;
    }    
}
