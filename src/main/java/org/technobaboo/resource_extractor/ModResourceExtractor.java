package org.technobaboo.resource_extractor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

public class ModResourceExtractor {
    protected ModContainer mod;
    protected ModMetadata metadata;

    public ModResourceExtractor(String modid) {
        this(FabricLoader.getInstance().getModContainer(modid).get());
    }
    public ModResourceExtractor(ModContainer mod) {
        this.mod = mod;
    }

    public void extract() {
        metadata = mod.getMetadata();
        try {
            Path assetsPath = mod.getPath("assets");
            String resourcePackName = mod.getMetadata().getName() + " Resources";
            Path resourcePackPath = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
            Path targetPath = resourcePackPath.resolve(resourcePackName);
            Files.createDirectories(targetPath);
            CopyRecursive(assetsPath, targetPath);

            Optional<String> iconPathString = mod.getMetadata().getIconPath(128);
            if(iconPathString.isPresent()) {
                Path iconPath = mod.getPath(iconPathString.get());
                Files.copy(iconPath, targetPath.resolve("pack.png"));
            }

            int packFormat = SharedConstants.getGameVersion().getPackVersion();
            JsonObject rootObj = new JsonObject();
            JsonObject packObj = new JsonObject();
            packObj.addProperty("description", mod.getMetadata().getDescription());
            packObj.addProperty("pack_format", packFormat);
            rootObj.add("pack", packObj);
            String packObject = new GsonBuilder().setPrettyPrinting().create().toJson(rootObj);
            Path metaFilePath = Files.createFile(targetPath.resolve("pack.mcmeta"));
            Files.write(metaFilePath, packObject.getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static protected void CopyRecursive(Path source, Path destination) throws IOException {
        CopyRecursive(source, destination, StandardCopyOption.COPY_ATTRIBUTES);
    }
    static protected void CopyRecursive(Path source, Path destination, CopyOption copyOptions) throws IOException {
        if(Files.isDirectory(source)) {
            Files.walk(source)
                    .forEach(sourcePath -> {
                        try {
                            Path destinationPath = destination.resolve(source.getFileName().toString());
                            Path targetPath = destinationPath.resolve(source.relativize(sourcePath).toString());
                            Files.copy(sourcePath, targetPath, copyOptions);
                        } catch (IOException ex) {
                            System.out.format("I/O error: %s%n", ex);
                        }
                    });
        } else if(Files.exists(source)) {
            Path destinationPath = destination.resolve(source.getFileName().toString());
            Files.copy(source, destination, copyOptions);
        } else {
            throw new FileNotFoundException(source.toAbsolutePath().toString());
        }
    }
}
