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

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.siondream.superjumper.World;
import com.siondream.superjumper.components.BobComponent;
import com.siondream.superjumper.components.BoundsComponent;
import com.siondream.superjumper.components.CastleComponent;
import com.siondream.superjumper.components.CoinComponent;
import com.siondream.superjumper.components.MovementComponent;
import com.siondream.superjumper.components.PlatformComponent;
import com.siondream.superjumper.components.StateComponent;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.SpringComponent;
import com.siondream.superjumper.components.SquirrelComponent;

public class CollisionSystem extends EntitySystem {
	private ComponentMapper<BoundsComponent> bm;
	private ComponentMapper<MovementComponent> mm;
	private ComponentMapper<StateComponent> sm;
	private ComponentMapper<TransformComponent> tm;
	
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
	private ImmutableArray<Entity> bobs;
	private ImmutableArray<Entity> coins;
	private ImmutableArray<Entity> squirrels;
	private ImmutableArray<Entity> springs;
	private ImmutableArray<Entity> castles;
	private ImmutableArray<Entity> platforms;
	
	public CollisionSystem(World world, CollisionListener listener) {
		this.world = world;
		this.listener = listener;
		
		bm = ComponentMapper.getFor(BoundsComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
		tm = ComponentMapper.getFor(TransformComponent.class);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
		
		bobs = engine.getEntitiesFor(Family.all(BobComponent.class, BoundsComponent.class, TransformComponent.class, StateComponent.class).get());
		coins = engine.getEntitiesFor(Family.all(CoinComponent.class, BoundsComponent.class).get());
		squirrels = engine.getEntitiesFor(Family.all(SquirrelComponent.class, BoundsComponent.class).get());
		springs = engine.getEntitiesFor(Family.all(SpringComponent.class, BoundsComponent.class, TransformComponent.class).get());
		castles = engine.getEntitiesFor(Family.all(CastleComponent.class, BoundsComponent.class).get());
		platforms = engine.getEntitiesFor(Family.all(PlatformComponent.class, BoundsComponent.class, TransformComponent.class).get());
	}
	
	@Override
	public void update(float deltaTime) {
		BobSystem bobSystem = engine.getSystem(BobSystem.class);
		PlatformSystem platformSystem = engine.getSystem(PlatformSystem.class);
		
		for (int i = 0; i < bobs.size(); ++i) {
			Entity bob = bobs.get(i);
			
			StateComponent bobState = sm.get(bob);
			
			if (bobState.get() == BobComponent.STATE_HIT) {
				continue;
			}
			
			MovementComponent bobMov = mm.get(bob);
			BoundsComponent bobBounds = bm.get(bob);
			
			if (bobMov.velocity.y < 0.0f) {
				TransformComponent bobPos = tm.get(bob);
				
				for (int j = 0; j < platforms.size(); ++j) {
					Entity platform = platforms.get(j);
					
					TransformComponent platPos = tm.get(platform);
					
					if (bobPos.pos.y > platPos.pos.y) {
						BoundsComponent platBounds = bm.get(platform);
						
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
				
				for (int j = 0; j < springs.size(); ++j) {
					Entity spring = springs.get(j);
					
					TransformComponent springPos = tm.get(spring);
					BoundsComponent springBounds = bm.get(spring);
					
					if (bobPos.pos.y > springPos.pos.y) {
						if (bobBounds.bounds.overlaps(springBounds.bounds)) {
							bobSystem.hitSpring(bob);
							listener.highJump();
						}
					} 
				}
			};

			for (int j = 0; j < squirrels.size(); ++j) {
				Entity squirrel = squirrels.get(j);
				
				BoundsComponent squirrelBounds = bm.get(squirrel);
				
				if (squirrelBounds.bounds.overlaps(bobBounds.bounds)) {
					bobSystem.hitSquirrel(bob);
					listener.hit();
				}
			}
			
			for (int j = 0; j < coins.size(); ++j) {
				Entity coin = coins.get(j);
				
				BoundsComponent coinBounds = bm.get(coin);
				
				if (coinBounds.bounds.overlaps(bobBounds.bounds)) {
					engine.removeEntity(coin);
					listener.coin();
					world.score += CoinComponent.SCORE;
				}
			}
			
			for (int j = 0; j < castles.size(); ++j) {
				Entity castle = castles.get(j);
				
				BoundsComponent castleBounds = bm.get(castle);
				
				if (castleBounds.bounds.overlaps(bobBounds.bounds)) {
					world.state = World.WORLD_STATE_NEXT_LEVEL;
				}
			}
		}
	}
}
