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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.TransformComponent;

public class MovementSystem extends IteratingSystem {
	private Vector2 tmp = new Vector2();
	private boolean pause = false;
	
	public MovementSystem() {
		super(Family.getFamilyFor(TransformComponent.class, MovementComponent.class));
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = entity.getComponent(TransformComponent.class);
		MovementComponent mov = entity.getComponent(MovementComponent.class);
		
		tmp.set(mov.accel).scl(deltaTime);
		mov.velocity.add(tmp);
		
		tmp.set(mov.velocity).scl(deltaTime);
		pos.pos.add(tmp.x, tmp.y, 0.0f);
	}
	
	@Override
	public boolean checkProcessing() {
		return !pause;
	}
	
	public void pause(boolean pause) {
		this.pause = pause;
	}
}
