package data.missions.envision_shiptestmission;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

    public void defineMission(MissionDefinitionAPI api) {

        // =====================================================================
        // 1. FLEET SETUP
        // =====================================================================
        api.initFleet(FleetSide.PLAYER, "SRSS", FleetGoal.ATTACK, false, 25);
        api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true, 25);

        api.setFleetTagline(FleetSide.PLAYER, "Solvaris / Oracle Prototypes");
        api.setFleetTagline(FleetSide.ENEMY, "Simulated Vanilla Threats");

        api.addBriefingItem("Test the capabilities of the Solvaris fleet doctrine.");
        api.addBriefingItem("Enemy fleet comprises a full spectrum of vanilla hulls.");
        api.addBriefingItem("Select any unit to pilot.");

        // =====================================================================
        // 2. PLAYER FLEET (Solvaris & Oracle)
        // =====================================================================
        // Note: setting the last boolean to 'true' makes them selectable as flagship
        
        // --- Capitals & Heavy Cruisers ---
        api.addToFleet(FleetSide.PLAYER, "srss_forbearance_standard", FleetMemberType.SHIP, "SRSS Forbearance", true);
        api.addToFleet(FleetSide.PLAYER, "srss_endurance_standard", FleetMemberType.SHIP, "SRSS Endurance", true);
        api.addToFleet(FleetSide.PLAYER, "srss_perpetuation_standard", FleetMemberType.SHIP, "SRSS Perpetuation", true);
        
        // --- Cruisers ---
        api.addToFleet(FleetSide.PLAYER, "srss_stamina_standard", FleetMemberType.SHIP, "SRSS Stamina", true);
        api.addToFleet(FleetSide.PLAYER, "srss_determinant_standard", FleetMemberType.SHIP, "SRSS Determinant", true);
        api.addToFleet(FleetSide.PLAYER, "srss_determinant_fuel_standard", FleetMemberType.SHIP, "SRSS Determinant Fuel", true);
        
        // --- Destroyers & Frigates ---
        api.addToFleet(FleetSide.PLAYER, "srss_continuance_standard", FleetMemberType.SHIP, "SRSS Continuance", true);
        api.addToFleet(FleetSide.PLAYER, "srss_austerity_standard", FleetMemberType.SHIP, "SRSS Austerity", true);
        api.addToFleet(FleetSide.PLAYER, "srss_buffalo_standard", FleetMemberType.SHIP, "SRSS Buffalo", true);
        
        // --- Oracle Series ---
        api.addToFleet(FleetSide.PLAYER, "oracle_vortex_F1", FleetMemberType.SHIP, "Oracle Vortex", true);
        api.addToFleet(FleetSide.PLAYER, "oracle_divergence_F1", FleetMemberType.SHIP, "Oracle Divergence", true);
        api.addToFleet(FleetSide.PLAYER, "oracle_defiance_F1", FleetMemberType.SHIP, "Oracle Defiance", true);

        // =====================================================================
        // 3. ENEMY FLEET (The Vanilla Gauntlet)
        // =====================================================================
        
        // --- Capitals (The Big Tests) ---
        api.addToFleet(FleetSide.ENEMY, "paragon_Elite", FleetMemberType.SHIP, "Sim Paragon", false);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Sim Onslaught", false);
        api.addToFleet(FleetSide.ENEMY, "conquest_Standard", FleetMemberType.SHIP, "Sim Conquest", false);
        api.addToFleet(FleetSide.ENEMY, "astral_Strike", FleetMemberType.SHIP, "Sim Astral", false);
        api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, "Sim Odyssey", false);
        api.addToFleet(FleetSide.ENEMY, "legion_Strike", FleetMemberType.SHIP, "Sim Legion", false);

        // --- Cruisers (Line Battle) ---
        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, "Sim Dominator", false);
        api.addToFleet(FleetSide.ENEMY, "eagle_Balanced", FleetMemberType.SHIP, "Sim Eagle", false);
        api.addToFleet(FleetSide.ENEMY, "aurora_Balanced", FleetMemberType.SHIP, "Sim Aurora", false);
        api.addToFleet(FleetSide.ENEMY, "apogee_Balanced", FleetMemberType.SHIP, "Sim Apogee", false);
        api.addToFleet(FleetSide.ENEMY, "doom_Strike", FleetMemberType.SHIP, "Sim Doom", false);
        api.addToFleet(FleetSide.ENEMY, "champion_Strike", FleetMemberType.SHIP, "Sim Champion", false);
        api.addToFleet(FleetSide.ENEMY, "mora_Strike", FleetMemberType.SHIP, "Sim Mora", false);

        // --- Destroyers (Support & Pressure) ---
        api.addToFleet(FleetSide.ENEMY, "hammerhead_Balanced", FleetMemberType.SHIP, "Sim Hammerhead", false);
        api.addToFleet(FleetSide.ENEMY, "sunder_CS", FleetMemberType.SHIP, "Sim Sunder", false);
        api.addToFleet(FleetSide.ENEMY, "medusa_Attack", FleetMemberType.SHIP, "Sim Medusa", false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, "Sim Enforcer", false);
        api.addToFleet(FleetSide.ENEMY, "harbinger_Strike", FleetMemberType.SHIP, "Sim Harbinger", false);
        api.addToFleet(FleetSide.ENEMY, "manticore_Strike", FleetMemberType.SHIP, "Sim Manticore", false);

        // --- Frigates (Swarmers) ---
        api.addToFleet(FleetSide.ENEMY, "tempest_Attack", FleetMemberType.SHIP, "Sim Tempest", false);
        api.addToFleet(FleetSide.ENEMY, "wolf_CS", FleetMemberType.SHIP, "Sim Wolf", false);
        api.addToFleet(FleetSide.ENEMY, "lasher_Standard", FleetMemberType.SHIP, "Sim Lasher", false);
        api.addToFleet(FleetSide.ENEMY, "hyperion_Strike", FleetMemberType.SHIP, "Sim Hyperion", false);
        api.addToFleet(FleetSide.ENEMY, "monitor_Escort", FleetMemberType.SHIP, "Sim Monitor", false);
        api.addToFleet(FleetSide.ENEMY, "omen_PD", FleetMemberType.SHIP, "Sim Omen", false);
        api.addToFleet(FleetSide.ENEMY, "afflictor_Strike", FleetMemberType.SHIP, "Sim Afflictor", false);

        // =====================================================================
        // 4. MAP SETUP
        // =====================================================================
        float width = 24000f; // Increased width for larger fleets
        float height = 18000f;
        api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);

        float minX = -width/2;
        float minY = -height/2;

        // Visuals: Blue Giant theme matches Seraphina
        api.addPlanet(0, 0, 80f, "star_blue_giant", 250f, true);
        api.addPlanet(500, -500, 200f, "barren", 250f, true);
        
        // Add a background nebula for texture
        api.addNebula(0, 0, 2000); 

        // Objectives
        api.addObjective(minX + width * 0.25f, minY + height * 0.25f, "nav_buoy");
        api.addObjective(minX + width * 0.75f, minY + height * 0.75f, "sensor_array");
        api.addObjective(minX + width * 0.5f,  minY + height * 0.5f,  "comm_relay");
        
        // Added extra objectives for the larger map
        api.addObjective(minX + width * 0.25f, minY + height * 0.75f, "nav_buoy");
        api.addObjective(minX + width * 0.75f, minY + height * 0.25f, "sensor_array");

        // Asteroids & Nebula clouds
        for (int i = 0; i < 20; i++) {
            float x = (float) Math.random() * width - width/2;
            float y = (float) Math.random() * height - height/2;
            float radius = 100f + (float) Math.random() * 900f;
            api.addNebula(x, y, radius);
        }
        
        api.addAsteroidField(0, 0, 0, 3000f, 20f, 70f, 150);
    }
}