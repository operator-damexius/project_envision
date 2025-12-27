package data.scripts.shipsystems;

import java.awt.Color;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;

// --- GRAPHICSLIB (Distortion Only) ---
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;

public class SolvarisSingularityStats extends BaseShipSystemScript {

    // --- CONFIGURATION ---
    public static final float ACTIVE_TIME = 10f;     
    public static final float PULL_RANGE = 3500f;    
    public static final float PULL_STRENGTH = 3000f; 
    public static final float KILL_RADIUS = 450f;    
    
    // MELTING CONFIG
    // 9000 DPS = Very fast, painful death.
    // Ships will survive deep inside the hole for several seconds.
    public static final float MELT_DPS = 9000f; 
    
    // --- COLORS ---
    public static final Color VOID_COLOR = new Color(0, 0, 0, 255); 
    public static final Color PHOTON_RING = new Color(200, 150, 255, 100); 
    public static final Color RING_INNER = new Color(160, 80, 255, 200); 
    public static final Color RING_MID = new Color(100, 0, 200, 150);    
    public static final Color RING_OUTER = new Color(60, 0, 150, 100);   
    
    public static final Color WARNING_COLOR = new Color(255, 50, 50, 200); 
    public static final Color ARC_CORE = new Color(255, 200, 200, 255);
    public static final Color ARC_FRINGE = new Color(200, 50, 50, 200);
    
    public static final Color MELT_COLOR = new Color(255, 100, 50, 150);
    public static final Color JITTER_COLOR = new Color(255, 50, 0, 80);

    private Vector2f targetLocation = null;
    private boolean isCharging = false;
    private float activeTimer = 0f;

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else {
            return;
        }
        
        CombatEngineAPI engine = Global.getCombatEngine();

        // 1. CHARGE UP
        if (state == State.IN) {
            if (targetLocation == null) {
                if (ship == engine.getPlayerShip()) {
                    targetLocation = new Vector2f(ship.getMouseTarget());
                } else {
                    if (ship.getShipTarget() != null) {
                        targetLocation = new Vector2f(ship.getShipTarget().getLocation());
                    } else {
                        targetLocation = Misc.getUnitVectorAtDegreeAngle(ship.getFacing());
                        targetLocation.scale(1500f);
                        Vector2f.add(ship.getLocation(), targetLocation, targetLocation);
                    }
                }
            }
            
            // Visual Guide: Lightning Stream
            if (Math.random() > 0.6) {
                engine.spawnEmpArcVisual(
                    ship.getLocation(), ship, targetLocation, null,
                    15f, ARC_CORE, ARC_FRINGE
                );
            }

            // Visual Warning: Implosion
            if (Math.random() > 0.5) {
                Vector2f spawnPoint = MathUtils_getRandomPointInCircle(targetLocation, 900f * effectLevel);
                Vector2f vel = Vector2f.sub(targetLocation, spawnPoint, null);
                vel.scale(4.0f); 
                engine.addHitParticle(spawnPoint, vel, 15f, 2f, 0.4f, WARNING_COLOR);
            }
            
            Global.getSoundPlayer().playLoop("system_entropy_loop", ship, 1.0f + effectLevel, 0.6f, targetLocation, new Vector2f(0,0));
            
            isCharging = true;
            activeTimer = 0f;
        }

        // 2. SINGULARITY ACTIVE
        if (state == State.ACTIVE || state == State.OUT) {
            isCharging = false;
            if (state == State.ACTIVE) activeTimer += engine.getElapsedInLastFrame();
            
            float scale = (state == State.OUT) ? effectLevel : 1.0f;
            if (scale <= 0.01f) return; 
            if (targetLocation == null) return; 

            // --- A. VISUALS (PURE MATH - NO SPRITES) ---
            
            // 1. The Particle Swirl (Accretion Disk)
            // Thousands of tiny dots spinning.
            spawnRingParticles(engine, targetLocation, 450f * scale, 15, 500f, 200f, RING_INNER, 35f);
            spawnRingParticles(engine, targetLocation, 750f * scale, 10, 300f, 100f, RING_MID, 50f);
            spawnRingParticles(engine, targetLocation, 1200f * scale, 5, 150f, 50f, RING_OUTER, 80f);

            // 2. The Core (Generated Circles)
            // Photon Ring (Glow behind)
            engine.addSmoothParticle(targetLocation, new Vector2f(0,0), 420f * scale, 1f, 0.1f, PHOTON_RING);
            // Event Horizon (Black Void on top)
            engine.addSmoothParticle(targetLocation, new Vector2f(0,0), 380f * scale, 1f, 0.1f, VOID_COLOR);
            engine.addSmoothParticle(targetLocation, new Vector2f(0,0), 350f * scale, 1f, 0.1f, VOID_COLOR);

            // --- B. DISTORTION ---
            if (Global.getSettings().getModManager().isModEnabled("shaderLib")) {
                RippleDistortion gravityWell = new RippleDistortion(targetLocation, new Vector2f(0,0));
                gravityWell.setSize(1800f * scale); 
                gravityWell.setIntensity(400f * scale); 
                gravityWell.setFrameRate(0f); 
                gravityWell.setLifetime(0.15f); 
                DistortionShader.addDistortion(gravityWell);
            }

            // --- C. PHYSICS (ALLY SAFE) ---
            if (state == State.ACTIVE) {
                List<ShipAPI> targets = engine.getShips();
                for (ShipAPI target : targets) {
                    if (target == ship) continue; 
                    if (target.getOwner() == ship.getOwner()) continue; // <--- ALLY IMMUNITY
                    if (target.isPhased()) continue; 
                    
                    float dist = Misc.getDistance(targetLocation, target.getLocation());
                    
                    if (dist < PULL_RANGE) {
                        Vector2f pullDir = Vector2f.sub(targetLocation, target.getLocation(), null);
                        pullDir.normalise();
                        
                        float strength = PULL_STRENGTH * (1f - (dist / PULL_RANGE));
                        strength *= strength; 
                        strength += 200f; 
                        
                        Vector2f tangent = new Vector2f(-pullDir.y, pullDir.x);
                        tangent.scale(strength * 0.5f); 
                        
                        Vector2f force = new Vector2f(pullDir);
                        force.scale(strength);
                        
                        Vector2f totalForce = Vector2f.add(force, tangent, null);
                        totalForce.scale(engine.getElapsedInLastFrame());
                        Vector2f.add(target.getVelocity(), totalForce, target.getVelocity());
                        
                        float angleToHole = Misc.getAngleInDegrees(target.getLocation(), targetLocation);
                        target.setFacing(Misc.normalizeAngle(angleToHole));
                        
                        // --- D. THE MELTING ZONE ---
                        if (dist < KILL_RADIUS) {
                            
                            // 1. ALIVE: SLOW MELT
                            if (!target.isHulk()) {
                                engine.applyDamage(
                                    target, target.getLocation(), 
                                    MELT_DPS * engine.getElapsedInLastFrame(), // 9000 DPS
                                    DamageType.ENERGY, 
                                    0f, true, false, ship
                                );
                                
                                // Visuals: The Ship is struggling
                                target.setJitter(id, JITTER_COLOR, 1.0f, 3, 5f + (float)Math.random() * 5f);
                                if (Math.random() > 0.5) {
                                    Vector2f rndLoc = MathUtils_getRandomPointInCircle(target.getLocation(), target.getCollisionRadius());
                                    engine.addHitParticle(rndLoc, target.getVelocity(), 5f, 1f, 0.5f, MELT_COLOR);
                                }
                            }
                            
                            // 2. DEAD: SLOW DIGESTION
                            else {
                                float currentAlpha = target.getExtraAlphaMult();
                                // Fade extremely slowly (0.5% per frame) so you can watch the wreck spin
                                target.setExtraAlphaMult(currentAlpha * 0.995f); 
                                
                                if (Math.random() > 0.5) {
                                    Vector2f debrisLoc = MathUtils_getRandomPointInCircle(target.getLocation(), target.getCollisionRadius());
                                    Vector2f debrisVel = Vector2f.sub(targetLocation, debrisLoc, null);
                                    debrisVel.scale(2.0f); 
                                    engine.addHitParticle(debrisLoc, debrisVel, 4f, 0.5f, 0.3f, Color.gray);
                                }

                                // Only delete when almost invisible
                                if (currentAlpha < 0.1f) {
                                    engine.removeEntity(target);
                                    engine.spawnExplosion(targetLocation, new Vector2f(0,0), VOID_COLOR, 100f, 0.5f);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void spawnRingParticles(CombatEngineAPI engine, Vector2f center, float radius, int count, float orbitSpeed, float suckSpeed, Color color, float sizeBase) {
        for (int i = 0; i < count; i++) {
            float r = radius + (float)Math.random() * 80f; 
            float theta = (float)Math.random() * 360f;
            Vector2f spawnLoc = MathUtils_getPointOnCircumference(center, r, theta);
            Vector2f orbitVel = MathUtils_getPointOnCircumference(new Vector2f(0,0), orbitSpeed, theta + 90f);
            Vector2f suckVel = Vector2f.sub(center, spawnLoc, null);
            suckVel.normalise();
            suckVel.scale(suckSpeed);
            Vector2f finalVel = Vector2f.add(orbitVel, suckVel, null);
            float pSize = sizeBase + (float)Math.random() * 15f;
            float dur = 0.3f + (float)Math.random() * 0.3f; 
            engine.addHitParticle(spawnLoc, finalVel, pSize, 0.7f, dur, color);
        }
    }

    private Vector2f MathUtils_getRandomPointInCircle(Vector2f center, float radius) {
        float r = radius * (float)Math.sqrt(Math.random());
        float theta = (float)Math.random() * 360f;
        Vector2f point = new Vector2f((float)Math.cos(Math.toRadians(theta)), (float)Math.sin(Math.toRadians(theta)));
        point.scale(r);
        Vector2f.add(center, point, point);
        return point;
    }
    
    private Vector2f MathUtils_getPointOnCircumference(Vector2f center, float radius, float angle) {
        Vector2f point = new Vector2f((float)Math.cos(Math.toRadians(angle)), (float)Math.sin(Math.toRadians(angle)));
        point.scale(radius);
        Vector2f.add(center, point, point);
        return point;
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        targetLocation = null;
        isCharging = false;
        activeTimer = 0f;
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        if (state == State.IN) return new StatusData("CHARGING SINGULARITY", true);
        if (state == State.ACTIVE) return new StatusData("EVENT HORIZON ACTIVE", true);
        return null;
    }
}