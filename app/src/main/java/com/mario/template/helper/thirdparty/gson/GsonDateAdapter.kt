package com.victoria.bleled.util.thirdparty.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class GsonDateAdapter : JsonDeserializer<LocalDate> {

    private val DATE_FORMATS = listOf(
        "yyyy.MM.dd",
        "yyyy-MM-dd"
    )

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): LocalDate? {
        for (format in DATE_FORMATS) {
            try {
                val j = json?.asJsonPrimitive?.asString
                return LocalDate.parse(j, DateTimeFormatter.ofPattern(format))
            } catch (e: DateTimeParseException) {
                // 복수개의 포멧중 되는놈으로 하겠다는 의미.
            }
        }

        // throw JsonParseException("DATE 타입으로 변환할 수 없습니다: ${json?.asJsonPrimitive?.asString}")
        return null
    }
}