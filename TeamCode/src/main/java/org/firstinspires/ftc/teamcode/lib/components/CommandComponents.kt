package org.firstinspires.ftc.teamcode.lib.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

/**
 * Experimental and possibly code-breaking "API" which allows custom behavior for a single entity.
 * Really should not be used, but left open for modularity and as a fail-deadly (i.e. it will break stuff) contingency.
 *
 */
interface CommandComponent : Component {
    /**
     *
     */
    fun execute(entity: Entity)
}