package org.technobaboo.resource_extractor.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.ModMenuTexturedButtonWidget;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.technobaboo.resource_extractor.ResourceExtractorMod;
import org.technobaboo.resource_extractor.gui.ResourceExtractionScreen;

import java.io.File;
import java.util.Map;
import java.util.Objects;

@Mixin(ModsScreen.class)
public class ModsScreenMixin extends Screen {
	@Shadow private int paneY;
	@Shadow private ModListEntry selected;
	@Shadow @Final public Map<String, Screen> configScreenCache;

	private static final TranslatableText EXTRACT = new TranslatableText("resource_extractor.extract");
	private static final Identifier EXTRACT_BUTTON_LOCATION = new Identifier("resource_extractor", "textures/gui/extract_button.png");

	private static ButtonWidget extractButton;

	protected ModsScreenMixin(Text title) {
		super(title);
	}
	@Inject(at = @At("TAIL"), method = "init()V")
	private void init(CallbackInfo info) {
		extractButton = new ModMenuTexturedButtonWidget(width - 24, paneY, 20, 20, 0, 0, EXTRACT_BUTTON_LOCATION, 32, 64, button -> {
			final String modid = Objects.requireNonNull(selected).getMod().getId();
			client.openScreen(new ResourceExtractionScreen(this, FabricLoader.getInstance().getModContainer(modid).get()));
		},
				EXTRACT, (buttonWidget, matrices, mouseX, mouseY) -> {
			ModMenuTexturedButtonWidget button = (ModMenuTexturedButtonWidget) buttonWidget;
			if (button.isJustHovered()) {
				this.renderTooltip(matrices, EXTRACT, mouseX, mouseY);
			} else if (button.isFocusedButNotHovered()) {
				this.renderTooltip(matrices, EXTRACT, button.x, button.y);
			}
		});
		this.buttons.add(0, extractButton);
		this.children.add(0, extractButton);
	}
	@Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V")
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo callbackInfo) {
		String modid = selected.getMod().getId();
		boolean hasConfigure = !ModMenuConfig.HIDE_CONFIG_BUTTONS.getValue() && configScreenCache.get(modid) != null;
//		extractButton.active = hasConfigure;
//		extractButton.visible = hasConfigure;
		extractButton.x = width - (hasConfigure ? 46 : 24);
	}
}
