package com.krzysztofcal.mileconverter

import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceConverterTest {
    @Test
    fun convertsMilesToKilometers() {
        assertEquals(1.609344, DistanceConverter.convert(1.0, DistanceUnit.Miles, DistanceUnit.Kilometers), 1e-9)
    }

    @Test
    fun convertsKilometersToNauticalMiles() {
        assertEquals(1.0, DistanceConverter.convert(1.852, DistanceUnit.Kilometers, DistanceUnit.NauticalMiles), 1e-9)
    }

    @Test
    fun convertsNauticalMilesToMiles() {
        assertEquals(1.1507794480235425, DistanceConverter.convert(1.0, DistanceUnit.NauticalMiles, DistanceUnit.Miles), 1e-12)
    }
}
