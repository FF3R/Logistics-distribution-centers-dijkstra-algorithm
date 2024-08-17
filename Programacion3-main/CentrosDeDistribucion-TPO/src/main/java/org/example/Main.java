package org.example;

import java.io.InputStream;

import java.util.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Lista de producion de clientes: ");
        int[] clientes = crearListaClientes();
        int cantidadClientes = clientes.length;
        System.out.println();

        System.out.println("Matriz de centros de distribucion(costo Unitario y costo fijo): ");
        int[][] centros = crearMatrizCentros();
        int cantidadCentros = centros.length;

        int[][] matrizRutas = crearMatrizRutas(cantidadClientes,cantidadCentros);
        //System.out.println("Matriz de costos unitarios de envio de cliente a centro: ");
        int[][] matrizUnitarios = crearMatrizMinimos(matrizRutas,cantidadClientes,cantidadCentros);

        System.out.println("Matriz de costos totales de envio: ");
        int[][] matrizCostosTotalesEnvio = agregarCostosTransporteUnitario(matrizUnitarios, centros, clientes);
        int[] mejorOpcion = buscarMejorOpcion(matrizCostosTotalesEnvio,centros);
        centroACadaCliente(matrizCostosTotalesEnvio, mejorOpcion);

    }

    private static int[] crearListaClientes(){

        //Codigo generado con CHATGPT3.5

        List<Integer> clientesProduccion = new ArrayList<>();

        try {
            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("clientes.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    clientesProduccion.add(Integer.parseInt(values[1]));
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }
        System.out.println(clientesProduccion);
        return clientesProduccion.stream().mapToInt(Integer::intValue).toArray();
    }

    //En posicion 0 tenemos el costo unitario de enviar al puerto
    //En posicion 1 tenemos el costo fijo del centro
    private static int[][]crearMatrizCentros(){

        //Codigo generado con CHATGPT3.5

        // List to store center coordinates dynamically
        List<int[]> matrizCentros = new ArrayList<>();

        try {
            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("centros.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    int centroIndex = Integer.parseInt(values[0]);
                    int xCoordinate = Integer.parseInt(values[1]);
                    int yCoordinate = Integer.parseInt(values[2]);

                    // Ensure the list has enough capacity to accommodate the center
                    while (matrizCentros.size() <= centroIndex) {
                        matrizCentros.add(new int[2]);
                    }

                    // Set the coordinates in the list
                    matrizCentros.get(centroIndex)[0] = xCoordinate;
                    matrizCentros.get(centroIndex)[1] = yCoordinate;
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }

        // Convert the list to an array
        int[][] matrizCentros2 = matrizCentros.toArray(new int[0][]);

        imprimirMatriz(matrizCentros2);

        return matrizCentros2;
    }

    private static int[][] crearMatrizRutas(int cantClientes, int cantCentros){

        // Tamaño de la matriz N*N
        int N = cantCentros + cantClientes;

        // Crear una matriz de enteros con valores predeterminados de Integer.MAX_VALUE
        int[][] matrizRutas = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == j){
                    matrizRutas[i][j] = 0;
                } else {
                    matrizRutas[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        try {

            // Use the class loader to load the resource as an InputStream
            ClassLoader classLoader = Main.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("rutas.txt");

            // Check if the resource is found
            if (inputStream == null) {
                System.out.println("File not found in the resources folder.");
            } else {
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()) {
                    String[] values = scanner.nextLine().split(",");
                    matrizRutas[Integer.parseInt(values[0])][Integer.parseInt(values[1])] = Integer.parseInt(values[2]);
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            System.out.println(e.getMessage());
        }

        //imprimirMatriz(matrizRutas);

        return matrizRutas;
    }

    private static int[] dijkstraUnVertice(int[][] matrizRutas, int vertice, int cantClientes, int cantCentros) {

        int ccc = cantCentros + cantClientes;

        //Creo el conjunto de candidatos
        Set<Integer> candidatos = new HashSet<>();
        for (int x = 0; x < ccc; x++){
            if (x != vertice){
                candidatos.add(x);
            }
        }

       //Creo la lista donde voy guardando los valores
        int[] valoresDijkstra= new int[ccc];

        //Cargo los valores del vecindario de v
        System.arraycopy(matrizRutas[vertice], 0, valoresDijkstra, 0, ccc);

        //Como propone Dijkstra, se elige el nodo conectado con menor coste, se itera hasta acabar todos los nodos
        while (!candidatos.isEmpty()){
            int min = Integer.MAX_VALUE;
            int candidatoAAgregar = -1;
            for( Integer candidato : candidatos){
                if (valoresDijkstra[candidato] <= min){
                    min = valoresDijkstra[candidato];
                    candidatoAAgregar = candidato;
                }
            }
            //System.out.println("Mejor candidato a agregar: " + Integer.toString(candidatoAAgregar));
            //System.out.println("Valor MIN: " + Integer.toString(min));

            candidatos.remove(candidatoAAgregar);
            List<Integer> auxCandidatos = new ArrayList<>(candidatos);

            for (Integer p : auxCandidatos){
                //Reviso si el candidato tiene conexion con nodo p
                if (matrizRutas[candidatoAAgregar][p] != Integer.MAX_VALUE){

                    //Reviso si ya tengo un valor que conecte al vertice con el nodo p
                    if (valoresDijkstra[p] != Integer.MAX_VALUE){

                        //Si es mejor actualizo
                        if (valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p] < valoresDijkstra[p]){
                            valoresDijkstra[p] = valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p];
                        }
                    } else {
                        //Si no tengo un valor que conecte vertice con p, lo actualizo
                        valoresDijkstra[p] = valoresDijkstra[candidatoAAgregar] + matrizRutas[candidatoAAgregar][p];
                    }
                }
            }
        }
        //Devuelvo solamente los valores del nodo a los clientes, ya que solamente se va a ejecutar con los nodos centroDistribucion
        return Arrays.copyOfRange(valoresDijkstra,0,cantClientes );
    }

    private static int[][] crearMatrizMinimos(int[][] matrizRutas,int cantClientes, int cantCentros){

        //Inicializo la matriz con filas(cantCentros) X columnas(cantClientes)
        int[][] matrizMinimos = new int[cantCentros][cantClientes];

        //Cargo la lista devuelta de Dijkstra para cada Centro de Distribucion
        for (int i = cantClientes; i < cantClientes + cantCentros; i++){
            matrizMinimos[i - cantClientes] = dijkstraUnVertice(matrizRutas,i,cantClientes, cantCentros);
        }

        //imprimirMatriz(matrizMinimos);

        return matrizMinimos;
    }

    private static int[][] agregarCostosTransporteUnitario(int[][] matrizMinimos, int[][] matrizCentros, int[] clientes ){

        //Agregamos los costos de llevarlo al puerto y se multiplica por la produccion del cliente
        for (int i = 0; i < matrizMinimos.length; i++){
            for (int j = 0; j < matrizMinimos[0].length; j++){
                matrizMinimos[i][j] = (matrizMinimos[i][j] + matrizCentros[i][0])*clientes[j];
            }
        }

        imprimirMatriz(matrizMinimos);

        return matrizMinimos;
    }

    private static int[] buscarMejorOpcion(int[][] matrizCostosTotales, int[][] matrizCentros){

        int cantidadCentros = matrizCostosTotales.length;

        int u = Integer.MAX_VALUE ;
        int c ;

        //Creo el nodo inicial con todos los centros en estado "0" (sin decision tomada)
        NodoBB nodoInicial = new NodoBB(new int[cantidadCentros],0,matrizCostosTotales, matrizCentros);

        //Inicializa la cola de prioridad ordenada de por menor C
        PriorityQueue<NodoBB> colaNodos = new PriorityQueue<>(Comparator.comparing(NodoBB::getC));
        colaNodos.add(nodoInicial);

        NodoBB nodoEstudiado = null;

        //Mientras haya en la cola nodos cuyo mejor caso es mejor que el peor caso actual, se sigue revisando nodos
        while (!colaNodos.isEmpty() && (colaNodos.peek().getC() <= u )){

            //Saco el nodo con el mejor caso más bajo
        	nodoEstudiado = colaNodos.remove();

           /* System.out.println("Nodo estudiado: " + Arrays.toString(nodoEstudiado.getCordenadas()));
            System.out.println("U: " + nodoEstudiado.getU());
            System.out.println("C: " + nodoEstudiado.getC()); */

            u = nodoEstudiado.getU();

            //Se revisa que no se hallan definido todos los centros
            if (nodoEstudiado.getIndice() < cantidadCentros){

                //Se agrega un nodo con el proximo centro construido
                int[] copiaCordenadas1 = new int[cantidadCentros];
                System.arraycopy(nodoEstudiado.getCordenadas(),0,copiaCordenadas1,0,cantidadCentros);
                copiaCordenadas1[nodoEstudiado.getIndice()] = 1;
                colaNodos.add(new NodoBB(copiaCordenadas1,nodoEstudiado.getIndice() + 1, matrizCostosTotales, matrizCentros));

                //Se agrega un nodo con el proximo centro no construido
                int[] copiaCordenadas2 = new int[cantidadCentros];
                System.arraycopy(nodoEstudiado.getCordenadas(),0,copiaCordenadas2,0,cantidadCentros);
                copiaCordenadas2[nodoEstudiado.getIndice()] = -1;
                colaNodos.add(new NodoBB(copiaCordenadas2,nodoEstudiado.getIndice() + 1,matrizCostosTotales, matrizCentros));
            }
        }

        assert nodoEstudiado != null;
        System.out.println("El mejor nodo posible es: " + Arrays.toString(nodoEstudiado.getCordenadas()));
        System.out.println("Tiene un valor de " + nodoEstudiado.getC());
        return nodoEstudiado.getCordenadas();

        /* System.out.println("NODOS NO ESTUDIADOS PENDIENTES EN COLA");
        while (!colaNodos.isEmpty()){
            //Saco el nodo con el mejor caso más bajo
            nodoEstudiado = colaNodos.remove();

            System.out.println("Nodo estudiado: " + Arrays.toString(nodoEstudiado.getCordenadas()));
            System.out.println("U: " + nodoEstudiado.getU());
            System.out.println("C: " + nodoEstudiado.getC());
        } */

    }

    private static void imprimirMatriz(int[][] matriz){
        int filas = matriz.length;
        int columnas = matriz[0].length;

        // Imprimir la matriz
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

    }
    private static void centroACadaCliente(int[][] matriz, int[] mejorOpcion) {
        int largoFilas = matriz.length;
        int largoColumnas = matriz[0].length;

        Integer[][] centrosCorrespondientes = new Integer[largoFilas][largoColumnas];

        // Iterar sobre las columnas de la matriz
        for (int j = 0; j < largoColumnas; j++) {
            int minCosto = Integer.MAX_VALUE;
            Integer mejorFila = null;

            // Encontrar la fila con el menor costo para la columna actual
            for (int i = 0; i < largoFilas; i++) {
                if (mejorOpcion[i] == 1 && matriz[i][j] < minCosto) {
                    minCosto = matriz[i][j];
                    mejorFila = i;
                }
            }

            // Si se encuentra una fila, asignar el valor correspondiente en la matriz de resultados
            if (mejorFila != null) {
                centrosCorrespondientes[mejorFila][j] = j + 1;
            }
        }

        // Iterar sobre las filas de la matriz de resultados
        for (int k = 0; k < largoFilas; k++) {
            boolean hasValues = false;

            // Verificar si la fila contiene valores no nulos
            for (Integer valor : centrosCorrespondientes[k]) {
                if (valor != null) {
                    hasValues = true;
                    break;
                }
            }

            // Si la fila contiene valores no nulos, imprimir la fila
            if (hasValues) {
                System.out.print("Al centro de distribución " + (k + 1) + " [");
                boolean firstNonNull = true;

                // Itera sobre los valores no nulos de la fila y los imprime
                for (Integer valor : centrosCorrespondientes[k]) {
                    if (valor != null) {
                        if (!firstNonNull) {
                            System.out.print(", ");
                        }
                        System.out.print(valor);
                        firstNonNull = false;
                    }
                }
                System.out.println("]");
            }
        }
    }








}