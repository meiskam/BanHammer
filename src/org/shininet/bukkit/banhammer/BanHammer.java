/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.shininet.bukkit.banhammer;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class BanHammer extends JavaPlugin implements Listener {

	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents((Listener)this, this);
	}
 
	@Override
	public void onDisable() {
		EntityDamageByEntityEvent.getHandlerList().unregister((Listener)this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (cmd.getName().equalsIgnoreCase("BanHammer") && (sender instanceof Player) && sender.hasPermission("banhammer.spawn")) {
			PlayerInventory inv = ((Player)sender).getInventory();
			int firstEmpty = inv.firstEmpty();
			if (firstEmpty != -1) {
				
				CraftItemStack hammer = new CraftItemStack(Material.TRIPWIRE_HOOK,1,(short)0);
				NBTTagCompound hammerNBT = new NBTTagCompound();
				NBTTagCompound hammerNBTdisplay = new NBTTagCompound();
				NBTTagList hammerNBTLore = new NBTTagList();
				
				hammerNBTLore.add(new NBTTagString(null, "Skill: Level 1 banish"));
				hammerNBTdisplay.setString("Name", "§rBanhammer");
				hammerNBTdisplay.set("Lore", hammerNBTLore);
				
				hammerNBT.set("display", hammerNBTdisplay);				
				hammer.getHandle().tag = hammerNBT;
				
				inv.setItem(firstEmpty, hammer);
				return true;
			}
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			Player damager = (Player)event.getDamager();
			Player damagee = (Player)event.getEntity();
			ItemStack item = damager.getItemInHand();
			
			if (item != null && item.getType() == Material.TRIPWIRE_HOOK && item.getDurability() == 0) {
				event.setCancelled(true);
				damager.getServer().broadcastMessage("§r"+damager.getDisplayName()+"§r has banished "+damagee.getDisplayName());
				damagee.kickPlayer("§r"+damager.getDisplayName()+"§r has banished you");
			}
		}
	}
	
}
