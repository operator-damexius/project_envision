package data.scripts.shipsystems;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class TemporalSurgeStats extends BaseShipSystemScript {

    public static final float TIME_MULT = 3f;           
    public static final float SPEED_BONUS = 300f;       
    public static final float MANEUVER_BONUS = 600f;    
    public static final float ROF_BONUS = 1.5f;         
    public static final float FLUX_DISSIPATION = 2f;    
    
    // NEW COLORS: "White Glowing Blue"
    // Main Jitter: Bright White-Cyan
    public static final Color JITTER_COLOR = new Color(200, 245, 255, 90); 
    // Under Jitter: Deep Electric Blue (Creates the "mirage" depth)
    public static final Color JITTER_UNDER_COLOR = new Color(50, 100, 255, 160);

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        // 1. SHIP ACCELERATION
        float timeMult = 1f + (TIME_MULT - 1f) * effectLevel;
        stats.getTimeMult().modifyMult(id, timeMult);

        // 2. GLOBAL TIME DILATION (PLAYER ONLY)
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / timeMult);
        }

        // 3. STAT BONUSES
        stats.getMaxSpeed().modifyPercent(id, SPEED_BONUS * effectLevel);
        stats.getAcceleration().modifyPercent(id, MANEUVER_BONUS * effectLevel);
        stats.getDeceleration().modifyPercent(id, MANEUVER_BONUS * effectLevel);
        stats.getTurnAcceleration().modifyPercent(id, MANEUVER_BONUS * effectLevel);
        stats.getMaxTurnRate().modifyPercent(id, MANEUVER_BONUS * effectLevel);
        
        stats.getBallisticRoFMult().modifyMult(id, ROF_BONUS);
        stats.getEnergyRoFMult().modifyMult(id, ROF_BONUS);
        stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION);

        // 4. DEEP MIRAGE VISUALS
        if (effectLevel > 0) {
            // "Ghost" Jitter: High copies (8), Low range variance (3f) = Solid Afterimage
            ship.setJitter(this, JITTER_COLOR, effectLevel, 8, 3f, 10f);
            
            // "Displacement" Jitter: Lower copies (4), High range (35f) = Warping reality around the ship
            ship.setJitterUnder(this, JITTER_UNDER_COLOR, effectLevel, 4, 0f, 35f);
            
            // Engine Shift: Turns engine contrails to the deep blue color
            ship.getEngineController().fadeToOtherColor(this, JITTER_UNDER_COLOR, new Color(0,0,0,0), effectLevel, 0.5f);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();

        // 1. REMOVE GLOBAL DILATION
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            Global.getCombatEngine().getTimeMult().unmodify(id);
        }

        // 2. CLEANUP STATS
        stats.getTimeMult().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getBallisticRoFMult().unmodify(id);
        stats.getEnergyRoFMult().unmodify(id);
        stats.getFluxDissipation().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) return new StatusData("TEMPORAL SURGE", false);
        return null;
    }
}