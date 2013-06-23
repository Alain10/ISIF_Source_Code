package ca.ott.al.starmap.core.faction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ca.ott.al.starmap.core.unit.Reinforcement;;

public class ReinforcementPool implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 5916806284485929668L;
    
    private List<Reinforcement> pool;
    
    public ReinforcementPool(){
        pool = new ArrayList<Reinforcement>();
    }
    
    public List<Reinforcement> getPool(){
        List<Reinforcement> clone = new ArrayList<Reinforcement>();
        clone.addAll(pool);
        return clone;
    }
    
    public void add(Reinforcement reinforcement){
        pool.add(reinforcement);
    }
    
    public void remove(Reinforcement reinforcement){
        pool.remove(reinforcement);
    }

    public void addAll(Vector<Reinforcement> reinforcements) {
        pool.addAll(reinforcements);
    }
    
}
