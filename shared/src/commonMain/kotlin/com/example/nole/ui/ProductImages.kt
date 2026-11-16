package com.example.nole.ui

import nole.shared.generated.resources.Res
import nole.shared.generated.resources.c_1
import nole.shared.generated.resources.c_2
import nole.shared.generated.resources.c_3
import nole.shared.generated.resources.c_4
import nole.shared.generated.resources.c_5
import nole.shared.generated.resources.c_6
import nole.shared.generated.resources.c_7
import nole.shared.generated.resources.c_7_1
import nole.shared.generated.resources.c_8
import nole.shared.generated.resources.c_8_1
import nole.shared.generated.resources.c_9
import nole.shared.generated.resources.c_9_1
import nole.shared.generated.resources.f_1
import nole.shared.generated.resources.f_10
import nole.shared.generated.resources.f_11
import nole.shared.generated.resources.f_11_1
import nole.shared.generated.resources.f_12
import nole.shared.generated.resources.f_12_1
import nole.shared.generated.resources.f_13
import nole.shared.generated.resources.f_13_1
import nole.shared.generated.resources.f_14
import nole.shared.generated.resources.f_14_1
import nole.shared.generated.resources.f_2
import nole.shared.generated.resources.f_3
import nole.shared.generated.resources.f_4
import nole.shared.generated.resources.f_5
import nole.shared.generated.resources.f_6
import nole.shared.generated.resources.f_7
import nole.shared.generated.resources.f_8
import nole.shared.generated.resources.f_9
import org.jetbrains.compose.resources.DrawableResource

fun resolveDrawableResource(imageName: String): DrawableResource {
    return when (imageName) {
        "c_1" -> Res.drawable.c_1
        "c_2" -> Res.drawable.c_2
        "c_3" -> Res.drawable.c_3
        "c_4" -> Res.drawable.c_4
        "c_5" -> Res.drawable.c_5
        "c_6" -> Res.drawable.c_6
        "c_7" -> Res.drawable.c_7
        "c_7_1" -> Res.drawable.c_7_1
        "c_8" -> Res.drawable.c_8
        "c_8_1" -> Res.drawable.c_8_1
        "c_9" -> Res.drawable.c_9
        "c_9_1" -> Res.drawable.c_9_1
        "f_1" -> Res.drawable.f_1
        "f_2" -> Res.drawable.f_2
        "f_3" -> Res.drawable.f_3
        "f_4" -> Res.drawable.f_4
        "f_5" -> Res.drawable.f_5
        "f_6" -> Res.drawable.f_6
        "f_7" -> Res.drawable.f_7
        "f_8" -> Res.drawable.f_8
        "f_9" -> Res.drawable.f_9
        "f_10" -> Res.drawable.f_10
        "f_11" -> Res.drawable.f_11
        "f_11_1" -> Res.drawable.f_11_1
        "f_12" -> Res.drawable.f_12
        "f_12_1" -> Res.drawable.f_12_1
        "f_13" -> Res.drawable.f_13
        "f_13_1" -> Res.drawable.f_13_1
        "f_14" -> Res.drawable.f_14
        "f_14_1" -> Res.drawable.f_14_1
        else -> if (imageName.startsWith("c_")) Res.drawable.c_8 else Res.drawable.f_1
    }
}
