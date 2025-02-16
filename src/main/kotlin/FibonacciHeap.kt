// Converted from Java to Kotlin using AI technologies
// additionally with nailed down "friendly" extension functions to it...

package w01fe.fibonacci_heap

import java.util.ArrayList

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License), or the GPL v2. 
 * You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or //www.gnu.org/licenses/gpl-2.0.html.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is GraphMaker. The Initial Developer of the Original
 * Software is Nathan L. Fiedler. Portions created by Nathan L. Fiedler
 * are Copyright (C) 1999-2008. All Rights Reserved.
 *
 * Contributor(s): Nathan L. Fiedler.
 *
 * $Id: FibonacciHeap.java 20 2008-12-05 15:51:14Z nathanfiedler $
 *
 * Portions Copyrighted 2011 by Jason Wolfe.
 * 
 */
class FibonacciHeap {
    /** Points to the minimum node in the heap. */
    private var min: Node? = null
    /** Number of nodes in the heap. If the type is ever widened,
     * (e.g. changed to long) then recalcuate the maximum degree
     * value used in the consolidate() method. */
    private var n: Int = 0

    /**
     * Removes all elements from this heap.
     *
     * <p><em>Running time: O(1)</em></p>
     */
    fun clear() {
        min = null
        n = 0
    }

    /**
     * Consolidates the trees in the heap by joining trees of equal
     * degree until there are no more trees of equal degree in the
     * root list.
     *
     * <p><em>Running time: O(log n) amortized</em></p>
     */
    private fun consolidate() {
        // The magic 45 comes from log base phi of Integer.MAX_VALUE,
        // which is the most elements we will ever hold, and log base
        // phi represents the largest degree of any root list node.
        val A = arrayOfNulls<Node>(45)

        // For each root list node look for others of the same degree.
        var start = min!!
        var w = min
        do {
            var x = w!!
            // Because x might be moved, save its sibling now.
            var nextW = w.right
            var d = x.degree
            while (A[d] != null) {
                // Make one of the nodes a child of the other.
                var y = A[d]!!
                if (x.key > y.key) {
                    val temp = y
                    y = x
                    x = temp
                }
                if (y === start) {
                    // Because removeMin() arbitrarily assigned the min
                    // reference, we have to ensure we do not miss the
                    // end of the root node list.
                    start = start.right
                }
                if (y === nextW) {
                    // If we wrapped around we need to check for this case.
                    nextW = nextW.right
                }
                // Node y disappears from root list.
                y.link(x)
                // We've handled this degree, go to next one.
                A[d] = null
                d++
            }
            // Save this node for later when we might encounter another
            // of the same degree.
            A[d] = x
            // Move forward through list.
            w = nextW
        } while (w !== start)

        // The node considered to be min may have been changed above.
        min = start
        // Find the minimum key again.
        for (a in A) {
            if (a != null && a.key < min!!.key) {
                min = a
            }
        }
    }

    /**
     * Decreases the key value for a heap node, given the new value
     * to take on. The structure of the heap may be changed, but will
     * not be consolidated.
     *
     * <p><em>Running time: O(1) amortized</em></p>
     *
     * @param  x  node to decrease the key of
     * @param  k  new key value for node x
     * @exception  IllegalArgumentException
     *             if key is larger than x.key value.
     */
    fun decreaseKey(x: Node, newData: Any, k: Comparable<Any>) {
        decreaseKey(x, newData, k, false)
    }

    /**
     * Decrease the key value of a node, or simply bubble it up to the
     * top of the heap in preparation for a delete operation.
     *
     * @param  x       node to decrease the key of.
     * @param  k       new key value for node x.
     * @param  delete  true if deleting node (in which case, k is ignored).
     */
    private fun decreaseKey(x: Node, newData: Any, k: Comparable<Any>, delete: Boolean) {
        if (!delete && k > x.key) {
            throw IllegalArgumentException("cannot increase key value")
        }
        x.key = k
        x.data = newData
        val y = x.parent
        if (y != null && (delete || k < y.key)) {
            y.cut(x, min!!)
            y.cascadingCut(min!!)
        }
        if (delete || k < min!!.key) {
            min = x
        }
    }

    /**
     * Deletes a node from the heap given the reference to the node.
     * The trees in the heap will be consolidated, if necessary.
     *
     * <p><em>Running time: O(log n) amortized</em></p>
     *
     * @param  x  node to remove from heap.
     */
    fun delete(x: Node) {
        // make x as small as possible
        decreaseKey(x, x.data!!, 0 as Comparable<Any>, true)
        // remove the smallest, which decreases n also
        removeMin()
    }

    /**
     * Tests if the Fibonacci heap is empty or not. Returns true if
     * the heap is empty, false otherwise.
     *
     * <p><em>Running time: O(1)</em></p>
     *
     * @return  true if the heap is empty, false otherwise.
     */
    fun isEmpty(): Boolean {
        return min == null
    }

    /**
     * Inserts a new data element into the heap. No heap consolidation
     * is performed at this time, the new node is simply inserted into
     * the root list of this heap.
     *
     * <p><em>Running time: O(1)</em></p>
     *
     * @param  x    data object to insert into heap.
     * @param  key  key value associated with data object.
     * @return newly created heap node.
     */
    fun insert(x: Any, key: Comparable<Any>): Node {
        val node = Node(x, key)
        // concatenate node into min list
        if (min != null) {
            node.right = min as Node
            node.left = min!!.left
            min!!.left = node
            node.left.right = node
            if (key < min!!.key) {
                min = node
            }
        } else {
            min = node
        }
        n++
        return node
    }

    /**
     * Returns the smallest element in the heap. This smallest element
     * is the one with the minimum key value.
     *
     * <p><em>Running time: O(1)</em></p>
     *
     * @return  heap node with the smallest key, or null if empty.
     */
    fun min(): Node? {
        return min
    }

    /**
     * Removes the smallest element from the heap. This will cause
     * the trees in the heap to be consolidated, if necessary.
     *
     * <p><em>Running time: O(log n) amortized</em></p>
     *
     * @return  data object with the smallest key.
     */
    fun removeMin(): Any? {
        val z = min ?: return null
        if (z.child != null) {
            z.child!!.parent = null
            // for each child of z do...
            var x = z.child!!.right
            while (x !== z.child) {
                x.parent = null
                x = x.right
            }
            // merge the children into root list
            val minleft = min!!.left
            val zchildleft = z.child!!.left
            min!!.left = zchildleft
            zchildleft.right = min as Node
            z.child!!.left = minleft
            minleft.right = z.child!!
        }
        // remove z from root list of heap
        z.left.right = z.right
        z.right.left = z.left
        if (z === z.right) {
            min = null
        } else {
            min = z.right
            consolidate()
        }
        // decrement size of heap
        n--
        return z.data
    }

    /**
     * Returns the size of the heap which is measured in the
     * number of elements contained in the heap.
     *
     * <p><em>Running time: O(1)</em></p>
     *
     * @return  number of elements in the heap.
     */
    fun size(): Int {
        return n
    }

    fun count(): Int {
        return n
    }

    companion object {
        /**
         * Joins two Fibonacci heaps into a new one. No heap consolidation is
         * performed at this time. The two root lists are simply joined together.
         *
         * <p><em>Running time: O(1)</em></p>
         *
         * @param  H1  first heap
         * @param  H2  second heap
         * @return  new heap containing H1 and H2
         */
        @JvmStatic
        fun union(H1: FibonacciHeap?, H2: FibonacciHeap?): FibonacciHeap {
            val H = FibonacciHeap()
            if (H1 != null && H2 != null) {
                H.min = H1.min
                if (H.min != null) {
                    if (H2.min != null) {
                        H.min!!.right.left = H2.min!!.left
                        H2.min!!.left.right = H.min!!.right
                        H.min!!.right = H2.min!!
                        H2.min!!.left = H.min!!
                        if (H2.min!!.key < H1.min!!.key) {
                            H.min = H2.min
                        }
                    }
                } else {
                    H.min = H2.min
                }
                H.n = H1.n + H2.n
            }
            return H
        }
    }

    fun nodeList(): ArrayList<Node> {
        val l = ArrayList<Node>()
        if (min != null) min!!.addToList(l)
        return l
    }

    /**
     * Implements a node of the Fibonacci heap. It holds the information
     * necessary for maintaining the structure of the heap. It acts as
     * an opaque handle for the data element, and serves as the key to
     * retrieving the data from the heap.
     *
     * @author  Nathan Fiedler
     */
    class Node(var data: Any?, var key: Comparable<Any>) {
        /** Parent node. */
        var parent: Node? = null
        /** First child node. */
        var child: Node? = null
        /** Right sibling node. */
        var right: Node = this
        /** Left sibling node. */
        var left: Node = this
        /** Number of children of this node. */
        var degree: Int = 0
        /** True if this node has had a child removed since this node was
         * added to its parent. */
        private var mark: Boolean = false

        /**
         * Performs a cascading cut operation. Cuts this from its parent
         * and then does the same for its parent, and so on up the tree.
         *
         * <p><em>Running time: O(log n)</em></p>
         *
         * @param  min  the minimum heap node, to which nodes will be added.
         */
        fun cascadingCut(min: Node) {
            val z = parent
            // if there's a parent...
            if (z != null) {
                if (mark) {
                    // it's marked, cut it from parent
                    z.cut(this, min)
                    // cut its parent as well
                    z.cascadingCut(min)
                } else {
                    // if y is unmarked, set it marked
                    mark = true
                }
            }
        }

        /**
         * The reverse of the link operation: removes x from the child
         * list of this node.
         *
         * <p><em>Running time: O(1)</em></p>
         *
         * @param  x    child to be removed from this node's child list
         * @param  min  the minimum heap node, to which x is added.
         */
        fun cut(x: Node, min: Node) {
            // remove x from childlist and decrement degree
            x.left.right = x.right
            x.right.left = x.left
            degree--
            // reset child if necessary
            if (degree == 0) {
                child = null
            } else if (child === x) {
                child = x.right
            }
            // add x to root list of heap
            x.right = min
            x.left = min.left
            min.left = x
            x.left.right = x
            // set parent[x] to nil
            x.parent = null
            // set mark[x] to false
            x.mark = false
        }

        /**
         * Make this node a child of the given parent node. All linkages
         * are updated, the degree of the parent is incremented, and
         * mark is set to false.
         *
         * @param  parent  the new parent node.
         */
        fun link(parent: Node) {
            // Note: putting this code here in Node makes it 7x faster
            // because it doesn't have to use generated accessor methods,
            // which add a lot of time when called millions of times.
            // remove this from its circular list
            left.right = right
            right.left = left
            // make this a child of x
            this.parent = parent
            if (parent.child == null) {
                parent.child = this
                right = this
                left = this
            } else {
                left = parent.child!!
                right = parent.child!!.right
                parent.child!!.right = this
                right.left = this
            }
            // increase degree[x]
            parent.degree++
            // set mark false
            mark = false
        }

        fun addToList(l: ArrayList<Node>) {
            var cur: Node = this
            do {
                l.add(cur)
                if (cur.child != null) cur.child!!.addToList(l)
                cur = cur.right
            } while (cur !== this)
        }
    }
}

fun FibonacciHeap.insert(i : Int) = this.insert(i, i as Comparable<Any?>)
fun FibonacciHeap.extractMin(): FibonacciHeap.Node? {
    val min = this.min()
    this.removeMin()
    return min
}
