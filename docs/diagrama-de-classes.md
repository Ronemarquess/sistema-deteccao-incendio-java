# Diagrama de classes

Diagrama Mermaid fiel ao código em `src/main/java/br/com/estacio/incendio`. DTOs e controllers são descritos na sequência para não sobrecarregar a visualização das entidades.

```mermaid
classDiagram
    direction LR

    class Usuario {
        <<MappedSuperclass>>
        -Long id
        -String nome
        -String email
        -String senha
        -Boolean ativo
    }

    class Bombeiro {
        <<Entity>>
        -String matricula
        -String patente
        -String batalhao
        -String telefone
    }

    class Localizacao {
        <<Embeddable>>
        -String nomeLocal
        -Double latitude
        -Double longitude
        +de(nomeLocal, latitude, longitude) Localizacao
    }

    class Sensor {
        <<Entity>>
        -Long id
        -String localizacao
        -Localizacao local
        -Boolean ativo
    }

    class LeituraSensor {
        <<Entity>>
        -Long id
        -Double temperatura
        -Double nivelFumaca
        -Double umidade
        -LocalDateTime instanteMedicao
        -NivelRisco nivelRisco
    }

    class Chamado {
        <<Entity>>
        -Long id
        -StatusChamado status
        -LocalDateTime horarioExato
        -Double temperaturaMaxima
        -Double elevacaoTemperatura
        -Localizacao localizacao
        -LocalDateTime encerradoEm
        -Set~Sensor~ sensores
        -Bombeiro bombeiroResponsavel
        -List~Notificacao~ notificacoes
        +estaFechado() boolean
        +estaAberto() boolean
        +getQuantidadeSensores() int
        +adicionarSensor(sensor) void
        +despachar(bombeiro) void
        +marcarEmAtendimento(bombeiro) void
        +marcarFalsoAlarme() void
        +encerrar() void
    }

    class Notificacao {
        <<Entity>>
        -Long id
        -CanalNotificacao canal
        -LocalDateTime enviadaEm
        -String mensagem
        +Notificacao(chamado, destinatario, canal, mensagem)
    }

    class StatusChamado {
        <<enumeration>>
        NOVO
        EM_ATENDIMENTO
        ENCERRADO
        FALSO_ALARME
    }

    class NivelRisco {
        <<enumeration>>
        NORMAL
        ATENCAO
        ALERTA
    }

    class NivelFumaca {
        <<enumeration>>
        BAIXA
        MEDIA
        ALTA
    }

    class CanalNotificacao {
        <<enumeration>>
        PUSH
        WHATSAPP
        SMS
    }

    Usuario <|-- Bombeiro
    Sensor *-- Localizacao
    Chamado *-- Localizacao
    LeituraSensor --> Sensor : ManyToOne
    LeituraSensor --> NivelRisco
    Chamado "1" --> "0..1" Bombeiro : responsável
    Chamado "1" o-- "0..*" Notificacao : cascade ALL
    Chamado "0..*" --> "0..*" Sensor : chamado_sensor
    Chamado --> StatusChamado
    Notificacao --> Chamado
    Notificacao --> Bombeiro : destinatário
    Notificacao --> CanalNotificacao
```

## Serviços e configuração

```mermaid
classDiagram
    class AvaliadorRisco {
        -Limites limites
        +avaliar(temperatura, nivelFumaca) NivelRisco
        +rotuloFumaca(nivelFumaca) NivelFumaca
    }
    class Limites {
        -double temperaturaAtencao
        -double temperaturaAlerta
        -double temperaturaCritica
        -double fumacaAtencao
        -double fumacaAlerta
        -double fumacaCritica
    }
    class LeituraService {
        +listar(sensorId, limite) List~LeituraDto~
        +registrar(request) LeituraResultado
        -obterOuCriarChamado(sensor, leitura) ChamadoResultado
        -calcularBaseline(sensorId) double
    }
    class ChamadoService {
        +listar(status, busca) List~ChamadoItem~
        +resumo() ChamadoResumo
        +detalhe(id) ChamadoDetalhe
        +despachar(id, bombeiroId) ChamadoDetalhe
        +marcarEmAtendimento(id, bombeiroId) ChamadoDetalhe
        +marcarFalsoAlarme(id) ChamadoDetalhe
        +encerrar(id) ChamadoDetalhe
    }
    class SensorService {
        +listar() List~SensorResponse~
        +criar(request) SensorResponse
        +atualizar(id, request) SensorResponse
        +definirAtivo(id, request) SensorResponse
        +resumo() SensorResumo
    }
    class BombeiroService {
        +login(email, senha) BombeiroResponse
        +cadastrar(request) BombeiroResponse
        +definirAtivo(id, ativo) BombeiroResponse
    }
    class SimulacaoService {
        -boolean habilitada
        +reset() void
    }
    class SeedData {
        +semear() void
        +criarLeiturasNormais(sensor) void
    }

    AvaliadorRisco --> Limites
    LeituraService --> AvaliadorRisco
    ChamadoService --> AvaliadorRisco
    SeedData --> AvaliadorRisco
    SimulacaoService --> SeedData
```

## API e DTOs

Controllers em `br.com.estacio.incendio.controller`:

- `BombeiroController` — `/api/bombeiros` (login, cadastro, consulta, atualização, ativação lógica).
- `SensorController` — `/api/sensores` (CRUD e resumo).
- `LeituraController` — `/api/leituras` (histórico e registro).
- `ChamadoController` — `/api/chamados` (lista, resumo, detalhe e quatro ações).
- `ConfiguracaoController` — `/api/configuracao/limites`.
- `SimulacaoController` — `/api/simulacao/reset`.
- `ApiExceptionHandler` — `@RestControllerAdvice` que produz `ErroResponse`.

DTOs principais: `LoginRequest`, `BombeiroRequest`, `BombeiroResponse`, `SensorRequest`, `SensorResponse`, `SensorResumo`, `AtivoRequest`, `LeituraRequest`, `LeituraDto`, `LeituraResultado`, `ChamadoItem`, `ChamadoResumo`, `ChamadoDetalhe`, `SensorDoChamado`, `NotificacaoDto`, `LimitesResponse` e `ErroResponse`.
