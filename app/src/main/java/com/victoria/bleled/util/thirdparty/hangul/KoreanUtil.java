package com.victoria.bleled.util.thirdparty.hangul;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by paulo on 6/28/2017.
 */

public class KoreanUtil {

    private static final int REVERSE = -1;
    private static final int LEFT_FIRST = -1;
    private static final int RIGHT_FIRST = 1;
    private static final char HANGUL_BEGIN_UNICODE = 44032; // 가
    private static final char HANGUL_LAST_UNICODE = 55203; // 힣
    public static final int HANGUL_END_UNICODE = 55203; // 힣
    private static final char HANGUL_BASE_UNIT = 588;//각자음 마다 가지는 글자수
    //자음
    private static final char[] INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    public static final int[] INITIAL_SOUND_UNICODE = {12593, 12594, 12596,
            12599, 12600, 12601, 12609, 12610, 12611, 12613, 12614, 12615,
            12616, 12617, 12618, 12619, 12620, 12621, 12622};

    // First '가' : 0xAC00(44032), 끝 '힟' : 0xD79F(55199)
    private static final int FIRST_HANGUL = 44032;

    // 19 initial consonants
    private static final char[] CHOSUNG_LIST = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    private static int JUNGSUNG_COUNT = 21;

    // 21 vowels
    private static final char[] JUNGSUNG_LIST = {
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ'
    };

    private static int JONGSUNG_COUNT = 28;

    // 28 consonants placed under a vowel(plus one empty character)
    private static final char[] JONGSUNG_LIST = {
            ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    /**
     * 문자 관련 유틸
     */

    public static boolean isEnglish(char ch) {
        return (ch >= (int) 'A' && ch <= (int) 'Z')
                || (ch >= (int) 'a' && ch <= (int) 'z');
    }

    public static boolean isKorean(char ch) {
        return ch >= Integer.parseInt("AC00", 16)
                && ch <= Integer.parseInt("D7A3", 16);
    }

    public static boolean isNumber(char ch) {
        return ch >= (int) '0' && ch <= (int) '9';
    }

    public static boolean isSpecial(char ch) {
        return (ch >= (int) '!' && ch <= (int) '/') // !"#$%&'()*+,-./
                || (ch >= (int) ':' && ch <= (int) '@') //:;<=>?@
                || (ch >= (int) '[' && ch <= (int) '`') //[\]^_`
                || (ch >= (int) '{' && ch <= (int) '~'); //{|}~
    }

    public static Comparator<String> getComparator() {
        return new Comparator<String>() {
            public int compare(String left, String right) {
                return compare(left, right);
            }
        };
    }

    /**
     * 한글 > 영어 > 숫자 > 특수문자 순서 비교 함수
     *
     * @param left
     * @param right
     * @return
     */
    public static int compare(String left, String right) {

        left = left.toUpperCase().replaceAll(" ", "");
        right = right.toUpperCase().replaceAll(" ", "");

        int leftLen = left.length();
        int rightLen = right.length();
        int minLen = Math.min(leftLen, rightLen);

        for (int i = 0; i < minLen; ++i) {
            char leftChar = left.charAt(i);
            char rightChar = right.charAt(i);

            if (leftChar != rightChar) {
                if (isKoreanAndEnglish(leftChar, rightChar)
                        || isKoreanAndNumber(leftChar, rightChar)
                        || isEnglishAndNumber(leftChar, rightChar)
                        || isKoreanAndSpecial(leftChar, rightChar)) {
                    return (leftChar - rightChar) * REVERSE;
                } else if (isEnglishAndSpecial(leftChar, rightChar)
                        || isNumberAndSpecial(leftChar, rightChar)) {
                    if (isEnglish(leftChar) || isNumber(leftChar)) {
                        return LEFT_FIRST;
                    } else {
                        return RIGHT_FIRST;
                    }
                } else {
                    return leftChar - rightChar;
                }
            }
        }

        return leftLen - rightLen;
    }

    private static boolean isKoreanAndEnglish(char ch1, char ch2) {
        return (isEnglish(ch1) && isKorean(ch2))
                || (isKorean(ch1) && isEnglish(ch2));
    }

    private static boolean isKoreanAndNumber(char ch1, char ch2) {
        return (isNumber(ch1) && isKorean(ch2))
                || (isKorean(ch1) && isNumber(ch2));
    }

    private static boolean isEnglishAndNumber(char ch1, char ch2) {
        return (isNumber(ch1) && isEnglish(ch2))
                || (isEnglish(ch1) && isNumber(ch2));
    }

    private static boolean isKoreanAndSpecial(char ch1, char ch2) {
        return (isKorean(ch1) && isSpecial(ch2))
                || (isSpecial(ch1) && isKorean(ch2));
    }

    private static boolean isEnglishAndSpecial(char ch1, char ch2) {
        return (isEnglish(ch1) && isSpecial(ch2))
                || (isSpecial(ch1) && isEnglish(ch2));
    }

    private static boolean isNumberAndSpecial(char ch1, char ch2) {
        return (isNumber(ch1) && isSpecial(ch2))
                || (isSpecial(ch1) && isNumber(ch2));
    }


    /**
     * 해당 문자가 INITIAL_SOUND인지 검사.
     *
     * @param searchar
     * @return
     */
    private static boolean isInitialSound(char searchar) {
        for (char c : INITIAL_SOUND) {
            if (c == searchar) {
                return true;
            }
        }
        return false;
    }

    /**
     * 해당 문자의 자음을 얻는다.
     *
     * @param c 검사할 문자
     * @return
     */
    private static char getInitialSound(char c) {
        int hanBegin = (c - HANGUL_BEGIN_UNICODE);
        int index = hanBegin / HANGUL_BASE_UNIT;
        return INITIAL_SOUND[index];
    }

    /**
     * 해당 문자가 한글인지 검사
     *
     * @param c 문자 하나
     * @return
     */
    private static boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
    }


    /**
     * 검색을 한다. 초성 검색 완벽 지원함.
     *
     * @param value  : 검색 대상 ex> 초성검색합니다
     * @param search : 검색어 ex> ㅅ검ㅅ합ㄴ
     * @return 매칭 되는거 찾으면 true 못찾으면 false.
     */
    public static boolean matchChoSongString(String value, String search) {
        int t = 0;
        int seof = value.length() - search.length();
        int slen = search.length();
        if (seof < 0)
            return false; //검색어가 더 길면 false를 리턴한다.
        for (int i = 0; i <= seof; i++) {
            t = 0;
            while (t < slen) {
                if (isInitialSound(search.charAt(t)) == true && isHangul(value.charAt(i + t))) {
                    //만약 현재 char이 초성이고 value가 한글이면
                    if (getInitialSound(value.charAt(i + t)) == search.charAt(t))
                        //각각의 초성끼리 같은지 비교한다
                        t++;
                    else
                        break;
                } else {
                    //char이 초성이 아니라면
                    if (value.charAt(i + t) == search.charAt(t))
                        //그냥 같은지 비교한다.
                        t++;
                    else
                        break;
                }
            }
            if (t == slen)
                return true; //모두 일치한 결과를 찾으면 true를 리턴한다.
        }
        return false; //일치하는 것을 찾지 못했으면 false를 리턴한다.
    }

    /*
     *
     * @param decimal
     *
     * @return
     */
    private static String toHexString(int decimal) {
        Long intDec = Long.valueOf(decimal);
        return Long.toHexString(intDec);
    }


    /**
     * 문자를 유니코드(10진수)로 변환 후 반환 한다.
     *
     * @param ch
     * @return
     */
    public static int convertCharToUnicode(char ch) {
        return (int) ch;
    }

    /**
     * 문자열을 유니코드(10진수)로 변환 후 반환 한다.
     *
     * @param str
     * @return
     */
    public static int[] convertStringToUnicode(String str) {

        int[] unicodeList = null;

        if (str != null) {
            unicodeList = new int[str.length()];
            for (int i = 0; i < str.length(); i++) {
                unicodeList[i] = convertCharToUnicode(str.charAt(i));
            }
        }

        return unicodeList;
    }

    /**
     * 유니코드(16진수)를 문자로 변환 후 반환 한다.
     *
     * @param hexUnicode
     * @return
     */
    public static char convertUnicodeToChar(String hexUnicode) {
        return (char) Integer.parseInt(hexUnicode, 16);
    }

    /**
     * 유니코드(10진수)를 문자로 변환 후 반환 한다.
     *
     * @param unicode
     * @return
     */
    public static char convertUnicodeToChar(int unicode) {
        return convertUnicodeToChar(toHexString(unicode));
    }

    /**
     * @param value
     * @return
     */
    public static String getHangulInitialSound(String value) {

        StringBuffer result = new StringBuffer();

        int[] unicodeList = convertStringToUnicode(value);
        for (int unicode : unicodeList) {

            if (HANGUL_BEGIN_UNICODE <= unicode
                    && unicode <= HANGUL_END_UNICODE) {
                int tmp = (unicode - HANGUL_BEGIN_UNICODE);
                int index = tmp / HANGUL_BASE_UNIT;
                result.append(INITIAL_SOUND[index]);
            } else {
                result.append(convertUnicodeToChar(unicode));

            }
        }

        return result.toString();
    }

    public static boolean[] getIsChoSungList(String name) {
        if (name == null) {
            return null;
        }

        boolean[] choList = new boolean[name.length()];

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            boolean isCho = false;
            for (char cho : INITIAL_SOUND) {
                if (c == cho) {
                    isCho = true;
                    break;
                }
            }

            choList[i] = isCho;

        }

        return choList;
    }

    public static String getHangulInitialSound(String value,
                                               String searchKeyword) {
        return getHangulInitialSound(value, getIsChoSungList(searchKeyword));
    }

    public static String getHangulInitialSound(String value, boolean[] isChoList) {

        StringBuffer result = new StringBuffer();

        int[] unicodeList = convertStringToUnicode(value);
        for (int idx = 0; idx < unicodeList.length; idx++) {
            int unicode = unicodeList[idx];

            if (isChoList != null && idx <= (isChoList.length - 1)) {
                if (isChoList[idx]) {
                    if (HANGUL_BEGIN_UNICODE <= unicode
                            && unicode <= HANGUL_END_UNICODE) {
                        int tmp = (unicode - HANGUL_BEGIN_UNICODE);
                        int index = tmp / HANGUL_BASE_UNIT;
                        result.append(INITIAL_SOUND[index]);
                    } else {
                        result.append(convertUnicodeToChar(unicode));
                    }
                } else {
                    result.append(convertUnicodeToChar(unicode));
                }
            } else {
                result.append(convertUnicodeToChar(unicode));
            }
        }

        return result.toString();
    }


    public static List<String> disassemble(char hangul) throws Exception {
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
            throw new Exception("음절이 아닌 자음입니다");
        } else if (hangulStr.matches(".*[ㅏ-ㅣ]+.*")) {
            throw new Exception("음절이 아닌 모음입니다");
        } else {
            throw new Exception("한글이 아닙니다");
        }

        return jasoList;
    }

    public static List<String> disassemble(String hangul) throws Exception {
        List<String> jasoList = new ArrayList<String>();

        for (int i = 0, li = hangul.length(); i < li; i++) {
            try {
                jasoList.addAll(disassemble(hangul.charAt(i)));
            } catch (Exception e) {
                throw new Exception((i + 1) + "번째 글자 분리 오류 : " + e.getMessage());
            }
        }

        return jasoList;
    }

    public static String assemble(List<String> jasoList) throws Exception {
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
            throw new Exception("자소가 없습니다");
        }
    }

    private static String assemble(List<String> jasoList, final int startIdx, final int assembleSize) throws Exception {
        int unicode = FIRST_HANGUL;

        final int chosungIndex = new String(CHOSUNG_LIST).indexOf(jasoList.get(startIdx));

        if (chosungIndex >= 0) {
            unicode += JONGSUNG_COUNT * JUNGSUNG_COUNT * chosungIndex;
        } else {
            throw new Exception((startIdx + 1) + "번째 자소가 한글 초성이 아닙니다");
        }

        final int jungsungIndex = new String(JUNGSUNG_LIST).indexOf(jasoList.get(startIdx + 1));

        if (jungsungIndex >= 0) {
            unicode += JONGSUNG_COUNT * jungsungIndex;
        } else {
            throw new Exception((startIdx + 2) + "번째 자소가 한글 중성이 아닙니다");
        }

        if (assembleSize > 2) {
            final int jongsungIndex = new String(JONGSUNG_LIST).indexOf(jasoList.get(startIdx + 2));

            if (jongsungIndex >= 0) {
                unicode += jongsungIndex;
            } else {
                throw new Exception((startIdx + 3) + "번째 자소가 한글 종성이 아닙니다");
            }
        }

        return Character.toString((char) unicode);
    }

    private static int getNextAssembleSize(List<String> jasoList, final int startIdx) throws Exception {
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
            throw new Exception("한글을 구성할 자소가 부족하거나 한글이 아닌 문자가 있습니다");
        }

        return assembleSize;
    }

    private static String arrayToSring(List<String> stringList) {
        if (stringList == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (String s : stringList) {
            builder.append(s);
        }
        String str = builder.toString();
        return str;
    }

    public static boolean containSearchWord(String name, String searchWord) {
        if (name == null || searchWord == null) {
            return false;
        }

        boolean isContain = true;
        for (int i = 0; i < name.length(); i++) {
            char chName = name.charAt(i);

            if (i >= searchWord.length()) {
                break;
            }

            char chSearch = searchWord.charAt(i);

            try {
                String strCh = "";
                if (isInitialSound(chName) == true) {
                    strCh = String.format("%c", chName);
                } else {
                    List<String> arrChName = disassemble(chName);
                    strCh = arrayToSring(arrChName);
                }

                String strSearch = "";
                if (isInitialSound(chSearch) == true) {
                    strSearch = String.format("%c", chSearch);
                } else {
                    List<String> arrChSearch = disassemble(chSearch);

                    strSearch = arrayToSring(arrChSearch);
                }

                if (strCh.contains(strSearch) == false) {
                    isContain = false;
                }
            } catch (Exception e) {
                isContain = false;
            }

            if (isContain == false) {
                break;
            }
        }

        return isContain;
    }
}


