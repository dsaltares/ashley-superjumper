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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.siondream.superjumper.components.BackgroundComponent;
import com.siondream.superjumper.components.TransformComponent;

public class BackgroundSystem extends IteratingSystem {
	private OrthographicCamera camera;
	private ComponentMapper<TransformComponent> tm;
	
	public BackgroundSystem() {
		super(Family.all(BackgroundComponent.class).get());
		tm = ComponentMapper.getFor(TransformComponent.class);
	}
	
	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent t = tm.get(entity);
		t.pos.set(camera.position.x, camera.position.y, 10.0f);
	}
}
