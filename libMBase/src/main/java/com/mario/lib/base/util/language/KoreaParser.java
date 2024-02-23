package com.mario.lib.base.util.language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * HangulParser is to seperate Hangul to basic consonant and vowel by using Unicode
 *
 * @see KoreaParserException
 * <p>
 * ref : Hangul Syllables http://www.unicode.org/charts/PDF/UAC00.pdf
 */

public class KoreaParser {
    private static final String TAG = KoreaParser.class.getSimpleName();

    // First '가' : 0xAC00(44032), 끝 '힟' : 0xD79F(55199)
    private static final int FIRST_HANGUL = 44032;
    public static final char HANGUL_END_UNICODE = 55203; // 힣
    public static final char HANGUL_BASE_UNIT = 588;//각자음 마다 가지는 글자수
    private static int JUNGSUNG_COUNT = 21;
    private static int JONGSUNG_COUNT = 28;
    private static final String pattern_hangul = "^[가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$";

    // 19 initial consonants
    private static final char[] CHOSUNG_LIST = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    // 21 vowels
    private static final char[] JUNGSUNG_LIST = {
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ'
    };

    // 28 consonants placed under a vowel(plus one empty character)
    private static final char[] JONGSUNG_LIST = {
            ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    private static final Map<String, Integer> STROKE_MAP = new HashMap<String, Integer>() {
        {
            put(" ", 0);
            put("ㄱ", 2);
            put("ㄲ", 4);
            put("ㄴ", 2);
            put("ㄷ", 3);
            put("ㄸ", 6);
            put("ㄹ", 5);
            put("ㅁ", 4);
            put("ㅂ", 4);
            put("ㅃ", 8);
            put("ㅅ", 2);
            put("ㅆ", 4);
            put("ㅇ", 1);
            put("ㅈ", 3);
            put("ㅉ", 6);
            put("ㅊ", 4);
            put("ㅋ", 3);
            put("ㅌ", 4);
            put("ㅍ", 4);
            put("ㅎ", 3);
            put("ㅏ", 2);
            put("ㅐ", 3);
            put("ㅑ", 3);
            put("ㅒ", 4);
            put("ㅓ", 2);
            put("ㅔ", 3);
            put("ㅕ", 3);
            put("ㅖ", 4);
            put("ㅗ", 2);
            put("ㅘ", 4);
            put("ㅙ", 5);
            put("ㅚ", 3);
            put("ㅛ", 3);
            put("ㅜ", 2);
            put("ㅝ", 4);
            put("ㅞ", 5);
            put("ㅟ", 3);
            put("ㅠ", 3);
            put("ㅡ", 1);
            put("ㅢ", 2);
            put("ㅣ", 1);
            put("ㄳ", 4);
            put("ㄵ", 5);
            put("ㄶ", 5);
            put("ㄺ", 7);
            put("ㄻ", 9);
            put("ㄼ", 9);
            put("ㄽ", 7);
            put("ㄾ", 9);
            put("ㄿ", 9);
            put("ㅀ", 8);
            put("ㅄ", 6);
        }
    };

    public static List<String> disassemble(char hangul) throws KoreaParserException {
        List<String> jasoList = new ArrayList<>();

        String hangulStr = String.valueOf(hangul);

        if (hangulStr.matches(".*[가-힣]+.*")) {
            int baseCode = hangulStr.charAt(0) - FIRST_HANGUL;

            final int chosungIndex = baseCode / (JONGSUNG_COUNT * JUNGSUNG_COUNT);
            jasoList.add(Character.toString(CHOSUNG_LIST[chosungIndex]));

            final int jungsungIndex = (baseCode - ((JONGSUNG_COUNT * JUNGSUNG_COUNT) * chosungIndex)) / JONGSUNG_COUNT;
            jasoList.add(Character.toString(JUNGSUNG_LIST[jungsungIndex]));

            final int jongsungIndex = (baseCode - ((JONGSUNG_COUNT * JUNGSUNG_COUNT) * chosungIndex) - (JONGSUNG_COUNT * jungsungIndex));
            if (jongsungIndex > 0) {
                jasoList.add(Character.toString(JONGSUNG_LIST[jongsungIndex]));
            }
        } else if (hangulStr.matches(".*[ㄱ-ㅎ]+.*")) {
            throw new KoreaParserException("음절이 아닌 자음입니다");
        } else if (hangulStr.matches(".*[ㅏ-ㅣ]+.*")) {
            throw new KoreaParserException("음절이 아닌 모음입니다");
        } else {
            throw new KoreaParserException("한글이 아닙니다");
        }

        return jasoList;
    }

    public static List<String> disassemble(String hangul) throws KoreaParserException {
        List<String> jasoList = new ArrayList<String>();

        for (int i = 0, li = hangul.length(); i < li; i++) {
            try {
                jasoList.addAll(disassemble(hangul.charAt(i)));
            } catch (KoreaParserException e) {
                throw new KoreaParserException((i + 1) + "번째 글자 분리 오류 : " + e.getMessage());
            }
        }

        return jasoList;
    }

    public static String assemble(List<String> jasoList) throws KoreaParserException {
        if (jasoList.size() > 0) {
            String result = "";
            int startIdx = 0;

            while (true) {
                if (startIdx < jasoList.size()) {
                    final int assembleSize = getNextAssembleSize(jasoList, startIdx);
                    result += assemble(jasoList, startIdx, assembleSize);
                    startIdx += assembleSize;
                } else {
                    break;
                }
            }

            return result;
        } else {
            throw new KoreaParserException("자소가 없습니다");
        }
    }

    private static String assemble(List<String> jasoList, final int startIdx, final int assembleSize) throws KoreaParserException {
        int unicode = FIRST_HANGUL;

        final int chosungIndex = new String(CHOSUNG_LIST).indexOf(jasoList.get(startIdx));

        if (chosungIndex >= 0) {
            unicode += JONGSUNG_COUNT * JUNGSUNG_COUNT * chosungIndex;
        } else {
            throw new KoreaParserException((startIdx + 1) + "번째 자소가 한글 초성이 아닙니다");
        }

        final int jungsungIndex = new String(JUNGSUNG_LIST).indexOf(jasoList.get(startIdx + 1));

        if (jungsungIndex >= 0) {
            unicode += JONGSUNG_COUNT * jungsungIndex;
        } else {
            throw new KoreaParserException((startIdx + 2) + "번째 자소가 한글 중성이 아닙니다");
        }

        if (assembleSize > 2) {
            final int jongsungIndex = new String(JONGSUNG_LIST).indexOf(jasoList.get(startIdx + 2));

            if (jongsungIndex >= 0) {
                unicode += jongsungIndex;
            } else {
                throw new KoreaParserException((startIdx + 3) + "번째 자소가 한글 종성이 아닙니다");
            }
        }

        return Character.toString((char) unicode);
    }

    private static int getNextAssembleSize(List<String> jasoList, final int startIdx) throws KoreaParserException {
        final int remainJasoLength = jasoList.size() - startIdx;
        final int assembleSize;

        if (remainJasoLength > 3) {
            if (new String(JUNGSUNG_LIST).contains(jasoList.get(startIdx + 3))) {
                assembleSize = 2;
            } else {
                assembleSize = 3;
            }
        } else if (remainJasoLength == 3 || remainJasoLength == 2) {
            assembleSize = remainJasoLength;
        } else {
            throw new KoreaParserException("한글을 구성할 자소가 부족하거나 한글이 아닌 문자가 있습니다");
        }

        return assembleSize;
    }

    private static char getInitalSound(char hanChar) {
        int hanBegin = (hanChar - FIRST_HANGUL);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return CHOSUNG_LIST[index];
    }

    private static boolean isHangul(char unicode) {
        return FIRST_HANGUL <= unicode && unicode <= HANGUL_END_UNICODE;
    }

    private static boolean containInitialChar(char c) {
        return Arrays.binarySearch(CHOSUNG_LIST, c) != -1;
    }

    public static String getHangulInitialSound(String value, String search) {
        StringBuilder sb = new StringBuilder();
        int minLen = Math.min(value.length(), search.length());
        for (int i = 0; i < minLen; i++) {
            char ch = value.charAt(i);
            if (isHangul(ch) && containInitialChar(search.charAt(i))) {
                sb.append(getInitalSound(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static boolean isHangulInitialSound(String value, String sarch) {
        if (value == null || sarch == null) {
            if (value != null) return true;
            return sarch == value;
        }
        return sarch.equals(getHangulInitialSound(value, sarch));
    }

    private static int getStrokeCnt(String str) {
        try {
            List<String> arrChar = disassemble(str);
            int sum = 0;

            for (int i = 0; i < arrChar.size(); i++) {
                sum += STROKE_MAP.get(arrChar.get(i));
            }

            return sum;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getNameSum(String name1, String name2) {
        char[] arrName1 = name1.toCharArray();
        char[] arrName2 = name2.toCharArray();
        ArrayList<String> arrSumString = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            if (i >= arrName1.length) {
                arrSumString.add(" ");
            } else {
                arrSumString.add(String.valueOf(arrName1[i]));
            }

            if (i >= arrName2.length) {
                arrSumString.add(" ");
            } else {
                arrSumString.add(String.valueOf(arrName2[i]));
            }
        }

        ArrayList<Integer> arrSum = new ArrayList<>();
        for (int i = 0; i < arrSumString.size(); i++) {
            int cnt = getStrokeCnt(arrSumString.get(i));
            arrSum.add((cnt % 10));
        }

        ArrayList<Integer> arrLast = new ArrayList<>();
        do {
            arrLast.clear();
            for (int i = 0; i < (arrSum.size() - 1); i++) {
                int cnt = (arrSum.get(i) + arrSum.get(i + 1)) % 10;
                arrLast.add(cnt);
            }
            arrSum.clear();
            arrSum.addAll(arrLast);
        } while (arrLast.size() > 2);

        int sum = 0;
        if (arrLast.size() >= 2) {
            sum = arrLast.get(0) * 10 + arrLast.get(1);
        }
        if (sum < 10) {
            sum = 100 - sum;
        }

        return sum;
    }


    public final static boolean isOnlyHangul(String str) {
        Pattern pattern = null;
        pattern = Pattern.compile(pattern_hangul);

        boolean isOnly = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (pattern.matcher(Character.toString(c)).matches() == false) {
                isOnly = false;
                break;
            }
        }

        return isOnly;
    }
}