package com.example.pruebafirebase;

import java.util.ArrayList;
import java.util.List;

public class CocheManager {
    private static CocheManager instance;
    private List<Coche> listaCoches;

    private CocheManager() {
        listaCoches = new ArrayList<>();
    }

    public static CocheManager getInstance() {
        if (instance == null) {
            instance = new CocheManager();
        }
        return instance;
    }

    public List<Coche> getListaCoches() {
        return listaCoches;
    }

    public void setListaCoches(List<Coche> listaCoches) {
        this.listaCoches = listaCoches;
    }

    public void agregarCoche(Coche coche) {
        listaCoches.add(coche);
    }

}
