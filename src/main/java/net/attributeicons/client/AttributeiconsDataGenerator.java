package net.attributeicons.client;

import com.google.gson.JsonObject;
import net.attributeicons.AttributeIcons;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AttributeiconsDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        AttributeIcons.forceLoadConfig();
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(FontGen::new);

        var scopedEntries = new HashMap<String, List<AttributeIcons.Entry>>();
        for (var entry : AttributeIcons.entries) {
            var namespace = entry.customNamespace() != null
                    ? entry.customNamespace()
                    : entry.attributeId().getNamespace();
            scopedEntries.computeIfAbsent(namespace, k -> new ArrayList<>()).add(entry);
        }

        for (var scope: scopedEntries.entrySet()) {
            pack.addProvider((dataOutput, registryLookup) -> {
                return new ScopedLangeGen(dataOutput, registryLookup, scope.getKey(), scope.getValue());
            });
        }

    }

    public static class ScopedLangeGen extends FabricLanguageProvider {
        private final String namespace;
        private List<AttributeIcons.Entry> entries;

        private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup2;
        private final String languageCode2 = "en_us";

        protected ScopedLangeGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup,
                                 String namespace, List<AttributeIcons.Entry> entries) {
            super(dataOutput, registryLookup);
            this.registryLookup2 = registryLookup;
            this.namespace = namespace;
            this.entries = entries;
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
            entries.forEach(entry -> {
                // Registries.ATTRIBUTE.get
                var id = entry.attributeId();
                var translation = AttributeIcons.translation_cache.value.translations.get(id);
                if (translation != null) {
                    var newTranslation = "§f" + entry.characterCode() + "§r " + translation;
                    translationBuilder.add(entry.getTranslationKey(), newTranslation);
                }
            });
        }

        @Override
        public CompletableFuture<?> run(DataWriter writer) {
            TreeMap<String, String> translationEntries = new TreeMap();
            return this.registryLookup2.thenCompose((lookup) -> {
                this.generateTranslations(lookup, (key, value) -> {
                    Objects.requireNonNull(key);
                    Objects.requireNonNull(value);
                    if (translationEntries.containsKey(key)) {
                        throw new RuntimeException("Existing translation key found - " + key + " - Duplicate will be ignored.");
                    } else {
                        translationEntries.put(key, value);
                    }
                });
                JsonObject langEntryJson = new JsonObject();
                Iterator var5 = translationEntries.entrySet().iterator();

                while(var5.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)var5.next();
                    langEntryJson.addProperty((String)entry.getKey(), (String)entry.getValue());
                }

                return DataProvider.writeToPath(writer, langEntryJson, this.getLangFilePath(this.languageCode2));
            });
        }

        private Path getLangFilePath(String code) {
            return this.dataOutput.getResolver(DataOutput.OutputType.RESOURCE_PACK, "lang").resolveJson(Identifier.of(this.namespace, code));
        }

        public String getName() {
            return "Language " + this.namespace + " (%s)".formatted(this.languageCode2);
        }

    }

    public static class FontGen extends FontProvider {
        protected FontGen(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generateFont(FontContent content) {
            AttributeIcons.entries.forEach(entry -> {
                content.providers.add(FontContent.Entry.attributeIcon(entry.attributeId(), entry.characterCode() ));
            });
        }
    }

//    public static class LangeGen extends FabricLanguageProvider {
//        protected LangeGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
//            super(dataOutput, registryLookup);
//        }
//
//        @Override
//        public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
//            AttributeIcons.entries.forEach(entry -> {
//                // Registries.ATTRIBUTE.get
//                var id = entry.attributeId();
//                var translation = AttributeIcons.translation_cache.value.translations.get(id);
//                if (translation != null) {
//                    var newTranslation = "§f" + entry.characterCode() + "§r " + translation;
//                    translationBuilder.add(entry.getTranslationKey(), newTranslation);
//                }
//            });
//        }
//    }
}
