package bulletcarpet.mixins;

import net.minecraft.scoreboard.ScoreboardCriterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(ScoreboardCriterion.class)
public interface ScoreboardCriteriaMixin {

//    @Shadow(remap = false)
//    private static Map<String, ScoreboardCriterion> CRITERIA;

    @Accessor("CRITERIA")
    public static Map<String, ScoreboardCriterion> getCriteria() {
        throw new AssertionError();
    }
}
