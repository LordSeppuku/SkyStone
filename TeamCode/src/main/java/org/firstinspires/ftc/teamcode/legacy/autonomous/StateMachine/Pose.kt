package org.firstinspires.ftc.teamcode.legacy.autonomous.StateMachine

import org.firstinspires.ftc.teamcode.legacy.lib.Distance
import org.firstinspires.ftc.teamcode.legacy.lib.DriveToPositionCommand
import org.firstinspires.ftc.teamcode.legacy.lib.Rotation
import java.util.*
import kotlin.collections.HashSet

sealed class Pose : State, Comparable<Pose> {
    open val connections: Map<Pose, DriveToPositionCommand> = mapOf()
    val neighbors
        get() = connections.keys

    /*
// Returns list of poses on path to target pose (target inclusive, start exclusive), first pose is next pose on path
fun bfs(targetPose: Pose): List<Pose> {
val queue: Queue<Pose> = ArrayDeque<Pose>()
val poses = poses().map { it to false }.toMap().toMutableMap()
val pred: MutableMap<Pose, Pose?> = poses.keys.map { it to null }.toMap().toMutableMap()
poses[this] = true
queue.add(this)
run nerd@{ while (queue.isNotEmpty()) {
    with(queue.poll()) {
        if (this == targetPose) return@nerd
        for (connection in connections) {
            poses[connection.first] = true
            pred[connection.first] = this
            queue.add(connection.first)
        }
    }
} }

val path = mutablemapOf<Pose>()
var crawl = targetPose
while(pred[crawl] != null && pred[crawl] != this) {
    path.add(pred[crawl]!!)
    crawl = pred[crawl]!!
}

return path.toList()
}

*/

    override fun compareTo(other: Pose): Int = poses.indexOf(this).compareTo(poses.indexOf(other))

    operator fun get(other: Pose): List<Pose> {
        val nextToVisit: Queue<MutableList<Pose>> = LinkedList()
        val visited: MutableSet<Pose> = HashSet()
        val currentPath: MutableList<Pose> = mutableListOf(this)

        nextToVisit.offer(currentPath)
        while (nextToVisit.isNotEmpty()) {
            val path = nextToVisit.poll()
            val node = path.first()
            println(path.toString() + node.toString())
            if (node == other) return path.toList().reversed()
            if (visited.contains(node)) continue
            visited.add(node)
            node.neighbors.forEach {
                nextToVisit.add(mutableListOf(it, *path.toTypedArray()))
            }
        }
        return listOf(Bitch)
    }

    companion object {
        val poses by lazy {
            Pose::class.sealedSubclasses.map { it.objectInstance as Pose }
        }
    }

    object Bitch : Pose()

    object StartAlpha : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                Discerning to DriveToPositionCommand(lateral = Distance(6.0))
        )
    }

    object StartBeta : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                Crossways to DriveToPositionCommand(horizontal = Distance(26.25), rotation = Rotation(-90.0))
        )
    }

    object Discerning : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                ParkBeta to DriveToPositionCommand(lateral = Distance(-34.5), rotation = Rotation(-90.0)),
                StartAlpha to DriveToPositionCommand(lateral = Distance(-6.0), rotation = Rotation(0.0)),
                QuarryAlpha to DriveToPositionCommand(horizontal = Distance(20.25), rotation = Rotation(-90.0))
                //ParkUnprime, StartA, StageA
        )
    }

    object QuarryAlpha : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                QuarryBeta to DriveToPositionCommand(lateral = Distance(24.25), rotation = Rotation(-90.0)),
                Discerning to DriveToPositionCommand(lateral = Distance(-20.25), rotation = Rotation(0.0)),
                ParkAlpha to DriveToPositionCommand(lateral = Distance(-23.25), rotation = Rotation(-90.0))
                //StageB, Discerning, ParkPrime
        )
    }

    object QuarryBeta : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                QuarryAlpha to DriveToPositionCommand(lateral = Distance(-24.25), rotation = Rotation(-90.0))
        )
    }

    object ParkAlpha : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                ParkBeta to DriveToPositionCommand(horizontal = Distance(-20.25), rotation = Rotation(-90.0)),
                QuarryAlpha to DriveToPositionCommand(lateral = Distance(24.25), rotation = Rotation(-90.0)),
                Crossways to DriveToPositionCommand(lateral = Distance(-23.25), rotation = Rotation(-90.0))
                //ParkUnprime, StageA, Crossways
        )
    }

    object ParkBeta : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                Discerning to DriveToPositionCommand(horizontal = Distance(-20.25), rotation = Rotation(0.0)),
                ParkAlpha to DriveToPositionCommand(horizontal = Distance(20.25), rotation = Rotation(-90.0))
                //Discerning, ParkPrime
        )
    }

    object Crossways : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                StartBeta to DriveToPositionCommand(lateral = Distance(-26.25), rotation = Rotation(0.0)),
                ParkAlpha to DriveToPositionCommand(lateral = Distance(23.25), rotation = Rotation(-90.0)),
                FoundationWidth to DriveToPositionCommand(horizontal = Distance(21.5), rotation = Rotation(-90.0)),
                FoundationLateral to DriveToPositionCommand(horizontal = Distance(-8.625), rotation = Rotation(-180.0))
                //StartB, ParkPrime, FoundationWidth, FoundationLateral
        )
    }

    object FoundationWidth : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                Crossways to DriveToPositionCommand(horizontal = Distance(-21.5), rotation = Rotation(-90.0))
                //Crossways
        )
    }

    object FoundationLateral : Pose() {
        override val connections: Map<Pose, DriveToPositionCommand> = mapOf(
                Crossways to DriveToPositionCommand(lateral = Distance(8.625), rotation = Rotation(-90.0))
                //Crossways
        )
    }
}

typealias Connection = Pair<Pose, DriveToPositionCommand>