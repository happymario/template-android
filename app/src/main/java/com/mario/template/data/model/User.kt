package com.mario.template.data.model

import com.mario.template.base.BaseModel

data class User(var uid: Int) : BaseModel() {
    var access_token: String? = null
    var id: String? = null
    var name: String? = null
    private val reg_time: String? = null
    val profile_url: String? = null
    private val profile_url_check: String? = null
    val status = 0
    var pwd: String = ""

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
        normal("normal"), kk("kk"), nv("nv"), google("google"), facebook("facebook");

        fun value(): String {
            return value
        }

        companion object {
            fun toEnum(value: String?): EUserLoginType {
                if (value != null) {
                    if (value == "kk") {
                        return kk
                    } else if (value == "nv") {
                        return nv
                    } else if (value == "google") {
                        return google
                    } else if (value == "facebook") {
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