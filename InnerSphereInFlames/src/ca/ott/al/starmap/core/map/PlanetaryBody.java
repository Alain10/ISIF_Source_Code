package ca.ott.al.starmap.core.map;


public class PlanetaryBody extends StarMapObject {

    //Many of these are detailed attributes that would be directly used in ISIF
    // Noble Ruler: Grand Duke Morgan Kell
    // Star Type (Recharge Time): G3V (184 hours)
    // Position in System: 4th
    // Time to Jump Point: 8.53 days
    // Number of Satellites: 2 (Deven and Thorwald)
    // Surface Gravity: 1.01
    // Atm. Pressure: Standard (Breathable)
    // Equatorial Temperature: 32�C (Mild-Temperate)
    // Surface Water: 74%
    // Recharging Station: Zenith, Nadir
    // HPG Class Type: A
    // Highest Native Life: Mammals
    // Population: 2,098,000,000

    /**
     * 
     */
    private static final long serialVersionUID = -8341584008298775721L;

    public enum AtmosphericPressure {
        vaccum, low, standard, high, veryHigh
    }

    public enum Atmosphere {
        breathable, notBreathable, noSurface
    }

    public enum Life {
        mammal, reptile, insect, plants, primitivePlants, none
    }

    private double surfaceGravity; // express as multiple of G
    private double equatorialTemperature; // express in degrees Celcius
    private double surfaceWater; // express as fraction ex: 0.74 -> 74%
    private AtmosphericPressure atmosphericPressure;
    private Atmosphere atmosphere;
    private Life life;
    private long population;

    private String ruler;
    private String description;
    
    
    /**
     * Basic constructor with only the planet name
     */
    public PlanetaryBody(String name) {
        super(name);
    }

    /**
     * Complete constructor with descriptive planet details
     */
    public PlanetaryBody(String name, int positionInSystem,
            double surfaceGravity, double equatorialTemperature,
            double surfaceWater, AtmosphericPressure atmosphericPressure,
            Atmosphere atmosphere, Life life, long population,
            String ruler, String description) {
        super(name);
        this.surfaceGravity = surfaceGravity;
        this.equatorialTemperature = equatorialTemperature;
        this.surfaceWater = surfaceWater;
        this.atmosphericPressure = atmosphericPressure;
        this.atmosphere = atmosphere;
        this.life = life;
        this.population = population;
    }


    
    //Access Methods===========================================================
    public double getEquatorialTemperature() {
        return equatorialTemperature;
    }

    public void setEquatorialTemperature(float equatorialTemperature) {
        this.equatorialTemperature = equatorialTemperature;
    }

    public double getSurfaceWater() {
        return surfaceWater;
    }

    public void setSurfaceWater(float surfaceWater) {
        this.surfaceWater = surfaceWater;
    }

    public AtmosphericPressure getAtmosphericPressure() {
        return atmosphericPressure;
    }

    public void setAtmosphericPressure(AtmosphericPressure atmosphericPressure) {
        this.atmosphericPressure = atmosphericPressure;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    public Life getLife() {
        return life;
    }

    public void setLife(Life life) {
        this.life = life;
    }

    public String getRuler() {
        return ruler;
    }

    public void setRuler(String ruler) {
        this.ruler = ruler;
    }

    public double getSurfaceGravity() {
        return surfaceGravity;
    }

    public long getPopulation() {
        return population;
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    
}
