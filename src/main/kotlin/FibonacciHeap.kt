// Converted from Java by IntelliJ IDEA
// copied from https://www.geeksforgeeks.org/java-program-to-implement-fibonacci-heap/
package io.github.zerumi

import java.util.*
import kotlin.math.floor
import kotlin.math.ln

// Class to represent a node in the Fibonacci Heap
class FibonacciHeapNode(var key: Int) {
    var degree: Int = 0
    var parent: FibonacciHeapNode? = null
    var child: FibonacciHeapNode? = null
    var left: FibonacciHeapNode? = this
    var right: FibonacciHeapNode? = this
    var mark: Boolean = false
}

// Constructor to initialize an empty Fibonacci Heap
// Class to represent a Fibonacci Heap
class FibonacciHeap {
    // Pointer to the minimum node in the heap
    private var minNode: FibonacciHeapNode? = null

    // Total number of nodes in the heap
    private var nodeCount = 0

    // Insert a new node into the Fibonacci Heap
    fun insert(key: Int) {
        val node = FibonacciHeapNode(key)
        if (minNode != null) {
            // Add the new node to the root list
            addToRootList(node)
            if (key < minNode!!.key) {
                // Update the minNode pointer if necessary
                minNode = node
            }
        } else {
            // Set the new node as the minNode if the heap was empty
            minNode = node
        }
        nodeCount++
    }

    // Add a node to the root list of the Fibonacci Heap
    private fun addToRootList(node: FibonacciHeapNode) {
        node.left = minNode
        node.right = minNode!!.right
        minNode!!.right!!.left = node
        minNode!!.right = node
    }

    // Find the minimum node in the Fibonacci Heap
    fun findMin(): FibonacciHeapNode? {
        return minNode
    }

    // Extract the minimum node from the Fibonacci Heap
    fun extractMin(): FibonacciHeapNode? {
        val min = minNode
        if (min != null) {
            if (min.child != null) {
                // Add the children of the minNode to the root list
                addChildrenToRootList(min)
            }


            // Remove the minNode from the root list
            removeNodeFromRootList(min)
            if (min === min.right) {
                // Set minNode to null if it was the only node in the root list
                minNode = null
            } else {
                minNode = min.right
                // Consolidate the trees in the root list
                consolidate()
            }
            nodeCount--
        }
        return min
    }

    // Add the children of a node to the root list
    private fun addChildrenToRootList(min: FibonacciHeapNode) {
        var child = min.child
        do {
            val nextChild = child!!.right
            child.left = minNode
            child.right = minNode!!.right
            minNode!!.right!!.left = child
            minNode!!.right = child
            child.parent = null
            child = nextChild
        } while (child !== min.child)
    }

    // Remove a node from the root list
    private fun removeNodeFromRootList(node: FibonacciHeapNode?) {
        node!!.left!!.right = node.right
        node.right!!.left = node.left
    }

    // Consolidate the trees in the root list
    private fun consolidate() {
        val arraySize = (floor(ln(nodeCount.toDouble()) / ln(2.0)) as Int) + 1
        val array: MutableList<FibonacciHeapNode?> = ArrayList(Collections.nCopies<FibonacciHeapNode?>(arraySize, null))
        val rootList = rootList

        for (node in rootList) {
            var node = node
            var degree = node!!.degree
            while (array[degree] != null) {
                var other = array[degree]
                if (node!!.key > other!!.key) {
                    val temp = node
                    node = other
                    other = temp
                }


                // Link two trees of the same degree
                link(other, node)
                array[degree] = null
                degree++
            }
            array[degree] = node
        }

        minNode = null
        for (node in array) {
            if (node != null) {
                if (minNode == null) {
                    minNode = node
                } else {
                    // Add the node back to the root list

                    addToRootList(node)
                    if (node.key < minNode!!.key) {
                        minNode = node
                    }
                }
            }
        }
    }

    private val rootList: List<FibonacciHeapNode?>
        // Get a list of all root nodes
        get() {
            val rootList: MutableList<FibonacciHeapNode?> = ArrayList()
            if (minNode != null) {
                var current = minNode
                do {
                    rootList.add(current)
                    current = current!!.right
                } while (current !== minNode)
            }
            return rootList
        }

    // Link two trees of the same degree
    private fun link(y: FibonacciHeapNode?, x: FibonacciHeapNode?) {
        // Remove y from the root list
        removeNodeFromRootList(y)
        y!!.right = y
        y.left = y.right
        y.parent = x

        if (x!!.child == null) {
            // Make y a child of x
            x.child = y
        } else {
            y.right = x.child
            y.left = x.child!!.left
            x.child!!.left!!.right = y
            x.child!!.left = y
        }
        x.degree++
        y.mark = false
    }
}
