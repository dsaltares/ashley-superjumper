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
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.siondream.superjumper.World;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.PlatformComponent;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.StateComponent;

public class PlatformSystem extends IteratingSystem {
	private static final Family family = Family.all(PlatformComponent.class,
													   StateComponent.class,
												       TransformComponent.class,
													   MovementComponent.class).get();
	private Engine engine;
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	private ComponentMapper<PlatformComponent> pm;
	private ComponentMapper<StateComponent> sm;
	
	public PlatformSystem() {
		super(family);
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		pm = ComponentMapper.getFor(PlatformComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		this.engine = engine;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		PlatformComponent platform = pm.get(entity);
		
		if (platform.type == PlatformComponent.TYPE_MOVING) {
			TransformComponent pos = tm.get(entity);
			MovementComponent mov = mm.get(entity);

			if (pos.pos.x < PlatformComponent.WIDTH / 2) {
				mov.velocity.x = -mov.velocity.x;
				pos.pos.x = PlatformComponent.WIDTH / 2;
			}
			if (pos.pos.x > World.WORLD_WIDTH - PlatformComponent.WIDTH / 2) {
				mov.velocity.x = -mov.velocity.x;
				pos.pos.x = World.WORLD_WIDTH - PlatformComponent.WIDTH / 2;
			}
		}
		
		StateComponent state = sm.get(entity);
		
		if (state.get() == PlatformComponent.STATE_PULVERIZING &&
			state.time > PlatformComponent.PULVERIZE_TIME) {
			
			engine.removeEntity(entity);
		}
	}
	
	public void pulverize (Entity entity) {
		if (family.matches(entity)) {
			StateComponent state = sm.get(entity);
			MovementComponent mov = mm.get(entity);
			
			state.set(PlatformComponent.STATE_PULVERIZING);
			mov.velocity.x = 0;
		}
	}
}
