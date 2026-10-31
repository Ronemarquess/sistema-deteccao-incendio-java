# Sistema de Detecção Precoce de Incêndios IoT

> Projeto acadêmico da Estácio para a disciplina do professor Luiz Santos.

## Objetivo

Desenvolver uma solução que detecte condições de risco de incêndio por meio de sensores conectados, registre ocorrências e permita encaminhar alertas para uma equipe responsável. A proposta combina um dispositivo IoT, um serviço de software e documentação técnica. O escopo final deve ser confirmado com os requisitos da disciplina.

## Estado atual

O repositório está em fase inicial de desenvolvimento. Até o momento, há uma aplicação Spring Boot com banco H2 em memória e estes modelos Java:

- `Usuario` é uma classe abstrata com dados de perfil e um método para exibir o perfil.
- `Bombeiro` estende `Usuario` e sobrescreve a exibição do perfil.
- `Sensor` e `LeituraSensor` representam sensores e suas medições, com validações Jakarta e mapeamento JPA.
- `frontend-incendio` contém um centro de comando React + TypeScript com simulador local e seletor de cinco idiomas.
- `docs/TAREFAS.md` contém o backlog de evolução do projeto.

Ainda não há endpoints da API, serviços/repositórios de aplicação, testes automatizados ou firmware implementado. O dashboard informa quando a API está indisponível e diferencia os dados locais de demonstração. O banco H2 em memória não mantém os dados após o encerramento da aplicação; a integração com sensores permanece como trabalho futuro.

## Próximas evoluções

As etapas estão detalhadas e priorizadas em [`docs/TAREFAS.md`](docs/TAREFAS.md). Em resumo:

1. Confirmar os requisitos e critérios de avaliação e a forma de comunicação IoT.
2. Expandir e testar o domínio de sensores, leituras, ocorrências e despacho.
3. Criar serviços, repositórios e endpoints para receber leituras e consultar alertas.
4. Substituir ou configurar persistência durável conforme o escopo definido.
5. Desenvolver e validar o firmware/dispositivo IoT e sua comunicação com o serviço.
6. Concluir testes, diagramas e preparação da demonstração.

## Front-end

Consulte [frontend-incendio/README.md](frontend-incendio/README.md) para os requisitos, instalação, execução e verificação do dashboard.

## Estrutura existente

```text
docs/
	TAREFAS.md
	diagramas/
	prototipos/
src/
	main/java/br/com/estacio/incendio/
		IncendioApplication.java
		model/
			Usuario.java
			Bombeiro.java
			Sensor.java
			LeituraSensor.java
	test/java/br/com/estacio/incendio/ (a criar)
hardware/
	esp32_firmware/
```

Os pacotes de `controller`, `service`, `repository` e `dto`, assim como testes, diagramas, protótipos e firmware, ainda precisam receber suas implementações. A presença de uma pasta não significa que a funcionalidade esteja implementada.

## Pré-requisitos e execução

- Java 17
- Maven 3.6.3 ou superior

Compile e execute os testes:

```shell
mvn test
```

Inicie a aplicação Spring Boot:

```shell
mvn spring-boot:run
```

O projeto inicia o banco H2 em memória; não é necessário instalar ou configurar um banco externo. Ainda não há endpoints implementados.

## Equipe e contexto acadêmico

O projeto está sendo desenvolvido por Rone Marques e Nicolas para apresentação ao professor Luiz Santos. Confirmem com a equipe os nomes, papéis e requisitos que devem constar na entrega final.
