package com.purkynka.paintpp.menubar;

import com.purkynka.paintpp.menubar.filemenu.FileMenu;
import com.purkynka.paintpp.menubar.filtermenu.FilterMenu;
import com.purkynka.paintpp.menubar.infomenu.InfoMenu;

public class MenuBar extends javafx.scene.control.MenuBar {
    private FileMenu fileMenu;
    private FilterMenu filterMenu;
    private InfoMenu infoMenu;

    public MenuBar() {
        super();

        fileMenu = new FileMenu();
        filterMenu = new FilterMenu();
        infoMenu = new InfoMenu();

        getMenus().addAll(
                fileMenu,
                filterMenu,
                infoMenu
        );
    }
}
