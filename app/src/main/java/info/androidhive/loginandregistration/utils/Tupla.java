package info.androidhive.loginandregistration.utils;


/**
 *  Tupla genérica de dos objetos
 *
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
