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
import com.siondream.superjumper.components.CameraComponent;
import com.siondream.superjumper.components.TransformComponent;

public class CameraSystem extends IteratingSystem {
	
	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<CameraComponent> cm;
	
	public CameraSystem() {
		super(Family.getFor(CameraComponent.class));
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		cm = ComponentMapper.getFor(CameraComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		CameraComponent cam = cm.get(entity);
		
		if (cam.target == null) {
			return;
		}
		
		TransformComponent target = tm.get(cam.target);
		
		if (target == null) {
			return;
		}
		
		cam.camera.position.y = Math.max(cam.camera.position.y, target.pos.y);
	}
}
