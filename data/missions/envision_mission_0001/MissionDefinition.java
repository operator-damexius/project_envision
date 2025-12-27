package data.missions.envision_mission_0001;

import java.awt.Color;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {

		// --- 1. General Setup ---
		api.initFleet(FleetSide.PLAYER, "SS", FleetGoal.ATTACK, false);
		api.initFleet(FleetSide.ENEMY, "ISS", FleetGoal.ATTACK, true);

		api.setFleetTagline(FleetSide.PLAYER, "Oracle Loyalists");
		api.setFleetTagline(FleetSide.ENEMY, "Oracle Traitor Wing [MIRROR MATCH]");
		
		api.addBriefingItem("Destroy the Rogue Prototypes.");
		api.addBriefingItem("Navigate the hazard zone carefully.");

		// --- 2. Player Fleet ---
		api.addToFleet(FleetSide.PLAYER, "oracle_defiance_F1", FleetMemberType.SHIP, "SS Defiance", true);
		api.addToFleet(FleetSide.PLAYER, "oracle_divergence_F1", FleetMemberType.SHIP, "SS Olenus", false);
		api.addToFleet(FleetSide.PLAYER, "oracle_divergence_F1", FleetMemberType.SHIP, "SS Aegisthus", false);
		api.addToFleet(FleetSide.PLAYER, "oracle_vortex_F1", FleetMemberType.SHIP, "SS Clytie", false);
		api.addToFleet(FleetSide.PLAYER, "oracle_vortex_F1", FleetMemberType.SHIP, "SS Medusa", false);

		// --- 3. Enemy Fleet (Mirror) ---
		api.addToFleet(FleetSide.ENEMY, "oracle_defiance_F1", FleetMemberType.SHIP, "ISS Nemesis", true);
		api.addToFleet(FleetSide.ENEMY, "oracle_divergence_F1", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "oracle_divergence_F1", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "oracle_vortex_F1", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "oracle_vortex_F1", FleetMemberType.SHIP, false);
		
		// --- 4. Objectives ---
		api.defeatOnShipLoss("SS Defiance");

		// --- 5. ENHANCED ENVIRONMENT ---
		float width = 24000f; 
		float height = 24000f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);
		
		// A. The Central Star (Blue Giant)
		// High gravity will pull damaged ships in!
		api.addPlanet(0, 0, 0, "star_blue_giant", 450f, true);
		
		// B. The Background Planet (Visual Scale)
		api.addPlanet(1, 2000f, 2500f, "gas_giant", 300f, true);
		
		// REMOVED: addRingBand (Not supported in Missions, caused the crash)

		// D. Asteroid Fields (The real hazard)
		// Field 1: Inner dense belt (Combat cover)
		api.addAsteroidField(0, 0, 45, 1500f, 20f, 70f, 150);
		// Field 2: Outer scattered rocks (Sniper interference)
		api.addAsteroidField(0, 0, 90, 4000f, 10f, 50f, 300);
		
		// E. Nebula Clouds (Stealth/Ambush zones)
		// These block vision and look cool.
		api.addNebula(1000f, 1000f, 1500f);
		api.addNebula(-1500f, -500f, 2000f);
		api.addNebula(500f, -2000f, 1000f);
	}
}