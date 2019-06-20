/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author Cursos2
 */
public class programaR {
    public static void main(String[] args) {
        dirlist();
    }
    public static void dirlist(){
    int x, y, z, c;
    x=0;y=1;
        for (c = 1; c <= 900000; c++) {
            z=x+y;
            System.out.println(z);
            x=y;
            y=z;
        }

    }
    public void fibo(int entroalgo){
        double pi;
        double negativo = 0.0;
        double positivo = 0.0;
        double d = 3.0;
        double d1 = 5.0;

        int contador = 1;
        int contador2 = 1;

        ArrayList<Integer> areaSalida = new ArrayList<>();
        System.out.println("Termino\t" + "Aproximacion\n");

        while(contador2 != entroalgo){

            switch(contador){

                case 1:

                    negativo += 4/d;

                    d += 4.0;

                    contador += 1;

                    break;

                case 2:

                    positivo += 4/d1;

                    d1 += 4.0;

                    contador -= 1;

                    break;

                default:

                    System.out.println("No es el numero");

            }//fin del switch

            pi = 4.0 - negativo + positivo;

            //System.out.println("Resultados");

            contador2 += 1;

        }//fin del while


    }//fin del método main

}//fin de la clase Pi

