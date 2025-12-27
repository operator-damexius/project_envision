package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.util.IntervalUtil;

public class SolvarisPrimordialInstinct extends BaseHullMod {

    // --- CONFIGURATION ---
    public static final float ATTACK_DMG_BONUS = 35f;
    public static final float ATTACK_ROF_BONUS = 35f;
    public static final float AVOID_SPEED_BONUS = 75f;
    public static final float AVOID_MANEUVER_BONUS = 150f;
    public static final float AVOID_DAMAGE_REDUCTION = 25f;
    public static final float MANEUVER_FLUX_BONUS = 30f;
    public static final float MANEUVER_RANGE_BONUS = 20f;

    // MEMORY KEYS
    private static final String KEY_TIMER = "solvaris_instinct_timer";
    private static final String KEY_MODE = "solvaris_instinct_mode";
    
    // MODES
    public static final int MODE_ATTACK = 1;
    public static final int MODE_AVOID = 2;
    public static final int MODE_MANEUVER = 3;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // Passive buff: Always aware
        stats.getSightRadiusMod().modifyPercent(id, 30f);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        // --- 1. TIMER LOGIC ---
        // Retrieve or create the timer from the ship's custom data
        IntervalUtil timer = (IntervalUtil) ship.getCustomData().get(KEY_TIMER);
        if (timer == null) {
            timer = new IntervalUtil(0.5f, 0.5f); // Check combat state every 0.5 seconds
            ship.getCustomData().put(KEY_TIMER, timer);
            
            // Set initial mode
            ship.getCustomData().put(KEY_MODE, MODE_MANEUVER);
            applyMode(ship, MODE_MANEUVER);
        }

        // --- 2. THINKING LOOP ---
        timer.advance(amount);
        if (timer.intervalElapsed()) {
            
            // Get Current Mode
            Object modeObj = ship.getCustomData().get(KEY_MODE);
            int currentMode = (modeObj instanceof Integer) ? (Integer) modeObj : MODE_MANEUVER;

            // Analyze Situation
            int newMode = analyzeSituation(ship);
            
            // Only update if the mode has changed
            if (newMode != currentMode) {
                applyMode(ship, newMode);
                ship.getCustomData().put(KEY_MODE, newMode);
            }
        }
    }

    private int analyzeSituation(ShipAPI ship) {
        float myFlux = ship.getFluxTracker().getFluxLevel();
        float myHull = ship.getHullLevel();
        ShipAPI target = ship.getShipTarget();
        
        // PRIORITY 1: SURVIVAL
        // If high flux (>85%) or low hull (<30%), switch to Avoid Mode
        if (myFlux > 0.85f || myHull < 0.30f) return MODE_AVOID;
        
        // PRIORITY 2: PREDATION
        // If target exists and is vulnerable, switch to Attack Mode
        if (target != null && !target.isAlly()) {
            float targetFlux = target.getFluxTracker().getFluxLevel();
            if (target.getFluxTracker().isOverloadedOrVenting() || targetFlux > 0.85f || target.getHullLevel() < 0.25f) {
                return MODE_ATTACK;
            }
        }
        
        // DEFAULT: TACTICAL
        return MODE_MANEUVER;
    }

    private void applyMode(ShipAPI ship, int newMode) {
        MutableShipStatsAPI stats = ship.getMutableStats();
        String id = "solvaris_instinct_buffs"; 

        // 1. Clear Old Buffs
        stats.getEnergyWeaponDamageMult().unmodify(id);
        stats.getBallisticWeaponDamageMult().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getBallisticRoFMult().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getHullDamageTakenMult().unmodify(id);
        stats.getFluxDissipation().unmodify(id);
        stats.getBallisticWeaponRangeBonus().unmodify(id);
        stats.getEnergyWeaponRangeBonus().unmodify(id);

        // 2. Apply New Buffs
        if (newMode == MODE_ATTACK) {
            stats.getEnergyWeaponDamageMult().modifyPercent(id, ATTACK_DMG_BONUS);
            stats.getBallisticWeaponDamageMult().modifyPercent(id, ATTACK_DMG_BONUS);
            stats.getEnergyRoFMult().modifyPercent(id, ATTACK_ROF_BONUS);
            stats.getBallisticRoFMult().modifyPercent(id, ATTACK_ROF_BONUS);
            
            if (ship.getCaptain() != null) ship.getCaptain().setPersonality(Personalities.AGGRESSIVE);
        
        } else if (newMode == MODE_AVOID) {
            stats.getMaxSpeed().modifyPercent(id, AVOID_SPEED_BONUS);
            stats.getAcceleration().modifyPercent(id, AVOID_MANEUVER_BONUS);
            stats.getDeceleration().modifyPercent(id, AVOID_MANEUVER_BONUS);
            stats.getHullDamageTakenMult().modifyMult(id, 1f - (AVOID_DAMAGE_REDUCTION / 100f));
            
            if (ship.getCaptain() != null) ship.getCaptain().setPersonality(Personalities.CAUTIOUS);
            
        } else if (newMode == MODE_MANEUVER) {
            stats.getFluxDissipation().modifyPercent(id, MANEUVER_FLUX_BONUS);
            stats.getBallisticWeaponRangeBonus().modifyPercent(id, MANEUVER_RANGE_BONUS);
            stats.getEnergyWeaponRangeBonus().modifyPercent(id, MANEUVER_RANGE_BONUS);
            
            if (ship.getCaptain() != null) ship.getCaptain().setPersonality(Personalities.STEADY);
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int)ATTACK_DMG_BONUS + "%";
        if (index == 1) return "" + (int)AVOID_SPEED_BONUS + "%";
        if (index == 2) return "" + (int)AVOID_MANEUVER_BONUS + "%";
        if (index == 3) return "" + (int)MANEUVER_FLUX_BONUS + "%";
        if (index == 4) return "" + (int)MANEUVER_RANGE_BONUS + "%";
        return null;
    }
}