package data.scripts.shipsystems;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

public class LogicStepStats extends BaseShipSystemScript {

    // --- SOLVARIS VISUALS ---
    public static final Color CODE_COLOR = new Color(0, 255, 200, 255); 
    public static final Color CODE_GLOW = new Color(0, 255, 200, 100);

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }

        // 1. PHASE PROTECTION
        // Since it's a Phase ship, we ensure it's intangible while the system is "hot"
        if (state == State.OUT || state == State.IN || state == State.ACTIVE) {
            ship.setPhased(true);
        } else {
            ship.setPhased(false);
        }

        // 2. VISUALS: THE "GLOWING LOGIC" STREAM
        if (effectLevel > 0) {
            CombatEngineAPI engine = Global.getCombatEngine();
            
            // Lag Safety: Only draw if on screen
            if (engine.getViewport().isNearViewport(ship.getLocation(), 600f)) {
                
                // Spawn "Data Bits"
                int particleCount = (int)(4 * effectLevel); 
                for (int i = 0; i < particleCount; i++) {
                    Vector2f point = new Vector2f();
                    point.x = (float)Math.random() * ship.getCollisionRadius() * 0.8f; 
                    point.y = (float)Math.random() * ship.getCollisionRadius() * 0.4f - (ship.getCollisionRadius() * 0.2f);
                    point = Misc.rotateAroundOrigin(point, ship.getFacing());
                    Vector2f spawnLoc = Vector2f.add(ship.getLocation(), point, null);

                    Vector2f vel = Misc.getUnitVectorAtDegreeAngle(ship.getFacing());
                    vel.scale(50f + (float)Math.random() * 50f); 

                    engine.addHitParticle(spawnLoc, vel, 4f + (float)Math.random() * 3f, 1f, 0.3f, CODE_COLOR);
                }

                // The "Jump" Flash
                if (state == State.OUT && effectLevel > 0.8f && !ship.getCustomData().containsKey("logic_step_flash")) {
                    Global.getSoundPlayer().playSound("system_phase_skimmer", 1.0f, 0.8f, ship.getLocation(), ship.getVelocity());
                    ship.addAfterimage(CODE_GLOW, 0f, 0f, 0f, 0f, 0f, 0f, 0.05f, 0.3f, true, true, false);
                    ship.getCustomData().put("logic_step_flash", true);
                }
            }
        }
        
        if (state == State.IDLE) {
            ship.getCustomData().remove("logic_step_flash");
        }

        // 3. PROPULSION BUFFS
        if (state == State.OUT || state == State.ACTIVE) {
            stats.getMaxTurnRate().modifyPercent(id, 50f);
            stats.getDeceleration().modifyPercent(id, 50f);
            stats.getAcceleration().modifyPercent(id, 100f);
        } else {
            stats.getMaxTurnRate().unmodify(id);
            stats.getDeceleration().unmodify(id);
            stats.getAcceleration().unmodify(id);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getMaxTurnRate().unmodify(id);
        stats.getDeceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getEntity().getCustomData().remove("logic_step_flash");
        
        // Ensure we return to reality
        if (stats.getEntity() instanceof ShipAPI) {
            ((ShipAPI) stats.getEntity()).setPhased(false);
        }
    }
}