package io.github.zerumi

import kotlin.math.PI
import kotlin.math.pow

// arccos(x) = pi/2 - arcsin(x)
fun arccos(x: Double): Double =
    (PI / 2 - (x + x.pow(3) / 6 + 3 * x.pow(5) / 40 + 15 * x.pow(7) / 336 +
            105 * x.pow(9) / 3456 + 945 * x.pow(11) / 42240 +
            10395 * x.pow(13) / 599040 + 135135 * x.pow(15) / 9676800 +
            2027025 * x.pow(17) / 175472640 + 34459425 * x.pow(19) / 3530096640))
        .also { require(x in -1.0..1.0) }
