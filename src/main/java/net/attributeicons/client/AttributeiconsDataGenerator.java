package net.attributeicons.client;

import net.attributeicons.AttributeIcons;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class AttributeiconsDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        AttributeIcons.forceLoadConfig();
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(FontProvider::new);
        pack.addProvider(LangeGen::new);
    }

    public static class LangeGen extends FabricLanguageProvider {
        protected LangeGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(dataOutput, registryLookup);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
            AttributeIcons.entries.forEach(entry -> {
                // Registries.ATTRIBUTE.get
                var id = entry.attributeId();
                var translation = AttributeIcons.translation_cache.value.translations.get(id);
                if (translation != null) {
                    var newTranslation = "§f" + entry.characterCode() + "§r " + translation;
                    translationBuilder.add(entry.getTranslationKey(), newTranslation);
                }
            });
        }
    }
}
