package com.br.phdev.driver;

import com.pi4j.io.i2c.I2CFactory;

import java.util.List;

public class Module {

    protected final String moduleAddress;

    public Module(String moduleAddress) {
        this.moduleAddress = moduleAddress;
    }

    public void init() throws I2CFactory.UnsupportedBusNumberException {

    }

    public String getModuleAddress() {
        return moduleAddress;
    }

    public static Module getModule(List<Module> moduleList, String address) {
        System.out.println(moduleList.size());
        for (Module module : moduleList) {
            System.out.println(address + " = " + module.getModuleAddress());
            if (module.getModuleAddress().equals(address))
                return module;
        }
        return null;
    }

}
