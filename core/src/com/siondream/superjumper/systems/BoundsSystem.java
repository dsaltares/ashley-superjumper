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
import com.siondream.superjumper.components.BoundsComponent;
import com.siondream.superjumper.components.TransformComponent;

public class BoundsSystem extends IteratingSystem {
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<BoundsComponent> bm;
	
	public BoundsSystem() {
		super(Family.all(BoundsComponent.class, TransformComponent.class).get());
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		bm = ComponentMapper.getFor(BoundsComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = tm.get(entity);
		BoundsComponent bounds = bm.get(entity);
		
		bounds.bounds.x = pos.pos.x - bounds.bounds.width * 0.5f;
		bounds.bounds.y = pos.pos.y - bounds.bounds.height * 0.5f;
	}
}
