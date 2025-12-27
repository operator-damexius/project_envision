package data.scripts;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;

public class EnvisionFactionRelations {

    public static void init(SectorAPI sector) {
        
        // 1. Register Bounties
        // This allows your faction's deserters to appear on the bounty board
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("solvaris");

        // 2. Get Factions
        FactionAPI solvaris = sector.getFaction("solvaris");
        FactionAPI argent   = sector.getFaction("argent");

        // 3. Define Solvaris Relationships
        if (solvaris != null) {
            // Alliance with Argent
            solvaris.setRelationship("argent", 1.0f);
            
            // Core World Politics
            solvaris.setRelationship(Factions.HEGEMONY, -0.5f);    // Hostile
            solvaris.setRelationship(Factions.TRITACHYON, 0.5f);   // Friendly
            solvaris.setRelationship(Factions.PERSEAN, 0.1f);      // Neutral/Warm
            solvaris.setRelationship(Factions.LUDDIC_CHURCH, -0.7f); // Hostile
            solvaris.setRelationship(Factions.INDEPENDENT, 0.5f);  // Friendly
            solvaris.setRelationship(Factions.PLAYER, 0.0f);       // Neutral start
            
            // Permanent Enemies
            solvaris.setRelationship(Factions.LUDDIC_PATH, -1.0f);
            solvaris.setRelationship(Factions.PIRATES, -1.0f);
        }

        // 4. Define Argent Relationships
        if (argent != null) {
            // Alliance with Solvaris
            argent.setRelationship("solvaris", 1.0f);

            // Isolationist / Hostile
            argent.setRelationship(Factions.HEGEMONY, -1.0f);
            argent.setRelationship(Factions.LUDDIC_CHURCH, -1.0f);
            argent.setRelationship(Factions.LUDDIC_PATH, -1.0f);
            argent.setRelationship(Factions.PIRATES, -1.0f);
            
            // Neutrality
            argent.setRelationship(Factions.TRITACHYON, 0.0f);
            argent.setRelationship(Factions.INDEPENDENT, 0.0f);
            argent.setRelationship(Factions.PLAYER, 0.0f);
        }
    }
}