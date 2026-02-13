package data.scripts.industries;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class PacificationPlugin extends BaseIndustry {

    // --- CONFIGURATION ---
    private static final int DAYS_TO_REMOVE_DECIV = 90;
    private static final int DAYS_TO_REMOVE_ROGUE_AI = 60;
    
    // Bonus Configuration
    private static final int STABILITY_BONUS = 2;
    private static final float DEFENSE_BONUS = 0.2f; // 20%
    
    // Tracking variables
    private int daysPassedDeciv = 0;
    private int lastDayCheckedDeciv = 0;
    
    private int daysPassedRogue = 0;
    private int lastDayCheckedRogue = 0;

    @Override
    public void apply() {
        super.apply(true);
        // NO DEMANDS: This industry runs purely on credits (upkeep).
        
        // APPLY BONUSES
        if (isFunctional()) {
            // 1. Stability Bonus
            market.getStability().modifyFlat(getId(), STABILITY_BONUS, getCurrentName());
            
            // 2. Ground Defense Bonus (+20%)
            // FIXED: Using standard API chain with raw string ID to prevent crashes
            market.getStats().getDynamic().getMod("ground_defenses_mod")
                  .modifyPercent(getId(), DEFENSE_BONUS * 100f, getCurrentName());
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        
        // REMOVE BONUSES
        market.getStability().unmodifyFlat(getId());
        
        // FIXED: Using standard API chain
        market.getStats().getDynamic().getMod("ground_defenses_mod").unmodifyPercent(getId());
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);

        // Only run logic if the industry is actually working (not disrupted, not building)
        if (isFunctional()) {
            CampaignClockAPI clock = Global.getSector().getClock();
            int currentDay = clock.getDay();

            // --- 1. HANDLE DECIVILIZED SUBPOPULATION ---
            if (market.hasCondition(Conditions.DECIVILIZED_SUBPOP) || market.hasCondition(Conditions.DECIVILIZED)) {
                
                // Only increment once per day
                if (currentDay != lastDayCheckedDeciv) {
                    daysPassedDeciv++;
                    lastDayCheckedDeciv = currentDay;

                    // Completion Check
                    if (daysPassedDeciv >= DAYS_TO_REMOVE_DECIV) {
                        performDecivRemoval();
                    }
                }
            } else {
                daysPassedDeciv = 0;
            }

            // --- 2. HANDLE ROGUE AI CORE ---
            if (market.hasCondition(Conditions.ROGUE_AI_CORE)) {
                
                if (currentDay != lastDayCheckedRogue) {
                    daysPassedRogue++;
                    lastDayCheckedRogue = currentDay;

                    // Completion Check
                    if (daysPassedRogue >= DAYS_TO_REMOVE_ROGUE_AI) {
                        performRogueRemoval();
                    }
                }
            } else {
                daysPassedRogue = 0;
            }
        }
    }

    // --- LOGIC: REMOVAL ACTIONS ---
    
    private void performDecivRemoval() {
        if (market.isPlayerOwned()) {
            MessageIntel intel = new MessageIntel("Pacification Protocols on " + market.getName(), Misc.getBasePlayerColor());
            intel.addLine("    - Decivilized elements neutralized.");
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
            intel.addLine("    - Rogue AI Core terminated.");
            intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundStandardUpdate());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO, market);
        }
        market.removeCondition(Conditions.ROGUE_AI_CORE);
        daysPassedRogue = 0;
    }

    // --- LOGIC: PATHER SUPPRESSION ---

    @Override
    public float getPatherInterest() {
        // If an Alpha Core is installed, we neutralize ALL Pather interest on the planet.
        if (isFunctional() && getAICoreId() != null && getAICoreId().equals(Commodities.ALPHA_CORE)) {
            
            float totalPatherInterest = 0f;

            // 1. Check Admin
            if (market.getAdmin().getAICoreId() != null) {
                totalPatherInterest += 10f; // Standard Pather hate for AI admins
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

            // Return negative of the total to cancel it out exactly
            return -totalPatherInterest;
        } 
        
        // Base interest if no Alpha Core is present
        return 10.0f; 
    }

    // --- TOOLTIPS & UI ---

    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();

        // 1. Display Static Bonuses
        tooltip.addPara("Stability bonus: %s", opad, highlight, "+" + STABILITY_BONUS);
        tooltip.addPara("Ground defense bonus: %s", opad, highlight, "+" + (int)(DEFENSE_BONUS * 100f) + "%");

        // 2. Status: Deciv Removal Progress
        if ((market.hasCondition(Conditions.DECIVILIZED_SUBPOP) || market.hasCondition(Conditions.DECIVILIZED)) 
                && mode == IndustryTooltipMode.NORMAL && !isBuilding()) {
            
            int percent = (int) (((float) daysPassedDeciv / (float) DAYS_TO_REMOVE_DECIV) * 100f);
            if (percent > 99) percent = 99; 

            tooltip.addPara("Pacification progress: %s", opad, highlight, percent + "%");
        }

        // 3. Status: Rogue AI Removal Progress
        if (market.hasCondition(Conditions.ROGUE_AI_CORE) && mode == IndustryTooltipMode.NORMAL && !isBuilding()) {
            
            int percent = (int) (((float) daysPassedRogue / (float) DAYS_TO_REMOVE_ROGUE_AI) * 100f);
            if (percent > 99) percent = 99;

            tooltip.addPara("Rogue AI purge progress: %s", opad, highlight, percent + "%");
        }
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI Core";
        
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha Core";
        }
        
        tooltip.addPara(pre + " assumes direct control of local security protocols. " +
                "All Luddic Path interest generated by this colony is %s.", 
                opad, highlight, "neutralized");
    }

    // --- ACCESS CONTROL: OPEN TO EVERYONE ---

    @Override
    public boolean isAvailableToBuild() {
        return true;
    }
    
    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public String getUnavailableReason() {
        return "";
    }
    
    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        daysPassedDeciv = 0;
        daysPassedRogue = 0;
        super.notifyBeingRemoved(mode, forUpgrade);
    }
}