package net.firiz.renewatelier.version.tab;

public enum SkinProperty {
    GRAY(
            "ewogICJ0aW1lc3RhbXAiIDogMTU5MDc3NDEzMDk3MywKICAicHJvZmlsZUlkIiA6ICI4MmM2MDZjNWM2NTI0Yjc5OGI5MWExMmQzYTYxNjk3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3ROb3RvcmlvdXNOZW1vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhiYjliOWNmMDA3YTExN2UwZTNkMWZjMzIxZDNjNDJiNTViMDk0NDA2OGIzODQ2ZmVlNjUzM2Q3YTgxODFiYjQiCiAgICB9CiAgfQp9",
            "wgLrdD1GwajPaGboiPL3Zj4V88H6/HHYQbDSaDcbA4ehkrqf9DW/YYpTxLkI37Mz3Q141o9fMu0htW4EfpG5vtiomA5ohvNxi0TBkjHOlRkq42KZ0qxDjNza41ekeXErGizhsdG2pP5pWNX009+O+yfD9Vf79i3qNTjO4OyJHv+5thgBPbjdcV6VkNShQBFkDCFLMkif8B0g9HgLzakFT8GtEb3cXlHgNN0VVoM2arqZapc0L3i1WYILgSIaoHzCHsbnph7I6LPMJocqOOPjElUfAV1vJOWhITyqq2T0k8NwD/X+2U0MUbuax90LUiajws6vfWcoUlbKdtkJZ+g2VVowEJj3YS6mfzIFgBSd71EvNvO7zfqdKIxE9faoJK82mILJRNO2HxOdNziP57R+qey4okCxd8O7CpGQt0SEjsQcv3CcBAamGBm8ch3YOzMwrOgnWXQPZhA9iEdWDtQs6n/k3ymT8XcG3iF/HvfaE+DhTRg9/VWbCNPBvoFWKxbSKIRH8MTm912jHMIY8UHClPZuhiazC8A/o2m3jebkkwv/SaeHhjyGqSFJI+htDJ831NcFMRWqBrb9k/lu1U7E/i9yC0IaqKipWAQQBYcMBQMMxKUiBQ1Mp/4HNdgdOQAqQC8eItc3k7WYwrFRZm/VtFmMwlATDuFmkmU6+X+jkC0="
    ),
    LIGHT_GREEN(
            "ewogICJ0aW1lc3RhbXAiIDogMTU5MDc3NDI3NTA3NCwKICAicHJvZmlsZUlkIiA6ICJkNjBmMzQ3MzZhMTI0N2EyOWI4MmNjNzE1YjAwNDhkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJCSl9EYW5pZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGYzNDU0ZDYzYTc3YTQ4YjRlNzg1YmM0NjdjZmI4ZTRjMTY3YjUwZjFjMzEwNzIxOWJmNTFhYzQwMjg2ZjAyZCIKICAgIH0KICB9Cn0=",
            "ATEXSMxWmJA7QSZ6MLxmRuBUZ8YW1ibcv2hB6cgyNjW1yk/seDuVur3UuYysZeHdanX/tKPQorcwgih9xw2eCrIq9rev0+oZQnb5bnbC8s268aO5YgMiTpaltp9QHbOdeQ0X5EsVrHsteVvq5DZttuf9ONNJ5ftUkv6MaGoE2O3WaJBEEn7Nyb3nHDKLAAh4L7eUYRa11tZOqiDhto//+Z7/heyuD6F4L0KIr3kIX/9IdxTlLQKQScYlTSH4KwxiIvbHB/1kYqmdX69WIpX/wq39ZA+7kxMUyEB4Mq7f1eywd+9xWge5LHOu3Adj4dgeO77c64MZcBspFazvaL/R+dGsh8GeX4Cb8ZwXYFEpccrd0jUABU/2rrsWX0PJRUGXw6mriRL4SWCUpjT3rckJJ+Ic6ffDiY8w6Nl/kcOuI/zzU/Rzz2Lu8BIgZNPDg4O3Eix+CyMt8Aqc/ecCYo815B+SugyW6t7zJs0KdpKBU1qSJhRcRT2geFEWUTGwMKMjQBjstsMpBuwS2GhbEm8xCYI5g4NsGKQXdbXtx0DlESGLr0DbSdT/wr5xafCOwTyUH1Q3t6MFUYZizidr2aN7Vm4+H6QtfR2mybsWNrOr4t+uTLhYPdiijWpdyabHzMFMIkYS+2c1H3PDRccjTFR7fDCeOswDyF5K2sbCBZ2SUBI="
    );

    // 下記リンクのサイトでスキンテクスチャを元にtextureデータ(value)と署名(signature)を生成できる
    // https://mineskin.org

    private final String value;
    private final String signature;

    SkinProperty(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }
}
