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
        api.initFleet(FleetSide.PLAYER, "SRSS", FleetGoal.ATTACK, false, 20);
        api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true, 20);

        api.setFleetTagline(FleetSide.PLAYER, "Survivors");
        api.setFleetTagline(FleetSide.ENEMY, "Simulation Targets");

        api.addBriefingItem("Test the capabilities of the Solvaris fleet doctrine.");
        api.addBriefingItem("All units designated as Command Ships.");

        // =====================================================================
        // 2. PLAYER FLEET (Solvaris)
        // =====================================================================
        // All ships set to TRUE (Flagship status) for testing purposes
        
        api.addToFleet(FleetSide.PLAYER, "srss_forbearance_standard", FleetMemberType.SHIP, "SRSS Foundation", true);
        api.addToFleet(FleetSide.PLAYER, "srss_endurance_standard", FleetMemberType.SHIP, "SRSS Focus", true);
        api.addToFleet(FleetSide.PLAYER, "srss_perpetuation_standard", FleetMemberType.SHIP, "SRSS Cross", true);
        api.addToFleet(FleetSide.PLAYER, "srss_stamina_standard", FleetMemberType.SHIP, "SRSS Multitude", true);
        
        // Corrected spelling: "standard" instead of "stardard"
        api.addToFleet(FleetSide.PLAYER, "srss_determinant_standard", FleetMemberType.SHIP, "SRSS Assault", true);
        api.addToFleet(FleetSide.PLAYER, "srss_determinant_fuel_standard", FleetMemberType.SHIP, "SRSS Fountain", true);
        
        api.addToFleet(FleetSide.PLAYER, "srss_continuance_standard", FleetMemberType.SHIP, "SRSS Burst", true);
        api.addToFleet(FleetSide.PLAYER, "srss_austerity_standard", FleetMemberType.SHIP, "SRSS Impact", true);
        api.addToFleet(FleetSide.PLAYER, "oracle_vortex_F1", FleetMemberType.SHIP, "Oracle Vortex", true);
        api.addToFleet(FleetSide.PLAYER, "oracle_divergence_F1", FleetMemberType.SHIP, "Oracle Divergence", true);
        api.addToFleet(FleetSide.PLAYER, "oracle_defiance_F1", FleetMemberType.SHIP, "Oracle Defiance", true);

        // =====================================================================
        // 3. ENEMY FLEET (Targets)
        // =====================================================================
        api.addToFleet(FleetSide.ENEMY, "conquest_Standard", FleetMemberType.SHIP, "Target Alpha", true);
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, "Target Beta", true);
        api.addToFleet(FleetSide.ENEMY, "eagle_Balanced", FleetMemberType.SHIP, "Target Gamma", true);
        api.addToFleet(FleetSide.ENEMY, "falcon_Attack", FleetMemberType.SHIP, "Target Delta", true);
        api.addToFleet(FleetSide.ENEMY, "lasher_Standard", FleetMemberType.SHIP, "Target Epsilon", true);

        // =====================================================================
        // 4. MAP SETUP
        // =====================================================================
        float width = 18000f;
        float height = 14000f;
        api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);

        float minX = -width/2;
        float minY = -height/2;

        // Background Visuals
        api.addPlanet(0, 0, 50f, "star_blue_giant", 250f, true);
        api.addPlanet(500, -500, 200f, "barren", 250f, true);

        // Tactical Objectives
        api.addObjective(minX + width * 0.25f, minY + height * 0.25f, "nav_buoy");
        api.addObjective(minX + width * 0.75f, minY + height * 0.75f, "sensor_array");
        api.addObjective(minX + width * 0.5f,  minY + height * 0.5f,  "comm_relay");

        // Environment
        for (int i = 0; i < 15; i++) {
            float x = (float) Math.random() * width - width/2;
            float y = (float) Math.random() * height - height/2;
            float radius = 100f + (float) Math.random() * 900f;
            api.addNebula(x, y, radius);
        }
        
        api.addAsteroidField(minX, minY + height * 0.5f, 0, 2000f, 20f, 70f, 100);
    }
}