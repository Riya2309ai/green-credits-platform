package com.greencredits.verificationservice.rules;

import java.util.Map;

public final class ActionRules {

    private record Rule(long creditsPerUnit, int maxQuantity) {
    }

    // Demo assumptions, not official conversion factors.
    private static final Map<String, Rule> RULES = Map.of(
            "PLASTIC_RECYCLING", new Rule(10, 50),
            "PAPER_RECYCLING", new Rule(5, 100),
            "E_WASTE_HANDOVER", new Rule(20, 30),
            "ENERGY_SAVED_KWH", new Rule(2, 500));

    private ActionRules() {
    }

    public static boolean isKnown(String actionType) {
        return RULES.containsKey(actionType);
    }

    public static int maxQuantity(String actionType) {
        return RULES.get(actionType).maxQuantity();
    }

    public static long creditsFor(String actionType, int quantity) {
        return RULES.get(actionType).creditsPerUnit() * quantity;
    }
}