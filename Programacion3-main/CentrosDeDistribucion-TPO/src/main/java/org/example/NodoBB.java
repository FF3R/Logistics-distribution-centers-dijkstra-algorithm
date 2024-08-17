package org.example;

public class NodoBB {
    private final int indice;

    private final int[] cordenadas;

    private final int u;

    private final int c;

    public NodoBB(int[] cordenadas, int indice,int[][] matrizCostosTotales, int[][] matrizCentros) {
        this.cordenadas = cordenadas;
        this.indice = indice;
        this.u = calcularU(matrizCostosTotales,matrizCentros);
        this.c = calcularC(matrizCostosTotales,matrizCentros);
    }

    public int[] getCordenadas() {
        return cordenadas;
    }

    public int getIndice(){
        return this.indice;
    }

    public int getU() {
        return u;
    }

    public int getC() {
        return c;
    }

    private int calcularU(int[][] matrizCostosTotales, int[][] matrizCentros){

        int cantCentros = matrizCostosTotales.length;
        int cantClientes = matrizCostosTotales[0].length;
        int u = 0;
        int sumaFijos = 0;

        //Sumo los fijos de los centros construidos
        for(int x = 0; x < cantCentros; x++){
            if (cordenadas[x] == 1){
                sumaFijos += matrizCentros[x][1];
            }
        }

        //No hay ninguno construido
        if (sumaFijos == 0){
            return Integer.MAX_VALUE;
        } else {
            //Sumo los minimos de cada cliente de los centros construidos
            for (int j = 0; j < cantClientes; j++){

                int minimoCliente = Integer.MAX_VALUE;
                for (int i = 0; i < cantCentros; i++ ){

                    //Actuliza solo si esta consrtuido
                    if (matrizCostosTotales[i][j] < minimoCliente && this.cordenadas[i] == 1){
                        minimoCliente = matrizCostosTotales[i][j];
                    }
                }
                u += minimoCliente;
            }
            return u + sumaFijos;
        }
    }
    
    private int calcularC(int[][] matrizCostosTotales, int[][] matrizCentros){

        int cantCentros = matrizCostosTotales.length;
        int cantClientes = matrizCostosTotales[0].length;
        int c = 0;
        int sumaFijos = 0;

        //Sumo los fijos de los centros construidos
        for(int x = 0; x < cantCentros; x++){
            if (cordenadas[x] == 1){
                sumaFijos += matrizCentros[x][1];
            }
        }

        //Sumo los minimos de cada cliente de los centros construidos y eventuales (evito ya rechazados)
        for (int j = 0; j < cantClientes; j++){
            int minimoCliente = Integer.MAX_VALUE;
            for (int i = 0; i < cantCentros; i++ ){
                if (matrizCostosTotales[i][j] < minimoCliente && this.cordenadas[i] != -1){ //Actualiza solo si esta construido
                    minimoCliente = matrizCostosTotales[i][j];
                }
            }
            c += minimoCliente;
        }
        return c + sumaFijos;
    }
}
