package org.technobaboo.resource_extractor.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.technobaboo.resource_extractor.ModResourceExtractor;

public class ResourceExtractionScreen extends Screen {
    protected final Screen previousScreen;
    protected final int BUTTON_SPACING = 8;

    protected final ModResourceExtractor extractor;

    public ResourceExtractionScreen(Screen previousScreen, ModContainer modContainer) {
        super(Text.translatable("resource_extractor.title"));
        this.previousScreen = previousScreen;
        this.extractor = new ModResourceExtractor(modContainer);
    }

    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 150 - (BUTTON_SPACING/2), this.height - 24, 150, 20, Text.translatable("resource_extractor.screen.button.cancel"), button -> {
            this.onClose();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + (BUTTON_SPACING/2), this.height - 24, 150, 20, Text.translatable("resource_extractor.screen.button.extract"), button -> {
            extractor.extract();
        }));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215); // Render title

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void tick() {}

    public void onClose() {
        this.client.openScreen(previousScreen);
    }

    /**
     * A preliminary fix to Bedrockify, copied from https://github.com/ByMartrixx/VTDownloader/blob/master/src/main/java/io/github/bymartrixx/vtd/gui/VTDScreen.java
     */
    @Deprecated
    @Override
    public void renderBackgroundTexture(int vOffset) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.client.getTextureManager().bindTexture(OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0D, this.height, 0.0D).texture(0.0F, (float) this.height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0D).texture((float) this.width / 32.0F, (float) this.height / 32.0F + (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0D, 0.0D).texture((float) this.width / 32.0F, (float) vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0D, 0.0D, 0.0D).texture(0.0F, (float) vOffset).color(64, 64, 64, 255).next();
        tessellator.draw();
    }
}
