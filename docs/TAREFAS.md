# ?? Divisão de Tarefas e Orientações do Projeto

Este documento detalha o que já foi configurado e a divisão de trabalho entre Rone e Nicolas para o projeto **Sistema de Detecção Precoce de Incêndios (IoT)**.

---

## ?? O que já foi feito (Rone)
- [x] Repositório criado e estruturado no GitHub (`sistema-deteccao-incendio-java`).
- [x] Árvore de pastas padronizada (`docs/`, `hardware/`, `src/main/java/`, `src/test/java/`).
- [x] Configuração do `README.md` principal do projeto.
- [x] Implementação do **Core de Domínio e Usuários** (`Usuario.java`, `Bombeiro.java` e `MainApplication.java`), aplicando Herança, Encapsulamento e Polimorfismo.

---

## ?? O que precisa ser feito (Nicolas)
- [ ] Aceitar o convite de colaborador no repositório GitHub.
- [ ] Clonar o repositório localmente:
  `git clone https://github.com/Ronemarquess/sistema-deteccao-incendio-java.git`
- [ ] Criar e implementar as classes no pacote `br.com.estacio.incendio.model`:
  - [ ] `Sensor.java` (Classe base com atributos protected e método `verificarAlertaCritico()`).
  - [ ] `SensorTemperatura.java` (Subclasse com `temperaturaAtual`, `limiteCritico` e `@Override`).
  - [ ] `Chamado.java` (Lista de sensores, `avaliarUrgencia()` com `for`/`if` e `despacharEquipe()`).
- [ ] Criar Pull Request (PR) com a branch de funcionalidade.
