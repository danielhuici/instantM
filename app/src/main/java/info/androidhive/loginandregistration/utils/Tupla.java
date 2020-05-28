package info.androidhive.loginandregistration.utils;


/**
 * Tupla genérica de dos objetos
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class Tupla<A, B> {
    public final A a;
    public final B b;

    /**
     *  Construye una tupla
     *
     */
    public Tupla(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
