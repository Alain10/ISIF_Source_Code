package ca.ott.al.starmap.core.transport;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import ca.ott.al.starmap.core.MilitaryForceHolder;
import ca.ott.al.starmap.core.unit.MilitaryForce;

public class TransportFleet implements MilitaryForceHolder, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -2038967093628564731L;
    
    /**
     * A transport fleet can only transport one force.
     */
    private MilitaryForce forceEmbarked;
    private int transportPoints;
    
    public TransportFleet(int pointsRequired){
        forceEmbarked = null;
        transportPoints = pointsRequired;
    }
    
    @Override
    public Set<MilitaryForce> getMilitaryForces() {
        Set<MilitaryForce> militaryForceSet = new TreeSet<MilitaryForce>();
        militaryForceSet.add(forceEmbarked);
        return militaryForceSet;
    }

    @Override
    public boolean addMilitaryForce(MilitaryForce militaryForce) {
        if(forceEmbarked == null){
            forceEmbarked = militaryForce;
            return true;
        }   
        return false;
    }

    @Override
    public boolean removeMilitaryForce(MilitaryForce militaryForce) {
        //It must be the same instance of the object, it should have the 
        //same reference
        if(militaryForce == forceEmbarked){
            forceEmbarked = null;
            return true;
        }
        return false;
    }

    public int getTransportPoints() {
        return transportPoints;
    }
}
