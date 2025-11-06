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

public class FontProvider implements DataProvider {
    private final FabricDataOutput output;

    public FontProvider(FabricDataOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Generate font content
                var fontContent = new FontContent();
                for (AttributeIcons.Entry entry : AttributeIcons.entries) {
                    fontContent.providers.add(FontContent.Entry.attributeIcon(entry.attributeId(), entry.code()));
                }

                // Convert to JSON
                JsonElement json = AttributeIcons.gson.toJsonTree(fontContent);

                // Write to assets/minecraft/font/default.json
                Path path = this.output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "font")
                        .resolveJson(Identifier.ofVanilla("default"));

                DataProvider.writeToPath(writer, json, path);

                System.out.println("Font extension generated at: " + path);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate font provider", e);
            }
        });
    }

    @Override
    public String getName() {
        return "Attribute Icons Font Provider";
    }
}
