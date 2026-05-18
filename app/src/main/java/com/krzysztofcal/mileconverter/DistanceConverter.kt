package com.krzysztofcal.mileconverter

enum class DistanceUnit(
    val displayName: String,
    val kilometersFactor: Double,
) {
    Miles("Miles", 1.609344),
    Kilometers("Kilometers", 1.0),
    NauticalMiles("Nautical miles", 1.852),
}

object DistanceConverter {
    fun convert(value: Double, from: DistanceUnit, to: DistanceUnit): Double =
        value * from.kilometersFactor / to.kilometersFactor

    fun convertAll(value: Double, from: DistanceUnit): Map<DistanceUnit, Double> =
        DistanceUnit.entries.associateWith { unit -> convert(value, from, unit) }
}
