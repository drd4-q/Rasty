package expensive.display.display.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import expensive.main.Expensive;
import expensive.events.EventDisplay;
import expensive.modules.api.Function;
import expensive.display.display.ElementRenderer;
import expensive.display.styles.Style;
import expensive.util.client.main.KeyStorage;
import expensive.util.client.draggings.Dragging;
import expensive.util.visual.main.color.ColorUtils;
import expensive.util.visual.main.display.DisplayUtils;
import expensive.util.visual.components.Scissor;
import expensive.util.visual.main.fonts.Fonts;
import expensive.util.visual.main.color.fonts.GradientUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.text.ITextComponent;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class KeyBindRenderer implements ElementRenderer {

    final Dragging dragging;


    float width;
    float height;

    @Override
    public void render(EventDisplay eventDisplay) {
        MatrixStack ms = eventDisplay.getMatrixStack();

        float posX = dragging.getX();
        float posY = dragging.getY();
        float fontSize = 6.5f;
        float padding = 5;

        ITextComponent name = GradientUtil.gradient("KeyBinds");

        Style style = Expensive.getInstance().getStyleManager().getCurrentStyle();

        DisplayUtils.drawShadow(posX, posY, width, height, 10, style.getFirstColor().getRGB(), style.getSecondColor().getRGB());
        drawStyledRect(posX, posY, width, height, 4);
        Scissor.push();
        Scissor.setFromComponentCoordinates(posX, posY, width, height);
        Fonts.sfui.drawCenteredText(ms, name, posX + width / 2, posY + padding + 0.5f, fontSize);

        posY += fontSize + padding * 2;

        float maxWidth = Fonts.sfMedium.getWidth(name, fontSize) + padding * 2;
        float localHeight = fontSize + padding * 2;

        DisplayUtils.drawRectHorizontalW(posX + 0.5f, posY, width - 1, 2.5f, 3, ColorUtils.rgba(0, 0, 0, (int) (255 * 0.25f)));
        posY += 3f;

        for (Function f : Expensive.getInstance().getFunctionRegistry().getFunctions()) {
            f.getAnimation().update();
            if (!(f.getAnimation().getValue() > 0) || f.getBind() == 0) continue;
            String nameText = f.getName();
            float nameWidth = Fonts.sfMedium.getWidth(nameText, fontSize);

            String bindText = "[" + KeyStorage.getKey(f.getBind()) + "]";
            float bindWidth = Fonts.sfMedium.getWidth(bindText, fontSize);

            float localWidth = nameWidth + bindWidth + padding * 3;

            Fonts.sfMedium.drawText(ms, nameText, posX + padding, posY + 0.5f, ColorUtils.rgba(210, 210, 210, (int) (255 * f.getAnimation().getValue())), fontSize);
            Fonts.sfMedium.drawText(ms, bindText, posX + width - padding - bindWidth, posY + 0.5f, ColorUtils.rgba(210, 210, 210, (int) (255 * f.getAnimation().getValue())), fontSize);

            if (localWidth > maxWidth) {
                maxWidth = localWidth;
            }

            posY += (float) ((fontSize + padding) * f.getAnimation().getValue());
            localHeight += (float) ((fontSize + padding) * f.getAnimation().getValue());
        }
        Scissor.unset();
        Scissor.pop();
        width = Math.max(maxWidth, 80);
        height = localHeight + 2.5f;
        dragging.setWidth(width);
        dragging.setHeight(height);
    }

    private void drawStyledRect(float x,
                                float y,
                                float width,
                                float height,
                                float radius) {

        DisplayUtils.drawRoundedRect(x - 0.5f, y - 0.5f, width + 1, height + 1, radius + 0.5f, ColorUtils.getColor(0)); // outline
        DisplayUtils.drawRoundedRect(x, y, width, height, radius, ColorUtils.rgba(21, 21, 21, 255));
    }
}
