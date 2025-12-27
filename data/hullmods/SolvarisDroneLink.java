package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class SolvarisDroneLink extends BaseHullMod {

    public static final float REPLACEMENT_RATE = 25f; // Fighters respawn 25% faster
    public static final float FIGHTER_RANGE = 2000f; // Engage enemies from 2000u further away
    public static final float CREW_REDUCTION = 20f; // Needs fewer crew (AI does the work)

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFighterRefitTimeMult().modifyMult(id, 1f - (REPLACEMENT_RATE / 100f));
        stats.getFighterWingRange().modifyFlat(id, FIGHTER_RANGE);
        stats.getMinCrewMod().modifyMult(id, 1f - (CREW_REDUCTION / 100f));
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) REPLACEMENT_RATE + "%";
        if (index == 1) return "" + (int) FIGHTER_RANGE;
        if (index == 2) return "" + (int) CREW_REDUCTION + "%";
        return null;
    }
}