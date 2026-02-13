package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

public class Isola implements SectorGeneratorPlugin {

    public Isola() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            StarSystemAPI system = sector.createStarSystem("Isola");
            system.setBackgroundTextureFilename("graphics/backgrounds/background05.jpg");

            PlanetAPI star = system.initStar("isola_star", "star_white", 300f, 400f);
            star.setName("Isola");
            
            system.setLightColor(new Color(220, 230, 255));
            system.setCenter(star);
            
            system.addRingBand(star, "misc", "rings_dust0", 256f, 0, new Color(200, 200, 210, 150), 256f, 1800f, 60f, Terrain.RING, "The Pale Halo");
            
            system.addAsteroidBelt(star, 100, 3200, 128, 150, 250, Terrain.ASTEROID_BELT, "Isola's Veil");

            PlanetAPI planet0 = system.addPlanet("tacita", star, "Tacita", "tundra", 45, 300, 5500, 120);
            planet0.setCustomDescriptionId("planet_tacita");
            planet0.getSpec().setPlanetColor(new Color(200, 220, 255, 255));
            planet0.getSpec().setAtmosphereColor(new Color(150, 180, 200, 100));
            planet0.getSpec().setAtmosphereThickness(0.4f);
            planet0.getSpec().setGlowColor(new Color(0,0,0,0));
            planet0.applySpecChanges();

            Misc.initConditionMarket(planet0);
            planet0.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet0.getMarket().addCondition(Conditions.COLD);
            planet0.getMarket().addCondition(Conditions.RUINS_SCATTERED);
            planet0.getMarket().addCondition(Conditions.ORE_MODERATE);
            planet0.getMarket().addCondition(Conditions.RARE_ORE_MODERATE);
            planet0.getMarket().addCondition(Conditions.THIN_ATMOSPHERE);

            JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("isola_jump", "Isola Fringe");
            jumpPoint1.setCircularOrbit(star, 200, 5500, 120);
            jumpPoint1.setRelatedPlanet(planet0);
            jumpPoint1.setStandardWormholeToHyperspaceVisual();
            system.addEntity(jumpPoint1);

            SectorEntityToken stable1 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            stable1.setCircularOrbit(star, 45, 1200, 120);

            SectorEntityToken stable2 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            stable2.setCircularOrbit(star, 260, 3500, 180);

            SectorEntityToken stable3 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            stable3.setCircularOrbit(star, 140, 3500, 180);

            SectorEntityToken stable4 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            stable4.setCircularOrbit(star, 270, 6500, 400);

            SectorEntityToken stable5 = system.addCustomEntity(null, null, Entities.STABLE_LOCATION, Factions.NEUTRAL);
            stable5.setCircularOrbit(star, 135, 8000, 600);

            system.autogenerateHyperspaceJumpPoints(true, true);
            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Isola.class).error("Error generating Isola system: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            editor.clearArc(x, y, 0, 1400f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 1200f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}