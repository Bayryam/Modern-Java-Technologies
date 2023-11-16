package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {
    private int buildingArea;
    private final Map<String, E> buildings;

    public Plot(int buildableArea) {
        this.buildingArea = buildableArea;
        buildings = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null) {
            throw new IllegalArgumentException("Address argument is null!");
        }

        if (address.isBlank()) {
            throw new IllegalArgumentException("Address argument is blank!");
        }

        if (buildable == null) {
            throw new IllegalArgumentException("Buildable argument is null!");
        }

        if (buildings.containsKey(address)) {
            throw new BuildableAlreadyExistsException("Already occupied address!");
        }

        if (buildable.getArea() > buildingArea) {
            throw new InsufficientPlotAreaException("Not enough building area!");
        }

        buildingArea -= buildable.getArea();
        buildings.put(address, buildable);
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null) {
            throw new IllegalArgumentException("Buildables argument is null!");
        }

        if (buildables.isEmpty()) {
            throw new IllegalArgumentException("Buildables argument is empty!");
        }

        int sumOfAreas = 0;
        for (E buildable : buildables.values()) {
            sumOfAreas += buildable.getArea();
        }

        if (sumOfAreas > buildingArea) {
            throw new InsufficientPlotAreaException("Not enough area for all buildings!");
        }

        for (String address : buildables.keySet()) {
            if (buildings.containsKey(address)) {
                throw new BuildableAlreadyExistsException(
                        "There is a building that is already constructed on the plot!");
            }
        }
        buildingArea -= sumOfAreas;
        buildings.putAll(buildables);
    }

    @Override
    public void demolish(String address) {
        if (address == null) {
            throw new IllegalArgumentException("Address argument is null!");
        }

        if (address.isBlank()) {
            throw new IllegalArgumentException("Address argument is blank!");
        }

        if (!buildings.containsKey(address)) {
            throw new BuildableNotFoundException("Building with such address doesn't exist!");
        }
        buildingArea += buildings.get(address).getArea();
        buildings.remove(address);
    }

    @Override
    public void demolishAll() {
        for (E buildable : buildings.values()) {
            buildingArea += buildable.getArea();
        }
        buildings.clear();
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildings);
    }

    @Override
    public int getRemainingBuildableArea() {
        return buildingArea;
    }
}
