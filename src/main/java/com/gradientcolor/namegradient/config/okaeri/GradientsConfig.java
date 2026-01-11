package com.gradientcolor.namegradient.config.okaeri;

import com.gradientcolor.namegradient.model.Gradient;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Header("## GRADIENT SETTINGS")
@Header("## Each gradient must have a unique integer ID")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class GradientsConfig extends OkaeriConfig {

    private Map<Integer, GradientEntry> gradients = createDefaultGradients();

    private static Map<Integer, GradientEntry> createDefaultGradients() {
        Map<Integer, GradientEntry> gradients = new LinkedHashMap<>();
        gradients.put(1, new GradientEntry(
                "AbleGamers Name Colour",
                "#F2709C",
                "#FFB454",
                "yourserver.ablegamers",
                "yourserver.ablegamers.override",
                1));
        gradients.put(2, new GradientEntry("Beyond Blue $25 Name Colour", "#59EAA4", "#089CCC", null, null, 0));
        gradients.put(3, new GradientEntry("Beyond Blue $300 Name Colour", "#59EAA4", "#CD03FF", null, null, 0));
        gradients.put(4, new GradientEntry("Make-A-Wish Colour", "#f74fdb", "#940ee2", null, null, 0));
        gradients.put(5, new GradientEntry("SpecialEffect Name Colour", "#ffd728", "#ff7328", null, null, 0));
        gradients.put(6, new GradientEntry("Water.org Name Colour", "#33E7FF", "#0368FF", null, null, 0));
        return gradients;
    }

    /**
     * Get a gradient by ID
     */
    public Gradient getGradient(int id) {
        GradientEntry entry = gradients.get(id);
        if (entry == null) {
            return null;
        }
        return new Gradient(
                id,
                entry.getName(),
                entry.getStartColour(),
                entry.getEndColour(),
                entry.getAlternatePermission(),
                entry.getOverridePermission(),
                entry.getOverrideWeight());
    }

    /**
     * Get all gradients
     */
    public Collection<Gradient> getAllGradients() {
        return gradients.entrySet().stream()
                .map(entry -> new Gradient(
                        entry.getKey(),
                        entry.getValue().getName(),
                        entry.getValue().getStartColour(),
                        entry.getValue().getEndColour(),
                        entry.getValue().getAlternatePermission(),
                        entry.getValue().getOverridePermission(),
                        entry.getValue().getOverrideWeight()))
                .toList();
    }

    /**
     * Get all gradient IDs
     */
    public Collection<Integer> getGradientIds() {
        return gradients.keySet();
    }

    /**
     * Check if a gradient exists
     */
    public boolean hasGradient(int id) {
        return gradients.containsKey(id);
    }

    public Map<Integer, GradientEntry> getGradients() {
        return gradients;
    }

    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class GradientEntry extends OkaeriConfig {

        @Comment("Name of the gradient displayed within GUI (limited to ~27 characters)")
        private String name;

        @Comment("Starting colour of this gradient")
        private String startColour;

        @Comment("Ending colour of this gradient")
        private String endColour;

        @Comment("Optional alternative permission node giving a player access to this gradient")
        private String alternatePermission;

        @Comment("Optional permission node which will force this gradient to be applied")
        private String overridePermission;

        @Comment("If a player has multiple override permissions, the gradient with the highest weight will be applied")
        private int overrideWeight = 0;

        public GradientEntry() {
        }

        public GradientEntry(String name, String startColour, String endColour,
                String alternatePermission, String overridePermission, int overrideWeight) {
            this.name = name;
            this.startColour = startColour;
            this.endColour = endColour;
            this.alternatePermission = alternatePermission;
            this.overridePermission = overridePermission;
            this.overrideWeight = overrideWeight;
        }

        public String getName() {
            return name;
        }

        public String getStartColour() {
            return startColour;
        }

        public String getEndColour() {
            return endColour;
        }

        public String getAlternatePermission() {
            return alternatePermission;
        }

        public String getOverridePermission() {
            return overridePermission;
        }

        public int getOverrideWeight() {
            return overrideWeight;
        }
    }
}
