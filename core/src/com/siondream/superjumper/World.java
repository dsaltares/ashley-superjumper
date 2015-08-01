/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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

package com.siondream.superjumper;

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.components.AnimationComponent;
import com.siondream.superjumper.components.BackgroundComponent;
import com.siondream.superjumper.components.BobComponent;
import com.siondream.superjumper.components.BoundsComponent;
import com.siondream.superjumper.components.CameraComponent;
import com.siondream.superjumper.components.CastleComponent;
import com.siondream.superjumper.components.CoinComponent;
import com.siondream.superjumper.components.GravityComponent;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.PlatformComponent;
import com.siondream.superjumper.components.SpringComponent;
import com.siondream.superjumper.components.SquirrelComponent;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.StateComponent;
import com.siondream.superjumper.components.TextureComponent;
import com.siondream.superjumper.systems.RenderingSystem;

public class World {
	public static final float WORLD_WIDTH = 10;
	public static final float WORLD_HEIGHT = 15 * 20;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public static final Vector2 gravity = new Vector2(0, -12);

	public final Random rand;

	public float heightSoFar;
	public int score;
	public int state;
	
	private PooledEngine engine;

	public World (PooledEngine engine) {
		this.engine = engine;
		this.rand = new Random();
	}
	
	public void create() {
		Entity bob = createBob();
		createCamera(bob);
		createBackground();
		generateLevel();

		this.heightSoFar = 0;
		this.score = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	private void generateLevel () {
		float y = PlatformComponent.HEIGHT / 2;
		float maxJumpHeight = BobComponent.JUMP_VELOCITY * BobComponent.JUMP_VELOCITY / (2 * -gravity.y);
		while (y < WORLD_HEIGHT - WORLD_WIDTH / 2) {
			int type = rand.nextFloat() > 0.8f ? PlatformComponent.TYPE_MOVING : PlatformComponent.TYPE_STATIC;
			float x = rand.nextFloat() * (WORLD_WIDTH - PlatformComponent.WIDTH) + PlatformComponent.WIDTH / 2;

			createPlatform(type, x, y);

			if (rand.nextFloat() > 0.9f && type != PlatformComponent.TYPE_MOVING) {
				createSpring(x, y + PlatformComponent.HEIGHT / 2 + SpringComponent.HEIGHT / 2);
			}

			if (y > WORLD_HEIGHT / 3 && rand.nextFloat() > 0.8f) {
				createSquirrel(x + rand.nextFloat(), y + SquirrelComponent.HEIGHT + rand.nextFloat() * 2);
			}

			if (rand.nextFloat() > 0.6f) {
				createCoin(x + MathUtils.random(-0.5f, 0.5f), y + CoinComponent.HEIGHT + rand.nextFloat() * 3);
			}

			y += (maxJumpHeight - 0.5f);
			y -= rand.nextFloat() * (maxJumpHeight / 3);
		}

		createCastle(WORLD_WIDTH / 2, y);
	}
	
	private Entity createBob() {
		Entity entity = engine.createEntity();

		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		BobComponent bob = engine.createComponent(BobComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		GravityComponent gravity = engine.createComponent(GravityComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		animation.animations.put(BobComponent.STATE_FALL, Assets.bobFall);
		animation.animations.put(BobComponent.STATE_HIT, Assets.bobHit);
		animation.animations.put(BobComponent.STATE_JUMP, Assets.bobJump);
		
		bounds.bounds.width = BobComponent.WIDTH;
		bounds.bounds.height = BobComponent.HEIGHT;
		
		position.pos.set(5.0f, 1.0f, 0.0f);
		
		state.set(BobComponent.STATE_JUMP);
		
		entity.add(animation);
		entity.add(bob);
		entity.add(bounds);
		entity.add(gravity);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);
		
		engine.addEntity(entity);
		
		return entity;
	}
	
	private void createPlatform(int type, float x, float y) {
		Entity entity = new Entity();//engine.createEntity();
		
		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		PlatformComponent platform = engine.createComponent(PlatformComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		animation.animations.put(PlatformComponent.STATE_NORMAL, Assets.platform);
		animation.animations.put(PlatformComponent.STATE_PULVERIZING, Assets.breakingPlatform);
		
		bounds.bounds.width = PlatformComponent.WIDTH;
		bounds.bounds.height = PlatformComponent.HEIGHT;
		
		position.pos.set(x, y, 1.0f);
		
		state.set(PlatformComponent.STATE_NORMAL);
		
		platform.type = type;
		if (type == PlatformComponent.TYPE_MOVING) {
			movement.velocity.x = rand.nextBoolean() ? PlatformComponent.VELOCITY : -PlatformComponent.VELOCITY;
		}
		
		entity.add(animation);
		entity.add(platform);
		entity.add(bounds);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);
		
		engine.addEntity(entity);
	}
	
	private void createSpring(float x, float y) {
		Entity entity = engine.createEntity();
		
		SpringComponent spring = engine.createComponent(SpringComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		bounds.bounds.width = SpringComponent.WIDTH;
		bounds.bounds.height = SpringComponent.HEIGHT;
		
		position.pos.set(x, y, 2.0f);
		
		texture.region = Assets.spring;
		
		entity.add(spring);
		entity.add(bounds);
		entity.add(position);
		entity.add(texture);
		
		engine.addEntity(entity);
	}
	
	private void createSquirrel(float x, float y) {
		Entity entity = engine.createEntity();
		
		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		SquirrelComponent squirrel = engine.createComponent(SquirrelComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		MovementComponent movement = engine.createComponent(MovementComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		movement.velocity.x = rand.nextFloat() > 0.5f ? SquirrelComponent.VELOCITY : -SquirrelComponent.VELOCITY;
		
		animation.animations.put(SquirrelComponent.STATE_NORMAL, Assets.squirrelFly);
		
		bounds.bounds.width = SquirrelComponent.WIDTH;
		bounds.bounds.height = SquirrelComponent.HEIGHT;
		
		position.pos.set(x, y, 2.0f);
		
		state.set(SquirrelComponent.STATE_NORMAL);
		
		entity.add(animation);
		entity.add(squirrel);
		entity.add(bounds);
		entity.add(movement);
		entity.add(position);
		entity.add(state);
		entity.add(texture);
		
		engine.addEntity(entity);
	}
	
	private void createCoin(float x, float y) {
		Entity entity = engine.createEntity();
		
		AnimationComponent animation = engine.createComponent(AnimationComponent.class);
		CoinComponent coin = engine.createComponent(CoinComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		StateComponent state = engine.createComponent(StateComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		animation.animations.put(CoinComponent.STATE_NORMAL, Assets.coinAnim);
		
		bounds.bounds.width = CoinComponent.WIDTH;
		bounds.bounds.height = CoinComponent.HEIGHT;
		
		position.pos.set(x, y, 3.0f);
		
		state.set(CoinComponent.STATE_NORMAL);
		
		entity.add(coin);
		entity.add(bounds);
		entity.add(position);
		entity.add(texture);
		entity.add(animation);
		entity.add(state);
		
		engine.addEntity(entity);
	}
	
	private void createCastle(float x, float y) {
		Entity entity = engine.createEntity();
		
		CastleComponent castle = engine.createComponent(CastleComponent.class);
		BoundsComponent bounds = engine.createComponent(BoundsComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		bounds.bounds.width = CastleComponent.WIDTH;
		bounds.bounds.height = CastleComponent.HEIGHT;
		
		position.pos.set(x, y, 2.0f);
		
		texture.region = Assets.castle;
		
		entity.add(castle);
		entity.add(bounds);
		entity.add(position);
		entity.add(texture);
		
		engine.addEntity(entity);
	}
	
	private void createCamera(Entity target) {
		Entity entity = engine.createEntity();
		
		CameraComponent camera = new CameraComponent();
		camera.camera = engine.getSystem(RenderingSystem.class).getCamera();
		camera.target = target;
		
		entity.add(camera);
		
		engine.addEntity(entity);
	}
	
	private void createBackground() {
		Entity entity = engine.createEntity();
		
		BackgroundComponent background = engine.createComponent(BackgroundComponent.class);
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		texture.region = Assets.backgroundRegion;
		
		entity.add(background);
		entity.add(position);
		entity.add(texture);
		
		engine.addEntity(entity);
	}
}
