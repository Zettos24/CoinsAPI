package de.zettos.coinsapi;

interface Callback<T> {

    void accept(T t);
}
