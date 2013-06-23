package ca.ott.al.starmap.core;

import java.util.Set;

import ca.ott.al.starmap.core.unit.MilitaryForce;
import ca.ott.al.starmap.core.unit.StarMapUnit;

public interface MilitaryForceHolder {
    public Set<MilitaryForce> getMilitaryForces();
    
    public boolean addMilitaryForce(MilitaryForce militaryForce);
    
    public boolean removeMilitaryForce(MilitaryForce militaryForce);
}
