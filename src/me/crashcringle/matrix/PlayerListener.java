package me.crashcringle.matrix;

import java.util.Random;
import java.util.logging.*;

import org.bukkit.Bukkit;
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
		boolean chaosSouls = false;
		Player player = e.getPlayer();
		
		if (plugin.useStrongholdLocation()) return; // If ender eyes point to strongholds, ignore the event
		if (e.getItem() == null ||  e.getItem().getType() != Material.ENDER_EYE || e.getItem().getItemMeta().getLore() == null || e.getItem().getItemMeta().getLore().size() < 3) return; // If the player isn't holding an item
		if (player.getWorld().getEnvironment() != Environment.NORMAL && !plugin.getConfiguration().getAllowNetherEnd()) return;
		// Get nearest target location
		Location target = plugin.getLocationManager().getNearestTargetLocation(player.getLocation());
		if (target == null) return;
		
		EquipmentSlot slot = e.getHand();
		ItemStack item = slot == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand(): player.getInventory().getItemInMainHand();
		if (item.getItemMeta().getLore().get(3).contains("KAGJEABGJKBGKHA") && item.getItemMeta().getDisplayName().contains("Soul of Madness")) {
			chaosSouls = true;
		}	
		if (chaosSouls)
		{
			if (item.getAmount() >= 10) {
				chaosSouls = true;
				item.setAmount(item.getAmount() - 8);
				if (slot == EquipmentSlot.OFF_HAND)
					player.getInventory().setItemInOffHand(item);
				else
					player.getInventory().setItemInMainHand(item);
			} else {
				if (slot == EquipmentSlot.OFF_HAND)
					player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				else
					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
	       		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mgive " + player.getName() + " Cheese " + item.getAmount());
				if (((int) Math.random() * 10) % 2 == 0)
					player.sendMessage("§aNothing but a puff of smoke");
				else
					player.sendMessage("§bNothing but a puff of smoke");
				item = null;
				chaosSouls = false;
			}
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if (e.getClickedBlock() != null) if (e.getClickedBlock().getType() == Material.END_PORTAL_FRAME) return; 

				e.setCancelled(true); // If block clicked was End Portal Frame, the event is ignored and item is used normally.
				e.setUseItemInHand(Result.DENY); // This makes sure we don't get duplicate ender eyes spawning (happens if this isn't set to 'deny')
				
								
				// Get the location to spawn the ender signal at
				Location signalLocation = player.getLocation(); // The ender signal spawns at the player's eye height
				signalLocation.setY(signalLocation.getY() + player.getEyeHeight());
				
				// Spawn the ender signal + get EntityEnderSignal handle
				EnderSignal eye = e.getPlayer().getWorld().spawn(signalLocation, EnderSignal.class);
				// Make some noise :D
				World world =  e.getPlayer().getWorld();
				
				// Play sound in world (player entity, x, y, z, sound effect, sound category, balance?, volume?)
				world.playSound(signalLocation, Sound.ENTITY_EVOKER_PREPARE_WOLOLO, SoundCategory.NEUTRAL, 100, 2);
				// This function sets the location that ender eyes float towards
				eye.setTargetLocation(target);
				
				eye.setDropItem(false);

			}
		}
	}
}
