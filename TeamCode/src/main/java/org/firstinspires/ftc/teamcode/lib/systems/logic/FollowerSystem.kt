package org.firstinspires.ftc.teamcode.lib.systems.logic

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.math.Vector2

abstract class FollowerSystem : EntitySystem(), Steerable<Vector2>