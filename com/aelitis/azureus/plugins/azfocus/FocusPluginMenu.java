/**
 * File: FocusPluginMenu.java
 * Library: Focus Plugin for Azureus
 * Date: 23 Jan 2007
 *
 * Author: Allan Crooks
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the COPYING file ).
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.aelitis.azureus.plugins.azfocus;

import com.biglybt.pif.download.Download;
import com.biglybt.pif.ui.menus.MenuItem;
import com.biglybt.pif.ui.menus.MenuItemFillListener;
import com.biglybt.pif.ui.menus.MenuItemListener;
import com.biglybt.pif.ui.menus.MenuManager;

/**
 * @author Allan Crooks
 *
 */
public class FocusPluginMenu {
	
	private FocusPlugin plugin;
	
	public FocusPluginMenu(FocusPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void createMenu() {
		MenuManager mm = plugin.pi.getUIManager().getMenuManager();
		
		// Focus menu
		MenuItem menu_root = mm.addMenuItem(MenuManager.MENU_DOWNLOAD_CONTEXT, "azfocus.menu");
		menu_root.setStyle(MenuItem.STYLE_MENU);
		
		// Set Focus menu item.
		MenuItem menu_focus = mm.addMenuItem(menu_root, "azfocus.menu.focus_download");
		menu_focus.setStyle(MenuItem.STYLE_CHECK);
		
		MenuFocusEnabledListener enabled_listener = new MenuFocusEnabledListener();
		menu_root.addFillListener(enabled_listener);
		
		MenuFocusItemListener focus_listener = new MenuFocusItemListener();
		menu_focus.addFillListener(focus_listener);
		menu_focus.addMultiListener(focus_listener);
	}
	
	private class MenuFocusEnabledListener implements MenuItemFillListener {
		@Override
		public void menuWillBeShown(MenuItem menu, Object data) {
			Object[] rows = (Object[])data;
			for (int i=0; i<rows.length; i++) {
				Download d = (Download)rows[i]; 
				if (d.isComplete()) {menu.setEnabled(false); return;}
			}
			menu.setEnabled(true);
		}
	}
	
	private class MenuFocusItemListener implements MenuItemFillListener, MenuItemListener {

		@Override
		public void menuWillBeShown(MenuItem menu, Object data) {
			Object[] rows = (Object[])data;
			boolean all_focused = true;
			for (int i=0; i<rows.length; i++) {
				Download d = (Download)rows[i]; 
				all_focused = d.getBooleanAttribute(FocusPluginMenu.this.plugin.FOCUS_ATTRIBUTE);
				if (!all_focused) {break;}
			}
			menu.setData(Boolean.valueOf(all_focused));
		}

		@Override
		public void selected(MenuItem menu, Object target) {
			Download[] downloads = new Download[((Object[])target).length];
			System.arraycopy(target, 0, downloads, 0, downloads.length);
			boolean focus_on = ((Boolean)menu.getData()).booleanValue();
			FocusPluginMenu.this.plugin.setFocus(downloads, focus_on, true);
		}
		
	}
	
}
