package com.mario.lib.base.util

import android.content.Context
import android.os.Handler
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.DecimalFormat
import java.util.Locale
import java.util.StringTokenizer
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.min

/**
 * Created by YongTrim on 16. 5. 7. for rbooker_ad
 */
object StringUtil {
    const val EMPTY = "" //$NON-NLS-1$
    const val NULL = "null" //$NON-NLS-1$
    val WORD_SEPARATORS = charArrayOf('_', '-', '@', '$', '#', ' ')
    const val INDEX_NOT_FOUND = -1
    fun encode(input: String?): String? {
        var input = input
        try {
            input = URLEncoder.encode(input, "UTF-8")
        } catch (ignore: UnsupportedEncodingException) {
        }
        return input
    }

    fun decode(input: String?): String? {
        var input = input
        if (isEmpty(input)) {
            return ""
        }
        try {
            input = URLDecoder.decode(input, "UTF-8")
        } catch (ignore: UnsupportedEncodingException) {
        }
        return input
    }

    /**
     *
     *
     * 문자(char)가 단어 구분자('_', '-', '@', '$', '#', ' ')인지 판단한다.
     *
     *
     * @param c 문자(char)
     * @return 단어 구분자이면 true, 아니면 false를 반환한다.
     */
    fun isWordSeparator(c: Char): Boolean {
        for (i in WORD_SEPARATORS.indices) {
            if (WORD_SEPARATORS[i] == c) {
                return true
            }
        }
        return false
    }

    /**
     *
     *
     * 문자(char)가 단어 구분자('_', '-', '@', '$', '#', ' ')인지 판단한다.
     *
     *
     * @param c 문자(char)
     * @return 단어 구분자이면 true, 아니면 false를 반환한다.
     */
    fun isWordSeparator(c: Char, wordSeparators: CharArray?): Boolean {
        if (wordSeparators == null) {
            return false
        }
        for (i in wordSeparators.indices) {
            if (wordSeparators[i] == c) {
                return true
            }
        }
        return false
    }

    /**
     *
     *
     * 문자열(String)을 카멜표기법으로 표현한다.
     *
     *
     *
     * <pre>
     * StringUtil.camelString("ITEM_CODE", true)  = "ItemCode"
     * StringUtil.camelString("ITEM_CODE", false) = "itemCode"
    </pre> *
     *
     * @param str                     문자열
     * @param firstCharacterUppercase 첫문자열을 대문자로 할지 여부
     * @return 카멜표기법으로 표현환 문자열
     */
    fun camelString(str: String?, firstCharacterUppercase: Boolean): String? {
        if (str == null) {
            return null
        }
        val sb = StringBuffer()
        var nextUpperCase = false
        for (i in 0 until str.length) {
            val c = str[i]
            if (isWordSeparator(c)) {
                if (sb.length > 0) {
                    nextUpperCase = true
                }
            } else {
                if (nextUpperCase) {
                    sb.append(c.uppercaseChar())
                    nextUpperCase = false
                } else {
                    sb.append(c.lowercaseChar())
                }
            }
        }
        if (firstCharacterUppercase) {
            sb.setCharAt(0, sb[0].uppercaseChar())
        }
        return sb.toString()
    }

    /**
     *
     *
     * 입력 받은 문자를 반복숫자만큼 붙여서 만든다.
     *
     *
     *
     * <pre>
     * StringUtil.repeat(null, *)   = null
     * StringUtil.repeat("", -1)    = ""
     * StringUtil.repeat("", 2)     = ""
     * StringUtil.repeat("han", -1) = ""
     * StringUtil.repeat("han", 0)  = ""
     * StringUtil.repeat("han", 2)  = "hanhan"
    </pre> *
     *
     * @param str
     * @param repeat 반복숫자
     * @return
     */
    fun repeat(str: String?, repeat: Int): String? {
        if (str == null) {
            return null
        }
        if (repeat < 1) {
            return EMPTY
        }
        val inputLen = str.length
        if (inputLen == 0 || repeat == 1) {
            return str
        }
        val outputLen = inputLen * repeat
        return if (inputLen == 1) {
            val ch = str[0]
            val output = CharArray(outputLen)
            for (i in 0 until outputLen) {
                output[i] = ch
            }
            String(output)
        } else {
            val output = StringBuilder(
                min(
                    (outputLen * 110L / 100).toInt(),
                    Int.MAX_VALUE
                ) as Int
            )
            for (i in 0 until repeat) {
                output.append(str)
            }
            output.toString()
        }
    }
    // ----------------------------------------------------------------------
    // 공백/여백문자 검사, 제거, 치환
    // ----------------------------------------------------------------------
    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나 `null`인 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.isBlank(null)    = true
     * StringUtil.isBlank("")      = true
     * StringUtil.isBlank("   ")   = true
     * StringUtil.isBlank("han")   = false
     * StringUtil.isBlank(" han ") = false
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    fun isBlank(str: String?): Boolean {
        if (str == null) {
            return true
        }
        val strLen = str.length
        if (strLen > 0) {
            for (i in 0 until strLen) {
                if (Character.isWhitespace(str[i]) == false) {
                    return false
                }
            }
        }
        return true
    }

    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이 아니거나 `null`이 아닌지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.isNotBlank(null)    = false
     * StringUtil.isNotBlank("")      = false
     * StringUtil.isNotBlank("   ")   = false
     * StringUtil.isNotBlank("han")   = true
     * StringUtil.isNotBlank(" han ") = true
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    fun isNotBlank(str: String?): Boolean {
        return !isBlank(str)
    }

    /**
     *
     *
     * 문자열(String)이 공백("")이거나 `null`인 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.isEmpty(null)    = true
     * StringUtil.isEmpty("")      = true
     * StringUtil.isEmpty("   ")   = false
     * StringUtil.isEmpty("han")   = false
     * StringUtil.isEmpty(" han ") = false
    </pre> *
     *
     * @param str 검사할 문자열
     * @return
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.length == 0
    }

    /**
     *
     *
     * 문자열(String)이 공백("")이 아니거나 `null`이 아닌지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.isNotEmpty(null)    = false
     * StringUtil.isNotEmpty("")      = false
     * StringUtil.isNotEmpty("   ")   = true
     * StringUtil.isNotEmpty("han")   = true
     * StringUtil.isNotEmpty(" han ") = true
    </pre> *
     *
     * @param str 검사할 문자열
     * @return
     */
    fun isNotEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    /**
     *
     *
     * 문자열이 숫자로만 구성되어 있는지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.isNumber(null) = false;
     * StringUtil.isNumber(&quot;&quot;) = false;
     * StringUtil.isNumber(&quot;1234&quot;) = true;
     * StringUtil.isNumber(&quot;abc123&quot;) = false;
    </pre> *
     *
     * @param str 검사할 문자열
     * @return
     */
    fun isNumber(str: String): Boolean {
        try {
            str.toInt()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     *
     *
     * 문자열이 이메일인지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.isValidEmail(null)          = false;
     * StringUtil.isValidEmail("abc.abc")     = false;
     * StringUtil.isValidEmail("abc@abc.com") = true;
     *
     * @param inputStr
     * @return
    </pre> */
    fun isValidEmail(inputStr: String?): Boolean {
        var rtn = false
        if (inputStr == null) {
            return rtn
        }
        val regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"
        val p = Pattern.compile(regex)
        val m = p.matcher(inputStr)
        if (m.matches()) {
            rtn = true
        }
        return rtn
    }

    fun isValidPhoneNumber(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.PHONE.matcher(target).matches()
        }
    }

    /**
     *
     *
     * 문자열 중에서 문자형을 제외한 숫자형만 반환합니다.
     *
     *
     *
     * <pre>
     * StringUtil.removeString(null) = 0;
     * StringUtil.removeString(&quot;&quot;) = 0;
     * StringUtil.removeString(&quot;1234&quot;) = 1234;
     * StringUtil.removeString(&quot;abc123&quot;) = 123;
    </pre> *
     *
     * @param str 변환할 문자열
     * @return
     */
    fun removeString(str: String): String {
        var str = str
        str = defaultIfBlank(str, "0")
        if (isNumber(str)) return str
        val c = str.toCharArray()
        val sb = StringBuffer()
        for (i in c.indices) {
            if (isNumber(c[i].toString())) {
                sb.append(c[i])
            }
        }
        return sb.toString()
    }

    /**
     * <pre>
     * 하나의 문자열을 치환할 경우 사용
    </pre> *
     *
     *
     * <pre>
     * StringUtil.format(&quot;{0} 테스트&quot;, &quot;변환&quot;) = &quot;변환 테스트&quot;
    </pre> *
     *
     * @param str
     * @param obj
     * @return
     */
    fun format(str: String?, obj: String?): String? {
        return if (str == null || obj == null) str else replace(str, "{0}", obj)
    }

    /**
     * <pre>
     * 여러문자열을 치환할 경우 사용
    </pre> *
     *
     *
     * <pre>
     * StringUtil.format("{0} 테스트 {1}", {"변환", "입니다"}) = "변환 테스트 입니다"
    </pre> *
     *
     * @param str
     * @param obj
     * @return
     */
    fun format(str: String?, obj: Array<String?>?): String? {
        var str = str
        if (str == null || obj == null) return str
        for (i in obj.indices) str = replace(str, "{$i}", obj[i])
        return str
    }

    /**
     * <pre>
     * 여러 문자열을 치환할 경우 사용
    </pre> *
     *
     *
     * <pre>
     * StringUtil.format(&quot;{0} 테스트 {1}&quot;, &quot;변환&quot;, &quot;입니다&quot;) = &quot;변환 테스트 입니다&quot;
    </pre> *
     *
     * @param str
     * @param obj
     * @return
     */
    fun format(str: String?, vararg obj: Any): String? {
        var str = str
        if (str == null || obj == null) return str
        for (i in obj.indices) str = replace(str, "{$i}", obj[i].toString())
        return str
    }

    /**
     * 문자열을 세자리마다 콤마를 입력하여 반환한다.
     *
     * @param number
     * @return
     */
    fun formattedNumber(number: String?): String {
        if (number == null) return "0"
        var fn = "0"
        if (isNumber(number)) {
            val df = DecimalFormat("#,##0")
            fn = df.format(number.toDouble())
        }
        return fn
    }

    /**
     *
     *
     * 문자열을 숫자형으로 변경하여 반환합니다.
     *
     *
     *
     * <pre>
     * StringUtil.toNumber(null) = 0;
     * StringUtil.toNumber(&quot;&quot;) = 0;
     * StringUtil.toNumber(&quot;1234&quot;) = 1234;
     * StringUtil.toNumber(&quot;abc123&quot;) = 0;
    </pre> *
     *
     * @param str 변환할 문자열
     * @return
     */
    fun toNumber(str: String): Int {
        var str = str
        str = defaultIfBlank(str, "0")
        return if (isNumber(str)) str.toInt() else 0
    }

    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한다.
     *
     *
     *
     * <pre>
     * StringUtil.trim(null)    = null
     * StringUtil.trim("")      = ""
     * StringUtil.trim("   ")   = ""
     * StringUtil.trim("han")   = "han"
     * StringUtil.trim(" han ") = "han"
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    fun trim(str: String?): String? {
        return str?.trim { it <= ' ' }
    }

    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한 후 공백("")이거나 `null`이면 `null`을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.trimToNull(null)    = null
     * StringUtil.trimToNull("")      = null
     * StringUtil.trimToNull("   ")   = null
     * StringUtil.trimToNull("han")   = "han"
     * StringUtil.trimToNull(" han ") = "han"
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    fun trimToNull(str: String?): String? {
        return if (isBlank(str)) null else trim(str)
    }

    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한 후 공백("")이거나 `null`이면 공백("")을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.trimToEmpty(null)    = ""
     * StringUtil.trimToEmpty("")      = ""
     * StringUtil.trimToEmpty("   ")   = ""
     * StringUtil.trimToEmpty("han")   = "han"
     * StringUtil.trimToEmpty(" han ") = "han"
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    fun trimToEmpty(str: String?): String {
        return if (isBlank(str)) EMPTY else trim(str)!!
    }
    /**
     *
     *
     * 문자열(String)이 `null`이면 기본문자열을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.defaultIfNull(null, "")    = ""
     * StringUtil.defaultIfNull("", "")      = ""
     * StringUtil.defaultIfNull("   ", "")   = "   "
     * StringUtil.defaultIfNull("han", "")   = "han"
     * StringUtil.defaultIfNull(" han ", "") = " han "
    </pre> *
     *
     * @param str        문자열
     * @param defaultStr 기본문자열
     * @return
     */
    /**
     *
     *
     * 문자열(String)이 `null`이면 공백문자열을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.defaultIfNull(null)    = ""
     * StringUtil.defaultIfNull("")      = ""
     * StringUtil.defaultIfNull("   ")   = "   "
     * StringUtil.defaultIfNull("han")   = "han"
     * StringUtil.defaultIfNull(" han ") = " han "
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    @JvmOverloads
    fun defaultIfNull(str: String?, defaultStr: String = EMPTY): String {
        return str ?: defaultStr
    }
    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나 `null`이면, 기본문자열을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.defaultIfBlank(null, "")    = ""
     * StringUtil.defaultIfBlank("", "")      = ""
     * StringUtil.defaultIfBlank("   ", "")   = ""
     * StringUtil.defaultIfBlank("han", "")   = "han"
     * StringUtil.defaultIfBlank(" han ", "") = " han "
    </pre> *
     *
     * @param str        문자열
     * @param defaultStr 기본문자열
     * @return
     */
    /**
     *
     *
     * 문자열(String)의 좌우 여백문자(white space)를 제거한후, 공백("")이거나 `null`이면, 공백문자열을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.defaultIfBlank(null)    = ""
     * StringUtil.defaultIfBlank("")      = ""
     * StringUtil.defaultIfBlank("   ")   = ""
     * StringUtil.defaultIfBlank("han")   = "han"
     * StringUtil.defaultIfBlank(" han ") = " han "
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    @JvmOverloads
    fun defaultIfBlank(str: String?, defaultStr: String = EMPTY): String {
        return if (isBlank(str)) defaultStr else str!!
    }
    // ----------------------------------------------------------------------
    // 문자열 비교
    // ----------------------------------------------------------------------
    /**
     *
     *
     * 두 문자열(String)이 일치하면 `true`을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.equals(null, null)   = true
     * StringUtil.equals(null, "")     = false
     * StringUtil.equals("", null)     = false
     * StringUtil.equals(null, "han")  = false
     * StringUtil.equals("han", null)  = false
     * StringUtil.equals("han", "han") = true
     * StringUtil.equals("han", "HAN") = false
    </pre> *
     *
     * @param str1 첫번째 문자열
     * @param str2 두번째 문자열
     * @return 문자열(String)이 일치하면 `true`
     * @see String.equals
     */
    fun equals(str1: String?, str2: String?): Boolean {
        return if (str1 == null) str2 == null else str1 == str2
    }

    /**
     *
     *
     * 대소문자를 무시한, 두 문자열(String)이 일치하면 `true`을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, "")     = false
     * StringUtil.equalsIgnoreCase("", null)     = false
     * StringUtil.equalsIgnoreCase(null, "han")  = false
     * StringUtil.equalsIgnoreCase("han", null)  = false
     * StringUtil.equalsIgnoreCase("han", "han") = true
     * StringUtil.equalsIgnoreCase("han", "HAN") = true
    </pre> *
     *
     * @param str1 첫번째 문자열
     * @param str2 두번째 문자열
     * @return 대소문자를 무시한 문자열(String)이 일치하면 `true`
     * @see String.equalsIgnoreCase
     */
    fun equalsIgnoreCase(str1: String?, str2: String?): Boolean {
        return str1?.equals(str2, ignoreCase = true) ?: (str2 == null)
    }

    /**
     *
     *
     * 문자열이 접두사로 시작하는지를 판단한다.
     *
     *
     *
     * <pre>
     * StringUtil.startsWith(null, *)    = false
     * StringUtil.startsWith(*, null)    = false
     * StringUtil.startsWith("han", "h") = true
     * StringUtil.startsWith("han", "a") = false
    </pre> *
     *
     * @param str    문자열
     * @param prefix 접두사
     * @return
     */
    fun startsWith(str: String?, prefix: String?): Boolean {
        return if (str == null || prefix == null) {
            false
        } else str.startsWith(prefix)
    }

    /**
     *
     *
     * 문자열 offset 위치부터 접두사로 시작하는지를 판단한다.
     *
     *
     *
     * <pre>
     * StringUtil.startsWith(null, *, 0)    = false
     * StringUtil.startsWith(*, null, 0)    = false
     * StringUtil.startsWith("han", "h", 0) = true
     * StringUtil.startsWith("han", "a", 0) = false
     * StringUtil.startsWith("han", "a", 1) = true
    </pre> *
     *
     * @param str    문자열
     * @param prefix 접두사
     * @param offset 비교 시작 위치
     * @return
     */
    fun startsWith(str: String?, prefix: String?, offset: Int): Boolean {
        return if (str == null || prefix == null) {
            false
        } else str.startsWith(prefix, offset)
    }

    /**
     *
     *
     * 문자열이 접미사로 끝나는지를 판단한다.
     *
     *
     *
     * <pre>
     * StringUtil.endsWith(null, *)    = false
     * StringUtil.endsWith(*, null)    = false
     * StringUtil.endsWith("han", "h") = false
     * StringUtil.endsWith("han", "n") = true
    </pre> *
     *
     * @param str    문자열
     * @param suffix 접두사
     * @return
     */
    fun endsWith(str: String?, suffix: String?): Boolean {
        return if (str == null || suffix == null) {
            false
        } else str.endsWith(suffix)
    }

    /**
     *
     *
     * 문자열(String)에 검색문자열(String)이 몇번 포함되어 있는지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.containCount(&quot;haaaan&quot;, &quot;a&quot;) = 4
    </pre> *
     *
     * @param str       문자열
     * @param searchStr 검색문자열
     * @return 문자열(String)에 검색 문자열이 포함되어 있을때 `count`, 문자열(String)에 검색 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 `null`일때 `0`
     * @see String.indexOf
     */
    fun containCount(str: String?, searchStr: String?): Int {
        var str = str
        var i = 0
        var idx = 0
        if (str == null || searchStr == null) {
            return 0
        }
        while (true) {
            if (str!!.indexOf(searchStr).also { idx = it } > INDEX_NOT_FOUND) {
                str = substring(str, idx + searchStr.length, str.length)
                i++
            } else {
                break
            }
        }
        return i
    }

    /**
     *
     *
     * 문자열(String)에 검색문자열(String)이 포함되어 있는지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.contains(null, *)    = false
     * StringUtil.contains(*, null)    = false
     * StringUtil.contains("han", "")  = true
     * StringUtil.contains("han", "h") = true
     * StringUtil.contains("han", "H") = false
    </pre> *
     *
     * @param str       문자열
     * @param searchStr 검색문자열
     * @return 문자열(String)에 검색 문자열이 포함되어 있을때 `true`, 문자열(String)에 검색 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 `null`일때 `false`
     * @see String.indexOf
     */
    fun contains(str: String?, searchStr: String?): Boolean {
        return if (str == null || searchStr == null) {
            false
        } else str.indexOf(searchStr) > INDEX_NOT_FOUND
    }

    /**
     *
     *
     * 문자열(String) 배열에 검색문자열(String)이 포함되어 있는지 검사한다.
     *
     *
     * @param str       array 문자열 배열
     * @param searchStr 검색문자열
     * @return 문자열(String)에 검색 문자열이 포함되어 있을때 `true`, 문자열(String)에 검색 문자열이 포함되어 있지 않을때나, 문자열 또는 검색문자열이 `null`일때 `false`
     * @see String.indexOf
     */
    fun contains(str: Array<String?>?, searchStr: String?): Boolean {
        var `val` = false
        if (str == null || searchStr == null) {
            return `val`
        }
        for (s in str) {
            if (equals(s, searchStr)) {
                `val` = true
                break
            }
        }
        return `val`
    }

    /**
     *
     *
     * 문자열(String) List에 검색문자열(String)이 포함되어 있는지 검사한다.
     *
     *
     *
     * <pre>
     * StringUtil.contains("han", {"a", "b"}) = true
     * StringUtil.contains("han", {"aa", "bb"}) = false
    </pre> *
     *
     * @param str      문자열
     * @param keywords 검색할 문자열 목록
     * @return
     */
    fun contains(str: String?, keywords: List<String?>?): Boolean {
        if (str == null || keywords == null) {
            return false
        }
        for (key in keywords) {
            if (contains(str, key)) {
                return true
            }
        }
        return false
    }
    // ----------------------------------------------------------------------
    // 대/소문자 변환
    // ----------------------------------------------------------------------
    /**
     *
     *
     * 문자열(String)을 대문자로 변환한다.
     *
     *
     *
     * <pre>
     * StringUtil.toUpperCase(null)  = null
     * StringUtil.toUpperCase("han") = "HAN"
     * StringUtil.toUpperCase("hAn") = "HAN"
    </pre> *
     *
     * @param str 문자열
     * @return 대문자로 변환한 문자열
     */
    fun toUpperCase(str: String?): String? {
        return str?.uppercase(Locale.getDefault())
    }

    /**
     *
     *
     * 시작 인덱스부터 종료 인덱스까지 대문자로 변환한다.
     *
     *
     *
     * <pre>
     * StringUtil.toUpperCase(null, *, *)  = null
     * StringUtil.toUpperCase("han", 0, 1) = "Han"
     * StringUtil.toUpperCase("han", 0, 2) = "HAn"
     * StringUtil.toUpperCase("han", 0, 3) = "HAN"
    </pre> *
     *
     * @param str
     * @param beginIndex
     * @param endIndex
     * @return
     */
    fun toUpperCase(str: String?, beginIndex: Int, endIndex: Int): String? {
        var beginIndex = beginIndex
        var endIndex = endIndex
        if (str == null) {
            return null
        }
        val sb = StringBuilder()
        if (beginIndex < 0) {
            beginIndex = 0
        }
        if (endIndex > str.length) {
            endIndex = str.length
        }
        if (beginIndex > 0) {
            sb.append(str.substring(0, beginIndex))
        }
        sb.append(str.substring(beginIndex, endIndex).uppercase(Locale.getDefault()))
        if (endIndex < str.length) {
            sb.append(str.substring(endIndex))
        }
        return sb.toString()
    }

    /**
     *
     *
     * 문자열(String)을 소문자로 변환한다.
     *
     *
     *
     * <pre>
     * StringUtil.toLowerCase(null)  = null
     * StringUtil.toLowerCase("han") = "han"
     * StringUtil.toLowerCase("hAn") = "han"
    </pre> *
     *
     * @param str 문자열
     * @return 소문자로 변환한 문자열
     */
    fun toLowerCase(str: String?): String? {
        return str?.lowercase(Locale.getDefault())
    }

    /**
     *
     *
     * 시작 인덱스부터 종료 인덱스까지 소문자로 변환한다.
     *
     *
     *
     * <pre>
     * StringUtil.toLowerCase(null, *, *)  = null
     * StringUtil.toLowerCase("HAN", 0, 1) = "hAN"
     * StringUtil.toLowerCase("HAN", 0, 2) = "haN"
     * StringUtil.toLowerCase("HAN", 0, 3) = "han"
    </pre> *
     *
     * @param str
     * @param beginIndex
     * @param endIndex
     * @return
     */
    fun toLowerCase(str: String?, beginIndex: Int, endIndex: Int): String? {
        var beginIndex = beginIndex
        var endIndex = endIndex
        if (str == null) {
            return null
        }
        val sb = StringBuilder()
        if (beginIndex < 0) {
            beginIndex = 0
        }
        if (endIndex > str.length) {
            endIndex = str.length
        }
        if (beginIndex > 0) {
            sb.append(str.substring(0, beginIndex))
        }
        sb.append(str.substring(beginIndex, endIndex).lowercase(Locale.getDefault()))
        if (endIndex < str.length) {
            sb.append(str.substring(endIndex))
        }
        return sb.toString()
    }

    /**
     *
     *
     * 대문자는 소문자로 변환하고 소문자는 대문자로 변환한다.
     *
     *
     *
     * <pre>
     * StringUtil.swapCase(null)  = null
     * StringUtil.swapCase("Han") = "hAN"
     * StringUtil.swapCase("hAn") = "HaN"
    </pre> *
     *
     * @param str 문자열
     * @return
     */
    fun swapCase(str: String?): String? {
        if (str == null) {
            return null
        }
        val charArray = str.toCharArray()
        for (i in charArray.indices) {
            if (Character.isLowerCase(charArray[i])) {
                charArray[i] = charArray[i].uppercaseChar()
            } else {
                charArray[i] = charArray[i].lowercaseChar()
            }
        }
        return String(charArray)
    }

    /**
     * 문자열(String)의 첫번째 문자를 대문자로 변환한다.
     *
     *
     * <pre>
     * StringUtil.capitalize(null)  = null
     * StringUtil.capitalize("Han") = "Han"
     * StringUtil.capitalize("han") = "Han"
    </pre> *
     *
     * @param str 문자열
     * @return 첫번째 문자를 대문자로 변환한 문자열
     */
    fun capitalize(str: String?): String? {
        if (str == null) {
            return null
        }
        val charArray = str.toCharArray()
        if (charArray.size > 0) {
            charArray[0] = charArray[0].uppercaseChar()
        }
        return String(charArray)
    }

    /**
     * 문자열(String)의 첫번째 문자를 소문자로 변환한다.
     *
     *
     * <pre>
     * StringUtil.uncapitalize(null)  = null
     * StringUtil.uncapitalize("han") = "han"
     * StringUtil.uncapitalize("HAN") = "hAN"
    </pre> *
     *
     * @param str 문자열
     * @return 첫번째 문자를 대문자로 변환한 문자열
     */
    fun uncapitalize(str: String?): String? {
        if (str == null) {
            return null
        }
        val charArray = str.toCharArray()
        if (charArray.size > 0) {
            charArray[0] = charArray[0].lowercaseChar()
        }
        return String(charArray)
    }
    // ----------------------------------------------------------------------
    // 문자열 배열 결합/분리
    // ----------------------------------------------------------------------
    /**
     *
     *
     * 문자열 배열을 하나의 문자열로 결합시킨다.
     *
     *
     *
     * 배열의 문자열 중에 `null`과 공백("")은 무시한다.
     *
     *
     *
     * <pre>
     * StringUtil.compose(null, *)               = ""
     * StringUtil.compose(["h", "a", "n"], ".")  = "h.a.n"
     * StringUtil.compose([null, "a", "n"], ".") = "a.n"
     * StringUtil.compose(["", "a", "n"], ".")   = "a.n"
     * StringUtil.compose(["h", "", "n"], ".")   = "h.n"
     * StringUtil.compose(["  ", "a", "n"], ".") = "  .a.n"
    </pre> *
     *
     * @param strArray  문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    fun compose(strArray: Array<String?>?, separator: Char): String {
        val sb = StringBuilder()
        if (strArray != null) {
            for (i in strArray.indices) {
                if (isEmpty(strArray[i])) {
                    sb.append(EMPTY)
                } else {
                    if (sb.length > 0) {
                        sb.append(separator)
                    }
                    sb.append(strArray[i])
                }
            }
        }
        return sb.toString()
    }

    /**
     *
     *
     * 문자열 배열을 하나의 문자열로 결합시킨다.
     *
     *
     *
     * 배열의 문자열 중에 `null`과 공백("")은 무시한다.
     *
     *
     *
     * <pre>
     * StringUtil.compose(null, *)               = ""
     * StringUtil.compose(["h", "a", "n"], ".")  = "h.a.n"
     * StringUtil.compose([null, "a", "n"], ".") = "a.n"
     * StringUtil.compose(["", "a", "n"], ".")   = "a.n"
     * StringUtil.compose(["h", "", "n"], ".")   = "h.n"
     * StringUtil.compose(["  ", "a", "n"], ".") = "  .a.n"
    </pre> *
     *
     * @param strArray  문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    fun compose(strArray: Array<String?>?, separator: String?): String {
        val sb = StringBuilder()
        if (strArray != null) {
            for (i in strArray.indices) {
                if (isEmpty(strArray[i])) {
                    sb.append(EMPTY)
                } else {
                    if (sb.length > 0) {
                        sb.append(separator)
                    }
                    sb.append(strArray[i])
                }
            }
        }
        return sb.toString()
    }

    /**
     *
     *
     * 문자열 배열을 하나의 문자열로 결합시킨다.
     *
     *
     *
     * 배열의 문자열 중에 `null`과 공백("")도 포함한다.
     *
     *
     *
     * <pre>
     * StringUtil.join(null, *)               = ""
     * StringUtil.join(["h", "a", "n"], '-')  = "h-a-n"
     * StringUtil.join([null, "a", "n"], '-') = "-a-n"
     * StringUtil.join(["", "a", "n"], '-')   = "-a-n"
     * StringUtil.join(["h", "", "n"], '-')   = "h--n"
     * StringUtil.join(["  ", "a", "n"], '-') = "  -a-n"
    </pre> *
     *
     * @param strArray  문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    fun join(strArray: Array<String?>?, separator: Char): String {
        val sb = StringBuilder()
        if (strArray != null) {
            var isFirst = true
            for (i in strArray.indices) {
                if (isFirst) {
                    isFirst = false
                } else {
                    sb.append(separator)
                }
                if (isEmpty(strArray[i])) {
                    sb.append(EMPTY)
                } else {
                    sb.append(strArray[i])
                }
            }
        }
        return sb.toString()
    }

    /**
     *
     *
     * 문자열 배열을 하나의 문자열로 결합시킨다.
     *
     *
     *
     * 배열의 문자열 중에 `null`과 공백("")도 포함한다.
     *
     *
     *
     * <pre>
     * StringUtil.join(null, *)               = ""
     * StringUtil.join(["h", "a", "n"], "-")  = "h-a-n"
     * StringUtil.join([null, "a", "n"], "-") = "-a-n"
     * StringUtil.join(["", "a", "n"], "-")   = "-a-n"
     * StringUtil.join(["h", "", "n"], "-")   = "h--n"
     * StringUtil.join(["  ", "a", "n"], "-") = "  -a-n"
    </pre> *
     *
     * @param strArray  문자열 배열
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    fun join(strArray: Array<String?>?, separator: String?): String {
        val sb = StringBuilder()
        if (strArray != null) {
            var isFirst = true
            for (i in strArray.indices) {
                if (isFirst) {
                    isFirst = false
                } else {
                    sb.append(separator)
                }
                if (isEmpty(strArray[i])) {
                    sb.append(EMPTY)
                } else {
                    sb.append(strArray[i])
                }
            }
        }
        return sb.toString()
    }

    /**
     *
     *
     * 문자열 목록을 하나의 문자열로 결합시킨다.
     *
     *
     *
     * 목록의 문자열 중에 `null`과 공백("")도 포함한다.
     *
     *
     *
     * <pre>
     * StringUtil.join(null, *)               = ""
     * StringUtil.join(["h", "a", "n"], "-")  = "h-a-n"
     * StringUtil.join([null, "a", "n"], "-") = "-a-n"
     * StringUtil.join(["", "a", "n"], "-")   = "-a-n"
     * StringUtil.join(["h", "", "n"], "-")   = "h--n"
     * StringUtil.join(["  ", "a", "n"], "-") = "  -a-n"
    </pre> *
     *
     * @param strList   문자열 목록
     * @param separator 구분자
     * @return 구분자로 결합한 문자열
     */
    fun join(strList: List<String?>?, separator: String?): String {
        val sb = StringBuilder()
        if (strList != null && !strList.isEmpty()) {
            var isFirst = true
            for (str in strList) {
                if (isFirst) {
                    isFirst = false
                } else {
                    sb.append(separator)
                }
                if (isEmpty(str)) {
                    sb.append(EMPTY)
                } else {
                    sb.append(str)
                }
            }
        }
        return sb.toString()
    }

    fun join(strList: Set<String?>?, separator: String?): String {
        val sb = StringBuilder()
        if (strList != null && !strList.isEmpty()) {
            var isFirst = true
            for (str in strList) {
                if (isFirst) {
                    isFirst = false
                } else {
                    sb.append(separator)
                }
                if (isEmpty(str)) {
                    sb.append(EMPTY)
                } else {
                    sb.append(str)
                }
            }
        }
        return sb.toString()
    }

    /**
     * 문자열에서 해당 문자가 있을 경우 제외한 문자열을 반환합니다.
     *
     * @param str
     * @param searchStr
     * @return
     */
    fun remove(str: String?, searchStr: String?): String? {
        return if (isBlank(str) || searchStr == null) null else replace(
            str,
            searchStr,
            ""
        )
    }

    /**
     * @param strArray  문자열 배열
     * @param searchStr 찾는 문자
     * @return 찾는 문자를 제거한 문자열 배열
     */
    fun remove(strArray: Array<String?>, searchStr: String?): Array<String?> {
        if (!contains(strArray, searchStr)) return strArray
        var idx = 0
        val newStr = arrayOfNulls<String>(strArray.size - 1)
        for (i in strArray.indices) {
            if (!equals(strArray[i], searchStr)) {
                newStr[idx] = strArray[i]
                idx++
            }
        }
        return newStr
    }

    /**
     *
     *
     * 문자열을 구분자로 나누어서, 문자열 배열로 만든다.
     *
     *
     *
     * 배열의 문자열 중에 `null`과 공백("")도 포함한다.
     *
     *
     *
     * <pre>
     * StringUtil.split("h-a-n", '-') = ["h", "a", "n"]
     * StringUtil.split("h--n", '-')  = ["h", "", "n"]
     * StringUtil.split(null, *)      = null
    </pre> *
     *
     * @param str       문자열
     * @param separator 구분자
     * @return 구분자로 나누어진 문자열 배열
     */
    fun split(str: String?, separator: Char): Array<String?>? {
        return split(str, String(charArrayOf(separator)))
    }

    /**
     *
     *
     * 문자열을 구분자로 나누어서, 문자열 배열로 만든다.
     *
     *
     *
     * 배열의 문자열 중에 `null`과 공백("")도 포함한다.
     *
     *
     *
     * <pre>
     * StringUtil.split("h-a-n", "-") = ["h", "a", "n"]
     * StringUtil.split("h--n", "-")  = ["h", "", "n"]
     * StringUtil.split(null, *)      = null
    </pre> *
     *
     * @param str       문자열
     * @param separator 구분자
     * @return 구분자로 나누어진 문자열 배열
     */
    fun split(str: String?, separator: String?): Array<String?>? {
        if (str == null) {
            return null
        }
        val result: Array<String?>
        var i = 0 // index into the next empty array element

        // --- Declare and create a StringTokenizer
        val st = StringTokenizer(str, separator)
        // --- Create an array which will hold all the tokens.
        result = arrayOfNulls(st.countTokens())

        // --- Loop, getting each of the tokens
        while (st.hasMoreTokens()) {
            result[i++] = st.nextToken()
        }
        return result
    }
    // ----------------------------------------------------------------------
    // 문자열 자르기
    // ----------------------------------------------------------------------
    /**
     *
     *
     * 문자열(String)을 해당 길이(`length`) 만큼, 왼쪽부터 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.left(null, *)    = null
     * StringUtil.left(*, -length) = ""
     * StringUtil.left("", *)      = *
     * StringUtil.left("han", 0)   = ""
     * StringUtil.left("han", 1)   = "h"
     * StringUtil.left("han", 11)  = "han"
    </pre> *
     *
     * @param str    문자열
     * @param length 길이
     * @return
     */
    fun left(str: String?, length: Int): String? {
        if (str == null) {
            return null
        }
        if (length < 0) {
            return EMPTY
        }
        return if (str.length <= length) {
            str
        } else str.substring(0, length)
    }

    /**
     *
     *
     * 문자열(String)을 해당 길이(`length`) 만큼, 오른쪽부터 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.right(null, *)    = null
     * StringUtil.right(*, -length) = ""
     * StringUtil.right("", *)      = *
     * StringUtil.right("han", 0)   = ""
     * StringUtil.right("han", 1)   = "n"
     * StringUtil.right("han", 11)  = "han"
    </pre> *
     *
     * @param str    문자열
     * @param length 길이
     * @return
     */
    fun right(str: String?, length: Int): String? {
        if (str == null) {
            return null
        }
        if (length < 0) {
            return EMPTY
        }
        return if (str.length <= length) {
            str
        } else str.substring(str.length - length)
    }

    /**
     *
     *
     * 문자열(String)을 시작 위치(`beginIndex`)부터 길이( `length`) 만큼 자른다.
     *
     *
     *
     *
     *
     * 시작 위치(`beginIndex`)가 음수일 경우는 0으로 자동 변환된다.
     *
     *
     *
     * <pre>
     * StringUtil.mid(null, *, *)    = null
     * StringUtil.mid(*, *, -length) = ""
     * StringUtil.mid("han", 0, 1)   = "h"
     * StringUtil.mid("han", 0, 11)  = "han"
     * StringUtil.mid("han", 2, 3)   = "n"
     * StringUtil.mid("han", -2, 3)  = "han"
    </pre> *
     *
     * @param str        문자열
     * @param beginIndex 위치(음수일 경우는 0으로 자동 변환된다.)
     * @param length     길이
     * @return
     */
    fun mid(str: String?, beginIndex: Int, length: Int): String? {
        var beginIndex = beginIndex
        if (str == null) {
            return null
        }
        if (length < 0 || beginIndex > str.length) {
            return EMPTY
        }
        if (beginIndex < 0) {
            beginIndex = 0
        }
        return if (str.length <= beginIndex + length) {
            str.substring(beginIndex)
        } else str.substring(
            beginIndex,
            beginIndex + length
        )
    }

    /**
     *
     *
     * 시작 인덱스부터 문자열을 자는다.
     *
     *
     *
     * 시작 인덱스가 0보다 작거나, 문자열의 총길이보다 크면 공백("")을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.substring(null, *)    = null
     * StringUtil.substring("", *)      = ""
     * StringUtil.substring("han", 1)   = "an"
     * StringUtil.substring("han", 615) = ""
     * StringUtil.substring("han", -1)  = ""
    </pre> *
     *
     * @param str
     * @param beginIndex 시작 인덱스(0부터 시작)
     * @return
     */
    fun substring(str: String?, beginIndex: Int): String? {
        if (str == null) {
            return null
        }
        if (beginIndex < 0) {
            return EMPTY
        }
        return if (beginIndex > str.length) {
            EMPTY
        } else str.substring(beginIndex)
    }

    /**
     *
     *
     * 시작 인덱스부터 끝 인덱스까지 문자열을 자는다.
     *
     *
     *
     * 시작 인덱스또는 끝 인덱스가 0보다 작으면 공백("")을 반환한다.
     *
     *
     *
     * <pre>
     * StringUtil.substring(null, *, *)    = null
     * StringUtil.substring("", *, *)      = ""
     * StringUtil.substring("han", 1, 2)   = "a"
     * StringUtil.substring("han", 1, 3)   = "an"
     * StringUtil.substring("han", 1, 615) = "an"
     * StringUtil.substring("han", -1, *)  = ""
     * StringUtil.substring("han", *, -1)  = ""
    </pre> *
     *
     * @param str
     * @param beginIndex
     * @param endIndex
     * @return
     */
    fun substring(str: String?, beginIndex: Int, endIndex: Int): String? {
        var endIndex = endIndex
        if (str == null) {
            return null
        }
        if (beginIndex < 0 || endIndex < 0) {
            return EMPTY
        }
        if (endIndex > str.length) {
            endIndex = str.length
        }
        return if (beginIndex > endIndex || beginIndex > str.length) {
            EMPTY
        } else str.substring(beginIndex, endIndex)
    }

    /**
     *
     *
     * 처음 발견한 구분자의 위치까지 문자열을 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.substringBefore(null, *)       = null
     * StringUtil.substringBefore("", *)         = ""
     * StringUtil.substringBefore("han", null)   = "han"
     * StringUtil.substringBefore("han", "")     = ""
     * StringUtil.substringBefore("hanhan", "a") = "h"
     * StringUtil.substringBefore("hanhan", "g") = "hanhan"
    </pre> *
     *
     * @param str       문자열
     * @param separator 구분자
     * @return
     */
    fun substringBefore(str: String, separator: String?): String {
        if (isEmpty(str) || separator == null) {
            return str
        }
        if (separator.length == 0) {
            return EMPTY
        }
        val endIndex = str.indexOf(separator)
        return if (endIndex == INDEX_NOT_FOUND) {
            str
        } else str.substring(0, endIndex)
    }

    /**
     *
     *
     * 마지막으로 발견한 구분자의 위치까지 문자열을 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.substringBeforeLast(null, *)       = null
     * StringUtil.substringBeforeLast("", *)         = ""
     * StringUtil.substringBeforeLast("han", null)   = "han"
     * StringUtil.substringBeforeLast("han", "")     = "han"
     * StringUtil.substringBeforeLast("hanhan", "a") = "hanh"
     * StringUtil.substringBeforeLast("hanhan", "g") = "hanhan"
    </pre> *
     *
     * @param str       문자열
     * @param separator 구분자
     * @return
     */
    fun substringBeforeLast(str: String, separator: String?): String {
        if (isEmpty(str) || isEmpty(separator)) {
            return str
        }
        val endIndex = str.lastIndexOf(separator!!)
        return if (endIndex == INDEX_NOT_FOUND) {
            str
        } else str.substring(0, endIndex)
    }

    /**
     *
     *
     * 처음 발견한 구분자의 위치 다음부터 문자열을 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.substringAfter(null, *)       = null
     * StringUtil.substringAfter("", *)         = ""
     * StringUtil.substringAfter("han", null)   = ""
     * StringUtil.substringAfter("han", "")     = "han"
     * StringUtil.substringAfter("hanhan", "a") = "nhan"
     * StringUtil.substringAfter("hanhan", "g") = ""
    </pre> *
     *
     * @param str       문자열
     * @param separator 구분자
     * @return
     */
    fun substringAfter(str: String, separator: String?): String {
        if (isEmpty(str)) {
            return str
        }
        if (separator == null) {
            return EMPTY
        }
        var beginIndex = str.indexOf(separator)
        if (beginIndex == INDEX_NOT_FOUND) {
            return EMPTY
        }
        beginIndex = beginIndex + separator.length
        return if (beginIndex == str.length) {
            EMPTY
        } else str.substring(beginIndex)
    }

    /**
     *
     *
     * 마지막으로 발견한 구분자의 위치 다음부터 문자열을 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.substringAfterLast(null, *)       = null
     * StringUtil.substringAfterLast("", *)         = ""
     * StringUtil.substringAfterLast("han", null)   = ""
     * StringUtil.substringAfterLast("han",     "") = ""
     * StringUtil.substringAfterLast("hanhan", "a") = "n"
     * StringUtil.substringAfterLast("hanhan", "g") = ""
    </pre> *
     *
     * @param str       문자열
     * @param separator 구분자
     * @return
     */
    fun substringAfterLast(str: String, separator: String): String {
        if (isEmpty(str)) {
            return str
        }
        if (isEmpty(separator)) {
            return EMPTY
        }
        var beginIndex = str.lastIndexOf(separator)
        if (beginIndex == INDEX_NOT_FOUND) {
            return EMPTY
        }
        beginIndex = beginIndex + separator.length
        return if (beginIndex == str.length) {
            EMPTY
        } else str.substring(beginIndex)
    }

    /**
     *
     *
     * 시작 문자부터 끝 문자열까지 자른다.
     *
     *
     *
     * <pre>
     * StringUtil.substringBetween(null, *, *)       = null
     * StringUtil.substringBetween(*, null, *)       = null
     * StringUtil.substringBetween(*, *, null)       = null
     * StringUtil.substringBetween("h<a>n", "<", ">") = "a"
     * StringUtil.substringBetween("h<a>**n", "<", ">") = "a"
     **</a></a></pre> *
     *
     * @param str 문자열
     * @return
     * @since 1.1
     */
    fun substringBetween(str: String?, open: String?, close: String?): String? {
        if (str == null || open == null || close == null) {
            return null
        }
        val start = str.indexOf(open)
        if (start != INDEX_NOT_FOUND) {
            val end = str.indexOf(close, start + open.length)
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length, end)
            } else {
                // 끝이 없으면 null이 좋을까, 끝까지가 좋을까...
            }
        }
        return null
    }

    /**
     *
     *
     * 입력한 문자열을 한줄의 최대 길이만큼, 여러 줄로 나누어 반환한다.
     *
     *
     *
     * 공백(" ")을 기준으로 줄 바꿈을 시도한다.
     *
     *
     * @param str
     * @param maxLineLength 한줄의 최대 길이
     * @return
     */
    fun wrap(str: String?, maxLineLength: Int): List<String>? {
        if (str == null) {
            return null
        }
        val lines: MutableList<String> = ArrayList()
        if (str.length <= maxLineLength || str.indexOf(' ') == INDEX_NOT_FOUND) {
            // 전체 길이가 최대 길이보다 짧거나, 구분할수 있는 조건이 안되면 그대로 반환한다.
            lines.add(str)
            return lines
        }
        val sb = StringBuilder()
        val tokenzier = StringTokenizer(str, " ")
        sb.append(tokenzier.nextToken())
        while (tokenzier.hasMoreTokens()) {
            val token = tokenzier.nextToken()
            if (sb.length + token.length + 1 > maxLineLength) {
                lines.add(sb.toString())
                sb.setLength(0)
                sb.append(token)
            } else {
                sb.append(" ")
                sb.append(token)
            }
        }
        if (sb.toString().trim { it <= ' ' }.length > 0) {
            lines.add(sb.toString())
        }
        return lines
    }

    /**
     *
     *
     * 문자열이 해당 길이보다 크면, 자른 후 줄임말을 붙여준다.
     *
     *
     *
     * 길이는 기본문자들(영어/숫자등)이 1으로, 다국어(한글등)이면 2로 계산한다.
     *
     *
     *
     * <pre>
     * StringUtil.curtail(null, *, *) = null
     * StringUtil.curtail("abcdefghijklmnopqr", 10, null) = "abcdefghij"
     * StringUtil.curtail("abcdefghijklmnopqr", 10, "..") = "abcdefgh.."
     * StringUtil.curtail("한글을 사랑합시다.", 10, null)   = "한글을 사랑"
     * StringUtil.curtail("한글을 사랑합시다.", 10, "..")   = "한글을 사.."
    </pre> *
     *
     * @param str  문자열
     * @param size 길이(byte 길이)
     * @param tail 줄임말
     * @return
     */
    fun curtail(str: String?, size: Int, tail: String?): String? {
        if (str == null) {
            return null
        }
        val strLen = str.length
        val tailLen = tail?.length ?: 0
        val maxLen = size - tailLen
        var curLen = 0
        var index = 0
        while (index < strLen && curLen < maxLen) {
            if (Character.getType(str[index]) == Character.OTHER_LETTER.toInt()) {
                curLen++
            }
            curLen++
            index++
        }
        return if (index == strLen) {
            str
        } else {
            val result = StringBuilder()
            result.append(str.substring(0, index))
            if (tail != null) {
                result.append(tail)
            }
            result.toString()
        }
    }
    // ----------------------------------------------------------------------
    // 패딩
    // ----------------------------------------------------------------------
    /**
     *
     *
     * 왼쪽부터 크기만큼 패딩문자로 채운다.
     *
     *
     *
     * <pre>
     * StringUtil.leftPad("han", 5, " ")    = "  han"
     * StringUtil.leftPad("han", 5, "123")  = "12han"
     * StringUtil.leftPad("han", 10, "123") = "1231231han"
     * StringUtil.leftPad("han", -1, " ")   = "han"
    </pre> *
     *
     * @param str
     * @param size   크기
     * @param padStr 패딩문자
     * @return
     */
    fun leftPad(str: String?, size: Int, padStr: String): String? {
        var padStr = padStr
        if (str == null) {
            return null
        }
        if (isEmpty(padStr)) {
            padStr = " " //$NON-NLS-1$
        }
        val strLen = str.length
        val padStrLen = padStr.length
        val padLen = size - strLen
        if (padLen <= 0) {
            // 패딩할 필요가 없음
            return str
        }
        val result = StringBuilder()
        if (padLen == padStrLen) {
            result.append(padStr)
            result.append(str)
        } else if (padLen < padStrLen) {
            result.append(padStr.substring(0, padLen))
            result.append(str)
        } else {
            val padding = padStr.toCharArray()
            for (i in 0 until padLen) {
                result.append(padding[i % padStrLen])
            }
            result.append(str)
        }
        return result.toString()
    }

    /**
     *
     *
     * 오른쪽부터 크기만큼 패딩문자로 채운다.
     *
     *
     *
     * <pre>
     * StringUtil.rightPad("han", 5, " ")    = "han  "
     * StringUtil.rightPad("han", 5, "123")  = "han12"
     * StringUtil.rightPad("han", 10, "123") = "han1231231"
     * StringUtil.rightPad("han", -1, " ")   = "han"
    </pre> *
     *
     * @param str
     * @param size   크기
     * @param padStr 패딩문자
     * @return
     */
    fun rightPad(str: String?, size: Int, padStr: String): String? {
        var padStr = padStr
        if (str == null) {
            return null
        }
        if (isEmpty(padStr)) {
            padStr = " " //$NON-NLS-1$
        }
        val strLen = str.length
        val padStrLen = padStr.length
        val padLen = size - strLen
        if (padLen <= 0) {
            // 패딩할 필요가 없음
            return str
        }
        val result = StringBuilder()
        if (padLen == padStrLen) {
            result.append(str)
            result.append(padStr)
        } else if (padLen < padStrLen) {
            result.append(str)
            result.append(padStr.substring(0, padLen))
        } else {
            result.append(str)
            val padding = padStr.toCharArray()
            for (i in 0 until padLen) {
                result.append(padding[i % padStrLen])
            }
        }
        return result.toString()
    }

    /**
     *
     *
     * 숫자를 알바벳으로 변경한다.
     *
     *
     *
     * <pre>
     * StringUtil.changeAlpabet(0)  = "A"
     * StringUtil.changeAlpabet(1)  = "B"
     * StringUtil.changeAlpabet(5)  = "F"
     * StringUtil.changeAlpabet(100)= ""
    </pre> *
     *
     * @param num
     * @return
     */
    fun changeAlpabet(num: Int): String {
        val alpabet = arrayOf(
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "ToastUtil",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z"
        )
        return if (num > 25) "" else alpabet[num]
    }

    /**
     * HTML 포맷으로 특수 문자 변환
     *
     * @param s 원본 문자열
     * @return 치환된 문자열
     */
    fun toHTMLString(s: String?): String {
        val stringbuffer = StringBuffer()
        var i = 0
        while (s != null && i < s.length) {
            val c = s[i]
            if (c == '\'') // stringbuffer.append("&#39;");
                stringbuffer.append(c) else if (c == '"') // stringbuffer.append("&#34;");
                stringbuffer.append(c) else if (c == '\n') stringbuffer.append("<BR>\n") else if (c == '\t') stringbuffer.append(
                "&nbsp;&nbsp;&nbsp;&nbsp;"
            ) else if (c == '<') // stringbuffer.append("&lt;");
                stringbuffer.append(c) else if (c == '>') // stringbuffer.append("&gt;");
                stringbuffer.append(c) else if (c == '&') stringbuffer.append("&amp;") else stringbuffer.append(
                c
            )
            i++
        }
        return stringbuffer.toString()
    }

    /**
     * XML 포맷으로 특수 문자 변환
     *
     * @param s 원본 문자열
     * @return 치환된 문자열
     */
    fun toXMLString(s: String?): String {
        val stringbuffer = StringBuffer()
        var i = 0
        while (s != null && i < s.length) {
            val c = s[i]
            if (c == '\'') stringbuffer.append("&#39;") else if (c == '"') stringbuffer.append("&#34;") else if (c == '\n') stringbuffer.append(
                "<BR>\n"
            ) else if (c == '\t') stringbuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;") else if (c == '<') stringbuffer.append(
                "&lt;"
            ) else if (c == '>') stringbuffer.append("&gt;") else if (c == '&') stringbuffer.append(
                "&amp;"
            ) else stringbuffer.append(c)
            i++
        }
        return stringbuffer.toString()
    }

    /**
     * 문자열에서 이전문자를 새 문자로 대체해서 반환
     *
     * @param s      문자열
     * @param oldSub 이전문자
     * @param newSub 새 문자
     * @return String 결과 문자열
     */
    fun replace(s: String?, oldSub: Char, newSub: Char): String {
        return replace(s, oldSub, Character.valueOf(newSub))
    }

    /**
     * 문자열에서 이전문자를 새 문자로 대체해서 반환
     *
     * @param s      문자열
     * @param oldSub 이전문자
     * @param newSub 새 문자
     * @return String 결과 문자열
     */
    fun replace(s: String?, oldSub: Char, newSub: String?): String? {
        if (s == null || newSub == null) {
            return null
        }
        val c = s.toCharArray()
        val sb = StringBuffer()
        for (i in c.indices) {
            if (c[i] == oldSub) {
                sb.append(newSub)
            } else {
                sb.append(c[i])
            }
        }
        return sb.toString()
    }

    /**
     * 문자열에서 이전문자를 새 문자로 대체해서 반환
     *
     * @param s      문자열
     * @param oldSub 이전문자
     * @param newSub 새 문자
     * @return String 결과 문자열
     */
    fun replace(s: String?, oldSub: String?, newSub: String?): String? {
        if (s == null || oldSub == null || newSub == null) {
            return null
        }
        var y = s.indexOf(oldSub)
        return if (y >= 0) {
            val sb = StringBuffer()
            val length = oldSub.length
            var x = 0
            while (x <= y) {
                sb.append(s.substring(x, y))
                sb.append(newSub)
                x = y + length
                y = s.indexOf(oldSub, x)
            }
            sb.append(s.substring(x))
            sb.toString()
        } else {
            s
        }
    }

    /**
     * 문자열에서 이전문자를 새 문자로 대체해서 반환
     *
     * @param s       문자열
     * @param oldSubs 이전문자배열
     * @param newSubs 새 문자배열
     * @return String 결과 문자열
     */
    fun replace(s: String?, oldSubs: Array<String?>?, newSubs: Array<String?>?): String? {
        var s = s
        if (s == null || oldSubs == null || newSubs == null) {
            return null
        }
        if (oldSubs.size != newSubs.size) {
            return s
        }
        for (i in oldSubs.indices) {
            s = replace(s, oldSubs[i], newSubs[i])
        }
        return s
    }

    /**
     * 문자열에서 이전문자를 새 문자로 대체해서 반환
     *
     * @param s       문자열
     * @param oldSubs 이전문자배열
     * @param newSubs 새 문자배열
     * @return String 결과 문자열
     */
    fun replace(s: String?, oldSubs: Array<String?>?, newSubs: CharArray?): String? {
        var s = s
        if (s == null || oldSubs == null || newSubs == null) {
            return null
        }
        if (oldSubs.size != newSubs.size) {
            return s
        }
        for (i in oldSubs.indices) {
            s = replace(s, oldSubs[i], newSubs[i].toString())
        }
        return s
    }

    /**
     * 문자열을 날짜 형식으로 변환
     *
     * @param d     문자열
     * @param delim 구분 분자
     * @return String 결과 값
     */
    fun toDateFormat(d: String?, delim: String?): String? {
        val sb = StringBuffer()
        if (d == null || d == "" || d.length != 8) {
            return d
        }
        sb.append(d.substring(0, 4))
        sb.append(delim)
        sb.append(d.substring(4, 6))
        sb.append(delim)
        sb.append(d.substring(6))
        return sb.toString()
    }

    /**
     * \n -> <br></br>
     * 로 치환
     */
    fun newLineToBr(s: String?): String? {
        return if (s == null) null else replace(s, "\n", "<br>")
    }

    /**
     * 스트링 parameter의 모든 html 태그를 제거한다.
     *
     * @param s
     */
    fun removeAllTags(s: String?): String? {
        var s = s ?: return null
        var m: Matcher
        m = Patterns.TAGS.matcher(s)
        s = m.replaceAll("")
        m = Patterns.ENTITY_REFS.matcher(s)
        s = m.replaceAll("")
        m = Patterns.WHITESPACE.matcher(s)
        s = m.replaceAll(" ")
        return s
    }

    /**
     * URL에서 해당 파라미터를 제외하고 반환한다.
     *
     * @param url
     * @param excepts
     * @return
     */
    fun removeParameter(url: String?, excepts: List<String?>): String? {
        if (url == null) {
            return null
        }
        val sb = StringBuffer()
        try {
            val mUrl = URL(url)
            sb.append(mUrl.protocol).append("://").append(mUrl.host).append(mUrl.path).append("?")
            val params = split(mUrl.query, "&")
            for (p in params!!) {
                val arr = split(p, "=")
                if (!excepts.contains(arr!![0])) {
                    sb.append("&").append(p)
                }
            }
        } catch (e: MalformedURLException) {
            return null
        }
        return sb.toString()
    }

    /**
     * URL의 파라미터의 값을 반환
     *
     * @param url   URL
     * @param param 파라미터명
     * @return
     */
    fun getParamValue(url: String?, param: String?): String? {
        if (url == null || param == null) return null
        val sb = StringBuffer()
        try {
            val arrStr = split(URL(url).query, "&")
            for (str in arrStr!!) {
                val compare = split(str, "=")
                if (equals(param, compare!![0])) {
                    sb.append(compare[1])
                    break
                }
            }
        } catch (e: MalformedURLException) {
        }
        return sb.toString()
    }

    fun toMoneyStyle(string: String): String {
        val myFormatter = DecimalFormat("#,###")
        val output = myFormatter.format(string.toDouble())
        return "￦$output"
    }

    fun toMoneyStyleNoWon(string: String): String {
        val myFormatter = DecimalFormat("#,###")
        return myFormatter.format(string.toDouble())
    }

    fun toMoneyStyleNoWon(money: Int): String {
        val myFormatter = DecimalFormat("#,###,###")
        return myFormatter.format(money.toLong())
    }

    private interface Patterns {
        companion object {
            // HTML/XML tags
            val TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>")

            @Suppress("unused")
            val nTAGS = Pattern.compile("<\\w+\\s+[^<]*\\s*>")

            // entity references
            val ENTITY_REFS = Pattern.compile("&[^;]+;")

            // repeated whitespace
            val WHITESPACE = Pattern.compile("\\s\\s+")
        }
    }

    class StringFilter(context: Context?) {
        private var context: Context? = null

        // Allows only alphanumeric characters. Filters special and hangul
        // characters.
        var allowAlphanumeric = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_ALPHANUMERIC
            )!!
        }

        // Allows only alpha and hangul characters. Filters special
        // characters.
        var allowAlphaHangul = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_ALPHA_HANGUL
            )!!
        }

        // Allows only alphanumeric and hangul characters. Filters special
        // characters.
        var allowAlphanumericHangul = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_ALPHANUMERIC_HANGUL
            )!!
        }

        // Allows only alphanumeric and hangul characters. Filters special
        // characters.
        var allowNumericHangul = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_NUMERIC_HANGUL
            )!!
        }

        // Allows only alphanumeric and hangul characters. Filters special
        // characters.
        var allowAlphanumericHangulSpecial = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_ALPHANUMERIC_HANGUL_SPECIAL
            )!!
        }

        // Allows only alphanumeric and hangul characters. Filters special
        // characters.
        var allowNumericHnagulSpecial = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_NUMERIC_HANGUL_SPECIAL
            )!!
        }

        // Allows only alphanumeric and hangul characters. Filters special
        // characters.
        var allowAlphanumericMonkey = InputFilter { source, start, end, dest, dstart, dend ->
            filteredString(
                source,
                start,
                end,
                ALLOW_ALPHANUMERIC_MONKEY
            )!!
        }

        init {
            this.context = context
        }

        // Returns the string result which is filtered by the given mode
        private fun filteredString(
            source: CharSequence, start: Int,
            end: Int, mode: Int
        ): CharSequence? {
            var pattern: Pattern? = null
            pattern =
                if (mode == ALLOW_ALPHANUMERIC) {
                    Pattern.compile(pattern_alphanumeric)
                } else if (mode == ALLOW_ALPHA_HANGUL) {
                    Pattern.compile(pattern_alpha_hangul)
                } else if (mode == ALLOW_ALPHANUMERIC_HANGUL) {
                    Pattern.compile(pattern_alphanumeric_hangul)
                } else if (mode == ALLOW_ALPHANUMERIC_HANGUL_SPECIAL) {
                    Pattern.compile(pattern_alphanumeric_hangul_special)
                } else if (mode == ALLOW_ALPHANUMERIC_MONKEY) {
                    Pattern.compile(pattern_alphanumeric_monkey)
                } else if (mode == ALLOW_NUMERIC_HANGUL_SPECIAL) {
                    Pattern.compile(pattern_numeric_hangul_special)
                } else {
                    Pattern.compile(pattern_numeric_hangul)
                }
            var keepOriginal = true
            val stringBuilder = StringBuilder(end - start)
            for (i in start until end) {
                val c = source[i]
                if (pattern.matcher(c.toString()).matches()) {
                    stringBuilder.append(c)
                } else {
                    if (mode == ALLOW_ALPHANUMERIC) {
                        showToast(input_error_alphanum)
                    } else if (mode == ALLOW_ALPHA_HANGUL) {
                        showToast(input_error_alpha_hangul)
                    } else if (mode == ALLOW_ALPHANUMERIC_HANGUL) {
                        showToast(input_error_alphanumeric_hangul)
                    } else if (mode == ALLOW_ALPHANUMERIC_HANGUL_SPECIAL) {
                        showToast(input_error_alphanumeric_hangul_special)
                    } else if (mode == ALLOW_ALPHANUMERIC_MONKEY) {
                        showToast(input_error_alphanum_monkey)
                    } else if (mode == ALLOW_NUMERIC_HANGUL_SPECIAL) {
                        showToast(input_error_numeric_hangul_special)
                    } else {
                        showToast(input_error_numeric_hangul)
                    }
                    keepOriginal = false
                }
            }
            return if (keepOriginal) {
                null
            } else {
                if (source is Spanned) {
                    val spannableString = SpannableString(
                        stringBuilder
                    )
                    TextUtils.copySpansFrom(
                        source, start,
                        stringBuilder.length, null, spannableString, 0
                    )
                    spannableString
                } else {
                    stringBuilder
                }
            }
        }

        // Shows toast with specify delay that is shorter than Toast.LENGTH_SHORT
        private fun showToast(msg: String) {
            val toast = Toast.makeText(
                context!!.applicationContext,
                msg, Toast.LENGTH_SHORT
            )
            toast.show()
            val handler = Handler()
            handler.postDelayed({ toast.cancel() }, TOAST_LELNGTH.toLong())
        }

        companion object {
            private val CLASS_NAME = StringFilter::class.java
                .getCanonicalName()
            const val ALLOW_ALPHANUMERIC = 0
            const val ALLOW_ALPHA_HANGUL = 1
            const val ALLOW_ALPHANUMERIC_HANGUL = 2
            const val ALLOW_NUMERIC_HANGUL = 3
            const val ALLOW_ALPHANUMERIC_HANGUL_SPECIAL = 4
            const val ALLOW_ALPHANUMERIC_MONKEY = 5
            const val ALLOW_NUMERIC_HANGUL_SPECIAL = 6
            const val TOAST_LELNGTH = 400
            private const val pattern_alphanumeric = "^[a-zA-Z0-9]+$"
            private const val pattern_alphanumeric_monkey = "^[a-zA-Z0-9@.]+$"
            private const val pattern_alpha_hangul =
                "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$"
            private const val pattern_alphanumeric_hangul =
                "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55 \\n]+$"
            private const val pattern_numeric_hangul =
                "^[0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$"
            private const val pattern_alphanumeric_hangul_special =
                "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55 \\n!#$%^<![CDATA[&]]>*()?+=\\/]+$"
            private const val pattern_numeric_hangul_special =
                "^[0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55 \\n!#$%^<![CDATA[&]]>*()?+=\\/]+$"
            private const val input_error_alphanum = "영문자와 숫자만 허용합니다"
            private const val input_error_alphanum_monkey = "영문자와 숫자,@만 허용합니다."
            private const val input_error_alpha_hangul = "영문자와 한글만 허용합니다."
            private const val input_error_alphanumeric_hangul = "영문자와 숫자, 한글만 허용합니다."
            private const val input_error_alphanumeric_hangul_special = "영문자와 숫자, 한글, 특문만 허용합니다."
            private const val input_error_numeric_hangul = "숫자와 한글만 허용합니다."
            private const val input_error_numeric_hangul_special = "숫자와 한글, 특문만 허용합니다."
            fun setCharacterLimited(ctx: Context?, editText: EditText, type: Int) {
                val stringFilter = StringFilter(ctx)
                var fileter = stringFilter.allowAlphanumeric
                when (type) {
                    ALLOW_ALPHANUMERIC -> fileter = stringFilter.allowAlphanumeric
                    ALLOW_ALPHA_HANGUL -> fileter = stringFilter.allowAlphaHangul
                    ALLOW_ALPHANUMERIC_HANGUL -> fileter = stringFilter.allowAlphanumericHangul
                    ALLOW_ALPHANUMERIC_HANGUL_SPECIAL -> fileter =
                        stringFilter.allowAlphanumericHangulSpecial

                    ALLOW_ALPHANUMERIC_MONKEY -> fileter = stringFilter.allowAlphanumericMonkey
                    ALLOW_NUMERIC_HANGUL -> fileter = stringFilter.allowNumericHangul
                    ALLOW_NUMERIC_HANGUL_SPECIAL -> fileter = stringFilter.allowNumericHnagulSpecial
                }
                val curFilters = editText.filters
                val newFilters = arrayOfNulls<InputFilter>(curFilters.size + 1)
                for (i in curFilters.indices) newFilters[i] = curFilters[i]
                newFilters[curFilters.size] = fileter
                editText.setFilters(newFilters)
            }
        }
    }
}
