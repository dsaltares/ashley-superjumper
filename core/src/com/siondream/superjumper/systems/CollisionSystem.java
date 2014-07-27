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

import java.util.Random;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableIntMap;
import com.siondream.superjumper.World;
import com.siondream.superjumper.components.BobComponent;
import com.siondream.superjumper.components.BoundsComponent;
import com.siondream.superjumper.components.CastleComponent;
import com.siondream.superjumper.components.CoinComponent;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.PlatformComponent;
import com.siondream.superjumper.components.StateComponent;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.RemovalComponent;
import com.siondream.superjumper.components.SpringComponent;
import com.siondream.superjumper.components.SquirrelComponent;

public class CollisionSystem extends EntitySystem {
	private boolean pause = false;
	
	public static interface CollisionListener {		
		public void jump ();
		public void highJump ();
		public void hit ();
		public void coin ();
	}

	private Engine engine;
	private World world;
	private CollisionListener listener;
	private Random rand = new Random();
	private ImmutableIntMap<Entity> bobs;
	private ImmutableIntMap<Entity> coins;
	private ImmutableIntMap<Entity> squirrels;
	private ImmutableIntMap<Entity> springs;
	private ImmutableIntMap<Entity> castles;
	private ImmutableIntMap<Entity> platforms;
	
	public CollisionSystem(World world, CollisionListener listener) {
		this.world = world;
		this.listener = listener;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		
		bobs = engine.getEntitiesFor(Family.getFamilyFor(BobComponent.class, BoundsComponent.class, TransformComponent.class, StateComponent.class));
		coins = engine.getEntitiesFor(Family.getFamilyFor(CoinComponent.class, BoundsComponent.class));
		squirrels = engine.getEntitiesFor(Family.getFamilyFor(SquirrelComponent.class, BoundsComponent.class));
		springs = engine.getEntitiesFor(Family.getFamilyFor(SpringComponent.class, BoundsComponent.class, TransformComponent.class));
		castles = engine.getEntitiesFor(Family.getFamilyFor(CastleComponent.class, BoundsComponent.class));
		platforms = engine.getEntitiesFor(Family.getFamilyFor(PlatformComponent.class, BoundsComponent.class, TransformComponent.class));
	}
	
	@Override
	public void update(float deltaTime) {
		BobSystem bobSystem = engine.getSystem(BobSystem.class);
		PlatformSystem platformSystem = engine.getSystem(PlatformSystem.class);
		
		for (Entity bob : bobs.values()) {
			StateComponent bobState = bob.getComponent(StateComponent.class);
			
			if (bobState.get() == BobComponent.STATE_HIT) {
				continue;
			}
			
			MovementComponent bobMov = bob.getComponent(MovementComponent.class);
			BoundsComponent bobBounds = bob.getComponent(BoundsComponent.class);
			
			if (bobMov.velocity.y < 0.0f) {
				TransformComponent bobPos = bob.getComponent(TransformComponent.class);
				
				for (Entity platform : platforms.values()) {
					TransformComponent platPos = platform.getComponent(TransformComponent.class);
					
					if (bobPos.pos.y > platPos.pos.y) {
						BoundsComponent platBounds = platform.getComponent(BoundsComponent.class);
						
						if (bobBounds.bounds.overlaps(platBounds.bounds)) {
							bobSystem.hitPlatform(bob);
							listener.jump();
							if (rand.nextFloat() > 0.5f) {
								platformSystem.pulverize(platform);
							}
							
							break;
						}
					}
				}
				
				for (Entity spring : springs.values()) {
					TransformComponent springPos = spring.getComponent(TransformComponent.class);
					BoundsComponent springBounds = spring.getComponent(BoundsComponent.class);
					
					if (bobPos.pos.y > springPos.pos.y) {
						if (bobBounds.bounds.overlaps(springBounds.bounds)) {
							bobSystem.hitSpring(bob);
							listener.highJump();
						}
					} 
				}
			};

			for (Entity squirrel : squirrels.values()) {
				BoundsComponent squirrelBounds = squirrel.getComponent(BoundsComponent.class);
				
				if (squirrelBounds.bounds.overlaps(bobBounds.bounds)) {
					bobSystem.hitSquirrel(bob);
					listener.hit();
				}
			}
			
			for (Entity coin : coins.values()) {
				BoundsComponent coinBounds = coin.getComponent(BoundsComponent.class);
				
				if (coinBounds.bounds.overlaps(bobBounds.bounds)) {
					coin.add(new RemovalComponent());
					listener.coin();
					world.score += CoinComponent.SCORE;
				}
			}
			
			for (Entity castle : castles.values()) {
				BoundsComponent castleBounds = castle.getComponent(BoundsComponent.class);
				
				if (castleBounds.bounds.overlaps(bobBounds.bounds)) {
					world.state = World.WORLD_STATE_NEXT_LEVEL;
				}
			}
		}
	}
	
	@Override
	public boolean checkProcessing() {
		return !pause;
	}
	
	public void pause(boolean pause) {
		this.pause = pause;
	}
}
