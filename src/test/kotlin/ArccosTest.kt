import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.test.assertEquals

class ArccosTest {
    @ParameterizedTest
    @CsvFileSource(resources = ["/data.csv"], numLinesToSkip = 1)
    fun `testArccos should pass tests from coverage table`(
        testName: String,
        input: Double,
        expected: Double,
        tolerance: Double
    ) {
        assertEquals(
            expected,
            arccos(input),
            tolerance,
            "$testName failed, actual: ${arccos(input)}"
        )
    }

    @Test
    fun `testArccos should return 3,141 when x is -1`() {
        assertEquals(PI, arccos(-1.0), 0.25)
    }

    @Test
    fun `testArccos should return 2,094 when x is -0,5`() {
        assertEquals(2 * PI / 3, arccos(-0.5), 1e-2)
    }

    @Test
    fun `testArccos should return 1,5708 when x is 0`() {
        assertEquals(PI / 2, arccos(0.0), 1e-2)
    }

    @Test
    fun `testArccos should return 1,047 when x is 0,5`() {
        assertEquals(PI / 3, arccos(0.5), 1e-2)
    }

    @Test
    fun `testArccos should return 0,785 when x is 0,707`() {
        assertEquals(PI / 4, arccos(sqrt(2.0) / 2), 1e-2)
    }

    @Test
    fun `testArccos should return 0 when x is 1`() {
        assertEquals(0.0, arccos(1.0), 0.25)
    }

    @Test
    fun `testArccos should throw exception when x is greater than 1`() {
        assertThrows<IllegalArgumentException> { arccos(1.1) }
    }

    @Test
    fun `testArccos should throw exception when x is less than -1`() {
        assertThrows<IllegalArgumentException> { arccos(-1.1) }
    }
}
