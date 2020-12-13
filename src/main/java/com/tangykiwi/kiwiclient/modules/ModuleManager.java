package com.tangykiwi.kiwiclient.modules;

import com.tangykiwi.kiwiclient.modules.movement.Fly;

import java.util.ArrayList;

public class ModuleManager {

    public ArrayList<Module> moduleList = new ArrayList<Module>();

    public void init() {
        moduleList.add(new Fly());
    }

    public ArrayList<Module> getEnabledMods() {
        ArrayList<Module> enabledMods = new ArrayList<Module>();

        for(Module m : moduleList) {
            if(m.isEnabled()) enabledMods.add(m);
        }

        return enabledMods;
    }
}
