package data.scripts.industries;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class DysonHubPlugin extends BaseIndustry {

    // --- CONFIGURATION ---
    private static final int DAYS_TO_REMOVE_DECIV = 90;
    private static final int DAYS_TO_REMOVE_ROGUE_AI = 60;
    
    // Tracking variables
    private int daysPassedDeciv = 0;
    private int lastDayCheckedDeciv = 0;
    
    private int daysPassedRogue = 0;
    private int lastDayCheckedRogue = 0;

    @Override
    public void apply() {
        String id = getModId(); 

        // 1. BALANCED ACCESSIBILITY (+50%)
        market.getAccessibilityMod().modifyPercent(id, 50f, "Dyson Link Connectivity");

        // 2. MAX STABILITY (+10)
        market.getStability().modifyFlat(id, 10f, "Dyson Oversight Algorithm");

        // 3. FORCE REMOVE PATHER CELLS (Immediate Effect)
        // Note: The "Interest" is handled mathematically in getPatherInterest() below.
        // This ensures that if a cell ALREADY exists, it gets removed.
        if (isFunctional() && market.hasCondition(Conditions.PATHER_CELLS)) {
            market.removeCondition(Conditions.PATHER_CELLS);
        }

        // 4. BALANCED SUPPLY & DEMAND
        demand(Commodities.CREW, 1);
        demand(Commodities.HEAVY_MACHINERY, 1);
        
        supply(Commodities.FUEL, 30);
        supply(Commodities.VOLATILES, 20);

        super.apply(true);
    }

    @Override
    public void unapply() {
        String id = getModId();
        market.getAccessibilityMod().unmodify(id);
        market.getStability().unmodify(id);
        super.unapply();
    }

    // --- TIME-BASED PURGING LOGIC (Advance) ---

    @Override
    public void advance(float amount) {
        super.advance(amount);

        if (isFunctional()) {
            CampaignClockAPI clock = Global.getSector().getClock();
            int currentDay = clock.getDay();

            // 1. HANDLE DECIVILIZED SUBPOPULATION (90 Days)
            if (market.hasCondition(Conditions.DECIVILIZED_SUBPOP) || market.hasCondition(Conditions.DECIVILIZED)) {
                if (currentDay != lastDayCheckedDeciv) {
                    daysPassedDeciv++;
                    lastDayCheckedDeciv = currentDay;
                    if (daysPassedDeciv >= DAYS_TO_REMOVE_DECIV) {
                        performDecivRemoval();
                    }
                }
            } else {
                daysPassedDeciv = 0;
            }

            // 2. HANDLE ROGUE AI CORE (60 Days)
            if (market.hasCondition(Conditions.ROGUE_AI_CORE)) {
                if (currentDay != lastDayCheckedRogue) {
                    daysPassedRogue++;
                    lastDayCheckedRogue = currentDay;
                    if (daysPassedRogue >= DAYS_TO_REMOVE_ROGUE_AI) {
                        performRogueRemoval();
                    }
                }
            } else {
                daysPassedRogue = 0;
            }
        }
    }

    // --- PURGE ACTIONS ---

    private void performDecivRemoval() {
        if (market.isPlayerOwned()) {
            MessageIntel intel = new MessageIntel("Dyson Protocols on " + market.getName(), Misc.getBasePlayerColor());
            intel.addLine("    - Decivilized elements neutralized by automated security.");
            intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO, market);
        }
        market.removeCondition(Conditions.DECIVILIZED_SUBPOP);
        market.removeCondition(Conditions.DECIVILIZED);
        daysPassedDeciv = 0;
    }

    private void performRogueRemoval() {
        if (market.isPlayerOwned()) {
            MessageIntel intel = new MessageIntel("Security Sweep on " + market.getName(), Misc.getBasePlayerColor());
            intel.addLine("    - Rogue AI Core terminated by Dyson Hub oversight.");
            intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO, market);
        }
        market.removeCondition(Conditions.ROGUE_AI_CORE);
        daysPassedRogue = 0;
    }

    // --- PATHER INTEREST CANCELLATION ---

    @Override
    public float getPatherInterest() {
        // Works intrinsically (flawlessly) without needing an Alpha Core.
        if (isFunctional()) {
            float totalPatherInterest = 0f;

            // 1. Check Admin
            if (market.getAdmin().getAICoreId() != null) {
                totalPatherInterest += 10f; 
            }

            // 2. Sum up all other industries
            for (Industry industry : market.getIndustries()) {
                if (!industry.isHidden() && !industry.getId().equals(getId())) {
                    float interest = industry.getPatherInterest();
                    if (interest > 0) {
                        totalPatherInterest += interest;
                    }
                }
            }

            // Return negative total to mathematically cancel everything to 0.
            return -totalPatherInterest;
        } 
        
        return 0f; 
    }

    // --- TOOLTIP DESCRIPTION ---

    @Override
    protected void addPostDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        // Status: Deciv Removal
        if ((market.hasCondition(Conditions.DECIVILIZED_SUBPOP) || market.hasCondition(Conditions.DECIVILIZED)) 
                && mode == IndustryTooltipMode.NORMAL && !isBuilding()) {
            int percent = (int) (((float) daysPassedDeciv / (float) DAYS_TO_REMOVE_DECIV) * 100f);
            if (percent > 99) percent = 99; 
            tooltip.addPara("Pacification progress: %s", opad, highlight, percent + "%");
        }

        // Status: Rogue AI Removal
        if (market.hasCondition(Conditions.ROGUE_AI_CORE) && mode == IndustryTooltipMode.NORMAL && !isBuilding()) {
            int percent = (int) (((float) daysPassedRogue / (float) DAYS_TO_REMOVE_ROGUE_AI) * 100f);
            if (percent > 99) percent = 99;
            tooltip.addPara("Rogue AI purge progress: %s", opad, highlight, percent + "%");
        }
        
        // Pather Suppression Note
        tooltip.addPara("The Dyson Hub's panopticon sensors %s on this colony.", 
                opad, highlight, "neutralize all Luddic Path interest");
    }

    // --- LOCKING MECHANISM ---

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public String getUnavailableReason() {
        return "This is a unique stellar construct that cannot be replicated.";
    }
    
    // Cleanup variables if removed
    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        daysPassedDeciv = 0;
        daysPassedRogue = 0;
        super.notifyBeingRemoved(mode, forUpgrade);
    }
}