package basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.regex.Pattern;

@SpirePatch(
		clz=AbstractCard.class,
		method="renderDescriptionCN"
)
public class RenderCustomDynamicVariableCN
{
	@SpireInsertPatch(
			locator=Locator.class,
			localvars={"tmp"}
	)
	public static void Insert(AbstractCard __instance, SpriteBatch sb, @ByRef String[] tmp)
	{
		if (tmp[0].startsWith("$") || tmp[0].equals("D")) {
			String key = tmp[0];

			Pattern pattern = Pattern.compile("\\$(.+)\\$\\$");
			java.util.regex.Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				key = matcher.group(1);
			}

			DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(key);
			if (dv != null) {
				if (dv.isModified(__instance)) {
					if (dv.value(__instance) >= dv.modifiedBaseValue(__instance)) {
						tmp[0] = "[#" + dv.getIncreasedValueColor() + "]" + dv.value(__instance) + "[]";
					} else {
						tmp[0] = "[#" + dv.getDecreasedValueColor() + "]" + dv.value(__instance) + "[]";
					}
				} else {
					Color textColor = ReflectionHacks.getPrivate(__instance, AbstractCard.class, "textColor");
					Color dvColor = dv.getNormalColor();
					float oldAlpha = dvColor.a;
					if (textColor != null) {
						dvColor.a = textColor.a;
					}
					tmp[0] = "[#" + dvColor + "]" + dv.modifiedBaseValue(__instance) + "[]";
					dvColor.a = oldAlpha;
				}
			}
		}
	}

	private static class Locator extends SpireInsertLocator
	{
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException
		{
			Matcher finalMatcher = new Matcher.MethodCallMatcher(String.class, "length");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
