package com.gmail.subnokoii78.util;

import com.gmail.subnokoii78.util.file.json.*;
import com.gmail.subnokoii78.util.file.json.values.*;
import com.gmail.subnokoii78.util.file.mojangson.MojangsonParser;
import com.gmail.subnokoii78.util.file.mojangson.values.*;

public class Main {
    public static void main(String[] args) {
        final JSONObject jsonObject = JSONParser.object(
            """
            {
                "header": {
                    "format_version": "1.16.100",
                    "description": "description da yoooon"
                },
                "modules": [
                    {
                        "type": "script",
                        "language": "JavaScript",
                        "entry": "scripts/main.js",
                        "id": "foo"
                    }
                ],
                "dependencies": [
                    {
                        "name": "@minecraft/server",
                        "version": "2.0.0-alpha"
                    }
                ],
                "data": {
                    "int_id": -184629134,
                    "rate": 0.462,
                    "public": true,
                    "key": "hoge-fuga-piyo-tarou-man",
                    "array": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, null]
                }
            }
            """
        );

        jsonObject.set("data.array[10]", 'a');

        // System.out.println(JSONSerializer.serialize(jsonObject));

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
