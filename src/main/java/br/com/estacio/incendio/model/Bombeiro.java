package br.com.estacio.incendio.model;

public class Bombeiro extends Usuario {
    // Encapsulamento: os dados específicos do bombeiro também são privados.
    private String matriculaId;
    private String patente;

    public Bombeiro() {
        super();
    }

    public Bombeiro(Long id, String nome, String email, String senha,
                    String matriculaId, String patente) {
        // Herança: super(...) inicializa os atributos definidos em Usuario.
        super(id, nome, email, senha);
        this.matriculaId = matriculaId;
        this.patente = patente;
    }

    public String getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(String matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    @Override
    public void exibirPerfil() {
        // Polimorfismo: esta implementação substitui a exibição padrão de Usuario.
        System.out.println("Patente: " + patente);
        System.out.println("Nome: " + getNome());
        System.out.println("Matrícula: " + matriculaId);
    }
}