import kotlin.math.roundToInt
import kotlin.math.sqrt

// "Золотое Сердце" плыл через космическую ночь, теперь уже на обычном фотоновом двигателе.
// Четыре человека, составлявшие его экипаж, чувствовали себя неуютно, зная, что они вместе
// не по собственной воле и не по простому совпадению, а по странному физическому принципу --
// как будто отношения между людьми подчиняются тем же законам, что отношения между атомами
// и молекулами.

data class State<T>(
    val state: T,
    val test: (Boolean) -> ConditionalState<T>
)

data class ConditionalState<T>(
    val state: T,
    val ifTrue: ((T) -> T) -> ConditionalState<T>,
    val ifFalse: ((T) -> T) -> ConditionalState<T>,
    val then: () -> State<T>,
    val collectResult: () -> T
)

fun <T> initState(state: T): State<T> = State(
    state = state,
    test = { cond ->
        fun createConditionalState(updatedState: T): ConditionalState<T> = ConditionalState(
            state = updatedState,
            ifTrue = { function ->
                if (cond) createConditionalState(function(updatedState)) else createConditionalState(updatedState)
            },
            ifFalse = { function ->
                if (!cond) createConditionalState(function(updatedState)) else createConditionalState(updatedState)
            },
            then = { initState(updatedState) },
            collectResult = { updatedState }
        )
        createConditionalState(state)
    }
)

data class Spaceship(
    val name: String,
    val mass: UInt,
    val engine: Engine,
)

data class Engine(
    val mass: UInt, val power: UInt
)

data class Flight(
    val spaceship: Spaceship,
    var gas: Boolean,
    var brake: Boolean,
    var gasInitTime: Long,
    var brakeInitTime: Long,
    var speed: Double,
)

data class FlightControl(
    val gasInit: (Long) -> Unit,
    val gasStop: (Long) -> Unit,
    val brakeInit: (Long) -> Unit,
    val brakeStop: (Long) -> Unit,
    val speed: (Long) -> Double,
)

fun initFlight(
    spaceship: Spaceship,
): FlightControl {
    val flight = Flight(spaceship, gas = false, brake = false, gasInitTime = 0, brakeInitTime = 0, speed = 0.0)

    return FlightControl(gasInit = { timestamp ->
        flight.gas = true
        flight.gasInitTime = timestamp
    }, gasStop = { timestamp ->
        if (!flight.gas) return@FlightControl

        flight.gas = false

        val time = timestamp - flight.gasInitTime
        val acceleration = sqrt(
            spaceship.engine.power.toDouble() / (2.0 * (spaceship.mass + spaceship.engine.mass).toDouble() * time)
        )
        flight.speed += acceleration * time
    }, brakeInit = { timestamp ->
        flight.brake = true
        flight.brakeInitTime = timestamp
    }, brakeStop = { timestamp ->
        if (!flight.brake) return@FlightControl

        flight.brake = false

        val time = timestamp - flight.brakeInitTime
        val brake = (time / 250.0).roundToInt()
        flight.speed = (flight.speed - brake).coerceAtLeast(0.0)
    }, speed = { timestamp ->
        initState(flight.speed)
            .test(flight.gas)
            .ifTrue { speed ->
                val time = timestamp - flight.gasInitTime
                val acceleration = sqrt(
                    spaceship.engine.power.toDouble() / (2.0 * (spaceship.mass + spaceship.engine.mass).toDouble() * time)
                )
                speed + acceleration * time
            }
            .then()
            .test(flight.brake)
            .ifTrue { speed ->
                val time = timestamp - flight.brakeInitTime
                val brake = (time / 250.0).roundToInt()
                (speed - brake).coerceAtLeast(0.0)
            }
            .collectResult()
    })
}

fun main() {
    val goldenHeart = Spaceship(
        name = "Golden Heart", mass = 100000u, engine = Engine(
            mass = 10000u, power = 99999u
        )
    )

    val flight = initFlight(goldenHeart)
    val initFlightTime = System.currentTimeMillis()

    Thread {
        while (true) {
            val timestamp = System.currentTimeMillis() - initFlightTime
            println("Current speed ($timestamp): ${flight.speed(timestamp)}m/s")
            Thread.sleep(250)
        }
    }.start()

    Thread {
        while (true) {
            val reader = System.`in`.reader()
            val char = reader.read().toChar()
            val registerTime = System.currentTimeMillis() - initFlightTime
            when (char) {
                'w' -> flight.brakeStop(registerTime).also { flight.gasInit(registerTime) }
                's' -> flight.gasStop(registerTime).also { flight.brakeInit(registerTime) }
                'q' -> flight.gasStop(registerTime).also { flight.brakeStop(registerTime) }
            }
        }
    }.start()
}
