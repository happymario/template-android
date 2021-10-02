package com.victoria.bleled.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tb_user")
data class ModelUser(@PrimaryKey(autoGenerate = true) var uid: Int) : BaseModel() {
    @JvmField
    @ColumnInfo(name = "access_token")
    var access_token: String? = null

    @JvmField
    @ColumnInfo(name = "id")
    var id: String? = null

    @JvmField
    @ColumnInfo(name = "first_name")
    var first_name: String? = null

    @JvmField
    @ColumnInfo(name = "last_name")
    var last_name: String? = null

    @Ignore
    private val reg_time: String? = null

    @Ignore
    private val profile_url: String? = null

    @Ignore
    private val profile_url_check: String? = null

    @Ignore
    private val status = 0

    enum class EUserSex(private val value: String) {
        none(""), man("m"), woman("f");

        fun value(): String {
            return value
        }

        companion object {
            fun toEnum(value: String?): EUserSex {
                if (value != null) {
                    if (value == "m" == true) {
                        return man
                    } else if (value == "f" == true) {
                        return woman
                    }
                }
                return none
            }
        }
    }

    enum class EUserLoginType(private val value: String) {
        normal("normal"), kakao("kakao"), google("google"), facebook("facebook");

        fun value(): String {
            return value
        }

        companion object {
            fun toEnum(value: String?): EUserLoginType {
                if (value != null) {
                    if (value == "kakao" == true) {
                        return kakao
                    } else if (value == "google" == true) {
                        return google
                    } else if (value == "facebook" == true) {
                        return facebook
                    }
                }
                return normal
            }
        }
    }

    companion object {
        fun isNormalUser(user_id: String): Boolean {
            try {
                if (user_id.toInt() > 10) { // 10이하가 특수유저임.
                    return true
                }
            } catch (e: Exception) {
                return false
            }
            return false
        }
    }
}