package data.scripts.shipsystems;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class IonicOverdriveStats extends BaseShipSystemScript {

    // --- CONFIGURATION ---
    public static final float RANGE = 600f;
    public static final float EMP_DAMAGE = 200f;
    public static final float HULL_DAMAGE = 50f;
    
    public static final float SPEED_BONUS = 75f;
    public static final float TURN_BONUS = 150f;
    
    // --- VISUALS ---
    public static final Color ELECTRIC_COLOR = new Color(100, 220, 255, 255);

    // OPTIMIZATION: Only search for targets 4 times a second
    private IntervalUtil arcTimer = new IntervalUtil(0.20f, 0.30f);

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        // 1. ENGINE BOOST (Cheap math, always runs)
        if (state == State.OUT) {
            stats.getMaxSpeed().unmodify(id); 
            stats.getMaxTurnRate().unmodify(id);
            stats.getTurnAcceleration().unmodify(id);
            stats.getAcceleration().unmodify(id);
            stats.getDeceleration().unmodify(id);
        } else {
            stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS * effectLevel);
            stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
            stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
            stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
            stats.getMaxTurnRate().modifyPercent(id, TURN_BONUS * effectLevel);
        }

        // 2. ACTIVE LOOP
        if (state == State.ACTIVE) {
            CombatEngineAPI engine = Global.getCombatEngine();
            
            // --- OPTIMIZATION: VIEWPORT CHECK ---
            // Critical for i7-6600U: If off-screen, skip all visuals.
            boolean onScreen = engine.getViewport().isNearViewport(ship.getLocation(), ship.getCollisionRadius() + 100f);
            
            if (onScreen) {
                Vector2f loc = ship.getLocation();
                Vector2f vel = ship.getVelocity();
                float time = engine.getTotalElapsedTime(false);
                
                // --- MATH VISUALS: Rotating Electric Ring ---
                // Kept strictly to 3 particles to save GPU fill rate
                int particles = 3; 
                float radius = ship.getCollisionRadius() * 1.2f;
                float spinSpeed = 10.0f; 

                for (int i = 0; i < particles; i++) {
                    double angle = Math.toRadians((360f / particles) * i) + (time * spinSpeed);
                    
                    float x = (float)Math.cos(angle) * radius;
                    float y = (float)Math.sin(angle) * radius;
                    
                    Vector2f spawnLoc = new Vector2f(loc.x + x, loc.y + y);
                    
                    // Jitter
                    spawnLoc.x += (float)Math.random() * 20f - 10f;
                    spawnLoc.y += (float)Math.random() * 20f - 10f;

                    // Small size (25f) prevents Intel HD 520 lag
                    engine.addHitParticle(spawnLoc, vel, 25f, 0.7f, 0.1f, ELECTRIC_COLOR);
                }
            }

            // --- TARGET SEARCH (GATED BY TIMER) ---
            arcTimer.advance(engine.getElapsedInLastFrame());
            if (arcTimer.intervalElapsed()) {
                ShipAPI target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), HullSize.FIGHTER, RANGE, true);
                
                if (target != null && !target.isPhased()) {
                    Global.getSoundPlayer().playSound("system_emp_emitter_impact", 1.0f, 0.6f, target.getLocation(), target.getVelocity());
                    
                    engine.spawnEmpArc(
                        ship, 
                        ship.getLocation(), 
                        target, 
                        target, 
                        DamageType.ENERGY, 
                        HULL_DAMAGE, 
                        EMP_DAMAGE, 
                        1000f, 
                        "tachyon_lance_fire", 
                        20f, 
                        ELECTRIC_COLOR, 
                        Color.white
                    );
                }
            }
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (index == 0) return new StatusData("IONIC OVERDRIVE", false);
        return null;
    }
}