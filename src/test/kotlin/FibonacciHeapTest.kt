import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import w01fe.fibonacci_heap.FibonacciHeap
import w01fe.fibonacci_heap.insert
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FibonacciHeapTest {
    @Test
    fun `testHeap sequentially inserting 5 minimums`() {
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            val heap = FibonacciHeap()
            for (i in 10..15) heap.insert(i)
            val min = heap.min()
            assertNotNull(min)
            assertEquals(10, min.key as Int)
        }
    }

    @Test
    fun `testHeap sequentially inserting 1000000 minimums`() {
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            val heap = FibonacciHeap()
            val nums = 1..1000000
            for (i in nums) heap.insert(i)
            for (i in nums) {
                val min = heap.min()
                assertNotNull(min)
                assertEquals(i, min.key as Int)
                heap.removeMin()
            }
        }
    }

    @Test
    fun `testHeap reversed inserting 1000 minimums`() {
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            val heap = FibonacciHeap()
            val nums = 1000 downTo 1 step 2
            for (i in nums) heap.insert(i)
            for (i in nums.reversed()) {
                val min = heap.min()
                assertNotNull(min)
                assertEquals(i, min.key as Int)
                heap.removeMin()
            }
        }
    }

    @Test
    fun `testHeap shuffled inserting`() {
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            val heap = FibonacciHeap()
            val nums = 1..10000
            for (i in nums.shuffled()) heap.insert(i)
            for (i in nums) {
                val min = heap.min()
                assertNotNull(min)
                assertEquals(i, min.key as Int)
                heap.removeMin()
            }
        }
    }

    @Test
    fun `testHeap inserting similar elements`() {
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            val heap = FibonacciHeap()
            val nums = arrayOf(3, 3, 3, 4, 4, 4)
            for (i in nums) heap.insert(i)
            for (i in nums) {
                val min = heap.min()
                assertNotNull(min)
                assertEquals(i, min.key as Int)
                heap.removeMin()
            }
        }
    }

    @Test
    fun `testHeap fibonacci algorithm to build child and left-right elements`() {
        assertTimeoutPreemptively(Duration.ofSeconds(5)) {
            val heap = FibonacciHeap()
            val nums = arrayOf(3, 1, 2)
            for (i in nums) heap.insert(i)
            val min = heap.min()
            // 1 should be min
            assertNotNull(min)
            assertEquals(1, min.key as Int)

            // 2 should be left, 3 - right
            val left = min.left
            val right = min.right
            assertNotNull(left)
            assertNotNull(right)
            assertEquals(2, left.key as Int)
            assertEquals(3, right.key as Int)

            heap.insert(0)
            val zeroShouldBeMin = heap.min()
            assertNotNull(zeroShouldBeMin)
            assertEquals(0, zeroShouldBeMin.key as Int)

            heap.removeMin()

            // 2 should be left, 1 should be min with child 3
            val nextMin = heap.min()
            assertNotNull(nextMin)
            assertNotNull(nextMin.left)
            assertNotNull(nextMin.child)
            assertEquals(1, nextMin.key as Int)
            assertEquals(2, nextMin.left.key as Int)
            assertEquals(3, nextMin.child!!.key as Int)
        }
    }
}