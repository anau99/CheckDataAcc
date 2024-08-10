package org.example;

public class Account {
    public String De;
    public String Cre;
    public Double valueDe;
    public Double valueCre;

    public Account(String de, String cre, Double valueDe, Double valueCre) {
        De = de;
        Cre = cre;
        this.valueDe = valueDe;
        this.valueCre = valueCre;
    }

    public String getDe() {
        return De;
    }

    public String getCre() {
        return Cre;
    }

    public Double getValueDe() {
        return valueDe;
    }

    public Double getValueCre() {
        return valueCre;
    }
}
