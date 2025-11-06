package net.attributeicons.client;

import com.google.gson.JsonElement;
import net.attributeicons.AttributeIcons;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class FontProvider implements DataProvider {
    private final FabricDataOutput output;

    public FontProvider(FabricDataOutput output) {
        this.output = output;
    }

    public void generateFont(FontContent content) {
        // To be implemented by subclasses
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        try {
            System.out.println("FontProvider: Starting generation with " + AttributeIcons.entries.size() + " entries");

            // Generate font content
            var fontContent = new FontContent();
            this.generateFont(fontContent);

            // Convert to JSON
            JsonElement json = AttributeIcons.gson.toJsonTree(fontContent);

            // Write to assets/minecraft/font/default.json
            Path path = this.output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "font")
                    .resolveJson(Identifier.ofVanilla("default"));

            System.out.println("FontProvider: Writing to path: " + path);

            return DataProvider.writeToPath(writer, json, path).thenRun(() -> {
                System.out.println("FontProvider: Font extension successfully generated!");
            });
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(new RuntimeException("Failed to generate font provider", e));
        }
    }

    @Override
    public String getName() {
        return "Attribute Icons Font Provider";
    }
}
