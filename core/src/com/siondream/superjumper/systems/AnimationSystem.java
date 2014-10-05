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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.siondream.superjumper.components.AnimationComponent;
import com.siondream.superjumper.components.StateComponent;
import com.siondream.superjumper.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {
	private ComponentMapper<TextureComponent> tm;
	private ComponentMapper<AnimationComponent> am;
	private ComponentMapper<StateComponent> sm;
	
	public AnimationSystem() {
		super(Family.getFor(TextureComponent.class,
							AnimationComponent.class,
							StateComponent.class));
		
		tm = ComponentMapper.getFor(TextureComponent.class);
		am = ComponentMapper.getFor(AnimationComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		long id = entity.getId();
		TextureComponent tex = tm.get(entity);
		AnimationComponent anim = am.get(entity);
		StateComponent state = sm.get(entity);
		
		Animation animation = anim.animations.get(state.get());
		
		if (animation != null) {
			tex.region = animation.getKeyFrame(state.time); 
		}
		
		state.time += deltaTime;
	}
}
