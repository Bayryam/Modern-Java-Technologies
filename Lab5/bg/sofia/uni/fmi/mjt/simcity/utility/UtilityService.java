package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {
    private final Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = taxRates;
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null) {
            throw new IllegalArgumentException("Utility type is null!");
        }

        if (billable == null) {
            throw new IllegalArgumentException("Billable is null!");
        }

        return switch (utilityType) {
            case ELECTRICITY -> billable.getElectricityConsumption() * taxRates.get(utilityType);
            case WATER -> billable.getWaterConsumption() * taxRates.get(utilityType);
            case NATURAL_GAS -> billable.getNaturalGasConsumption() * taxRates.get(utilityType);
        };

    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException("Billable is null!");
        }

        return billable.getNaturalGasConsumption() * taxRates.get(UtilityType.NATURAL_GAS) +
                billable.getWaterConsumption() * taxRates.get(UtilityType.WATER) +
                billable.getElectricityConsumption() * taxRates.get(UtilityType.ELECTRICITY);
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null) {
            throw new IllegalArgumentException("First billable is null!");
        }

        if (secondBillable == null) {
            throw new IllegalArgumentException("Second billable is null!");
        }

        Map<UtilityType, Double> result = new HashMap<>();

        for (UtilityType type : UtilityType.values()) {
            result.put(type, Math.abs(getUtilityCosts(type, firstBillable)
                    - getUtilityCosts(type, secondBillable)));
        }

        return Collections.unmodifiableMap(result);
    }
}
