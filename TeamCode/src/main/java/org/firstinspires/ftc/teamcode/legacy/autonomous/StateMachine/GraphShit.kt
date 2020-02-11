package org.firstinspires.ftc.teamcode.legacy.autonomous.StateMachine

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class Graph<T> {
    val adjacencyMap: HashMap<T, HashSet<T>> = HashMap()

    fun addEdge(sourceVertex: T, destinationVertex: T) {
        // Add edge to source vertex / node.
        adjacencyMap
                .computeIfAbsent(sourceVertex) { HashSet() }
                .add(destinationVertex)
        // Add edge to destination vertex / node.
        adjacencyMap
                .computeIfAbsent(destinationVertex) { HashSet() }
                .add(sourceVertex)
    }

    override fun toString(): String = StringBuffer().apply {
        for (key in adjacencyMap.keys) {
            append("$key -> ")
            append(adjacencyMap[key]?.joinToString(", ", "[", "]\n"))
        }
    }.toString()
}

fun <T> Graph<T>.bfs(startNode: T, maxDepth: Int = Int.MAX_VALUE) = breadthFirstTraversal(this, startNode, maxDepth)

class BFS<T>(val graph: Graph<T>) {
    inner class VisitedMap {
        val traversalList = mutableListOf<T>()

        val visitedMap = mutableMapOf<T, Boolean>().apply {
            for (node in graph.adjacencyMap.keys) this[node] = false
        }

        fun isNotVisited(node: T): Boolean = !visitedMap[node]!!

        fun markVisitedAndAddToTraversalList(node: T) {
            visitedMap[node] = true
            traversalList.add(node)
        }
    }

    inner class Queue {
        val deck: Deque<T> = ArrayDeque<T>()
        fun add(node: T, depth: Int) {
            // Add to the tail of the queue.
            deck.add(node)
            // Record the depth of this node.
            depthMap[node] = depth
        }

        fun addAdjacentNodes(currentNode: T, depth: Int) {
            for (node in graph.adjacencyMap[currentNode]!!) {
                add(node, depth)
            }
        }

        fun isNotEmpty() = deck.isNotEmpty()
        fun remove() = deck.remove()
    }

    val visitedMap = VisitedMap()
    // Keep track of the depth of each node, so that more than maxDepth nodes
    // aren't visited.
    val depthMap = mutableMapOf<T, Int>().apply {
        for (node in graph.adjacencyMap.keys) this[node] = Int.MAX_VALUE
    }

    inline operator fun <reified U : T> T.get(other: U): List<T> {
        val queue = Queue()

        //
        // Algorithm implementation.
        //

        // Initial step -> add the startNode to the queue.
        queue.add(this, /* depth= */0)

        // Traverse the graph
        while (queue.isNotEmpty()) {
            // Remove the item at the head of the queue.
            val currentNode = queue.remove()
            val currentDepth = depthMap[currentNode]!!

            if (currentNode is U) break

            if (currentDepth <= Int.MAX_VALUE) {
                if (visitedMap.isNotVisited(currentNode)) {
                    // Mark the current node visited and add to traversal list.
                    visitedMap.markVisitedAndAddToTraversalList(currentNode)
                    // Add nodes in the adjacency map.
                    queue.addAdjacentNodes(currentNode, /* depth= */currentDepth + 1)
                }
            }
        }
        return visitedMap.traversalList.toList()
    }
}

fun <T> breadthFirstTraversal(graph: Graph<T>,
                              startNode: T,
                              maxDepth: Int = Int.MAX_VALUE): List<T> {
    //
    // Setup.
    //

    // Mark all the vertices / nodes as not visited. And keep track of sequence
    // in which nodes are visited, for return value.
    class VisitedMap {
        val traversalList = mutableListOf<T>()

        val visitedMap = mutableMapOf<T, Boolean>().apply {
            for (node in graph.adjacencyMap.keys) this[node] = false
        }

        fun isNotVisited(node: T): Boolean = !visitedMap[node]!!

        fun markVisitedAndAddToTraversalList(node: T) {
            visitedMap[node] = true
            traversalList.add(node)
        }
    }

    val visitedMap = VisitedMap()

    // Keep track of the depth of each node, so that more than maxDepth nodes
    // aren't visited.
    val depthMap = mutableMapOf<T, Int>().apply {
        for (node in graph.adjacencyMap.keys) this[node] = Int.MAX_VALUE
    }

    // Create a queue for BFS.
    class Queue {
        val deck: Deque<T> = ArrayDeque<T>()
        fun add(node: T, depth: Int) {
            // Add to the tail of the queue.
            deck.add(node)
            // Record the depth of this node.
            depthMap[node] = depth
        }

        fun addAdjacentNodes(currentNode: T, depth: Int) {
            for (node in graph.adjacencyMap[currentNode]!!) {
                add(node, depth)
            }
        }

        fun isNotEmpty() = deck.isNotEmpty()
        fun remove() = deck.remove()
    }

    val queue = Queue()

    //
    // Algorithm implementation.
    //

    // Initial step -> add the startNode to the queue.
    queue.add(startNode, /* depth= */0)

    // Traverse the graph
    while (queue.isNotEmpty()) {
        // Remove the item at the head of the queue.
        val currentNode = queue.remove()
        val currentDepth = depthMap[currentNode]!!

        if (currentDepth <= maxDepth) {
            if (visitedMap.isNotVisited(currentNode)) {
                // Mark the current node visited and add to traversal list.
                visitedMap.markVisitedAndAddToTraversalList(currentNode)
                // Add nodes in the adjacency map.
                queue.addAdjacentNodes(currentNode, /* depth= */currentDepth + 1)
            }
        }

    }

    return visitedMap.traversalList.toList()
}
