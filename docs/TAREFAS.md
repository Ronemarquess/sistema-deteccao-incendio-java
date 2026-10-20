# Tarefas e evolução do projeto

Backlog do **Sistema de Detecção Precoce de Incêndios IoT**. Marque uma tarefa como concluída somente depois de implementada e verificada. As tecnologias e os requisitos abaixo são propostas a confirmar com a equipe e com a orientação da disciplina.

## Já existe no repositório

- [x] Estrutura inicial de diretórios e repositório Git.
- [x] `Usuario.java`: classe abstrata com dados básicos e exibição de perfil.
- [x] `Bombeiro.java`: especialização de usuário com matrícula, patente e sobrescrita do perfil.
- [x] README inicial do projeto.

Ainda não foram encontrados build Maven/Gradle, classe principal Spring Boot, testes, API, persistência ou firmware. Esses itens não devem ser descritos como concluídos no relatório.

## Prioridade 0: fechar o escopo

- [ ] Obter os requisitos e critérios de avaliação da disciplina e registrar as funcionalidades obrigatórias.
- [ ] Confirmar integrantes, papéis, entregáveis e prazo com a equipe.
- [ ] Definir versão do Java e ferramenta de build; decidir se Spring Boot é realmente requisito.
- [ ] Definir quais riscos serão detectados (por exemplo, temperatura e fumaça) e quais sensores/dispositivos serão usados.
- [ ] Escolher como o dispositivo enviará dados (por exemplo, HTTP ou MQTT) e documentar a decisão.
- [ ] Definir o que significa alerta crítico, como a urgência será calculada e quem poderá despachar uma equipe.

## Prioridade 1: tornar o projeto executável

- [ ] Criar a configuração de build e dependências do projeto Java.
- [ ] Criar a classe de inicialização e a configuração mínima do Spring Boot, se essa tecnologia for confirmada.
- [ ] Adicionar instruções reproduzíveis para compilar, executar e testar.
- [ ] Confirmar que a estrutura de código e testes é reconhecida pela ferramenta de build.

## Prioridade 2: completar o domínio e testar regras

- [ ] Modelar sensores e leituras com identificador, tipo, localização, valor e instante da medição, conforme o escopo acordado.
- [ ] Implementar as classes concretas dos sensores escolhidos e suas regras de limite crítico.
- [ ] Implementar uma ocorrência/alerta que associe leituras relevantes, avalie a urgência e registre seu estado.
- [ ] Definir o despacho como uma ação de domínio verificável, sem alegar integração real com equipes antes de implementá-la.
- [ ] Revisar validações e encapsulamento dos modelos existentes; não armazenar ou exibir senhas em texto puro.
- [ ] Criar testes unitários para limites, leituras inválidas, cálculo de urgência e transições de estado.

## Prioridade 3: criar o serviço de aplicação

- [ ] Definir endpoints para receber leituras e consultar sensores e alertas, caso uma API REST seja aprovada.
- [ ] Validar entradas e retornar erros consistentes para requisições inválidas.
- [ ] Escolher e configurar persistência; documentar como iniciar o banco e preparar o esquema.
- [ ] Separar modelos de domínio, objetos de entrada/saída e regras de negócio quando necessário.
- [ ] Proteger endpoints e credenciais; documentar autenticação e autorização compatíveis com o escopo acadêmico.
- [ ] Criar testes de integração para os fluxos principais da API e persistência.

## Prioridade 4: integrar o dispositivo IoT

- [ ] Registrar modelo do microcontrolador, sensores, ligações e limites elétricos usados no protótipo.
- [ ] Implementar firmware para ler os sensores escolhidos e enviar medições ao serviço.
- [ ] Tratar indisponibilidade de rede, leituras inválidas e envio repetido de dados.
- [ ] Testar a comunicação com dados simulados antes de validar o hardware real.
- [ ] Documentar os limites do protótipo: não usar testes com fogo real; não apresentar o sistema como equipamento certificado de segurança.

## Prioridade 5: validar e preparar a entrega

- [ ] Criar diagrama de casos de uso e diagrama de classes atualizados com o código.
- [ ] Criar diagrama de arquitetura e sequência do fluxo dispositivo → serviço → alerta.
- [ ] Adicionar protótipo de interface ou painel somente se fizer parte dos requisitos confirmados.
- [ ] Executar e registrar testes; incluir evidências reproduzíveis e resultados reais da demonstração.
- [ ] Atualizar README com pré-requisitos, configuração, comandos, exemplos e limitações.
- [ ] Preparar roteiro de demonstração e relatório acadêmico; distinguir claramente o que está pronto e o que é proposta futura.
- [ ] Revisar acessibilidade, segurança, tratamento de dados e limitações antes da apresentação.

## Colaboração e controle de versão

- [ ] Confirmar que Nicolas aceitou o convite de colaboração, se ainda estiver pendente.
- [ ] Dividir as tarefas entre os integrantes e registrar responsáveis e prazos após a confirmação da equipe.
- [ ] Trabalhar em branches por funcionalidade, abrir Pull Requests e revisar as alterações antes de integrar.
