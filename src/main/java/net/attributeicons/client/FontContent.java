package net.attributeicons.client;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class FontContent { public FontContent() { }
    public record Entry(String type, String file, int ascent, int height, List<String> chars) {
        public static Entry bitmap(String file, int ascent, int height, List<String> chars) {
            return new Entry("bitmap", file, ascent, height, chars);
        }

        public static Entry attributeIcon(Identifier attributeId, String codeString) {
            // var codeString = "\\" + "uF" + code;
            return bitmap(
                    attributeId.getNamespace() + ":attribute_icon/" + attributeId.getPath() + ".png",
                    8, 9,
                    List.of(codeString)
            );
        }
    }

    public List<Entry> providers = new ArrayList<>();
}
