package com.example.territorio.dominio;

/** Um ponto concreto do território onde os sensores estão instalados. */
public class SetorUrbano extends ComponenteTerritorial {

    public SetorUrbano(String id, String nome, String regiao) {
        super(id, nome, regiao);
    }

    @Override
    public String verificarStatus() {
        return "OPERACIONAL".equals(getStatus())
                ? getNome() + ": operacional"
                : getNome() + ": atenção (" + getStatus() + ")";
    }
}
