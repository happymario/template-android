package com.mario.template.util.extension


fun String.capitalize() = replaceFirstChar { it.uppercase() }