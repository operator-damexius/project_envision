package data.scripts.world;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;
import com.fs.starfarer.api.util.Misc;

public class Vailara implements SectorGeneratorPlugin {

    public Vailara() {}

    @Override
    public void generate(SectorAPI sector) {
        try {
            // =================================================================
            // 1. SYSTEM SETUP
            // =================================================================
            StarSystemAPI system = sector.createStarSystem("Vailara");
            system.getLocation().set(33000, -12000); 
            system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

            // --- VAILARA NEBULA ---
            Misc.addNebulaFromPNG("data/campaign/terrain/vailara_nebula.png",
                0, 0, system, "terrain", "nebula_blue", 4, 4, StarAge.YOUNG);

            // Primary Star
            PlanetAPI vailara_star = system.initStar("vailara", "star_white", 600f, 400f, 10f, 1.0f, 2.0f);
            system.setLightColor(new Color(245, 250, 255));

            // =================================================================
            // 2. THE LEGENDARY RINGS (40 RINGS)
            // =================================================================
            int ringCount = 0;
            float currentRadius = 800f;
            
            while (ringCount < 40) {
                String texture = "rings_ice0";
                Color ringColor = new Color(200, 220, 255, 150); 
                
                if (currentRadius > 8000) { 
                    texture = "rings_dust0"; 
                    ringColor = new Color(150, 140, 130, 100);
                } else if (currentRadius > 2500 && currentRadius < 4500) {
                    texture = "rings_special0"; 
                    ringColor = new Color(100, 200, 255, 200);
                }

                // Gaps for planets
                boolean gap = (currentRadius > 2000 && currentRadius < 2300) || 
                              (currentRadius > 4500 && currentRadius < 5500) || 
                              (currentRadius > 7000 && currentRadius < 7600);   

                if (!gap) {
                    system.addRingBand(vailara_star, "misc", texture, 256f, 0, ringColor, 256f, currentRadius, 40f + (ringCount * 2), Terrain.RING, "Vailara Ring " + (ringCount + 1));
                    ringCount++;
                }
                currentRadius += 250f;
            }

            // =================================================================
            // 3. PLANETS
            // =================================================================
            
            // Nyxara (Arid World)
            PlanetAPI planet0 = system.addPlanet("nyxara", vailara_star, "Nyxara", "arid", 10, 110, 2150, 110);
            planet0.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet0.getSpec().setGlowColor(new Color(240, 220, 130, 255));
            planet0.getSpec().setUseReverseLightForGlow(true);
            planet0.applySpecChanges();
            planet0.setInteractionImage("illustrations", "mine");
            planet0.setCustomDescriptionId("planet_nyxara");
            
            Misc.initConditionMarket(planet0);
            MarketAPI market0 = planet0.getMarket();
            market0.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            market0.addCondition(Conditions.HOT);
            market0.addCondition(Conditions.ORE_RICH);
            market0.addCondition(Conditions.RARE_ORE_RICH);
            market0.addCondition(Conditions.FARMLAND_RICH);
            market0.addCondition(Conditions.ORGANICS_ABUNDANT);
            market0.addCondition(Conditions.VOLATILES_DIFFUSE);

            // Eldara (Ice Giant)
            PlanetAPI planet1 = system.addPlanet("eldara", vailara_star, "Eldara", "ice_giant", 98, 400, 4750, 200);
            system.addRingBand(planet1, "misc", "rings_ice0", 256f, 2, Color.white, 256f, 650, 128f, Terrain.RING, "Eldara Rings");
            planet1.getSpec().setPlanetColor(new Color(200, 255, 245, 255));
            planet1.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "aurorae"));
            planet1.getSpec().setGlowColor(new Color(0, 255, 205, 62));
            planet1.getSpec().setUseReverseLightForGlow(true);
            planet1.applySpecChanges();
            planet1.setCustomDescriptionId("planet_eldara");
            
            Misc.initConditionMarket(planet1);
            planet1.getMarket().setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            planet1.getMarket().addCondition(Conditions.COLD);
            planet1.getMarket().addCondition(Conditions.EXTREME_WEATHER);
            planet1.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
            planet1.getMarket().addCondition(Conditions.HIGH_GRAVITY);
            planet1.getMarket().addCondition(Conditions.VOLATILES_PLENTIFUL);

            // =================================================================
            // ORVION (STATION - MILITARY HUB - SIZE 8)
            // =================================================================
            SectorEntityToken orvionStation = system.addCustomEntity("orvion", "Orvion Fortress", "station_hightech3", "solvaris");
            orvionStation.setCircularOrbitPointingDown(planet1, 90, 900, 30);
            orvionStation.setCustomDescriptionId("station_orvion");
            
            // Create Market (Size 8)
            MarketAPI orvionMarket = Global.getFactory().createMarket("orvion_market", "Orvion Fortress", 8);
            orvionMarket.setFactionId("solvaris");
            orvionMarket.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            orvionMarket.setPrimaryEntity(orvionStation);
            
            // Submarkets
            orvionMarket.addSubmarket(Submarkets.SUBMARKET_OPEN);
            orvionMarket.addSubmarket(Submarkets.SUBMARKET_STORAGE);
            orvionMarket.addSubmarket(Submarkets.SUBMARKET_BLACK);
            orvionMarket.addSubmarket(Submarkets.GENERIC_MILITARY);
            
            // Conditions (POPULATION 8)
            orvionMarket.addCondition(Conditions.POPULATION_8);
            orvionMarket.addCondition(Conditions.OUTPOST);
            
            // --- INDUSTRIES (Military & Fuel Only) ---
            orvionMarket.addIndustry(Industries.POPULATION);
            orvionMarket.addIndustry(Industries.MEGAPORT);
            orvionMarket.addIndustry(Industries.WAYSTATION);
            orvionMarket.addIndustry(Industries.HEAVYBATTERIES);
            orvionMarket.addIndustry(Industries.STARFORTRESS_HIGH);
            orvionMarket.addIndustry(Industries.PATROLHQ);
            
            // Production (No Farms, No Light Industry)
            orvionMarket.addIndustry(Industries.FUELPROD);
            
            // Custom Industry
            orvionMarket.addIndustry("cryosanctum");

            orvionStation.setMarket(orvionMarket);
            sector.getEconomy().addMarket(orvionMarket, true);

            // Zyphir (Ruined)
            PlanetAPI planet2 = system.addPlanet("zyphir", vailara_star, "Zyphir", "desert", 20, 180, 7300, 340);
            planet2.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "banded"));
            planet2.getSpec().setGlowColor(new Color(0, 200, 255, 180));
            planet2.getSpec().setUseReverseLightForGlow(true);
            planet2.getSpec().setPlanetColor(new Color(156, 46, 53, 255));
            planet2.applySpecChanges();
            planet2.setCustomDescriptionId("planet_zyphir");
            
            Misc.initConditionMarket(planet2);
            planet2.getMarket().addCondition(Conditions.DECIVILIZED);
            planet2.getMarket().addCondition(Conditions.RUINS_VAST);
            planet2.getMarket().addCondition(Conditions.ORE_RICH);
            planet2.getMarket().addCondition(Conditions.FARMLAND_ADEQUATE);

            // =================================================================
            // 4. TERRAIN & ENTITIES
            // =================================================================
            
            // Eldara Magnetic Field
            system.addTerrain(Terrain.MAGNETIC_FIELD,
                new MagneticFieldParams(300f, (planet1.getRadius() + 50f) + 150f, planet1, planet1.getRadius() + 50f, planet1.getRadius() + 350f,
                    new Color(66, 97, 143, 50), 0.5f, new Color(66, 97, 143, 255), new Color(98, 121, 165, 255), new Color(149, 166, 209, 255),
                    new Color(173, 186, 221, 255), new Color(213, 227, 254, 255), new Color(230, 239, 255, 255), new Color(255, 255, 255, 255)
                )
            ).setCircularOrbit(planet1, 0, 0, 100);

            // L4/L5 Nebula
            String nebulaPattern = "   xxx   "; 
            system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(nebulaPattern, 10, 10, "terrain", "nebula_blue", 4, 4, null))
                .setCircularOrbit(vailara_star, 120, 11500, 820);
            system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(nebulaPattern, 10, 10, "terrain", "nebula_blue", 4, 4, null))
                .setCircularOrbit(vailara_star, 0, 11500, 820);

            // Jump Points
            system.autogenerateHyperspaceJumpPoints(true, true);
            cleanup(system);

        } catch (Exception e) {
            Global.getLogger(Vailara.class).error("Error generating Vailara system: ", e);
        }
    }

    void cleanup(StarSystemAPI system) {
        try {
            HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
            NebulaEditor editor = new NebulaEditor(plugin);
            float x = system.getLocation().x;
            float y = system.getLocation().y;
            editor.clearArc(x, y, 0, 1500f, 0, 360f, 0.25f);
            editor.clearArc(x, y, 0, 2000f, 0, 360f, 0.0f);
        } catch (Exception e) {}
    }
}