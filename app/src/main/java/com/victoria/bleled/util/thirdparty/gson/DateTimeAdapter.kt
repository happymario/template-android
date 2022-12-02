package com.victoria.bleled.util.thirdparty.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateTimeAdapter : JsonDeserializer<LocalDateTime> {

    private var FOTMATS = listOf(
        "yyyy.MM.dd HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.n"

    )

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): LocalDateTime? {
        for (format in FOTMATS) {
            try {
                val j = json?.asJsonPrimitive?.asString
                return LocalDateTime.parse(j, DateTimeFormatter.ofPattern(format))
            } catch (e: DateTimeParseException) {

            }
        }

        throw JsonParseException("DATE 타입으로 변환할 수 없습니다: ${json?.asJsonPrimitive?.asString}")
    }
}