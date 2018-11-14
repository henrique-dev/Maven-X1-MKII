package com.br.phdev.driver;

import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.List;

public class Module {

    protected final String moduleAddress;

    public Module(String moduleAddress) {
        this.moduleAddress = moduleAddress;
    }

    public void init() throws I2CFactory.UnsupportedBusNumberException {

    }

    public void restartDriver() throws IOException {

    }

    public String getModuleAddress() {
        return moduleAddress;
    }

    public static Module getModule(List<Module> moduleList, String address) {
        for (Module module : moduleList) {
            if (module.getModuleAddress().equals(address))
                return module;
        }
        return null;
    }

}
