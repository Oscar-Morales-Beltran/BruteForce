package com.company;

import java.util.Stack;

public class SearchCheapestPath {

    //arreglo de nodos que forman el grafo
    protected Object[][] grafo = {{'A','0','B'},{'C','D','E'},{'0','F','G'}};
    //arreglo que forman las uniones entre los nodos
    protected char[][] uniones = {{'A','C'},{'A','D'},{'B','A'},{'C','D'},{'D','B'},{'D','E'},{'D','G'},{'D','F'},{'E','B'},{'E','G'},{'F','C'},{'G','F'}};
    //pesos entre las uniones del grafo
    protected int[] pesos = {6,4,9,9,5,6,7,5,6,1,1,9};
    protected Object[] nodos;
    private int contidad_Nodos;             //almacena la contidad de nodos que tiene el grafo sin contar 0, que será el tamaño del vector nodos
    private char begin;                     //Nodo donde inicia la búsqueda
    private char aim;                       //Nodo meta
    private Object nodeMin;                 //almacena el nodo con el peso más barato
    private int indexMin;                   //almacena el peso más barato encontrado
    private int pos_MinAnt;                 //almacena la posición del nodo mínimo anterior por si tal nodo no tiene un vecino diferente a los ya recorridos y se debe buscar un nuevo nodo mínimo

    Stack<Object> finalNodes = new Stack<>();               //almacena los nodos que formaran la ruta con el menor peso
    Stack<Integer> weights = new Stack<>();                 //almacena los pesos de los nodos de la pila finalNodes
    Stack<Object> recorridos = new Stack<>();               //almacena los nodos que fueron recorridos
    Stack<Object> porRecorrer = new Stack<>();              //almacena los nodos que serán evaluados con respecto al peso más barato
    Stack<Integer> pesos_porRecorrer = new Stack<>();       //almacena los pesos de los nodos que serán evaluados para buscar el siguiente camino más barato

    public SearchCheapestPath(){
        setBegin('A');                  //Damos el nodo de partida
        setAim('F');                    //Damos el nodo a encontrar
        finalNodes.push(getBegin());    //Agregaos al grupo de los nodos finales(ruta final) el nodo de partida
        recorridos.push('0');     //indica que no se ha recorrido ningún nodo dentro del grafo
    }

    //Obtiene la longitud para el vector "cantidad_Nodos" buscando los nodos en el arreglo "grafo" que son diferentes de 0(éstos no son válidos)
    protected int getLengthVec(){
        int contador = 0;
        for (int i = 0; i < grafo.length; i++){
            for (int j = 0; j < grafo.length; j++){
                if (!grafo[i][j].equals('0')){
                    contador++;
                }
            }
        }
        return contador;
    }

    //obtiene los nodos que forman el grafo del array "grafo" para buscar sus uniones en el array "uniones"
    protected void getValidNodes(){
        int contador = 0;                           //contador para movernos dentro del vector nodos agregando los nodos diferentes de 0
        setContidad_Nodos(getLengthVec());          //establecemos el valor de la variable catidad_Nodos
        nodos = new Object[getContidad_Nodos()];    //establecemos la longitud del vector nodos
        for (int i = 0; i < grafo.length; i++){
            for (int j = 0; j < grafo.length; j++){
                if (!grafo[i][j].equals('0')){
                    nodos[contador] = grafo[i][j];
                    contador++;
                }
            }
        }
        findCheapest(getBegin());                   //Comienzo a buscar la ruta pasando como parámetro el nodo inicio
        finalNodes.push(getNodeMin());
        weights.push(getIndexMin());
    }

    /* Saca a los nodos vecinos del nodo actual para saber cual tiene la ruta más barata
     * parameter1: nodo a buscar sus vecinos */
    protected void findCheapest(Object nodoBusca){
        if (!nodoBusca.equals(getAim())){
            for (int i = 0; i < uniones.length; i++){                   //nos movemos en uniones entorno al deje y
                if (nodoBusca.equals(uniones[i][0])){
                    if (!recorridos.contains(nodoBusca)){
                        pesos_porRecorrer.push(pesos[i]);               //agrego el peso de la union con ese nodo X
                        porRecorrer.push(uniones[i][1]);                //agrego el nodo X con el que esta unido
                    }
                }
            }
            if (recorridos.lastElement().equals('0')){                  //si el último elemento de recorridos es 0 quiere decir que sólo se ha recoorrido el nodo inicio
                recorridos.pop();
                recorridos.push(getBegin());
            }
            if (getNodeMin() != null){                                  //si existe un nodo mínimo y no se debe volver a reocorrer
                recorridos.push(getNodeMin());
            }
        }
        mainFunction();
    }

    /*Método de funciones principales*/
    public void mainFunction(){
        if (!findCheaper()){
            if (!findNextNode()){
                finalNodes.push(getNodeMin());
                Object temp;
                int lengRecorridos = porRecorrer.size();
                for (int j = 0; j < lengRecorridos;j++){
                    temp = porRecorrer.pop();
                    if (!temp.equals(getAim())){                             //si el nodo no es igual al nodo_objetivo lo alamceno en recorridos o al nodo actual que estoy buscando
                        if (!temp.equals(getNodeMin())){
                            recorridos.push(temp);                           //agrego cada uno de los nodos que estaba en la pila porRecorrer para
                            //que ya no sean tomados en cuenta para un recorrido y vacio la pila porRecorrer que contiene los nodos que ya
                            //fueron comparados para obtener el peso menor de la ruta del siguiente nodo
                        }
                    }
                }
                porRecorrer.clear();
                weights.push(getIndexMin());
                pesos_porRecorrer.clear();                                  //limpio la lista pesos por recorrer ya que no necesito más dichos pesos
                findCheapest(getNodeMin());
            }else{
                popElement(getPos_MinAnt());                                //saco el elemento que tiene vecinos ya recorridos ya que se cicla la busqueda
                System.out.println("Voy a imprimir los nodos");
                imprimir();
                mainFunction();
            }
        }
    }

    //encuentra el peso más barato entre los pesos de los nodos encotrados y retorna su posición al método findNextNode
    protected boolean findCheaper(){
        int tamanio = pesos_porRecorrer.size();
        int min = pesos_porRecorrer.lastElement();                //asumo que el último elemento fue el mínimo elemento introducido
        for (int i = 0; i < tamanio; i++){
            if (pesos_porRecorrer.get(i) <= min){
                min = pesos_porRecorrer.get(i);
                setIndexMin(pesos_porRecorrer.get(i));            //encuentro el peso mínimo
                setNodeMin(porRecorrer.get(i));                   //establezco el nodo que tiene el peso mínimo
                setPos_MinAnt(porRecorrer.search(getNodeMin())-1);//almaceno la posición del nodo mínimo anterior por si se debe buscar un nuevo nodo_mínimo(debido a que el actual crea un ciclo infinito al apuntar a nodos ya reocrridos)
            }
        }
        if (getNodeMin().equals(getAim())){                       //si mi nodo_menor_costo es igual a mi nodo_objetivo entonces encontré la ruta de inicio a objetivo//ture/caso contrario/false
            return true;
        }else{
            return false;
        }
    }

    //retorna true si el nodo a buscar 'sí' tiene vecinos diferentes a los ya recorridos para evitar un ciclo infinito
    protected boolean findNextNode(){
        if (!checkIfwasWalked(getNodeMin())){                   //si el nodo no ha sido recorrido
            for (int i = 0; i < uniones.length; i++){
                if (getNodeMin().equals(uniones[i][0])){        //encuentro el nodo dentro del arreglo uniones
                    if (!recorridos.contains(uniones[i][1])){   //si tiene al menos un nodo vecino que sea diferente a los recorridos puedo tomar esa ruta sin ciclarme
                        return false;
                    }
                }
            }
        }
        return true;                                            //asumo que simpre se cicla porque tiene vecinos iguales para evitar el ciclo infinito
    }

    /*retorna un true si el nodo ya esta en los nodos recorridos por lo tanto ya no se puede volver a recorrer
    * parameter1: nodo a comprobar si ya fue recorrido*/
    protected boolean checkIfwasWalked(Object node){
        for (int i = 0; i < recorridos.size(); i++){
            if (recorridos.get(i).equals(node)){
                return true;
            }
        }
        return false;
    }

    /*si el nodo ya fue recorrido tengo que sacarlo de las pilas pesos_poRecorrer y porRecorrer*/
    protected void popElement(int index){
        //voy a sacar los elementos de la pila porRecorrer y guardarlos en una pila temporal para sacar al elemetno que tiene un vecino que ya fue recorrido
        Stack<Object> temp = new Stack<>();
        Stack<Integer> tempWeights = new Stack<>();
        int tamano = porRecorrer.size();
        for (int i = 0; i < tamano; i++){
            if (i == index){
                recorridos.push(porRecorrer.pop());
                pesos_porRecorrer.pop();
            }else{
                temp.push(porRecorrer.pop());
                tempWeights.push(pesos_porRecorrer.pop());
            }
        }

        //voy a regresar a su pila original a los elementos que se encuentrán en la pila temp
        int tamanio2 = temp.size();
        for (int j = 0; j < tamanio2; j++){
            porRecorrer.push(temp.pop());
            pesos_porRecorrer.push(tempWeights.pop());
        }
    }

    //uso exclusivo para pruebas de impresión
    public void imprimir(){
        System.out.println("Los nodos finales son: ");
        for (int i = 0; i < finalNodes.size(); i++){
            System.out.print(finalNodes.get(i) + " ");
        }
        System.out.println();
        System.out.println("Los pesos de los nodos finales son: ");
        for (int j = 0; j < weights.size(); j++){
            System.out.print(weights.get(j) + " ");
        }
    }

    /*Estos métodoso no son obligatorios aunque deben ser usados si dichas variables a los cuales
    * pertenecen serán usadas en clases distintas a esta*/
    public char getBegin() {
        return begin;
    }

    public void setBegin(char begin) {
        this.begin = begin;
    }

    public char getAim() {
        return aim;
    }

    public void setAim(char aim) {
        this.aim = aim;
    }

    public int getContidad_Nodos() {
        return contidad_Nodos;
    }

    public void setContidad_Nodos(int contidad_Nodos) {
        this.contidad_Nodos = contidad_Nodos;
    }

    public Object getNodeMin() {
        return nodeMin;
    }

    public void setNodeMin(Object nodeMin) {
        this.nodeMin = nodeMin;
    }

    public int getIndexMin() {
        return indexMin;
    }

    public void setIndexMin(int indexMin) {
        this.indexMin = indexMin;
    }

    public int getPos_MinAnt() {
        return pos_MinAnt;
    }

    public void setPos_MinAnt(int pos_MinAnt) {
        this.pos_MinAnt = pos_MinAnt;
    }
}