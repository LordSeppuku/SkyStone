package org.firstinspires.ftc.teamcode.lib.factory

import com.badlogic.ashley.core.Engine
import ktx.ashley.EngineEntity
import ktx.ashley.entity

typealias EntityCreator = (iteration: Int) -> EngineEntity.() -> Unit

/**
 * Sealed class hierarchy providing easy peasy handling of the [EntityFactory.times] lambda.
 */
sealed class IntProducer {

    /**
     * Lambda that produces an [Int] value.
     */
    abstract val produce: () -> Int
}

/**
 * Wraps around integer value to be compatible with [IntProducer].
 *
 * @property number number to be passed through [IntProducer] through invocation.
 */
data class NoProducer(val number: Int) : IntProducer() {
    override val produce = { number }
}

/**
 * Produces [Int] value based on a lambda.
 */
data class LambdaProducer(override val produce: () -> Int) : IntProducer()

/**
 * Simple wrapper for a function that creates an entity and its components and how many times to do so.
 *
 * @property times function to determine how many times to run the lambda creator, thus how many entities are created, defaults to 1
 * @property creator simple lambda that creates an entity based on which entity.
 */
data class EntityFactory(
        val times: IntProducer = NoProducer(0),
        val creator: EntityCreator
) {

    /**
     * Creates [times] amount of entities based on [creator] lambda in a [List] for further use.
     */
    fun produce(engine: Engine) {
        for (i in 0 until times.produce()) engine.entity(creator(i))
    }

}