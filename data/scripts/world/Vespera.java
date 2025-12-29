package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class Vespera implements SectorGeneratorPlugin {

    public Vespera() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            // =================================================================
            // --- STAR SYSTEM ---
            // =================================================================
            StarSystemAPI system = sector.createStarSystem("Vespera");
            system.getLocation().set(30000, -12000);
            system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

            // =================================================================
            // --- STAR (Vespera - White-Yellow Dwarf) ---
            // =================================================================
            // "star_yellow" is the base type, but we will tint it to be whiter/brighter.
            PlanetAPI vespera_star = system.initStar("vespera", "star_yellow", 900f, 200f, 5f, 1.0f, 2.0f);
            
            // SYSTEM LIGHT: 
            // A mix of White (255) and a tiny bit of warmth (240/230). 
            // This makes the whole system look bright and natural, not intensely orange.
            system.setLightColor(new Color(255, 250, 240)); 
            
            system.setCenter(vespera_star); // Stability Fix

            // STAR VISUALS:
            // 1. The "Surface" (The star disk itself)
            // We set this to a very pale yellow (almost white) to represent the high heat.
            vespera_star.getSpec().setPlanetColor(new Color(255, 255, 245, 255));
            
            // 2. The "Corona" (The glowing halo around the star)
            // This gives it the "Yellow" personality. We use a soft gold here.
            vespera_star.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded")); // or "aurorae" for wispy look
            vespera_star.getSpec().setGlowColor(new Color(255, 220, 150, 255));
            vespera_star.getSpec().setUseReverseLightForGlow(true);
            
            vespera_star.applySpecChanges();
            vespera_star.setCustomDescriptionId("vespera_system");

            // =================================================================
            // --- BELTS & RINGS ---
            // =================================================================
            system.addAsteroidBelt(vespera_star, 150, 6300, 256f, 300, 256f, Terrain.ASTEROID_BELT, null);
            system.addRingBand(vespera_star, "misc", "rings_ice0", 256f, 1, Color.BLUE, 256f, 6300, 512f, Terrain.RING, "Vespera Ring Belt");
            system.addRingBand(vespera_star, "misc", "rings_dust0", 256f, 0, new Color(100, 90, 80, 255), 512f, 23000f, 400f, Terrain.RING, "The Outer Barrier");

            // =================================================================
            // --- PLANET 0: BLACKROCK (The Iron Core) ---
            // =================================================================
            PlanetAPI planet0 = system.addPlanet("blackrock", vespera_star, "Blackrock", "rocky_metallic", 177, 170, 4300, 365);
            
            // SURFACE: Dark Gunmetal / Charcoal
            // (Represents heavy metals like Iron/Tungsten absorbing the yellow sunlight)
            planet0.getSpec().setPlanetColor(new Color(50, 50, 55, 255));
            
            // GLOW: Dull Molten Red (Residual Heat)
            // Since it is near the star, the metal retains massive heat, glowing faintly in cracks.
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet0.getSpec().setGlowColor(new Color(150, 40, 20, 100));
            planet0.getSpec().setUseReverseLightForGlow(true);
            
            planet0.applySpecChanges();
            
            planet0.setCustomDescriptionId("planet_blackrock");
            
            // Optional: Add conditions to reflect the "Heavy Elements" lore
            Misc.initConditionMarket(planet0);
            planet0.getMarket().addCondition(Conditions.ORE_ULTRARICH); // Extremely rich in common minerals
            planet0.getMarket().addCondition(Conditions.RARE_ORE_ULTRARICH); // Extremely rich in rare minerals
            planet0.getMarket().addCondition(Conditions.NO_ATMOSPHERE); // Barren world
            planet0.getMarket().addCondition(Conditions.VERY_HOT); // Proximity to star

            // =================================================================
            // --- PLANET 1: NIMORIA (The Garden World) ---
            // =================================================================
            PlanetAPI planet1 = system.addPlanet("nimoria", vespera_star, "Nimoria", "terran", 24, 130, 8000, 325);
            
            // COLOR: Pure White Tint
            // Since "terran" texture is already Green/Blue, we use white to keep the colors 
            // natural and vibrant under the yellow sunlight (True Earth-like).
            planet1.getSpec().setPlanetColor(new Color(255, 255, 255, 255));
            
            // ATMOSPHERE: Classic Sky Blue (Rayleigh Scattering)
            planet1.getSpec().setAtmosphereColor(new Color(100, 150, 255, 160));
            planet1.getSpec().setCloudColor(new Color(255, 255, 255, 200));
            planet1.getSpec().setAtmosphereThickness(0.5f);
            
            // TILT: 23.5 Degrees (Same as Earth)
            // This implies natural seasons, fitting the "Mild Climate" condition.
            planet1.getSpec().setTilt(23.5f);
            
            // No City Glow: Keeps it looking "Natural" and wild (Ruins implied)
            planet1.getSpec().setGlowColor(new Color(0, 0, 0, 0)); 
            
            planet1.applySpecChanges();
            
            planet1.setCustomDescriptionId("planet_nimoria");
            
            Misc.initConditionMarket(planet1);
            planet1.getMarket().addCondition(Conditions.HABITABLE); // Breathable atmosphere
            planet1.getMarket().addCondition(Conditions.MILD_CLIMATE); // Temperate world
            planet1.getMarket().addCondition(Conditions.FARMLAND_BOUNTIFUL); // Fertile world
            planet1.getMarket().addCondition(Conditions.RUINS_SCATTERED); // Old first settlements

            // =================================================================
            // --- MOON AMARIS (The White Mirror) ---
            // =================================================================
            PlanetAPI planet1a = system.addPlanet("amaris", planet1, "Amaris", "barren2", 24, 50, 700, 31);
            
            // VISUAL UPDATE: Pure White to simulate high reflectivity
            // This overrides the reddish/brown tint of "barren2"
            planet1a.getSpec().setPlanetColor(new Color(255, 255, 255, 255));
            
            // Optional: A very faint white glow to simulate intense reflection of sunlight
            planet1a.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet1a.getSpec().setGlowColor(new Color(255, 255, 255, 30)); 
            planet1a.getSpec().setUseReverseLightForGlow(true);
            
            planet1a.applySpecChanges();
            
            planet1a.setCustomDescriptionId("planet_amaris");
            
            Misc.initConditionMarket(planet1a);
            planet1a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            planet1a.getMarket().addCondition(Conditions.ORE_RICH);
            planet1a.getMarket().addCondition(Conditions.LOW_GRAVITY);

            // =================================================================
            // --- PLANET 2: VESPERIS (The Sapphire Jewel) ---
            // =================================================================
            PlanetAPI planet2 = system.addPlanet("vesperis", vespera_star, "Vesperis", "water", 24, 290, 11000, 365);
            
            // SURFACE: Deep Royal Blue / Sapphire
            // (Accurate for deep water under a Yellow Star - absorbs red/yellow, reflects blue)
            planet2.getSpec().setPlanetColor(new Color(0, 50, 150, 255));
            
            // ATMOSPHERE: Bright Azure
            // (Thick, breathable atmosphere scattering the star's light)
            planet2.getSpec().setAtmosphereColor(new Color(130, 200, 255, 180));
            planet2.getSpec().setCloudColor(new Color(255, 255, 255, 200));
            planet2.getSpec().setAtmosphereThickness(0.6f);
            
            // GLOW: Bioluminescent Currents (Cyan/Teal)
            // Instead of city lights, this represents massive algae blooms glowing at night
            planet2.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet2.getSpec().setGlowColor(new Color(0, 255, 200, 60)); 
            planet2.getSpec().setUseReverseLightForGlow(true);
            
            planet2.applySpecChanges();
            
            planet2.setCustomDescriptionId("planet_vesperis");
            
            // Rings match the deep blue aesthetic
            system.addRingBand(planet2, "misc", "rings_ice0", 256f, 1, Color.BLUE, 256f, 1000, 256f, Terrain.RING, "Vesperis Ice Rings");
            
            Misc.initConditionMarket(planet2);
            planet2.getMarket().addCondition(Conditions.WATER_SURFACE); // Ocean world
            planet2.getMarket().addCondition(Conditions.HABITABLE); // Breathable atmosphere
            planet2.getMarket().addCondition(Conditions.VOLATILES_TRACE); // Oceans provide some volatiles
            // Added ORGANICS to represent the bioluminescent life
            planet2.getMarket().addCondition(Conditions.ORGANICS_ABUNDANT); // Rich marine biosphere

            // =================================================================
            // --- PLANET 3: AETHERIS (The Golden Super-Earth) ---
            // =================================================================
            PlanetAPI planet3 = system.addPlanet("aetheris", vespera_star, "Aetheris", "terran", 24, 220, 17000, 681);
            
            // SURFACE: Neutral White/Tan 
            // Allows the terrain to show through the atmosphere without clashing.
            planet3.getSpec().setPlanetColor(new Color(255, 245, 230, 255));
            
            // ATMOSPHERE: Majestic Gold
            // A rich, warm gold that glows intensely against the white star.
            planet3.getSpec().setAtmosphereColor(new Color(255, 190, 50, 140));
            
            // CLOUDS: Pale Cream/White
            // Provides definition against the gold air.
            planet3.getSpec().setCloudColor(new Color(255, 240, 220, 200));
            
            // THICKNESS: 0.8f (Very thick, creating a "halo" effect)
            planet3.getSpec().setAtmosphereThickness(0.8f);
            
            // GLOW: Aurora Borealis (Green/Teal)
            // Keeping the aurora gives a beautiful contrast (Gold Sky + Green Lights).
            planet3.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet3.getSpec().setGlowColor(new Color(50, 255, 150, 120)); 
            planet3.getSpec().setUseReverseLightForGlow(true);
            
            planet3.applySpecChanges();
            
            planet3.setCustomDescriptionId("planet_aetheris");
            
            Misc.initConditionMarket(planet3);
            planet3.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet3.getMarket().addCondition(Conditions.HABITABLE); // Breathable atmosphere
            planet3.getMarket().addCondition(Conditions.HIGH_GRAVITY); // Super-Earth
            planet3.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE); // Thick golden air
            planet3.getMarket().addCondition(Conditions.ORE_RICH); // Mineral wealth
            planet3.getMarket().addCondition(Conditions.FARMLAND_RICH); // Fertile lands
            planet3.getMarket().addCondition(Conditions.ORGANICS_PLENTIFUL); // Lush biosphere
            planet3.getMarket().addCondition(Conditions.RUINS_WIDESPREAD); // Ancient advanced civilization

            // =================================================================
            // --- MOON: FERRONOX (The Gilded Desert) ---
            // =================================================================
            // UPDATED: Radius 110 (Large Habitable Moon). Type "desert".
            PlanetAPI planet3a = system.addPlanet("ferronox", planet3, "Ferronox", "desert", 24, 110, 1000, 31);
            
            // SURFACE: Pale Gold and White
            // Mixes the "desert" texture with a bright, sandy gold tint.
            planet3a.getSpec().setPlanetColor(new Color(255, 235, 215, 255));
            
            // ATMOSPHERE: Very Thin, Dusty White
            // Being a moon, it holds less air than Aetheris, so the sky is a pale, dusty bleach.
            planet3a.getSpec().setAtmosphereColor(new Color(255, 240, 200, 100));
            planet3a.getSpec().setCloudColor(new Color(255, 255, 255, 150));
            planet3a.getSpec().setAtmosphereThickness(0.2f); // Thin atmosphere
            
            // GLOW: None (Natural daylight reflection)
            // Or distinct oases if you wanted, but "Natural" fits the white/gold theme better.
            planet3a.getSpec().setGlowColor(new Color(0, 0, 0, 0));
            
            planet3a.applySpecChanges();
            
            planet3a.setCustomDescriptionId("planet_ferronox");
            
            Misc.initConditionMarket(planet3a);
            planet3a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            // CONDITIONS: Living Desert Moon
            planet3a.getMarket().addCondition(Conditions.HABITABLE); // Breathable atmosphere
            planet3a.getMarket().addCondition(Conditions.DESERT); // Arid terrain
            planet3a.getMarket().addCondition(Conditions.LOW_GRAVITY); // 110 is still moon-sized relative to Aetheris
            planet3a.getMarket().addCondition(Conditions.FARMLAND_POOR); // It's a desert, but farmable
            planet3a.getMarket().addCondition(Conditions.ORGANICS_TRACE); // Hardy desert life
            planet3a.getMarket().addCondition(Conditions.HOT); // Reflective sands make it hot
            planet3a.getMarket().addCondition(Conditions.RUINS_WIDESPREAD); // Historical settlements near oases

            // =================================================================
            // --- PLANET 4: CRYON (The Frozen Sentinel) ---
            // =================================================================
            // UPDATED: Radius 500 (Massive Ice Giant).
            PlanetAPI planet4 = system.addPlanet("cryon", vespera_star, "Cryon", "ice_giant", 24, 500, 20000, 1095);
            
            // VISUALS: Crystalline Cyan
            // A brighter, cleaner turquoise that looks like frozen hydrogen crystals.
            planet4.getSpec().setPlanetColor(new Color(150, 240, 255, 255));
            planet4.getSpec().setAtmosphereColor(new Color(100, 200, 255, 140));
            planet4.getSpec().setAtmosphereThickness(0.6f);
            
            // GLOW: Aurorae (Bright Teal)
            // Massive magnetic field interactions.
            planet4.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet4.getSpec().setGlowColor(new Color(0, 255, 220, 100));
            planet4.getSpec().setUseReverseLightForGlow(true);
            
            planet4.applySpecChanges();
            planet4.setCustomDescriptionId("planet_cryon");
            
            // =================================================================
            // --- EXPANDED RINGS ---
            // =================================================================
            // Planet Radius is 500. We push rings out to 1100-1200 to clear the atmosphere.
            // Width increased to 512f (was 256f) for a grander look.
            
            // Layer 1: Dust (The "Backbone" of the ring)
            system.addRingBand(planet4, "misc", "rings_dust0", 512f, 2, new Color(200, 240, 255, 100), 256f, 1100, 160f, Terrain.RING, "Cryon Rings");
            
            // Layer 2: Ice (The "Sparkle" on top) - Overlapped perfectly as requested
            system.addRingBand(planet4, "misc", "rings_ice0", 512f, 2, Color.white, 256f, 1100, 170f, null, null);
            
            Misc.initConditionMarket(planet4);
            planet4.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet4.getMarket().addCondition(Conditions.COLD);
            planet4.getMarket().addCondition(Conditions.EXTREME_WEATHER);
            planet4.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
            planet4.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            planet4.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);

            // =================================================================
            // --- ENTITIES & LOGISTICS (FULL TRADITIONAL SET) ---
            // =================================================================
            
            // 1. CUSTOM JUMP POINT
            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("cryon_jump_point", "Cryon Point");
            jumpPoint.setCircularOrbit(planet4, 270, planet4.getRadius() + 1000f, 100);
            jumpPoint.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint);

            // 2. COMM RELAY (Vesperis Orbit)
            // Assigned to Token to prevent orphaned entity error
            SectorEntityToken vesperaComm = system.addCustomEntity("vespera_comm", "Vesperis Comm Relay", "comm_relay", "solvaris");
            vesperaComm.setCircularOrbitPointingDown(planet2, 0, 780, 31);
            
            // 3. NAV BUOY (Added - Fleet Burn Speed Boost)
            // Orbiting the star at 6000 distance for central system coverage
            SectorEntityToken vesperaNav = system.addCustomEntity("vespera_nav", "Vespera Nav Buoy", "nav_buoy", "solvaris");
            vesperaNav.setCircularOrbit(vespera_star, 135, 6000, 200);
            
            // 4. SENSOR ARRAY (Added - System-wide Detection)
            // Orbiting the star at 12000 distance for outer system coverage
            SectorEntityToken vesperaSensor = system.addCustomEntity("vespera_sensor", "Vespera Sensor Array", "sensor_array", "solvaris");
            vesperaSensor.setCircularOrbit(vespera_star, 315, 12000, 400);

            // =================================================================
            // --- VESPERA GATE (The Frozen Arch) ---
            // =================================================================
            // Placed at 1500, orbiting Cryon.
            // This sits just outside Cryon's massive ring system (which ends at 1200).
            // It creates a stunning view: The Gate framed by the rings and the cyan giant.
            SectorEntityToken vesperaGate = system.addCustomEntity("vespera_gate", "Solvaris Gate", "inactive_gate", null);
            
            // Orbit: Angle 45, Radius 1500 (Safe distance from rings), Period 100
            vesperaGate.setCircularOrbitPointingDown(planet4, 45, 1500, 100);
            
            vesperaGate.setCustomDescriptionId("gate_vespera");

            // =================================================================
            // --- FINAL TOUCHES --- 
            // =================================================================
            system.autogenerateHyperspaceJumpPoints(true, true);

            cleanup(system);
            
        } catch (Exception e) {
            Global.getLogger(Vespera.class).error("Error generating Vespera: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            // Radius cleared to ensure hyperspace nebula doesn't cover outer planets
            editor.clearArc(x, y, 0, 2200f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 2500f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}