package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin; 
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.util.Misc;

public class Pyralis implements SectorGeneratorPlugin {

    public Pyralis() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            // =================================================================
            // --- STAR SYSTEM ---
            // =================================================================
            StarSystemAPI system = sector.createStarSystem("Pyralis");
            system.getLocation().set(32000, -10000);
            system.setBackgroundTextureFilename("graphics/backgrounds/background3.jpg");

            // =================================================================
            // --- STAR ---
            // =================================================================
            PlanetAPI pyralis_star = system.initStar("pyralis", "star_orange", 1100f, 600f);
            system.setCenter(pyralis_star); // Stability Fix

            // =================================================================
            // --- RINGS & BELTS ---
            // =================================================================
            // Zone 1: The Furnace
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(255, 100, 20), 256f, 1600f, 40f, Terrain.RING, "Furnace Wall");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(255, 140, 40), 256f, 2000f, 50f, Terrain.RING, "Molten Track");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(230, 100, 60), 256f, 2400f, 60f, Terrain.RING, "Ember Band");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 2, new Color(200, 80, 40), 256f, 2800f, 70f, Terrain.RING, "Scorched Inner");
            
            // Zone 2: The Grinder
            system.addAsteroidBelt(pyralis_star, 150, 3800f, 800f, 200, 300, Terrain.ASTEROID_BELT, "The Grinder");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 3, new Color(180, 160, 150), 256f, 3800f, 100f, Terrain.RING, "Grinder Dust");

            // Zone 3: The Smog
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(160, 120, 100), 256f, 4500f, 90f, Terrain.RING, "Rust Belt");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(140, 110, 90), 256f, 5000f, 100f, Terrain.RING, "Heavy Smog");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 2, new Color(120, 100, 80), 256f, 5500f, 110f, Terrain.RING, "Slag Stream");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 3, new Color(100, 90, 80), 256f, 6000f, 120f, Terrain.RING, "Iron Haze");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(90, 80, 70), 256f, 6600f, 130f, Terrain.RING, "Carbon Cloud");

            // Zone 4: The Deadlands
            system.addAsteroidBelt(pyralis_star, 150, 7800f, 500f, 300, 256f, Terrain.ASTEROID_BELT, "The Deadlands");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(150, 150, 150), 256f, 7800f, 140f, Terrain.RING, "Deadlands Debris");

            // Zone 5: The Ash
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 2, new Color(110, 110, 120), 256f, 8800f, 160f, Terrain.RING, "Titanis Wake");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 3, new Color(90, 90, 100), 256f, 9600f, 180f, Terrain.RING, "Outer Ash");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 16000f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 16200f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 1, new Color(70, 70, 80), 256f, 16400f, 220f, Terrain.RING, "Rim Soot");
            system.addRingBand(pyralis_star, "misc", "rings_dust0", 256f, 0, new Color(50, 50, 60), 256f, 16600f, 260f, Terrain.RING, "Void Edge");

            // =================================================================
            // --- PLANET : TITANIS (The Ember Giant) ---
            // =================================================================
            PlanetAPI planet0 = system.addPlanet("titanis", pyralis_star, "Titanis", "gas_giant", 0, 600f, 13000f, 4000f);
            
            // Visuals: Warm, burning orange to match the "Pyralis" (Fire) theme
            planet0.getSpec().setPlanetColor(new Color(255, 140, 50, 255));
            planet0.getSpec().setAtmosphereColor(new Color(255, 160, 80, 150));
            planet0.getSpec().setCloudColor(new Color(255, 100, 50, 200));
            planet0.getSpec().setAtmosphereThickness(0.5f);
            
            // Aurorae: Red/Orange glow (High energy particle interaction)
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet0.getSpec().setGlowColor(new Color(255, 60, 0, 60));
            planet0.getSpec().setUseReverseLightForGlow(true);
            
            planet0.applySpecChanges();
            
            // CRITICAL ADDITION:
            planet0.setCustomDescriptionId("planet_titanis");
            
            SectorEntityToken magField = system.addTerrain(Terrain.MAGNETIC_FIELD, 
                new MagneticFieldParams(
                    planet0.getRadius() + 200f, 
                    planet0.getRadius() + 1500f, 
                    planet0, 
                    planet0.getRadius() + 100f, 
                    planet0.getRadius() + 1500f, 
                    new Color(255, 80, 20, 50), 
                    0.5f, 
                    new Color(255, 120, 50, 150), 
                    new Color(200, 60, 20, 150)
                )
            );
            magField.setCircularOrbit(planet0, 0, 0, 100);

            Misc.initConditionMarket(planet0);
            planet0.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0.getMarket().addCondition(Conditions.HIGH_GRAVITY);      
            planet0.getMarket().addCondition(Conditions.EXTREME_WEATHER);   
            planet0.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);
            planet0.getMarket().addCondition(Conditions.IRRADIATED);  

            // =================================================================
            // --- IGNIS (The Scabbed Moon) ---
            // =================================================================
            // Changed type from "lava" to "barren" to give it a solid rock surface
            PlanetAPI planet0a = system.addPlanet("ignis", planet0, "Ignis", "barren", 0, 150f, 1700f, 30f);
            
            // Surface Color: Dark Ash/Basalt (Dark Grey-Red)
            // This makes it look like cooled, hardened volcanic rock.
            planet0a.getSpec().setPlanetColor(new Color(60, 50, 45, 255));
            
            // Texture: "Banded" works well to simulate stress fractures/cracks
            planet0a.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            
            // Glow Color: Deep Magma Red (Not bright orange, but deep heat from within)
            planet0a.getSpec().setGlowColor(new Color(200, 40, 0, 120));
            
            // Use reverse light so the glow shows up on the "night" side (magma cooling)
            planet0a.getSpec().setUseReverseLightForGlow(true);
            
            planet0a.applySpecChanges();
            
            // Add Description ID
            planet0a.setCustomDescriptionId("planet_ignis");

            Misc.initConditionMarket(planet0a);
            planet0a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0a.getMarket().addCondition(Conditions.VERY_HOT);           
            planet0a.getMarket().addCondition(Conditions.TECTONIC_ACTIVITY);  
            planet0a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);      
            planet0a.getMarket().addCondition(Conditions.RARE_ORE_RICH);      
            planet0a.getMarket().addCondition(Conditions.ORE_RICH);           

            // =================================================================
            // --- JUMP POINTS & STABLE LOCATIONS ---
            // =================================================================
            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("pyralis_jump_point", "Point Pyralis Jump");
            jumpPoint.setCircularOrbit(pyralis_star, 220, 10000f, 600f);
            jumpPoint.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint);

            // STABLE LOCATIONS (Correctly Tokenized)
            SectorEntityToken pyralisStable1 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable1.setCircularOrbit(pyralis_star, 90, 3100f, 200f); 
                  
            SectorEntityToken pyralisStable2 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable2.setCircularOrbit(pyralis_star, 210, 4350f, 250f); 

            SectorEntityToken pyralisStable3 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable3.setCircularOrbit(pyralis_star, 270, 7200f, 400f); 

            SectorEntityToken pyralisStable4 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable4.setCircularOrbit(pyralis_star, 150, 11000f, 500f); 

            SectorEntityToken pyralisStable5 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            pyralisStable5.setCircularOrbit(pyralis_star, 45, 13000f, 600f);

            system.autogenerateHyperspaceJumpPoints(true, false);
            
            // CLEANUP OPTIMIZED for 13k size system
            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Pyralis.class).error("Error generating Pyralis system: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            // Clears arc up to 1400 to cover the 1300 stable location
            editor.clearArc(x, y, 0, 1400f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 1200f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}