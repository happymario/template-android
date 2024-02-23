package com.mario.lib.base.extension


fun String.capitalize() = replaceFirstChar { it.uppercase() }