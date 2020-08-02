package me.crashcringle.matrix;

import java.util.Random;
import java.util.logging.*;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.World.Environment;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	
	private EnderEyeChanger plugin;

	public PlayerListener(EnderEyeChanger plugin) 
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		
		if (plugin.useStrongholdLocation()) return; // If ender eyes point to strongholds, ignore the event
		if (e.getItem() == null ||  e.getItem().getType() != Material.ENDER_EYE) return; // If the player isn't holding an item
		if (player.getWorld().getEnvironment() != Environment.NORMAL && !plugin.getConfiguration().getAllowNetherEnd()) return;
	
		if (e.getItem().getType() == Material.ENDER_EYE)
		{
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if (e.getClickedBlock() != null) if (e.getClickedBlock().getType() == Material.END_PORTAL_FRAME) return; 

				e.setCancelled(true); // If block clicked was End Portal Frame, the event is ignored and item is used normally.
				e.setUseItemInHand(Result.DENY); // This makes sure we don't get duplicate ender eyes spawning (happens if this isn't set to 'deny')
				
				// Get nearest target location
				Location target = plugin.getLocationManager().getNearestTargetLocation(player.getLocation());
				if (target == null) return;
								
				// Get the location to spawn the ender signal at
				Location signalLocation = player.getLocation(); // The ender signal spawns at the player's eye height
				signalLocation.setY(signalLocation.getY() + player.getEyeHeight());
				
				// Spawn the ender signal + get EntityEnderSignal handle
				EnderSignal eye = e.getPlayer().getWorld().spawn(signalLocation, EnderSignal.class);
				
				
				// Make some noise :D
				World world =  e.getPlayer().getWorld();
				
				// Play sound in world (player entity, x, y, z, sound effect, sound category, balance?, volume?)
				world.playSound(signalLocation, Sound.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 100, 1);
				
				// This function sets the location that ender eyes float towards
				eye.setTargetLocation(target);
				
				if (e.getPlayer().getGameMode() == GameMode.CREATIVE) return; // If the player is in creative mode, we don't need to remove items

				EquipmentSlot slot = e.getHand();
				ItemStack item = slot == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand(): player.getInventory().getItemInMainHand();	
				if (item.getAmount() > 1) {
					item.setAmount(item.getAmount() - 1);
					if (slot == EquipmentSlot.OFF_HAND)
						player.getInventory().setItemInOffHand(item);
					else
						player.getInventory().setItemInMainHand(item);
				} else {
					item = null;
				}
			}
		}
	}
}
