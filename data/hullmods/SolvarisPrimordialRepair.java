package data.hullmods;

import java.awt.Color;
import java.util.List;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

// IMPORTS
import com.fs.starfarer.api.combat.ShipEngineControllerAPI; 
import com.fs.starfarer.api.combat.ShipEngineControllerAPI.ShipEngineAPI;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class SolvarisPrimordialRepair extends BaseHullMod {

    // --- CONFIGURATION ---
    public static final float BASE_HULL_REPAIR = 0.5f;   // Base % per second
    public static final float WEAPON_REPAIR_SPEED = 200f; 
    public static final float CR_RECOVERY = 50f;
    
    // Chance to instantly fix a broken gun every second
    public static final float INSTANT_FIX_CHANCE = 0.25f; 

    private static final String DATA_KEY = "solvaris_repair_data";
    private static final Color HEAL_SPARK_COLOR = new Color(100, 255, 150, 200);

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // 1. Biological Automation
        stats.getMinCrewMod().modifyMult(id, 0f);
        stats.getMaxCrewMod().modifyMult(id, 0f);
        
        // 2. Stat Buffs
        stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f / (1f + WEAPON_REPAIR_SPEED / 100f));
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f / (1f + WEAPON_REPAIR_SPEED / 100f));
        stats.getRepairRatePercentPerDay().modifyPercent(id, CR_RECOVERY);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;
        
        CombatEngineAPI engine = Global.getCombatEngine();
        
        // Timer Logic
        IntervalUtil interval = (IntervalUtil) ship.getCustomData().get(DATA_KEY);
        if (interval == null) {
            interval = new IntervalUtil(1f, 1f); // Run once per second
            ship.getCustomData().put(DATA_KEY, interval);
        }
        
        interval.advance(amount);

        if (interval.intervalElapsed()) {
            float maxHull = ship.getMaxHitpoints();
            float currentHull = ship.getHitpoints();
            
            // --- 1. ADAPTIVE REGENERATION ---
            if (currentHull < maxHull && currentHull > 0) {
                float hullFraction = currentHull / maxHull;
                float multiplier = 1f;

                if (hullFraction < 0.25f) multiplier = 4f;      // CRITICAL STATE
                else if (hullFraction < 0.5f) multiplier = 2f;  // DAMAGED STATE

                float repairAmount = maxHull * (BASE_HULL_REPAIR / 100f) * multiplier;
                ship.setHitpoints(Math.min(maxHull, currentHull + repairAmount));
                
                // Visuals
                if (Math.random() > 0.5f) {
                    float angle = (float) Math.random() * 360f;
                    float dist = (float) Math.random() * ship.getCollisionRadius() * 0.7f;
                    
                    Vector2f point = Misc.getUnitVectorAtDegreeAngle(angle);
                    point.scale(dist);
                    Vector2f.add(point, ship.getLocation(), point);
                    
                    engine.addHitParticle(point, ship.getVelocity(), 10f + (multiplier * 5f), 1f, 0.5f, HEAL_SPARK_COLOR);
                }
            }
            
            // --- 2. NEURAL RECONNECTION (Instant Weapon Fix) ---
            if (Math.random() < INSTANT_FIX_CHANCE) {
                restoreRandomSystem(ship);
            }
        }
    }
    
    // Helper to instantly repair one broken thing
    private void restoreRandomSystem(ShipAPI ship) {
        // Try weapons first
        List<WeaponAPI> weapons = ship.getAllWeapons();
        for (WeaponAPI w : weapons) {
            if (w.isDisabled()) {
                w.repair(); // Force weapon back online
                
                // Brute force health reset (clamped by game engine)
                w.setCurrHealth(10000f);
                
                // FIX: Corrected Method Signature for addFloatingText
                // We added the '20f' (Font Size) which was missing and causing the crash.
                Global.getCombatEngine().addFloatingText(
                    w.getLocation(), 
                    "RECONNECTED", 
                    20f, 
                    new Color(100, 255, 150, 255), 
                    ship, 
                    0f, 
                    0f
                );
                return; // Fixed one, stop.
            }
        }
        
        // If weapons are fine, try engines
        ShipEngineControllerAPI ec = ship.getEngineController();
        if (ec != null) {
            for (ShipEngineAPI e : ec.getShipEngines()) {
                if (e.isDisabled()) {
                    e.repair(); 
                    return;
                }
            }
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + BASE_HULL_REPAIR + "%";
        if (index == 1) return "" + (int) WEAPON_REPAIR_SPEED + "%";
        if (index == 2) return "4x"; 
        return null;
    }
}