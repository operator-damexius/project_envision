package data.scripts.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;

public class DysonHubPlugin extends BaseIndustry {

    @Override
    public void apply() {
        String id = getModId(); 

        // 1. BALANCED ACCESSIBILITY (+50%)
        // Reduced from 200% to 50%. This is still extremely strong (Tier 4 level),
        // but it won't break the global market economy instantly.
        market.getAccessibilityMod().modifyPercent(id, 50f, "Dyson Link Connectivity");

        // 2. MAX STABILITY (+10)
        // Kept exactly as requested.
        market.getStability().modifyFlat(id, 10f, "Dyson Oversight Algorithm");

        // 3. ALPHA CORE PROTOCOLS (The "Purge")
        if (getAICoreId() != null && getAICoreId().equals(Commodities.ALPHA_CORE)) {
            market.getStats().getDynamic().getMod("luddic_path_interest")
                  .modifyMult(id, 0f, "Dyson Security Firewall (Alpha Core)");

            if (market.hasCondition(Conditions.PATHER_CELLS)) {
                market.removeCondition(Conditions.PATHER_CELLS);
            }
        }

        // 4. BALANCED SUPPLY & DEMAND
        // A structure of this scale needs a massive workforce to operate.
        demand(Commodities.CREW, 1);
        demand(Commodities.HEAVY_MACHINERY, 1);
        
        // Output reduced slightly to be "Legendary" but not "Infinite Credits"
        supply(Commodities.FUEL, 30);
        supply(Commodities.VOLATILES, 20);

        
        super.apply(true);
    }

    @Override
    public void unapply() {
        String id = getModId();
        market.getAccessibilityMod().unmodify(id);
        market.getStability().unmodify(id);
        market.getStats().getDynamic().getMod("luddic_path_interest").unmodify(id);
        super.unapply();
    }
    
    // --- TOOLTIP DESCRIPTION ---
    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();
        
        String pre = "Alpha-level AI integration";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha Core";
        }
        
        tooltip.addPara(pre + " activates the 'Panopticon' security protocols, which %s.", opad, highlight, 
            "completely hides the colony from Luddic Path interest and purges any existing Sleeper Cells");
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
}