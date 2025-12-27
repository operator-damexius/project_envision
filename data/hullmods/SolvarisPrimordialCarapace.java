package data.hullmods;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class SolvarisPrimordialCarapace extends BaseHullMod {

    // --- CONFIGURATION ---
    public static final float HE_RESISTANCE = 20f;        // Buffed from 15%
    public static final float EMP_RESISTANCE = 50f;       // NEW: Bio-ships hate EMP less
    public static final float ARMOR_REGEN_PER_MIN = 10f;  // Buffed from 5% -> 10%
    
    private static final Color HEAL_COLOR = new Color(100, 255, 100, 55); // Spooky Solvaris Green
    private static final String DATA_KEY = "solvaris_carapace_data";

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        // 1. Resistances
        stats.getHighExplosiveDamageTakenMult().modifyMult(id, 1f - (HE_RESISTANCE / 100f));
        stats.getEmpDamageTakenMult().modifyMult(id, 1f - (EMP_RESISTANCE / 100f));
        
        // 2. Automated Ship Logic
        stats.getMinCrewMod().modifyMult(id, 0f);
        stats.getMaxCrewMod().modifyMult(id, 0f); // It's fully automated/biological
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive()) return;

        // 1. Get/Create the Timer
        IntervalUtil interval = (IntervalUtil) ship.getCustomData().get(DATA_KEY);
        if (interval == null) {
            interval = new IntervalUtil(1f, 1f); // Check every 1 second for smoother healing
            ship.getCustomData().put(DATA_KEY, interval);
        }

        interval.advance(amount);

        // 2. The Healing Pulse
        if (interval.intervalElapsed()) {
            float[][] grid = ship.getArmorGrid().getGrid();
            float maxArmor = ship.getArmorGrid().getMaxArmorInCell();
            
            // Calculate heal amount per cell per second
            // (Total % per minute / 60 seconds) * Max Armor
            float healPerTick = maxArmor * (ARMOR_REGEN_PER_MIN / 100f) / 60f; 

            boolean didHeal = false;

            // Iterate the Grid
            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {
                    float current = grid[x][y];
                    
                    // UPGRADE: We now heal cells even if they are 0 (Regrowing limbs!)
                    if (current < maxArmor) {
                        grid[x][y] = Math.min(maxArmor, current + healPerTick);
                        didHeal = true;
                    }
                }
            }

            // 3. Visual Feedback (The "Living Ship" Effect)
            if (didHeal) {
                // Makes the ship vibrate/glow slightly green to show it is regenerating
                ship.setJitter(this, HEAL_COLOR, 0.5f, 2, 5f);
                
                // Optional: Sync armor mesh so the game knows visuals changed
                ship.syncWithArmorGridState();
            }
        }
    }
    
    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) HE_RESISTANCE + "%";
        if (index == 1) return "" + (int) EMP_RESISTANCE + "%";
        if (index == 2) return "" + (int) ARMOR_REGEN_PER_MIN + "%";
        return null;
    }
}