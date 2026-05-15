package cn.iocoder.yudao.module.business.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ZeroWidthBypasser {
    // 定义零宽字符池：零宽空格、零宽不连字符、零宽连字符、零宽非换行空格
    private static final String[] ZW_POOL = {"\u200B", "\u200C", "\u200D"};
    private static final Random RANDOM = new Random();

    // 丰富的小红书风格表情池
    private static final String[] EMOJI_POOL = {
        "🀄️", "👌", "💰", "🏠", "✨", "🎈", "🔥"
    };

    /**
     * 极致混淆函数
     * @param input 原始评论文本
     * @return 注入了随机零宽噪声的文本
     */
    public static String obfuscate(String input) {
        if (input == null || input.isEmpty()) return input;
        String suffix = getRandomEmojis(2);
        return input + suffix;
    }

    private static String getRandomEmojis(int count) {
        List<String> list = new ArrayList<>(List.of(EMOJI_POOL));
        Collections.shuffle(list);
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < count; i++) {
            res.append(list.get(i));
        }
        return res.toString();
    }
}
