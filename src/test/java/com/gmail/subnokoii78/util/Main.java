package com.gmail.subnokoii78.util;

import com.gmail.subnokoii78.util.file.mojangson.MojangsonParser;
import com.gmail.subnokoii78.util.file.mojangson.values.MojangsonCompound;

public class Main {
    public static void main(String[] args) {
        final MojangsonCompound compound = MojangsonParser.compound(
            """
            {
                id: "minecraft:carrot_on_a_stick",
                count: 1,
                components: {
                    "minecraft:custom_name": '{"text": "NINNJINN BOU"}',
                    "minecraft:enchantment_glint_override": 1b,
                    "minecraft:custom_data": {
                        foo: bar,
                        bytes: [B; 0b, 1b, false, true, -10b, 127b],
                        ints: [I; 1, 2, 3, 4],
                        longs: [L; 1L, 2L, 3L, 4L],
                        compounds: [
                            {a: 1}, {b: 2}, {c: 3}
                        ],
                        strs: [hoge, fuga, piyo, banana]
                    }
                }
            }
            """
        );
    }
}
