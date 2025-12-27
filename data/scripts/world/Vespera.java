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
            StarSystemAPI system = sector.createStarSystem("Vespera");
            system.getLocation().set(30000, -12000); 
            system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

            // --- STAR ---
            PlanetAPI vespera_star = system.initStar("vespera", "star_yellow", 900f, 200f, 5f, 1.0f, 2.0f);
            system.setLightColor(new Color(245, 250, 255));
            
            // --- BELTS & RINGS ---
            system.addAsteroidBelt(vespera_star, 150, 6300, 256f, 300, 256f, Terrain.ASTEROID_BELT, null);
            system.addRingBand(vespera_star, "misc", "rings_ice0", 256f, 1, Color.BLUE, 256f, 6300, 512f, Terrain.RING, "Vespera Ring Belt");
            system.addRingBand(vespera_star, "misc", "rings_dust0", 256f, 0, new Color(100, 90, 80, 255), 512f, 23000f, 400f, Terrain.RING, "The Outer Barrier");

            // --- PLANET 0: BLACKROCK ---
            PlanetAPI planet0 = system.addPlanet("blackrock", vespera_star, "Blackrock", "rocky_metallic", 177, 170, 4300, 365);
            planet0.getSpec().setPlanetColor(new Color(116, 185, 238, 255));
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "sindria"));
            planet0.getSpec().setGlowColor(new Color(30, 100, 200, 255));
            planet0.getSpec().setUseReverseLightForGlow(true);
            planet0.applySpecChanges();
            planet0.setCustomDescriptionId("planet_blackrock");

            // --- PLANET 1: NIMORIA ---
            PlanetAPI planet1 = system.addPlanet("nimoria", vespera_star, "Nimoria", "terran", 24, 130, 8000, 325);
            planet1.setCustomDescriptionId("planet_nimoria");
            
            Misc.initConditionMarket(planet1);
            planet1.getMarket().addCondition(Conditions.HABITABLE);
            planet1.getMarket().addCondition(Conditions.MILD_CLIMATE);
            planet1.getMarket().addCondition(Conditions.FARMLAND_POOR);
            planet1.getMarket().addCondition(Conditions.RUINS_SCATTERED);

            // --- MOON: AMARIS ---
            PlanetAPI planet1a = system.addPlanet("amaris", planet1, "Amaris", "barren2", 24, 50, 700, 31);
            planet1a.setCustomDescriptionId("planet_amaris");
            Misc.initConditionMarket(planet1a);
            planet1a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
            planet1a.getMarket().addCondition(Conditions.LOW_GRAVITY);

            // --- PLANET 2: VESPERIS ---
            PlanetAPI planet2 = system.addPlanet("vesperis", vespera_star, "Vesperis", "water", 24, 290, 11000, 365);
            planet2.getSpec().setPlanetColor(new Color(0, 190, 255, 255));
            planet2.getSpec().setAtmosphereColor(new Color(100, 200, 255, 200));
            planet2.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "sindria"));
            planet2.getSpec().setGlowColor(new Color(30, 100, 200, 255));
            planet2.getSpec().setUseReverseLightForGlow(true);
            planet2.applySpecChanges();
            planet2.setCustomDescriptionId("planet_vesperis");
            system.addRingBand(planet2, "misc", "rings_ice0", 256f, 1, Color.BLUE, 256f, 1000, 256f, Terrain.RING, "Vesperis Ice Rings");
            Misc.initConditionMarket(planet2);
            planet2.getMarket().addCondition(Conditions.WATER_SURFACE);
            planet2.getMarket().addCondition(Conditions.HABITABLE);
            planet2.getMarket().addCondition(Conditions.VOLATILES_TRACE);

            // --- PLANET 3: AETHERIS ---
            PlanetAPI planet3 = system.addPlanet("aetheris", vespera_star, "Aetheris", "terran", 24, 180, 17000, 681);
            planet3.getSpec().setPlanetColor(new Color(200, 255, 245, 255));
            planet3.getSpec().setAtmosphereColor(new Color(220, 250, 240, 150));
            planet3.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet3.getSpec().setGlowColor(new Color(30, 100, 200, 255));
            planet3.getSpec().setUseReverseLightForGlow(true);
            planet3.applySpecChanges();
            planet3.setCustomDescriptionId("planet_aetheris");
            Misc.initConditionMarket(planet3);
            planet3.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet3.getMarket().addCondition(Conditions.MILD_CLIMATE);
            planet3.getMarket().addCondition(Conditions.FARMLAND_RICH);
            planet3.getMarket().addCondition(Conditions.ORE_RICH);
            planet3.getMarket().addCondition(Conditions.RUINS_VAST);
            planet3.getMarket().addCondition(Conditions.HABITABLE);
            planet3.getMarket().addCondition(Conditions.ORGANICS_PLENTIFUL);

            // --- MOON: FERRONOX ---
            PlanetAPI planet3a = system.addPlanet("ferronox", planet3, "Ferronox", "barren", 24, 50, 1000, 31);
            Misc.initConditionMarket(planet3a);
            planet3a.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet3a.getMarket().addCondition(Conditions.LOW_GRAVITY);
            planet3a.getMarket().addCondition(Conditions.NO_ATMOSPHERE);

            // --- PLANET 4: CRYON ---
            PlanetAPI planet4 = system.addPlanet("cryon", vespera_star, "Cryon", "ice_giant", 24, 400, 20000, 1095);
            system.addRingBand(planet4, "misc", "rings_dust0", 256f, 2, Color.white, 256f, 650, 128f, Terrain.RING, "Cryon Rings");
            system.addRingBand(planet4, "misc", "rings_ice0", 256f, 2, Color.white, 256f, 650, 128f, Terrain.RING, "Cryon Rings");
            planet4.getSpec().setPlanetColor(new Color(175, 238, 238, 255));
            planet4.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet4.getSpec().setGlowColor(new Color(0, 255, 205, 80));
            planet4.getSpec().setUseReverseLightForGlow(true);
            planet4.applySpecChanges();
            planet4.setCustomDescriptionId("planet_cryon");
            Misc.initConditionMarket(planet4);
            planet4.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet4.getMarket().addCondition(Conditions.COLD);
            planet4.getMarket().addCondition(Conditions.EXTREME_WEATHER);
            planet4.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
            planet4.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            planet4.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);

            // --- CUSTOM JUMP POINT (VRI FORMULA) ---
            JumpPointAPI jumpPoint = Global.getFactory().createJumpPoint("cryon_jump_point", "Cryon Point");
            jumpPoint.setCircularOrbit(planet4, 270, planet4.getRadius() + 1000f, 100);
            jumpPoint.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint);

            // --- ENTITIES ---
            system.addCustomEntity("vespera_comm", "Vesperis Comm", "comm_relay", "solvaris").setCircularOrbitPointingDown(planet2, 0, 780, 31);
            SectorEntityToken gate = system.addCustomEntity("vespera_gate", "Solvaris Gate", "inactive_gate", null);
            gate.setCircularOrbitPointingDown(planet4, 45, 1500, 100);

            // --- GENERATE STAR JUMP POINTS ---
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
            editor.clearArc(x, y, 0, 2000f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 2500f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}