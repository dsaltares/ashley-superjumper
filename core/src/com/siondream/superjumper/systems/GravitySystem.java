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
import com.siondream.superjumper.components.GravityComponent;
import com.siondream.superjumper.components.MovementComponent;

public class GravitySystem extends IteratingSystem {
	private ComponentMapper<MovementComponent> mm;
	
	public GravitySystem() {
		super(Family.all(GravityComponent.class, MovementComponent.class).get());
		
		mm = ComponentMapper.getFor(MovementComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		MovementComponent mov = mm.get(entity);
		mov.velocity.add(World.gravity.x * deltaTime, World.gravity.y * deltaTime);
	}
}
