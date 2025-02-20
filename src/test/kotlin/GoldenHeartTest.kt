import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class GoldenHeartTest {
    @Test
    fun `testState single ifTrue should be executed when testing true`() {
        val state = initState(10)
        val result = state.test(true)
            .ifTrue { 2 * it }
            .collectResult()

        assertEquals(20, result)
    }

    @Test
    fun `testState single ifFalse should be executed when testing false`() {
        val state = initState(10)
        val result = state.test(false)
            .ifFalse { 2 * it }
            .collectResult()

        assertEquals(20, result)
    }

    @Test
    fun `testState multiple ifTrue should execute all of them`() {
        val state = initState(10)
        val result = state.test(true)
            .ifTrue { 2 * it }
            .ifTrue { 3 * it }
            .collectResult()

        assertEquals(60, result)
    }

    @Test
    fun `testState multiple ifFalse should execute all of them`() {
        val state = initState(10)
        val result = state.test(false)
            .ifFalse { 2 * it }
            .ifFalse { 3 * it }
            .collectResult()

        assertEquals(60, result)
    }

    @Test
    fun `testState multiple ifTrue and ifFalse should execute only true branch when testing true`() {
        val state = initState(10)
        val result = state.test(true)
            .ifTrue { it }
            .ifFalse { 2 * it }
            .ifTrue { 3 * it }
            .ifFalse { 4 * it }
            .collectResult()

        assertEquals(30, result)
    }

    @Test
    fun `testState multiple ifTrue and ifFalse should execute only false branch when testing false`() {
        val state = initState(10)
        val result = state.test(false)
            .ifTrue { it }
            .ifFalse { 2 * it }
            .ifTrue { 3 * it }
            .ifFalse { 4 * it }
            .collectResult()

        assertEquals(80, result)
    }

    @Test
    fun `testState then() should allow to construct new state`() {
        val state = initState(10)
        val result = state.test(true)
            .ifTrue { 2 * it }
            .ifFalse { 3 * it }
            .then().test(false)
            .ifTrue { 2 * it }
            .ifFalse { 3 * it }
            .collectResult()

        assertEquals(60, result)
    }

    @Test
    fun `testState similar states should be compared`() {
        val state = initState(10)
        val state2 = initState(10)

        assertEquals(state.state, state2.state)
        assertNotEquals(state.test, state2.test)

        val state3 = state2.test(true)
            .ifTrue { it }
            .then()

        assertEquals(state.state, state3.state)
        assertNotEquals(state.test, state3.test)
    }

    @Test
    fun `testFlight gas initialization`() {
        val spaceship = Spaceship("Apollo", 1000u, Engine(500u, 2000u))
        val flightControl = initFlight(spaceship)

        flightControl.gasInit(1000L)
        assertTrue(flightControl.speed(1000L) == 0.0)
    }

    @Test
    fun `testFlight gas should increase speed`() {
        val spaceship = Spaceship("Apollo", 1000u, Engine(500u, 2000u))
        val flightControl = initFlight(spaceship)

        flightControl.gasInit(1000L)
        flightControl.gasStop(2000L)

        val expectedSpeed = sqrt(2000.0 / (2.0 * 1500.0 * 1000)) * 1000
        assertEquals(expectedSpeed, flightControl.speed(2000L), 0.01)
    }

    @Test
    fun `testFlight brake initialization`() {
        val spaceship = Spaceship("Apollo", 1000u, Engine(500u, 2000u))
        val flightControl = initFlight(spaceship)

        flightControl.brakeInit(3000L)
        assertTrue(flightControl.speed(3000L) == 0.0)
    }

    @Test
    fun `testFlight brake should decrease speed`() {
        val spaceship = Spaceship("Apollo", 1000u, Engine(500u, 2000u))
        val flightControl = initFlight(spaceship)

        flightControl.gasInit(1000L)
        flightControl.gasStop(2000L)
        val initialSpeed = flightControl.speed(2000L)

        flightControl.brakeInit(3000L)
        flightControl.brakeStop(4000L)
        val brakeEffect = (1000 / 250.0).roundToInt()

        assertEquals((initialSpeed - brakeEffect).coerceAtLeast(0.0), flightControl.speed(4000L), 0.01)
    }
}
