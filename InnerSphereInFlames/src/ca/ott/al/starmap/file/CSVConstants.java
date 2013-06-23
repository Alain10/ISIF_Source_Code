package ca.ott.al.starmap.file;

public interface CSVConstants {
    
    //Constants for recognizing the rows of the CSV
    public static final String headingKey = "heading";
    public static final String personKey = "person";
    public static final String factionKey = "faction";
    public static final String techKey = "tech";
    public static final String systemKey = "star system";
    public static final String warshipKey = "warship";
    public static final String militaryUnitKey = "military unit";
    public static final String correctionKey = "correction";
    public static final String factoryKey = "factory";
    public static final String diplomacyKey = "diplomacy";
    
    //Constants for distinguishing types of military forces
    public static final String regularForce = "regular";
    public static final String garrisonForce = "garrison";
    public static final String mercenaryForce = "mercenary";
    
    //Constants for military production
    public static final String productionItemKey = "production item";
    public static final String productionOrderKey = "production order";
    
    //Constants for faction orders
    public static final String factionOrderKey = "factionOrder";
    public static final String buildFortification = "build fortification";
    public static final String buildFactory = "build Factory";
        
}
