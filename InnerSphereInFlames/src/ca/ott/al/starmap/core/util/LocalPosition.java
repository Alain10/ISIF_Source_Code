package ca.ott.al.starmap.core.util;

import java.io.Serializable;

import ca.ott.al.starmap.core.map.PlanetaryBody;
import ca.ott.al.starmap.core.map.Star;
import ca.ott.al.starmap.core.unit.SpaceCraft;

public class LocalPosition implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1303703967929282776L;

    public enum SpaceCraftPosition {
        planetSurface, orbitingPlanet, stellarNadir, stellarZenith, inTransitToZenith, 
        inTransitToNadir, inTransitToPlanet, adrift, inTransitToAdrift, piratePoint, 
        inTransitToPiratePoint, dockedToSpaceCraft
    }

    SpaceCraftPosition position;

    PlanetaryBody planet;
    Star star;
    SpaceCraft mothership;

    double eta = 0;

    // The constructors restrict where a spacecraft may be placed when created
    /**
     * Use when creating a spacecraft near a planet
     * 
     * @param position
     *            Restricted to planetSurface and orbitingPlanet
     * @param planet
     *            The planet in question
     */
    public LocalPosition(SpaceCraftPosition position, PlanetaryBody planet) {
        this.position = position;
        this.planet = planet;
    }

    /**
     * Use when creating a spacecraft in the general region of a star
     * 
     * @param position
     *            Restricted to stellarNadir, stellarZenith, adrift, piratePoint
     * @param star
     *            The star in question
     */
    public LocalPosition(SpaceCraftPosition position, Star star) {
        this.position = position;
        this.star = star;
    }

    /**
     * Use when creating a spacecraft docked to a Jumpship, Warship or Space
     * Station
     * 
     * @param mothership
     */
    public LocalPosition(SpaceCraft mothership) {
        this.position = SpaceCraftPosition.dockedToSpaceCraft;
        this.mothership = mothership;
    }

    // The movement methods
    public void transitToPlanet(PlanetaryBody planet, double eta) {
        clearFields();
        position = SpaceCraftPosition.inTransitToPlanet;
        this.planet = planet;
        this.eta = eta;
    }

    public void orbitPlanet() {
        position = SpaceCraftPosition.orbitingPlanet;
        eta = 0;
    }

    public void landOnPlanet() {
        position = SpaceCraftPosition.planetSurface;
        eta = 0;
    }

    public void transitToNadir(double eta) {
        clearFields();
        position = SpaceCraftPosition.inTransitToNadir;
        this.eta = eta;
    }

    public void arriveAtNadir() {
        clearFields();
        position = SpaceCraftPosition.stellarNadir;
    }

    public void transitToZenith(double eta) {
        clearFields();
        position = SpaceCraftPosition.inTransitToZenith;
        this.eta = eta;
    }

    public void arriveAtZenith() {
        clearFields();
        position = SpaceCraftPosition.stellarZenith;
    }

    public void transitToPiratePoint(double eta) {
        clearFields();
        position = SpaceCraftPosition.inTransitToPiratePoint;
        this.eta = eta;
    }

    public void arriveAtPiratePoint() {
        clearFields();
        position = SpaceCraftPosition.piratePoint;
    }

    /**
     * When docking to a Jumpship for instance
     * 
     * @param mothership
     */
    public void dockToSpaceCraft(SpaceCraft mothership) {
        clearFields();
        position = SpaceCraftPosition.dockedToSpaceCraft;
        this.mothership = mothership;
    }

    /**
     * For those rescue missions...
     * 
     * @param eta
     */
    public void transitIntoDriftSpace(double eta) {
        clearFields();
        position = SpaceCraftPosition.inTransitToAdrift;
        this.eta = eta;
    }

    public void arriveInDrift(double rescueTimeCost) {
        clearFields();
        position = SpaceCraftPosition.adrift;
        this.eta = rescueTimeCost;
    }

    // Only use the accessors for special uses, otherwise use the movement
    // methods
    public SpaceCraftPosition getSpaceCraftPosition() {
        return position;
    }

    public PlanetaryBody getPlanet() {
        return planet;
    }

    public Star getStar() {
        return star;
    }

    public SpaceCraft getMothership() {
        return mothership;
    }

    public void setMothership(SpaceCraft mothership) {
        this.mothership = mothership;
    }

    public double getEta() {
        return eta;
    }

    public void setEta(double eta) {
        this.eta = eta;
    }

    // Use to clear the fields so there can be no loose ends to transitions
    private void clearFields() {
        position = null;
        planet = null;
        star = null;
        mothership = null;
        eta = 0;
    }
}
