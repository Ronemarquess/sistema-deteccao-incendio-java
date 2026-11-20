# Sistema de Detecção Precoce de Incêndios IoT

> Projeto acadêmico da Estácio para a disciplina do professor Luiz Santos.

## Objetivo

Desenvolver uma solução que detecte condições de risco de incêndio por meio de sensores conectados, registre ocorrências e permita encaminhar alertas para uma equipe responsável. A proposta combina um dispositivo IoT, um serviço de software e documentação técnica. O escopo final deve ser confirmado com os requisitos da disciplina.

## Estado atual

O repositório está em fase inicial de desenvolvimento. Até o momento, há um modelo Java básico para usuários e bombeiros:

- `Usuario` é uma classe abstrata com dados de perfil e um método para exibir o perfil.
- `Bombeiro` estende `Usuario` e sobrescreve a exibição do perfil.
- `docs/TAREFAS.md` contém o backlog de evolução do projeto.

Ainda não há configuração de Maven ou Gradle, aplicação Spring Boot inicializável, API, banco de dados, testes automatizados ou firmware implementado. Portanto, o sistema ainda não pode ser executado como uma aplicação completa, e a integração com sensores permanece como trabalho futuro.

## Próximas evoluções

As etapas estão detalhadas e priorizadas em [`docs/TAREFAS.md`](docs/TAREFAS.md). Em resumo:

1. Confirmar os requisitos e critérios de avaliação; escolher versões de Java, Spring Boot e a forma de comunicação IoT.
2. Configurar o projeto para compilação e execução, incluindo uma aplicação inicializável.
3. Implementar e testar o domínio de sensores, leituras, ocorrências e despacho.
4. Criar uma API e persistência para receber leituras e consultar alertas.
5. Desenvolver e validar o firmware/dispositivo IoT e sua comunicação com o serviço.
6. Concluir testes, diagramas, instruções de execução e demonstração.

## Estrutura existente

```text
docs/
	TAREFAS.md
	diagramas/
	prototipos/
src/
	main/java/br/com/estacio/incendio/model/
		Usuario.java
		Bombeiro.java
	test/java/br/com/estacio/incendio/
hardware/
	esp32_firmware/
```

As pastas de diagramas, protótipos, testes e firmware ainda precisam receber os artefatos correspondentes. A presença de uma pasta não significa que a funcionalidade esteja implementada.

## Execução

Ainda não há comando de compilação ou execução definido. Essa instrução será adicionada quando a configuração do projeto Java e a classe de inicialização estiverem implementadas. Consulte o backlog antes de considerar o sistema pronto para demonstração.

## Equipe e contexto acadêmico

O projeto está sendo desenvolvido por Rone Marques e Nicolas para apresentação ao professor Luiz Santos. Confirmem com a equipe os nomes, papéis e requisitos que devem constar na entrega final.
