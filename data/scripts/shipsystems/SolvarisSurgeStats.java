package data.scripts.shipsystems;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class SolvarisSurgeStats extends BaseShipSystemScript {

    public static final float TIME_MULT = 3f;           
    public static final float SPEED_BONUS = 300f;       
    public static final float MANEUVER_BONUS = 600f;    
    public static final float ROF_BONUS = 1.5f;         
    public static final float FLUX_DISSIPATION = 2f;    
    
    public static final Color JITTER_COLOR = new Color(255, 220, 200, 80); 
    public static final Color JITTER_UNDER_COLOR = new Color(255, 100, 20, 150);

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        // 1. SHIP ACCELERATION (Always applies to the user)
        // This makes the ship move 3x faster relative to the game time.
        float timeMult = 1f + (TIME_MULT - 1f) * effectLevel;
        stats.getTimeMult().modifyMult(id, timeMult);

        // 2. GLOBAL TIME DILATION (PLAYER ONLY)
        // Only slow the world down if the PLAYER is the one using the system.
        // This prevents AI usage from annoying the player or stacking slowdowns.
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / timeMult);
        } else {
            // Safety: If an AI uses it, ensure they don't accidentally apply the global slow
            // (Note: We rely on unapply to clean up, but this is a double-check)
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

        // 4. VISUALS
        // We keep Jitter for everyone so you can see when an enemy is supercharged.
        if (effectLevel > 0) {
            ship.setJitter(this, JITTER_COLOR, effectLevel, 5, 5f, 10f);
            ship.setJitterUnder(this, JITTER_UNDER_COLOR, effectLevel, 10, 0f, 25f);
            ship.getEngineController().fadeToOtherColor(this, JITTER_COLOR, new Color(0,0,0,0), effectLevel, 0.4f);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();

        // 1. REMOVE GLOBAL DILATION
        // Only attempt to remove the global slow if the Player was the one using it.
        // This prevents an AI finishing their system from cancelling the Player's active slow motion.
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