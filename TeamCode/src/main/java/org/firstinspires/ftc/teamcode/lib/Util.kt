package org.firstinspires.ftc.teamcode.lib

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem

/**
 * Remove all systems registered in [Engine]. Seriously.
 */
fun Engine.removeAllSystems() {
    for (system in systems) removeSystem(system)
}

/**
 * Add all [EntitySystem] contained in a list at certain priority
 *
 * NOTE: Priority levels 0-7 have been pre-designated, however, it's up to you if you want to "muck" it up.
 *
 * @param systems List of EntitySystems to add
 * @param priority engine priority of all EntitySystems added. Default is 69 (haha)
 */
fun Engine.addSystemsFromList(systems: List<EntitySystem>, priority: Int = 69) {
    for (system in systems) {
        system.priority = priority
        addSystem(system)
    }
}

fun Engine.addEntitiesFromList(entities: List<Entity>) {
    for (entity in entities) addEntity(entity)
}
