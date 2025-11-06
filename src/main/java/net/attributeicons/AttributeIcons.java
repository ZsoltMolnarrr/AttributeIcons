package net.attributeicons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.combat_roll.api.CombatRoll;
import net.critical_strike.api.CriticalStrikeAttributes;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.more_rpg_classes.entity.attribute.MRPGCEntityAttributes;
import net.spell_engine.api.entity.SpellEngineAttributes;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellResistance;
import net.spell_power.api.SpellSchools;
import net.tinyconfig.ConfigManager;
import net.witcher_rpg.entity.attribute.WitcherAttributes;

import javax.xml.stream.events.Characters;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/// HOW TO USE
/// 1) Delete generated sources
/// 2) Run Minecraft from IDE (run client), enter world
/// 3) Run datagen
/// 4) Copy printed AttributeIcons font injection json to your resource pack

public class AttributeIcons implements ModInitializer {

    public static final String ID = "attribute_icons";

    public static class Translations { public Translations() { }
        public LinkedHashMap<Identifier, String> translations = new LinkedHashMap<>();
    }
    public static class ParsableTranslations { public ParsableTranslations() { }
        public LinkedHashMap<String, String> translations = new LinkedHashMap<>();
        public Translations toTranslations() {
            var result = new Translations();
            translations.forEach((key, value) -> {
                try {
                    var id = Identifier.tryParse(key);
                    if (id != null) {
                        result.translations.put(id, value);
                    } else {
                        System.out.println("Invalid Identifier key in translation cache: " + key);
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing Identifier key in translation cache: " + key + " Error: " + e.getMessage());
                }
            });
            return result;
        }
    }

    public static ConfigManager<Translations> translation_cache = new ConfigManager<>
            ("translation_cache", new Translations())
            .builder()
            .setDirectory(ID)
            .sanitize(true)
            .build();

    public record Entry(Identifier attributeId, int code) {
        public String getTranslationKey() {
            var attribute =  Registries.ATTRIBUTE.get(attributeId);
            if (attribute != null) {
                return attribute.getTranslationKey();
            } else {
                var namespace = attributeId.getNamespace().equals("minecraft") ? "" : (attributeId.getNamespace() + ".");
                return "attribute.name." + namespace + attributeId.getPath();
            }
        }

        public String characterCode() {
            var slash = new StringBuilder().append('\\').toString().substring(0, 1);
            return slash + "uF" + code;
//            char c = 0x2202;//aka 8706 in decimal. u codepoints are in hex.
//            String s = String.valueOf(c);
//            return s  + "F" + code;
        }
    }
    public static ArrayList<Entry> entries = new ArrayList<>();
    public static final int START_CODE = 933;
    public static Entry add(Identifier id) {
        var entry = new Entry(id, START_CODE + entries.size());
        entries.add(entry);
        return entry;
    }

    static {
        add(Identifier.ofVanilla("generic.attack_range")); // Better Combat fake attribute
        add(EntityAttributes.GENERIC_ARMOR.getKey().get().getValue());
        add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS.getKey().get().getValue());
        add(EntityAttributes.GENERIC_ATTACK_DAMAGE.getKey().get().getValue());
        add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK.getKey().get().getValue());
        add(EntityAttributes.GENERIC_ATTACK_SPEED.getKey().get().getValue());
        add(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO.getKey().get().getValue());
        add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE.getKey().get().getValue());
        add(EntityAttributes.GENERIC_LUCK.getKey().get().getValue());
        add(EntityAttributes.GENERIC_MAX_HEALTH.getKey().get().getValue());
        add(EntityAttributes.GENERIC_MOVEMENT_SPEED.getKey().get().getValue());
        add(EntityAttributes.GENERIC_JUMP_STRENGTH.getKey().get().getValue());
        add(EntityAttributes.GENERIC_GRAVITY.getKey().get().getValue());
        add(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER.getKey().get().getValue());
        add(EntityAttributes.PLAYER_MINING_EFFICIENCY.getKey().get().getValue());
        add(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED.getKey().get().getValue());
        add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE.getKey().get().getValue());
        add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE.getKey().get().getValue());
        CombatRoll.Attributes.all.forEach(entry -> {
            add(entry.id);
        });
        CriticalStrikeAttributes.all.forEach(entry -> {
            add(entry.id);
        });
        EntityAttributes_RangedWeapon.all.forEach(entry -> {
            add(entry.id);
        });
        SpellPowerMechanics.all.values().forEach(entry -> {
            add(entry.id);
        });
        SpellResistance.Attributes.all.forEach(entry -> {
            add(entry.id);
        });
        SpellSchools.all().forEach(school -> {
            if (school.ownsAttribute() && school.getAttributeEntry() != null) {
                add(school.getAttributeEntry().getKey().get().getValue());
            }
        });
        SpellEngineAttributes.all.forEach(entry -> {
            add(entry.id);
        });

        /// MRPG Attributes - Fichte
        add(MRPGCEntityAttributes.AIR_FUSE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.ARCANE_FUSE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.DAMAGE_REFLECT_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.EARTH_FUSE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.FIRE_FUSE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.FROST_FUSE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.HEALING_FUSE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.LIFESTEAL_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.RAGE_MODIFIER.getKey().get().getValue());
        add(MRPGCEntityAttributes.SPELL_VAMPIRE.getKey().get().getValue());
        add(MRPGCEntityAttributes.WATER_FUSE_MODIFIER.getKey().get().getValue());
        add(WitcherAttributes.AARD_INTENSITY.getKey().get().getValue());
        add(WitcherAttributes.ADRENALINE_MODIFIER.getKey().get().getValue());
        add(WitcherAttributes.AXII_INTENSITY.getKey().get().getValue());
        add(WitcherAttributes.IGNI_INTENSITY.getKey().get().getValue());
        add(WitcherAttributes.SIGN_INTENSITY.getKey().get().getValue());
        add(WitcherAttributes.QUEN_INTENSITY.getKey().get().getValue());
        add(WitcherAttributes.YRDEN_INTENSITY.getKey().get().getValue());

    }

    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onInitialize() {
        // Hello registry, fire up injections
        Registries.ATTRIBUTE.getEntry(EntityAttributes.GENERIC_ARMOR.getKey().get());

        translation_cache.load();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            translation_cache.value.translations.clear();
            for (Entry entry : entries) {
                Identifier id = entry.attributeId;
                var key = entry.getTranslationKey();
                if (Language.getInstance().hasTranslation(key)) {
                    var translation = Language.getInstance().get(key);
                    translation_cache.value.translations.put(id, translation);
                } else {
                    System.out.println("Missing translation for attribute: " + id + " key: " + key);
                }
            }
            translation_cache.save();
        });
    }

    public static void printFontExtension() {
        var fontStuff = new FontContent();
        for (Entry entry : entries) {
            fontStuff.providers.add(FontContent.Entry.attributeIcon(entry.attributeId, entry.code));
        }
        var json = gson.toJson(fontStuff);
        System.out.println("AttributeIcons font injection: ");
        System.out.println(json);
    }

    public static void forceLoadConfig() {
        var pathString = "../../run/config/attribute_icons/translation_cache.json";

        Path path = Paths.get(pathString).normalize();

        try {
            if (!Files.exists(path)) {
                System.out.println("Translation cache force load skipped. File does not exist: " + path.toAbsolutePath());
                return;
            }

            try (Reader reader = Files.newBufferedReader(path, java.nio.charset.StandardCharsets.UTF_8)) {
                var loaded = gson.fromJson(reader, ParsableTranslations.class);
                if (loaded == null) {
                    System.out.println("Parsed Translation cache is null (empty or invalid JSON).");
                    return;
                }

                translation_cache.value = loaded.toTranslations();
                System.out.println("Translation cache force loaded from: " + path.toAbsolutePath());
            }
        } catch (com.google.gson.JsonParseException e) {
            System.out.println("Failed to parse JSON into Translation cache: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.out.println("I/O error while loading Translation cache: " + e.getMessage());
        }
    }
}
