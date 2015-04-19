/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.siondream.superjumper.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.siondream.superjumper.World;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.SquirrelComponent;

public class SquirrelSystem extends IteratingSystem {

	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	public SquirrelSystem() {
		super(Family.all(SquirrelComponent.class,
							TransformComponent.class,
							MovementComponent.class).get());
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		MovementComponent mov = mm.get(entity);
		
		if (t.pos.x < SquirrelComponent.WIDTH * 0.5f) {
			t.pos.x = SquirrelComponent.WIDTH * 0.5f;
			mov.velocity.x = SquirrelComponent.VELOCITY;
		}
		if (t.pos.x > World.WORLD_WIDTH - SquirrelComponent.WIDTH * 0.5f) {
			t.pos.x = World.WORLD_WIDTH - SquirrelComponent.WIDTH * 0.5f;
			mov.velocity.x = -SquirrelComponent.VELOCITY;
		}
		
		t.scale.x = mov.velocity.x < 0.0f ? Math.abs(t.scale.x) * -1.0f : Math.abs(t.scale.x);
	}
}
